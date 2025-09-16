package com.itlesports.nightmaremode.mixin.gui;

import net.minecraft.src.InventoryBasic;
import net.minecraft.src.InventoryEnderChest;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryEnderChest.class)
public abstract class InventoryEnderChestMixin extends InventoryBasic {
//
    public InventoryEnderChestMixin(String string, boolean bl, int i) {
        super(string, bl, i);
    }

}
