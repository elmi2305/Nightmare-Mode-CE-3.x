package com.itlesports.nightmaremode.mixin;

import btw.block.BTWBlocks;
import btw.community.nightmaremode.NightmareMode;
import btw.entity.attribute.BTWAttributes;
import btw.entity.mob.JungleSpiderEntity;
import btw.item.BTWItems;
import btw.world.util.difficulty.Difficulties;
import com.itlesports.nightmaremode.AITasks.EntityAIChaseTargetSmart;
import com.itlesports.nightmaremode.AITasks.EntityAILunge;
import com.itlesports.nightmaremode.NMDifficultyParam;
import com.itlesports.nightmaremode.NMUtils;
import com.itlesports.nightmaremode.entity.EntitySkeletonDrowned;
import com.itlesports.nightmaremode.entity.EntityShadowZombie;
import com.itlesports.nightmaremode.entity.EntitySkeletonMelted;
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
            float witherSkeletonChanceModifier = this.worldObj.getDifficultyParameter(NMDifficultyParam.ShouldMobsBeBuffed.class) ? 0f : (float) (0.2f * NMUtils.getNiteMultiplier());
            boolean isEclipse = NMUtils.getIsMobEclipsed(this);

            if (this.rand.nextInt((this.worldObj.getDifficultyParameter(NMDifficultyParam.ShouldMobsBeBuffed.class) ? 2 : 6)) < 2 - NMUtils.getNiteMultiplier()) {
                // 100% on hostile, 33% on relaxed
                int progress = NMUtils.getWorldProgress();
                EntitySkeleton skeleton = new EntitySkeleton(this.worldObj);
                skeleton.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
                skeleton.setHealth((float) Math.min((skeleton.getMaxHealth() - this.rand.nextInt(7) - 2 + progress * 2) * NMUtils.getNiteMultiplier(), skeleton.getMaxHealth() * NMUtils.getNiteMultiplier()));
                for (int i = 0; i < 5; i++) {
                    skeleton.setCurrentItemOrArmor(i, this.getCurrentItemOrArmor(i));
                    skeleton.setEquipmentDropChance(i,0f);
                }
                skeleton.getEntityAttribute(SharedMonsterAttributes.followRange).setAttribute(30d);

                if (this.getAttackTarget() != null) {
                    skeleton.setAttackTarget(this.getAttackTarget());
                    skeleton.entityToAttack = this.getEntityToAttack();
                }
                if (skeleton.getCurrentItemOrArmor(0) == null && this.worldObj.getDifficultyParameter(NMDifficultyParam.ShouldMobsBeBuffed.class)) {
                    if (rand.nextInt(Math.max((int) (25 / NMUtils.getNiteMultiplier()), 1)) == 0) {
                        skeleton.setCurrentItemOrArmor(0, new ItemStack(BTWItems.boneClub));
                    }
                }
                if (skeleton.getCurrentItemOrArmor(4) == null){
                    if (rand.nextInt(Math.max((int) (10 / NMUtils.getNiteMultiplier()), 1)) == 0) {
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
            return this.worldObj.getDifficultyParameter(NMDifficultyParam.ShouldMobsBeBuffed.class) ? canSeeSky : canSeeSky && this.rand.nextBoolean();
        }
        return false;
    }

    @Inject(method = "attackEntityFrom", at = @At("HEAD"), cancellable = true)
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
            } else if(NMUtils.getIsMobEclipsed(this) && !this.worldObj.isRemote){
                summonSilverfish(this);
            } else if(par1DamageSource == DamageSource.drown || par1DamageSource == DamageSource.lava){
                this.transformToVariant(par1DamageSource == DamageSource.lava);
                cir.setReturnValue(true);
            }
        }
    }
    @Unique private void transformToVariant(boolean wasFireDamage){
        int progress = NMUtils.getWorldProgress();
        EntitySkeleton skeleton;
        if (wasFireDamage) {
            skeleton = new EntitySkeletonMelted(this.worldObj);
            for (int i = 0; i < 10; i++) {
                double offsetX = (this.rand.nextDouble() - 0.5D) * 2.5D;
                double offsetY = this.rand.nextDouble() * this.height * 1.5D;
                double offsetZ = (this.rand.nextDouble() - 0.5D) * 2.5D;

                this.worldObj.playAuxSFX(2278, (int) (this.posX + offsetX), (int) (this.posY + offsetY), (int) (this.posZ + offsetZ), 0);
            }
        } else{
            skeleton = new EntitySkeletonDrowned(this.worldObj);
        }

        skeleton.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
        skeleton.setHealth((float) Math.min((skeleton.getMaxHealth() - this.rand.nextInt(7) - 2 + progress * 2) * NMUtils.getNiteMultiplier(), skeleton.getMaxHealth() * NMUtils.getNiteMultiplier()));
        for (int i = 0; i < 5; i++) {
            skeleton.setCurrentItemOrArmor(i, this.getCurrentItemOrArmor(i));
            skeleton.setEquipmentDropChance(i,this.equipmentDropChances[i]);
        }
        skeleton.getEntityAttribute(SharedMonsterAttributes.followRange).setAttribute(30d);

        if (this.hasAttackTarget()) {
            skeleton.setAttackTarget(this.getAttackTarget());
            skeleton.entityToAttack = this.getEntityToAttack();
        }
        if (!this.worldObj.isRemote) {
            this.worldObj.spawnEntityInWorld(skeleton);
        }
        this.setDead();
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
        if(NMUtils.getIsMobEclipsed(this)){
            if(rand.nextInt(3) == 0 && attackedEntity instanceof EntityLivingBase){
                ((EntityLivingBase) attackedEntity).addPotionEffect(new PotionEffect(Potion.poison.id, 40,0));
            }
        }
    }
    @Unique private boolean isValidForEventLoot = false;
    @Inject(method = "attackEntityFrom", at = @At("HEAD"))
    private void storeLastHit(DamageSource par1DamageSource, float par2, CallbackInfoReturnable<Boolean> cir){
        this.isValidForEventLoot = par1DamageSource.getEntity() instanceof EntityPlayer;
    }

    @Override
    protected void entityLivingDropFewItems(boolean par1, int par2) {
        if (par1 && NMUtils.getIsMobEclipsed(this) && isValidForEventLoot) {
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
        int bloodOrbID = NMUtils.getIsBloodMoon() ? NMItems.bloodOrb.itemID : 0;

        if (bloodOrbID > 0 && par1 && isValidForEventLoot) {
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
            boolean wasVariantSelected = false;

            int progress = NMUtils.getWorldProgress();
            double bloodMoonModifier = NMUtils.getIsBloodMoon() ? 0.5 : 1;
            boolean isHostile = this.worldObj.getDifficultyParameter(NMDifficultyParam.ShouldMobsBeBuffed.class);
            boolean isEclipse = NMUtils.getIsMobEclipsed(this);

            if (!isEclipse) {
                // NON ECLIPSE
                if (rand.nextInt(Math.max(MathHelper.floor_double((isHostile ? 50 : 100) * bloodMoonModifier * (1 / NMUtils.getNiteMultiplier())), 2)) == 0) {
                    this.setCurrentItemOrArmor(0, new ItemStack(BTWItems.boneClub));
                    this.equipmentDropChances[0] = 0f;
                    this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute(Math.floor(3.0 + progress*1.5));
                    // 3.0 -> 4.0 -> 6.0 -> 7.0
                    wasVariantSelected = true;
                } else if (rand.nextInt(Math.max(MathHelper.floor_double((isHostile ? 18 : 20) * bloodMoonModifier * (1 / NMUtils.getNiteMultiplier())), 2)) == 0) {
                    this.setCurrentItemOrArmor(0, new ItemStack(Item.swordWood));
                    this.equipmentDropChances[0] = 0f;
                    this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute(2.0 + progress);
                    // 2.0 -> 3.0 -> 4.0 -> 5.0
                    wasVariantSelected = true;
                }

                if(rand.nextInt(Math.max(MathHelper.floor_double(16 * bloodMoonModifier * (1 / NMUtils.getNiteMultiplier())), 2)) == 0 && isHostile && this.posY <= 50){
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
                    wasVariantSelected = true;
                }



                if (progress == 1 || areMobsEvolved) {
                    if (rand.nextInt(Math.max(MathHelper.floor_double((isHostile ? 18 : 68) * bloodMoonModifier * (1 / NMUtils.getNiteMultiplier())), 2)) == 0) {
                        this.setCurrentItemOrArmor(0, new ItemStack(Item.axeGold));
                        this.setCurrentItemOrArmor(4, new ItemStack(Item.helmetGold));
                        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute(6.0);
                        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute((isHostile ? 0.34f : 0.29f) * ((NMUtils.getNiteMultiplier() - 1) / 20 + 1));
                        this.getEntityAttribute(SharedMonsterAttributes.followRange).setAttribute(32.0d);
                        this.equipmentDropChances[0] = 0f;
                        this.equipmentDropChances[4] = 0f;
                        wasVariantSelected = true;
                    }
                } else if (rand.nextInt((int) Math.max(MathHelper.floor_double ((isHostile ? 22 : 50) * bloodMoonModifier) * (1 / NMUtils.getNiteMultiplier()), 2)) == 0 && (progress > 1 || areMobsEvolved)) {
                    this.setCurrentItemOrArmor(0, new ItemStack(Item.swordDiamond));
                    this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(36.0);
                    this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute(Math.floor(12.0 + (progress-2)*3) - (isHostile ? 0 : 4));
                    this.equipmentDropChances[0] = 0f;
                    // 12.0 -> 15.0
                    wasVariantSelected = true;
                }

                if(isHostile && (NMUtils.getIsBloodMoon() || areMobsEvolved)){
                    if(rand.nextInt((int) Math.max(50 * (1 / NMUtils.getNiteMultiplier()), 2)) == 0){
                        this.setCurrentItemOrArmor(0, new ItemStack(BTWItems.steelSword));
                        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute(Math.floor(12.0 + (progress-2)*3));
                        this.equipmentDropChances[0] = 0;
                        wasVariantSelected = true;
                    }
                    float streakModifier = 0.0f;
                    for (int i = 1; i <= 4; i++) {
                        if(this.getCurrentItemOrArmor(i) == null){ // starts at index 1, index 0 is held item
                            if(rand.nextFloat() < 0.07f + streakModifier){
                                streakModifier += 0.01f;
                                streakModifier += (float) (NMUtils.getNiteMultiplier() - 1);
                                List<ItemStack> diamondArmor = getDiamondArmor();
                                this.setCurrentItemOrArmor(i, diamondArmor.get(i - 1));
                                this.equipmentDropChances[i] = 0;
                            }
                        }
                    }
                }
                if(isHostile && !wasVariantSelected && this.getCurrentItemOrArmor(4) == null && this.rand.nextInt(24) == 0){
                    this.setCurrentItemOrArmor(4, new ItemStack(BTWBlocks.carvedPumpkin));
                    this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.26f);
                    this.getEntityAttribute(BTWAttributes.armor).setAttribute(2f);
                    this.equipmentDropChances[4] = 0f;
                }
            }
            else{
                // ECLIPSE
                if (rand.nextInt((int) Math.max(8 * (1 / NMUtils.getNiteMultiplier()), 2)) == 0) {
                    this.setCurrentItemOrArmor(0, new ItemStack(BTWItems.boneClub));
                    this.equipmentDropChances[0] = 0f;
                    this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute(Math.floor(3.0 + progress*1.5));
                    // 3.0 -> 4.0 -> 6.0 -> 7.0
                } else if (rand.nextInt((int) Math.max(8 * (1 / NMUtils.getNiteMultiplier()), 2)) == 0) {
                    this.setCurrentItemOrArmor(0, new ItemStack(Item.swordWood));
                    this.equipmentDropChances[0] = 0f;
                    this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute(2.0 + progress);
                    // 2.0 -> 3.0 -> 4.0 -> 5.0
                }

                if(rand.nextInt((int) Math.max(8 * (1 / NMUtils.getNiteMultiplier()), 2)) == 0 && isHostile){
                    if (this.posY <= 45) {
                        this.setCurrentItemOrArmor(0, new ItemStack(Item.pickaxeStone));
                        this.equipmentDropChances[0] = 0f;
                        this.getEntityAttribute(SharedMonsterAttributes.followRange).setAttribute(24.0d);
                    } else{
                        this.setCurrentItemOrArmor(0, new ItemStack(Item.swordStone));
                        this.equipmentDropChances[0] = 0f;
                    }
                }

                if (rand.nextInt((int) Math.max(10 * (1 / NMUtils.getNiteMultiplier()), 2)) == 0) {
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

            // iron tool zombies have a 99% chance to spawn with a leather helmet
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
            boolean isEclipse = NMUtils.getIsMobEclipsed(this);
            boolean isBloodMoon = NMUtils.getIsBloodMoon();

            try {
                progress = NMUtils.getWorldProgress();
            } catch (RuntimeException ignored) {}


            this.getEntityAttribute(BTWAttributes.armor).setAttribute((2.0d + progress * (isBloodMoon ? 1.5 : 1) + (isEclipse ? rand.nextInt(3)+2 : 0)) * NMUtils.getNiteMultiplier());
            this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(((isBloodMoon ? 24 : 20) + progress * (isBloodMoon ? 8 : 6) + (isEclipse ? 20 : 0)) * NMUtils.getNiteMultiplier());
            // 40 -> 46 -> 52 -> 58 eclipse
            // 24 -> 32 -> 40 -> 48 bm
            // 20 -> 26 -> 32 -> 38 normal
            this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setAttribute(((double) progress / (isBloodMoon ? 5 : 10)) * NMUtils.getNiteMultiplier());

            this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute((3.0d + (progress > 0 ? 1 : 0)) * NMUtils.getNiteMultiplier());
        }
    }

    @Inject(method = "addRandomArmor", // changes iron sword damage specifically
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/src/EntityZombie;setCurrentItemOrArmor(ILnet/minecraft/src/ItemStack;)V",
                    ordinal = 0))
    private void setDamageIfIronSword(CallbackInfo ci){
        if (this.worldObj!= null) {
            if(NMUtils.getIsBloodMoon()){
                this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute((Math.floor(4.0 + (NMUtils.getWorldProgress()) * 2.5)) * NMUtils.getNiteMultiplier());
                // 4.0 -> 6.0 -> 9.0 -> 11.0
            } else {
                this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute((Math.floor(4.0 + (NMUtils.getWorldProgress()) * 1.75)) * NMUtils.getNiteMultiplier());
                // 4.0 -> 5.0 -> 7.0 -> 9.0
            }
        }
    }
    @Inject(method = "addRandomArmor", // summons crystalhead zombie on a 1% chance
            at = @At("TAIL"))
    private void chanceToSpawnShadowOrCrystal(CallbackInfo ci){
        if (this.worldObj != null) {
            int chance = 60;
            if(this.worldObj.getDifficultyParameter(NMDifficultyParam.ShouldMobsBeBuffed.class)){
                chance = 32;
            }
            if(NMUtils.getIsBloodMoon()){chance /= 2;}
            if(NMUtils.getIsMobEclipsed(this)){chance /= 4;}

            if((NMUtils.getWorldProgress() >= 2 || areMobsEvolved) && rand.nextInt((int) Math.max(chance * (1 / NMUtils.getNiteMultiplier()), 4)) == 0){
                summonCrystalHeadAtPos();
            } else if((NMUtils.getWorldProgress() >= 1 || areMobsEvolved) && rand.nextInt((int) Math.max(4 * (1 / NMUtils.getNiteMultiplier()), 2)) == 0){
                summonShadowZombieAtPos((EntityZombie)(Object)this);
            }
        }
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void manageEclipseChance(World world, CallbackInfo ci){
        NMUtils.manageEclipseChance(this,12);
    }


    @ModifyConstant(method = "addRandomArmor",constant = @Constant(floatValue = 0.05F))
    private float modifyChanceToHaveIronTool(float constant){
        if (this.worldObj != null) {
            if((EntityZombie)(Object)this instanceof EntityShadowZombie){
                return 0;
            }
            if(NMUtils.getWorldProgress()==3){return 0.3f;}
            else {
                int bloodMoonToolBonus = NMUtils.getIsBloodMoon() ? 2 : 1;
                return (float) ((0.05F + (NMUtils.getWorldProgress() * (0.03 * bloodMoonToolBonus))) * NMUtils.getNiteMultiplier());
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
        if(!(this.getAttackTarget() instanceof EntityPlayer)){
            if((this.ticksExisted & 32) == 0){
                EntityPlayer p;
                if((p = this.worldObj.getClosestVulnerablePlayerToEntity(this, 20)) != null){
                    this.setAttackTarget(p);
                }
            }
        }

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
//        if((NMUtils.getIsBloodMoon() || areMobsEvolved) && this.isWearingArmorSet(this, getDiamondArmorIDs()) && rand.nextInt(20) == 0){
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
        crystalhead.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.38f);
        EntityPlayer nearestPlayer = this.worldObj.getClosestPlayerToEntity(crystalhead, 100);
        if (nearestPlayer != null) {
            crystalhead.setAttackTarget(nearestPlayer);
            crystalhead.getMoveHelper().setMoveTo(nearestPlayer.posX,nearestPlayer.posY,nearestPlayer.posZ, 1.2f);
        }
        this.setDead();
    }


@Inject(method = "<init>", at = @At("TAIL"))
private void addHordeTasks(World par1World, CallbackInfo ci){
    if (NightmareMode.hordeMode) {
        this.tasks.removeAllTasksOfClass(EntityAIAttackOnCollide.class);
        this.tasks.addTask(4, new EntityAIChaseTargetSmart(this, 1.0D));
    }
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