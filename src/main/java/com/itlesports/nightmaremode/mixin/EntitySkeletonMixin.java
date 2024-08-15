package com.itlesports.nightmaremode.mixin;

import btw.item.BTWItems;
import btw.world.util.difficulty.Difficulty;
import com.itlesports.nightmaremode.NightmareUtils;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(EntitySkeleton.class)
public abstract class EntitySkeletonMixin extends EntityMob {
    @Shadow public abstract void setSkeletonType(int par1);
    @Shadow public abstract void setCurrentItemOrArmor(int par1, ItemStack par2ItemStack);
    @Shadow public abstract int getSkeletonType();public EntitySkeletonMixin(World par1World) {
        super(par1World);
    }


    @ModifyConstant(method = "setSkeletonType", constant = @Constant(floatValue = 2.34f))
    private float shortWitherSkeletons(float constant){
        return 1.8f;
    }
    // redirecting hostile call in constructor
    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lbtw/world/util/difficulty/Difficulty;isHostile()Z"),remap = false)
    private boolean returnTrue(Difficulty instance){return true;}
    @Redirect(method = "onSpawnWithEgg", at = @At(value = "INVOKE", target = "Lbtw/world/util/difficulty/Difficulty;isHostile()Z"))
    private boolean returnTrue1(Difficulty instance){return true;}
    // done redirecting

    @ModifyArg(method = "onSpawnWithEgg", at = @At(value = "INVOKE", target = "Ljava/util/Random;nextInt(I)I"))
    private int modifyChanceToSpawnWitherSkelly(int bound){
        return 4; // from 1/8 to 1/4 chance
    }
    
    @Inject(method = "<init>",
            at = @At(value = "TAIL"))
    private void removeHideFromSun(CallbackInfo ci) {
        this.tasks.removeAllTasksOfClass(EntityAIFleeSun.class);
        this.tasks.removeAllTasksOfClass(EntityAIRestrictSun.class);
    }

    @ModifyConstant(method = "applyEntityAttributes", constant = @Constant(doubleValue = 0.25))
    private double increaseMoveSpeed(double constant){
        if (this.getSkeletonType() == 1){
            return 0.35;
        }
        if (this.worldObj != null) {
            return 0.3+NightmareUtils.getGameProgressMobsLevel(this.worldObj)*0.015;
        } else return 0.3;
        // 0.3 -> 0.315 -> 0.33 -> 0.345
    }

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityAIWatchClosest;<init>(Lnet/minecraft/src/EntityLiving;Ljava/lang/Class;F)V"),index = 2)
    private float modifyDetectionRadius(float f){
        return 24f;
    }
    @Inject(method = "applyEntityAttributes", at = @At("TAIL"))
    private void increaseHealth(CallbackInfo ci){
        if (this.worldObj != null) {
            this.getEntityAttribute(SharedMonsterAttributes.followRange).setAttribute(24.0);
            if (this.getSkeletonType()!=1) {
                this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(16.0 + NightmareUtils.getGameProgressMobsLevel(this.worldObj) * 6);
                // 16.0 -> 22.0 -> 28.0 -> 34.0
            } else {
                this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(24 + NightmareUtils.getGameProgressMobsLevel(this.worldObj) * 4);
                // 24.0 -> 28.0 -> 32.0 -> 36.0
            }

            this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute(3.0 * (NightmareUtils.getGameProgressMobsLevel(this.worldObj)+1));
            // not really important this is just melee damage
        }
    }

    @Redirect(method = "onLivingUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntitySkeleton;checkForCatchFireInSun()V"))
    private void doNothing(EntitySkeleton instance){}

    @Inject(method = "addRandomArmor", at = @At("TAIL"))
    private void manageSkeletonVariants(CallbackInfo ci){
        if (this.worldObj != null) {
            this.addPotionEffect(new PotionEffect(Potion.fireResistance.id, 1000000,0));
            if (NightmareUtils.getGameProgressMobsLevel(this.worldObj) >= 2 && rand.nextFloat() < 0.13 + ((NightmareUtils.getGameProgressMobsLevel(this.worldObj)-2)*0.07)){
                // 13% -> 20%
                this.setSkeletonType(4); // ender skeleton
                this.clearActivePotions();

                ItemStack var1 = new ItemStack(Item.skull,1,1);
                ItemStack var2 = new ItemStack(Item.bow,1);
                this.setCurrentItemOrArmor(4, var1);
                this.setCurrentItemOrArmor(0, var2);
                this.setCurrentItemOrArmor(1, setItemColor(new ItemStack(BTWItems.woolBoots), 1052688)); // black
                this.setCurrentItemOrArmor(2, setItemColor(new ItemStack(BTWItems.woolLeggings), 1052688)); // black
                this.setCurrentItemOrArmor(3, setItemColor(new ItemStack(BTWItems.woolChest), 1052688)); // black

            } else if (NightmareUtils.getGameProgressMobsLevel(this.worldObj) >= 1 && rand.nextFloat()<0.09 + ((NightmareUtils.getGameProgressMobsLevel(this.worldObj)-1)*0.02) && this.dimension != -1) {
                // 9% -> 11% -> 13%
                this.setSkeletonType(3); // fire skeleton

                Entity magmaCube = new EntityMagmaCube(this.worldObj);
                magmaCube.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
                this.worldObj.spawnEntityInWorld(magmaCube);
                magmaCube.mountEntity(this);
                this.setFire(1000000);

            } else if(NightmareUtils.getGameProgressMobsLevel(this.worldObj) <= 3 && rand.nextFloat() < 0.02 + (NightmareUtils.getGameProgressMobsLevel(this.worldObj)*0.02)) {
                // 6% -> 8% -> 10% -> 12%
                this.setSkeletonType(2); // ice skeleton
                ItemStack var1 = new ItemStack(BTWItems.woolHelmet, 1);
                this.setCurrentItemOrArmor(4, setItemColor(var1, 13260));
            }
        }
        // overall chances to be a variant: 2% -> 13% -> 30% -> 41%
    }

    @Inject(method = "attackEntityWithRangedAttack",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/src/World;spawnEntityInWorld(Lnet/minecraft/src/Entity;)Z", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    private void deleteArrowEntity(EntityLivingBase target, float fDamageModifier, CallbackInfo ci, EntityArrow arrow, int iPowerLevel, int iPunchLevel, int iFlameLevel){
        if (this.worldObj != null) {
            if(this.getSkeletonType()==3){arrow.setDead();}
        }
    }
    @ModifyConstant(method = "attackEntityWithRangedAttack", constant = @Constant(floatValue = 12.0f))
    private float reduceArrowSpread(float constant){
        if (this.worldObj != null) {
            return 8.0f - NightmareUtils.getGameProgressMobsLevel(this.worldObj)*2;
        } else return 12.0f;
        // 8.0 -> 6.0 -> 4.0 -> 2.0
    }

    @Inject(method = "attackEntityFrom", at = @At(value = "RETURN"), cancellable = true)
    private void manageFallDamageImmunity(DamageSource damageSource, float damage, CallbackInfoReturnable<Boolean> cir){
        if (this.getSkeletonType() == 1 && this.dimension == 1 && damageSource == DamageSource.fall){
            cir.setReturnValue(false);
        }
    }


    @Inject(method = "attackEntityWithRangedAttack",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/src/EntitySkeleton;playSound(Ljava/lang/String;FF)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void determineWhatProjectileToShoot(EntityLivingBase target, float fDamageModifier, CallbackInfo ci, EntityArrow arrow){
        if (this.worldObj != null) {
            if(this.getSkeletonType()==3){
                for(int i = -2; i<=2; i+=2) {
                    double var3 = target.posX - this.posX + i;
                    double var5 = target.boundingBox.minY + (double) (target.height / 2.0F) - (this.posY + (double) (this.height / 2.0F));
                    double var7 = target.posZ - this.posZ + i;

                    EntitySmallFireball var11 = new EntitySmallFireball(this.worldObj, this, var3, var5, var7);
                    this.worldObj.playAuxSFXAtEntity(null, 1009, (int)this.posX, (int)this.posY, (int)this.posZ, 0);
                    var11.posY = this.posY + (double) (this.height / 2.0f) + 0.5;
                    this.worldObj.spawnEntityInWorld(var11);
                }
            }
        }
    }



    @Inject(method = "attackEntityWithRangedAttack",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/src/World;spawnEntityInWorld(Lnet/minecraft/src/Entity;)Z"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void chanceToSetArrowOnFire(EntityLivingBase target, float fDamageModifier, CallbackInfo ci, EntityArrow arrow, int iPowerLevel, int iPunchLevel, int iFlameLevel){
        if (this.worldObj != null) {
            if(rand.nextInt(60) < 3+(NightmareUtils.getGameProgressMobsLevel(this.worldObj)*2) && this.getSkeletonType()!=4 && this.getSkeletonType() != 2){
                arrow.setFire(400);
                arrow.playSound("fire.fire", 1.0f, this.rand.nextFloat() * 0.4f + 0.8f);
            } else{
                arrow.setDamage(MathHelper.floor_double(3.0 + (NightmareUtils.getGameProgressMobsLevel(this.worldObj) * 2)));
                // 5 -> 7 -> 9 -> 11
            }
        }
    }


    @Unique
    private ItemStack setItemColor(ItemStack item, int color){
        NBTTagCompound var3 = item.getTagCompound();
        if (var3 == null) {
            var3 = new NBTTagCompound();
            item.setTagCompound(var3);
        }
        NBTTagCompound var4 = var3.getCompoundTag("display");
        if (!var3.hasKey("display")) {
            var3.setCompoundTag("display", var4);
        }

        var4.setInteger("color", color);
        item.setTagCompound(var3);
        return item;
    }
    @ModifyConstant(method = "<init>", constant = @Constant(intValue = 60))
    private int modifyAttackInterval(int constant){
        if (this.worldObj != null) {
            return switch (NightmareUtils.getGameProgressMobsLevel(this.worldObj)) {
                case 0 -> 60;
                case 1 -> 50 + rand.nextInt(5);
                case 2 -> 45 + rand.nextInt(10);
                case 3 -> 40 + rand.nextInt(15);
                default -> throw new IllegalStateException("Unexpected value: " + NightmareUtils.getGameProgressMobsLevel(this.worldObj));
            };
        } else return 60;
    }
    @ModifyConstant(method = "<init>", constant = @Constant(floatValue = 15.0f))
    private float modifyAttackRange(float constant){
        if (this.worldObj != null) {
            return 18.0f + NightmareUtils.getGameProgressMobsLevel(this.worldObj)*3;
        } else return 15.0f;
        // 18 -> 21 -> 24 -> 27
    }
}
