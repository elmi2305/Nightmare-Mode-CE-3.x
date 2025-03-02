package com.itlesports.nightmaremode.mixin;

import btw.community.nightmaremode.NightmareMode;
import btw.entity.attribute.BTWAttributes;
import btw.entity.mob.JungleSpiderEntity;
import btw.item.BTWItems;
import btw.world.util.difficulty.Difficulties;
import com.itlesports.nightmaremode.AITasks.EntityAILunge;
import com.itlesports.nightmaremode.entity.EntityShadowZombie;
import com.itlesports.nightmaremode.NightmareUtils;
import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.*;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(EntityZombie.class)
public abstract class EntityZombieMixin extends EntityMob{
    @Unique private static boolean areMobsEvolved = NightmareMode.evolvedMobs;

    public EntityZombieMixin(World par1World) {
        super(par1World);
    }

    @Shadow public abstract boolean isVillager();

    @Unique public void onKilledBySun() {
        if (!this.worldObj.isRemote) {
            float witherSkeletonChanceModifier = this.worldObj.getDifficulty() == Difficulties.HOSTILE ? 0f : (float) (0.2f * NightmareUtils.getNiteMultiplier());
            boolean isEclipse = NightmareUtils.getIsMobEclipsed(this);

            if (this.rand.nextInt((this.worldObj.getDifficulty() == Difficulties.HOSTILE ? 2 : 6)) < 2 - NightmareUtils.getNiteMultiplier()) {
                // 100% on hostile, 33% on relaxed
                int progress = NightmareUtils.getWorldProgress(this.worldObj);
                EntitySkeleton skeleton = new EntitySkeleton(this.worldObj);
                skeleton.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
                skeleton.setHealth((float) Math.min((skeleton.getMaxHealth() - this.rand.nextInt(7) - 2 + progress * 2) * NightmareUtils.getNiteMultiplier(), skeleton.getMaxHealth() * NightmareUtils.getNiteMultiplier()));
                for (int i = 0; i < 5; i++) {
                    skeleton.setCurrentItemOrArmor(i, this.getCurrentItemOrArmor(i));
                    skeleton.setEquipmentDropChance(i,0f);
                }
                skeleton.getEntityAttribute(SharedMonsterAttributes.followRange).setAttribute(30d);

                if (this.getAttackTarget() != null) {
                    skeleton.setAttackTarget(this.getAttackTarget());
                    skeleton.entityToAttack = this.getEntityToAttack();
                }
                if (skeleton.getCurrentItemOrArmor(0) == null && this.worldObj.getDifficulty() == Difficulties.HOSTILE) {
                    if (rand.nextInt(Math.max((int) (25 / NightmareUtils.getNiteMultiplier()), 1)) == 0) {
                        skeleton.setCurrentItemOrArmor(0, new ItemStack(BTWItems.boneClub));
                    }
                }
                if (skeleton.getCurrentItemOrArmor(4) == null){
                    if (rand.nextInt(Math.max((int) (10 / NightmareUtils.getNiteMultiplier()), 1)) == 0) {
                        ItemStack var2 = new ItemStack(Item.skull,1,2);
                        skeleton.setCurrentItemOrArmor(4,var2);
                    }
                }
                if((progress >= 1 || areMobsEvolved) && this.rand.nextFloat() <= (0.3 - witherSkeletonChanceModifier)){
                    skeleton.setSkeletonType(1);
                }

                if(isEclipse){
                    skeleton.setSkeletonType(this.rand.nextInt(5));
                }
                this.worldObj.spawnEntityInWorld(skeleton);
                this.setDead();
            }
        }
    }

    @Inject(method = "checkForScrollDrop", at = @At("HEAD"),cancellable = true)
    private void doNotDropScrolls(CallbackInfo ci){
        ci.cancel();
    }


    @Unique private boolean canEntitySeeSun(){
        if(this.worldObj.isDaytime() && !this.worldObj.isRainingAtPos((int)this.posX, (int)this.posY, (int)this.posZ) && !this.isChild() && !this.inWater){
            boolean canSeeSky = this.worldObj.canBlockSeeTheSky(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY + (double) this.getEyeHeight()), MathHelper.floor_double(this.posZ));
            return this.worldObj.getDifficulty() == Difficulties.HOSTILE ? canSeeSky : canSeeSky && this.rand.nextBoolean();
        }
        return false;
    }

    @Inject(method = "attackEntityFrom", at = @At("HEAD"))
    private void transformIntoSkeletonOnFireDeath(DamageSource par1DamageSource, float par2, CallbackInfoReturnable<Boolean> cir){
        if (!this.isEntityInvulnerable() && !this.isVillager() && this.getHealth() <= par2) {
            if (canEntitySeeSun()) {
                boolean shouldBurn =
                        !isCrystalHead(this) &&
                                !this.isImmuneToFire &&
                                this.getCurrentItemOrArmor(4) == null &&
                                !this.isInWater();

                if (shouldBurn) {
                    this.onKilledBySun();
                }
            } else if(NightmareUtils.getIsMobEclipsed(this) && !this.worldObj.isRemote){
                summonSilverfish(this);
            }
        }
    }

    @Unique private static void summonSilverfish(EntityMob zombie){
        if (!(zombie instanceof EntityPigZombie) && !(zombie instanceof EntityShadowZombie)) {
            int i = zombie.rand.nextInt(5)+1;
            while(i > 0){
                if(i >= 2 && zombie.rand.nextBoolean()){
                    JungleSpiderEntity spider = new JungleSpiderEntity(zombie.worldObj);
                    spider.copyLocationAndAnglesFrom(zombie);
                    zombie.worldObj.spawnEntityInWorld(spider);
                    i -= 2;
                } else{
                    EntitySilverfish fish = new EntitySilverfish(zombie.worldObj);
                    fish.setLocationAndAngles(zombie.posX,zombie.posY,zombie.posZ, zombie.rand.nextFloat()*90, zombie.rotationPitch);
                    fish.copyLocationAndAnglesFrom(zombie);
                    zombie.worldObj.spawnEntityInWorld(fish);
                    i--;
                }
            }
            zombie.onDeath(DamageSource.generic);
            zombie.setDead();
        }
    }

    @Inject(method = "attackEntityAsMob", at = @At("HEAD"))
    private void manageEclipseAttack(Entity attackedEntity, CallbackInfoReturnable<Boolean> cir){
        if(NightmareUtils.getIsMobEclipsed(this)){
            if(rand.nextInt(3) == 0 && attackedEntity instanceof EntityLivingBase){
                ((EntityLivingBase) attackedEntity).addPotionEffect(new PotionEffect(Potion.poison.id, 40,0));
            }
        }
    }

    @Override
    protected void entityLivingDropFewItems(boolean par1, int par2) {
        if (par1 && NightmareUtils.getIsMobEclipsed(this)) {
            for(int i = 0; i < (par2 * 2) + 1; i++) {
                if (this.rand.nextInt(8) == 0) {
                    this.dropItem(NMItems.darksunFragment.itemID, 1);
                    if (this.rand.nextBoolean()) {
                        break;
                    }
                }
            }

            int itemID = NMItems.decayedFlesh.itemID;
            int var4 = this.rand.nextInt(3);
            if (par2 > 0) {
                var4 += this.rand.nextInt(par2 + 1);
            }
            for (int var5 = 0; var5 < var4; ++var5) {
                if(this.rand.nextInt(3) == 0) continue;
                this.dropItem(itemID, 1);
            }

        }
        int bloodOrbID = NightmareUtils.getIsBloodMoon() ? NMItems.bloodOrb.itemID : 0;

        if (bloodOrbID > 0 && par1) {
            int dropCount = this.rand.nextInt(2); // 0 - 1
            if(((EntityZombie)(Object)this) instanceof EntityPigZombie){
                dropCount = this.rand.nextInt(6) == 0 ? 1 : 0;
            }

            if (this.isCrystalHead(this)) {
                dropCount += 3;
            } else if(this.getHeldItem() != null && (this.getHeldItem().itemID == Item.axeGold.itemID || this.getHeldItem().itemID == BTWItems.steelSword.itemID)){
                dropCount += 1;
            }

            for (int i = 0; i < dropCount; ++i) {
                this.dropItem(bloodOrbID, 1);
            }
        }
        if(this.isWearingAnyDiamondArmor(this)){
            this.dropItem(Item.diamond.itemID, 1);
        }
        super.entityLivingDropFewItems(par1, par2);
    }



    @Inject(method = "checkForLooseFood", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;playAuxSFX(IIIII)V"))
    private void healZombie(CallbackInfo ci){
        this.heal(4.0F);
        this.addPotionEffect(new PotionEffect(Potion.damageBoost.id,80,0));
        this.addPotionEffect(new PotionEffect(Potion.moveSpeed.id,160,1));
    }

    @Inject(method = "addRandomArmor", at = @At("TAIL"))
    private void manageZombieVariants(CallbackInfo ci) {
        if (this.worldObj != null && !this.isVillager()) {
            int progress = NightmareUtils.getWorldProgress(this.worldObj);
            double bloodMoonModifier = NightmareUtils.getIsBloodMoon() ? 0.5 : 1;
            boolean isHostile = this.worldObj.getDifficulty() == Difficulties.HOSTILE;
            boolean isEclipse = NightmareUtils.getIsMobEclipsed(this);

            if (!isEclipse) {
                // NON ECLIPSE
                if (rand.nextInt(Math.max(MathHelper.floor_double((isHostile ? 50 : 100) * bloodMoonModifier * (1 / NightmareUtils.getNiteMultiplier())), 2)) == 0) {
                    this.setCurrentItemOrArmor(0, new ItemStack(BTWItems.boneClub));
                    this.equipmentDropChances[0] = 0f;
                    this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute(Math.floor(3.0 + progress*1.5));
                    // 3.0 -> 4.0 -> 6.0 -> 7.0
                } else if (rand.nextInt(Math.max(MathHelper.floor_double((isHostile ? 18 : 20) * bloodMoonModifier * (1 / NightmareUtils.getNiteMultiplier())), 2)) == 0) {
                    this.setCurrentItemOrArmor(0, new ItemStack(Item.swordWood));
                    this.equipmentDropChances[0] = 0f;
                    this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute(2.0 + progress);
                    // 2.0 -> 3.0 -> 4.0 -> 5.0
                }

                if(rand.nextInt(Math.max(MathHelper.floor_double(16 * bloodMoonModifier * (1 / NightmareUtils.getNiteMultiplier())), 2)) == 0 && isHostile && this.posY <= 50){
                    this.setCurrentItemOrArmor(0, new ItemStack(Item.pickaxeStone));
                    this.equipmentDropChances[0] = 0f;
                    List<ItemStack> leatherArmor = getLeatherArmor();
                    for (int i = 1; i <= 4; i++) {
                        if(this.getCurrentItemOrArmor(i) == null){
                            this.setCurrentItemOrArmor(i, leatherArmor.get(i - 1));
                            this.equipmentDropChances[i] = 0;
                        }
                    }
                    this.getEntityAttribute(SharedMonsterAttributes.followRange).setAttribute(32.0d);
                }



                if (progress == 1 || areMobsEvolved) {
                    if (rand.nextInt(Math.max(MathHelper.floor_double((isHostile ? 18 : 68) * bloodMoonModifier * (1 / NightmareUtils.getNiteMultiplier())), 2)) == 0) {
                        this.setCurrentItemOrArmor(0, new ItemStack(Item.axeGold));
                        this.setCurrentItemOrArmor(4, new ItemStack(Item.helmetGold));
                        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute(6.0);
                        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute((isHostile ? 0.34f : 0.29f) * ((NightmareUtils.getNiteMultiplier() - 1) / 20 + 1));
                        this.getEntityAttribute(SharedMonsterAttributes.followRange).setAttribute(32.0d);
                        this.equipmentDropChances[0] = 0f;
                        this.equipmentDropChances[4] = 0f;
                    }
                } else if (rand.nextInt((int) Math.max(MathHelper.floor_double ((isHostile ? 22 : 50) * bloodMoonModifier) * (1 / NightmareUtils.getNiteMultiplier()), 2)) == 0 && (progress > 1 || areMobsEvolved)) {
                    this.setCurrentItemOrArmor(0, new ItemStack(Item.swordDiamond));
                    this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(36.0);
                    this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute(Math.floor(12.0 + (progress-2)*3) - (isHostile ? 0 : 4));
                    this.equipmentDropChances[0] = 0f;
                    // 12.0 -> 15.0
                }

                if(isHostile && (NightmareUtils.getIsBloodMoon() || areMobsEvolved)){
                    if(rand.nextInt((int) Math.max(50 * (1 / NightmareUtils.getNiteMultiplier()), 2)) == 0){
                        this.setCurrentItemOrArmor(0, new ItemStack(BTWItems.steelSword));
                        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute(Math.floor(12.0 + (progress-2)*3));
                        this.equipmentDropChances[0] = 0;
                    }
                    float streakModifier = 0.0f;
                    for (int i = 1; i <= 4; i++) {
                        if(this.getCurrentItemOrArmor(i) == null){ // starts at index 1, index 0 is held item
                            if(rand.nextFloat() < 0.07f + streakModifier){
                                streakModifier += 0.01f;
                                streakModifier += (float) (NightmareUtils.getNiteMultiplier() - 1);
                                List<ItemStack> diamondArmor = getDiamondArmor();
                                this.setCurrentItemOrArmor(i, diamondArmor.get(i - 1));
                                this.equipmentDropChances[i] = 0;
                            }
                        }
                    }
                }
            }
            else{
                // ECLIPSE
                if (rand.nextInt((int) Math.max(8 * (1 / NightmareUtils.getNiteMultiplier()), 2)) == 0) {
                    this.setCurrentItemOrArmor(0, new ItemStack(BTWItems.boneClub));
                    this.equipmentDropChances[0] = 0f;
                    this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute(Math.floor(3.0 + progress*1.5));
                    // 3.0 -> 4.0 -> 6.0 -> 7.0
                } else if (rand.nextInt((int) Math.max(8 * (1 / NightmareUtils.getNiteMultiplier()), 2)) == 0) {
                    this.setCurrentItemOrArmor(0, new ItemStack(Item.swordWood));
                    this.equipmentDropChances[0] = 0f;
                    this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute(2.0 + progress);
                    // 2.0 -> 3.0 -> 4.0 -> 5.0
                }

                if(rand.nextInt((int) Math.max(8 * (1 / NightmareUtils.getNiteMultiplier()), 2)) == 0 && isHostile){
                    if (this.posY <= 45) {
                        this.setCurrentItemOrArmor(0, new ItemStack(Item.pickaxeStone));
                        this.equipmentDropChances[0] = 0f;
                        this.getEntityAttribute(SharedMonsterAttributes.followRange).setAttribute(24.0d);
                    } else{
                        this.setCurrentItemOrArmor(0, new ItemStack(Item.swordStone));
                        this.equipmentDropChances[0] = 0f;
                    }
                }

                if (rand.nextInt((int) Math.max(10 * (1 / NightmareUtils.getNiteMultiplier()), 2)) == 0) {
                    ItemStack var1 = new ItemStack(Item.axeGold);
                    ItemStack var2 = new ItemStack(Item.helmetGold);
                    this.setCurrentItemOrArmor(0, var1);
                    this.setCurrentItemOrArmor(4, var2);
                    this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute(6.0);
                    this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.34f);
                    this.equipmentDropChances[0] = 0f;
                    this.equipmentDropChances[4] = 0f;
                }
            }

            // iron tool zombies have a 33% chance to spawn with a leather helmet
            if(this.getHeldItem() != null && (this.getHeldItem().itemID == Item.swordIron.itemID || this.getHeldItem().itemID == Item.shovelIron.itemID) && this.rand.nextInt(100) != 0 && this.getCurrentItemOrArmor(4) == null){
                this.setCurrentItemOrArmor(4, new ItemStack(Item.helmetLeather));
                this.equipmentDropChances[4] = 0f;
            }
        }
    }
    @Unique
    private static @NotNull List<ItemStack> getLeatherArmor() {
        ItemStack boots = new ItemStack(Item.bootsLeather);
        ItemStack pants = new ItemStack(Item.legsLeather);
        ItemStack chest = new ItemStack(Item.plateLeather);
        ItemStack helmet = new ItemStack(Item.helmetLeather);

        List<ItemStack> leatherArmorList = new ArrayList<>(4);
        leatherArmorList.add(boots);
        leatherArmorList.add(pants);
        leatherArmorList.add(chest);
        leatherArmorList.add(helmet);
        return leatherArmorList;
    }

    @Override
    public float knockbackMagnitude() {
        return this.isWeighted() ? 0.2f : super.knockbackMagnitude();
    }

    @Inject(method = "applyEntityAttributes", at = @At("TAIL"))
    private void addAdditionalAttributes(CallbackInfo ci){
        if (this.worldObj != null) {
            int progress = 0;
            boolean isEclipse = NightmareUtils.getIsMobEclipsed(this);
            boolean isBloodMoon = NightmareUtils.getIsBloodMoon();

            try {
                progress = NightmareUtils.getWorldProgress(this.worldObj);
            } catch (RuntimeException ignored) {}


            this.getEntityAttribute(BTWAttributes.armor).setAttribute((2.0d + progress * (isBloodMoon ? 1.5 : 1) + (isEclipse ? rand.nextInt(3)+2 : 0)) * NightmareUtils.getNiteMultiplier());
            this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(((isBloodMoon ? 24 : 20) + progress * (isBloodMoon ? 8 : 6) + (isEclipse ? 20 : 0)) * NightmareUtils.getNiteMultiplier());
            // 40 -> 46 -> 52 -> 58 eclipse
            // 24 -> 32 -> 40 -> 48 bm
            // 20 -> 26 -> 32 -> 38 normal
            this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setAttribute(((double) progress / (isBloodMoon ? 5 : 10)) * NightmareUtils.getNiteMultiplier());

            this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute((3.0d + (progress > 0 ? 1 : 0)) * NightmareUtils.getNiteMultiplier());
        }
    }

    @Inject(method = "addRandomArmor", // changes iron sword damage specifically
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/src/EntityZombie;setCurrentItemOrArmor(ILnet/minecraft/src/ItemStack;)V",
                    ordinal = 0))
    private void setDamageIfIronSword(CallbackInfo ci){
        if (this.worldObj!= null) {
            if(NightmareUtils.getIsBloodMoon()){
                this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute((Math.floor(4.0 + (NightmareUtils.getWorldProgress(this.worldObj)) * 2.5)) * NightmareUtils.getNiteMultiplier());
                // 4.0 -> 6.0 -> 9.0 -> 11.0
            } else {
                this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute((Math.floor(4.0 + (NightmareUtils.getWorldProgress(this.worldObj)) * 1.75)) * NightmareUtils.getNiteMultiplier());
                // 4.0 -> 5.0 -> 7.0 -> 9.0
            }
        }
    }
    @Inject(method = "addRandomArmor", // summons crystalhead zombie on a 1% chance
            at = @At("TAIL"))
    private void chanceToSpawnShadowOrCrystal(CallbackInfo ci){
        if (this.worldObj != null) {
            int chance = 60;
            if(this.worldObj.getDifficulty() == Difficulties.HOSTILE){
                chance = 32;
            }
            if(NightmareUtils.getIsBloodMoon()){chance /= 2;}
            if(NightmareUtils.getIsMobEclipsed(this)){chance /= 4;}

            if((NightmareUtils.getWorldProgress(this.worldObj) >= 2 || areMobsEvolved) && rand.nextInt((int) Math.max(chance * (1 / NightmareUtils.getNiteMultiplier()), 4)) == 0){
                summonCrystalHeadAtPos();
            } else if((NightmareUtils.getWorldProgress(this.worldObj) >= 1 || areMobsEvolved) && rand.nextInt((int) Math.max(4 * (1 / NightmareUtils.getNiteMultiplier()), 2)) == 0){
                summonShadowZombieAtPos((EntityZombie)(Object)this);
            }
        }
    }
    @Inject(method = "<init>", at = @At("TAIL"))
    private void manageEclipseChance(World world, CallbackInfo ci){
        NightmareUtils.manageEclipseChance(this,12);
    }


    @ModifyConstant(method = "addRandomArmor",constant = @Constant(floatValue = 0.05F))
    private float modifyChanceToHaveIronTool(float constant){
        if (this.worldObj != null) {
            if((EntityZombie)(Object)this instanceof EntityShadowZombie){
                return 0;
            }
            if(NightmareUtils.getWorldProgress(this.worldObj)==3){return 0.3f;}
            else {
                int bloodMoonToolBonus = NightmareUtils.getIsBloodMoon() ? 2 : 1;
                return (float) ((0.05F + (NightmareUtils.getWorldProgress(this.worldObj) * (0.03 * bloodMoonToolBonus))) * NightmareUtils.getNiteMultiplier());
            }
        }
        return constant;
        // 0.05f -> 0.08f -> 0.11f -> 0.30f
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void addLungeAI(World par1World, CallbackInfo ci){
        this.targetTasks.addTask(2, new EntityAILunge(this, true));
    }

    @Inject(method = "onUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityMob;onUpdate()V"))
    private void deleteEndCrystalIfZombieDied(CallbackInfo ci){
        if (this.worldObj != null) {
            if(isCrystalHead(this)){
                if(this.getHealth() <= 0.5f){
                    this.riddenByEntity.setDead();
                }
            }
        }
    }

//    @Override
//    protected void dropFewItems(boolean par1, int par2) {
//        super.dropFewItems(par1, par2);
//        if((NightmareUtils.getIsBloodMoon() || areMobsEvolved) && this.isWearingArmorSet(this, getDiamondArmorIDs()) && rand.nextInt(20) == 0){
//            this.dropItem(Item.diamond.itemID,1);
//        }
//    }

    @Unique
    private void summonCrystalHeadAtPos(){
        EntityZombie crystalhead = new EntityZombie(this.worldObj);
        Entity crystal = new EntityEnderCrystal(this.worldObj);
        crystal.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
        crystalhead.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
        this.worldObj.spawnEntityInWorld(crystal);
        this.worldObj.spawnEntityInWorld(crystalhead);
        crystal.mountEntity(crystalhead);

        ItemStack var1 = new ItemStack(Item.skull,1,1);
        crystalhead.setCurrentItemOrArmor(4, var1);
        crystalhead.setCurrentItemOrArmor(0, null);
        crystalhead.setCurrentItemOrArmor(1, setItemColor(new ItemStack(BTWItems.woolBoots))); // black
        crystalhead.setCurrentItemOrArmor(2, setItemColor(new ItemStack(BTWItems.woolLeggings))); // black
        crystalhead.setCurrentItemOrArmor(3, setItemColor(new ItemStack(BTWItems.woolChest))); // black
        for (int i = 0; i < 5; i++) {
            crystalhead.setEquipmentDropChance(i,0f);
        }
        this.setDead();
    }

    @Unique
    private void summonShadowZombieAtPos(EntityZombie zombie){
        EntityShadowZombie shadowZombie = new EntityShadowZombie(this.worldObj);
        shadowZombie.setLocationAndAngles(zombie.posX, zombie.posY, zombie.posZ, zombie.rotationYaw, zombie.rotationPitch);

        shadowZombie.setCurrentItemOrArmor(1, null);
        shadowZombie.setCurrentItemOrArmor(2, null);
        shadowZombie.setCurrentItemOrArmor(3, null);
        shadowZombie.setCurrentItemOrArmor(4, null);
        zombie.setDead();
        zombie.worldObj.spawnEntityInWorld(shadowZombie);
    }

    @Unique
    private ItemStack setItemColor(ItemStack item){
        NBTTagCompound var3 = item.getTagCompound();
        if (var3 == null) {
            var3 = new NBTTagCompound();
            item.setTagCompound(var3);
        }
        NBTTagCompound var4 = var3.getCompoundTag("display");
        if (!var3.hasKey("display")) {
            var3.setCompoundTag("display", var4);
        }

        var4.setInteger("color", 1052688);
        item.setTagCompound(var3);
        return item;
    }

    @Unique
    public boolean isCrystalHead(Entity par1Entity){
        return par1Entity.riddenByEntity instanceof EntityEnderCrystal;
    }

    @Unique
    private static @NotNull List<ItemStack> getDiamondArmor() {
        ItemStack boots = new ItemStack(Item.bootsDiamond);
        ItemStack pants = new ItemStack(Item.legsDiamond);
        ItemStack chest = new ItemStack(Item.plateDiamond);
        ItemStack helmet = new ItemStack(Item.helmetDiamond);

        List<ItemStack> advancedArmorList = new ArrayList<>(4);
        advancedArmorList.add(boots);
        advancedArmorList.add(pants);
        advancedArmorList.add(chest);
        advancedArmorList.add(helmet);
        return advancedArmorList;
    }

    @Unique
    private static @NotNull List<Integer> getDiamondArmorIDs() {
        ItemStack boots = new ItemStack(Item.bootsDiamond);
        ItemStack pants = new ItemStack(Item.legsDiamond);
        ItemStack chest = new ItemStack(Item.plateDiamond);
        ItemStack helmet = new ItemStack(Item.helmetDiamond);

        List<Integer> advancedArmorList = new ArrayList<>(4);
        advancedArmorList.add(boots.itemID);
        advancedArmorList.add(pants.itemID);
        advancedArmorList.add(chest.itemID);
        advancedArmorList.add(helmet.itemID);
        return advancedArmorList;
    }

    @Unique private boolean isWearingAnyDiamondArmor(EntityMob mob){
        for(int i = 1; i < 5; i++){
            if(mob.getCurrentItemOrArmor(i) != null && getDiamondArmorIDs().contains(mob.getCurrentItemOrArmor(i).itemID)){
                return true;
            }
        }
        return false;
    }
}