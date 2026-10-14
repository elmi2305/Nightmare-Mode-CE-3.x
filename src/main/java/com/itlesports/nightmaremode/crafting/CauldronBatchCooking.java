package com.itlesports.nightmaremode.crafting;

import api.item.tag.TagInstance;
import api.item.tag.TagOrStack;
import btw.crafting.manager.BulkCraftingManager;
import btw.crafting.recipe.types.BulkRecipe;
import com.itlesports.nightmaremode.mixin.accessor.BulkRecipeAccessor;
import com.itlesports.nightmaremode.skill.SkillLockedCrafting;
import net.minecraft.src.FurnaceRecipes;
import net.minecraft.src.ItemFood;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public final class CauldronBatchCooking {
    private CauldronBatchCooking() {}

    public static boolean hasRecipe(ItemStack[] contents, BulkCraftingManager manager, World world, boolean allowFood) {
        boolean[] reserved = new boolean[contents.length];
        for (Recipe recipe : recipes(manager, world)) {
            if (allocate(contents, reserved, -1, recipe) != null) return true;
        }
        if (allowFood) {
            for (ItemStack stack : contents) {
                if (foodOutput(stack) != null) return true;
            }
        }
        return false;
    }

    // returns a complete new inventory, or null without modifying any input when a cycle cannot finish.
    public static ItemStack[] cook(ItemStack[] contents, BulkCraftingManager manager, World world,
                                   boolean allowFood, int inventoryLimit) {
        List<Recipe> recipes = recipes(manager, world);
        boolean[] reserved = new boolean[contents.length];
        List<Batch> batches = new ArrayList<>();
        while (true) {
            Batch next = null;
            for (Recipe recipe : recipes) {
                Batch candidate = allocate(contents, reserved, -1, recipe);
                if (candidate != null && candidate.usedSlots() > 1) {
                    next = candidate;
                    break;
                }
            }
            if (next == null) break;
            batches.add(next);
            reserve(reserved, next);
        }

        List<Batch> singles = new ArrayList<>();
        for (int slot = 0; slot < contents.length; ++slot) {
            if (reserved[slot] || contents[slot] == null) continue;
            Batch next = null;
            for (Recipe recipe : recipes) {
                next = allocate(contents, reserved, slot, recipe);
                if (next != null) break;
            }
            if (next == null && allowFood) {
                ItemStack output = foodOutput(contents[slot]);
                if (output != null) {
                    int[] consumed = new int[contents.length];
                    consumed[slot] = 1;
                    next = new Batch(consumed, List.of(output));
                }
            }
            if (next != null) {
                singles.add(next);
                reserve(reserved, next);
            }
        }
        // emptying a source first can make room for subsequent batches' outputs.
        singles.sort(Comparator.comparing((Batch batch) -> !batch.emptiesSource(contents))
                .thenComparingInt(Batch::firstSlot));
        batches.addAll(singles);
        if (batches.isEmpty()) return null;

        ItemStack[] working = new ItemStack[contents.length];
        for (int slot = 0; slot < contents.length; ++slot) {
            if (contents[slot] != null) working[slot] = contents[slot].copy();
        }
        for (Batch batch : batches) {
            for (int slot = 0; slot < working.length; ++slot) {
                if (batch.consumed[slot] == 0) continue;
                working[slot].stackSize -= batch.consumed[slot];
                if (working[slot].stackSize == 0) working[slot] = null;
            }
            for (ItemStack output : batch.outputs) {
                if (!insert(working, output, inventoryLimit)) return null;
            }
        }
        return working;
    }

    private static List<Recipe> recipes(BulkCraftingManager manager, World world) {
        List<Recipe> result = new ArrayList<>();
        if (manager == null) return result;
        for (BulkRecipe recipe : manager.getRecipeList()) {
            if (SkillLockedCrafting.isWorldLocked(world, recipe)) continue;
            List<TagOrStack> inputs = recipe.getCraftingIngrediantList();
            List<ItemStack> outputs = recipe.getCraftingOutputList();
            if (inputs == null || inputs.isEmpty() || outputs == null || outputs.isEmpty()) continue;
            List<Ingredient> ingredients = new ArrayList<>();
            for (TagOrStack input : inputs) {
                if (input instanceof ItemStack stack && stack.stackSize > 0) {
                    ingredients.add(new Ingredient(stack.stackSize, List.of(stack)));
                } else if (input instanceof TagInstance tag && tag.stackSize() > 0) {
                    ingredients.add(new Ingredient(tag.stackSize(), tag.tag().getItems()));
                } else break;
            }
            if (ingredients.size() != inputs.size()) continue;
            result.add(new Recipe(ingredients, outputs, ((BulkRecipeAccessor)recipe).nm$isMetadataExclusive()));
        }
        return result;
    }

    private static ItemStack foodOutput(ItemStack stack) {
        if (stack == null || stack.stackSize <= 0 || !(stack.getItem() instanceof ItemFood)) return null;
        return FurnaceRecipes.smelting().getSmeltingResult(stack.itemID);
    }

    private static Batch allocate(ItemStack[] contents, boolean[] reserved, int onlySlot, Recipe recipe) {
        int ingredientCount = recipe.ingredients.size();
        long supply = 0L;
        for (int slot = 0; slot < contents.length; ++slot) {
            if (!reserved[slot] && (onlySlot < 0 || slot == onlySlot) && contents[slot] != null) {
                supply += Math.max(0, contents[slot].stackSize);
            }
        }
        long demand = 0L;
        // reject unrelated recipes before allocating the residual graph.
        for (Ingredient ingredient : recipe.ingredients) {
            demand += ingredient.amount;
            if (demand > supply || demand > Integer.MAX_VALUE) return null;
            long matching = 0L;
            for (int slot = 0; slot < contents.length; ++slot) {
                if (!reserved[slot] && (onlySlot < 0 || slot == onlySlot)
                        && ingredient.matches(contents[slot], recipe.exclusive)) {
                    matching += contents[slot].stackSize;
                }
            }
            if (matching < ingredient.amount) return null;
        }
        int firstSlotNode = 1 + ingredientCount;
        int sink = firstSlotNode + contents.length;
        int[][] capacity = new int[sink + 1][sink + 1];
        for (int index = 0; index < ingredientCount; ++index) {
            Ingredient ingredient = recipe.ingredients.get(index);
            capacity[0][index + 1] = ingredient.amount;
            for (int slot = 0; slot < contents.length; ++slot) {
                if (!reserved[slot] && (onlySlot < 0 || slot == onlySlot)
                        && ingredient.matches(contents[slot], recipe.exclusive)) {
                    capacity[index + 1][firstSlotNode + slot] = ingredient.amount;
                }
            }
        }
        for (int slot = 0; slot < contents.length; ++slot) {
            if (reserved[slot] || onlySlot >= 0 && slot != onlySlot || contents[slot] == null) continue;
            capacity[firstSlotNode + slot][sink] = Math.max(0, contents[slot].stackSize);
        }

        // residual paths reassign flexible tag inputs when a stricter ingredient needs their items.
        int supplied = 0;
        int[] parent = new int[capacity.length];
        int[] queue = new int[capacity.length];
        while (supplied < demand) {
            Arrays.fill(parent, -1);
            parent[0] = 0;
            int head = 0;
            int tail = 1;
            queue[0] = 0;
            while (head < tail && parent[sink] < 0) {
                int node = queue[head++];
                for (int next = 1; next < capacity.length; ++next) {
                    if (parent[next] < 0 && capacity[node][next] > 0) {
                        parent[next] = node;
                        queue[tail++] = next;
                    }
                }
            }
            if (parent[sink] < 0) return null;
            int amount = Integer.MAX_VALUE;
            for (int node = sink; node != 0; node = parent[node]) {
                amount = Math.min(amount, capacity[parent[node]][node]);
            }
            for (int node = sink; node != 0; node = parent[node]) {
                capacity[parent[node]][node] -= amount;
                capacity[node][parent[node]] += amount;
            }
            supplied += amount;
        }
        int[] consumed = new int[contents.length];
        for (int slot = 0; slot < contents.length; ++slot) {
            consumed[slot] = capacity[sink][firstSlotNode + slot];
        }
        return new Batch(consumed, recipe.outputs);
    }

    private static void reserve(boolean[] reserved, Batch batch) {
        for (int slot = 0; slot < reserved.length; ++slot) {
            if (batch.consumed[slot] > 0) reserved[slot] = true;
        }
    }

    private static boolean insert(ItemStack[] contents, ItemStack output, int inventoryLimit) {
        if (output == null || output.stackSize <= 0 || output.getItem() == null) return false;
        int limit = Math.min(inventoryLimit, output.getMaxStackSize());
        if (limit <= 0) return false;
        int remaining = output.stackSize;
        if (output.isStackable()) {
            for (ItemStack destination : contents) {
                if (destination == null || !destination.isStackable() || !destination.isItemEqual(output)
                        || !ItemStack.areItemStackTagsEqual(destination, output)) continue;
                int amount = Math.min(remaining, Math.max(0, limit - destination.stackSize));
                destination.stackSize += amount;
                remaining -= amount;
                if (remaining == 0) return true;
            }
        }
        for (int slot = 0; slot < contents.length; ++slot) {
            if (contents[slot] != null) continue;
            contents[slot] = output.copy();
            contents[slot].stackSize = Math.min(remaining, limit);
            remaining -= contents[slot].stackSize;
            if (remaining == 0) return true;
        }
        return false;
    }

    private record Ingredient(int amount, List<ItemStack> alternatives) {
        private boolean matches(ItemStack stack, boolean exclusive) {
            if (stack == null || stack.stackSize <= 0) return false;
            for (ItemStack alternative : this.alternatives) {
                if (stack.itemID == alternative.itemID && (alternative.getItemDamage() == Short.MAX_VALUE
                        || (stack.getItemDamage() == alternative.getItemDamage()) != exclusive)) return true;
            }
            return false;
        }
    }

    private record Recipe(List<Ingredient> ingredients, List<ItemStack> outputs, boolean exclusive) {}

    private record Batch(int[] consumed, List<ItemStack> outputs) {
        private int usedSlots() {
            int count = 0;
            for (int amount : this.consumed) if (amount > 0) ++count;
            return count;
        }

        private int firstSlot() {
            for (int slot = 0; slot < this.consumed.length; ++slot) {
                if (this.consumed[slot] > 0) return slot;
            }
            return -1;
        }

        private boolean emptiesSource(ItemStack[] contents) {
            int slot = firstSlot();
            return this.consumed[slot] == contents[slot].stackSize;
        }
    }
}
