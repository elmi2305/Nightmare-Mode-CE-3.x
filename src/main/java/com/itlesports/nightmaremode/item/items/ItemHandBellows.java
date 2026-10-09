package com.itlesports.nightmaremode.item.items;

import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.item.items.template.NMItem;
import net.minecraft.src.*;

public class ItemHandBellows extends NMItem {
    public ItemHandBellows(int id) {
        super(id);
        this.setMaxStackSize(1);
        this.setCreativeTab(CreativeTabs.tabTools);
        this.setUnlocalizedName("ifhyHandBellows");
        this.setTextureName("nightmare:ifhyHandBellows");
    }

    private boolean hasFreshAir(EntityPlayer player, World world) {
        int x = MathHelper.floor_double(player.posX);
        int y = MathHelper.floor_double(player.posY + player.getEyeHeight());
        int z = MathHelper.floor_double(player.posZ);
        return player.dimension == 0 && player.posY >= 54.0D && y <= 120
                && !player.isInsideOfMaterial(Material.water) && world.canBlockSeeTheSky(x, y, z);
    }

    private int findEmptyFlask(EntityPlayer player) {
        for (int slot = 0; slot < player.inventory.mainInventory.length; ++slot) {
            ItemStack stack = player.inventory.mainInventory[slot];
            if (stack != null && stack.itemID == NMItems.emptyAirFlask.itemID) return slot;
        }
        return -1;
    }

    @Override public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!this.hasFreshAir(player, world)) {
            if (!world.isRemote) player.addChatMessage(I18n.getString("item.ifhyHandBellows.invalid"));
        } else if (this.findEmptyFlask(player) >= 0) player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
        return stack;
    }

    @Override public int getMaxItemUseDuration(ItemStack stack) { return 40; }
    @Override public EnumAction getItemUseAction(ItemStack stack) { return EnumAction.block; }

    @Override public ItemStack onEaten(ItemStack stack, World world, EntityPlayer player) {
        int slot = this.findEmptyFlask(player);
        if (!world.isRemote && slot >= 0 && this.hasFreshAir(player, world)) {
            player.inventory.decrStackSize(slot, 1);
            ItemStack filled = new ItemStack(NMItems.airFlask);
            if (!player.inventory.addItemStackToInventory(filled)) player.dropPlayerItemWithRandomChoice(filled, false);
            world.playSoundAtEntity(player, "random.breath", 0.6F, 1.3F);
        }
        return stack;
    }

}
