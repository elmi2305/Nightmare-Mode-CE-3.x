package com.itlesports.nightmaremode.item.items;

import com.itlesports.nightmaremode.util.NMFields;
import com.itlesports.nightmaremode.util.NMSanityUtils;
import net.minecraft.src.*;

import static btw.community.nightmaremode.NightmareMode.SANITY_CAPACITY_LEVEL;

public class ItemSanityAnchor extends ItemFood {
    private final int requiredLevel;

    public ItemSanityAnchor(int id, int requiredLevel) {
        super(id, 0, 0.0F, false);
        this.requiredLevel = requiredLevel;
        this.setMaxStackSize(1);
        this.setAlwaysEdible();
        this.setCreativeTab(CreativeTabs.tabFood);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        int current = MathHelper.clamp_int(player.getData(SANITY_CAPACITY_LEVEL), 0, NMFields.MAX_SANITY_CAPACITY_LEVEL);
        if (current != requiredLevel) {
            player.onCantConsume();
            return stack;
        }
        player.setItemInUse(stack, getMaxItemUseDuration(stack));
        return stack;
    }

    @Override
    public ItemStack onEaten(ItemStack stack, World world, EntityPlayer player) {
        int current = MathHelper.clamp_int(player.getData(SANITY_CAPACITY_LEVEL), 0, NMFields.MAX_SANITY_CAPACITY_LEVEL);
        if (current != requiredLevel) return stack;
        if (!world.isRemote) {
            player.setData(SANITY_CAPACITY_LEVEL, current + 1);
            NMSanityUtils.restore(player, NMFields.SANITY_PER_CAPACITY_LEVEL);
            if (!player.capabilities.isCreativeMode) stack.stackSize--;
            player.playSound("random.levelup", 0.8F, 0.8F + current * 0.2F);
        }
        return stack;
    }

    @Override public String getModId() { return "nightmare"; }
}
