package com.itlesports.nightmaremode.util;

import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.util.interfaces.IDyeableStorage;
import com.itlesports.nightmaremode.util.interfaces.IColoredChest;
import net.minecraft.src.Block;
import net.minecraft.src.BlockChest;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ResourceLocation;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;

public final class StorageColor {
    public static final int DEFAULT_CHEST_COLOR = 0xA06540;
    public static final int WHITE = 0xFFFFFF;
    private static final String CHEST_COLOR_TAG = "nmChestColor";
    private static final String STORAGE_COLOR_TAG = "nmStorageColor";
    public static final int BROWN = 3;
    public static final int RED = 1;
    public static final int GRAY = 8;
    public static final int DEFAULT_BLOOD_CHEST_COLOR = 0xA83333;
    public static final int DEFAULT_STEEL_LOCKER_COLOR = 0x9E9E9E;

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
    private static int chestItemRenderColor = -1;
    private static int storageItemRenderColor = -1;
    private static final ThreadLocal<CapturedStorageDrop> capturedStorageDrop = new ThreadLocal<CapturedStorageDrop>();

    private StorageColor() {}

    public static boolean dyeStorage(World world, int x, int y, int z, EntityPlayer player, ItemStack dye) {
        if (dye == null || dye.itemID != Item.dyePowder.itemID || !isStorageBlock(world.getBlockId(x, y, z))) {
            return false;
        }

        int blockId = world.getBlockId(x, y, z);

        TileEntity tile = world.getBlockTileEntity(x, y, z);
        if (tile instanceof IColoredChest chest) {
            int color = mixChestColor(chest.nm$hasChestColor() ? chest.nm$getChestColor()
                    : getStorageDefaultChestColor(blockId),
                    dye.getItemDamage());
            chest.nm$setChestColor(color);
            if (isStandardChest(blockId)) {
                mixMatchingChest(world, x - 1, y, z, blockId, chest);
                mixMatchingChest(world, x + 1, y, z, blockId, chest);
                mixMatchingChest(world, x, y, z - 1, blockId, chest);
                mixMatchingChest(world, x, y, z + 1, blockId, chest);
            }
            consumeHeldItem(player, dye);
            return true;
        }

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

        consumeHeldItem(player, dye);
        return true;
    }

    public static boolean washChest(World world, int x, int y, int z, EntityPlayer player, ItemStack soap) {
        TileEntity tile = world.getBlockTileEntity(x, y, z);
        if (!isStorageBlock(world.getBlockId(x, y, z)) || !(tile instanceof IColoredChest chest)) {
            return false;
        }
        chest.nm$setChestColor(WHITE);
        int blockId = world.getBlockId(x, y, z);
        if (isStandardChest(blockId)) {
            dyeMatchingChest(world, x - 1, y, z, blockId, WHITE);
            dyeMatchingChest(world, x + 1, y, z, blockId, WHITE);
            dyeMatchingChest(world, x, y, z - 1, blockId, WHITE);
            dyeMatchingChest(world, x, y, z + 1, blockId, WHITE);
        }
        consumeHeldItem(player, soap);
        return true;
    }

    public static boolean isStorageBlock(int blockId) {
        return isStandardChest(blockId) || blockId == NMBlocks.bloodChest.blockID || blockId == NMBlocks.steelLocker.blockID;
    }

    private static boolean isStandardChest(int blockId) {
        return blockId > 0 && blockId < Block.blocksList.length && Block.blocksList[blockId] instanceof BlockChest;
    }

    public static void mixAdjacentChestColors(World world, int x, int y, int z) {
        if (!(world.getBlockTileEntity(x, y, z) instanceof IColoredChest chest)) return;
        int blockId = world.getBlockId(x, y, z);
        mixMatchingChest(world, x - 1, y, z, blockId, chest);
        mixMatchingChest(world, x + 1, y, z, blockId, chest);
        mixMatchingChest(world, x, y, z - 1, blockId, chest);
        mixMatchingChest(world, x, y, z + 1, blockId, chest);
    }

    private static void mixMatchingChest(World world, int x, int y, int z, int blockId, IColoredChest source) {
        if (world.getBlockId(x, y, z) != blockId) {
            return;
        }
        TileEntity neighbor = world.getBlockTileEntity(x, y, z);
        if (neighbor instanceof IColoredChest chest) {
            boolean sourceDyed = source.nm$hasChestColor();
            boolean neighborDyed = chest.nm$hasChestColor();
            if (!sourceDyed && !neighborDyed) {
                return;
            }
            if (!sourceDyed) {
                source.nm$setChestColor(chest.nm$getChestColor());
                return;
            }
            if (!neighborDyed) {
                chest.nm$setChestColor(source.nm$getChestColor());
                return;
            }

            int color = blendChestColors(source.nm$getChestColor(), chest.nm$getChestColor());
            source.nm$setChestColor(color);
            chest.nm$setChestColor(color);
        }
    }

    private static void dyeMatchingChest(World world, int x, int y, int z, int blockId, int color) {
        if (world.getBlockId(x, y, z) != blockId) return;
        TileEntity neighbor = world.getBlockTileEntity(x, y, z);
        if (neighbor instanceof IColoredChest chest) chest.nm$setChestColor(color);
        else if (neighbor instanceof IDyeableStorage storage) storage.nm$setStorageColor(color);
    }

    public static int mixChestColor(int chestColor, int dyeDamage) {
        float[] dyeColor = net.minecraft.src.EntitySheep.fleeceColorTable[
                net.minecraft.src.BlockColored.getBlockFromDye(dyeDamage)];
        int dyeRed = (int)(dyeColor[0] * 255.0F);
        int dyeGreen = (int)(dyeColor[1] * 255.0F);
        int dyeBlue = (int)(dyeColor[2] * 255.0F);
        int chestRed = chestColor >> 16 & 255;
        int chestGreen = chestColor >> 8 & 255;
        int chestBlue = chestColor & 255;
        int averageRed = (chestRed + dyeRed) / 2;
        int averageGreen = (chestGreen + dyeGreen) / 2;
        int averageBlue = (chestBlue + dyeBlue) / 2;
        float averageBrightness = (float)(Math.max(chestRed, Math.max(chestGreen, chestBlue))
                + Math.max(dyeRed, Math.max(dyeGreen, dyeBlue))) / 2.0F;
        float maximum = Math.max(averageRed, Math.max(averageGreen, averageBlue));
        return (int)((float)averageRed * averageBrightness / maximum) << 16
                | (int)((float)averageGreen * averageBrightness / maximum) << 8
                | (int)((float)averageBlue * averageBrightness / maximum);
    }

    public static int blendChestColors(int firstColor, int secondColor) {
        int red = (firstColor >> 16 & 255) + (secondColor >> 16 & 255);
        int green = (firstColor >> 8 & 255) + (secondColor >> 8 & 255);
        int blue = (firstColor & 255) + (secondColor & 255);
        int averageRed = red / 2;
        int averageGreen = green / 2;
        int averageBlue = blue / 2;
        float brightness = (float)(Math.max(firstColor >> 16 & 255, Math.max(firstColor >> 8 & 255, firstColor & 255))
                + Math.max(secondColor >> 16 & 255, Math.max(secondColor >> 8 & 255, secondColor & 255))) / 2.0F;
        float maximum = Math.max(averageRed, Math.max(averageGreen, averageBlue));
        return (int)((float)averageRed * brightness / maximum) << 16
                | (int)((float)averageGreen * brightness / maximum) << 8
                | (int)((float)averageBlue * brightness / maximum);
    }

    public static int getLegacyStorageColor(int color) {
        float[] tint = TINTS[normalize(color)];
        return (int)(tint[0] * 255.0F) << 16 | (int)(tint[1] * 255.0F) << 8 | (int)(tint[2] * 255.0F);
    }

    public static boolean isChestItem(ItemStack stack) {
        return stack != null && isStandardChest(stack.itemID);
    }

    public static boolean isStorageItem(ItemStack stack) {
        return stack != null && isStorageBlock(stack.itemID);
    }

    public static boolean hasChestItemColor(ItemStack stack) {
        return isStorageItem(stack) && stack.hasTagCompound() && stack.getTagCompound().hasKey(CHEST_COLOR_TAG);
    }

    public static int getChestItemColor(ItemStack stack) {
        return hasChestItemColor(stack) ? stack.getTagCompound().getInteger(CHEST_COLOR_TAG) & 0xFFFFFF
                : hasStorageItemColor(stack) ? getLegacyStorageColor(getStorageItemColor(stack))
                : getStorageDefaultChestColor(stack.itemID);
    }

    public static void setChestItemColor(ItemStack stack, int color) {
        net.minecraft.src.NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) {
            tag = new net.minecraft.src.NBTTagCompound();
            stack.setTagCompound(tag);
        }
        tag.setInteger(CHEST_COLOR_TAG, color & 0xFFFFFF);
    }

    public static boolean hasStorageItemColor(ItemStack stack) {
        return stack != null && isStorageBlock(stack.itemID) && stack.hasTagCompound()
                && stack.getTagCompound().hasKey(STORAGE_COLOR_TAG);
    }

    public static int getStorageItemColor(ItemStack stack) {
        return hasStorageItemColor(stack) ? stack.getTagCompound().getByte(STORAGE_COLOR_TAG) & 15
                : getStorageDefaultColor(stack.itemID);
    }

    public static void setStorageItemColor(ItemStack stack, int color) {
        net.minecraft.src.NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) {
            tag = new net.minecraft.src.NBTTagCompound();
            stack.setTagCompound(tag);
        }
        tag.setByte(STORAGE_COLOR_TAG, (byte)(color & 15));
    }

    public static void captureStorageColorBeforeHarvest(World world, int x, int y, int z) {
        capturedStorageDrop.remove();
        int blockId = world.getBlockId(x, y, z);
        if (!isStorageBlock(blockId)) {
            return;
        }

        TileEntity tile = world.getBlockTileEntity(x, y, z);
        if (tile instanceof IColoredChest chest && chest.nm$hasChestColor()) {
            capturedStorageDrop.set(new CapturedStorageDrop(world, x, y, z, blockId, chest.nm$getChestColor(), true));
        } else if (tile instanceof IDyeableStorage storage
                && storage.nm$getStorageColor() != getStorageDefaultColor(blockId)) {
            capturedStorageDrop.set(new CapturedStorageDrop(world, x, y, z, blockId,
                    storage.nm$getStorageColor(), false));
        }
    }

    public static boolean applyCapturedStorageColor(World world, int x, int y, int z, int blockId, ItemStack stack) {
        CapturedStorageDrop captured = capturedStorageDrop.get();
        if (captured == null || captured.world != world || captured.x != x || captured.y != y || captured.z != z
                || captured.blockId != blockId || stack.itemID != blockId) {
            return false;
        }

        capturedStorageDrop.remove();
        if (captured.chestColor) {
            setChestItemColor(stack, captured.color);
        } else {
            setStorageItemColor(stack, captured.color);
        }
        return true;
    }

    public static void clearCapturedStorageColor() {
        capturedStorageDrop.remove();
    }

    public static void beginChestItemRender(ItemStack stack) {
        chestItemRenderColor = hasChestItemColor(stack) ? getChestItemColor(stack) : -1;
        storageItemRenderColor = hasStorageItemColor(stack) ? getStorageItemColor(stack) : -1;
    }

    public static void endChestItemRender() {
        chestItemRenderColor = -1;
        storageItemRenderColor = -1;
    }

    public static boolean hasChestItemRenderColor() {
        return chestItemRenderColor >= 0;
    }

    public static int getChestItemRenderColor() {
        return chestItemRenderColor;
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

    public static ResourceLocation getTintableChestTexture(int chestType, boolean doubleChest, boolean dyed,
                                                            ResourceLocation fallback) {
        if (!dyed) return fallback;
        if (chestType == 1) {
            return doubleChest ? TRAPPED_DOUBLE_CHEST_TEXTURE : TRAPPED_CHEST_TEXTURE;
        }
        return doubleChest ? NORMAL_DOUBLE_CHEST_TEXTURE : NORMAL_CHEST_TEXTURE;
    }

    public static void applyChestTint(int color, boolean dyed) {
        if (!dyed) {
            org.lwjgl.opengl.GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            return;
        }
        org.lwjgl.opengl.GL11.glColor4f((float)(color >> 16 & 255) / 255.0F,
                (float)(color >> 8 & 255) / 255.0F, (float)(color & 255) / 255.0F, 1.0F);
    }

    public static ResourceLocation getBloodChestTexture(int color, ResourceLocation fallback) {
        return normalize(color) == RED ? fallback : BLOOD_CHEST_TEXTURE;
    }

    public static ResourceLocation getBloodChestTexture(boolean dyed, ResourceLocation fallback) {
        return dyed ? BLOOD_CHEST_TEXTURE : fallback;
    }

    public static ResourceLocation getSteelLockerTexture(int color, ResourceLocation fallback) {
        return normalize(color) == GRAY ? fallback : STEEL_LOCKER_TEXTURE;
    }

    public static ResourceLocation getSteelLockerTexture(boolean dyed, ResourceLocation fallback) {
        return dyed ? STEEL_LOCKER_TEXTURE : fallback;
    }

    public static void setRenderColorOverride(int color) {
        renderColorOverride = color & 15;
    }

    public static void clearRenderColorOverride() {
        renderColorOverride = -1;
    }

    public static int getRenderColor(int storageColor) {
        return normalize(renderColorOverride >= 0 ? renderColorOverride
                : storageItemRenderColor >= 0 ? storageItemRenderColor : storageColor);
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

    private static int getStorageDefaultColor(int blockId) {
        if (blockId == NMBlocks.bloodChest.blockID) return RED;
        if (blockId == NMBlocks.steelLocker.blockID) return GRAY;
        return BROWN;
    }

    public static int getStorageDefaultChestColor(int blockId) {
        if (blockId == NMBlocks.bloodChest.blockID) return DEFAULT_BLOOD_CHEST_COLOR;
        if (blockId == NMBlocks.steelLocker.blockID) return DEFAULT_STEEL_LOCKER_COLOR;
        return DEFAULT_CHEST_COLOR;
    }

    private static void consumeHeldItem(EntityPlayer player, ItemStack stack) {
        if (!player.worldObj.isRemote && !player.capabilities.isCreativeMode && --stack.stackSize == 0) {
            player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
        }
    }

    private static ResourceLocation texture(String name) {
        return new ResourceLocation("nightmare:textures/blocks/chests/" + name + ".png");
    }

    private static final class CapturedStorageDrop {
        private final World world;
        private final int x;
        private final int y;
        private final int z;
        private final int blockId;
        private final int color;
        private final boolean chestColor;

        private CapturedStorageDrop(World world, int x, int y, int z, int blockId, int color, boolean chestColor) {
            this.world = world;
            this.x = x;
            this.y = y;
            this.z = z;
            this.blockId = blockId;
            this.color = color;
            this.chestColor = chestColor;
        }
    }
}
