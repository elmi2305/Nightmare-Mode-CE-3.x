package com.itlesports.nightmaremode.mixin;

import btw.community.nightmaremode.NightmareMode;
import btw.item.items.FoodItem;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(FoodItem.class)
public class FoodItemMixin extends ItemFood{
    public FoodItemMixin(int par1, int par2, float par3, boolean par4) {
        super(par1, par2, par3, par4);
    }

    @Override
    protected void onFoodEaten(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) {
        if(!NightmareMode.nite){
            super.onFoodEaten(par1ItemStack,par2World,par3EntityPlayer);
        }
    }
}
