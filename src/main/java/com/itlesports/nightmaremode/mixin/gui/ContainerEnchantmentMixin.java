package com.itlesports.nightmaremode.mixin.gui;

import com.itlesports.nightmaremode.item.items.bloodItems.IBloodTool;
import com.itlesports.nightmaremode.skill.SkillHandler;
import net.minecraft.src.ContainerEnchantment;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ContainerEnchantment.class)
public class ContainerEnchantmentMixin {
    @Shadow public IInventory tableInventory;

    @Redirect(method = "enchantItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityPlayer;addExperienceLevel(I)V"))
    private void reduceXPLossOnBloodArmor(EntityPlayer player, int par1){
        ItemStack stack = this.tableInventory.getStackInSlot(0);
        int adjustedCost = par1;
        if(stack != null && stack.getItem() instanceof IBloodTool){
            adjustedCost = Math.min(-1, (int)Math.floor(par1 * 0.4));
        }
        else if(stack != null){
            adjustedCost = Math.min(-1, (int)Math.floor(par1 * 0.7));
        }
        float skillMultiplier = Math.max(0.0F, 1.0F - SkillHandler.getPlayerData(player).enchantCostReduction);
        player.addExperienceLevel(Math.min(-1, (int)Math.ceil(adjustedCost * skillMultiplier)));
    }
}
