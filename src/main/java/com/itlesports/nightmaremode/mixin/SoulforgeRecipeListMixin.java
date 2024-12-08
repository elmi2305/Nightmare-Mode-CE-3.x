package com.itlesports.nightmaremode.mixin;

import btw.block.BTWBlocks;
import btw.crafting.recipe.RecipeManager;
import btw.crafting.recipe.SoulforgeRecipeList;
import btw.item.BTWItems;
import net.minecraft.src.Block;
import net.minecraft.src.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoulforgeRecipeList.class)
public class SoulforgeRecipeListMixin {
    @Inject(method = "addRecipes", at = @At("TAIL"),remap = false)
    private static void addAdditionalRecipes(CallbackInfo ci){
        RecipeManager.removeSoulforgeRecipe(new ItemStack(BTWItems.canvas), new Object[]{"MMMM", "MFFM", "MFFM", "MMMM", Character.valueOf('F'), BTWItems.fabric, Character.valueOf('M'), new ItemStack(BTWItems.woodMouldingStubID, 1, Short.MAX_VALUE)});
        RecipeManager.addSoulforgeRecipe(new ItemStack(Block.cobblestoneMossy, 4),new Object[]{"####", "#XX#", "#XX#", "####", Character.valueOf('#'), Block.vine, Character.valueOf('X'), BTWBlocks.looseCobblestone});
    }
}
