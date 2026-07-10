package com.itlesports.nightmaremode.mixin.blocks;

import btw.block.blocks.MillstoneBlock;
import btw.block.tileentity.MillstoneTileEntity;
import net.minecraft.src.Potion;
import net.minecraft.src.TileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MillstoneTileEntity.class)
public abstract class MillstoneTileEntityMixin extends TileEntity {


    @ModifyConstant(method = "updateEntity", constant = @Constant(intValue = 200))
    private int fasterMillstones(int constant){
        return constant * 8;
    }
}
