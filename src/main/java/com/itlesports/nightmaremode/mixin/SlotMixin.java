package com.itlesports.nightmaremode.mixin;

import com.itlesports.nightmaremode.util.NMInventoryLocks;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IInventory;
import net.minecraft.src.InventoryPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Slot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Slot.class)
public class SlotMixin {
    @Shadow @Final public IInventory inventory;
    @Shadow @Final private int slotIndex;

    @Inject(method = "isItemValid", at = @At("HEAD"), cancellable = true)
    private void lockUnavailablePlayerSlots(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (this.inventory instanceof InventoryPlayer inv
                && !NMInventoryLocks.isMainInventorySlotUnlocked(inv.player, this.slotIndex)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "canTakeStack", at = @At("HEAD"), cancellable = true)
    private void preventTakingFromUnavailablePlayerSlots(EntityPlayer player, CallbackInfoReturnable<Boolean> cir) {
        if (this.inventory instanceof InventoryPlayer inv
                && !NMInventoryLocks.isMainInventorySlotUnlocked(inv.player, this.slotIndex)) {
            cir.setReturnValue(false);
        }
    }
}
