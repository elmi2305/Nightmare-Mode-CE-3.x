package com.itlesports.nightmaremode.crafting.recipe.types;

import net.minecraft.src.Block;
import net.minecraft.src.ItemStack;

import java.util.Arrays;

public class HammerRecipe {
    private final ItemStack[] output;
    private final Block block;
    private final int[] metadatas;

    public HammerRecipe(ItemStack[] output, Block block, int[] metadatas) {
        this.output = output;
        this.block = block;
        this.metadatas = metadatas;
    }

    public boolean ignoreMetadata() {
        return this.metadatas.length == 1 && this.metadatas[0] == Short.MAX_VALUE;
    }

    public boolean matchesRecipe(HammerRecipe recipe) {
        return this.block == recipe.block
                && Arrays.equals(this.metadatas, recipe.metadatas)
                && this.outputsMatch(recipe.output);
    }

    public boolean matchesInputs(Block block, int metadata) {
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

    public ItemStack[] getOutput() {
        return this.output;
    }

    public Block getInputBlock() {
        return this.block;
    }

    public int[] getInputMetadata() {
        return this.metadatas;
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
