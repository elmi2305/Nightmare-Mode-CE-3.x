package com.itlesports.nightmaremode.underworld.crafting;

import btw.item.BTWItems;
import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class UnderforgeRecipeManager {
    private static final List<UnderforgeRecipe> RECIPES = new ArrayList<>();

    private UnderforgeRecipeManager() {}

    public static void registerDefaults() {
        if (!RECIPES.isEmpty()) return;
        add(new ItemStack(NMItems.rawTitanium), null, null, new ItemStack(NMItems.bloomResin), new ItemStack(NMItems.titaniumIngot), false);
        add(new ItemStack(NMItems.rawTungsten), null, new ItemStack(NMItems.blastDust), new ItemStack(NMItems.cinderResin), new ItemStack(NMItems.tungstenIngot), false);
        add(new ItemStack(BTWItems.soulforgedSteelIngot), new ItemStack(NMItems.titaniumIngot, 2), new ItemStack(NMItems.underwebThread), new ItemStack(NMItems.bloomResin), new ItemStack(NMItems.titaniumSteelPlate), false);
        add(new ItemStack(BTWItems.soulforgedSteelIngot), new ItemStack(NMItems.tungstenIngot, 2), new ItemStack(NMItems.blastDust), new ItemStack(NMItems.cinderResin), new ItemStack(NMItems.tungstenSteelPlate), false);

        // equipment upgrades are disabled; the redesigned ladder crafts each tier directly from native materials
        // addTitaniumTools();
        // addTungstenTools();
        // addTitaniumArmor();
        // addTungstenArmor();

        add(new ItemStack(NMItems.lucidFruit), new ItemStack(NMItems.brittleBone, 2), new ItemStack(NMItems.voidMembrane), new ItemStack(NMItems.cinderResin), new ItemStack(NMItems.clarityDraught), false);
        add(new ItemStack(Block.glass, 2), new ItemStack(NMItems.tungstenIngot), new ItemStack(NMItems.mycelialCore), new ItemStack(NMItems.cinderResin), new ItemStack(NMItems.tungstenLens, 2), false);
        add(new ItemStack(BTWItems.corpseEye), new ItemStack(NMItems.tungstenLens), new ItemStack(NMItems.cinderResin), new ItemStack(NMItems.voidMembrane), new ItemStack(Item.eyeOfEnder, 2), false);
    }

    private static void addTitaniumTools() {
        addUpgrade(BTWItems.steelPickaxe, NMItems.titaniumPickaxe, NMItems.titaniumSteelPlate, 3, NMItems.underwebThread, NMItems.bloomResin);
        addUpgrade(BTWItems.steelSword, NMItems.titaniumSword, NMItems.titaniumSteelPlate, 2, NMItems.underwebThread, NMItems.bloomResin);
        addUpgrade(BTWItems.steelAxe, NMItems.titaniumAxe, NMItems.titaniumSteelPlate, 3, NMItems.underwebThread, NMItems.bloomResin);
        addUpgrade(BTWItems.steelShovel, NMItems.titaniumShovel, NMItems.titaniumSteelPlate, 1, NMItems.underwebThread, NMItems.bloomResin);
        addUpgrade(BTWItems.steelHoe, NMItems.titaniumHoe, NMItems.titaniumSteelPlate, 2, NMItems.underwebThread, NMItems.bloomResin);
    }

    private static void addTungstenTools() {
        addUpgrade(NMItems.titaniumPickaxe, NMItems.tungstenPickaxe, NMItems.tungstenSteelPlate, 3, NMItems.blastDust, NMItems.cinderResin);
        addUpgrade(NMItems.titaniumSword, NMItems.tungstenSword, NMItems.tungstenSteelPlate, 2, NMItems.blastDust, NMItems.cinderResin);
        addUpgrade(NMItems.titaniumAxe, NMItems.tungstenAxe, NMItems.tungstenSteelPlate, 3, NMItems.blastDust, NMItems.cinderResin);
        addUpgrade(NMItems.titaniumShovel, NMItems.tungstenShovel, NMItems.tungstenSteelPlate, 1, NMItems.blastDust, NMItems.cinderResin);
        addUpgrade(NMItems.titaniumHoe, NMItems.tungstenHoe, NMItems.tungstenSteelPlate, 2, NMItems.blastDust, NMItems.cinderResin);
    }

    private static void addTitaniumArmor() {
        addArmorUpgrade(BTWItems.plateHelmet, NMItems.titaniumHelmet, NMItems.titaniumSteelPlate, 4, NMItems.lucidPetal, NMItems.bloomResin);
        addArmorUpgrade(BTWItems.plateBreastplate, NMItems.titaniumChestplate, NMItems.titaniumSteelPlate, 6, NMItems.lucidPetal, NMItems.bloomResin);
        addArmorUpgrade(BTWItems.plateLeggings, NMItems.titaniumLeggings, NMItems.titaniumSteelPlate, 5, NMItems.lucidPetal, NMItems.bloomResin);
        addArmorUpgrade(BTWItems.plateBoots, NMItems.titaniumBoots, NMItems.titaniumSteelPlate, 3, NMItems.lucidPetal, NMItems.bloomResin);
    }

    private static void addTungstenArmor() {
        addArmorUpgrade(NMItems.titaniumHelmet, NMItems.tungstenHelmet, NMItems.tungstenSteelPlate, 4, NMItems.voidMembrane, NMItems.cinderResin);
        addArmorUpgrade(NMItems.titaniumChestplate, NMItems.tungstenChestplate, NMItems.tungstenSteelPlate, 6, NMItems.voidMembrane, NMItems.cinderResin);
        addArmorUpgrade(NMItems.titaniumLeggings, NMItems.tungstenLeggings, NMItems.tungstenSteelPlate, 5, NMItems.voidMembrane, NMItems.cinderResin);
        addArmorUpgrade(NMItems.titaniumBoots, NMItems.tungstenBoots, NMItems.tungstenSteelPlate, 3, NMItems.voidMembrane, NMItems.cinderResin);
    }

    private static void addUpgrade(Item base, Item output, Item plate, int plates, Item flux, Item fuel) {
        add(new ItemStack(base, 1, Short.MAX_VALUE), new ItemStack(plate, plates), new ItemStack(flux), new ItemStack(fuel), new ItemStack(output), true);
    }

    private static void addArmorUpgrade(Item base, Item output, Item plate, int plates, Item flux, Item fuel) {
        addUpgrade(base, output, plate, plates, flux, fuel);
    }

    public static void add(ItemStack base, ItemStack metal, ItemStack flux, ItemStack fuel, ItemStack output, boolean transferToolData) {
        RECIPES.add(new UnderforgeRecipe(base, metal, flux, fuel, output, transferToolData));
    }

    public static UnderforgeRecipe find(ItemStack[] inventory) {
        for (UnderforgeRecipe recipe : RECIPES) if (recipe.matches(inventory)) return recipe;
        return null;
    }

    public static List<UnderforgeRecipe> getRecipes() { return Collections.unmodifiableList(RECIPES); }
}
