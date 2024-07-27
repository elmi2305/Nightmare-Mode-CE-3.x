package com.itlesports.nightmaremode.mixin;

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
        if(rageTimer>0){
            rageTimer++;
            EntityPlayer entityTargeted = thisObj.worldObj.getClosestVulnerablePlayerToEntity(thisObj, 100.0);
            if (entityTargeted != null && rageTimer % 4 == 0) {
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
}
