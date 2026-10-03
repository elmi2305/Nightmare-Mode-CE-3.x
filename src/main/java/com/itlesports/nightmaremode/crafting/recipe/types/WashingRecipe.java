package com.itlesports.nightmaremode.crafting.recipe.types;

import net.minecraft.src.Block;
import net.minecraft.src.ItemStack;

public class WashingRecipe {
    public enum Method {
        WATER,
        RAIN
    }

    private final ItemStack input;
    private final ItemStack output;
    private final Block inputBlock;
    private final Block outputBlock;
    private final int inputMetadata;
    private final int outputMetadata;
    private final int duration;
    private final int chanceDivisor;
    private final Method method;

    public WashingRecipe(ItemStack output, ItemStack input, int duration) {
        this.input = input.copy();
        this.output = output.copy();
        this.inputBlock = null;
        this.outputBlock = null;
        this.inputMetadata = input.getItemDamage();
        this.outputMetadata = output.getItemDamage();
        this.duration = Math.max(1, duration);
        this.chanceDivisor = 1;
        this.method = Method.WATER;
    }

    public WashingRecipe(Block outputBlock, int outputMetadata, Block inputBlock, int inputMetadata,
                         int duration, int chanceDivisor) {
        this.input = new ItemStack(inputBlock, 1, inputMetadata);
        this.output = new ItemStack(outputBlock, 1, outputMetadata);
        this.inputBlock = inputBlock;
        this.outputBlock = outputBlock;
        this.inputMetadata = inputMetadata;
        this.outputMetadata = outputMetadata;
        this.duration = Math.max(0, duration);
        this.chanceDivisor = Math.max(1, chanceDivisor);
        this.method = Method.RAIN;
    }

    public boolean matchesWaterInput(ItemStack stack) {
        return this.method == Method.WATER
                && stack != null
                && stack.stackSize >= this.input.stackSize
                && stack.itemID == this.input.itemID
                && (this.inputMetadata == Short.MAX_VALUE || stack.getItemDamage() == this.inputMetadata);
    }

    public boolean matchesRainInput(Block block, int metadata) {
        return this.method == Method.RAIN
                && block != null
                && block.blockID == this.inputBlock.blockID
                && (this.inputMetadata == Short.MAX_VALUE || metadata == this.inputMetadata);
    }

    public ItemStack getInput() {
        return this.input.copy();
    }

    public ItemStack getOutput() {
        ItemStack result = this.output.copy();
        if (com.itlesports.nightmaremode.world.BalanceProfile.isEasy()
                && (this.output.itemID == com.itlesports.nightmaremode.item.NMPostItems.washedIronMix.itemID
                || this.output.itemID == com.itlesports.nightmaremode.item.NMItems.washedAzureSediment.itemID)) result.stackSize *= 3;
        return result;
    }

    public Block getOutputBlock() {
        return this.outputBlock;
    }

    public int getOutputMetadata() {
        return this.outputMetadata;
    }

    public int getDuration() {
        return com.itlesports.nightmaremode.util.EasyBalance.processingTicks(this.duration);
    }

    public int getChanceDivisor() {
        return this.chanceDivisor;
    }

    public Method getMethod() {
        return this.method;
    }
}
