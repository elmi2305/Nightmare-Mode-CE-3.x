package com.itlesports.nightmaremode.mixin;

import btw.entity.SpiderWebEntity;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(EntitySlime.class)
public abstract class EntitySlimeMixin {
    @Shadow protected abstract void setSlimeSize(int iSize);
    @Unique private int timeSpentTargeting = 60;
    @Unique private float streakModifier = 1;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void changeSize(World par1World, CallbackInfo ci){
        EntitySlime thisObj = (EntitySlime)(Object)this;
        if(thisObj instanceof EntityMagmaCube magmaCube && thisObj.dimension==0) {
            this.setSlimeSize(2);
            magmaCube.addPotionEffect(new PotionEffect(Potion.fireResistance.id, 10000000,0));
        }
    }


    @Inject(method = "updateEntityActionState",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/src/EntitySlime;faceEntity(Lnet/minecraft/src/Entity;FF)V",
                    shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    private void checkIfShouldShootPlayer(CallbackInfo ci, EntityPlayer targetPlayer){
        timeSpentTargeting++;
        EntitySlime thisObj = (EntitySlime)(Object)this;
        if (thisObj.ridingEntity == null) {
            if(timeSpentTargeting%110==109){
                if (!thisObj.worldObj.isRemote && thisObj.getSlimeSize()>=2) {
                    if(thisObj instanceof EntityMagmaCube && thisObj.dimension!=0){
                        EntityLivingBase target = thisObj.getAITarget();
                        if (target != null) {
                            double var3 = target.posX - thisObj.posX;
                            double var5 = target.boundingBox.minY + (double) (target.height / 2.0F) - (thisObj.posY + (double) (thisObj.height / 2.0F));
                            double var7 = target.posZ - thisObj.posZ;

                            EntitySmallFireball var11 = new EntitySmallFireball(thisObj.worldObj, thisObj, var3, var5, var7);
                            thisObj.worldObj.playAuxSFXAtEntity(null, 1009, (int)thisObj.posX, (int)thisObj.posY, (int)thisObj.posZ, 0);
                            var11.posY = thisObj.posY + (double) (thisObj.height / 2.0f) + 0.5;

                            thisObj.worldObj.spawnEntityInWorld(var11);
                        }
                    } else {
                        thisObj.worldObj.spawnEntityInWorld(new SpiderWebEntity(thisObj.worldObj, thisObj, targetPlayer));
                    }
                    timeSpentTargeting = thisObj.rand.nextInt(80);
                }
            }
        }
    }

    @Inject(method = "jump", at = @At("TAIL"))
    private void chanceToSpawnSlimeOnJump(CallbackInfo ci){
        EntitySlime thisObj = (EntitySlime)(Object)this;
        if (thisObj.getSlimeSize() >= 2){
            if(thisObj.rand.nextFloat()<0.5 / streakModifier){
                EntitySlime baby = new EntitySlime(thisObj.worldObj);
                int size = thisObj.getSlimeSize();
                baby.getDataWatcher().updateObject(16, (byte)(size/2));
                baby.setPositionAndUpdate(thisObj.posX,thisObj.posY,thisObj.posZ);
                thisObj.worldObj.spawnEntityInWorld(baby);
                streakModifier += 1+(float) thisObj.getSlimeSize();
            }
        }
    }
}
