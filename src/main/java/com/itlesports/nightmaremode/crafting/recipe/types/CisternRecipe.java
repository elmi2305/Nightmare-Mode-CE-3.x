package com.itlesports.nightmaremode.crafting.recipe.types;

import com.itlesports.nightmaremode.block.tileEntities.CisternTileEntity;
import net.minecraft.src.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CisternRecipe {
    private final ItemStack[] inputs;
    private final int requiredFluid;
    private final int requiredHeat;
    private final int requiredStir;
    private final int duration;
    private final ItemStack[] outputs;
    private final List<RandomOutput> randomOutputs = new ArrayList<>();
    private int resultingFluid = -1;
    private boolean consumesFluid;

    public CisternRecipe(ItemStack[] inputs, int requiredFluid, int requiredHeat, int requiredStir, int duration, ItemStack[] outputs) {
        this.inputs = inputs;
        this.requiredFluid = requiredFluid;
        this.requiredHeat = requiredHeat;
        this.requiredStir = requiredStir;
        this.duration = duration;
        this.outputs = outputs;
    }

    public boolean matches(ItemStack[] inventory, int fluid, int heat, int stir) {
        if (this.requiredFluid != CisternTileEntity.FLUID_ANY && fluid != this.requiredFluid) {
            return false;
        }
        if (heat < this.requiredHeat || stir < this.requiredStir) {
            return false;
        }
        boolean[] used = new boolean[inventory.length];
        for (ItemStack input : this.inputs) {
            boolean matched = false;
            for (int i = CisternTileEntity.FIRST_INPUT_SLOT; i <= CisternTileEntity.LAST_INPUT_SLOT; ++i) {
                ItemStack stack = inventory[i];
                if (!used[i] && stack != null && stack.stackSize >= input.stackSize && stack.isItemEqual(input)) {
                    used[i] = true;
                    matched = true;
                    break;
                }
            }
            if (!matched) {
                return false;
            }
        }
        return true;
    }

    public void consumeInputs(ItemStack[] inventory) {
        boolean[] used = new boolean[inventory.length];
        for (ItemStack input : this.inputs) {
            for (int i = CisternTileEntity.FIRST_INPUT_SLOT; i <= CisternTileEntity.LAST_INPUT_SLOT; ++i) {
                ItemStack stack = inventory[i];
                if (!used[i] && stack != null && stack.stackSize >= input.stackSize && stack.isItemEqual(input)) {
                    stack.stackSize -= input.stackSize;
                    if (stack.stackSize <= 0) {
                        inventory[i] = null;
                    }
                    used[i] = true;
                    break;
                }
            }
        }
    }

    public ItemStack[] getOutputs(Random random) {
        List<ItemStack> results = new ArrayList<>();
        for (ItemStack output : this.outputs) {
            results.add(output.copy());
        }
        for (RandomOutput output : this.randomOutputs) {
            if (random.nextFloat() < output.chance) {
                results.add(output.stack.copy());
            }
        }
        return results.toArray(new ItemStack[results.size()]);
    }

    public ItemStack[] getPotentialOutputs() {
        List<ItemStack> results = new ArrayList<>();
        for (ItemStack output : this.outputs) {
            results.add(output.copy());
        }
        for (RandomOutput output : this.randomOutputs) {
            results.add(output.stack.copy());
        }
        return results.toArray(new ItemStack[results.size()]);
    }

    public ItemStack[] getInputs() {
        ItemStack[] result = new ItemStack[this.inputs.length];
        for (int i = 0; i < this.inputs.length; ++i) {
            result[i] = this.inputs[i].copy();
        }
        return result;
    }

    public ItemStack[] getOutputs() {
        ItemStack[] result = new ItemStack[this.outputs.length];
        for (int i = 0; i < this.outputs.length; ++i) {
            result[i] = this.outputs[i].copy();
        }
        return result;
    }

    public List<RandomOutput> getRandomOutputs() {
        return new ArrayList<>(this.randomOutputs);
    }

    public int getRequiredFluid() {
        return this.requiredFluid;
    }

    public int getRequiredHeat() {
        return this.requiredHeat;
    }

    public int getRequiredStir() {
        return this.requiredStir;
    }

    public int getResultingFluid() {
        return this.resultingFluid;
    }

    public CisternRecipe addRandomOutput(ItemStack stack, float chance) {
        this.randomOutputs.add(new RandomOutput(stack, chance));
        return this;
    }

    public CisternRecipe setResultingFluid(int fluid) {
        this.resultingFluid = fluid;
        return this;
    }

    public CisternRecipe setConsumesFluid() {
        this.consumesFluid = true;
        return this;
    }

    public int getDuration() {
        return this.duration;
    }

    public int getResultingFluid(int currentFluid) {
        return this.resultingFluid >= 0 ? this.resultingFluid : currentFluid;
    }

    public boolean consumesFluid() {
        return this.consumesFluid;
    }

    public static class RandomOutput {
        private final ItemStack stack;
        private final float chance;

        private RandomOutput(ItemStack stack, float chance) {
            this.stack = stack.copy();
            this.chance = chance;
        }

        public ItemStack getStack() {
            return this.stack.copy();
        }

        public float getChance() {
            return this.chance;
        }
    }
}
