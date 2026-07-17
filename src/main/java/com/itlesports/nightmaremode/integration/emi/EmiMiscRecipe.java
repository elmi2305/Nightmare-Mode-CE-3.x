package com.itlesports.nightmaremode.integration.emi;

import com.itlesports.nightmaremode.crafting.recipe.types.MiscRecipe;
import emi.dev.emi.emi.EmiPort;
import emi.dev.emi.emi.api.recipe.EmiRecipe;
import emi.dev.emi.emi.api.recipe.EmiRecipeCategory;
import emi.dev.emi.emi.api.render.EmiTexture;
import emi.dev.emi.emi.api.stack.EmiIngredient;
import emi.dev.emi.emi.api.stack.EmiStack;
import emi.dev.emi.emi.api.widget.WidgetHolder;
import emi.shims.java.com.unascribed.retroemi.RetroEMI;
import java.util.List;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ResourceLocation;

public class EmiMiscRecipe implements EmiRecipe {
    private final ResourceLocation id;
    private final EmiIngredient input;
    private final EmiStack output;
    private final String description;

    public EmiMiscRecipe(MiscRecipe recipe, int index) {
        this.id = new ResourceLocation("nightmare", "misc/" + index);
        ItemStack input = recipe.getInput();
        this.input = input.getItemDamage() == Short.MAX_VALUE
                ? RetroEMI.wildcardIngredient(input)
                : EmiStack.of(input);
        this.output = EmiStack.of(recipe.getOutput());
        this.description = recipe.getDescription();
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return NightmareEmiRegistry.MISC;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return List.of(this.input);
    }

    @Override
    public List<EmiStack> getOutputs() {
        return List.of(this.output);
    }

    @Override
    public int getDisplayWidth() {
        return 88;
    }

    @Override
    public int getDisplayHeight() {
        return 38;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addSlot(this.input, 0, 0);
        widgets.addTexture(EmiTexture.EMPTY_ARROW, 24, 1);
        widgets.addSlot(this.output, 58, 0).recipeContext(this);
        widgets.addText(EmiPort.literal(this.description), 0, 27, 0x404040, false);
    }
}
