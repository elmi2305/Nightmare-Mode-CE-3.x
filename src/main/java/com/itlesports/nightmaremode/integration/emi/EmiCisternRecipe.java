package com.itlesports.nightmaremode.integration.emi;

import com.itlesports.nightmaremode.block.tileEntities.CisternTileEntity;
import com.itlesports.nightmaremode.crafting.recipe.types.CisternRecipe;
import emi.dev.emi.emi.EmiPort;
import emi.dev.emi.emi.api.recipe.EmiRecipe;
import emi.dev.emi.emi.api.recipe.EmiRecipeCategory;
import emi.dev.emi.emi.api.stack.EmiIngredient;
import emi.dev.emi.emi.api.stack.EmiStack;
import emi.dev.emi.emi.api.widget.WidgetHolder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ResourceLocation;

public class EmiCisternRecipe implements EmiRecipe {
    private final ResourceLocation id;
    private final List<EmiIngredient> inputs;
    private final List<EmiStack> outputs;
    private final int requiredFluid;
    private final int resultingFluid;
    private final boolean consumesFluid;
    private final int requiredHeat;
    private final int requiredStir;
    private final int duration;

    public EmiCisternRecipe(CisternRecipe recipe, int index) {
        this.id = new ResourceLocation("nightmare", "cistern/" + index);
        this.inputs = Arrays.stream(recipe.getInputs()).map(EmiStack::of).collect(Collectors.toList());
        this.outputs = new ArrayList<>();
        for (ItemStack output : recipe.getOutputs()) {
            this.outputs.add(EmiStack.of(output));
        }
        for (CisternRecipe.RandomOutput output : recipe.getRandomOutputs()) {
            this.outputs.add(EmiStack.of(output.getStack()).setChance(output.getChance()));
        }
        this.requiredFluid = recipe.getRequiredFluid();
        this.resultingFluid = recipe.getResultingFluid();
        this.consumesFluid = recipe.consumesFluid();
        this.requiredHeat = recipe.getRequiredHeat();
        this.requiredStir = recipe.getRequiredStir();
        this.duration = recipe.getDuration();
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return NightmareEmiRegistry.CISTERN;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return this.inputs;
    }

    @Override
    public List<EmiStack> getOutputs() {
        return this.outputs;
    }

    @Override
    public int getDisplayWidth() {
        return 128;
    }

    @Override
    public int getDisplayHeight() {
        return 56;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        for (int i = 0; i < 2; ++i) {
            if (i < this.inputs.size()) {
                widgets.addSlot(this.inputs.get(i), i * 18, 9);
            } else {
                widgets.addSlot(i * 18, 9);
            }
        }
        widgets.addFillingArrow(44, 10, Math.max(500, this.duration * 50));
        for (int i = 0; i < 4; ++i) {
            if (i < this.outputs.size()) {
                widgets.addSlot(this.outputs.get(i), 78 + i % 2 * 18, i / 2 * 18).recipeContext(this);
            } else {
                widgets.addSlot(78 + i % 2 * 18, i / 2 * 18);
            }
        }
        widgets.addText(EmiPort.literal(this.getRequirementsText()), 0, 39, 0x404040, false);
    }

    private String getRequirementsText() {
        String fluid = CisternTileEntity.getFluidDisplayName(this.requiredFluid);
        if (this.consumesFluid) {
            fluid += " -> Empty";
        } else if (this.resultingFluid >= 0 && this.resultingFluid != this.requiredFluid) {
            fluid += " -> " + CisternTileEntity.getFluidDisplayName(this.resultingFluid);
        }
        return fluid + " | Heat " + this.requiredHeat + " | Stir " + this.requiredStir
                + " | " + this.formatTime();
    }

    private String formatTime() {
        if (this.duration % 20 == 0) {
            return this.duration / 20 + "s";
        }
        return String.format("%.1fs", this.duration / 20.0F);
    }
}
