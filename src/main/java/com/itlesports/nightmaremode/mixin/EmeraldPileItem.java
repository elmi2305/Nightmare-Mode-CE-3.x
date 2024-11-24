package com.itlesports.nightmaremode.mixin;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(btw.item.items.EmeraldPileItem.class)
public class EmeraldPileItem {
    @Inject(method = "onItemRightClick", at = @At(value = "FIELD", target = "Lnet/minecraft/src/PlayerCapabilities;isCreativeMode:Z"))
    private void decreaseStackSizeOnUse(ItemStack stack, World world, EntityPlayer player, CallbackInfoReturnable<ItemStack> cir){
        if(!player.capabilities.isCreativeMode){
            --stack.stackSize;
        }
    }
}
