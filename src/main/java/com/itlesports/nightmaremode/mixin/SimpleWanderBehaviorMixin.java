package com.itlesports.nightmaremode.mixin;

import btw.entity.mob.behavior.SimpleWanderBehavior;
import btw.world.util.BlockPos;
import com.itlesports.nightmaremode.NMUtils;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SimpleWanderBehavior.class)
public class SimpleWanderBehaviorMixin {

    @Shadow private EntityCreature myEntity;
    @Shadow protected BlockPos destPos;
    @Unique private EntityPlayer lastCachedPlayer = null;

    @Inject(method = "shouldExecute", at = @At("HEAD"), cancellable = true)
    private void injectShouldExecute(CallbackInfoReturnable<Boolean> cir) {
        if(!(myEntity instanceof EntityMob)) return;

        if (this.myEntity.getRNG().nextInt(80) != 0) return;

        int chanceToDoSmartCheck = this.getChanceForSmartCheck();

        if(this.myEntity.getRNG().nextInt(chanceToDoSmartCheck) != 0) return;

        EntityPlayer nearestPlayer;
        // find the nearest player
        if(lastCachedPlayer == null || this.myEntity.getRNG().nextInt(20) == 0){
            if(this.myEntity.posY < 40) return;
            nearestPlayer = this.myEntity.worldObj.getClosestPlayerToEntity(this.myEntity, 256);
            lastCachedPlayer = nearestPlayer;
        } else{
            nearestPlayer = lastCachedPlayer;
        }

        if (nearestPlayer != null) {
            Vec3 playerVec = Vec3.createVectorHelper(
                    nearestPlayer.posX,
                    nearestPlayer.posY,
                    nearestPlayer.posZ
            );

            // attempt to find a random target toward that player
            Vec3 targetVec = RandomPositionGenerator.findRandomTargetBlockTowards(this.myEntity, 10, 7, playerVec);

            if (targetVec != null) {
                this.destPos = new BlockPos((int) targetVec.xCoord, (int) targetVec.yCoord, (int) targetVec.zCoord);
                cir.setReturnValue(true);
            }
        }
    }

    @Unique private int getChanceForSmartCheck(){
        int progress = NMUtils.getWorldProgress();
        int baseChance = 36;
                                                        //      b    b/2  b/3  b/4
        int tempChance = baseChance / (progress + 1);    //     36,  18,  12,  9
        if(NMUtils.getIsBloodMoon()) {tempChance /= 2;}  //     18,   9,   6,  4
        if(NMUtils.getIsEclipse()) {tempChance /= 4;}    //     9,   4,   3,   2

        return tempChance;

    }
}
