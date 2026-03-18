package com.itlesports.nightmaremode.mixin;

import btw.block.BTWBlocks;
import btw.community.nightmaremode.NightmareMode;
import btw.item.BTWItems;
import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.block.blocks.NMBlock;
import com.itlesports.nightmaremode.item.NMItems;
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
        InventoryPlayer inv = (InventoryPlayer)(Object)this;

        if (NightmareMode.perfectStart) {
            inv.addItemStackToInventory(new ItemStack(BTWBlocks.idleOven));
            inv.addItemStackToInventory(new ItemStack(Item.axeStone));
            inv.addItemStackToInventory(new ItemStack(BTWItems.tangledWeb));
            inv.addItemStackToInventory(new ItemStack(BTWBlocks.looseDirtSlab, 16));
            inv.addItemStackToInventory(new ItemStack(BTWItems.stone, 8));
        }
        if(NightmareMode.isAprilFools){
            inv.addItemStackToInventory(new ItemStack(NMItems.creeperBallSoup));
            inv.addItemStackToInventory(new ItemStack(BTWItems.bedroll,64));
        }
        if(NightmareMode.extraArmor){
            inv.addItemStackToInventory(new ItemStack(Item.bootsLeather));
            inv.addItemStackToInventory(new ItemStack(BTWItems.woolLeggings));
            inv.addItemStackToInventory(new ItemStack(BTWItems.woolChest));
            inv.addItemStackToInventory(new ItemStack(BTWItems.woolHelmet));
        }


        inv.addItemStackToInventory(new ItemStack(NMBlocks.underworldPortal));

    }
}
