package com.itlesports.nightmaremode.mixin;

import btw.community.nightmaremode.NightmareMode;
import btw.entity.InfiniteArrowEntity;
import btw.entity.RottenArrowEntity;
import btw.entity.attribute.BTWAttributes;
import btw.item.BTWItems;
import btw.world.util.WorldUtils;
import btw.world.util.difficulty.Difficulties;
import com.itlesports.nightmaremode.NightmareUtils;
import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mixin(EntitySkeleton.class)
public abstract class EntitySkeletonMixin extends EntityMob implements EntityAccessor{
    @Unique
    private static final List<Integer> droppableArmor = new ArrayList<>(Arrays.asList(
            Item.swordIron.itemID,
            Item.shovelIron.itemID,
            Item.plateIron.itemID,
            Item.bootsIron.itemID,
            Item.legsIron.itemID,
            Item.helmetIron.itemID
    ));

    @Unique private static boolean areMobsEvolved = NightmareMode.evolvedMobs;

    @Shadow public abstract void setSkeletonType(int par1);
    @Shadow public abstract void setCurrentItemOrArmor(int par1, ItemStack par2ItemStack);
    @Shadow public abstract int getSkeletonType();public EntitySkeletonMixin(World par1World) {
        super(par1World);
    }
    @Unique int jumpCooldown = 0;


    @Override
    public float knockbackMagnitude() {
        return this.isWeighted() ? 0.2f : super.knockbackMagnitude();
    }
    @ModifyConstant(method = "setSkeletonType", constant = @Constant(floatValue = 2.34f))
    private float shortWitherSkeletons(float constant){
        if (this.worldObj != null) {
            return this.worldObj.getDifficulty() == Difficulties.HOSTILE ? 1.8f : constant;
        }
        return constant;
    }
    @Override
    public boolean isImmuneToHeadCrabDamage() {
        return true;
    }

    @Override
    public Entity getHeadCrabSharedAttackTarget() {
        return this.getAttackTarget();
    }

    @Inject(method = "dropFewItems", at = @At("HEAD"))
    private void allowBloodOrbDrops(boolean bKilledByPlayer, int iLootingModifier, CallbackInfo ci){
        if (bKilledByPlayer) {
            int bloodOrbID = NightmareUtils.getIsBloodMoon() ? NMItems.bloodOrb.itemID : 0;
            if (bloodOrbID > 0) {
                int var4 = this.rand.nextInt(3);
                // 0 - 2
                switch(this.getSkeletonType()){
                    case 1, 4:
                        var4 += 2;
                        break;
                    case 2, 3:
                        var4 += 1;
                        break;
                }
                if (iLootingModifier > 0) {
                    var4 += this.rand.nextInt(iLootingModifier + 1);
                }
                for (int var5 = 0; var5 < var4; ++var5) {
                    this.dropItem(bloodOrbID, 1);
                }
            }
            if (NightmareUtils.getIsMobEclipsed(this)) {
                for(int i = 0; i < (iLootingModifier * 2) + 1; i++) {
                    if (this.rand.nextInt(8) == 0) {
                        this.dropItem(NMItems.darksunFragment.itemID, 1);
                        if (this.rand.nextBoolean()) {
                            break;
                        }
                    }
                }

                int itemID = NMItems.witheredBone.itemID;

                int var4 = this.rand.nextInt(3);
                if (iLootingModifier > 0) {
                    var4 += this.rand.nextInt(iLootingModifier + 1);
                }
                for (int var5 = 0; var5 < var4; ++var5) {
                    if(this.rand.nextInt(3) == 0) continue;
                    this.dropItem(itemID, 1);
                }
            }
        }
    }

    @Inject(method = "<init>",
            at = @At(value = "TAIL"))
    private void removeHideFromSun(CallbackInfo ci) {
        this.tasks.removeAllTasksOfClass(EntityAIFleeSun.class);
        this.tasks.removeAllTasksOfClass(EntityAIRestrictSun.class);
    }
    
    @ModifyConstant(method = "applyEntityAttributes", constant = @Constant(doubleValue = 0.25))
    private double increaseMoveSpeed(double constant){
        if (this.worldObj != null) {
            if (this.worldObj.getDifficulty() == Difficulties.HOSTILE) {
                if (this.getSkeletonType() == 1){
                    return 0.3  * (1 + (NightmareUtils.getNiteMultiplier() - 1) / 10);
                }
                if (this.worldObj != null) {
                    return (0.29 + NightmareUtils.getWorldProgress(this.worldObj) * 0.005) * (1 + (NightmareUtils.getNiteMultiplier() - 1) / 10);
                }
                // 0.29 -> 0.295 -> 0.30 -> 0.305
            }
        }
        return 0.26;
    }
    @Inject(method = "<init>", at = @At("TAIL"))
    private void manageEclipseChance(World world, CallbackInfo ci){
        NightmareUtils.manageEclipseChance(this,8);
    }

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityAIWatchClosest;<init>(Lnet/minecraft/src/EntityLiving;Ljava/lang/Class;F)V"),index = 2)
    private float modifyDetectionRadius(float f){
        if (this.worldObj != null) {
            if (this.worldObj.getDifficulty() == Difficulties.HOSTILE) {
                return (float) (24f * (1 + (NightmareUtils.getNiteMultiplier() - 1) / 10));
            }
        }
        return f;
    }
    @Inject(method = "applyEntityAttributes", at = @At("TAIL"))
    private void increaseHealth(CallbackInfo ci){
        if (this.worldObj != null) {
            int progress = NightmareUtils.getWorldProgress(this.worldObj);
            float bloodMoonModifier = NightmareUtils.getIsBloodMoon() ? 1.4f : 1;
            boolean isBloodMoon = bloodMoonModifier > 1;
            boolean isEclipse = NightmareUtils.getIsMobEclipsed(this);
            boolean isHostile = this.worldObj.getDifficulty() == Difficulties.HOSTILE;

            if (isHostile) {
                if(isBloodMoon || isEclipse){
                    this.getEntityAttribute(SharedMonsterAttributes.followRange).setAttribute(24d);
                } else {
                    this.getEntityAttribute(SharedMonsterAttributes.followRange).setAttribute(MathHelper.floor_double(20.0d + progress));
                    // 20 -> 21 -> 22 -> 23
                }
            }

            if(this.getSkeletonType() == 1){
                this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(((isHostile ? 24 : 20) + progress * (isHostile ? 4 : 2) + (isEclipse ? 15 : 0 )) * NightmareUtils.getNiteMultiplier());
                // 24.0 -> 28.0 -> 32.0 -> 36.0
            } else{
                this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(MathHelper.floor_double(((16.0 + progress * (isHostile ? 7 : 3)) * bloodMoonModifier + (isEclipse ? 15 : 0) * NightmareUtils.getNiteMultiplier())));
                // 16.0 -> 23.0 -> 30.0 -> 37.0
            }
            if(this.getSkeletonType() == 4){
                this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(((isHostile ? 24 : 20) + progress * (isHostile ? (isEclipse ? 8 : 6) : 2)) * NightmareUtils.getNiteMultiplier());
                // 24.0 -> 30.0 -> 36.0 -> 40.0
            }

            this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute((3.0 * (progress + 1) + (isEclipse ? 1 : 0)) * NightmareUtils.getNiteMultiplier());
            // 3.0 -> 4.0 -> 5.0 -> 6.0
            // 4.5 -> 6.0 -> 7.5 -> 9.0
        }
    }

    @Inject(method = "preInitCreature", at = @At("TAIL"))
    private void manageBloodMoonWitherSkellySpawning(CallbackInfo ci){
        if(this.worldObj != null){
            if((NightmareUtils.getIsBloodMoon() || areMobsEvolved) && this.rand.nextInt(16) == 0 && this.getSkeletonType() == 0){
                this.setSkeletonType(1);
            } else if (this.rand.nextInt(NightmareUtils.divByNiteMultiplier(10, 4)) == 0 && (WorldUtils.gameProgressHasWitherBeenSummonedServerOnly() || areMobsEvolved) && this.getSkeletonType() == 0){
                this.setSkeletonType(1);
            }
        }
    }

    @Redirect(method = "onSpawnWithEgg", at = @At(value = "INVOKE", target = "Ljava/lang/Boolean;booleanValue()Z"))
    private boolean alwaysWitherSkelliesUnderground(Boolean instance){
        return true;
    }
    @ModifyArg(method = "onSpawnWithEgg", at = @At(value = "INVOKE", target = "Ljava/util/Random;nextInt(I)I"))
    private int chanceToSpawnAsWitherSkelly(int bound){
        if(this.worldObj != null){
            return 12 - (NightmareUtils.getWorldProgress(this.worldObj) * 3);
        }
        return bound;
    }

    @Redirect(method = "onLivingUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntitySkeleton;checkForCatchFireInSun()V"))
    private void doNotCatchFireInSun(EntitySkeleton instance){}

    @Inject(method = "onLivingUpdate", at = @At("TAIL"))
    private void manageJumpShotAndSpiderDismount(CallbackInfo ci){
        if (this.worldObj != null ) {
            if (this.getHeldItem() != null && this.getHeldItem().itemID == Item.bow.itemID && this.worldObj.getDifficulty() == Difficulties.HOSTILE) {
                EntityPlayer targetPlayer = this.worldObj.getClosestVulnerablePlayer(this.posX,this.posY,this.posZ,12);
                this.jumpCooldown ++;
                if(targetPlayer != null && (canSeeIfJumped(this,targetPlayer) && cannotSeeNormally(this, targetPlayer)) && !this.isAirBorne && this.jumpCooldown >= 60){
                    this.motionY = 0.45;
                    this.jumpCooldown = 0;
                }
            }
            if(this.ridingEntity instanceof EntitySpider && this.worldObj.isDaytime() && this.ticksExisted % 20 == 0 && !this.worldObj.isRemote){
                if(this.hasAttackTarget()){
                    ((EntitySpider) this.ridingEntity).entityToAttack = this.getAttackTarget();
                }
            }
        }
    }

    @Inject(method = "attackEntityWithRangedAttack", at = @At("TAIL"))
    private void manageBloodMoonAttack(EntityLivingBase target, float fDamageModifier, CallbackInfo ci){
        if(this.worldObj != null){
            if (NightmareUtils.getIsBloodMoon()){
                if (rand.nextBoolean()) {
                    this.shootArrowAtEntity(target);
                    if(rand.nextBoolean()){
                        this.shootArrowAtEntity(target);
                    }
                }
            }
        }
    }

    @Unique private boolean canSeeIfJumped(EntityLiving skelly, Entity targetPlayer){
        return skelly.worldObj.rayTraceBlocks_do_do(skelly.worldObj.getWorldVec3Pool().getVecFromPool(skelly.posX, skelly.posY + (double)skelly.getEyeHeight()+1, skelly.posZ), skelly.worldObj.getWorldVec3Pool().getVecFromPool(targetPlayer.posX, targetPlayer.posY + (double)targetPlayer.getEyeHeight(), targetPlayer.posZ), false, true) == null;
    }
    @Unique private boolean cannotSeeNormally(EntityLiving skelly, Entity targetPlayer){
        return skelly.worldObj.rayTraceBlocks_do_do(skelly.worldObj.getWorldVec3Pool().getVecFromPool(skelly.posX, skelly.posY + (double) skelly.getEyeHeight(), skelly.posZ), skelly.worldObj.getWorldVec3Pool().getVecFromPool(targetPlayer.posX, targetPlayer.posY + (double) targetPlayer.getEyeHeight(), targetPlayer.posZ), false, true) != null;
    }

    @Inject(method = "addRandomArmor", at = @At("TAIL"))
    private void manageSkeletonVariants(CallbackInfo ci){
        if (this.worldObj != null) {
            this.isImmuneToFire = true;
            int progress = NightmareUtils.getWorldProgress(this.worldObj);
            boolean isHostile = this.worldObj.getDifficulty() == Difficulties.HOSTILE;
            double bloodMoonModifier = NightmareUtils.getIsBloodMoon() ? 1.5 : 1;
            boolean isEclipse = NightmareUtils.getIsEclipse();


            if (!isEclipse) {
                // for non-solar eclipse. randomly picks which variant to spawn. chances are different from each
                if ((progress >= 2 || areMobsEvolved) && rand.nextFloat() < (0.13 + (Math.abs((progress - 2)) * (isHostile ? 0.07 : 0.03))) * bloodMoonModifier * NightmareUtils.getNiteMultiplier()) {
                    // 13% -> 20%
                    // 19.5% -> 30%
                    this.setSkeletonType(4); // ender skeleton

                    this.setCurrentItemOrArmor(1, setItemColor(new ItemStack(BTWItems.woolBoots), 1052688)); // black
                    this.setCurrentItemOrArmor(2, setItemColor(new ItemStack(BTWItems.woolLeggings), 1052688)); // black
                    this.setCurrentItemOrArmor(3, setItemColor(new ItemStack(BTWItems.woolChest), 1052688)); // black
                    this.equipmentDropChances[1] = 0f;
                    this.equipmentDropChances[2] = 0f;
                    this.equipmentDropChances[3] = 0f;

                    this.getEntityAttribute(BTWAttributes.armor).setAttribute(8.0d);

                } else if ((progress >= 1 || areMobsEvolved) && rand.nextFloat() < ((isHostile ? 0.09 : 0.03) + (Math.abs((progress - 1)) * 0.02)) * bloodMoonModifier * NightmareUtils.getNiteMultiplier() && this.dimension != -1) {
                    // 9% -> 11% -> 13%
                    // 13.5% -> 16.5% -> 19.5%
                    this.setSkeletonType(3); // fire skeleton
                    this.setCurrentItemOrArmor(1, null);
                    this.setCurrentItemOrArmor(2, null);
                    this.setCurrentItemOrArmor(3, null);
                    this.setCurrentItemOrArmor(4, null);
                    this.setFire(10000);
                    this.getEntityAttribute(BTWAttributes.armor).setAttribute(4.0d);

                } else if ((progress <= 3 || areMobsEvolved) && rand.nextFloat() < (0.02 + (progress * 0.02)) * bloodMoonModifier * NightmareUtils.getNiteMultiplier()) {
                    // 2% -> 4% -> 6% -> 8%
                    // 3% -> 6% -> 9% -> 12%
                    this.setSkeletonType(2); // ice skeleton
                    ItemStack var1 = new ItemStack(BTWItems.woolHelmet, 1);
                    this.setCurrentItemOrArmor(4, setItemColor(var1, 13260));
                    this.equipmentDropChances[4] = 0f;
                }
            }
            else{
                // picks a random variant including default & wither. each variant now has an equal 20% chance of spawning
                switch (rand.nextInt(5)){
                    case 0:
                        this.setSkeletonType(4); // ender skeleton

                        this.setCurrentItemOrArmor(1, setItemColor(new ItemStack(BTWItems.woolBoots), 1052688)); // black
                        this.setCurrentItemOrArmor(2, setItemColor(new ItemStack(BTWItems.woolLeggings), 1052688)); // black
                        this.setCurrentItemOrArmor(3, setItemColor(new ItemStack(BTWItems.woolChest), 1052688)); // black
                        this.equipmentDropChances[1] = 0f;
                        this.equipmentDropChances[2] = 0f;
                        this.equipmentDropChances[3] = 0f;

                        this.getEntityAttribute(BTWAttributes.armor).setAttribute(8.0d);
                        break;
                    case 1:
                        this.setSkeletonType(3); // fire skeleton
                        this.setCurrentItemOrArmor(1, null);
                        this.setCurrentItemOrArmor(2, null);
                        this.setCurrentItemOrArmor(3, null);
                        this.setCurrentItemOrArmor(4, null);
                        this.setFire(10000);
                        this.getEntityAttribute(BTWAttributes.armor).setAttribute(4.0d);
                        break;
                    case 2:
                        this.setSkeletonType(2); // ice skeleton
                        ItemStack var1 = new ItemStack(BTWItems.woolHelmet, 1);
                        this.setCurrentItemOrArmor(4, setItemColor(var1, 13260));
                        this.equipmentDropChances[4] = 0f;
                        break;
                    case 3:
                        this.setSkeletonType(1);
                        break;
                    case 4:
                        this.setSkeletonType(0);
                        break;
                }
            }
        }
        // overall chances to be a variant (non eclipse): 2% -> 13% -> 30% -> 41%

        // skeleton types:
        // 0: regular
        // 1: wither
        // 2: ice
        // 3: fire
        // 4: ender
    }

    @Inject(method = "onLivingUpdate", at = @At("TAIL"))
    private void manageVariantEffects(CallbackInfo ci){
        if(this.getSkeletonType() == 3){ // fireskeleton
            if (!this.isInWater()) {
                this.setFire(2000);
            }
        }
    }

    @Inject(method = "attackEntityWithRangedAttack",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/src/World;spawnEntityInWorld(Lnet/minecraft/src/Entity;)Z", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    private void killArrowEntityForEnderSkeleton(EntityLivingBase target, float fDamageModifier, CallbackInfo ci, EntityArrow arrow, int iPowerLevel, int iPunchLevel, int iFlameLevel){
        if (this.worldObj != null) {
            if(this.getSkeletonType()==3){arrow.setDead();}
        }
    }
    @ModifyConstant(method = "attackEntityWithRangedAttack", constant = @Constant(floatValue = 12.0f))
    private float reduceArrowSpread(float constant){
        if (this.worldObj != null) {
            return NightmareUtils.divByNiteMultiplier((int) (8.0f - NightmareUtils.getWorldProgress(this.worldObj)*2), 2);
        }
        return constant;
        // 8.0 -> 6.0 -> 4.0 -> 2.0
    }

    @Inject(method = "attackEntityFrom", at = @At(value = "RETURN"), cancellable = true)
    private void manageFallDamageImmunity(DamageSource damageSource, float damage, CallbackInfoReturnable<Boolean> cir){
        if(!this.isImmuneToFire()){
            this.isImmuneToFire = true;
        }
        if (this.getSkeletonType() == 1 && this.dimension == 1 && damageSource == DamageSource.fall){
            cir.setReturnValue(false); // refers to wither skeletons spawned by the ender dragon
        }
    }


    @Inject(method = "attackEntityWithRangedAttack",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/src/EntitySkeleton;playSound(Ljava/lang/String;FF)V"))
    private void determineWhatProjectileToShoot(EntityLivingBase target, float fDamageModifier, CallbackInfo ci){
        if (this.worldObj != null) {
            if(this.getSkeletonType()==3){
                if (this.worldObj.getDifficulty() == Difficulties.HOSTILE) {
                    for(int i = -2; i<=2; i+=2) {
                        double var3 = target.posX - this.posX + i;
                        double var5 = target.boundingBox.minY + (double) (target.height / 2.0F) - (this.posY + (double) (this.height / 2.0F));
                        double var7 = target.posZ - this.posZ + i;

                        EntitySmallFireball var11 = new EntitySmallFireball(this.worldObj, this, var3, var5, var7);
                        this.worldObj.playAuxSFXAtEntity(null, 1009, (int)this.posX, (int)this.posY, (int)this.posZ, 0);
                        var11.posY = this.posY + (double) (this.height / 2.0f) + 0.5;
                        this.worldObj.spawnEntityInWorld(var11);
                    }
                } else{
                    double var3 = target.posX - this.posX;
                    double var5 = target.boundingBox.minY + (double) (target.height / 2.0F) - (this.posY + (double) (this.height / 2.0F));
                    double var7 = target.posZ - this.posZ;

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
            if(this.worldObj.getDifficulty() == Difficulties.HOSTILE && rand.nextInt(NightmareUtils.divByNiteMultiplier(60, 20)) < 3+(NightmareUtils.getWorldProgress(this.worldObj)*2) && this.getSkeletonType()!=4 && this.getSkeletonType() != 2){
                arrow.setFire(400);
                arrow.playSound("fire.fire", 1.0f, this.rand.nextFloat() * 0.4f + 0.8f);
            } else{
                arrow.setDamage(MathHelper.floor_double((2.0 + (NightmareUtils.getWorldProgress(this.worldObj) * 2 - (this.worldObj.getDifficulty() == Difficulties.HOSTILE ? 0 : 1)))) * NightmareUtils.getNiteMultiplier());
                // 4 -> 6 -> 8 -> 10
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
        if (this.worldObj != null && this.worldObj.getDifficulty() == Difficulties.HOSTILE) {

            return switch (NightmareUtils.getWorldProgress(this.worldObj)) {
                case 0 -> NightmareUtils.divByNiteMultiplier(60, 20);
                case 1 -> NightmareUtils.divByNiteMultiplier(50, 20);
                case 2 -> NightmareUtils.divByNiteMultiplier(45 + rand.nextInt(5), 20);
                case 3 -> NightmareUtils.divByNiteMultiplier(40 + rand.nextInt(5), 20);
                default -> constant;
            };
        }
        return constant;
    }
    @ModifyConstant(method = "<init>", constant = @Constant(floatValue = 15.0f))
    private float modifyAttackRange(float constant){
        if (this.worldObj != null && this.worldObj.getDifficulty() == Difficulties.HOSTILE) {
            return (float) ((18.0f + NightmareUtils.getWorldProgress(this.worldObj) * 3) * Math.min(NightmareUtils.getNiteMultiplier(), 1.3f));
        }
        return constant;
        // 18 -> 21 -> 24 -> 27
    }

    @Unique
    private void shootArrowAtEntity(EntityLivingBase target){
        EntityArrow arrow;
        if (this.worldObj.provider.dimensionId == -1) {
            arrow = new InfiniteArrowEntity(this.worldObj, this, target, 1.6F, 16.0F);
        } else {
            arrow = new RottenArrowEntity(this.worldObj, this, target, 1.6F, 16.0F);
        }

        int iPowerLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, this.getHeldItem());
        if (iPowerLevel > 0) {
            arrow.setDamage(arrow.getDamage() + (double)iPowerLevel * (double)0.5F + (double)0.5F);
        }

        int iPunchLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, this.getHeldItem());
        if (iPunchLevel > 0) {
            arrow.setKnockbackStrength(iPunchLevel);
        }

        int iFlameLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, this.getHeldItem());
        if (iFlameLevel > 0 || this.getSkeletonType() == 1 || this.isBurning() && this.rand.nextFloat() < 0.3F) {
            arrow.setFire(100);
        }

        this.playSound("random.bow", 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
        this.worldObj.spawnEntityInWorld(arrow);
    }
}
