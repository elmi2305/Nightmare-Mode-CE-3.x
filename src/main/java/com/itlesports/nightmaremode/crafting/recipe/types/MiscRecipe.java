package com.itlesports.nightmaremode.crafting.recipe.types;

import net.minecraft.src.Block;
import net.minecraft.src.ItemStack;

public class MiscRecipe {
    private final ItemStack input;
    private final ItemStack output;
    private final Block inputBlock;
    private final Block outputBlock;
    private final int inputMetadata;
    private final int outputMetadata;
    private final String description;

    public MiscRecipe(ItemStack output, ItemStack input, String description) {
        this.input = input.copy();
        this.output = output.copy();
        this.inputBlock = null;
        this.outputBlock = null;
        this.inputMetadata = input.getItemDamage();
        this.outputMetadata = output.getItemDamage();
        this.description = description;
    }

    public MiscRecipe(Block outputBlock, int outputMetadata, Block inputBlock, int inputMetadata,
                      String description) {
        this.input = new ItemStack(inputBlock, 1, inputMetadata);
        this.output = new ItemStack(outputBlock, 1, outputMetadata);
        this.inputBlock = inputBlock;
        this.outputBlock = outputBlock;
        this.inputMetadata = inputMetadata;
        this.outputMetadata = outputMetadata;
        this.description = description;
    }

    public boolean matchesBlockInput(Block block, int metadata) {
        return this.inputBlock != null
                && block != null
                && block.blockID == this.inputBlock.blockID
                && (this.inputMetadata == Short.MAX_VALUE || metadata == this.inputMetadata);
    }

    public boolean matchesInput(ItemStack stack) {
        return stack != null
                && stack.stackSize >= this.input.stackSize
                && stack.itemID == this.input.itemID
                && (this.inputMetadata == Short.MAX_VALUE || stack.getItemDamage() == this.inputMetadata);
    }

    public ItemStack getInput() {
        return this.input.copy();
    }

    public ItemStack getOutput() {
        return this.output.copy();
    }

    public Block getOutputBlock() {
        return this.outputBlock;
    }

    public int getOutputMetadata() {
        return this.outputMetadata;
    }

    public String getDescription() {
        return this.description;
    }
}
