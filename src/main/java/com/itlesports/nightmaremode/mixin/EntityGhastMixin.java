package com.itlesports.nightmaremode.mixin;

import btw.world.util.difficulty.Difficulties;
import com.itlesports.nightmaremode.NightmareUtils;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityGhast.class)
public abstract class EntityGhastMixin extends EntityFlying{
    @Shadow public abstract boolean getCanSpawnHereNoPlayerDistanceRestrictions();

    @Unique int rageTimer = 0;

    public EntityGhastMixin(World world) {
        super(world);
    }

    @Inject(method = "applyEntityAttributes", at = @At("TAIL"))
    private void applyAdditionalAttributes(CallbackInfo ci){
        int progress = NightmareUtils.getWorldProgress(this.worldObj);
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(20 + 6 * progress);
        // 20 -> 26 -> 32 -> 38
    }

    @ModifyConstant(method = "attackEntityFrom", constant = @Constant(floatValue = 1000.0f))
    private float ghastEnrageOnSelfHit(float constant){
        rageTimer = 1;
        return 5f;
    }
    @Inject(method = "updateEntityActionState", at =@At("HEAD"))
    private void manageRageState(CallbackInfo ci){
        if(rageTimer > 0){
            rageTimer++;
            EntityPlayer entityTargeted = this.worldObj.getClosestVulnerablePlayerToEntity(this, 100.0);
            if (entityTargeted != null && rageTimer % (this.worldObj.getDifficulty() == Difficulties.HOSTILE ? 4 : 8) == 0) {
                Vec3 ghastLookVec = this.getLook(1.0f);
                double dFireballX = this.posX + ghastLookVec.xCoord;
                double dFireballY = this.posY + (double)(this.height / 2.0f);
                double dFireballZ = this.posZ + ghastLookVec.zCoord;
                double dDeltaX = entityTargeted.posX - dFireballX;
                double dDeltaY = entityTargeted.posY + (double)entityTargeted.getEyeHeight() - dFireballY;
                double dDeltaZ = entityTargeted.posZ - dFireballZ;
                EntityLargeFireball fireball = new EntityLargeFireball(this.worldObj, this, dDeltaX, dDeltaY, dDeltaZ);
                fireball.field_92057_e = 1;
                double dDeltaLength = MathHelper.sqrt_double(dDeltaX * dDeltaX + dDeltaY * dDeltaY + dDeltaZ * dDeltaZ);
                double dUnitDeltaX = dDeltaX / dDeltaLength;
                double dUnitDeltaY = dDeltaY / dDeltaLength;
                double dUnitDeltaZ = dDeltaZ / dDeltaLength;
                fireball.posX = dFireballX + dUnitDeltaX * 4.0;
                fireball.posY = dFireballY + dUnitDeltaY * 4.0 - (double)fireball.height / 2.0;
                fireball.posZ = dFireballZ + dUnitDeltaZ * 4.0;
                this.worldObj.spawnEntityInWorld(fireball);
            }
        }
        if(rageTimer > 100){
            rageTimer = 0;
        }
    }
    @Inject(method = "getCanSpawnHere", at = @At("HEAD"),cancellable = true)
    private void manageOverworldSpawn(CallbackInfoReturnable<Boolean> cir){
        if(this.dimension == 0){
            if (NightmareUtils.getIsBloodMoon()) {
                if (this.getCanSpawnHereNoPlayerDistanceRestrictions() && this.posY >= 63) {
                    cir.setReturnValue(true);
                }
            } else{
                cir.setReturnValue(false);
            }
        }
    }

    @Inject(method = "onUpdate", at = @At("TAIL"))
    private void killIfTooHigh(CallbackInfo ci){
        if(this.dimension == 0 && this.posY >= 200){
            this.setDead();
        }
    }

//    @Inject(method = "onUpdate", at = @At("HEAD"))
//    private void manageOverworldBehavior(CallbackInfo ci){
//        EntityGhast thisObj = (EntityGhast)(Object)this;
//        EntityPlayer player = thisObj.worldObj.getClosestVulnerablePlayerToEntity(thisObj,100);
//        if(player == null){thisObj.setInvisible(true);}
//        else if(thisObj.dimension == 0 && thisObj.getEntitySenses().canSee(player)){
//            thisObj.setInvisible(false);
//            thisObj.getLookHelper().setLookPositionWithEntity(player,0,0);
//            thisObj.motionX = 0;
//            thisObj.motionY = 0;
//            thisObj.motionZ = 0;
//        }
//    }

    @ModifyConstant(method = "updateEntityActionState",constant = @Constant(intValue = 10,ordinal = 0))
    private int lowerSoundThreshold(int constant){
        EntityGhast thisObj = (EntityGhast)(Object)this;
        if(thisObj.dimension == 0){
            return constant*2;
        }
        if(thisObj.worldObj != null && NightmareUtils.getWorldProgress(thisObj.worldObj)>0){
            return constant - NightmareUtils.getWorldProgress(thisObj.worldObj)*2 -1;
            // 9 -> 7 -> 4 -> 2
        }
        return constant;
    }
    @ModifyConstant(method = "updateEntityActionState",constant = @Constant(intValue = 20,ordinal = 1))
    private int lowerAttackThreshold(int constant){
        EntityGhast thisObj = (EntityGhast)(Object)this;
        if(thisObj.dimension == 0){
            return constant*2;
        }
        if(thisObj.worldObj != null && NightmareUtils.getWorldProgress(thisObj.worldObj)>0){
            return constant - NightmareUtils.getWorldProgress(thisObj.worldObj)*3 - 5;
            // 15 -> 12 -> 9 -> 6
        }
        return constant;
    }

    @ModifyConstant(method = "fireAtTarget", constant = @Constant(intValue = -40))
    private int lowerAttackCooldownOnFire(int constant){
        EntityGhast thisObj = (EntityGhast)(Object)this;
        return -10 - thisObj.rand.nextInt(21);
        // from -10 to -30
    }
}
