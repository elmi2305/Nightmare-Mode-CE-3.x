package com.itlesports.nightmaremode.client.emi;

import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.underworld.crafting.UnderforgeRecipe;
import com.itlesports.nightmaremode.underworld.crafting.UnderforgeRecipeManager;
import emi.dev.emi.emi.api.EmiRegistry;
import emi.dev.emi.emi.api.recipe.BasicEmiRecipe;
import emi.dev.emi.emi.api.recipe.EmiRecipeCategory;
import emi.dev.emi.emi.api.render.EmiTexture;
import emi.dev.emi.emi.api.stack.EmiStack;
import emi.dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ResourceLocation;

public final class UnderforgeEmiRecipes {
    public static final EmiRecipeCategory CATEGORY = new EmiRecipeCategory(
            new ResourceLocation("nightmare", "underforge"), EmiStack.of(NMBlocks.underforge));

    private UnderforgeEmiRecipes() {}

    public static void register(EmiRegistry registry) {
        registry.addCategory(CATEGORY);
        registry.addWorkstation(CATEGORY, EmiStack.of(NMBlocks.underforge));
        int index = 0;
        for (UnderforgeRecipe recipe : UnderforgeRecipeManager.getRecipes()) {
            registry.addRecipe(new DisplayRecipe(recipe, index++));
        }
    }

    private static class DisplayRecipe extends BasicEmiRecipe {
        private final EmiStack[] roleStacks = new EmiStack[4];

        DisplayRecipe(UnderforgeRecipe recipe, int index) {
            super(CATEGORY, new ResourceLocation("nightmare", "underforge/" + index), 152, 38);
            ItemStack[] source = {recipe.getBase(), recipe.getMetal(), recipe.getFlux(), recipe.getFuel()};
            for (int slot = 0; slot < source.length; slot++) {
                roleStacks[slot] = source[slot] == null ? EmiStack.EMPTY : EmiStack.of(source[slot]);
                if (source[slot] != null) this.inputs.add(roleStacks[slot]);
            }
            this.outputs.add(EmiStack.of(recipe.getOutputTemplate()));
        }

        @Override
        public void addWidgets(WidgetHolder widgets) {
            int[] x = {0, 27, 54, 81};
            for (int slot = 0; slot < roleStacks.length; slot++) {
                widgets.addSlot(roleStacks[slot], x[slot], 10);
                if (slot < roleStacks.length - 1) widgets.addTexture(EmiTexture.PLUS, x[slot] + 17, 12);
            }
            widgets.addTexture(EmiTexture.EMPTY_ARROW, 105, 10);
            widgets.addSlot(this.outputs.get(0), 132, 10).recipeContext(this);
        }
    }
}
