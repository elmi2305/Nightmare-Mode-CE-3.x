package com.itlesports.nightmaremode.crafting.recipe.types;

import com.itlesports.nightmaremode.util.StorageColor;
import com.prupe.mcpatcher.cc.ColorizeEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;

import java.util.ArrayList;
import java.util.List;

/** Applies the vanilla leather-armor dye blend to a storage stack. */
public class ChestDyeRecipe implements IRecipe {
    private static final ResourceLocation ID = new ResourceLocation("nightmare", "dye_chest");

    @Override
    public boolean matches(InventoryCrafting inventory, World world) {
        return this.findChestAndDyes(inventory) != null;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inventory) {
        ChestAndDyes input = this.findChestAndDyes(inventory);
        if (input == null) return null;

        int[] channels = new int[3];
        int brightness = 0;
        int contributors = 0;
        int chestColor = StorageColor.getChestItemColor(input.chest);
        brightness += Math.max(chestColor >> 16 & 255, Math.max(chestColor >> 8 & 255, chestColor & 255));
        channels[0] += chestColor >> 16 & 255;
        channels[1] += chestColor >> 8 & 255;
        channels[2] += chestColor & 255;
        ++contributors;

        for (ItemStack dye : input.dyes) {
            int dyeId = BlockColored.getBlockFromDye(dye.getItemDamage());
            float[] dyeColor = MinecraftServer.getIsServer() ? EntitySheep.fleeceColorTable[dyeId]
                    : ColorizeEntity.getArmorDyeColor(EntitySheep.fleeceColorTable[dyeId], dyeId);
            int red = (int)(dyeColor[0] * 255.0F);
            int green = (int)(dyeColor[1] * 255.0F);
            int blue = (int)(dyeColor[2] * 255.0F);
            brightness += Math.max(red, Math.max(green, blue));
            channels[0] += red;
            channels[1] += green;
            channels[2] += blue;
            ++contributors;
        }

        int red = channels[0] / contributors;
        int green = channels[1] / contributors;
        int blue = channels[2] / contributors;
        float averageBrightness = (float)brightness / (float)contributors;
        float maximum = Math.max(red, Math.max(green, blue));
        red = (int)((float)red * averageBrightness / maximum);
        green = (int)((float)green * averageBrightness / maximum);
        blue = (int)((float)blue * averageBrightness / maximum);

        ItemStack result = input.chest.copy();
        result.stackSize = 1;
        StorageColor.setChestItemColor(result, red << 16 | green << 8 | blue);
        return result;
    }

    private ChestAndDyes findChestAndDyes(InventoryCrafting inventory) {
        ItemStack chest = null;
        List<ItemStack> dyes = new ArrayList<ItemStack>();
        for (int slot = 0; slot < inventory.getSizeInventory(); ++slot) {
            ItemStack stack = inventory.getStackInSlot(slot);
            if (stack == null) continue;
            if (StorageColor.isStorageItem(stack)) {
                if (chest != null) return null;
                chest = stack;
            } else if (stack.itemID == Item.dyePowder.itemID) {
                dyes.add(stack);
            } else {
                return null;
            }
        }
        return chest != null && !dyes.isEmpty() ? new ChestAndDyes(chest, dyes) : null;
    }

    @Override public int getRecipeSize() { return 10; }
    @Override public ItemStack getRecipeOutput() { return null; }
    @Override public boolean matches(IRecipe recipe) { return recipe == this; }
    @Override public boolean hasSecondaryOutput() { return false; }
    @Override public ItemStack[] getSecondaryOutput(IInventory inventory) { return null; }
    @Override public ResourceLocation getId() { return ID; }

    private record ChestAndDyes(ItemStack chest, List<ItemStack> dyes) {}
}
