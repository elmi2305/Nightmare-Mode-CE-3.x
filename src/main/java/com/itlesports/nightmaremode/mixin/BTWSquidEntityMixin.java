package com.itlesports.nightmaremode.mixin;

import btw.entity.mob.BTWSquidEntity;
import btw.world.util.difficulty.Difficulties;
import com.itlesports.nightmaremode.NightmareUtils;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.List;

@Mixin(BTWSquidEntity.class)
public abstract class BTWSquidEntityMixin extends EntityWaterMob{
    @Shadow(remap = false) private int headCrabDamageCounter;
    @Shadow(remap = false) private int tentacleAttackCooldownTimer;
    @Unique int squidOnHeadTimer = 0;
    @Unique int jumpCooldown;

    public BTWSquidEntityMixin(World par1World) {
        super(par1World);
    }

    @Inject(method = "updateTentacleAttack",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/BTWSquidEntity;tentacleAttackFlingTarget(Lnet/minecraft/src/Entity;Z)V",
                    shift = At.Shift.AFTER),
            locals = LocalCapture.CAPTURE_FAILHARD)

    private void teleportPlayer(CallbackInfo ci, Vec3 tentacleTip, AxisAlignedBB tipBox, List potentialCollisionList, Iterator collisionIterator, EntityLivingBase tempEntity){
        tempEntity.setPositionAndUpdate(this.posX,this.posY-1,this.posZ);
        if (tempEntity instanceof EntityPlayer) {
            this.playSound("mob.endermen.portal",2.0F,1.0F);
        } else{
            this.worldObj.playSoundAtEntity(this,"mob.endermen.portal",1.0F,this.worldObj.rand.nextFloat() * 0.1F + 0.9F);
        }
    }
    @ModifyConstant(method = "dropFewItems", constant = @Constant(intValue = 8))
    private int increaseMysteriousGlandDropRate(int constant){return 2;}
    // makes the random function roll a number between 0 and 2 instead of 0 and 8

    // adds a flat +1 to inc sac drops
    @Inject(method = "dropFewItems", at = @At(value = "FIELD", target = "Lbtw/entity/mob/BTWSquidEntity;rand:Ljava/util/Random;",ordinal = 0))
    private void dropMoreSacs(boolean bKilledByPlayer, int iLootingModifier, CallbackInfo ci){
        this.entityDropItem(new ItemStack(Item.dyePowder, 1, 0), 0.0f);
    }
    @Redirect(method = "updateTentacleAttack",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/BTWSquidEntity;tentacleAttackFlingTarget(Lnet/minecraft/src/Entity;Z)V"))
    private void doNothing(BTWSquidEntity instance, Entity iFXJ, boolean iFXK){}

    @Inject(method = "updateHeadCrab",
            at = @At(value = "FIELD",
                    target = "Lbtw/entity/mob/BTWSquidEntity;headCrabDamageCounter:I",
                    ordinal = 3,
                    shift = At.Shift.AFTER))
    private void resetHeadCrabCounter(CallbackInfo ci){
        if (this.worldObj.getDifficulty() == Difficulties.HOSTILE) {
            this.headCrabDamageCounter = 11;
        }
    }

    @ModifyArg(method = "checkForScrollDrop", at = @At(value = "INVOKE", target = "Ljava/util/Random;nextInt(I)I"))
    private int reduceScrollDropChance(int bound){
        return 100;
    }

    @Inject(method = "applyEntityAttributes", at = @At("TAIL"))
    private void increaseSquidRangeDuringBloodMoon(CallbackInfo ci){
        if(NightmareUtils.getIsBloodMoon(this.worldObj)){
            this.getEntityAttribute(SharedMonsterAttributes.followRange).setAttribute(22.0d);
        }
    }


    @Inject(method = "updateHeadCrab",
            at = @At("HEAD"),remap = false)
    private void doScaryThingsOnHead(CallbackInfo ci) {
        squidOnHeadTimer++;
        if (rand.nextInt(60)==0) {
            this.playSound("mob.ghast.scream",0.3F, this.worldObj.rand.nextFloat() * 0.1F + 0.9F);
        }

        if(this.worldObj.getDifficulty() == Difficulties.HOSTILE && this.ridingEntity instanceof EntityPlayer headcrabbedPlayer) {
            if (squidOnHeadTimer > 100) {
                headcrabbedPlayer.addPotionEffect(new PotionEffect(Potion.blindness.id, 200, 0));
            }
            switch (NightmareUtils.getWorldProgress(this.worldObj)) {
                case 0:
                    break;
                case 1:
                    if (!headcrabbedPlayer.isPotionActive(Potion.poison)) {
                        headcrabbedPlayer.addPotionEffect(new PotionEffect(Potion.poison.id,120,0));
                    }
                    break;
                case 2:
                    if (!headcrabbedPlayer.isPotionActive(Potion.wither)) {
                        headcrabbedPlayer.addPotionEffect(new PotionEffect(Potion.wither.id, 120,0));
                    }
                    break;
                case 3:
                    if (!headcrabbedPlayer.isPotionActive(Potion.wither)) {
                        headcrabbedPlayer.addPotionEffect(new PotionEffect(Potion.wither.id, 300,0));
                    }
                    headcrabbedPlayer.addPotionEffect(new PotionEffect(Potion.hunger.id, 160,0));
                    break;
            }
        }
    }


// sets the time until the squid starts dealing damage to n ticks
    @Inject(method = "checkForHeadCrab", at = @At(value = "FIELD", target = "Lbtw/entity/mob/BTWSquidEntity;headCrabDamageCounter:I", shift = At.Shift.AFTER))
    private void firstHeadCrabInterval(CallbackInfo ci){
        this.headCrabDamageCounter = 15;
    }

    @ModifyArg(method = "applyEntityAttributes", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/AttributeInstance;setAttribute(D)V"))
    private double modifySquidHP(double d) {
        if (this.worldObj.getDifficulty() == Difficulties.HOSTILE) {
            return NightmareUtils.getWorldProgress(this.worldObj) > 0 ? 12*(NightmareUtils.getWorldProgress(this.worldObj)+1) : 18;
            // pre nether 18, hardmode 24, post wither 36, post dragon 48
        }
        return d;
    }
    // TENTACLES
    @Inject(method = "updateEntityActionState", at = @At("HEAD"))
    private void lowerTentacleCooldown(CallbackInfo ci) {
        if (this.worldObj.getDifficulty() == Difficulties.HOSTILE) {
            --this.tentacleAttackCooldownTimer;
            if (NightmareUtils.getWorldProgress(this.worldObj) > 1) {
                this.tentacleAttackCooldownTimer -= 2;
            }
        }
    }
    @ModifyConstant(method = "launchTentacleAttackInDirection", constant = @Constant(intValue = 100),remap = false)
    private int lowerTentacleAttackCooldownTimer(int constant){
        if (this.worldObj.getDifficulty() == Difficulties.HOSTILE) {
            return 100-(NightmareUtils.getWorldProgress(this.worldObj)*30);
        }
        return constant;
        // cooldown has a degree of randomness, so it's not like it'll fire every 10 ticks post dragon. it has some variance.
        // this purely insures the cooldown condition is true whenever it is checked
    }

    // increasing the squid range
    @Shadow(remap = false) private double tentacleAttackTargetX;
    @Shadow(remap = false) private double tentacleAttackTargetY;
    @Shadow(remap = false) private double tentacleAttackTargetZ;
    @Shadow(remap = false)
    public abstract void launchTentacleAttackInDirection(double dUnitVectorToTargetX, double dUnitVectorToTargetY, double dUnitVectorToTargetZ);

    @Shadow public abstract void setTarget(Entity targetEntity);

    @Inject(method = "launchTentacleAttackInDirection",
            at = @At(value = "FIELD",
                    target = "Lbtw/entity/mob/BTWSquidEntity;tentacleAttackTargetZ:D",
                    shift = At.Shift.AFTER)
    )
    private void editTentacleVectors(double dUnitVectorToTargetX, double dUnitVectorToTargetY, double dUnitVectorToTargetZ, CallbackInfo ci){
        if (this.worldObj.getDifficulty() == Difficulties.HOSTILE) {
            this.tentacleAttackTargetX = this.posX + dUnitVectorToTargetX * 8.0;
            this.tentacleAttackTargetY = this.posY + (double)(this.height / 2.0F) + dUnitVectorToTargetY * 8.0;
            this.tentacleAttackTargetZ = this.posZ + dUnitVectorToTargetZ * 8.0;
        }
    }

    @Redirect(method = "updateHeadCrabActionState",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/BTWSquidEntity;attemptTentacleAttackOnTarget()V"))
    // redirecting attemptTentacleAttackOnTarget to execute basically a better version of itself. doubled the range in the
    // if statement, and I made there not be any LOS (line of sight) checks when launching the tentacle.
    private void attemptTentacleAttackOnTarget1(BTWSquidEntity instance) {
        double range = this.worldObj.getDifficulty() == Difficulties.HOSTILE ? 81.0 : 36.0;
        double dDeltaX = instance.entityToAttack.posX - this.posX;
        double dDeltaY = instance.entityToAttack.posY + (double) (instance.entityToAttack.height / 2.0F) - (this.posY + (double) (this.height / 2.0F));
        double dDeltaZ = instance.entityToAttack.posZ - this.posZ;
        double dDistSqToTarget = dDeltaX * dDeltaX + dDeltaY * dDeltaY + dDeltaZ * dDeltaZ;
        if (dDistSqToTarget < range) {
            dDeltaY = instance.entityToAttack.posY + (double) instance.entityToAttack.getEyeHeight() - (this.posY + (double) (this.height / 2.0F));
            dDistSqToTarget = dDeltaX * dDeltaX + dDeltaY * dDeltaY + dDeltaZ * dDeltaZ;

            double dDistToTarget = MathHelper.sqrt_double(dDistSqToTarget);
            double dUnitVectorToTargetX = dDeltaX / dDistToTarget;
            double dUnitVectorToTargetY = dDeltaY / dDistToTarget;
            double dUnitVectorToTargetZ = dDeltaZ / dDistToTarget;
            this.launchTentacleAttackInDirection(dUnitVectorToTargetX, dUnitVectorToTargetY, dUnitVectorToTargetZ);
        }
    }


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
        if(NightmareUtils.getWorldProgress(this.worldObj)>0){
            return false;
        } else return this.worldObj.isDaytime();
    }

    @Redirect(method = "findClosestValidAttackTargetWithinRange", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityPlayer;getBrightness(F)F"))
    private float playerPermanentlyInDarknessAfterNether(EntityPlayer instance, float v){
        if(NightmareUtils.getWorldProgress(this.worldObj)>0 && this.worldObj.getDifficulty() == Difficulties.HOSTILE){
            return 0.01f;
        }
        return instance.getBrightness(1.0f);
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

    @Redirect(method = "updateEntityActionState", at = @At(value = "INVOKE", target = "Lbtw/entity/mob/BTWSquidEntity;attemptTentacleAttackOnTarget()V"))
    private void attemptTentacleAttackOnTargetBetter2(BTWSquidEntity instance){
        double range = this.worldObj.getDifficulty() == Difficulties.HOSTILE ? 81.0 : 36.0;

        BTWSquidEntity thisObject = (BTWSquidEntity) (Object) this;
        double dDeltaX = thisObject.entityToAttack.posX - this.posX;
        double dDeltaY = thisObject.entityToAttack.posY + (double) (thisObject.entityToAttack.height / 2.0F) - (this.posY + (double) (this.height / 2.0F));
        double dDeltaZ = thisObject.entityToAttack.posZ - this.posZ;
        double dDistSqToTarget = dDeltaX * dDeltaX + dDeltaY * dDeltaY + dDeltaZ * dDeltaZ;
        if (dDistSqToTarget < range) {
            dDeltaY = thisObject.entityToAttack.posY + (double) thisObject.entityToAttack.getEyeHeight() - (this.posY + (double) (this.height / 2.0F));
            dDistSqToTarget = dDeltaX * dDeltaX + dDeltaY * dDeltaY + dDeltaZ * dDeltaZ;

            double dDistToTarget = MathHelper.sqrt_double(dDistSqToTarget);
            double dUnitVectorToTargetX = dDeltaX / dDistToTarget;
            double dUnitVectorToTargetY = dDeltaY / dDistToTarget;
            double dUnitVectorToTargetZ = dDeltaZ / dDistToTarget;
            this.launchTentacleAttackInDirection(dUnitVectorToTargetX, dUnitVectorToTargetY, dUnitVectorToTargetZ);
        }
    }
}
