package com.itlesports.nightmaremode.mixin;

import btw.community.nightmaremode.NightmareMode;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityAITarget.class)
public abstract class EntityAITargetMixin extends EntityAIBase {
    @Shadow private int targetSearchStatus;
    @Shadow protected boolean shouldCheckSight;
    @Shadow protected boolean ignoreTargetsOutsideHome;

    @Inject(method = "canEasilyReach", at = @At("HEAD"),cancellable = true)
    private void canAlwaysReach(EntityLivingBase par1EntityLivingBase, CallbackInfoReturnable<Boolean> cir){
        if (NightmareMode.hordeMode) {
            cir.setReturnValue(true);
        }
    }
    @Redirect(method = "continueExecuting", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityCreature;getDistanceSqToEntity(Lnet/minecraft/src/Entity;)D"))
    private double bypassDistanceLimit(EntityCreature instance, Entity entity){
        return NightmareMode.hordeMode ? 0d : instance.getDistanceSqToEntity(entity);
    }

    @Inject(method = "isSuitableTarget", at = @At(value = "HEAD"), cancellable = true)
    private void ensurePlayerTargetting(EntityLivingBase par1EntityLivingBase, boolean par2, CallbackInfoReturnable<Boolean> cir){
        if (NightmareMode.hordeMode) {
            if (par1EntityLivingBase instanceof EntityPlayer) {
                this.targetSearchStatus = 1;
                this.shouldCheckSight = false;
                this.ignoreTargetsOutsideHome = false;
                cir.setReturnValue(true);
            }
        }
    }
}
