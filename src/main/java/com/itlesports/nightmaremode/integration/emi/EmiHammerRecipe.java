package com.itlesports.nightmaremode.integration.emi;

import com.itlesports.nightmaremode.crafting.recipe.types.HammerRecipe;
import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.util.HammerAnvilMaterials;
import emi.dev.emi.emi.EmiPort;
import emi.dev.emi.emi.api.recipe.EmiRecipe;
import emi.dev.emi.emi.api.recipe.EmiRecipeCategory;
import emi.dev.emi.emi.api.stack.EmiIngredient;
import emi.dev.emi.emi.api.stack.EmiStack;
import emi.dev.emi.emi.api.widget.WidgetHolder;
import emi.shims.java.com.unascribed.retroemi.RetroEMI;
import emi.shims.java.net.minecraft.client.gui.tooltip.TooltipComponent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Collections;
import java.util.stream.Collectors;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Block;
import net.minecraft.src.ResourceLocation;

public class EmiHammerRecipe implements EmiRecipe {
    private final ResourceLocation id;
    private final EmiIngredient input;
    private final List<EmiStack> outputs;
    private final int hits;
    private final int experienceCost;
    private final boolean blockRecipe;
    private final Map<String, Integer> anvilHits;

    public EmiHammerRecipe(HammerRecipe recipe, int index) {
        this.id = new ResourceLocation("nightmare", "hammering/" + index);
        this.input = createInput(recipe);
        this.outputs = Arrays.stream(recipe.getOutput()).map(EmiStack::of).collect(Collectors.toList());
        this.hits = recipe.getHitsRequired();
        this.experienceCost = recipe.getExperienceCost();
        this.blockRecipe = recipe.isBlockRecipe();
        Map<String, Integer> bonuses = new LinkedHashMap<>();
        if (!this.blockRecipe) {
            for (Block anvil : new Block[]{NMBlocks.stoneAnvil, NMBlocks.lithiumAnvil, NMBlocks.ironAnvil,
                    NMBlocks.diamondAnvil, NMBlocks.netherrackAnvil, Block.anvil}) {
                if (anvil == null) continue;
                int adjusted = HammerAnvilMaterials.getHitsRequired(anvil.blockID, recipe);
                if (adjusted < this.hits) bonuses.put(new ItemStack(anvil).getDisplayName(), adjusted);
            }
        }
        this.anvilHits = Collections.unmodifiableMap(bonuses);
    }

    public Map<String, Integer> getAnvilHits() {
        return this.anvilHits;
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
        var inputSlot = widgets.addSlot(this.input, 0, 0).appendTooltip(EmiPort.literal(
                this.blockRecipe ? "Placed-block hammering" : "Anvil hammering"));
        widgets.addFillingArrow(24, 1, Math.max(500, this.hits * 500));
        for (int i = 0; i < this.outputs.size(); ++i) {
            widgets.addSlot(this.outputs.get(i), 56 + i % 3 * 18, i / 3 * 18).recipeContext(this);
        }
        String requirements = this.hits + (this.hits == 1 ? " hit" : " hits");
        if (this.experienceCost > 0) {
            requirements += " | " + this.experienceCost + " XP";
        }
        List<TooltipComponent> bonusTooltip = new ArrayList<>();
        for (Map.Entry<String, Integer> bonus : this.anvilHits.entrySet()) {
            int count = bonus.getValue();
            var description = EmiPort.literal(bonus.getKey() + ": " + count + (count == 1 ? " hit" : " hits"));
            inputSlot.appendTooltip(description);
            bonusTooltip.add(TooltipComponent.of(EmiPort.ordered(description)));
        }
        if (!bonusTooltip.isEmpty()) {
            requirements += " *";
            widgets.addDrawable(0, 26, this.getDisplayWidth(), 11, (draw, mouseX, mouseY, delta) -> {
            }).tooltip((mouseX, mouseY) -> bonusTooltip);
        }
        widgets.addText(EmiPort.literal(requirements), 0, 27, 0x404040, false);
    }
}
