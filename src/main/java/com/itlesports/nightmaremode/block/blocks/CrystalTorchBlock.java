package com.itlesports.nightmaremode.block.blocks;

import api.world.WorldUtils;
import btw.block.blocks.TorchBlockBase;
import com.itlesports.nightmaremode.block.tileEntities.CrystalTorchTileEntity;
import com.itlesports.nightmaremode.item.NMItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;
import java.util.Random;

public class CrystalTorchBlock extends TorchBlockBase implements ITileEntityProvider {
    private boolean submerging;

    public CrystalTorchBlock(int id) {
        super(id);
        this.isBlockContainer = true;
        this.setLightValue(1.0F);
        this.setUnlocalizedName("ifhyCrystalTorch");
        this.setTextureName("nightmare:ifhyCrystalTorch");
        this.setTickRandomly(false);
    }

    @Override public String getModId() { return "nightmare"; }
    @Override public int idDropped(int metadata, Random random, int fortune) { return NMItems.crystalTorch.itemID; }
    @Override public int idPicked(World world, int x, int y, int z) { return NMItems.crystalTorch.itemID; }
    @Override public int damageDropped(int metadata) { return 0; }
    @Override public boolean hasTileEntity() { return true; }
    @Override public TileEntity createNewTileEntity(World world) { return new CrystalTorchTileEntity(); }
    @Override public int getLightValue(IBlockAccess world, int x, int y, int z) {
        return (world.getBlockMetadata(x, y, z) & 8) == 0 ? 15 : 0;
    }

    @Override public void dropBlockAsItemWithChance(World world, int x, int y, int z, int metadata, float chance, int fortune) {}

    @Override public void breakBlock(World world, int x, int y, int z, int oldId, int oldMetadata) {
        TileEntity tile = world.getBlockTileEntity(x, y, z);
        if (!world.isRemote && !this.submerging && (oldMetadata & 8) == 0
                && tile instanceof CrystalTorchTileEntity torch && torch.getRemainingBurnTicks() > 0) {
            this.dropBlockAsItem_do(world, x, y, z, torch.createTorchDrop());
        }
        super.breakBlock(world, x, y, z, oldId, oldMetadata);
        world.removeBlockTileEntity(x, y, z);
    }

    public boolean submerge(World world, int x, int y, int z, int waterMetadata) {
        int orientation = world.getBlockMetadata(x, y, z) & 7;
        TileEntity tile = world.getBlockTileEntity(x, y, z);
        int remaining = tile instanceof CrystalTorchTileEntity torch
                ? torch.getRemainingBurnTicks() : CrystalTorchTileEntity.MAX_BURN_TICKS;
        this.submerging = true;
        try {
            if (!SubmergedCrystalTorchBlock.place(world, x, y, z, waterMetadata, orientation)) return false;
            if (world.getBlockTileEntity(x, y, z) instanceof CrystalTorchTileEntity torch) torch.setRemainingBurnTicks(remaining);
            return true;
        } finally {
            this.submerging = false;
        }
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderer, int x, int y, int z) {
        int metadata = renderer.blockAccess.getBlockMetadata(x, y, z);
        Tessellator.instance.setBrightness(this.getMixedBrightnessForBlock(renderer.blockAccess, x, y, z));
        float shade = (metadata & 8) == 0 ? 1.0F : 0.25F;
        Tessellator.instance.setColorOpaque_F(shade, shade, shade);
        renderTorch(renderer, this, x, y, z, metadata & 7, metadata);
        return true;
    }

    @Environment(EnvType.CLIENT)
    public static void renderTorch(RenderBlocks renderer, Block block, double x, double y, double z, int orientation, int metadata) {
        // use the normal torch mesh, lifting its bottom cap clear of the support face.
        y += 0.001D;
        if (orientation == 1) renderer.renderTorchAtAngle(block, x - 0.1D, y + 0.2D, z, -0.4D, 0, metadata);
        else if (orientation == 2) renderer.renderTorchAtAngle(block, x + 0.1D, y + 0.2D, z, 0.4D, 0, metadata);
        else if (orientation == 3) renderer.renderTorchAtAngle(block, x, y + 0.2D, z - 0.1D, 0, -0.4D, metadata);
        else if (orientation == 4) renderer.renderTorchAtAngle(block, x, y + 0.2D, z + 0.1D, 0, 0.4D, metadata);
        else renderer.renderTorchAtAngle(block, x, y, z, 0, 0, metadata);
    }

    public static boolean isSupported(World world, int x, int y, int z, int orientation) {
        int facing = 6 - orientation;
        if (orientation < 1 || orientation > 5) return false;
        int side = Block.getOppositeFacing(facing);
        int supportX = x + Facing.offsetsXForSide[side];
        int supportY = y + Facing.offsetsYForSide[side];
        int supportZ = z + Facing.offsetsZForSide[side];
        return facing == 1
                ? WorldUtils.doesBlockHaveSmallCenterHardpointToFacing(world, supportX, supportY, supportZ, facing, true)
                : WorldUtils.doesBlockHaveCenterHardpointToFacing(world, supportX, supportY, supportZ, facing, true);
    }

    public static AxisAlignedBB torchBounds(int orientation) {
        if (orientation == 1) return AxisAlignedBB.getAABBPool().getAABB(0, 0.2, 0.35, 0.3, 0.8, 0.65);
        if (orientation == 2) return AxisAlignedBB.getAABBPool().getAABB(0.7, 0.2, 0.35, 1, 0.8, 0.65);
        if (orientation == 3) return AxisAlignedBB.getAABBPool().getAABB(0.35, 0.2, 0, 0.65, 0.8, 0.3);
        if (orientation == 4) return AxisAlignedBB.getAABBPool().getAABB(0.35, 0.2, 0.7, 0.65, 0.8, 1);
        return AxisAlignedBB.getAABBPool().getAABB(0.4, 0, 0.4, 0.6, 0.6, 0.6);
    }
}
