package com.itlesports.nightmaremode.mixin;

import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PathFinder.class)
public abstract class PathFinderMixin implements PathFinderInvoker{
    @Shadow private boolean isPathingInWater;

    @Shadow public abstract int getVerticalOffset(Entity par1Entity, int par2, int par3, int par4, PathPoint par5PathPoint);

    @Shadow protected abstract PathPoint openPoint(int par1, int par2, int par3);


    @Inject(method = "getSafePoint", at = @At(value = "RETURN", ordinal = 0), cancellable = true)
    private void getSafePointImprovement(Entity par1Entity, int par2, int par3, int par4, PathPoint par5PathPoint, int par6, CallbackInfoReturnable<PathPoint> cir){
        if(this.getVerticalOffset(par1Entity,par2,par3 + 3,par4, par5PathPoint) == 1){
            cir.setReturnValue(this.openPoint(par2,par3 + 1,par4));
        } else{
            cir.setReturnValue(this.openPoint(par2,par3,par4));
        }
    }



//    @Redirect(method = "findPathOptions", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/PathFinder;getSafePoint(Lnet/minecraft/src/Entity;IIILnet/minecraft/src/PathPoint;I)Lnet/minecraft/src/PathPoint;"))
//    private PathPoint getSafePointFix(PathFinder instance, Entity entity, int x, int y, int z, PathPoint target, int verticalStep) {
//        PathPoint safePoint = null;
//        int verticalOffset = instance.getVerticalOffset(entity, x, y, z, target);
//
//        // If it's completely passable, return the point directly
//        if (verticalOffset == 2) {
//            return this.invokeOpenPoint(x, y, z);
//        }
//
//        // If it's walkable, mark it as a candidate
//        if (verticalOffset == 1) {
//            safePoint = this.invokeOpenPoint(x, y, z);
//        }
//
//        // Try checking a point vertically offset upwards (e.g., stairs or stepping up)
//        boolean canStepUp = verticalOffset != -3 && verticalOffset != -4;
//        if (safePoint == null && verticalStep > 0 && canStepUp) {
//            int aboveOffset = instance.getVerticalOffset(entity, x, y + verticalStep, z, target);
//            if (aboveOffset == 1) {
//                safePoint = this.invokeOpenPoint(x, y + verticalStep, z);
//                y += verticalStep;
//            }
//        }
//
//        // If a valid point was found, try walking downward safely
//        if (safePoint != null) {
//            int attempts = 0;
//            int downwardOffset = 0;
//
//            while (y > 0) {
//                downwardOffset = instance.getVerticalOffset(entity, x, y - 1, z, target);
//
//                if (isPathingInWater && downwardOffset == -1) {
//                    return null;
//                }
//
//                if (downwardOffset != 1) {
//                    break;
//                }
//
//                if (++attempts >= entity.getMaxSafePointTries()) {
//                    return null;
//                }
//
//                y--;
//                safePoint = this.invokeOpenPoint(x, y, z);
//            }
//
//            if (downwardOffset == -2) {
//                return null;
//            }
//        }
//
//        return safePoint;
//    }
}
