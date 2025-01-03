package com.itlesports.nightmaremode.mixin;

import btw.entity.SpiderWebEntity;
import btw.world.util.difficulty.Difficulties;
import com.itlesports.nightmaremode.NightmareUtils;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(EntitySlime.class)
public abstract class EntitySlimeMixin {

    @Unique private int timeSpentTargeting = 60;
    @Unique private float streakModifier = 1;
    @Unique private float splitCounter = 0;

//    @ModifyArg(method = "checkForScrollDrop", at = @At(value = "INVOKE", target = "Ljava/util/Random;nextInt(I)I"))
//    private int increaseScrollRates(int bound){
//        return 150;
//    }
    @Inject(method = "checkForScrollDrop", at = @At("HEAD"),cancellable = true)
    private void noScrollDrops(CallbackInfo ci){
        ci.cancel();
    }
    @Inject(method = "updateEntityActionState",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/src/EntitySlime;faceEntity(Lnet/minecraft/src/Entity;FF)V",
                    shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    private void checkIfShouldShootPlayer(CallbackInfo ci, EntityPlayer targetPlayer){
        timeSpentTargeting++;
        EntitySlime thisObj = (EntitySlime)(Object)this;
        if (thisObj.ridingEntity == null) {
            if(timeSpentTargeting >= 110){
                if (!thisObj.worldObj.isRemote && thisObj.getSlimeSize()>=2) {
                    if(thisObj instanceof EntityMagmaCube && thisObj.dimension != 0){
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
                    timeSpentTargeting = thisObj.rand.nextInt(40);
                }
            }
        }
    }

    @Redirect(method = "getCanSpawnHere", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;getCurrentMoonPhaseFactor()F"))
    private float slimeBloodMoon(World world){
        if(NightmareUtils.getIsBloodMoon()){
            return 0;
        }
        return world.getCurrentMoonPhaseFactor();
    }

    @Inject(method = "jump", at = @At("TAIL"))
    private void chanceToSpawnSlimeOnJump(CallbackInfo ci){
        EntitySlime thisObj = (EntitySlime)(Object)this;

        if (thisObj.getSlimeSize() >= 2 && this.splitCounter < 3){
            if(thisObj.rand.nextFloat() < 0.5 / this.streakModifier){
                EntitySlime baby = new EntitySlime(thisObj.worldObj);
                baby.getDataWatcher().updateObject(16, (byte)(thisObj.getSlimeSize()/2)); // makes the newly spawned slime half the size of the current one
                baby.setHealth(baby.getSlimeSize());
                baby.setPositionAndUpdate(thisObj.posX,thisObj.posY,thisObj.posZ);
                thisObj.worldObj.spawnEntityInWorld(baby);
                this.streakModifier += 1 + (float)thisObj.getSlimeSize() + (thisObj.worldObj.getDifficulty() == Difficulties.HOSTILE ? 0 : 2);
                this.splitCounter += 1;
            }
        }
    }
}
