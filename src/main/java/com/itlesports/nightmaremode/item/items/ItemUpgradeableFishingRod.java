package com.itlesports.nightmaremode.item.items;

import btw.crafting.recipe.types.customcrafting.FishingRodBaitingRecipe;
import net.minecraft.src.EntityFishHook;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemFishingRod;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;

/** Fishing rod variant which retains its NBT upgrades while bait is applied or lost. */
public class ItemUpgradeableFishingRod extends ItemFishingRod {
    private final int counterpartItemId;
    private final boolean baited;

    public ItemUpgradeableFishingRod(int id, int counterpartConstructorId, boolean baited, int durability) {
        super(id);
        // Item constructors receive IDs without Minecraft's 256-item block offset.
        this.counterpartItemId = counterpartConstructorId + 256;
        this.baited = baited;
        this.setMaxDamage(durability);
    }

    public boolean isBaited() {
        return this.baited;
    }

    public int getCounterpartItemId() {
        return this.counterpartItemId;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (player.fishEntity != null) {
            int damage = player.fishEntity.catchFish();
            ItemStack held = player.getCurrentEquippedItem();
            if (held != null) held.damageItem(damage, player);
            player.swingItem();
            return stack;
        }

        if (!this.baited && this.tryApplyBait(world, player)) return stack;

        world.playSoundAtEntity(player, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
        if (!world.isRemote) world.spawnEntityInWorld(new EntityFishHook(world, player, this.baited));
        player.swingItem();
        return stack;
    }

    private boolean tryApplyBait(World world, EntityPlayer player) {
        for (int slot = 0; slot < 9; ++slot) {
            ItemStack bait = player.inventory.getStackInSlot(slot);
            if (bait == null || !FishingRodBaitingRecipe.isFishingBait(bait)) continue;
            world.playSoundAtEntity(player, "mob.slime.attack", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
            if (!player.capabilities.isCreativeMode) player.inventory.consumeInventoryItem(bait.itemID);
            ItemStack baitedRod = player.getCurrentEquippedItem().copy();
            baitedRod.itemID = this.counterpartItemId;
            player.inventory.setInventorySlotContents(player.inventory.currentItem, baitedRod);
            return true;
        }
        return false;
    }
}
