package com.itlesports.nightmaremode.mixin;

import com.itlesports.nightmaremode.skill.SkillLockedCrafting;
import com.itlesports.nightmaremode.util.NMInventoryLocks;
import net.minecraft.src.Container;
import net.minecraft.src.CraftingManager;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.InventoryPlayer;
import net.minecraft.src.InventoryCrafting;
import net.minecraft.src.IRecipe;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Slot;
import net.minecraft.src.SlotCrafting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(Container.class)
public class ContainerMixin {
    @Shadow public List inventorySlots;

    @Inject(method = "slotClick", at = @At("HEAD"), cancellable = true)
    private void blockNumberKeySwapToLockedHotbar(int slotId, int mouseButton, int modifier, EntityPlayer player, CallbackInfoReturnable<ItemStack> cir) {
        if (slotId >= 0 && slotId < this.inventorySlots.size()) {
            Slot slot = (Slot)this.inventorySlots.get(slotId);
            if (slot instanceof SlotCrafting craftingSlot) {
                IRecipe recipe = CraftingManager.getInstance().findMatchingIRecipe((InventoryCrafting)craftingSlot.getCraftMatrix(), player.worldObj);
                if (SkillLockedCrafting.isLocked(player, recipe)) {
                    SkillLockedCrafting.notifyLocked(player, recipe);
                    cir.setReturnValue(null);
                    return;
                }
            }
        }
        if (modifier == 2 && mouseButton >= 0 && mouseButton < 9
                && !NMInventoryLocks.isMainInventorySlotUnlocked(player, mouseButton)) {
            cir.setReturnValue(null);
        }
    }

    @Inject(method = "attemptToMergeWithSlot", at = @At("HEAD"), cancellable = true)
    private void preventMergingIntoLockedSlots(ItemStack stackSource, int slotId, CallbackInfoReturnable<Boolean> cir) {
        if (this.nightmareMode$isLockedPlayerSlot(slotId)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "attemptToMergeWithSlotIfEmpty", at = @At("HEAD"), cancellable = true)
    private void preventMergingIntoEmptyLockedSlots(ItemStack stackSource, int slotId, CallbackInfoReturnable<Boolean> cir) {
        if (this.nightmareMode$isLockedPlayerSlot(slotId)) {
            cir.setReturnValue(false);
        }
    }

    @Unique
    private boolean nightmareMode$isLockedPlayerSlot(int slotId) {
        if (slotId < 0 || slotId >= this.inventorySlots.size()) {
            return false;
        }

        Slot slot = (Slot)this.inventorySlots.get(slotId);
        if (!(slot.inventory instanceof InventoryPlayer inv)) {
            return false;
        }
        return !NMInventoryLocks.isMainInventorySlotUnlocked(inv.player, slot.getSlotIndex());
    }
}
