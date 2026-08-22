package com.itlesports.nightmaremode.mixin;

import btw.item.BTWItems;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemBow.class)
public abstract class ItemBowMixin extends Item {
    @Shadow public abstract ItemStack getFirstArrowStackInHotbar(EntityPlayer player);
    @Shadow protected abstract void applyBowEnchantmentsToArrow(ItemStack bowStack, EntityArrow entityArrow);

    public ItemBowMixin(int par1) {
        super(par1);
    }

    @ModifyConstant(method = "applyBowEnchantmentsToArrow", constant = @Constant(doubleValue = 0.5))
    private double reducePowerDefaultScaling(double constant){
        return 0.15d;
    }

    @Redirect(method = "onPlayerStoppedUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/InventoryPlayer;consumeInventoryItem(I)Z"))
    private boolean addInfinityEnchantment(InventoryPlayer inventoryPlayer, int par1){
        ItemStack itemStack = inventoryPlayer.player.getHeldItem();
        boolean bInfiniteArrows = EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, itemStack) > 0 || inventoryPlayer.player.capabilities.isCreativeMode;
        if(bInfiniteArrows) return false;
        return inventoryPlayer.consumeInventoryItem(par1);
    }

    @Redirect(method = "onPlayerStoppedUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/ItemBow;applyBowEnchantmentsToArrow(Lnet/minecraft/src/ItemStack;Lnet/minecraft/src/EntityArrow;)V"))
    private void makeInfinityArrowsUnpickable(ItemBow bow, ItemStack bowStack, EntityArrow arrow) {
        this.applyBowEnchantmentsToArrow(bowStack, arrow);

        if (EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, bowStack) > 0) {
            arrow.canBePickedUp = 0;
        }
    }
}
