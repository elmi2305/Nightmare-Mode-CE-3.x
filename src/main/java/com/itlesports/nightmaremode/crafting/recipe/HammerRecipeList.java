package com.itlesports.nightmaremode.crafting.recipe;

import btw.item.BTWItems;
import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.crafting.manager.HammerCraftingManager;
import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.Block;
import net.minecraft.src.ItemStack;

public abstract class HammerRecipeList {
    public static void addRecipes() {

        HammerCraftingManager.instance.addRecipe(new ItemStack(NMItems.ovenPart, 1), Block.stoneSingleSlab, new int[]{4, 12}).setCanBeMinedByAnyHammer();
        HammerCraftingManager.instance.addRecipe(new ItemStack(BTWItems.ironNugget, 1), NMBlocks.ironBloom, new int[]{0, 1, 2, 3, 4, 5, 6, 7}).setHitsRequired(8);

        HammerCraftingManager.instance.addItemRecipe(new ItemStack(NMItems.ovenPart, 1), new ItemStack(Block.stoneSingleSlab, 1, 4));
        HammerCraftingManager.instance.addItemRecipe(new ItemStack(NMItems.ovenPart, 1), new ItemStack(Block.stoneSingleSlab, 1, 12));
        HammerCraftingManager.instance.addItemRecipe(new ItemStack(BTWItems.ironNugget, 1), new ItemStack(NMItems.ironBloom)).setHitsRequired(8);
        HammerCraftingManager.instance.addItemRecipe(new ItemStack(NMItems.crushedNickelRock), new ItemStack(NMItems.rawNickelRock)).setHitsRequired(2);
        HammerCraftingManager.instance.addItemRecipe(new ItemStack(NMItems.hammeredLithium), new ItemStack(NMItems.rawLithium)).setHitsRequired(2);
        HammerCraftingManager.instance.addItemRecipe(new ItemStack(NMItems.polishedCrystalShard), new ItemStack(NMItems.cleanCrystalShard, 1, Short.MAX_VALUE)).setHitsRequired(4);
        HammerCraftingManager.instance.addItemRecipe(new ItemStack(NMItems.crackedDiamondBearingRock), new ItemStack(NMItems.diamondBearingRock)).setHitsRequired(3);
        HammerCraftingManager.instance.addItemRecipe(new ItemStack(NMItems.nickelPlate), new ItemStack(NMItems.nickelIngot)).setHitsRequired(4);

    }
}
