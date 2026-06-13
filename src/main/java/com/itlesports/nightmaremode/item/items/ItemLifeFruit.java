package com.itlesports.nightmaremode.item.items;

import com.itlesports.nightmaremode.item.items.template.NMFoodItem;
import net.minecraft.src.*;

public class ItemLifeFruit extends NMFoodItem {
    public ItemLifeFruit(int iItemID, String sItemName) {
        super(iItemID, 0, 1.0f, false, sItemName, false);
        this.setMaxStackSize(4);
    }
    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!player.isPotionActive(Potion.hunger) && !player.isPotionActive(Potion.wither) && player.getMaxHealth() < 30) {
            player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
        } else {
            player.onCantConsume();
        }
        return stack;
    }

    @Override
    protected void onFoodEaten(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        if(entityPlayer.getMaxHealth() < 30){
            entityPlayer.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(entityPlayer.getMaxHealth() + 1);
            if(entityPlayer.getHealth() >= (entityPlayer.getMaxHealth() - 1)){
                entityPlayer.setHealth(entityPlayer.getMaxHealth());
            }
            entityPlayer.playSound("random.levelup", 0.75f, 1.0f);
        }

        super.onFoodEaten(itemStack, world, entityPlayer);
    }
}
