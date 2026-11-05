package com.itlesports.nightmaremode.block.blocks;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.List;
import java.util.Random;

public class BlockUnderworldLeaves extends BlockLeavesBase {
    public static final int GREEN = 0;
    public static final int PURPLE = 1;
    public static final int GOLD = 2;
    public static final int PINK = 3;
    public static final int BLOOD = 4;
    public static final int SHADOW = 5;
    public static final String[] VARIANT_NAMES = {"green", "purple", "gold", "pink", "blood", "shadow"};
    private static final int[] COLORS = {0xa7ed42, 0x963fbf, 0xffe600, 0xDF88AE, 0xD81F1F, 0x1b181f};

    public BlockUnderworldLeaves(int id) {
        super(id, Material.leaves, true);
        setUnlocalizedName("nmUnderworldLeaves");
        setTextureName("nightmare:nmUnderworldLeaves");
        setLightOpacity(0);
        setHardness(0.2F);
        setStepSound(soundGrassFootstep);
        setCreativeTab(CreativeTabs.tabDecorations);
        // all metadata bits describe color. these leaves don't have decay bits :)
        setTickRandomly(false);
    }

    public static int getColor(int metadata) {
        return COLORS[getVariant(metadata)];
    }

    public static int getVariant(int metadata) {
        return metadata >= 0 && metadata < COLORS.length ? metadata : GREEN;
    }

    @Override public boolean isLeafBlock(IBlockAccess world, int x, int y, int z) { return true; }
    @Override public int damageDropped(int metadata) { return metadata & 15; }
    @Override public int quantityDropped(Random random) { return 0; }
    @Override protected boolean canSilkHarvest(int metadata) { return true; }

    @Override
    public void harvestBlock(World world, EntityPlayer player, int x, int y, int z, int metadata) {
        ItemStack held = player.getCurrentEquippedItem();
        if (!world.isRemote && held != null && held.getItem() instanceof ItemShears) {
            dropBlockAsItem_do(world, x, y, z, new ItemStack(this, 1, metadata & 15));
            held.damageItem(1, player);
        } else {
            super.harvestBlock(world, player, x, y, z, metadata);
        }
    }

    @Override @Environment(EnvType.CLIENT)
    public int getBlockColor() { return getColor(GREEN); }

    @Override @Environment(EnvType.CLIENT)
    public int getRenderColor(int metadata) { return getColor(metadata); }

    @Override @Environment(EnvType.CLIENT)
    public int colorMultiplier(IBlockAccess world, int x, int y, int z) {
        return getColor(world.getBlockMetadata(x, y, z));
    }

    @Override @Environment(EnvType.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side) {
        this.graphicsLevel = Block.leaves.graphicsLevel;
        return super.shouldSideBeRendered(world, x, y, z, side);
    }

    @Override @Environment(EnvType.CLIENT)
    public void getSubBlocks(int id, CreativeTabs tab, List stacks) {
        for (int metadata = 0; metadata < COLORS.length; metadata++) stacks.add(new ItemStack(id, 1, metadata));
    }
}
