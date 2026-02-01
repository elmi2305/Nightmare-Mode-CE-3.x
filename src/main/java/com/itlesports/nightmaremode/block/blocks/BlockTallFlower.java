package com.itlesports.nightmaremode.block.blocks;

import com.itlesports.nightmaremode.block.NMBlocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.List;
import java.util.Random;

public class BlockTallFlower extends BlockFlower {
    @Environment(EnvType.CLIENT)
    private Icon[] bottomIcons;
    @Environment(EnvType.CLIENT)
    private Icon[] topIcons;

    private static final int TYPE_MASK = 0x7;
    private static final int TOP_FLAG = 0x8;

    public static int ROSEBUSH = 0;
    public static int PEONY = 1;
    public static int LILAC = 2;
    public static int SUNFLOWER = 3;
    public static int BERRYBUSH = 4;

    public BlockTallFlower(int blockID) {
        super(blockID, Material.plants);
        this.setHardness(0.0F);
        this.setStepSound(soundGrassFootstep);
        this.setUnlocalizedName("tallFlower");
        this.setCreativeTab(CreativeTabs.tabDecorations);
        this.setBlockBounds(0.1F, 0.0F, 0.1F, 0.9F, 1.0F, 0.9F);
        this.setTickRandomly(false);
    }

    // --- Placement: Only allow bottom, and place top if possible ---
    @Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        return super.canPlaceBlockAt(world, x, y, z) && world.isAirBlock(x, y + 1, z);
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack stack) {
        int type = stack.getItemDamage() & TYPE_MASK;
        world.setBlock(x, y, z, this.blockID, type, 2);  // Bottom half

        if (world.isAirBlock(x, y + 1, z)) {
            world.setBlock(x, y + 1, z, this.blockID, type | TOP_FLAG, 2);  // Top half
        }
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, int blockID, int meta) {
        boolean isTop = (meta & TOP_FLAG) != 0;

        if (isTop) {
            // Break bottom if exists
            if (world.getBlockId(x, y - 1, z) == this.blockID) {
                world.setBlockToAir(x, y - 1, z);
            }
        } else {
            // Break top if exists
            if (world.getBlockId(x, y + 1, z) == this.blockID) {
                world.setBlockToAir(x, y + 1, z);
            }
        }

        super.breakBlock(world, x, y, z, blockID, meta);
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, int neighborID) {
        int meta = world.getBlockMetadata(x, y, z);
        if ((meta & TOP_FLAG) != 0) {  // Is top half
            int belowID = world.getBlockId(x, y - 1, z);
            if (belowID != this.blockID || (world.getBlockMetadata(x, y - 1, z) & TOP_FLAG) != 0) {
                world.setBlockToAir(x, y, z);
            }
        }
    }

    @Override
    public int damageDropped(int meta) {
        return (meta & TOP_FLAG) != 0 ? 0 : (meta & TYPE_MASK);  // Drop type only from bottom
    }

    @Override
    public int idDropped(int meta, Random rand, int fortune) {
        return (meta & TOP_FLAG) != 0 ? 0 : this.blockID;  // Drop the block itself (as item)
    }

    @Override
    public int quantityDropped(Random rand) {
        return 1;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon(int side, int meta) {
        int type = meta & TYPE_MASK;
        boolean isTop = (meta & TOP_FLAG) != 0;

        if (isTop) {
            return topIcons[type];
        } else {
            return bottomIcons[type];
        }
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons(IconRegister reg) {
        bottomIcons = new Icon[8];
        topIcons = new Icon[8];

        String[] names = {"dandelion", "dandelion2", "dandelion3", "dandelion4", "dandelion5", "dandelion5", "dandelion5", "dandelion5", };

        for (int i = 0; i < names.length; i++) {
            bottomIcons[i] = reg.registerIcon("nightmare:nmTallFlower_" + names[i] + "_bottom");
            topIcons[i] = reg.registerIcon("nightmare:nmTallFlower_" + names[i] + "_top");
        }
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void getSubBlocks(int blockID, CreativeTabs tab, List list) {
        for (int i = 0; i < 8; i++) {
            list.add(new ItemStack(blockID, 1, i));
        }
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        return null;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public int getRenderType() {
        return 1;  // Crossed squares (same as BlockFlower)
    }
}