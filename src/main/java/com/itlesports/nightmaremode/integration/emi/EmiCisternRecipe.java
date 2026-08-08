package com.itlesports.nightmaremode.integration.emi;

import com.itlesports.nightmaremode.block.tileEntities.CisternTileEntity;
import com.itlesports.nightmaremode.crafting.recipe.types.CisternRecipe;
import com.itlesports.nightmaremode.skill.SkillLockedCrafting;
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
    private static final int BASE_HEIGHT = 78;
    private static final int SKILL_ROW_Y = 79;

    private final CisternRecipe recipe;
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
        this.recipe = recipe;
        this.id = new ResourceLocation("nightmare", "cistern/" + index);
        this.inputs = Arrays.stream(this.mergeInputs(recipe.getInputs())).map(EmiStack::of).collect(Collectors.toList());
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
        return 154;
    }

    @Override
    public int getDisplayHeight() {
        return EmiIconHelper.getSkillRequirementDisplayHeight(
                BASE_HEIGHT,
                this.getDisplayWidth(),
                SkillLockedCrafting.getRequiredSkills(this.recipe));
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        boolean compact = this.inputs.size() < 4;
        int displayedSlots = compact ? 4 : 9;
        int columns = compact ? 2 : 3;
        int offset = compact ? 9 : 0;
        for (int i = 0; i < displayedSlots; ++i) {
            int x = offset + i % columns * 18;
            int y = offset + i / columns * 18;
            if (i < this.inputs.size()) {
                widgets.addSlot(this.inputs.get(i), x, y);
            } else {
                widgets.addSlot(x, y);
            }
        }
        widgets.addFillingArrow(62, 19, Math.max(500, this.duration * 50));
        for (int i = 0; i < 4; ++i) {
            if (i < this.outputs.size()) {
                widgets.addSlot(this.outputs.get(i), 100 + i % 2 * 18, 9 + i / 2 * 18).recipeContext(this);
            } else {
                widgets.addSlot(100 + i % 2 * 18, 9 + i / 2 * 18);
            }
        }
        this.addRequirementIcons(widgets);
        EmiIconHelper.addSkillRequirements(
                widgets,
                SKILL_ROW_Y,
                this.getDisplayWidth(),
                SkillLockedCrafting.getRequiredSkills(this.recipe));
    }

    private ItemStack[] mergeInputs(ItemStack[] recipeInputs) {
        List<ItemStack> merged = new ArrayList<>();
        for (ItemStack input : recipeInputs) {
            boolean found = false;
            for (ItemStack existing : merged) {
                if (existing.isItemEqual(input) && ItemStack.areItemStackTagsEqual(existing, input)) {
                    existing.stackSize += input.stackSize;
                    found = true;
                    break;
                }
            }
            if (!found) {
                merged.add(input.copy());
            }
        }
        return merged.toArray(new ItemStack[merged.size()]);
    }

    private void addRequirementIcons(WidgetHolder widgets) {
        int x = 0;
        int y = 58;

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
