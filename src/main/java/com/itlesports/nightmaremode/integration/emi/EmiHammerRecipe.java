package com.itlesports.nightmaremode.integration.emi;

import com.itlesports.nightmaremode.crafting.recipe.types.HammerRecipe;
import emi.dev.emi.emi.EmiPort;
import emi.dev.emi.emi.api.recipe.EmiRecipe;
import emi.dev.emi.emi.api.recipe.EmiRecipeCategory;
import emi.dev.emi.emi.api.stack.EmiIngredient;
import emi.dev.emi.emi.api.stack.EmiStack;
import emi.dev.emi.emi.api.widget.WidgetHolder;
import emi.shims.java.com.unascribed.retroemi.RetroEMI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ResourceLocation;

public class EmiHammerRecipe implements EmiRecipe {
    private final ResourceLocation id;
    private final EmiIngredient input;
    private final List<EmiStack> outputs;
    private final int hits;
    private final int experienceCost;
    private final boolean blockRecipe;

    public EmiHammerRecipe(HammerRecipe recipe, int index) {
        this.id = new ResourceLocation("nightmare", "hammering/" + index);
        this.input = createInput(recipe);
        this.outputs = Arrays.stream(recipe.getOutput()).map(EmiStack::of).collect(Collectors.toList());
        this.hits = recipe.getHitsRequired();
        this.experienceCost = recipe.getExperienceCost();
        this.blockRecipe = recipe.isBlockRecipe();
    }

    private static EmiIngredient createInput(HammerRecipe recipe) {
        if (recipe.isItemRecipe()) {
            ItemStack input = recipe.getInput();
            return input.getItemDamage() == Short.MAX_VALUE
                    ? RetroEMI.wildcardIngredient(input)
                    : EmiStack.of(input);
        }

        if (recipe.ignoreMetadata()) {
            return RetroEMI.wildcardIngredient(new ItemStack(recipe.getInputBlock(), 1, Short.MAX_VALUE));
        }

        List<EmiIngredient> inputs = new ArrayList<>();
        for (int metadata : recipe.getInputMetadata()) {
            inputs.add(EmiStack.of(new ItemStack(recipe.getInputBlock(), 1, metadata)));
        }
        return EmiIngredient.of(inputs);
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return NightmareEmiRegistry.HAMMERING;
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
        return this.outputs;
    }

    @Override
    public int getDisplayWidth() {
        return 110;
    }

    @Override
    public int getDisplayHeight() {
        return Math.max(38, ((this.outputs.size() + 2) / 3) * 18);
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addSlot(this.input, 0, 0).appendTooltip(EmiPort.literal(
                this.blockRecipe ? "Placed-block hammering" : "Anvil hammering"));
        widgets.addFillingArrow(24, 1, Math.max(500, this.hits * 500));
        for (int i = 0; i < this.outputs.size(); ++i) {
            widgets.addSlot(this.outputs.get(i), 56 + i % 3 * 18, i / 3 * 18).recipeContext(this);
        }
        String requirements = this.hits + (this.hits == 1 ? " hit" : " hits");
        if (this.experienceCost > 0) {
            requirements += " | " + this.experienceCost + " XP";
        }
        widgets.addText(EmiPort.literal(requirements), 0, 27, 0x404040, false);
    }
}
