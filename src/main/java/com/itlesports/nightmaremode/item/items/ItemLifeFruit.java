package com.itlesports.nightmaremode.item.items;

import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.item.items.template.NMFoodItem;
import com.itlesports.nightmaremode.util.interfaces.EntityPlayerExt;
import com.itlesports.nightmaremode.util.interfaces.FoodStatsExt;
import net.minecraft.src.*;

import static com.itlesports.nightmaremode.util.NMFields.MAX_FOOD_FROM_FRUITS;
import static com.itlesports.nightmaremode.util.NMFields.MAX_HEALTH_FROM_FRUITS;

public class ItemLifeFruit extends NMFoodItem {
    public ItemLifeFruit(int iItemID, String sItemName) {
        super(iItemID, 0, 0f, false, sItemName, false);
        this.setMaxStackSize(4);
    }
    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!player.isPotionActive(Potion.hunger) && !player.isPotionActive(Potion.wither) && ((player.getFoodStats().getFoodLevel() < 90 && this.itemID == NMItems.honeyMelon.itemID) || (player.getMaxHealth() < 30 && this.itemID == NMItems.lifeFruit.itemID))) {
            player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
        } else {
            player.onCantConsume();
        }
        return stack;
    }

    @Override
    protected void onFoodEaten(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        if(entityPlayer.getMaxHealth() < MAX_HEALTH_FROM_FRUITS && this.itemID == NMItems.lifeFruit.itemID){
            entityPlayer.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(entityPlayer.getMaxHealth() + 1);
            if(entityPlayer.getHealth() >= (entityPlayer.getMaxHealth() - 1)){
                entityPlayer.setHealth(entityPlayer.getMaxHealth());
            }
            entityPlayer.playSound("random.levelup", 0.75f, 1.0f);
        }
        FoodStatsExt fs = (FoodStatsExt)(entityPlayer.getFoodStats());
        if(fs.nightmareMode$getMaxFoodLevel() < MAX_FOOD_FROM_FRUITS && this.itemID == NMItems.honeyMelon.itemID){
            fs.nightmareMode$setMaxFoodLevel(fs.nightmareMode$getMaxFoodLevel() + 6);
            ((EntityPlayerExt)entityPlayer).nightmareMode$setFoodMax(fs.nightmareMode$getMaxFoodLevel()); // sets it for the player and sends the packet server -> client

            if(entityPlayer.getFoodStats().getFoodLevel() >= (fs.nightmareMode$getMaxFoodLevel() - 6)){
                entityPlayer.foodStats.setFoodLevel(fs.nightmareMode$getMaxFoodLevel());
            }
            entityPlayer.playSound("random.levelup", 0.75f, 1.0f);
        }

        super.onFoodEaten(itemStack, world, entityPlayer);
    }
}
