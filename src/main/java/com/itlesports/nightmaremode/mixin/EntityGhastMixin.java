package com.itlesports.nightmaremode.mixin;

import btw.world.util.difficulty.Difficulties;
import com.itlesports.nightmaremode.NightmareUtils;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityGhast.class)
public class EntityGhastMixin {
    @Unique int rageTimer = 0;
    @Inject(method = "applyEntityAttributes", at = @At("TAIL"))
    private void applyAdditionalAttributes(CallbackInfo ci){
        EntityGhast thisObj = (EntityGhast)(Object)this;
        int progress = NightmareUtils.getGameProgressMobsLevel(thisObj.worldObj);
        thisObj.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(20 + 6*progress);
        // 20 -> 26 -> 32 -> 38
    }

    @ModifyConstant(method = "attackEntityFrom", constant = @Constant(floatValue = 1000.0f))
    private float ghastEnrageOnSelfHit(float constant){
        rageTimer = 1;
        return 5f;
    }
    @Inject(method = "updateEntityActionState", at =@At("HEAD"))
    private void manageRageState(CallbackInfo ci){
        EntityGhast thisObj = (EntityGhast)(Object)this;
        if(rageTimer > 0){
            rageTimer++;
            EntityPlayer entityTargeted = thisObj.worldObj.getClosestVulnerablePlayerToEntity(thisObj, 100.0);
            if (entityTargeted != null && rageTimer % (thisObj.worldObj.getDifficulty() == Difficulties.HOSTILE ? 4 : 8) == 0) {
                Vec3 ghastLookVec = thisObj.getLook(1.0f);
                double dFireballX = thisObj.posX + ghastLookVec.xCoord;
                double dFireballY = thisObj.posY + (double)(thisObj.height / 2.0f);
                double dFireballZ = thisObj.posZ + ghastLookVec.zCoord;
                double dDeltaX = entityTargeted.posX - dFireballX;
                double dDeltaY = entityTargeted.posY + (double)entityTargeted.getEyeHeight() - dFireballY;
                double dDeltaZ = entityTargeted.posZ - dFireballZ;
                EntityLargeFireball fireball = new EntityLargeFireball(thisObj.worldObj, thisObj, dDeltaX, dDeltaY, dDeltaZ);
                fireball.field_92057_e = 1;
                double dDeltaLength = MathHelper.sqrt_double(dDeltaX * dDeltaX + dDeltaY * dDeltaY + dDeltaZ * dDeltaZ);
                double dUnitDeltaX = dDeltaX / dDeltaLength;
                double dUnitDeltaY = dDeltaY / dDeltaLength;
                double dUnitDeltaZ = dDeltaZ / dDeltaLength;
                fireball.posX = dFireballX + dUnitDeltaX * 4.0;
                fireball.posY = dFireballY + dUnitDeltaY * 4.0 - (double)fireball.height / 2.0;
                fireball.posZ = dFireballZ + dUnitDeltaZ * 4.0;
                thisObj.worldObj.spawnEntityInWorld(fireball);
            }
        }
        if(rageTimer > 100){
            rageTimer = 0;
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
        if(thisObj.worldObj != null && NightmareUtils.getGameProgressMobsLevel(thisObj.worldObj)>0){
            return constant - NightmareUtils.getGameProgressMobsLevel(thisObj.worldObj)*2 -1;
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
        if(thisObj.worldObj != null && NightmareUtils.getGameProgressMobsLevel(thisObj.worldObj)>0){
            return constant - NightmareUtils.getGameProgressMobsLevel(thisObj.worldObj)*3 - 5;
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
