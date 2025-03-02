package com.itlesports.nightmaremode.mixin;

import btw.block.BTWBlocks;
import btw.crafting.recipe.CraftingRecipeList;
import btw.crafting.recipe.RecipeManager;
import btw.item.BTWItems;
import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.Block;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CraftingRecipeList.class)
public class CraftingRecipeListMixin {
    @Inject(method = "addBlockRecipes",at = @At("TAIL"),remap = false)
    private static void addAdditionalBlocks(CallbackInfo ci){
        RecipeManager.addRecipe(new ItemStack(BTWBlocks.planter, 1), new Object[]{"# #", "# #", "###", Character.valueOf('#'), Item.brick});
        RecipeManager.addRecipe(new ItemStack(Block.bookShelf), new Object[]{"###", "XXX", "###", Character.valueOf('#'), new ItemStack(BTWItems.woodSidingStubID, 1, Short.MAX_VALUE), Character.valueOf('X'), new ItemStack(Item.book,1,Short.MAX_VALUE)});
    }
    @Inject(method = "addRecipes", at = @At("TAIL"),remap = false)
    private static void addNightmareItemRecipes(CallbackInfo ci){
        // add gapple and carrot recipes
        RecipeManager.removeVanillaRecipe(new ItemStack(Item.appleGold, 1, 0), new Object[]{"###", "#X#", "###", '#', Item.ingotGold, 'X', Item.appleRed});
        RecipeManager.removeVanillaRecipe(new ItemStack(Item.appleGold, 1, 1), new Object[]{"###", "#X#", "###", '#', Block.blockGold, 'X', Item.appleRed});
        RecipeManager.addRecipe(new ItemStack(Item.appleGold,1,1), new Object[]{"###", "#X#", "###", Character.valueOf('#'), new ItemStack(Item.ingotGold, 1, Short.MAX_VALUE), Character.valueOf('X'), new ItemStack(Item.appleRed,1,Short.MAX_VALUE)});
        RecipeManager.addRecipe(new ItemStack(Item.appleGold), new Object[]{"###", "#X#", "###", Character.valueOf('#'), new ItemStack(Item.goldNugget, 1, Short.MAX_VALUE), Character.valueOf('X'), new ItemStack(Item.appleRed,1,Short.MAX_VALUE)});
        RecipeManager.addRecipe(new ItemStack(NMItems.dungApple), new Object[]{"###", "#X#", "###", Character.valueOf('#'), new ItemStack(BTWItems.dung, 1), Character.valueOf('X'), new ItemStack(Item.appleRed,1,Short.MAX_VALUE)});
        RecipeManager.removeVanillaRecipe(new ItemStack(Item.goldenCarrot, 1, 0), new Object[]{"###", "#X#", "###", Character.valueOf('#'), Item.goldNugget, Character.valueOf('X'), BTWItems.carrot});
        RecipeManager.addRecipe(new ItemStack(Item.goldenCarrot, 1, 0), new Object[]{" # ", "#X#", " # ", Character.valueOf('#'), Item.goldNugget, Character.valueOf('X'), BTWItems.carrot});
        // done with gapples and carrots

        // fishing recipes
        RecipeManager.removeVanillaShapelessRecipe(new ItemStack(Item.fishingRod), new Object[]{new ItemStack(Item.stick), new ItemStack(Item.silk), new ItemStack(Item.silk), new ItemStack(BTWItems.ironNugget)});
        RecipeManager.removeVanillaShapelessRecipe(new ItemStack(Item.fishingRod), new Object[]{new ItemStack(Item.stick), new ItemStack(BTWItems.sinew), new ItemStack(BTWItems.sinew), new ItemStack(BTWItems.ironNugget)});
        RecipeManager.removeVanillaShapelessRecipe(new ItemStack(Item.fishingRod), new Object[]{new ItemStack(Item.stick), new ItemStack(BTWItems.sinew), new ItemStack(BTWItems.sinew), new ItemStack(BTWItems.boneFishHook)});
        RecipeManager.addRecipe(new ItemStack(NMItems.ironFishingPole,1), new Object[]{"  #", " #X", "Y #", Character.valueOf('#'), BTWItems.ironNugget, Character.valueOf('X'), BTWItems.rope, Character.valueOf('Y'), Item.ingotIron});
        // fishing recipes added

        // add misc recipes
        RecipeManager.addRecipe(new ItemStack(NMItems.eclipseBow,1), new Object[]{"###", "#X#", "###", Character.valueOf('#'), new ItemStack(NMItems.darksunFragment, 1), Character.valueOf('X'), new ItemStack(BTWItems.compositeBow)});
        RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.wickerPane, 8), new Object[]{new ItemStack(BTWBlocks.hamper)});
        RecipeManager.addRecipe(new ItemStack(BTWItems.canvas,1), new Object[]{"###", "#X#", "###", Character.valueOf('#'), new ItemStack(BTWItems.woodMouldingStubID, 1, Short.MAX_VALUE), Character.valueOf('X'), new ItemStack(BTWItems.fabric)});
        RecipeManager.addRecipe(new ItemStack(NMItems.ironKnittingNeedles,1), new Object[]{"# #", "# #", "#X#", Character.valueOf('#'), BTWItems.ironNugget, Character.valueOf('X'), Item.silk});
        RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.wickerPane,1), new Object[]{new ItemStack(NMItems.ironKnittingNeedles,1,Short.MAX_VALUE),Item.reed,Item.reed,Item.reed,Item.reed});
        RecipeManager.addShapelessRecipe(new ItemStack(Item.silk,1), new Object[]{new ItemStack(NMItems.ironKnittingNeedles,1,Short.MAX_VALUE),BTWItems.tangledWeb});
        for (int i = 0; i < 16; i++) {
            RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.woolKnit,1, i), new Object[]{new ItemStack(NMItems.ironKnittingNeedles,1,Short.MAX_VALUE),new ItemStack(BTWItems.wool, 1, i),new ItemStack(BTWItems.wool, 1, i)});
        }
        RecipeManager.addShapelessRecipe(new ItemStack(NMItems.bandage,2), new Object[]{BTWItems.wickerPane, new ItemStack(BTWItems.wool, 1, Short.MAX_VALUE), new ItemStack(BTWItems.wool, 1, Short.MAX_VALUE), Item.silk});
        RecipeManager.addShapelessRecipe(new ItemStack(NMItems.bandage,2), new Object[]{new ItemStack(BTWItems.woolKnit, 1, Short.MAX_VALUE), new ItemStack(BTWItems.wool, 1, Short.MAX_VALUE), new ItemStack(BTWItems.wool, 1, Short.MAX_VALUE), Item.silk});

        RecipeManager.addShapelessRecipe(new ItemStack(NMItems.steelBunch,1), new Object[]{new ItemStack(BTWItems.steelNugget),new ItemStack(BTWItems.steelNugget),new ItemStack(BTWItems.steelNugget,4),new ItemStack(BTWItems.steelNugget)});
        RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.steelNugget, 4), new Object[]{new ItemStack(NMItems.steelBunch)});

        RecipeManager.addShapelessRecipe(new ItemStack(NMItems.magicArrow, 4), new Object[]{new ItemStack(NMItems.magicFeather), new ItemStack(Item.stick), new ItemStack(BTWItems.soulFlux), new ItemStack(BTWItems.broadheadArrowHead)});
        // done adding misc recipes

        // fish sandwich
        RecipeManager.removeVanillaShapelessRecipe(new ItemStack(BTWItems.tastySandwich, 2), new Object[]{new ItemStack(Item.bread), new ItemStack(Item.fishCooked)});
        RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.tastySandwich, 1), new Object[]{new ItemStack(Item.bread), new ItemStack(Item.fishCooked)});
        // fish sandwich end

        // remove sinew recipes, add custom ones
        RecipeManager.removeVanillaShapelessRecipe(new ItemStack(BTWItems.sinewExtractingBeef, 1, 600), new Object[]{new ItemStack(Item.beefCooked), new ItemStack(Item.beefCooked), new ItemStack(BTWItems.sharpStone)});
        RecipeManager.removeVanillaShapelessRecipe(new ItemStack(BTWItems.sinewExtractingWolf, 1, 600), new Object[]{new ItemStack(BTWItems.cookedWolfChop), new ItemStack(BTWItems.cookedWolfChop), new ItemStack(BTWItems.sharpStone)});

        RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.sinewExtractingBeef, 1, 600), new Object[]{new ItemStack(Item.beefCooked), new ItemStack(BTWItems.sharpStone)});
        RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.sinewExtractingBeef, 1, 600), new Object[]{new ItemStack(Item.porkCooked), new ItemStack(BTWItems.sharpStone)});
        RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.sinewExtractingWolf, 1, 600), new Object[]{new ItemStack(BTWItems.cookedMutton), new ItemStack(BTWItems.sharpStone)});
        RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.sinewExtractingWolf, 1, 600), new Object[]{new ItemStack(BTWItems.cookedWolfChop), new ItemStack(BTWItems.sharpStone)});
        // done with sinew

        // add blood recipes
        RecipeManager.addRecipe(new ItemStack(NMItems.bloodIngot), new Object[]{" # ", "#X#", " # ", Character.valueOf('#'), new ItemStack(NMItems.bloodOrb), Character.valueOf('X'), new ItemStack(BTWItems.diamondIngot)});

        RecipeManager.addRecipe(new ItemStack(NMItems.bloodHelmet), new Object[]{"###", "# #", Character.valueOf('#'), new ItemStack(NMItems.bloodIngot)});
        RecipeManager.addRecipe(new ItemStack(NMItems.bloodChestplate), new Object[]{"# #", "###", "###", Character.valueOf('#'), new ItemStack(NMItems.bloodIngot)});
        RecipeManager.addRecipe(new ItemStack(NMItems.bloodLeggings), new Object[]{"###", "# #", "# #", Character.valueOf('#'), new ItemStack(NMItems.bloodIngot)});
        RecipeManager.addRecipe(new ItemStack(NMItems.bloodBoots), new Object[]{"# #", "# #",  Character.valueOf('#'), new ItemStack(NMItems.bloodIngot)});

        RecipeManager.addRecipe(new ItemStack(NMItems.bloodSword), new Object[]{" # ", " # ", " X ", Character.valueOf('#'), new ItemStack(NMItems.bloodIngot), Character.valueOf('X'), new ItemStack(Item.stick)});
        RecipeManager.addRecipe(new ItemStack(NMItems.bloodPickaxe), new Object[]{"###", " X ", " X ", Character.valueOf('#'), new ItemStack(NMItems.bloodIngot), Character.valueOf('X'), new ItemStack(Item.stick)});
        RecipeManager.addRecipe(new ItemStack(NMItems.bloodAxe), new Object[]{"#  ", "#X ", " X ", Character.valueOf('#'), new ItemStack(NMItems.bloodIngot), Character.valueOf('X'), new ItemStack(Item.stick)});
        RecipeManager.addRecipe(new ItemStack(NMItems.bloodShovel), new Object[]{" # ", " X ", " X ", Character.valueOf('#'), new ItemStack(NMItems.bloodIngot), Character.valueOf('X'), new ItemStack(Item.stick)});
        RecipeManager.addRecipe(new ItemStack(NMItems.bloodHoe), new Object[]{"#X ", " X ", " X ", Character.valueOf('#'), new ItemStack(NMItems.bloodIngot), Character.valueOf('X'), new ItemStack(Item.stick)});

        RecipeManager.addRecipe(new ItemStack(BTWItems.rawMysteryMeat), new Object[]{"###", "#X#", "###", Character.valueOf('#'), new ItemStack(NMItems.bloodOrb), Character.valueOf('X'), new ItemStack(Item.beefRaw)});
        RecipeManager.addRecipe(new ItemStack(BTWItems.rawMysteryMeat), new Object[]{"###", "#X#", "###", Character.valueOf('#'), new ItemStack(NMItems.bloodOrb), Character.valueOf('X'), new ItemStack(BTWItems.rawCheval)});
        RecipeManager.addRecipe(new ItemStack(BTWItems.rawMysteryMeat), new Object[]{"###", "#X#", "###", Character.valueOf('#'), new ItemStack(NMItems.bloodOrb), Character.valueOf('X'), new ItemStack(Item.porkRaw)});
        // done adding blood recipes

        // add blaze rod recipes

        RecipeManager.addShapelessRecipe(new ItemStack(NMItems.elementalRod, 1), new Object[]{NMItems.shadowRod, Item.blazeRod, NMItems.fireRod, NMItems.waterRod});
        // done adding blaze rod recipes

        RecipeManager.addRecipe(new ItemStack(Item.book), new Object[]{"###", "XXX", Character.valueOf('#'), BTWItems.cutScouredLeather, Character.valueOf('X'), new ItemStack(Item.paper, 1, Short.MAX_VALUE)});
        RecipeManager.addRecipe(new ItemStack(Item.book), new Object[]{"###", "XXX", Character.valueOf('#'), BTWItems.cutTannedLeather, Character.valueOf('X'), new ItemStack(Item.paper, 1, Short.MAX_VALUE)});

        RecipeManager.removeVanillaShapelessRecipe(new ItemStack(Item.gunpowder), new Object[]{new ItemStack(BTWItems.nitre), new ItemStack(BTWItems.brimstone), new ItemStack(BTWItems.coalDust)});
    }

    @Inject(method = "addAlternateVanillaRecipes", at = @At("TAIL"),remap = false)
    private static void removeStupidRecipes(CallbackInfo ci){
        RecipeManager.removeVanillaRecipe(new ItemStack(Item.helmetDiamond), new Object[]{"XXX", "XYX", 'X', BTWItems.diamondIngot, 'Y', BTWItems.diamondArmorPlate});
        RecipeManager.removeVanillaRecipe(new ItemStack(Item.plateDiamond), new Object[]{"Y Y", "XXX", "XXX", 'X', BTWItems.diamondIngot, 'Y', BTWItems.diamondArmorPlate});
        RecipeManager.removeVanillaRecipe(new ItemStack(Item.legsDiamond), new Object[]{"XXX", "Y Y", "Y Y", 'X', BTWItems.diamondIngot, 'Y', BTWItems.diamondArmorPlate});

        RecipeManager.addRecipe(new ItemStack(Item.helmetDiamond), new Object[]{"###", "# #", "   ", Character.valueOf('#'), new ItemStack(BTWItems.diamondIngot)});
        RecipeManager.addRecipe(new ItemStack(Item.plateDiamond), new Object[]{"# #", "###", "###", Character.valueOf('#'), new ItemStack(BTWItems.diamondIngot)});
        RecipeManager.addRecipe(new ItemStack(Item.legsDiamond), new Object[]{"###", "# #", "# #", Character.valueOf('#'), new ItemStack(BTWItems.diamondIngot)});
    }
}
