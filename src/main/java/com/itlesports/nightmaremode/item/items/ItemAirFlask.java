package com.itlesports.nightmaremode.item.items;

import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.item.items.template.NMItem;
import net.minecraft.src.*;

public class ItemAirFlask extends NMItem {
    public ItemAirFlask(int id) {
        super(id);
        this.setMaxStackSize(1);
        this.setCreativeTab(CreativeTabs.tabTools);
        this.setUnlocalizedName("ifhyAirFlask");
        this.setTextureName("nightmare:ifhyAirFlask");
    }

    @Override public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (player.getAir() < 300) player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
        return stack;
    }

    @Override public int getMaxItemUseDuration(ItemStack stack) { return 40; }
    @Override public EnumAction getItemUseAction(ItemStack stack) { return EnumAction.drink; }

    @Override public ItemStack onEaten(ItemStack stack, World world, EntityPlayer player) {
        if (world.isRemote) return stack;
        player.setAir(Math.min(300, Math.max(0, player.getAir()) + 120));
        world.playSoundAtEntity(player, "random.breath", 0.6F, 1.0F);
        if (player.capabilities.isCreativeMode) return stack;
        --stack.stackSize;
        ItemStack empty = new ItemStack(NMItems.emptyAirFlask);
        if (stack.stackSize <= 0) return empty;
        if (!player.inventory.addItemStackToInventory(empty)) player.dropPlayerItemWithRandomChoice(empty, false);
        return stack;
    }

}
