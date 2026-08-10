package com.itlesports.nightmaremode.crafting.recipe;

import btw.block.BTWBlocks;
import btw.item.BTWItems;
import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.crafting.manager.HammerCraftingManager;
import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.Block;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;

public abstract class HammerRecipeList {
    public static void addRecipes() {

        HammerCraftingManager.instance.addRecipe(new ItemStack(NMItems.ovenPart, 1), Block.stoneSingleSlab, new int[]{4, 12}).setCanBeMinedByAnyHammer();
        HammerCraftingManager.instance.addRecipe(new ItemStack(BTWItems.sharpStone), BTWBlocks.looseCobblestoneSlab, new int[]{0, 4, 8}).setCanBeMinedByAnyHammer();
        HammerCraftingManager.instance.addRecipe(new ItemStack(BTWItems.ironNugget, 1), NMBlocks.ironBloom, new int[]{0, 1, 2, 3, 4, 5, 6, 7}).setHitsRequired(8);
        HammerCraftingManager.instance.addRecipe(new ItemStack(NMItems.obsidianPowder), Block.obsidian, new int[]{1}).setHitsRequired(8).setCanBeMinedByAnyHammer();

        HammerCraftingManager.instance.addItemRecipe(new ItemStack(NMItems.ovenPart, 1), new ItemStack(Block.stoneSingleSlab, 1, 4));
        HammerCraftingManager.instance.addItemRecipe(new ItemStack(NMItems.ovenPart, 1), new ItemStack(Block.stoneSingleSlab, 1, 12));
        HammerCraftingManager.instance.addItemRecipe(new ItemStack(BTWItems.ironNugget, 1), new ItemStack(NMItems.ironBloom)).setHitsRequired(8);
        HammerCraftingManager.instance.addItemRecipe(new ItemStack(NMItems.nickelCrushedRock), new ItemStack(NMItems.nickelRawRock)).setHitsRequired(2);
        HammerCraftingManager.instance.addItemRecipe(new ItemStack(NMItems.lithiumHammered), new ItemStack(NMItems.lithiumRaw)).setHitsRequired(2);
        HammerCraftingManager.instance.addItemRecipe(new ItemStack(NMItems.crystalPolishedShard), new ItemStack(NMItems.crystalCleanShard, 1, Short.MAX_VALUE)).setHitsRequired(4);
        HammerCraftingManager.instance.addItemRecipe(new ItemStack(NMItems.crackedDiamondBearingRock), new ItemStack(NMItems.diamondBearingRock)).setHitsRequired(3);
        HammerCraftingManager.instance.addItemRecipe(new ItemStack(NMItems.nickelPlate), new ItemStack(NMItems.nickelIngot)).setHitsRequired(4);
        HammerCraftingManager.instance.addItemRecipe(new ItemStack(NMItems.wetFusedPlantSheet), new ItemStack(NMItems.washedPith));
        HammerCraftingManager.instance.addItemRecipe(new ItemStack(BTWItems.hempFibers, 4), new ItemStack(NMItems.driedHemp)).setHitsRequired(3);
        HammerCraftingManager.instance.addItemRecipe(new ItemStack(NMItems.workedScouredLeather), new ItemStack(NMItems.washedScouredLeather)).setHitsRequired(3);
        HammerCraftingManager.instance.addItemRecipe(new ItemStack(NMItems.crushedTungsten, 2), new ItemStack(NMItems.tungstenChunk)).setHitsRequired(2);
        HammerCraftingManager.instance.addItemRecipe(new ItemStack(BTWItems.goldOrePile, 2), new ItemStack(BTWItems.goldOreChunk)).setHitsRequired(3);
        HammerCraftingManager.instance.addItemRecipe(new ItemStack(NMItems.tungstenPowder), new ItemStack(NMItems.brittleTungstenCake)).setHitsRequired(2);
        HammerCraftingManager.instance.addItemRecipe(new ItemStack(BTWItems.tannedLeather), new ItemStack(BTWItems.cutTannedLeather, 4)).setHitsRequired(3);
        HammerCraftingManager.instance.addItemRecipe(new ItemStack(Item.redstone), new ItemStack(NMItems.redstoneCrystal));
        HammerCraftingManager.instance.addItemRecipe(new ItemStack(Item.redstone, 4), new ItemStack(NMItems.refinedRedstone));
        HammerCraftingManager.instance.addItemRecipe(new ItemStack(NMItems.crushedAzureStone, 2), new ItemStack(NMItems.rawAzureStone)).setHitsRequired(2);
        HammerCraftingManager.instance.addItemRecipe(new ItemStack(Item.dyePowder, 4, 4), new ItemStack(NMItems.brittleAzureCake)).setHitsRequired(3);
        HammerCraftingManager.instance.addItemRecipe(new ItemStack(NMItems.pressedGlueCake), new ItemStack(NMItems.glueSlurry)).setHitsRequired(4);
        HammerCraftingManager.instance.addItemRecipe(new ItemStack(NMItems.hammeredStoneBrick), new ItemStack(NMItems.roughStoneBrick)).setHitsRequired(6);
        for (int woodType = 0; woodType < 5; ++woodType) {
            HammerCraftingManager.instance.addItemRecipe(
                    new ItemStack(Block.planks, 1, woodType),
                    new ItemStack(BTWItems.woodSidingStubID, 2, woodType)).setHitsRequired(2);
            HammerCraftingManager.instance.addItemRecipe(
                    new ItemStack(Block.planks, 1, woodType),
                    new ItemStack(Block.woodSingleSlab, 2, woodType)).setHitsRequired(2);
            HammerCraftingManager.instance.addItemRecipe(
                    new ItemStack(BTWItems.woodSidingStubID, 1, woodType),
                    new ItemStack(BTWItems.woodMouldingStubID, 2, woodType)).setHitsRequired(2);
            HammerCraftingManager.instance.addItemRecipe(
                    new ItemStack(BTWItems.woodMouldingStubID, 1, woodType),
                    new ItemStack(BTWItems.woodCornerStubID, 2, woodType)).setHitsRequired(2);
        }

    }
}
