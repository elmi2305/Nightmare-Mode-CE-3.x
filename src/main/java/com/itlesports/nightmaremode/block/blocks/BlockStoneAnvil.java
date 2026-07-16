package com.itlesports.nightmaremode.block.blocks;

import api.util.MiscUtils;
import btw.block.model.AnvilModel;
import btw.block.model.BlockModel;
import com.itlesports.nightmaremode.block.blocks.templates.NMBlockContainer;
import com.itlesports.nightmaremode.block.tileEntities.TileEntityHammerAnvil;
import com.itlesports.nightmaremode.block.tileEntities.TileEntityStoneAnvil;
import com.itlesports.nightmaremode.util.HammerAnvilHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.Block;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.EntityLivingBase;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.IconRegister;
import net.minecraft.src.Material;
import net.minecraft.src.MovingObjectPosition;
import net.minecraft.src.RenderBlocks;
import net.minecraft.src.TileEntity;
import net.minecraft.src.Vec3;
import net.minecraft.src.World;

public class BlockStoneAnvil extends NMBlockContainer {
    private final AnvilModel model = new AnvilModel();

    public BlockStoneAnvil(int id) {
        super(id, Material.rock);
        this.setHardness(3.0F);
        this.setResistance(8.0F);
        this.setStepSound(Block.soundStoneFootstep);
        this.setLightOpacity(0);
        this.setCreativeTab(CreativeTabs.tabDecorations);
        this.setUnlocalizedName("ifhyStoneAnvil");
        this.setTextureName("nightmare:ifhyStoneAnvil");
    }

    @Override
    public TileEntity createNewTileEntity(World world) {
        return new TileEntityStoneAnvil();
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public int onBlockPlaced(World world, int x, int y, int z, int facing, float clickX, float clickY, float clickZ, int metadata) {
        return this.setFacing(metadata, facing);
    }

    @Override
    public int preBlockPlacedBy(World world, int x, int y, int z, int metadata, EntityLivingBase entityBy) {
        return this.setFacing(metadata, MiscUtils.convertOrientationToFlatBlockFacing(entityBy));
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return true;
        }

        TileEntity tile = world.getBlockTileEntity(x, y, z);
        if (!(tile instanceof TileEntityStoneAnvil)) {
            tile = new TileEntityStoneAnvil();
            world.setBlockTileEntity(x, y, z, tile);
        }
        TileEntityHammerAnvil anvil = tile instanceof TileEntityHammerAnvil ? (TileEntityHammerAnvil)tile : null;
        HammerAnvilHelper.tryHammerHeldItem(world, x, y, z, player, anvil);
        return true;
    }

    @Override
    public MovingObjectPosition collisionRayTrace(World world, int x, int y, int z, Vec3 startRay, Vec3 endRay) {
        BlockModel transformedModel = this.model.makeTemporaryCopy();
        transformedModel.rotateAroundYToFacing(this.getFacing(world, x, y, z));
        return transformedModel.collisionRayTrace(world, x, y, z, startRay, endRay);
    }

    @Override
    public int getFacing(int metadata) {
        int orientation = metadata & 3;
        if ((orientation & 1) == 0) {
            return orientation == 0 ? 3 : 2;
        }
        return orientation == 1 ? 4 : 5;
    }

    @Override
    public int setFacing(int metadata, int facing) {
        int orientation = facing == 2 ? 2 : (facing == 3 ? 0 : (facing == 4 ? 1 : 3));
        return (metadata & 0xFFFFFFFC) | orientation;
    }

    @Override
    public boolean canRotateOnTurntable(IBlockAccess blockAccess, int x, int y, int z) {
        return true;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderer, int x, int y, int z) {
        int facing = this.getFacing(renderer.blockAccess, x, y, z);
        BlockModel transformedModel = this.model.makeTemporaryCopy();
        transformedModel.rotateAroundYToFacing(facing);
        renderer.setUVRotateTop(this.convertFacingToTopTextureRotation(facing));
        renderer.setUVRotateBottom(this.convertFacingToBottomTextureRotation(facing));
        boolean rendered = transformedModel.renderAsBlock(renderer, this, x, y, z);
        renderer.clearUVRotation();
        return rendered;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess blockAccess, int neighborX, int neighborY, int neighborZ, int side) {
        return this.currentBlockRenderer.shouldSideBeRenderedBasedOnCurrentBounds(neighborX, neighborY, neighborZ, side);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void renderBlockAsItem(RenderBlocks renderer, int itemDamage, float brightness) {
        this.model.renderAsItemBlock(renderer, this, itemDamage);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        AxisAlignedBB transformedBox = this.model.boxSelection.makeTemporaryCopy();
        transformedBox.rotateAroundYToFacing(this.getFacing(world, x, y, z));
        transformedBox.offset(x, y, z);
        return transformedBox;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons(IconRegister register) {
        this.blockIcon = register.registerIcon(this.getTextureName());
    }
}
