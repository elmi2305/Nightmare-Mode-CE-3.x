package com.itlesports.nightmaremode.integration.emi;

import com.itlesports.nightmaremode.block.tileEntities.CisternTileEntity;
import com.itlesports.nightmaremode.crafting.recipe.types.CisternRecipe;
import com.itlesports.nightmaremode.util.NMFields;
import emi.dev.emi.emi.EmiPort;
import emi.dev.emi.emi.api.plugin.BTWPlugin;
import emi.dev.emi.emi.api.recipe.EmiRecipe;
import emi.dev.emi.emi.api.recipe.EmiRecipeCategory;
import emi.dev.emi.emi.api.render.EmiTexture;
import emi.dev.emi.emi.api.stack.EmiIngredient;
import emi.dev.emi.emi.api.stack.EmiStack;
import emi.dev.emi.emi.api.widget.WidgetHolder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.src.Block;
import net.minecraft.src.Icon;
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
        return 58;
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
        this.addRequirementIcons(widgets);
    }

    private void addRequirementIcons(WidgetHolder widgets) {
        int x = 0;
        int y = 40;

        EmiIconHelper.addIcon(
                widgets,
                x,
                y,
                () -> this.getFluidIcon(this.requiredFluid),
                CisternTileEntity.getFluidDisplayName(this.requiredFluid));
        x += 18;

        if (this.consumesFluid || this.resultingFluid >= 0 && this.resultingFluid != this.requiredFluid) {
            widgets.addTexture(EmiTexture.EMPTY_ARROW, x, y);
            x += 26;
            if (this.consumesFluid) {
                widgets.addTexture(BTWPlugin.X_ICON, x, y);
                EmiIconHelper.addTooltip(widgets, x, y, 15, 15, "Fluid is consumed");
                x += 17;
            } else {
                EmiIconHelper.addIcon(
                        widgets,
                        x,
                        y,
                        () -> this.getFluidIcon(this.resultingFluid),
                        "Converts to " + CisternTileEntity.getFluidDisplayName(this.resultingFluid));
                x += 18;
            }
        }

        if (this.requiredHeat > 0) {
            EmiIconHelper.addIcon(
                    widgets,
                    x,
                    y,
                    this::getHeatIcon,
                    "Requires heat level " + this.requiredHeat);
            x += 18;
        }

        if (this.requiredStir > 0) {
            EmiIconHelper.addIcon(
                    widgets,
                    x,
                    y,
                    () -> NMFields.ICON_STIRRING,
                    "Stir x" + this.requiredStir);
            x += 18;
        }

        widgets.addText(EmiPort.literal(this.formatTime()), x, y + 4, 0x404040, false);
    }

    private Icon getFluidIcon(int fluid) {
        return switch (fluid) {
            case CisternTileEntity.FLUID_BRINE -> NMFields.ICON_BRINE;
            case CisternTileEntity.FLUID_SLURRY -> NMFields.ICON_SLURRY;
            case CisternTileEntity.FLUID_ACIDIC_WASH -> NMFields.ICON_ACID;
            case CisternTileEntity.FLUID_LAVA -> Block.lavaStill.getIcon(0, 0);
            default -> Block.waterStill.getIcon(0, 0);
        };
    }

    private Icon getHeatIcon() {
        return switch (this.requiredHeat) {
            case 1 -> NMFields.ICON_HEAT_1;
            case 2 -> NMFields.ICON_HEAT_2;
            default -> NMFields.ICON_HEAT_3;
        };
    }

    private String formatTime() {
        if (this.duration % 20 == 0) {
            return this.duration / 20 + "s";
        }
        return String.format("%.1fs", this.duration / 20.0F);
    }
}
