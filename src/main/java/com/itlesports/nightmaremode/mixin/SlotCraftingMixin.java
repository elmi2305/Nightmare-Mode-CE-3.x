package com.itlesports.nightmaremode.mixin;

import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.skill.SkillHandler;
import com.itlesports.nightmaremode.skill.SkillTreeData;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SlotCrafting.class)
public class SlotCraftingMixin extends Slot {
    @Shadow private EntityPlayer thePlayer;
    @Shadow private int amountCrafted;

    public SlotCraftingMixin(IInventory par1IInventory, int par2, int par3, int par4) {
        super(par1IInventory, par2, par3, par4);
    }

    @Inject(method = "onCrafting(Lnet/minecraft/src/ItemStack;)V", at = @At("HEAD"))
    private void craft(ItemStack par1ItemStack, CallbackInfo ci){
        if (par1ItemStack != null && par1ItemStack.itemID == Item.book.itemID) {
            SkillHandler.incrementBooksCrafted(this.thePlayer, Math.max(1, this.amountCrafted));
        }
        if (par1ItemStack != null && par1ItemStack.itemID == Block.bookShelf.blockID) {
            SkillHandler.incrementBookshelvesCrafted(this.thePlayer, Math.max(1, this.amountCrafted));
        }
        if (NightmareMode.isAprilFools) {
            this.damageCraftedItem(par1ItemStack);
        }
    }

    @Inject(method = "onCrafting(Lnet/minecraft/src/ItemStack;I)V", at = @At("HEAD"))
    private void craft(ItemStack par1ItemStack, int par2, CallbackInfo ci){
        if (NightmareMode.isAprilFools) {
            double gaussian = this.thePlayer.rand.nextGaussian(); // Mean = 0, Std Dev = 1
            double normalized = (gaussian + 3) / 6; // Shifting and scaling to [0,1]
            float durabilityBonus = SkillHandler.getPlayerData(this.thePlayer).craftingDurabilityBonus;
            int damage = (int) ((Math.max(0, Math.min(1, normalized)) * par1ItemStack.getMaxDamage() - 1)
                    * Math.max(0.0F, 1.0F - durabilityBonus));

            par1ItemStack.attemptDamageItem(damage, this.thePlayer.rand);
        }
    }

    private void damageCraftedItem(ItemStack stack) {
        if (stack == null || !stack.isItemStackDamageable()) {
            return;
        }
        SkillTreeData data = SkillHandler.getPlayerData(this.thePlayer);
        int damage = (int)((Math.abs(this.thePlayer.rand.nextGaussian() * 0.5F) * stack.getMaxDamage() - 1)
                * Math.max(0.0F, 1.0F - data.craftingDurabilityBonus));
        stack.attemptDamageItem(Math.max(0, damage), this.thePlayer.rand);
    }
}
