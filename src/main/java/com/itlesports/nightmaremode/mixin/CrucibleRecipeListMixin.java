package com.itlesports.nightmaremode.mixin;

import btw.block.BTWBlocks;
import btw.crafting.manager.CrucibleStokedCraftingManager;
import btw.crafting.recipe.CrucibleRecipeList;
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

@Mixin(CrucibleRecipeList.class)
public class CrucibleRecipeListMixin {
    @Inject(method = "addRecipes", at = @At("TAIL"),remap = false)
    private static void addBloodCrucibleRecipes(CallbackInfo ci){
        // remove vanilla helmet recipe because it returns 6 ingots instead of 5
        CrucibleStokedCraftingManager.getInstance().removeRecipe(new ItemStack(BTWItems.diamondIngot, 6), new ItemStack[]{new ItemStack(Item.helmetDiamond, 1, Short.MAX_VALUE)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(BTWItems.diamondIngot, 5), new ItemStack[]{new ItemStack(Item.helmetDiamond, 1, Short.MAX_VALUE)});

        // remove all gold recipes from crucible
        CrucibleStokedCraftingManager.getInstance().removeRecipe(new ItemStack(Item.ingotGold, 2), new ItemStack[]{new ItemStack(Item.pickaxeGold, 1, Short.MAX_VALUE)});
        CrucibleStokedCraftingManager.getInstance().removeRecipe(new ItemStack(Item.goldNugget, 12), new ItemStack[]{new ItemStack(Item.axeGold, 1, Short.MAX_VALUE)});
        CrucibleStokedCraftingManager.getInstance().removeRecipe(new ItemStack(Item.goldNugget, 12), new ItemStack[]{new ItemStack(Item.swordGold, 1, Short.MAX_VALUE)});
        CrucibleStokedCraftingManager.getInstance().removeRecipe(new ItemStack(Item.goldNugget, 6), new ItemStack[]{new ItemStack(Item.hoeGold, 1, Short.MAX_VALUE)});
        CrucibleStokedCraftingManager.getInstance().removeRecipe(new ItemStack(Item.goldNugget, 6), new ItemStack[]{new ItemStack(Item.shovelGold, 1, Short.MAX_VALUE)});
        CrucibleStokedCraftingManager.getInstance().removeRecipe(new ItemStack(Item.goldNugget, 30), new ItemStack[]{new ItemStack(Item.helmetGold, 1, Short.MAX_VALUE)});
        CrucibleStokedCraftingManager.getInstance().removeRecipe(new ItemStack(Item.goldNugget, 48), new ItemStack[]{new ItemStack(Item.plateGold, 1, Short.MAX_VALUE)});
        CrucibleStokedCraftingManager.getInstance().removeRecipe(new ItemStack(Item.goldNugget, 42), new ItemStack[]{new ItemStack(Item.legsGold, 1, Short.MAX_VALUE)});
        CrucibleStokedCraftingManager.getInstance().removeRecipe(new ItemStack(Item.goldNugget, 24), new ItemStack[]{new ItemStack(Item.bootsGold, 1, Short.MAX_VALUE)});
        CrucibleStokedCraftingManager.getInstance().removeRecipe(new ItemStack(Item.goldNugget, 3), new ItemStack[]{new ItemStack(Item.pocketSundial)});
        CrucibleStokedCraftingManager.getInstance().removeRecipe(new ItemStack(Item.ingotGold, 9), new ItemStack[]{new ItemStack(Block.blockGold)});
        CrucibleStokedCraftingManager.getInstance().removeRecipe(new ItemStack(Item.goldNugget, 5), new ItemStack[]{new ItemStack(BTWItems.ocularOfEnder)});
        CrucibleStokedCraftingManager.getInstance().removeRecipe(new ItemStack(Item.goldNugget, 11), new ItemStack[]{new ItemStack(BTWItems.enderSpectacles, 1, Short.MAX_VALUE)});
        CrucibleStokedCraftingManager.getInstance().removeRecipe(new ItemStack(Item.goldNugget, 6), new ItemStack[]{new ItemStack(BTWItems.goldenDung)});
        CrucibleStokedCraftingManager.getInstance().removeRecipe(new ItemStack(Item.goldNugget, 2), new ItemStack[]{new ItemStack(BTWItems.redstoneLatch)});
        CrucibleStokedCraftingManager.getInstance().removeRecipe(new ItemStack(Item.goldNugget, 2), new ItemStack[]{new ItemStack(BTWBlocks.redstoneClutch)});
        CrucibleStokedCraftingManager.getInstance().removeRecipe(new ItemStack(Item.goldNugget, 2), new ItemStack[]{new ItemStack(Block.music)});
        CrucibleStokedCraftingManager.getInstance().removeRecipe(new ItemStack(Item.goldNugget, 60), new ItemStack[]{new ItemStack(BTWBlocks.dormandSoulforge)});
        CrucibleStokedCraftingManager.getInstance().removeRecipe(new ItemStack(Item.goldNugget, 8), new ItemStack[]{new ItemStack(BTWBlocks.lightningRod)});
        CrucibleStokedCraftingManager.getInstance().removeRecipe(new ItemStack(Item.ingotGold, 4), new ItemStack[]{new ItemStack(Item.horseArmorGold)});
        // done removing all gold recipes from crucible


        // add my own gold recipes
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(Item.ingotGold, 1), new ItemStack[]{new ItemStack(Item.pickaxeGold, 1, Short.MAX_VALUE)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(Item.goldNugget, 6), new ItemStack[]{new ItemStack(Item.axeGold, 1, Short.MAX_VALUE)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(Item.goldNugget, 6), new ItemStack[]{new ItemStack(Item.swordGold, 1, Short.MAX_VALUE)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(Item.goldNugget, 3), new ItemStack[]{new ItemStack(Item.hoeGold, 1, Short.MAX_VALUE)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(Item.goldNugget, 3), new ItemStack[]{new ItemStack(Item.shovelGold, 1, Short.MAX_VALUE)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(Item.goldNugget, 15), new ItemStack[]{new ItemStack(Item.helmetGold, 1, Short.MAX_VALUE)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(Item.goldNugget, 24), new ItemStack[]{new ItemStack(Item.plateGold, 1, Short.MAX_VALUE)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(Item.goldNugget, 21), new ItemStack[]{new ItemStack(Item.legsGold, 1, Short.MAX_VALUE)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(Item.goldNugget, 12), new ItemStack[]{new ItemStack(Item.bootsGold, 1, Short.MAX_VALUE)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(Item.goldNugget, 2), new ItemStack[]{new ItemStack(Item.pocketSundial)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(Item.ingotGold, 9), new ItemStack[]{new ItemStack(Block.blockGold)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(Item.goldNugget, 3), new ItemStack[]{new ItemStack(BTWItems.ocularOfEnder)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(Item.goldNugget, 5), new ItemStack[]{new ItemStack(BTWItems.enderSpectacles, 1, Short.MAX_VALUE)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(Item.goldNugget, 3), new ItemStack[]{new ItemStack(BTWItems.goldenDung)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(Item.goldNugget, 1), new ItemStack[]{new ItemStack(BTWItems.redstoneLatch)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(Item.goldNugget, 1), new ItemStack[]{new ItemStack(BTWBlocks.redstoneClutch)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(Item.goldNugget, 1), new ItemStack[]{new ItemStack(Block.music)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(Item.goldNugget, 30), new ItemStack[]{new ItemStack(BTWBlocks.dormandSoulforge)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(Item.goldNugget, 4), new ItemStack[]{new ItemStack(BTWBlocks.lightningRod)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(Item.ingotGold, 2), new ItemStack[]{new ItemStack(Item.horseArmorGold)});
        // done adding




        // add blood armor and tool recipes
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(BTWItems.diamondIngot, 5), new ItemStack[]{new ItemStack(NMItems.bloodHelmet, 1, Short.MAX_VALUE)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(BTWItems.diamondIngot, 8), new ItemStack[]{new ItemStack(NMItems.bloodChestplate, 1, Short.MAX_VALUE)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(BTWItems.diamondIngot, 7), new ItemStack[]{new ItemStack(NMItems.bloodLeggings, 1, Short.MAX_VALUE)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(BTWItems.diamondIngot, 4), new ItemStack[]{new ItemStack(NMItems.bloodBoots, 1, Short.MAX_VALUE)});

        RecipeManager.addStokedCrucibleRecipe(new ItemStack(BTWItems.diamondIngot, 3), new ItemStack[]{new ItemStack(NMItems.bloodPickaxe, 1, Short.MAX_VALUE)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(BTWItems.diamondIngot, 2), new ItemStack[]{new ItemStack(NMItems.bloodSword, 1, Short.MAX_VALUE)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(BTWItems.diamondIngot, 2), new ItemStack[]{new ItemStack(NMItems.bloodAxe, 1, Short.MAX_VALUE)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(BTWItems.diamondIngot, 1), new ItemStack[]{new ItemStack(NMItems.bloodShovel, 1, Short.MAX_VALUE)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(BTWItems.diamondIngot, 1), new ItemStack[]{new ItemStack(NMItems.bloodHoe, 1, Short.MAX_VALUE)});
    }
}
