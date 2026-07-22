package com.itlesports.nightmaremode.item.items;

import btw.crafting.recipe.types.customcrafting.FishingRodBaitingRecipe;
import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.util.interfaces.INetherItem;
import net.minecraft.src.EntityFishHook;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemFishingRod;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;

public class ItemNetherFishingRod extends ItemFishingRod implements INetherItem {
    private final boolean baited;

    public ItemNetherFishingRod(int id, boolean baited) {
        super(id);
        this.baited = baited;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (player.fishEntity != null) {
            int damage = player.fishEntity.catchFish();
            ItemStack held = player.getCurrentEquippedItem();
            if (held != null) {
                held.damageItem(damage, player);
            }
            player.swingItem();
            return stack;
        }

        if (!this.baited && this.tryApplyBait(world, player)) {
            return stack;
        }

        world.playSoundAtEntity(player, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
        if (!world.isRemote) {
            world.spawnEntityInWorld(this.baited
                    ? new EntityFishHook(world, player, true)
                    : new EntityFishHook(world, player));
        }
        player.swingItem();
        return stack;
    }

    private boolean tryApplyBait(World world, EntityPlayer player) {
        for (int slot = 0; slot < 9; ++slot) {
            ItemStack bait = player.inventory.getStackInSlot(slot);
            if (bait == null || !FishingRodBaitingRecipe.isFishingBait(bait)) {
                continue;
            }
            world.playSoundAtEntity(player, "mob.slime.attack", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
            if (!player.capabilities.isCreativeMode) {
                player.inventory.consumeInventoryItem(bait.itemID);
            }
            ItemStack baitedRod = player.getCurrentEquippedItem().copy();
            baitedRod.itemID = NMItems.netherFishingRodBaited.itemID;
            player.inventory.setInventorySlotContents(player.inventory.currentItem, baitedRod);
            return true;
        }
        return false;
    }
}
