package com.itlesports.nightmaremode.integration.emi;

import com.itlesports.nightmaremode.crafting.recipe.types.FishingRodUpgradeRecipe;
import com.itlesports.nightmaremode.item.NMTags;
import emi.dev.emi.emi.api.recipe.EmiPatternCraftingRecipe;
import emi.dev.emi.emi.api.stack.EmiIngredient;
import emi.dev.emi.emi.api.stack.EmiStack;
import emi.dev.emi.emi.api.stack.TagEmiIngredient;
import emi.dev.emi.emi.api.widget.GeneratedSlotWidget;
import emi.dev.emi.emi.api.widget.SlotWidget;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ResourceLocation;
import java.util.List;
import java.util.Random;
import java.util.ArrayList;
import java.util.Collections;

/** indexes the whole attachment tag while keeping each cycling input and output in sync. */
public final class EmiFishingAttachmentRecipe extends EmiPatternCraftingRecipe {
    private final Item rod;

    public EmiFishingAttachmentRecipe(Item rod) {
        super(List.of(EmiStack.of(rod), EmiIngredient.of(NMTags.fishingAttachments)), EmiStack.of(rod),
                new ResourceLocation("nightmare", "fishing_attachment/" + rod.itemID));
        this.rod = rod;
    }

    public ItemStack resultFor(Item attachment) {
        return FishingRodUpgradeRecipe.applyUpgrade(new ItemStack(rod), FishingRodUpgradeRecipe.upgradeKey(attachment));
    }

    private Item attachment(Random random) {
        List<EmiStack> choices = getInputs().get(1).getEmiStacks();
        return choices.get(random.nextInt(choices.size())).getItemStack().getItem();
    }

    private EmiIngredient exampleInput(Random random) {
        EmiIngredient ingredient = getInputs().get(1);
        List<EmiStack> choices = new ArrayList<>(ingredient.getEmiStacks());
        Collections.rotate(choices, -random.nextInt(choices.size()));
        // native tag tooltips retain all seven choices while its first icon matches the example output.
        if (ingredient instanceof TagEmiIngredient tag) return new TagEmiIngredient(tag.key, choices, 1);
        return EmiIngredient.of(choices);
    }

    @Override public SlotWidget getInputWidget(int slot, int x, int y) {
        if (slot == 0) return new SlotWidget(getInputs().get(0), x, y);
        if (slot != 1) return new SlotWidget(EmiStack.EMPTY, x, y);
        return new GeneratedSlotWidget(this::exampleInput, unique, x, y);
    }

    @Override public SlotWidget getOutputWidget(int x, int y) {
        return new GeneratedSlotWidget(random -> EmiStack.of(resultFor(attachment(random))), unique, x, y);
    }
}
