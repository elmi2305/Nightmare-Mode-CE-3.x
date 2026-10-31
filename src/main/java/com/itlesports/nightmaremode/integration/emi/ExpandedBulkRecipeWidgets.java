package com.itlesports.nightmaremode.integration.emi;

import emi.dev.emi.emi.api.plugin.BTWPlugin;
import emi.dev.emi.emi.api.recipe.BTWEmiRecipeCategories;
import emi.dev.emi.emi.api.recipe.EmiRecipe;
import emi.dev.emi.emi.api.recipe.EmiRecipeCategory;
import emi.dev.emi.emi.api.render.EmiTexture;
import emi.dev.emi.emi.api.stack.EmiIngredient;
import emi.dev.emi.emi.api.stack.EmiStack;
import emi.dev.emi.emi.api.widget.WidgetHolder;
import emi.shims.java.net.minecraft.text.Text;

import java.util.List;

public final class ExpandedBulkRecipeWidgets {
    private ExpandedBulkRecipeWidgets() {}

    public static void add(WidgetHolder widgets, List<EmiIngredient> input, List<EmiStack> output,
                           boolean stoked, EmiRecipeCategory category, EmiRecipe recipe) {
        int inputs = input.size(), outputs = output.size();
        widgets.addFillingArrow(55, EmiBulkLayout.arrowY(inputs, outputs), 10000);
        int heatY = EmiBulkLayout.heatY(inputs, outputs);
        if (stoked) {
            widgets.addTexture(EmiTexture.EMPTY_FLAME, 60, heatY);
            widgets.addAnimatedTexture(BTWPlugin.FULL_STOKED_FLAME, 60, heatY, 10000, false, true, true)
                    .tooltipText(List.of(Text.translatable("emi.info.stoked")));
        } else if (category == BTWEmiRecipeCategories.CAULDRON) {
            widgets.addTexture(EmiTexture.EMPTY_FLAME, 60, heatY);
            widgets.addAnimatedTexture(EmiTexture.FULL_FLAME, 60, heatY, 10000, false, true, true);
        } else if (category == BTWEmiRecipeCategories.MILLSTONE) {
            widgets.addTexture(BTWPlugin.FULL_GEAR, 60, heatY)
                    .tooltipText(List.of(Text.translatable("emi.info.mechanical")));
        }
        int inputY = EmiBulkLayout.inputY(inputs, outputs);
        for (int slot = 0; slot < EmiBulkLayout.rows(inputs) * 3; ++slot) {
            widgets.addSlot(slot < inputs ? input.get(slot) : EmiStack.EMPTY, slot % 3 * 18, inputY + slot / 3 * 18);
        }
        int outputY = EmiBulkLayout.outputY(inputs, outputs);
        for (int slot = 0; slot < outputs; ++slot) {
            widgets.addSlot(output.get(slot), 80 + slot % 2 * 26, outputY + slot / 2 * 26)
                    .large(true).recipeContext(recipe);
        }
    }
}
