package com.itlesports.nightmaremode.mixin;

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
}
