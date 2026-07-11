package com.itlesports.nightmaremode.item.items.template;

import api.item.items.ProgressiveCraftingItem;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;

public class NMProgressiveItem extends ProgressiveCraftingItem {
    private int damage = 600;
    private String soundID = "mob.zombie.woodbreak";
    private final int returnID;
    public NMProgressiveItem(int iItemID, int returnID) {
        super(iItemID);
        this.returnID = returnID;
    }

    @Override
    public void onCreated(ItemStack stack, World world, EntityPlayer player) {
        if (player.timesCraftedThisTick == 0 && world.isRemote) {
            player.playSound(soundID, 1.0f, world.rand.nextFloat() * 0.1f + 0.9f);
        }
        super.onCreated(stack, world, player);
    }
    @Override
    protected int getProgressiveCraftingMaxDamage() {
        return damage;
    }

    public NMProgressiveItem setTargetDurability(int damage) {
        this.damage = damage;
        return this;
    }

    @Override
    public ItemStack onEaten(ItemStack stack, World world, EntityPlayer player) {
        player.playSound(soundID, 0.1f, 1.25f + world.rand.nextFloat() * 0.25f);
        return new ItemStack(returnID, 1, 0);
    }

    public NMProgressiveItem setSoundID(String soundID) {
        this.soundID = soundID;
        return this;
    }
}
