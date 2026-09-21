package com.itlesports.nightmaremode.util;

import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.util.interfaces.IDyeableStorage;
import net.minecraft.src.Block;
import net.minecraft.src.BlockChest;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ResourceLocation;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;

public final class StorageColor {
    public static final int BROWN = 3;
    public static final int RED = 1;
    public static final int GRAY = 8;

    private static final float[][] TINTS = {
            {0.12F, 0.12F, 0.12F}, {0.66F, 0.20F, 0.20F}, {0.25F, 0.37F, 0.12F}, {0.46F, 0.32F, 0.20F},
            {0.22F, 0.32F, 0.70F}, {0.52F, 0.25F, 0.72F}, {0.27F, 0.55F, 0.62F}, {0.62F, 0.62F, 0.62F},
            {0.30F, 0.30F, 0.30F}, {0.95F, 0.52F, 0.66F}, {0.50F, 0.80F, 0.12F}, {0.90F, 0.90F, 0.12F},
            {0.34F, 0.62F, 0.80F}, {0.72F, 0.32F, 0.86F}, {0.90F, 0.50F, 0.12F}, {1.00F, 1.00F, 1.00F}
    };
    private static final ResourceLocation NORMAL_CHEST_TEXTURE = texture("normal_tintable");
    private static final ResourceLocation NORMAL_DOUBLE_CHEST_TEXTURE = texture("normal_tintable_double");
    private static final ResourceLocation TRAPPED_CHEST_TEXTURE = texture("trapped_tintable");
    private static final ResourceLocation TRAPPED_DOUBLE_CHEST_TEXTURE = texture("trapped_tintable_double");
    private static final ResourceLocation BLOOD_CHEST_TEXTURE = texture("blood_tintable");
    private static final ResourceLocation STEEL_LOCKER_TEXTURE = texture("steel_locker_tintable");
    private static int renderColorOverride = -1;

    private StorageColor() {}

    public static boolean dyeStorage(World world, int x, int y, int z, EntityPlayer player, ItemStack dye) {
        if (dye == null || dye.itemID != Item.dyePowder.itemID || !isStorageBlock(world.getBlockId(x, y, z))) {
            return false;
        }

        int blockId = world.getBlockId(x, y, z);

        TileEntity tile = world.getBlockTileEntity(x, y, z);
        if (!(tile instanceof IDyeableStorage storage)) {
            return false;
        }

        int color = dye.getItemDamage() & 15;
        storage.nm$setStorageColor(color);

        // only standard and trapped chests can form doubles, and both halves must match.
        if (isStandardChest(blockId)) {
            dyeMatchingChest(world, x - 1, y, z, blockId, color);
            dyeMatchingChest(world, x + 1, y, z, blockId, color);
            dyeMatchingChest(world, x, y, z - 1, blockId, color);
            dyeMatchingChest(world, x, y, z + 1, blockId, color);
        }

        if (!world.isRemote && !player.capabilities.isCreativeMode && --dye.stackSize == 0) {
            player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
        }
        return true;
    }

    public static boolean isStorageBlock(int blockId) {
        return isStandardChest(blockId) || blockId == NMBlocks.bloodChest.blockID || blockId == NMBlocks.steelLocker.blockID;
    }

    private static boolean isStandardChest(int blockId) {
        return blockId > 0 && Block.blocksList[blockId] instanceof BlockChest;
    }

    private static void dyeMatchingChest(World world, int x, int y, int z, int blockId, int color) {
        if (world.getBlockId(x, y, z) != blockId) {
            return;
        }
        TileEntity neighbor = world.getBlockTileEntity(x, y, z);
        if (neighbor instanceof IDyeableStorage storage) {
            storage.nm$setStorageColor(color);
        }
    }

    public static ResourceLocation getTintableChestTexture(int chestType, boolean doubleChest, int color, ResourceLocation fallback) {
        if (normalize(color) == BROWN) {
            return fallback;
        }
        if (chestType == 1) {
            return doubleChest ? TRAPPED_DOUBLE_CHEST_TEXTURE : TRAPPED_CHEST_TEXTURE;
        }
        return doubleChest ? NORMAL_DOUBLE_CHEST_TEXTURE : NORMAL_CHEST_TEXTURE;
    }

    public static ResourceLocation getBloodChestTexture(int color, ResourceLocation fallback) {
        return normalize(color) == RED ? fallback : BLOOD_CHEST_TEXTURE;
    }

    public static ResourceLocation getSteelLockerTexture(int color, ResourceLocation fallback) {
        return normalize(color) == GRAY ? fallback : STEEL_LOCKER_TEXTURE;
    }

    public static void setRenderColorOverride(int color) {
        renderColorOverride = color & 15;
    }

    public static void clearRenderColorOverride() {
        renderColorOverride = -1;
    }

    public static int getRenderColor(int storageColor) {
        return normalize(renderColorOverride >= 0 ? renderColorOverride : storageColor);
    }

    public static void applyTint(int color, int defaultColor) {
        int normalized = normalize(color);
        if (normalized == defaultColor) {
            org.lwjgl.opengl.GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            return;
        }
        float[] tint = TINTS[normalized];
        org.lwjgl.opengl.GL11.glColor4f(tint[0], tint[1], tint[2], 1.0F);
    }

    private static int normalize(int color) {
        return color >= 0 && color < TINTS.length ? color : BROWN;
    }

    private static ResourceLocation texture(String name) {
        return new ResourceLocation("nightmare:textures/blocks/chests/" + name + ".png");
    }
}
