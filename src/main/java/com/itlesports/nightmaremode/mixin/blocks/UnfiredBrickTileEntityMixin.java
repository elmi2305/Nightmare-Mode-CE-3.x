package com.itlesports.nightmaremode.mixin.blocks;

import btw.block.tileentity.UnfiredBrickTileEntity;
import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.util.NMUtils;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(UnfiredBrickTileEntity.class)
public class UnfiredBrickTileEntityMixin extends TileEntity {

    @ModifyConstant(method = "updateCooking",
            constant = @Constant(
    intValue = 11900),remap = false)
    private int reduceClayCookTime(int constant){
        return 36000;
    }

}
