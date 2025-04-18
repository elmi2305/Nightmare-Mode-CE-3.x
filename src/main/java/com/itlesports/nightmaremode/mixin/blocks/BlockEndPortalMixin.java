package com.itlesports.nightmaremode.mixin.blocks;

import net.minecraft.src.BlockEndPortal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockEndPortal.class)
public class BlockEndPortalMixin {
    @Redirect(method = "onBlockAdded", at = @At(value = "INVOKE", target = "Lbtw/world/util/WorldUtils;gameProgressSetEndDimensionHasBeenAccessedServerOnly()V"))
    private void doNotActivatePostDragonOnPortalActivation(){}
    @Redirect(method = "updateTick", at = @At(value = "INVOKE", target = "Lbtw/world/util/WorldUtils;gameProgressSetEndDimensionHasBeenAccessedServerOnly()V"))
    private void doNotActivatePostDragonOnPortalActivationEveryTick(){}
}
