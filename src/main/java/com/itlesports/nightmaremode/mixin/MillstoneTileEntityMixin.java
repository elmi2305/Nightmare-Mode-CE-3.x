package com.itlesports.nightmaremode.mixin;

import btw.block.blocks.MillstoneBlock;
import btw.block.tileentity.MillstoneTileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MillstoneTileEntity.class)
public class MillstoneTileEntityMixin {
    @Redirect(method = "updateEntity"
            ,at = @At(value = "INVOKE",
            target = "Lbtw/block/tileentity/MillstoneTileEntity;checkForNauseateNearbyPlayers(Lbtw/block/blocks/MillstoneBlock;)V"),remap = false)
    private void doNothing(MillstoneTileEntity instance, MillstoneBlock block){}
    @ModifyConstant(method = "updateEntity", constant = @Constant(intValue = 200))
    private int fasterMillstones(int constant){
        return 100;
    }
}
