package com.itlesports.nightmaremode.integration.emi;

import com.itlesports.nightmaremode.crafting.recipe.types.WashingRecipe;
import emi.dev.emi.emi.EmiPort;
import emi.dev.emi.emi.api.recipe.EmiRecipe;
import emi.dev.emi.emi.api.recipe.EmiRecipeCategory;
import emi.dev.emi.emi.api.stack.EmiIngredient;
import emi.dev.emi.emi.api.stack.EmiStack;
import emi.dev.emi.emi.api.widget.WidgetHolder;
import emi.shims.java.com.unascribed.retroemi.RetroEMI;
import java.util.List;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ResourceLocation;

public class EmiWashingRecipe implements EmiRecipe {
    private final ResourceLocation id;
    private final EmiIngredient input;
    private final EmiStack output;
    private final WashingRecipe.Method method;
    private final int duration;

    public EmiWashingRecipe(WashingRecipe recipe, int index) {
        this.id = new ResourceLocation("nightmare", "washing/" + index);
        ItemStack input = recipe.getInput();
        this.input = input.getItemDamage() == Short.MAX_VALUE
                ? RetroEMI.wildcardIngredient(input)
                : EmiStack.of(input);
        this.output = EmiStack.of(recipe.getOutput());
        this.method = recipe.getMethod();
        this.duration = recipe.getDuration();
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return NightmareEmiRegistry.WASHING;
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
        widgets.addFillingArrow(24, 1, this.method == WashingRecipe.Method.WATER
                ? Math.max(500, this.duration * 50)
                : 4000);
        widgets.addSlot(this.output, 58, 0).recipeContext(this);

        String description = this.method == WashingRecipe.Method.WATER
                ? "Water: " + this.formatTime()
                : "Rain";
        widgets.addText(EmiPort.literal(description), 0, 27, 0x404040, false);
    }

    private String formatTime() {
        if (this.duration % 20 == 0) {
            return this.duration / 20 + "s";
        }
        return String.format("%.1fs", this.duration / 20.0F);
    }
}
