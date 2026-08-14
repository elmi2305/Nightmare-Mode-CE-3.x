package com.itlesports.nightmaremode.mixin.blocks;

import net.minecraft.src.BlockEndPortal;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityItem;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEndPortal.class)
public class BlockEndPortalMixin {
    /** End freight is never tied to the Nether entity-transport unlock. */
    @Inject(method = "onEntityCollidedWithBlock", at = @At("HEAD"), cancellable = true)
    private void alwaysTransportItemEntities(World world, int x, int y, int z, Entity entity, CallbackInfo ci) {
        if (entity instanceof EntityItem && entity.ridingEntity == null && entity.riddenByEntity == null && !world.isRemote) {
            entity.travelToDimension(1);
            ci.cancel();
        }
    }
    @Redirect(method = "onBlockAdded", at = @At(value = "INVOKE", target = "Lapi/world/WorldUtils;gameProgressSetEndDimensionHasBeenAccessedServerOnly()V", remap = false))
    private void doNotActivatePostDragonOnPortalActivation(){}
    @Redirect(method = "updateTick", at = @At(value = "INVOKE", target = "Lapi/world/WorldUtils;gameProgressSetEndDimensionHasBeenAccessedServerOnly()V", remap = false))
    private void doNotActivatePostDragonOnPortalActivationEveryTick(){}
}
