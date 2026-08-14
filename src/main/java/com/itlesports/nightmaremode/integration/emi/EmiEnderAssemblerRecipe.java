package com.itlesports.nightmaremode.integration.emi;

import com.itlesports.nightmaremode.crafting.recipe.types.EnderAssemblerRecipe;
import emi.dev.emi.emi.EmiPort;
import emi.dev.emi.emi.api.recipe.EmiRecipe;
import emi.dev.emi.emi.api.recipe.EmiRecipeCategory;
import emi.dev.emi.emi.api.stack.EmiIngredient;
import emi.dev.emi.emi.api.stack.EmiStack;
import emi.dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public final class EmiEnderAssemblerRecipe implements EmiRecipe {
    private final ResourceLocation id;
    private final List<EmiIngredient> inputs = new ArrayList<>();
    private final List<EmiStack> outputs;
    private final int duration;

    public EmiEnderAssemblerRecipe(EnderAssemblerRecipe recipe, int index) {
        this.id = new ResourceLocation("nightmare", "ender_assembler/" + index);
        for (ItemStack ingredient : recipe.getIngredients()) this.inputs.add(EmiStack.of(ingredient));
        this.outputs = List.of(EmiStack.of(recipe.getOutput()));
        this.duration = recipe.getDuration();
    }

    @Override public EmiRecipeCategory getCategory() { return NightmareEmiRegistry.ENDER_ASSEMBLER; }
    @Override public ResourceLocation getId() { return this.id; }
    @Override public List<EmiIngredient> getInputs() { return this.inputs; }
    @Override public List<EmiStack> getOutputs() { return this.outputs; }
    @Override public int getDisplayWidth() { return 136; }
    @Override public int getDisplayHeight() { return 46; }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        for (int slot = 0; slot < 6; ++slot) {
            int x = slot % 3 * 18;
            int y = slot / 3 * 18;
            if (slot < this.inputs.size()) widgets.addSlot(this.inputs.get(slot), x, y);
            else widgets.addSlot(x, y);
        }
        widgets.addFillingArrow(62, 10, Math.max(500, this.duration * 50));
        widgets.addSlot(this.outputs.get(0), 100, 9).recipeContext(this);
        widgets.addText(EmiPort.literal(this.formatTime()), 0, 38, 0x404040, false);
    }

    private String formatTime() {
        if (this.duration % 20 == 0) return this.duration / 20 + "s | mechanical power";
        return String.format("%.1fs | mechanical power", this.duration / 20.0F);
    }
}
