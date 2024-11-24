package com.itlesports.nightmaremode.mixin;

import btw.entity.attribute.BTWAttributes;
import btw.item.BTWItems;
import btw.world.util.difficulty.Difficulties;
import com.itlesports.nightmaremode.AITasks.EntityAILunge;
import com.itlesports.nightmaremode.EntityShadowZombie;
import com.itlesports.nightmaremode.NightmareUtils;
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

    public EntityZombieMixin(World par1World) {
        super(par1World);
    }

    @Unique boolean doNotDropItems;

    @Shadow public abstract boolean isVillager();

    @Unique public void onKilledBySun() {
        if (!this.worldObj.isRemote) {
            float witherSkeletonChanceModifier = this.worldObj.getDifficulty() == Difficulties.HOSTILE ? 0f : 0.2f;
            if (this.rand.nextInt((this.worldObj.getDifficulty() == Difficulties.HOSTILE ? 2 : 6)) < 2) {
                // 100% on hostile, 33% on relaxed
                int progress = NightmareUtils.getWorldProgress(this.worldObj);
                EntitySkeleton skeleton = new EntitySkeleton(this.worldObj);
                skeleton.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
                skeleton.setHealth(Math.min(skeleton.getMaxHealth() - this.rand.nextInt(7) - 2 + progress * 2, skeleton.getMaxHealth()));
                for (int i = 0; i < 5; i++) {
                    skeleton.setCurrentItemOrArmor(i, this.getCurrentItemOrArmor(i));
                }
                if (skeleton.getCurrentItemOrArmor(0) == null && this.worldObj.getDifficulty() == Difficulties.HOSTILE) {
                    if (rand.nextInt(25) == 0) {
                        skeleton.setCurrentItemOrArmor(0, new ItemStack(BTWItems.boneClub));
                    }
                }
                if (skeleton.getCurrentItemOrArmor(4) == null){
                    if (rand.nextInt(10)==0) {
                        ItemStack var2 = new ItemStack(Item.skull,1,2);
                        skeleton.setCurrentItemOrArmor(4,var2);
                    }
                }
                if(progress >= 1 && this.rand.nextFloat() <= (0.3 - witherSkeletonChanceModifier)){
                    skeleton.setSkeletonType(1);
                }
                this.worldObj.spawnEntityInWorld(skeleton);
                this.setDead();
            }
        }
    }

    @Override
    public boolean getCanSpawnHere() {
        if(this.worldObj != null){
            if(NightmareUtils.getIsBloodMoon(this.worldObj)){
                return true;
            }
        }
        return super.getCanSpawnHere();
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
        if (!this.isEntityInvulnerable() && canEntitySeeSun()) {
            if ((!this.isVillager() && this.getHealth() <= par2) && !isCrystalHead(this) && !this.isImmuneToFire && this.getCurrentItemOrArmor(4) == null && !this.isInWater()) {this.onKilledBySun();}
        }
    }
    @Inject(method = "checkForLooseFood", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;playAuxSFX(IIIII)V"))
    private void healZombie(CallbackInfo ci){
        this.heal(4.0F);
        this.addPotionEffect(new PotionEffect(Potion.damageBoost.id,80,0));
        this.addPotionEffect(new PotionEffect(Potion.moveSpeed.id,160,1));
    }

    @Override
    protected void dropEquipment(boolean par1, int par2) {
        if (!this.doNotDropItems) {
            super.dropEquipment(par1, par2);
        }
    }


    @Inject(method = "addRandomArmor", at = @At("TAIL"))
    private void manageZombieVariants(CallbackInfo ci) {
        if (this.worldObj != null && !this.isVillager()) {
            int progress = NightmareUtils.getWorldProgress(this.worldObj);
            double bloodMoonModifier = NightmareUtils.getIsBloodMoon(this.worldObj) ? 0.5 : 1;
            boolean isHostile = this.worldObj.getDifficulty() == Difficulties.HOSTILE;

            if (rand.nextInt(MathHelper.floor_double((isHostile ? 50 : 100) * bloodMoonModifier)) == 0) {
                this.setCurrentItemOrArmor(0, new ItemStack(BTWItems.boneClub));
                this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute(Math.floor(3.0 + progress*1.5));
                // 3.0 -> 4.0 -> 6.0 -> 7.0
                this.doNotDropItems = true;
            } else if (rand.nextInt(MathHelper.floor_double((isHostile ? 18 : 20) * bloodMoonModifier)) == 0) {
                this.setCurrentItemOrArmor(0, new ItemStack(Item.swordWood));
                this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute(2.0 + progress);
                // 2.0 -> 3.0 -> 4.0 -> 5.0
                this.doNotDropItems = true;
            }

            if(rand.nextInt(MathHelper.floor_double(48 * bloodMoonModifier)) == 0 && isHostile && this.posY <= 45){
                this.setCurrentItemOrArmor(0, new ItemStack(Item.pickaxeStone));
                this.equipmentDropChances[0] = 0f;
                this.getEntityAttribute(SharedMonsterAttributes.followRange).setAttribute(24.0d);
            }


            if (progress == 1) {
                if (rand.nextInt(MathHelper.floor_double((isHostile ? 18 : 68) * bloodMoonModifier)) == 0) {
                    ItemStack var1 = new ItemStack(Item.axeGold);
                    ItemStack var2 = new ItemStack(Item.helmetGold);
                    this.setCurrentItemOrArmor(0, var1);
                    this.setCurrentItemOrArmor(4, var2);
                    this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute(6.0);
                    this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(isHostile ? 0.34f : 0.29f);
                    this.getEntityAttribute(SharedMonsterAttributes.followRange).setAttribute(32.0d);
                    this.equipmentDropChances[0] = 0f;
                }
            } else if (rand.nextInt(MathHelper.floor_double ((isHostile ? 22 : 50) * bloodMoonModifier)) == 0 && progress > 1) {
                this.setCurrentItemOrArmor(0, new ItemStack(Item.swordDiamond));
                this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(36.0);
                this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute(Math.floor(12.0 + (progress-2)*3) - (isHostile ? 0 : 4));
                this.equipmentDropChances[0] = 0f;
                // 12.0 -> 15.0
            }

            if(isHostile && NightmareUtils.getIsBloodMoon(this.worldObj)){
                if(rand.nextInt(10) == 0){
                    this.setCurrentItemOrArmor(0, new ItemStack(BTWItems.steelSword));
                    this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute(Math.floor(12.0 + (progress-2)*3) - (isHostile ? 0 : 4));
                    this.equipmentDropChances[0] = 0;
                }
                float streakModifier = 0.0f;
                for (int i = 1; i <= 4; i++) {
                    if(this.getCurrentItemOrArmor(i) == null){ // starts at index 1, index 0 is held item
                        if(rand.nextFloat() < (0.1f + progress*0.05) + streakModifier){
                            // 0.04f -> 0.06f -> 0.08f -> 0.10f                                    
                            streakModifier += 0.08f;
                            List<ItemStack> advancedArmorList = getAdvancedArmor();
                            this.setCurrentItemOrArmor(i, advancedArmorList.get(i-1));
                            this.equipmentDropChances[i] = 0;
                        }
                    }
                }
            }
        }
    }
    @Inject(method = "applyEntityAttributes", at = @At("TAIL"))
    private void addAdditionalAttributes(CallbackInfo ci){
        if (this.worldObj != null) {
            int progress = 0;
            try {
                progress = NightmareUtils.getWorldProgress(this.worldObj);
            } catch (RuntimeException ignored) {}

            if(NightmareUtils.getIsBloodMoon(this.worldObj)){
                this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.27d);
                this.getEntityAttribute(SharedMonsterAttributes.followRange).setAttribute(64.0d);
                this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute((int)(3.0d + (progress > 0 ? 1 : 0)) * 1.25);
                this.getEntityAttribute(BTWAttributes.armor).setAttribute(2.0d + progress * 1.5);
                this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute((20 + progress * 6) * 1.25);
                // 25 -> 32.5 -> 40 -> 47.5
                this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setAttribute((double) progress / 5);
            } else {
                this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute(3.0d + (progress > 0 ? 1 : 0));
                this.getEntityAttribute(BTWAttributes.armor).setAttribute(2.0d + progress);
                this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(20 + progress * 6);
                // 20 -> 26 -> 32 -> 38
                this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setAttribute((double) progress / 10);
            }
        }
    }

    @Inject(method = "addRandomArmor", // changes iron sword damage specifically
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/src/EntityZombie;setCurrentItemOrArmor(ILnet/minecraft/src/ItemStack;)V",
                    ordinal = 0))
    private void setDamageIfIronSword(CallbackInfo ci){
        if (this.worldObj!= null) {
            if(NightmareUtils.getIsBloodMoon(this.worldObj)){
                this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute(Math.floor(4.0 + (NightmareUtils.getWorldProgress(this.worldObj)) * 2.5));
                // 4.0 -> 6.0 -> 9.0 -> 11.0
            } else {
                this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute(Math.floor(4.0 + (NightmareUtils.getWorldProgress(this.worldObj)) * 1.75));
                // 4.0 -> 5.0 -> 7.0 -> 9.0
            }
        }
    }
    @Inject(method = "addRandomArmor", // summons crystalhead zombie on a 1% chance
            at = @At("TAIL"))
    private void chanceToSpawnCrystalHead(CallbackInfo ci){
        if (this.worldObj != null) {
            int chance = 60;
            if(this.worldObj.getDifficulty() == Difficulties.HOSTILE){
                chance = 32;
            }
            if(NightmareUtils.getIsBloodMoon(this.worldObj)){chance /= 2;}

            if(NightmareUtils.getWorldProgress(this.worldObj) >= 2 && rand.nextInt(chance) == 0){
                summonCrystalHeadAtPos();
            }
        }
    }

    @Inject(method = "addRandomArmor",
            at = @At("TAIL"))
    private void chanceToSpawnShadowZombie(CallbackInfo ci){
        if (this.worldObj != null) {
            if(NightmareUtils.getWorldProgress(this.worldObj)>=1 && rand.nextInt(4) == 0){
                summonShadowZombieAtPos((EntityZombie)(Object)this);
            }
        }
    }


    @ModifyConstant(method = "addRandomArmor",constant = @Constant(floatValue = 0.05F))
    private float modifyChanceToHaveIronTool(float constant){
        if (this.worldObj != null) {
            if((EntityZombie)(Object)this instanceof EntityShadowZombie){
                return 0;
            }
            if(NightmareUtils.getWorldProgress(this.worldObj)==3){return 0.3f;}
            else {
                int bloodMoonToolBonus = NightmareUtils.getIsBloodMoon(this.worldObj) ? 5 : 1;
                return (float)(0.05F + (NightmareUtils.getWorldProgress(this.worldObj)* (0.03 * bloodMoonToolBonus)));
            }
        }
        return constant;
        // 0.05f -> 0.08f -> 0.11f -> 0.30f
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void addLungeAI(World par1World, CallbackInfo ci){
        this.targetTasks.addTask(2, new EntityAILunge(this, true));
    }

    @ModifyConstant(method = "addRandomArmor", constant = @Constant(floatValue = 0.99F))
    private float makeSpecialZombiesNotDropHeldItem(float constant){
        if(this.getHeldItem() != null){
            if(this.getHeldItem().equals(new ItemStack(Item.swordDiamond))
                    || this.getHeldItem().equals(new ItemStack(Item.swordWood))
                    || this.getHeldItem().equals(new ItemStack(Item.axeGold))
                    || this.getHeldItem().equals(new ItemStack(BTWItems.boneClub))) {
                return 0;
            }
        }
        return 0.99f;
    }

    @Inject(method = "onUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityMob;onUpdate()V"))
    private void deleteEndCrystalIfZombieDied(CallbackInfo ci){
        if (this.worldObj != null) {
            if(isCrystalHead(this) && this.getHealth() < 1.0f){
                this.riddenByEntity.setDead();
            }
        }
    }

    @Unique
    private void summonCrystalHeadAtPos(){
        Entity crystalhead = new EntityZombie(this.worldObj);
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
        ((EntityZombieMixin)crystalhead).doNotDropItems = true;
        this.setDead();
    }

    @Unique
    private void summonShadowZombieAtPos(EntityZombie zombie){
        Entity shadowZombie = new EntityShadowZombie(this.worldObj);
        shadowZombie.setLocationAndAngles(zombie.posX, zombie.posY, zombie.posZ, zombie.rotationYaw, zombie.rotationPitch);

        shadowZombie.setCurrentItemOrArmor(1, null);
        shadowZombie.setCurrentItemOrArmor(2, null);
        shadowZombie.setCurrentItemOrArmor(3, null);
        shadowZombie.setCurrentItemOrArmor(4, null);

        zombie.worldObj.spawnEntityInWorld(shadowZombie);

        zombie.setDead();
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
    private static @NotNull List<ItemStack> getAdvancedArmor() {
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

    @Unique private boolean isWearingAnything(EntityMob mob){
        for(int i = 1; i < 5; i++){
            if(mob.getCurrentItemOrArmor(i) != null){
                return false;
            }
        }
        return true;
    }
}
