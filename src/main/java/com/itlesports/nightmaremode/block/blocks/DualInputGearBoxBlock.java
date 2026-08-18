package com.itlesports.nightmaremode.block.blocks;

import api.block.util.MechPowerUtils;
import api.world.BlockPos;
import btw.block.BTWBlocks;
import btw.block.blocks.AxleBlock;
import btw.block.blocks.GearBoxBlock;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

/** A gearbox whose front and back faces are inputs and whose other faces output. */
public class DualInputGearBoxBlock extends GearBoxBlock {
    @Environment(EnvType.CLIENT)
    private Icon iconInput;
    @Environment(EnvType.CLIENT)
    private Icon iconOutput;

    public DualInputGearBoxBlock(int blockId) {
        super(blockId);
        this.setUnlocalizedName("ifhyDualInputGearBox");
        this.setTextureName("nightmare:ifhyDoubleGearbox");
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

//    @Override
//    @Environment(EnvType.CLIENT)
//    public Icon getBlockTexture(IBlockAccess access, int x, int y, int z, int side) {
//        int facing = this.getFacing(access, x, y, z);
//        if (side == facing || side == Block.getOppositeFacing(facing)) {
//            // Asking the base implementation for the marked face returns its input texture.
//            return super.getBlockTexture(access, x, y, z, facing);
//        }
//        return super.getBlockTexture(access, x, y, z, side);
//    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void registerIcons(IconRegister register) {
        super.registerIcons(register);
        this.iconInput = register.registerIcon(this.getTextureName() + "_input");
        this.iconOutput = register.registerIcon(this.getTextureName() + "_output");
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public Icon getIcon(int iSide, int iMetadata) {
        if (iSide == 3) {
            return this.iconInput;
        }
        return this.blockIcon;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public Icon getBlockTexture(IBlockAccess blockAccess, int i, int j, int k, int iSide) {
        int iFacing = this.getFacing(blockAccess, i, j, k);
        if (iSide == iFacing || iSide == Block.getOppositeFacing(iFacing)) {
            return this.iconInput;
        }
        BlockPos sideBlockPos = new BlockPos(i, j, k);
        sideBlockPos.addFacingAsOffset(iSide);
        if (blockAccess.getBlockId(sideBlockPos.x, sideBlockPos.y, sideBlockPos.z) == BTWBlocks.axle.blockID && ((AxleBlock)BTWBlocks.axle).isAxleOrientedTowardsFacing(blockAccess, sideBlockPos.x, sideBlockPos.y, sideBlockPos.z, iSide)) {
            return this.iconOutput;
        }
        return this.blockIcon;
    }
}
