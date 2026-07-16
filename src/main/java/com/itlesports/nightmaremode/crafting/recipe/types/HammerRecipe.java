package com.itlesports.nightmaremode.crafting.recipe.types;

import net.minecraft.src.Block;
import net.minecraft.src.Enchantment;
import net.minecraft.src.EnchantmentHelper;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class HammerRecipe {
    private final ItemStack[] output;
    private final Block block;
    private final ItemStack input;
    private final int[] metadatas;
    private int hitsRequired = 1;
    private boolean canBeMinedByAnyHammer;
    private int experienceCost;
    private final Map<Integer, Integer> requiredEnchantments = new HashMap<>();

    public HammerRecipe(ItemStack[] output, Block block, int[] metadatas) {
        this.output = output;
        this.block = block;
        this.input = null;
        this.metadatas = metadatas;
    }

    public HammerRecipe(ItemStack[] output, ItemStack input) {
        this.output = output;
        this.block = null;
        this.input = input.copy();
        this.metadatas = new int[]{input.getItemDamage()};
    }

    public boolean ignoreMetadata() {
        return this.metadatas.length == 1 && this.metadatas[0] == Short.MAX_VALUE;
    }

    public boolean matchesRecipe(HammerRecipe recipe) {
        return this.matchesInputRecipe(recipe)
                && Arrays.equals(this.metadatas, recipe.metadatas)
                && this.outputsMatch(recipe.output);
    }

    public boolean matchesInputs(Block block, int metadata) {
        if (!this.isBlockRecipe()) {
            return false;
        }
        if (this.block.blockID != block.blockID) {
            return false;
        }
        if (this.ignoreMetadata()) {
            return true;
        }

        for (int recipeMetadata : this.metadatas) {
            if (recipeMetadata == metadata) {
                return true;
            }
        }
        return false;
    }

    public boolean matchesInputs(ItemStack stack) {
        if (!this.isItemRecipe() || stack == null || stack.stackSize < this.input.stackSize) {
            return false;
        }
        if (stack.itemID != this.input.itemID) {
            return false;
        }
        return this.ignoreMetadata() || stack.getItemDamage() == this.input.getItemDamage();
    }

    public ItemStack[] getOutput() {
        return this.output;
    }

    public HammerRecipe setHitsRequired(int hitsRequired) {
        this.hitsRequired = Math.max(1, hitsRequired);
        return this;
    }

    public int getHitsRequired() {
        return this.hitsRequired;
    }

    public boolean requiresMultipleHits() {
        return this.hitsRequired > 1;
    }

    public boolean isFinalHit(int metadata) {
        return !this.requiresMultipleHits() || metadata >= this.hitsRequired - 1;
    }

    public int getNextHitMetadata(int metadata) {
        return Math.min(metadata + 1, this.hitsRequired - 1);
    }

    public HammerRecipe setCanBeMinedByAnyHammer() {
        this.canBeMinedByAnyHammer = true;
        return this;
    }

    public boolean canBeMinedByAnyHammer() {
        return this.canBeMinedByAnyHammer;
    }

    public HammerRecipe setExperienceCost(int experienceCost) {
        this.experienceCost = Math.max(0, experienceCost);
        return this;
    }

    public int getExperienceCost() {
        return this.experienceCost;
    }

    public HammerRecipe addRequiredEnchantment(Enchantment enchantment, int level) {
        return enchantment == null ? this : this.addRequiredEnchantment(enchantment.effectId, level);
    }

    public HammerRecipe addRequiredEnchantment(int enchantmentID, int level) {
        this.requiredEnchantments.put(enchantmentID, Math.max(1, level));
        return this;
    }

    public boolean hammerMeetsEnchantmentRequirements(ItemStack hammer) {
        if (hammer == null && !this.requiredEnchantments.isEmpty()) {
            return false;
        }
        for (Map.Entry<Integer, Integer> entry : this.requiredEnchantments.entrySet()) {
            if (EnchantmentHelper.getEnchantmentLevel(entry.getKey(), hammer) < entry.getValue()) {
                return false;
            }
        }
        return true;
    }

    public boolean canPlayerUseHammer(ItemStack hammer, EntityPlayer player) {
        if (!this.hammerMeetsEnchantmentRequirements(hammer)) {
            return false;
        }
        return player == null || player.capabilities.isCreativeMode || player.experienceLevel >= this.experienceCost;
    }

    public void chargePlayerExperience(EntityPlayer player) {
        if (player != null && !player.capabilities.isCreativeMode && this.experienceCost > 0) {
            player.addExperienceLevel(-this.experienceCost);
        }
    }

    public boolean isBlockRecipe() {
        return this.block != null;
    }

    public boolean isItemRecipe() {
        return this.input != null;
    }

    public ItemStack getInput() {
        return this.input == null ? null : this.input.copy();
    }

    public Block getInputBlock() {
        return this.block;
    }

    public int[] getInputMetadata() {
        return this.metadatas;
    }

    private boolean matchesInputRecipe(HammerRecipe recipe) {
        if (this.isBlockRecipe() != recipe.isBlockRecipe()) {
            return false;
        }
        if (this.isBlockRecipe()) {
            return this.block == recipe.block;
        }
        return this.input != null
                && recipe.input != null
                && this.input.itemID == recipe.input.itemID
                && this.input.stackSize == recipe.input.stackSize;
    }

    private boolean outputsMatch(ItemStack[] otherOutput) {
        if (this.output.length != otherOutput.length) {
            return false;
        }

        for (int i = 0; i < this.output.length; i++) {
            if (!ItemStack.areItemStacksEqual(this.output[i], otherOutput[i])) {
                return false;
            }
        }
        return true;
    }
}
