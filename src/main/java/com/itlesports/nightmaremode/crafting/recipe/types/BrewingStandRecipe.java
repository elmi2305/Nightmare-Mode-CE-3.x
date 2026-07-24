package com.itlesports.nightmaremode.crafting.recipe.types;

import net.minecraft.src.ItemStack;

/**
 * a non-potion brewing stand conversion. use {@link #ANY_METADATA} on an
 * input stack when that input should match every damage value.
 */
public class BrewingStandRecipe {
    public static final int ANY_METADATA = Short.MAX_VALUE;

    private final ItemStack output;
    private final ItemStack ingredient;
    private final ItemStack bottleInput;
    private final float brewingTimeMultiplier;

    public BrewingStandRecipe(ItemStack output, ItemStack ingredient, ItemStack bottleInput) {
        this(output, ingredient, bottleInput, 1.0F);
    }

    public BrewingStandRecipe(ItemStack output, ItemStack ingredient, ItemStack bottleInput, float brewingTimeMultiplier) {
        if (output == null || ingredient == null || bottleInput == null) {
            throw new IllegalArgumentException("Brewing stand recipes require an output, ingredient, and bottle-slot input");
        }
        if (brewingTimeMultiplier <= 0.0F) {
            throw new IllegalArgumentException("Brewing time multiplier must be greater than zero");
        }

        this.output = output.copy();
        this.ingredient = ingredient.copy();
        this.bottleInput = bottleInput.copy();
        this.brewingTimeMultiplier = brewingTimeMultiplier;
    }

    public boolean matches(ItemStack ingredientStack, ItemStack bottleStack) {
        return this.matchesStack(this.ingredient, ingredientStack) && this.matchesStack(this.bottleInput, bottleStack);
    }

    public boolean matchesIngredient(ItemStack stack) {
        return this.matchesStack(this.ingredient, stack);
    }

    public boolean matchesBottleInput(ItemStack stack) {
        return this.matchesStack(this.bottleInput, stack);
    }

    private boolean matchesStack(ItemStack expected, ItemStack actual) {
        return actual != null
                && actual.itemID == expected.itemID
                && (expected.getItemDamage() == ANY_METADATA || actual.getItemDamage() == expected.getItemDamage());
    }

    public ItemStack getOutput() {
        return this.output.copy();
    }

    public ItemStack getIngredient() {
        return this.ingredient.copy();
    }

    public ItemStack getBottleInput() {
        return this.bottleInput.copy();
    }

    public float getBrewingTimeMultiplier() {
        return this.brewingTimeMultiplier;
    }
}
