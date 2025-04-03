package com.itlesports.nightmaremode.mixin;

import btw.community.nightmaremode.NightmareMode;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(SlotCrafting.class)
public class SlotCraftingMixin extends Slot {
    @Shadow private EntityPlayer thePlayer;

    public SlotCraftingMixin(IInventory par1IInventory, int par2, int par3, int par4) {
        super(par1IInventory, par2, par3, par4);
    }

    @Inject(method = "onCrafting(Lnet/minecraft/src/ItemStack;)V", at = @At("HEAD"))
    private void craft(ItemStack par1ItemStack, CallbackInfo ci){
        if (NightmareMode.isAprilFools) {
            par1ItemStack.attemptDamageItem( (int) (Math.abs(this.thePlayer.rand.nextGaussian() * 0.5f) * par1ItemStack.getMaxDamage() - 1), this.thePlayer.rand);
        }
    }

    @Inject(method = "onCrafting(Lnet/minecraft/src/ItemStack;I)V", at = @At("HEAD"))
    private void craft(ItemStack par1ItemStack, int par2, CallbackInfo ci){
        if (NightmareMode.isAprilFools) {
            double gaussian = this.thePlayer.rand.nextGaussian(); // Mean = 0, Std Dev = 1
            double normalized = (gaussian + 3) / 6; // Shifting and scaling to [0,1]
            int damage = (int) (Math.max(0, Math.min(1, normalized)) * par1ItemStack.getMaxDamage() - 1);

            par1ItemStack.attemptDamageItem(damage, this.thePlayer.rand);
        }
    }
}
