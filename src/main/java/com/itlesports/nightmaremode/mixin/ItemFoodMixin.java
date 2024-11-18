package com.itlesports.nightmaremode.mixin;

import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemFood.class)
public class ItemFoodMixin {
    @Inject(method = "onEaten", at = @At("TAIL"))
    private void goldenCarrotFunctionality(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer, CallbackInfoReturnable<ItemStack> cir){
        if(par1ItemStack.itemID == Item.goldenCarrot.itemID){
            par3EntityPlayer.addPotionEffect(new PotionEffect(Potion.nightVision.id, 2400,0)); // 2 minutes of night vision
        }
    }
}
