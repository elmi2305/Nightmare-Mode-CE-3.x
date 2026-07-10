package com.itlesports.nightmaremode.item.items.template;

import api.item.items.ProgressiveCraftingItem;
import btw.item.BTWItems;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;

public class NMProgressiveItem extends ProgressiveCraftingItem {
    private int duration = 72000;
    private int damage = 600;
    private String soundID = "mob.zombie.woodbreak";
    private final int returnID;
    public NMProgressiveItem(int iItemID, int returnID) {
        super(iItemID);
        this.returnID = returnID;
    }
    @Override
    public int getMaxItemUseDuration(ItemStack par1ItemStack) {
        return this.duration;
    }

    public NMProgressiveItem setDuration(int duration) {
        this.duration = duration;
        return this;
    }

    @Override
    public ItemStack onEaten(ItemStack stack, World world, EntityPlayer player) {
        player.playSound(soundID, 0.1f, 1.25f + world.rand.nextFloat() * 0.25f);
        return new ItemStack(returnID, 1, 0);
    }

    @Override
    protected int getProgressiveCraftingMaxDamage() {
        return damage;
    }
    public NMProgressiveItem setDamage(int damage) {
        this.damage = damage;
        return this;
    }
    public NMProgressiveItem setSoundID(String soundID) {
        this.soundID = soundID;
        return this;
    }
}
