package com.itlesports.nightmaremode.item.items;

import com.itlesports.nightmaremode.util.NetherRecall;
import net.minecraft.src.*;

import java.util.List;

public class ItemLimitedRecallPotion extends Item {
    public ItemLimitedRecallPotion(int id) {
        super(id);
        this.setMaxStackSize(1);
        this.setMaxDamage(20);
        this.setUnlocalizedName("ifhyLimitedRecallPotion");
        this.setTextureName("nightmare:ifhyLimitedRecallPotion");
    }

    @Override public int getMaxItemUseDuration(ItemStack stack) { return 16; }
    @Override public EnumAction getItemUseAction(ItemStack stack) { return EnumAction.drink; }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (world.isRemote || NetherRecall.isValid(stack, player)) player.setItemInUse(stack, getMaxItemUseDuration(stack));
        return stack;
    }

    @Override
    public ItemStack onEaten(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote && player instanceof EntityPlayerMP serverPlayer) {
            if (NetherRecall.isValid(stack, player) && NetherRecall.recall(serverPlayer, stack)) stack.stackSize = 0;
        }
        return stack;
    }

    @Override
    public void onUpdate(ItemStack stack, World world, EntityPlayer entity, int slot, boolean held) {
        if (!world.isRemote) NetherRecall.age(stack, entity);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List lines, boolean advanced) {
        lines.add(StatCollector.translateToLocal("item.ifhyLimitedRecallPotion.desc"));
        lines.add(StatCollector.translateToLocal("item.ifhyLimitedRecallPotion.warning"));
    }
}
