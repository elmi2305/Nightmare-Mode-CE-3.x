package com.itlesports.nightmaremode.mixin;

import com.itlesports.nightmaremode.util.NMInventoryLocks;
import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.world.SandboxRules;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Slot.class)
public class SlotMixin {
    @Shadow @Final public IInventory inventory;
    @Shadow @Final private int slotIndex;

    @Inject(method = "onPickupFromSlot", at = @At("HEAD"))
    private void nightmareMode$recordSlotAcquisition(EntityPlayer player, ItemStack stack, CallbackInfo ci) {
        SandboxRules.recordAcquisition(player, stack);
    }

    @Inject(method = "putStack", at = @At("HEAD"), cancellable = true)
    private void destroyStoredRecall(ItemStack stack, org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci) {
        if (!(this.inventory instanceof InventoryPlayer) && com.itlesports.nightmaremode.util.NetherRecall.isRecall(stack)) {
            stack.stackSize = 0;
            this.inventory.setInventorySlotContents(this.slotIndex, null);
            this.inventory.onInventoryChanged();
            ci.cancel();
        }
    }

    @Inject(method = "isItemValid", at = @At("HEAD"), cancellable = true)
    private void lockUnavailablePlayerSlots(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (this.inventory instanceof InventoryPlayer inv
                && !NMInventoryLocks.isMainInventorySlotUnlocked(inv.player, this.slotIndex)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "canTakeStack", at = @At("HEAD"), cancellable = true)
    private void preventTakingFromUnavailablePlayerSlots(EntityPlayer player, CallbackInfoReturnable<Boolean> cir) {
        if (NightmareMode.lockDownCreative
                && (player.capabilities.isCreativeMode || SandboxRules.isSandbox(player.worldObj))
                && !(this.inventory instanceof InventoryPlayer)
                && !SandboxRules.mayCreate(player.worldObj, this.inventory.getStackInSlot(this.slotIndex))) {
            cir.setReturnValue(false);
            return;
        }
        if (this.inventory instanceof InventoryPlayer inv
                && !NMInventoryLocks.isMainInventorySlotUnlocked(inv.player, this.slotIndex)) {
            cir.setReturnValue(false);
        }
    }
}
