package com.itlesports.nightmaremode.mixin;

import btw.block.BTWBlocks;
import btw.community.nightmaremode.NightmareMode;
import btw.item.BTWItems;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.InventoryPlayer;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryPlayer.class)
public class InventoryPlayerMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void perfectStart(EntityPlayer par1EntityPlayer, CallbackInfo ci){
        if (NightmareMode.perfectStart) {
            InventoryPlayer thisObj = (InventoryPlayer)(Object)this;
            thisObj.addItemStackToInventory(new ItemStack(BTWBlocks.idleOven));
            thisObj.addItemStackToInventory(new ItemStack(Item.axeStone));
            thisObj.addItemStackToInventory(new ItemStack(BTWItems.tangledWeb));
        }
    }
}
