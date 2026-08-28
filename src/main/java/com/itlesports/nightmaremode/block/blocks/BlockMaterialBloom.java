package com.itlesports.nightmaremode.block.blocks;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.Block;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.Icon;
import net.minecraft.src.IconRegister;
import net.minecraft.src.Material;
import net.minecraft.src.RenderBlocks;
import net.minecraft.src.World;

import java.util.Arrays;
import java.util.Random;

/** Small placeable bloom whose metadata is reserved for hammer-hit progress. */
public class BlockMaterialBloom extends Block {
    private static final int HITS_TO_FINISH = 8;
    private final int dropItemID;
    private final String iconName;
    @Environment(EnvType.CLIENT) private Icon[] icons;

    public BlockMaterialBloom(int blockID, int dropItemID, String name, String iconName) {
        super(blockID, Material.rock);
        this.dropItemID = dropItemID;
        this.iconName = iconName;
        this.setHardness(0.6F);
        this.setResistance(2.0F);
        this.setStepSound(Block.soundStoneFootstep);
        this.setUnlocalizedName(name);
    }

    @Override public boolean isOpaqueCube() { return false; }
    @Override public boolean renderAsNormalBlock() { return false; }

    @Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        return super.canPlaceBlockAt(world, x, y, z) && world.doesBlockHaveSolidTopSurface(x, y - 1, z);
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, int neighborBlockID) {
        if (!world.doesBlockHaveSolidTopSurface(x, y - 1, z)) {
            this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
            world.setBlockToAir(x, y, z);
        }
    }

    @Override public void setBlockBoundsBasedOnState(IBlockAccess access, int x, int y, int z) { }
    @Override public void setBlockBoundsForItemRender() { }

    @Override
    public AxisAlignedBB getBlockBoundsFromPoolBasedOnState(IBlockAccess access, int x, int y, int z) {
        return bloomBounds();
    }

    @Override
    @Environment(EnvType.CLIENT)
    public AxisAlignedBB getBlockBoundsFromPoolForItemRender(int damage) {
        return bloomBounds();
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderer, int x, int y, int z) {
        renderer.setRenderBounds(this.getBlockBoundsFromPoolBasedOnState(renderer.blockAccess, x, y, z));
        return renderer.renderStandardBlock(this, x, y, z);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess access, int x, int y, int z, int side) {
        return this.currentBlockRenderer.shouldSideBeRenderedBasedOnCurrentBounds(x, y, z, side);
    }

    private static AxisAlignedBB bloomBounds() {
        return AxisAlignedBB.getAABBPool().getAABB(0.1875D, 0.0D, 0.1875D, 0.8125D, 0.25D, 0.8125D);
    }

    @Override public int idDropped(int metadata, Random random, int fortune) { return this.dropItemID; }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon(int side, int metadata) {
        return this.icons[Math.min(metadata, this.icons.length - 1)];
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons(IconRegister register) {
        this.icons = new Icon[HITS_TO_FINISH];
        Icon icon = register.registerIcon(this.iconName);
        Arrays.fill(this.icons, icon);
        this.blockIcon = icon;
    }
}
