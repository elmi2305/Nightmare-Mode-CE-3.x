package com.itlesports.nightmaremode.mixin;

import btw.community.nightmaremode.NightmareMode;
import btw.entity.InfiniteArrowEntity;
import btw.entity.RottenArrowEntity;
import btw.entity.attribute.BTWAttributes;
import btw.entity.component.VariantComponent;
import btw.entity.mob.behavior.SkeletonArrowAttackBehavior;
import btw.item.BTWItems;
import btw.world.util.WorldUtils;
import btw.world.util.difficulty.DifficultyParam;
import com.itlesports.nightmaremode.AITasks.EntityAIChaseTargetSmart;
import com.itlesports.nightmaremode.AITasks.SkeletonChaseSmart;
import com.itlesports.nightmaremode.NMDifficultyParam;
import com.itlesports.nightmaremode.NMUtils;
import com.itlesports.nightmaremode.entity.EntityBurningArrow;
import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(EntitySkeleton.class)
public abstract class EntitySkeletonMixin extends EntityMob implements EntityAccessor{

    @Unique private boolean areMobsEvolved = NightmareMode.evolvedMobs;

    @Unique private SkeletonChaseSmart aiRangedAttackHorde = new SkeletonChaseSmart((EntitySkeleton)(Object)this, 1.0f, 60, 24f);
    @Unique private EntityAIChaseTargetSmart aiMeleeAttackHorde = new EntityAIChaseTargetSmart(this, 1.25f);

    @Shadow public abstract void setCurrentItemOrArmor(int par1, ItemStack par2ItemStack);


    @Shadow private EntityAIAttackOnCollide aiMeleeAttack;

    @Shadow private SkeletonArrowAttackBehavior aiRangedAttack;

    @Shadow public abstract boolean setSkeletonType(int id);

    @Shadow public abstract VariantComponent.EntityVariant getSkeletonType();

    public EntitySkeletonMixin(World par1World) {
        super(par1World);
    }
    @Unique int jumpCooldown = 0;



    @Override
    public float knockbackMagnitude() {
        return this.isWeighted() ? 0.2f : super.knockbackMagnitude();
    }
    @ModifyArg(method = "lambda$initComponents$0", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntitySkeleton;setSize(FF)V", ordinal = 0), index =  1)
    private float shortWitherSkeletons(float constant){
        if (this.worldObj != null) {
            return this.worldObj.getDifficultyParameter(NMDifficultyParam.ShouldMobsBeBuffed.class) ? 1.8f : constant;
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

    @Unique private boolean isValidForEventLoot = false;
    @Inject(method = "attackEntityFrom", at = @At("HEAD"))
    private void storeLastHit(DamageSource par1DamageSource, float par2, CallbackInfoReturnable<Boolean> cir){
        this.isValidForEventLoot = par1DamageSource.getEntity() instanceof EntityPlayer;
    }
    @Inject(method = "dropFewItems", at = @At("HEAD"))
    private void allowBloodOrbDrops(boolean bKilledByPlayer, int iLootingModifier, CallbackInfo ci){
        if (bKilledByPlayer && isValidForEventLoot) {
            int bloodOrbID = NMUtils.getIsBloodMoon() ? NMItems.bloodOrb.itemID : 0;
            if (bloodOrbID > 0) {
                int var4 = this.rand.nextInt(3);
                // 0 - 2
                switch(this.getSkeletonType().id()){
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
            if (NMUtils.getIsMobEclipsed(this)) {
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

    @Inject(method = "setCombatTask", at = @At("TAIL"))
    private void hordeModeCombatTask(CallbackInfo ci){
        if (NightmareMode.hordeMode) {
            this.tasks.removeTask(this.aiMeleeAttack);
            this.tasks.removeTask(this.aiRangedAttack);
            this.tasks.removeTask(this.aiMeleeAttackHorde);
            this.tasks.removeTask(this.aiRangedAttackHorde);
            ItemStack heldStack = this.getHeldItem();
            if (heldStack != null && heldStack.itemID == Item.bow.itemID) {
                this.tasks.addTask(4, this.aiRangedAttackHorde);
            } else {
                this.tasks.addTask(4, this.aiMeleeAttackHorde);
            }
        }
    }
    @ModifyConstant(method = "applyEntityAttributes", constant = @Constant(doubleValue = 0.25))
    private double increaseMoveSpeed(double constant){
        if (this.worldObj != null) {
            if (this.worldObj.getDifficultyParameter(NMDifficultyParam.ShouldMobsBeBuffed.class)) {
                if (this.getSkeletonType().id() == 1){
                    return 0.3  * (1 + (NMUtils.getNiteMultiplier() - 1) / 10);
                }
                if (this.worldObj != null) {
                    return (0.29 + NMUtils.getWorldProgress() * 0.005) * (1 + (NMUtils.getNiteMultiplier() - 1) / 10);
                }
                // 0.29 -> 0.295 -> 0.30 -> 0.305
            }
        }
        return 0.26;
    }
    @Inject(method = "<init>", at = @At("TAIL"))
    private void manageEclipseChance(World world, CallbackInfo ci){
        NMUtils.manageEclipseChance(this,8);
    }

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityAIWatchClosest;<init>(Lnet/minecraft/src/EntityLiving;Ljava/lang/Class;F)V"),index = 2)
    private float modifyDetectionRadius(float f){
        if (this.worldObj != null) {
            if (this.worldObj.getDifficultyParameter(NMDifficultyParam.ShouldMobsBeBuffed.class)) {
                return (float) (24f * (1 + (NMUtils.getNiteMultiplier() - 1) / 10));
            }
        }
        return f;
    }
    @Inject(method = "applyEntityAttributes", at = @At("TAIL"))
    private void increaseHealth(CallbackInfo ci){
        if (this.worldObj != null) {
            int progress = NMUtils.getWorldProgress();
            float bloodMoonModifier = NMUtils.getIsBloodMoon() ? 1.4f : 1;
            boolean isBloodMoon = bloodMoonModifier > 1;
            boolean isEclipse = NMUtils.getIsMobEclipsed(this);
            boolean isHostile = this.worldObj.getDifficultyParameter(NMDifficultyParam.ShouldMobsBeBuffed.class);

            if (isHostile) {
                if(isBloodMoon || isEclipse){
                    this.getEntityAttribute(SharedMonsterAttributes.followRange).setAttribute(24d);
                } else {
                    this.getEntityAttribute(SharedMonsterAttributes.followRange).setAttribute(MathHelper.floor_double(20.0d + progress * 1.5));
                    // 20 -> 21 -> 22 -> 23
                }
            }

            if(this.getSkeletonType().id() == 1){
                this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(((isHostile ? 24 : 20) + progress * (isHostile ? 4 : 2) + (isEclipse ? 15 : 0 )) * NMUtils.getNiteMultiplier());
                // 24.0 -> 28.0 -> 32.0 -> 36.0
            } else{
                this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(MathHelper.floor_double(((16.0 + progress * (isHostile ? 7 : 3)) * bloodMoonModifier + (isEclipse ? 15 : 0) * NMUtils.getNiteMultiplier())));
                // 16.0 -> 23.0 -> 30.0 -> 37.0
            }
            if(this.getSkeletonType().id() == 4){
                this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(((isHostile ? 24 : 20) + progress * (isHostile ? (isEclipse ? 8 : 6) : 2)) * NMUtils.getNiteMultiplier());
                // 24.0 -> 30.0 -> 36.0 -> 40.0
            }

            this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute((3.0 * (progress + 1) + (isEclipse ? 1 : 0)) * NMUtils.getNiteMultiplier());
            // 3.0 -> 4.0 -> 5.0 -> 6.0
            // 4.5 -> 6.0 -> 7.5 -> 9.0
        }
    }

    @Inject(method = "preInitCreature", at = @At("TAIL"))
    private void manageBloodMoonWitherSkellySpawning(CallbackInfo ci){
        if(this.worldObj != null){
            if((NMUtils.getIsBloodMoon() || areMobsEvolved) && this.rand.nextInt(16) == 0 && this.getSkeletonType().id() == 0){
                this.setSkeletonType(1);
            } else if (this.rand.nextInt(NMUtils.divByNiteMultiplier(10, 4)) == 0 && (WorldUtils.gameProgressHasWitherBeenSummonedServerOnly() || areMobsEvolved) && this.getSkeletonType().id() == 0){
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
            return 12 - (NMUtils.getWorldProgress() * 3);
        }
        return bound;
    }

    @Redirect(method = "onLivingUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntitySkeleton;checkForCatchFireInSun()V"))
    private void doNotCatchFireInSun(EntitySkeleton instance){}

    @Unique private int visibilityCheckCooldown = 0;
    @Unique private boolean canSeeIfJumpedCached = false;
    @Unique private boolean cannotSeeNormallyCached = false;
    @Unique private EntityPlayer lastTarget = null;

    @Inject(method = "onLivingUpdate", at = @At("TAIL"))
    private void manageJumpShotAndSpiderDismount(CallbackInfo ci) {
        if (this.worldObj != null) {
            if (this.getHeldItem() != null && this.getHeldItem().itemID == Item.bow.itemID && this.worldObj.getDifficultyParameter(NMDifficultyParam.ShouldMobsBeBuffed.class) && this.getAttackTarget() instanceof EntityPlayer targetPlayer) {
                this.jumpCooldown++;

                // Recalculate visibility every 4 ticks or if target changes
                if (this.visibilityCheckCooldown <= 0 || targetPlayer != this.lastTarget) {
                    this.canSeeIfJumpedCached = canSeeIfJumped(this, targetPlayer);
                    this.cannotSeeNormallyCached = cannotSeeNormally(this, targetPlayer);
                    this.visibilityCheckCooldown = 4; // Only check every 4 ticks
                    this.lastTarget = targetPlayer;
                }

                if (this.canSeeIfJumpedCached && this.cannotSeeNormallyCached && !this.isAirBorne && this.jumpCooldown >= 60) {
                    this.motionY = 0.45;
                    this.jumpCooldown = 0;
                }

                this.visibilityCheckCooldown--;
            }

            if (this.ridingEntity instanceof EntitySpider spider) {
                if(this.getAttackTarget() == spider){
                    this.setAttackTarget(null);
                }

                if (this.worldObj.isDaytime() && this.ticksExisted % 20 == 0 && !this.worldObj.isRemote) {
                    if (this.hasAttackTarget()) {
                        spider.entityToAttack = this.getAttackTarget();
                    } else if(spider.hasAttackTarget()){
                        this.entityToAttack = spider.entityToAttack;
                        this.setAttackTarget((EntityLivingBase) spider.entityToAttack);
                    }
                }
            }
        }
    }


    @Inject(method = "attackEntityWithRangedAttack", at = @At("TAIL"))
    private void manageBloodMoonAttack(EntityLivingBase target, float fDamageModifier, CallbackInfo ci){
        if(this.worldObj != null){
            if (NMUtils.getIsBloodMoon()){
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

    @Inject(method = "onSpawnWithEgg", at = @At("TAIL"))
    private void manageSkeletonVariants(EntityLivingData data, CallbackInfoReturnable<EntityLivingData> cir){
        if (this.worldObj != null) {
            int progress = NMUtils.getWorldProgress();
            boolean isHostile = this.worldObj.getDifficultyParameter(NMDifficultyParam.ShouldMobsBeBuffed.class);
            double bloodMoonModifier = NMUtils.getIsBloodMoon() ? 1.5 : 1;
            boolean isEclipse = NMUtils.getIsEclipse();
            double niteMultiplier = NMUtils.getNiteMultiplier();

            if (isEclipse) {
                assignEclipseSkeletonVariant();
                return;
            }

            if (shouldSpawnEnderSkeleton(progress, isHostile, bloodMoonModifier, niteMultiplier)) {
                setEnderSkeleton();
            } else if (shouldSpawnFireSkeleton(progress, isHostile, bloodMoonModifier, niteMultiplier)) {
                setFireSkeleton();
            } else {
                if (shouldSpawnIceSkeleton(progress, bloodMoonModifier, niteMultiplier)) {
                    setIceSkeleton();
                } else if (shouldSpawnJungleSkeleton(progress, bloodMoonModifier, niteMultiplier)) {
                    setJungleSkeleton();
                } else if (shouldSpawnSuperCriticalSkeleton(progress, bloodMoonModifier, niteMultiplier)) {
                    setSuperCriticalSkeleton();
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
        // 5: jungle
        // 6: supercritical
    }
    @Unique
    private void assignEclipseSkeletonVariant() {
        // picks a random variant including default & wither. each variant now has an equal 1/6 chance of spawning
        switch (rand.nextInt(6)) {
            case 5:
                setJungleSkeleton();
                break;
            case 4:
                setEnderSkeleton();
                break;
            case 3:
                setFireSkeleton();
                break;
            case 2:
                setIceSkeleton();
                break;
            case 1:
                this.setSkeletonType(1);
                break;
            case 0:
                this.setSkeletonType(0);
                break;
        }
    }

    @Inject(method = "onLivingUpdate", at = @At("TAIL"))
    private void manageVariantEffects(CallbackInfo ci){
        if(this.getSkeletonType().id() == 3){ // fireskeleton
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
            if(this.getSkeletonType().id() == 3){arrow.setDead();}
        }
    }
    @ModifyConstant(method = "attackEntityWithRangedAttack", constant = @Constant(floatValue = 12.0f))
    private float reduceArrowSpread(float constant){
        if (this.worldObj != null) {
            return NMUtils.divByNiteMultiplier((int) (8.0f - NMUtils.getWorldProgress()*2), 2);
        }
        return constant;
        // 8.0 -> 6.0 -> 4.0 -> 2.0
    }

    @Inject(method = "attackEntityFrom", at = @At(value = "HEAD"), cancellable = true)
    private void manageFallDamageImmunity(DamageSource damageSource, float damage, CallbackInfoReturnable<Boolean> cir){
        if(!this.isImmuneToFire()){
            this.isImmuneToFire = true;
        }
        if(damageSource == DamageSource.inWall){
            EntitySpider mountedSpider = this.getMountedSpider();

            if(mountedSpider != null){
                cir.setReturnValue(false);
            }
        }
        if (this.getSkeletonType().id() == 1 && this.dimension == 1 && damageSource == DamageSource.fall){
            cir.setReturnValue(false); // refers to wither skeletons spawned by the ender dragon
        }
    }

    @Unique private EntitySpider getMountedSpider(){
        if(this.ridingEntity instanceof EntitySpider) {
            return (EntitySpider) this.ridingEntity;
        }
        return null;
    }


    @Inject(method = "attackEntityWithRangedAttack",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/src/EntitySkeleton;playSound(Ljava/lang/String;FF)V"))
    private void determineWhatProjectileToShoot(EntityLivingBase target, float fDamageModifier, CallbackInfo ci){
        if (this.worldObj != null) {
            if(this.getSkeletonType().id() == 3){
                if (this.worldObj.getDifficultyParameter(NMDifficultyParam.ShouldMobsBeBuffed.class)) {
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
            if(this.worldObj.getDifficultyParameter(NMDifficultyParam.ShouldMobsBeBuffed.class) && (this.rand.nextInt(NMUtils.divByNiteMultiplier(60, 20)) < 3 + (NMUtils.getWorldProgress()*2) && this.getSkeletonType().id() != 4 && this.getSkeletonType().id() != 2)){
                EntityBurningArrow newArrow = new EntityBurningArrow(this.worldObj, arrow);
                this.worldObj.spawnEntityInWorld(newArrow);
                arrow.setDead();
                arrow.playSound("fire.fire", 1.0f, this.rand.nextFloat() * 0.4f + 0.8f);
            } else{
                arrow.setDamage(MathHelper.floor_double((2.0 + (NMUtils.getWorldProgress() * 2 - (this.worldObj.getDifficultyParameter(NMDifficultyParam.ShouldMobsBeBuffed.class) ? 0 : 1)))) * NMUtils.getNiteMultiplier());
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
        if (this.worldObj != null && this.worldObj.getDifficultyParameter(NMDifficultyParam.ShouldMobsBeBuffed.class)) {

            return switch (NMUtils.getWorldProgress()) {
                case 0 -> NMUtils.divByNiteMultiplier(60, 20);
                case 1 -> NMUtils.divByNiteMultiplier(50, 20);
                case 2 -> NMUtils.divByNiteMultiplier(45 + rand.nextInt(5), 20);
                case 3 -> NMUtils.divByNiteMultiplier(40 + rand.nextInt(5), 20);
                default -> constant;
            };
        }
        return constant;
    }
    @ModifyConstant(method = "<init>", constant = @Constant(floatValue = 15.0f))
    private float modifyAttackRange(float constant){
        if (this.worldObj != null && this.worldObj.getDifficultyParameter(NMDifficultyParam.ShouldMobsBeBuffed.class)) {
            return (float) ((18.0f + NMUtils.getWorldProgress() * 3) * Math.min(NMUtils.getNiteMultiplier(), 1.3f));
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
        if (iFlameLevel > 0 || this.getSkeletonType().id() == 1 || this.isBurning() && this.rand.nextFloat() < 0.3F) {
            arrow.setFire(100);
        }

        this.playSound("random.bow", 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
        this.worldObj.spawnEntityInWorld(arrow);
    }

    @Unique
    private boolean shouldSpawnEnderSkeleton(int progress, boolean isHostile, double bloodMoonModifier, double niteMultiplier) {
        double chance = (0.13 + Math.abs(progress - 2) * (isHostile ? 0.07 : 0.03)) * bloodMoonModifier * niteMultiplier;
        return (progress >= 2 || areMobsEvolved) && rand.nextFloat() < chance;
    }

    @Unique
    private boolean shouldSpawnFireSkeleton(int progress, boolean isHostile, double bloodMoonModifier, double niteMultiplier) {
        double chance = ((isHostile ? 0.09 : 0.03) + Math.abs(progress - 1) * 0.02) * bloodMoonModifier * niteMultiplier;
        return (progress >= 1 || areMobsEvolved) && rand.nextFloat() < chance && this.dimension != -1;
    }
    @ModifyArg(method = "initComponents", at = @At(value = "INVOKE", target = "Lbtw/entity/component/VariantComponent;<init>(IIILjava/util/function/Function;)V"), index =  0)
    private int allowMoreThanTwoSkeletonVariants(int numVariants){
        return 6;
    }

    @Unique
    private boolean shouldSpawnIceSkeleton(int progress, double bloodMoonModifier, double niteMultiplier) {
        double chance = (0.04 + progress * 0.02) * bloodMoonModifier * niteMultiplier;
        return rand.nextFloat() < chance;
    }

    @Unique
    private boolean shouldSpawnJungleSkeleton(int progress, double bloodMoonModifier, double niteMultiplier) {
        if(!NightmareMode.moreVariants){
            return false;
        }
        BiomeGenBase spawnBiome = this.worldObj.getBiomeGenForCoords((int) this.posX, (int) this.posZ);
        double jungleBonus = spawnBiome == BiomeGenBase.jungle || spawnBiome == BiomeGenBase.jungleHills ? 0.3 : 0;
        double chance = (0.04 + progress * 0.06 + jungleBonus) * bloodMoonModifier * niteMultiplier;
        return rand.nextFloat() < chance;
    }

    @Unique
    private boolean shouldSpawnSuperCriticalSkeleton(int progress, double bloodMoonModifier, double niteMultiplier) {
        if(!NightmareMode.moreVariants){
            return false;
        }
        BiomeGenBase spawnBiome = this.worldObj.getBiomeGenForCoords((int) this.posX, (int) this.posZ);
        double desertBonus = spawnBiome == BiomeGenBase.desert || spawnBiome == BiomeGenBase.desertHills ? 0.3 : 0;
        double chance = (0.04 + progress * 0.06 + desertBonus) * bloodMoonModifier * niteMultiplier;
        return rand.nextFloat() < chance;
    }

    @Unique
    private void setEnderSkeleton() {
        this.setSkeletonType(NightmareMode.SKELETON_ENDER);
        this.setArmor(1052688, BTWItems.woolBoots, BTWItems.woolLeggings, BTWItems.woolChest);
        this.getEntityAttribute(BTWAttributes.armor).setAttribute(8.0d);
    }

    @Unique
    private void setFireSkeleton() {
        this.setSkeletonType(NightmareMode.SKELETON_FIRE);
        this.clearArmor();
        this.setFire(10000);
        this.getEntityAttribute(BTWAttributes.armor).setAttribute(4.0d);
    }

    @Unique
    private void setIceSkeleton() {
        this.setSkeletonType(NightmareMode.SKELETON_ICE);
        this.setHelmet(13260, BTWItems.woolHelmet);
    }

    @Unique
    private void setJungleSkeleton() {
        this.setSkeletonType(NightmareMode.SKELETON_JUNGLE);
    }
    @Unique
    private void setSuperCriticalSkeleton() {
        this.targetTasks.removeAllTasksOfClass(EntityAINearestAttackableTarget.class);
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, false));

        this.setSkeletonType(NightmareMode.SKELETON_SUPERCRITICAL);
    }

    @Unique
    private void setArmor(int color, Item... items) {
        for (int i = 0; i < items.length; i++) {
            this.setCurrentItemOrArmor(i + 1, setItemColor(new ItemStack(items[i]), color));
            this.equipmentDropChances[i + 1] = 0f;
        }
    }

    @Unique
    private void setHelmet(int color, Item item) {
        this.setCurrentItemOrArmor(4, setItemColor(new ItemStack(item), color));
        this.equipmentDropChances[4] = 0f;
    }

    @Unique
    private void clearArmor() {
        for (int i = 1; i <= 4; i++) {
            this.setCurrentItemOrArmor(i, null);
        }
    }

}
