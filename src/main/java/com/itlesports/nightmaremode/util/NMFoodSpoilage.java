package com.itlesports.nightmaremode.util;

import btw.crafting.recipe.RecipeManager;
import btw.item.BTWItems;
import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.mixin.interfaces.ItemInvoker;
import net.minecraft.src.Block;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;

import java.util.HashSet;
import java.util.Set;

public final class NMFoodSpoilage {
    public static final int RAW_FOOD_MAX_FRESHNESS_DAMAGE = 8;
    public static final int COOKED_MEAT_MAX_DRYING_DAMAGE = 12;

    private static final int RAW_SPOIL_INTERVAL = 12000;
    private static final int FISH_SPOIL_INTERVAL = 1200;
    private static final int COOKED_DRY_INTERVAL = 24000;

    private static final Set<Integer> RAW_FOODS = new HashSet<>();
    private static final Set<Integer> FAST_SPOILING_FISH = new HashSet<>();
    private static final Set<Integer> COOKED_MEATS = new HashSet<>();

    private NMFoodSpoilage() {
    }

    public static void init() {
        registerRawFood(Item.beefRaw);
        registerRawFood(Item.chickenRaw);
        registerFastSpoilingFish(Item.fishRaw);
        registerRawFood(Item.porkRaw);
        registerRawFood(BTWItems.rawMutton);
        registerRawFood(BTWItems.rawCheval);
        registerRawFood(BTWItems.rawLiver);
        registerRawFood(BTWItems.rawWolfChop);
        registerRawFood(BTWItems.rawMysteryMeat);
        registerRawFood(NMItems.creeperChop);
        registerRawFood(NMItems.calamari);
        for (Item fish : NMItems.getRawFish()) {
            registerFastSpoilingFish(fish);
        }

        registerCookedMeat(Item.beefCooked);
        registerCookedMeat(Item.chickenCooked);
        registerCookedMeat(Item.fishCooked);
        registerCookedMeat(Item.porkCooked);
        registerCookedMeat(BTWItems.cookedMutton);
        registerCookedMeat(BTWItems.cookedCheval);
        registerCookedMeat(BTWItems.cookedLiver);
        registerCookedMeat(BTWItems.cookedWolfChop);
        registerCookedMeat(BTWItems.cookedMysteryMeat);
        registerCookedMeat(NMItems.calamariRoast);
    }

    public static void addSnowRefreshRecipes() {
        addSnowRefreshRecipe(Item.beefRaw);
        addSnowRefreshRecipe(Item.chickenRaw);
        addSnowRefreshRecipe(Item.fishRaw);
        addSnowRefreshRecipe(Item.porkRaw);
        addSnowRefreshRecipe(BTWItems.rawMutton);
        addSnowRefreshRecipe(BTWItems.rawCheval);
        addSnowRefreshRecipe(BTWItems.rawLiver);
        addSnowRefreshRecipe(BTWItems.rawWolfChop);
        addSnowRefreshRecipe(BTWItems.rawMysteryMeat);
        addSnowRefreshRecipe(NMItems.creeperChop);
        addSnowRefreshRecipe(NMItems.calamari);
        for (Item fish : NMItems.getRawFish()) {
            addSnowRefreshRecipe(fish);
        }
    }

    public static void updateFoodSpoilage(ItemStack stack, World world, EntityPlayer player, int inventorySlot) {
        if (world.isRemote || stack == null || player == null || inventorySlot < 0) {
            return;
        }

        if (FAST_SPOILING_FISH.contains(stack.itemID)) {
            damageFoodStack(stack, world, player, inventorySlot, FISH_SPOIL_INTERVAL, new ItemStack(BTWItems.foulFood, stack.stackSize));
        } else if (RAW_FOODS.contains(stack.itemID)) {
            damageFoodStack(stack, world, player, inventorySlot, RAW_SPOIL_INTERVAL, new ItemStack(BTWItems.foulFood, stack.stackSize));
        } else if (COOKED_MEATS.contains(stack.itemID)) {
            damageFoodStack(stack, world, player, inventorySlot, COOKED_DRY_INTERVAL, new ItemStack(BTWItems.curedMeat, stack.stackSize));
        }
    }

    private static void damageFoodStack(ItemStack stack, World world, EntityPlayer player, int inventorySlot, int interval, ItemStack expiredStack) {
        long staggeredTime = world.getTotalWorldTime() + (long)inventorySlot * 37L + stack.itemID;
        if (staggeredTime % interval != 0L) {
            return;
        }

        int nextDamage = stack.getItemDamage() + 1;
        if (nextDamage >= stack.getMaxDamage()) {
            player.inventory.setInventorySlotContents(inventorySlot, expiredStack);
        } else {
            stack.setItemDamage(nextDamage);
        }
    }

    private static void registerRawFood(Item item) {
        if (item == null) {
            return;
        }

        RAW_FOODS.add(item.itemID);
        ((ItemInvoker)item).invokeSetMaxDamage(RAW_FOOD_MAX_FRESHNESS_DAMAGE);
    }

    private static void registerFastSpoilingFish(Item item) {
        if (item == null) {
            return;
        }

        FAST_SPOILING_FISH.add(item.itemID);
        ((ItemInvoker)item).invokeSetMaxDamage(4);
    }

    private static void registerCookedMeat(Item item) {
        if (item == null) {
            return;
        }

        COOKED_MEATS.add(item.itemID);
        ((ItemInvoker)item).invokeSetMaxDamage(COOKED_MEAT_MAX_DRYING_DAMAGE);
    }

    private static void addSnowRefreshRecipe(Item item) {
        RecipeManager.addShapelessRecipe(new ItemStack(item, 1, 0), new Object[]{new ItemStack(item, 1, Short.MAX_VALUE), Item.snowball});
        RecipeManager.addShapelessRecipe(new ItemStack(item, 1, 0), new Object[]{new ItemStack(item, 1, Short.MAX_VALUE), Block.snow});
        RecipeManager.addShapelessRecipe(new ItemStack(item, 1, 0), new Object[]{new ItemStack(item, 1, Short.MAX_VALUE), Block.blockSnow});
    }
}
