package com.itlesports.nightmaremode.mixin;

import btw.inventory.container.CookingVesselContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(CookingVesselContainer.class)
public class CookingVesselContainerMixin {
    @ModifyArgs(method = "<init>", at = @At(value = "INVOKE", target = "Lbtw/inventory/container/InventoryContainer;<init>(Lnet/minecraft/src/IInventory;Lnet/minecraft/src/IInventory;IIIIII)V"))
    private static void restoreVesselGrid(Args args) {
        args.set(3, 9);
        args.set(4, 8);
    }
}
