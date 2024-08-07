package com.itlesports.nightmaremode.mixin;

import btw.entity.mob.BTWSquidEntity;
import btw.world.util.WorldUtils;
import com.itlesports.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.NightmareUtils;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.Iterator;
import java.util.List;

@Mixin(BTWSquidEntity.class)
public abstract class BTWSquidEntityMixin extends EntityLivingBase{
    @Shadow(remap = false) private int headCrabDamageCounter;
    @Shadow(remap = false) private int tentacleAttackCooldownTimer;
    @Unique int squidOnHeadTimer = 0;

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
        this.playSound("mob.endermen.portal",20.0F,1.0F);
    }
    @ModifyConstant(method = "dropFewItems", constant = @Constant(intValue = 8))
    private int increaseMysteriousGlandDropRate(int constant){return 3;}
    // makes the random function roll a number between 0 and 3 instead of 0 and 8


    @ModifyConstant(method = "dropFewItems", constant = @Constant(intValue = 1))
    private int increaseInkSacDrops(int constant){return 3;}
    // adds a flat +2 increase to ink sac drops

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
        this.headCrabDamageCounter = 11;
    }


    @Inject(method = "updateHeadCrab",
            at = @At("HEAD"),remap = false)
    private void doScaryThingsOnHead(CallbackInfo ci) {
        squidOnHeadTimer++;
        if (rand.nextInt(25)==0) {
            this.playSound("mob.ghast.scream",2.0F, this.worldObj.rand.nextFloat() * 0.1F + 0.9F);
        }

        if(this.ridingEntity instanceof EntityPlayer headcrabbedPlayer) {
            if (squidOnHeadTimer > 100) {
                headcrabbedPlayer.addPotionEffect(new PotionEffect(Potion.blindness.id, 200, 0));
            }
            switch (NightmareUtils.getGameProgressMobsLevel(this.worldObj)) {
                case 0:
                    break;
                case 1:
                    headcrabbedPlayer.addPotionEffect(new PotionEffect(Potion.poison.id,120,0));
                    break;
                case 2:
                    headcrabbedPlayer.addPotionEffect(new PotionEffect(Potion.wither.id, 200,0));
                    break;
                case 3:
                    headcrabbedPlayer.addPotionEffect(new PotionEffect(Potion.wither.id, 300,0));
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
        return NightmareUtils.getGameProgressMobsLevel(this.worldObj) > 0 ? 12*(NightmareUtils.getGameProgressMobsLevel(this.worldObj)+1) : 18;
        // pre nether 20, hardmode 24, post wither 36, post dragon 48
    }



    // TENTACLES
    @Inject(method = "updateEntityActionState", at = @At("HEAD"))
    private void lowerTentacleCooldown(CallbackInfo ci){
        --this.tentacleAttackCooldownTimer;
        if(NightmareUtils.getGameProgressMobsLevel(this.worldObj)>1){
            this.tentacleAttackCooldownTimer-=2;
        }
    }


    @ModifyConstant(method = "launchTentacleAttackInDirection", constant = @Constant(intValue = 100),remap = false)
    private int lowerTentacleAttackCooldownTimer(int constant){
        return 100-(NightmareUtils.getGameProgressMobsLevel(this.worldObj)*30);
    }





    // ATTEMPT TO DOUBLE THE SQUID RANGE
    @Shadow(remap = false) private double tentacleAttackTargetX;
    @Shadow(remap = false) private double tentacleAttackTargetY;
    @Shadow(remap = false) private double tentacleAttackTargetZ;

    @Shadow(remap = false)
    public abstract void launchTentacleAttackInDirection(double dUnitVectorToTargetX, double dUnitVectorToTargetY, double dUnitVectorToTargetZ);

    @Inject(method = "launchTentacleAttackInDirection",
            at = @At(value = "FIELD",
                    target = "Lbtw/entity/mob/BTWSquidEntity;tentacleAttackTargetZ:D",
                    shift = At.Shift.AFTER),
            locals = LocalCapture.CAPTURE_FAILHARD)
    private void editTentacleVectors(double dUnitVectorToTargetX, double dUnitVectorToTargetY, double dUnitVectorToTargetZ, CallbackInfo ci){
        this.tentacleAttackTargetX = this.posX + dUnitVectorToTargetX * 8.0;
        this.tentacleAttackTargetY = this.posY + (double)(this.height / 2.0F) + dUnitVectorToTargetY * 8.0;
        this.tentacleAttackTargetZ = this.posZ + dUnitVectorToTargetZ * 8.0;
    }


    @Redirect(method = "updateHeadCrabActionState",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/BTWSquidEntity;attemptTentacleAttackOnTarget()V"))
    // redirecting attemptTentacleAttackOnTarget to execute basically a better version of itself. doubled the range in the
    // if statement, and I made there not be any LOS (line of sight) checks when launching the tentacle.
    private void attemptTentacleAttackOnTarget1(BTWSquidEntity instance) {
        BTWSquidEntity thisObject = (BTWSquidEntity) (Object) this;
        double dDeltaX = thisObject.entityToAttack.posX - this.posX;
        double dDeltaY = thisObject.entityToAttack.posY + (double) (thisObject.entityToAttack.height / 2.0F) - (this.posY + (double) (this.height / 2.0F));
        double dDeltaZ = thisObject.entityToAttack.posZ - this.posZ;
        double dDistSqToTarget = dDeltaX * dDeltaX + dDeltaY * dDeltaY + dDeltaZ * dDeltaZ;
        if (dDistSqToTarget < 80.0) {
            dDeltaY = thisObject.entityToAttack.posY + (double) thisObject.entityToAttack.getEyeHeight() - (this.posY + (double) (this.height / 2.0F));
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
        return NightmareUtils.getGameProgressMobsLevel(this.worldObj) > 0;
    }
    @Redirect(method = "findClosestValidAttackTargetWithinRange", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;isDaytime()Z"))
    private boolean squidAlwaysNightPostNether(World instance){
        if(NightmareUtils.getGameProgressMobsLevel(this.worldObj)>0){
            return false;
        } else return this.worldObj.getWorldTime() % 24000 < 12000; // the % 24000 might not be needed, but I'm not sure
    }

    @Redirect(method = "findClosestValidAttackTargetWithinRange", at = @At(value = "INVOKE", target = "Lbtw/entity/mob/BTWSquidEntity;canEntityBeSeen(Lnet/minecraft/src/Entity;)Z"))
    private boolean canAlwaysSeePlayer(BTWSquidEntity instance, Entity entity){
        return true;
    }

    // making the squid launch tentacles even if it cannot see the player, even if its on land, even if the player is
    // not in water potentially
    @Redirect(method = "updateEntityActionState", at = @At(value = "FIELD", target = "Lbtw/entity/mob/BTWSquidEntity;inWater:Z",ordinal = 0))
    private boolean tentacleEvenIfBeached(BTWSquidEntity instance){
        return true;
    }

    @Redirect(method = "updateEntityActionState", at = @At(value = "INVOKE", target = "Lbtw/entity/mob/BTWSquidEntity;attemptTentacleAttackOnTarget()V"))
    private void attemptTentacleAttackOnTargetBetter2(BTWSquidEntity instance){
        BTWSquidEntity thisObject = (BTWSquidEntity) (Object) this;
        double dDeltaX = thisObject.entityToAttack.posX - this.posX;
        double dDeltaY = thisObject.entityToAttack.posY + (double) (thisObject.entityToAttack.height / 2.0F) - (this.posY + (double) (this.height / 2.0F));
        double dDeltaZ = thisObject.entityToAttack.posZ - this.posZ;
        double dDistSqToTarget = dDeltaX * dDeltaX + dDeltaY * dDeltaY + dDeltaZ * dDeltaZ;
        if (dDistSqToTarget < 80.0) {
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
