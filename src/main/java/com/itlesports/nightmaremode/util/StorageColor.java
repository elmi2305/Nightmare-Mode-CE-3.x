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

    private static final String[] COLOR_NAMES = {
            "black", "red", "green", "brown", "blue", "purple", "cyan", "light_gray",
            "gray", "pink", "lime", "yellow", "light_blue", "magenta", "orange", "white"
    };
    private static final ResourceLocation[] NORMAL_CHEST_TEXTURES = createTextures("normal", false);
    private static final ResourceLocation[] NORMAL_DOUBLE_CHEST_TEXTURES = createTextures("normal", true);
    private static final ResourceLocation[] TRAPPED_CHEST_TEXTURES = createTextures("trapped", false);
    private static final ResourceLocation[] TRAPPED_DOUBLE_CHEST_TEXTURES = createTextures("trapped", true);
    private static final ResourceLocation[] BLOOD_CHEST_TEXTURES = createTextures("blood", false);
    private static final ResourceLocation[] STEEL_LOCKER_TEXTURES = createTextures("steel_locker", false);
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

    public static ResourceLocation getChestTexture(int chestType, boolean doubleChest, int color, ResourceLocation fallback) {
        int index = normalize(color);
        if (index == BROWN) {
            return fallback;
        }
        if (chestType == 1) {
            return (doubleChest ? TRAPPED_DOUBLE_CHEST_TEXTURES : TRAPPED_CHEST_TEXTURES)[index];
        }
        return (doubleChest ? NORMAL_DOUBLE_CHEST_TEXTURES : NORMAL_CHEST_TEXTURES)[index];
    }

    public static ResourceLocation getBloodChestTexture(int color, ResourceLocation fallback) {
        return textureOrFallback(BLOOD_CHEST_TEXTURES, color, fallback);
    }

    public static ResourceLocation getSteelLockerTexture(int color, ResourceLocation fallback) {
        return textureOrFallback(STEEL_LOCKER_TEXTURES, color, fallback);
    }

    public static void setRenderColorOverride(int color) {
        renderColorOverride = color & 15;
    }

    public static void clearRenderColorOverride() {
        renderColorOverride = -1;
    }

    public static int getRenderColor(int storageColor) {
        return renderColorOverride >= 0 ? renderColorOverride : storageColor;
    }

    private static int normalize(int color) {
        return color >= 0 && color < COLOR_NAMES.length ? color : BROWN;
    }

    private static ResourceLocation textureOrFallback(ResourceLocation[] textures, int color, ResourceLocation fallback) {
        int index = normalize(color);
        return index == BROWN ? fallback : textures[index];
    }

    private static ResourceLocation[] createTextures(String type, boolean doubleChest) {
        ResourceLocation[] textures = new ResourceLocation[COLOR_NAMES.length];
        for (int i = 0; i < textures.length; ++i) {
            String suffix = doubleChest ? "_double" : "";
            textures[i] = new ResourceLocation("nightmare:textures/blocks/chests/" + type + "_" + COLOR_NAMES[i] + suffix + ".png");
        }
        return textures;
    }
}
