package com.itlesports.nightmaremode.util;

import com.itlesports.nightmaremode.block.tileEntities.TileEntityHammerAnvil;
import com.itlesports.nightmaremode.crafting.manager.HammerCraftingManager;
import com.itlesports.nightmaremode.crafting.recipe.types.HammerRecipe;
import com.itlesports.nightmaremode.item.items.ItemHammer;
import net.minecraft.src.ChatMessageComponent;
import net.minecraft.src.EntityItem;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.InventoryPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;

public abstract class HammerAnvilHelper {
    public static boolean tryHammerHeldItem(World world, int x, int y, int z, EntityPlayer player, TileEntityHammerAnvil anvil) {
        if (anvil != null && anvil.isBusy()) {
            return false;
        }
        if (!player.capabilities.isCreativeMode && player.getFoodStats().getFoodLevel() < 6) {
            sendStatus(player, anvil, "You are too hungry to use the anvil.");
            return false;
        }

        ItemStack input = player.getHeldItem();
        HammerRecipe recipe = HammerCraftingManager.instance.getRecipe(input);
        if (recipe == null) {
            sendStatus(player, anvil, "No hammer recipe for held item.");
            return false;
        }

        int hammerSlot = findUsableHammerSlot(player, recipe);
        if (hammerSlot < 0) {
            sendStatus(player, anvil, "A valid hammer is required.");
            return false;
        }

        int hits = recipe.getHitsRequired();
        if (anvil != null && !anvil.canSpendHits(hits)) {
            if (!anvil.isWaitingToBreak()) {
                sendStatus(player, anvil, "The anvil is too worn for this recipe.");
            }
            return false;
        }

        ItemStack hammer = player.inventory.mainInventory[hammerSlot];
        ItemStack recipeInput = recipe.getInput();
        if (!player.capabilities.isCreativeMode) {
            input.stackSize -= recipeInput.stackSize;
            if (input.stackSize <= 0) {
                player.inventory.mainInventory[player.inventory.currentItem] = null;
            }

            hammer.damageItem(hits, player);
            if (hammer.stackSize <= 0) {
                player.inventory.mainInventory[hammerSlot] = null;
            }
        }

        recipe.chargePlayerExperience(player);
        player.addExhaustion(0.2F * hits);

        for (ItemStack output : recipe.getOutput()) {
            if (output == null) {
                continue;
            }
            ItemStack outputCopy = output.copy();
            player.inventory.addItemStackToInventory(outputCopy);
            if (outputCopy.stackSize > 0) {
                spawnOutputAboveAnvil(world, x, y, z, outputCopy);
            }
        }

        if (anvil != null) {
            anvil.spendHits(hits);
        } else {
            world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D,
                    "random.anvil_use", 0.5F, world.rand.nextFloat() * 0.25F + 1.25F);
        }

        player.inventory.onInventoryChanged();
        return true;
    }

    private static void spawnOutputAboveAnvil(World world, int x, int y, int z, ItemStack stack) {
        EntityItem entity = new EntityItem(world, x + 0.5D, y + 1.2D, z + 0.5D, stack);
        entity.motionX = 0.0D;
        entity.motionY = 0.0D;
        entity.motionZ = 0.0D;
        world.spawnEntityInWorld(entity);
    }

    private static int findUsableHammerSlot(EntityPlayer player, HammerRecipe recipe) {
        InventoryPlayer inventory = player.inventory;
        for (int i = 0; i < inventory.mainInventory.length; ++i) {
            ItemStack stack = inventory.mainInventory[i];
            if (stack != null && stack.getItem() instanceof ItemHammer && recipe.canPlayerUseHammer(stack, player)) {
                return i;
            }
        }
        return -1;
    }

    public static void sendStatus(EntityPlayer player, TileEntityHammerAnvil anvil, String message) {
        if (player == null || player.worldObj.isRemote) {
            return;
        }
        String suffix = "";
        if (anvil != null && anvil.isLimited()) {
            suffix = " Uses: " + anvil.getUsesRemaining() + " / " + anvil.getMaxUses() + ".";
        }
        player.sendChatToPlayer(new ChatMessageComponent().addText(message + suffix));
    }
}
