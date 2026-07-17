package com.itlesports.nightmaremode.block.blocks;

import api.world.WorldUtils;
import com.itlesports.nightmaremode.block.tileEntities.DryingGrassTileEntity;
import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.util.NMFields;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.Random;

public class BlockDryingGrass extends BlockContainer {
    public static final int META_DRYING = 0;
    public static final int META_DRIED = 1;

    @Environment(EnvType.CLIENT)
    private Icon driedIcon;

    public BlockDryingGrass(int blockID) {
        super(blockID, Material.grass);
        this.initBlockBounds(0.0, 0.0, 0.0, 1.0, 0.125, 1.0);
        this.setHardness(0.0f);
        this.setBuoyant();
        this.setLightOpacity(0);
        this.setStepSound(Block.soundGrassFootstep);
        this.setCreativeTab(CreativeTabs.tabDecorations);
        this.setUnlocalizedName("ifhyDryingGrass");
        this.setTextureName("nightmare:ifhyDryingGrass");
    }

    @Override public String getModId() {
        return NMFields.modID;
    }

    @Override public TileEntity createNewTileEntity(World world) {
        return new DryingGrassTileEntity();
    }
    @Override public boolean isOpaqueCube() {
        return false;
    }
    @Override public boolean renderAsNormalBlock() {
        return false;
    }
    @Override public boolean isGroundCover() {
        return true;
    }

    @Override public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        return null;
    }
    @Override public AxisAlignedBB getBlockBoundsFromPoolBasedOnState(IBlockAccess blockAccess, int x, int y, int z) {return AxisAlignedBB.getAABBPool().getAABB(0.0, 0.0, 0.0, 1.0, 0.125, 1.0);}
    @Override @Environment(EnvType.CLIENT) public AxisAlignedBB getBlockBoundsFromPoolForItemRender(int itemDamage) {return AxisAlignedBB.getAABBPool().getAABB(0.0, 0.0, 0.0, 1.0, 0.125, 1.0);}

    @Override public boolean canPlaceBlockAt(World world, int x, int y, int z) {return WorldUtils.doesBlockHaveLargeCenterHardpointToFacing(world, x, y - 1, z, 1, true);}
    @Override public int getHarvestToolLevel(IBlockAccess blockAccess, int i, int j, int k) {return 0;}

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, int neighborBlockID) {
        if (!this.canPlaceBlockAt(world, x, y, z)) {
            this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
            world.setBlockToAir(x, y, z);
        }
    }

    @Override
    public int idDropped(int metadata, Random random, int fortuneModifier) {
        return metadata == META_DRIED ? NMItems.driedPlantFiber.itemID : NMItems.plantFiber.itemID;
    }

    @Override
    public void onBlockDestroyedWithImproperTool(World world, EntityPlayer player, int i, int j, int k, int iMetadata) {
        this.dropItemsIndividually(world,  i, j, k, this.idDropped(iMetadata, world.rand, 0), 1, 0, 1);
    }

//    @Override
//    public boolean dropComponentItemsOnBadBreak(World world, int i, int j, int k, int iMetadata, float fChanceOfDrop) {
//        this.dropItemsIndividually(world,i,j,k, this.idDropped(iMetadata, world.rand, 0), 1, 0, 1);
//        return true;
//    }

    @Override
    @Environment(EnvType.CLIENT)
    public int idPicked(World world, int x, int y, int z) {
        return NMItems.plantFiber.itemID;
    }

    public void onFinishedDrying(World world, int x, int y, int z) {
        world.setBlockMetadataWithNotify(x, y, z, META_DRIED);
        world.removeBlockTileEntity(x, y, z);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderer, int x, int y, int z) {
        renderer.setRenderBounds(this.getBlockBoundsFromPoolBasedOnState(renderer.blockAccess, x, y, z));
        return renderer.renderStandardBlock(this, x, y, z);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess blockAccess, int neighborX, int neighborY, int neighborZ, int side) {
        return side == 1 || this.currentBlockRenderer.shouldSideBeRenderedBasedOnCurrentBounds(neighborX, neighborY, neighborZ, side);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon(int side, int metadata) {
        return metadata == META_DRIED ? this.driedIcon : this.blockIcon;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons(IconRegister register) {
        this.blockIcon = register.registerIcon("nightmare:ifhyDryingGrass");
        this.driedIcon = register.registerIcon("nightmare:ifhyDriedPlantFibers");
    }
}
