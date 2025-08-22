package com.itlesports.nightmaremode.mixin.gui;

import com.itlesports.nightmaremode.item.items.bloodItems.IBloodTool;
import com.itlesports.nightmaremode.item.items.bloodItems.ItemBloodArmor;
import net.minecraft.src.ContainerEnchantment;
import net.minecraft.src.EnchantmentHelper;
import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ContainerEnchantment.class)
public class ContainerEnchantmentMixin {
    @Shadow public IInventory tableInventory;

    @ModifyArg(method = "enchantItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityPlayer;addExperienceLevel(I)V"))
    private int reduceXPLossOnBloodArmor(int par1){
        ItemStack stack = this.tableInventory.getStackInSlot(0);
        if(stack != null && stack.getItem() instanceof IBloodTool){
            return Math.min(-1, (int)Math.floor(par1 * 0.4));
        }
        return par1;
    }
}
