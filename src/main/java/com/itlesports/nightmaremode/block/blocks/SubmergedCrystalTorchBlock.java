package com.itlesports.nightmaremode.block.blocks;

import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.block.tileEntities.CrystalTorchTileEntity;
import com.itlesports.nightmaremode.item.NMItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;
import java.util.Random;

public class SubmergedCrystalTorchBlock extends BlockFluid implements ITileEntityProvider {
    public SubmergedCrystalTorchBlock(int id) {
        super(id, Material.water);
        this.isBlockContainer = true;
        this.setHardness(0.0F);
        this.setLightValue(1.0F);
        this.setLightOpacity(1);
        this.setTickRandomly(false);
        this.setUnlocalizedName("ifhyCrystalTorch");
    }

    @Override public String getModId() { return "nightmare"; }
    @Override public boolean hasTileEntity() { return true; }
    @Override public TileEntity createNewTileEntity(World world) { return new CrystalTorchTileEntity(); }
    @Override public boolean canCollideCheck(int metadata, boolean hitLiquids) { return true; }
    @Override public int idDropped(int metadata, Random random, int fortune) { return NMItems.crystalTorch.itemID; }
    @Override public int quantityDropped(Random random) { return 1; }
    @Override public int damageDropped(int metadata) { return 0; }
    @Override public int idPicked(World world, int x, int y, int z) { return NMItems.crystalTorch.itemID; }
    @Override public void onBlockAdded(World world, int x, int y, int z) {}

    @Override public void breakBlock(World world, int x, int y, int z, int oldId, int oldMetadata) {
        if (!world.isRemote && world.getBlockTileEntity(x, y, z) instanceof CrystalTorchTileEntity torch) {
            this.dropBlockAsItem_do(world, x, y, z, torch.createTorchDrop());
        }
        super.breakBlock(world, x, y, z, oldId, oldMetadata);
        world.removeBlockTileEntity(x, y, z);
    }

    @Override public void dropBlockAsItemWithChance(World world, int x, int y, int z, int metadata, float chance, int fortune) {}

    public int getTorchOrientation(IBlockAccess world, int x, int y, int z) {
        TileEntity tile = world.getBlockTileEntity(x, y, z);
        return tile instanceof CrystalTorchTileEntity torch ? torch.getOrientation() : 5;
    }

    public static boolean place(World world, int x, int y, int z, int waterMetadata, int orientation) {
        if (!world.setBlock(x, y, z, NMBlocks.submergedCrystalTorch.blockID, waterMetadata, 2)) return false;
        TileEntity tile = world.getBlockTileEntity(x, y, z);
        if (tile instanceof CrystalTorchTileEntity torch) torch.setOrientation(orientation);
        world.notifyBlocksOfNeighborChange(x, y, z, NMBlocks.submergedCrystalTorch.blockID);
        return true;
    }

    @Override public void onNeighborBlockChange(World world, int x, int y, int z, int neighbor) {
        if (!world.isRemote && !CrystalTorchBlock.isSupported(world, x, y, z, this.getTorchOrientation(world, x, y, z))) {
            int metadata = world.getBlockMetadata(x, y, z);
            this.dropBlockAsItem(world, x, y, z, 0, 0);
            world.setBlock(x, y, z, Block.waterMoving.blockID, metadata, 3);
        }
    }

    @Override public void onBlockDestroyedByPlayer(World world, int x, int y, int z, int metadata) {
        if (!world.isRemote && world.isAirBlock(x, y, z)) world.setBlock(x, y, z, Block.waterMoving.blockID, metadata, 3);
    }

    @Override public AxisAlignedBB getBlockBoundsFromPoolBasedOnState(IBlockAccess world, int x, int y, int z) {
        return CrystalTorchBlock.torchBounds(this.getTorchOrientation(world, x, y, z));
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderer, int x, int y, int z) {
        // fluid metadata and biome tint stay native; orientation is stored separately.
        renderer.setRenderBounds(0, 0, 0, 1, 1, 1);
        // the torch itself is rendered before transparent water by its tile renderer.
        return renderer.renderBlockFluids(this, x, y, z);
    }
}
