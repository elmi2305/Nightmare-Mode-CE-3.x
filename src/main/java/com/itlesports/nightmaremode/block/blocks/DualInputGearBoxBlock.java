package com.itlesports.nightmaremode.block.blocks;

import api.block.util.MechPowerUtils;
import btw.block.blocks.GearBoxBlock;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.Block;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.Icon;
import net.minecraft.src.World;

/** A gearbox whose front and back faces are inputs and whose other faces output. */
public class DualInputGearBoxBlock extends GearBoxBlock {
    public DualInputGearBoxBlock(int blockId) {
        super(blockId);
        this.setUnlocalizedName("ifhyDualInputGearBox");
        this.setTextureName("btw:gearbox");
    }

    @Override
    public boolean isInputtingMechanicalPower(World world, int x, int y, int z) {
        int facing = this.getFacing(world, x, y, z);
        return MechPowerUtils.isBlockPoweredByAxleToSide(world, x, y, z, facing)
                || MechPowerUtils.isBlockPoweredByAxleToSide(world, x, y, z, Block.getOppositeFacing(facing));
    }

    @Override
    public boolean canInputAxlePowerToFacing(World world, int x, int y, int z, int facing) {
        int blockFacing = this.getFacing(world, x, y, z);
        return facing == blockFacing || facing == Block.getOppositeFacing(blockFacing);
    }

    @Override
    public int getMechanicalPowerLevelProvidedToAxleAtFacing(World world, int x, int y, int z, int facing) {
        int inputFacing = this.getFacing(world, x, y, z);
        if (this.isGearBoxOn(world, x, y, z)
                && facing != inputFacing && facing != Block.getOppositeFacing(inputFacing)) {
            return 4;
        }
        return 0;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getBlockTexture(IBlockAccess access, int x, int y, int z, int side) {
        int facing = this.getFacing(access, x, y, z);
        if (side == facing || side == Block.getOppositeFacing(facing)) {
            // Asking the base implementation for the marked face returns its input texture.
            return super.getBlockTexture(access, x, y, z, facing);
        }
        return super.getBlockTexture(access, x, y, z, side);
    }
}
