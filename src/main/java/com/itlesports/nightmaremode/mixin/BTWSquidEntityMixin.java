package com.itlesports.nightmaremode.mixin;

import btw.community.nightmaremode.NightmareMode;
import btw.entity.mob.BTWSquidEntity;
import btw.world.util.difficulty.Difficulties;
import com.itlesports.nightmaremode.entity.EntityBloodWither;
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

import java.util.Iterator;
import java.util.List;

@Mixin(BTWSquidEntity.class)
public abstract class BTWSquidEntityMixin extends EntityWaterMob{
    @Shadow(remap = false) private int tentacleAttackCooldownTimer;
    @Shadow protected abstract void retractTentacleAttackOnCollision();

    @Unique private int squidOnHeadTimer = 0;
    @Unique private int recentParryCount = 0;
    @Unique private static final int buffedSquidBonus = NightmareMode.buffedSquids ? 2 : 1;

    public BTWSquidEntityMixin(World par1World) {
        super(par1World);
    }
    @Inject(method = "<init>", at = @At("TAIL"))
    private void manageEclipseChance(World world, CallbackInfo ci){
        NightmareUtils.manageEclipseChance(this,24);
    }

    @Redirect(method = "updateEntityActionState", at = @At(value = "FIELD", target = "Lnet/minecraft/src/Entity;inWater:Z"))
    private boolean attackUnderwaterPlayers(Entity instance){
        return false;
    }

    @Inject(method = "updateTentacleAttack",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/BTWSquidEntity;tentacleAttackFlingTarget(Lnet/minecraft/src/Entity;Z)V",
                    shift = At.Shift.AFTER),
            locals = LocalCapture.CAPTURE_FAILHARD)

    private void teleportPlayer(CallbackInfo ci, Vec3 tentacleTip, AxisAlignedBB tipBox, List potentialCollisionList, Iterator collisionIterator, EntityLivingBase tempEntity){
        if (tempEntity instanceof EntityPlayer && ((EntityPlayer) tempEntity).isBlocking()) {
            Vec3 lookVec = tempEntity.getLookVec(); // Player's look direction
            Vec3 directionToSquid = Vec3.createVectorHelper(
                    this.posX - tempEntity.posX,
                    this.posY - (tempEntity.posY + tempEntity.getEyeHeight()), // From eye level
                    this.posZ - tempEntity.posZ
            ).normalize(); // Normalize to get direction

            double dotProduct = lookVec.dotProduct(directionToSquid); // How aligned the vectors are

            if (dotProduct > 0.7) { // Threshold: 1 = perfect alignment, 0 = perpendicular, -1 = opposite
                ((EntityPlayer) tempEntity).setItemInUse(null, 0);
                tempEntity.getHeldItem().attemptDamageItem(this.rand.nextInt(6) + 2 + 2 * this.recentParryCount, this.rand);
                this.retractTentacleAttackOnCollision();
                this.recentParryCount += 1;
                return;
            }
        }
        tempEntity.setPositionAndUpdate(this.posX,this.posY,this.posZ);
        if (tempEntity instanceof EntityPlayer) {
            this.playSound("mob.endermen.portal",2.0F,1.0F);
        } else{
            this.worldObj.playSoundAtEntity(this,"mob.endermen.portal",1.0F,this.worldObj.rand.nextFloat() * 0.1F + 0.9F);
        }
    }

    @Inject(method = "onLivingUpdate", at = @At("HEAD"))
    private void manageRecentParries(CallbackInfo ci){
        if (this.ticksExisted % 160 == 0) {
            this.recentParryCount = Math.max(recentParryCount - 1, 0);
        }
    }
    @ModifyConstant(method = "dropFewItems", constant = @Constant(intValue = 8))
    private int increaseMysteriousGlandDropRate(int constant){
        if(NightmareUtils.getIsMobEclipsed(this)){
            return 4;
        }
        return 2;
    }
    // makes the random function roll a number between 0 and 2 instead of 0 and 8

    @ModifyArg(method = "dropFewItems", at = @At(value = "INVOKE", target = "Lbtw/entity/mob/BTWSquidEntity;dropItem(II)Lnet/minecraft/src/EntityItem;"),index = 0)
    private int eclipseDropVoidSacks(int par1){
        if(NightmareUtils.getIsMobEclipsed(this)){
            return NMItems.voidSack.itemID;
        }
        return par1;
    }

    @Inject(method = "dropFewItems", at = @At("TAIL"))
    private void allowBloodOrbDrops(boolean bKilledByPlayer, int iLootingModifier, CallbackInfo ci){
        int worldProgress = this.worldObj != null ? NightmareUtils.getWorldProgress(this.worldObj) : 0;
        int bloodOrbID = NightmareUtils.getIsBloodMoon() ? NMItems.bloodOrb.itemID : 0;
        if (bloodOrbID > 0 && bKilledByPlayer) {
            int var4 = this.rand.nextInt(4) + 2;
            // 2 - 5
            if (iLootingModifier > 0) {
                var4 += this.rand.nextInt(iLootingModifier + 1);
            }
            for (int var5 = 0; var5 < var4; ++var5) {
                this.dropItem(bloodOrbID, 1);
            }
        }


        if (bKilledByPlayer && NightmareUtils.getIsMobEclipsed(this)) {
            for(int i = 0; i < (iLootingModifier * 2) + 1; i++){
                if(this.rand.nextInt(12) == 0){
                    this.dropItem(NMItems.darksunFragment.itemID, 1);
                    if (this.rand.nextBoolean()) {
                        break;
                    }
                }
            }
        }
        if(bKilledByPlayer){
            for(int i = 0; i < (iLootingModifier * 2) + 1; i++){
                if(this.rand.nextInt(12 - worldProgress * 2) == 0){
                    // 1/12 -> 1/10 -> 1/8 -> 1/6
                    this.dropItem(NMItems.calamari.itemID, 1);
                }
            }
        }
    }

    // adds a flat +1 to inc sac drops
    @Inject(method = "dropFewItems", at = @At(value = "FIELD", target = "Lbtw/entity/mob/BTWSquidEntity;rand:Ljava/util/Random;",ordinal = 0))
    private void dropMoreSacs(boolean bKilledByPlayer, int iLootingModifier, CallbackInfo ci){
        this.entityDropItem(new ItemStack(Item.dyePowder, 1, 0), 0.0f);
    }
    @Redirect(method = "updateTentacleAttack",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/BTWSquidEntity;tentacleAttackFlingTarget(Lnet/minecraft/src/Entity;Z)V"))
    private void doNothing(BTWSquidEntity instance, Entity iFXJ, boolean iFXK){}

    @ModifyConstant(method = "updateHeadCrab", constant = @Constant(intValue = 40),remap = false)
    private int reduceSquidDamageInterval(int constant){
        return 11 / buffedSquidBonus;
    }

    @ModifyArg(method = "checkForScrollDrop", at = @At(value = "INVOKE", target = "Ljava/util/Random;nextInt(I)I"))
    private int reduceScrollDropChance(int bound){
        return 100;
    }

    @ModifyArg(method = "updateEntityActionState", at = @At(value = "INVOKE", target = "Lbtw/entity/mob/BTWSquidEntity;findClosestValidAttackTargetWithinRange(D)Lnet/minecraft/src/Entity;"))
    private double increaseSquidRange(double dRange){
        if(NightmareUtils.getIsEclipse()){
            return 16;
        }
        return (dRange + (NightmareUtils.getWorldProgress(this.worldObj) > 0 ? 4 : 0)) * (NightmareMode.buffedSquids ? 1.5f : 1);
        // 20 max
    }


    @Inject(method = "updateHeadCrab",
            at = @At("HEAD"),remap = false)
    private void doScaryThingsOnHead(CallbackInfo ci) {
        this.squidOnHeadTimer++;
        if (rand.nextInt(60) == 0) {
            this.playSound("mob.ghast.scream",0.3F, this.worldObj.rand.nextFloat() * 0.1F + 0.9F);
        }

        if(this.worldObj.getDifficulty() == Difficulties.HOSTILE && this.ridingEntity instanceof EntityPlayer player) {
            if (this.squidOnHeadTimer > 100 && !EntityBloodWither.isBossActive()) {
                player.addPotionEffect(new PotionEffect(Potion.blindness.id, 200, 0));
            }
            switch (NightmareUtils.getWorldProgress(this.worldObj)) {
                case 1:
                    if (!player.isPotionActive(Potion.poison)) {
                        player.addPotionEffect(new PotionEffect(Potion.poison.id,120 * buffedSquidBonus,0));
                    }
                    break;
                case 2,3:
                    if (!player.isPotionActive(Potion.wither)) {
                        player.addPotionEffect(new PotionEffect(Potion.wither.id, 120 * buffedSquidBonus,0));
                    }
                    break;
                default:
                    break;
            }
        }
    }


    @ModifyConstant(method = "checkForHeadCrab", constant = @Constant(intValue = 40),remap = false)
    private int reduceDamageInterval(int constant){
        return 15 / buffedSquidBonus;
    }

    @ModifyArg(method = "applyEntityAttributes", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/AttributeInstance;setAttribute(D)V"))
    private double modifySquidHP(double d) {
        if (this.worldObj.getDifficulty() == Difficulties.HOSTILE) {
            return (NightmareUtils.getWorldProgress(this.worldObj) > 0 ? 12 * (NightmareUtils.getWorldProgress(this.worldObj)+1) : 18) * buffedSquidBonus * NightmareUtils.getNiteMultiplier();
            // pre nether 18, hardmode 24, post wither 36, post dragon 48
            // if BS is on, 36 -> 48 -> 72 -> 96
        }
        return d;
    }
    // TENTACLES
    @Inject(method = "updateEntityActionState", at = @At("HEAD"))
    private void lowerTentacleCooldown(CallbackInfo ci) {
        if (this.worldObj.getDifficulty() == Difficulties.HOSTILE) {
            --this.tentacleAttackCooldownTimer;
            if (NightmareUtils.getWorldProgress(this.worldObj) > 1) {
                this.tentacleAttackCooldownTimer -= 2 * buffedSquidBonus;
            }
        }
    }
    @ModifyConstant(method = "launchTentacleAttackInDirection", constant = @Constant(intValue = 100),remap = false)
    private int lowerTentacleAttackCooldownTimer(int constant){
        if (this.worldObj.getDifficulty() == Difficulties.HOSTILE) {
            return 100 - (NightmareUtils.getWorldProgress(this.worldObj) * 10);
        }
        return constant;
        // cooldown has a degree of randomness, so it's not like it'll fire every 10 ticks post dragon. it has some variance.
        // this purely insures the cooldown condition is true whenever it is checked
    }

    @ModifyArg(method = "getValidHeadCrabTargetInRange", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;getEntitiesWithinAABB(Ljava/lang/Class;Lnet/minecraft/src/AxisAlignedBB;)Ljava/util/List;"),index = 0)
    private Class avoidAttackingAnimalsIfEclipsed(Class par1Class){
        return NightmareUtils.getIsMobEclipsed(this) ? EntityPlayer.class : par1Class;
    }

    @Inject(method = "checkForHeadCrab", at = @At("HEAD"),remap = false)
    private void manageJumpOnEclipse(CallbackInfo ci){
        if(this.entityToAttack != null && this.posY < this.entityToAttack.posY + this.entityToAttack.getEyeHeight() + 1 && (NightmareUtils.getIsMobEclipsed(this) || buffedSquidBonus == 2) && !this.entityToAttack.hasHeadCrabbedSquid() && !this.entityToAttack.isInWater()){
            this.motionY = 0.2f;
        }
    }
    @Inject(method = "onLivingUpdate", at = @At("HEAD"))
    private void manageSquidsOnBossFight(CallbackInfo ci){
        if(this.posY <= 200 && EntityBloodWither.isBossActive()){
            this.setDead();
        }
    }

    @ModifyConstant(method = "onLivingUpdate", constant = @Constant(doubleValue = 0.02,ordinal = 0))
    private double noGravityOnEclipse(double constant){
        return NightmareUtils.getIsMobEclipsed(this) || buffedSquidBonus == 2 ? 0d : constant;
    }

    @ModifyConstant(method = "onLivingUpdate", constant = @Constant(doubleValue = 0.8d))
    private double noGravityOnEclipse1(double constant){
        return NightmareUtils.getIsMobEclipsed(this) || buffedSquidBonus == 2 ? 1d : constant;
    }

    // increasing the squid range

    @ModifyConstant(method = "launchTentacleAttackInDirection", constant = @Constant(doubleValue = 6.0d),remap = false)
    private double increaseCalculatedTentacleRange(double constant){
        return (NightmareUtils.getIsMobEclipsed(this) ? 12.0d : 7.0d) * buffedSquidBonus;
    }
    @Inject(method = "attemptTentacleAttackOnTarget", at = @At("HEAD"),cancellable = true,remap = false)
    private void squidAvoidAttackingHeadcrabbedPlayer(CallbackInfo ci){
        if(this.entityToAttack.hasHeadCrabbedSquid()){
            ci.cancel();
        }
    }

    @ModifyConstant(method = "attemptTentacleAttackOnTarget", constant = @Constant(doubleValue = 36.0),remap = false)
    private double manageRange(double constant){
        return (NightmareUtils.getIsMobEclipsed(this) ? 144.0 : 64) * (buffedSquidBonus * buffedSquidBonus);
    }
    // let squid see through walls
    @Redirect(method = "attemptTentacleAttackOnTarget", at = @At(value = "INVOKE", target = "Lbtw/entity/mob/BTWSquidEntity;canEntityBeSeen(Lnet/minecraft/src/Entity;)Z"))
    private boolean canSeeThroughWalls1(BTWSquidEntity instance, Entity entity){
        return true;
    }
    @Redirect(method = "attemptTentacleAttackOnTarget", at = @At(value = "INVOKE", target = "Lbtw/entity/mob/BTWSquidEntity;canEntityCenterOfMassBeSeen(Lnet/minecraft/src/Entity;)Z"))
    private boolean canSeeThroughWalls2(BTWSquidEntity instance, Entity entity){
        return true;
    }

//    @Inject(method = "checkForHeadCrab", at = @At("HEAD"),remap = false)
//    private void manageEclipseRage(CallbackInfo ci){
//        if(NightmareUtils.getIsEclipse() && this.eclipseRageCooldown <= 40){
//            double deltaX = getRandomOffsetFromPosition(this,this.posX);
//            double deltaY = getRandomOffsetFromPosition(this,this.posY);
//            double deltaZ = getRandomOffsetFromPosition(this,this.posZ);
//
//            double distSqToTarget = deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;
//            if(distSqToTarget <= 200){
//
//                double dDistToTarget = MathHelper.sqrt_double(distSqToTarget);
//                double dUnitVectorToTargetX = deltaX / dDistToTarget;
//                double dUnitVectorToTargetY = deltaY / dDistToTarget;
//                double dUnitVectorToTargetZ = deltaZ / dDistToTarget;
//
//                this.invokeLaunchTentacle(dUnitVectorToTargetX, dUnitVectorToTargetY, dUnitVectorToTargetZ);
//                this.tentacleAttackCooldownTimer = 1;
//                this.tentacleAttackInProgressCounter = -1;
//            }
//
//            this.eclipseRageCooldown = (this.rand.nextInt(3) + 2) * 50;
//        }
//
//        this.eclipseRageCooldown = Math.max(this.eclipseRageCooldown - 2, 0);
//    }
//
//
//    @Unique private static double getRandomOffsetFromPosition(EntityLivingBase entity, double pos){
//        return (entity.rand.nextBoolean() ? -1 : 1) * entity.rand.nextInt(4)+6;
//    }
    
    // sets the squid to be permanently in darkness if post nether. this is so the squids are always hostile
    @ModifyVariable(method = "updateEntityActionState", at = @At(value = "STORE"),ordinal = 0)
    private boolean hostilePostNether(boolean bIsInDarkness) {
        if (NightmareUtils.getWorldProgress(this.worldObj) > 0 && this.worldObj.getDifficulty() == Difficulties.HOSTILE) {
            return true;
        }
        return bIsInDarkness;
    }
    @Redirect(method = "findClosestValidAttackTargetWithinRange", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;isDaytime()Z"))
    private boolean squidAlwaysNightPostNether(World instance){
        if(NightmareUtils.getWorldProgress(this.worldObj) > 0){
            return false;
        } else return this.worldObj.isDaytime();
    }

    @Redirect(method = "findClosestValidAttackTargetWithinRange", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityPlayer;getBrightness(F)F"))
    private float playerPermanentlyInDarknessAfterNether(EntityPlayer instance, float v){
        if(NightmareUtils.getWorldProgress(this.worldObj)>0 && this.worldObj.getDifficulty() == Difficulties.HOSTILE){
            return 0f;
        }
        return instance.getBrightness(v);
    }

    @Inject(method = "getCanSpawnHere", at = @At("HEAD"),cancellable = true)
    private void manageEclipseSpawns(CallbackInfoReturnable<Boolean> cir){
        int targetY = EntityBloodWither.isBossActive() ? 205 : 63;
        if(NightmareUtils.getIsEclipse() && this.rand.nextInt(4) == 0 && this.getCanSpawnHereNoPlayerDistanceRestrictions() && this.worldObj.getClosestPlayer(this.posX, this.posY, this.posZ, 10) == null && this.posY >= targetY){
            cir.setReturnValue(true);
        }
    }

    @Unique private boolean getCanSpawnHereNoPlayerDistanceRestrictions() {
        return this.worldObj.checkNoEntityCollision(this.boundingBox) && this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox).isEmpty();
    }

    @Redirect(method = "findClosestValidAttackTargetWithinRange", at = @At(value = "INVOKE", target = "Lbtw/entity/mob/BTWSquidEntity;canEntityBeSeen(Lnet/minecraft/src/Entity;)Z"))
    private boolean canSeeThroughObstacles(BTWSquidEntity instance, Entity entity){
        return true;
    }

    // making the squid launch tentacles even if it cannot see the player, even if its on land, even if the player is not in water
    @Redirect(method = "updateEntityActionState", at = @At(value = "FIELD", target = "Lbtw/entity/mob/BTWSquidEntity;inWater:Z",ordinal = 0))
    private boolean tentacleEvenIfBeached(BTWSquidEntity instance){
        return true;
    }

    @Override
    public boolean isInWater() {
        if(NightmareUtils.getIsMobEclipsed(this) || buffedSquidBonus == 2){
            return true;
        }
        return super.isInWater();
    }

    @Override
    public boolean isInsideOfMaterial(Material par1Material) {
        if((NightmareUtils.getIsMobEclipsed(this) || buffedSquidBonus == 2) && par1Material == Material.water){
            return true;
        }
        return super.isInsideOfMaterial(par1Material);
    }
}
