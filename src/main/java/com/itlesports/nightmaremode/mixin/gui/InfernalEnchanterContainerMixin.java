package com.itlesports.nightmaremode.mixin.gui;

import btw.inventory.container.InfernalEnchanterContainer;
import com.itlesports.nightmaremode.item.items.bloodItems.IBloodTool;
import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InfernalEnchanterContainer.class)
public class InfernalEnchanterContainerMixin {
    @Shadow public IInventory tableInventory;

    @Shadow(remap = false) public int[] currentEnchantmentLevels;

    @ModifyConstant(method = "setCurrentEnchantingLevels", constant = @Constant(intValue = 30, ordinal = 5),remap = false)
    private int reduceEnchantmentCostMultiplier(int constant){
        ItemStack itemToEnchantStack = this.tableInventory.getStackInSlot(1);
        if(itemToEnchantStack != null && itemToEnchantStack.getItem() instanceof IBloodTool){
            return 10;
        }
        return 20;
    }
    @ModifyArg(method = "enchantItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityPlayer;addExperienceLevel(I)V"))
    private int reduceXPLossOnBloodArmor(int par1){
        ItemStack stack = this.tableInventory.getStackInSlot(0);
        if(stack != null && stack.getItem() instanceof IBloodTool){
            return Math.min(-1, (int)Math.floor(par1 * 0.4));
        }
        return par1;
    }
    @Inject(method = "setCurrentEnchantingLevels", at = @At(value = "CONSTANT", args = "intValue=30",ordinal = 5), remap = false)
    private void beforeICostIncrementStore(int iMaxPowerLevel, int iCostMultiplier, int iMaxBaseCostForItem, CallbackInfo ci) {
        if (iMaxPowerLevel == 1) {
            this.currentEnchantmentLevels[0] = 20;
        } else if (iMaxPowerLevel == 2) {
            this.currentEnchantmentLevels[0] = 10;
            this.currentEnchantmentLevels[1] = 20;
        } else if (iMaxPowerLevel == 3) {
            this.currentEnchantmentLevels[0] = 10;
            this.currentEnchantmentLevels[1] = 15;
            this.currentEnchantmentLevels[2] = 20;
        } else if (iMaxPowerLevel == 4) {
            this.currentEnchantmentLevels[0] = 8;
            this.currentEnchantmentLevels[1] = 10;
            this.currentEnchantmentLevels[2] = 15;
            this.currentEnchantmentLevels[3] = 20;
        } else if (iMaxPowerLevel == 5) {
            this.currentEnchantmentLevels[0] = 6;
            this.currentEnchantmentLevels[1] = 8;
            this.currentEnchantmentLevels[2] = 10;
            this.currentEnchantmentLevels[3] = 15;
            this.currentEnchantmentLevels[4] = 20;
        }
        // this injection just overwrites the established values earlier in the method
        // I'm not about to do 20 modifyConstants for every value
    }
}
