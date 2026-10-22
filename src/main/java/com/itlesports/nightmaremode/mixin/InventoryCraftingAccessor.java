package com.itlesports.nightmaremode.mixin;

import net.minecraft.src.Container;
import net.minecraft.src.InventoryCrafting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(InventoryCrafting.class)
public interface InventoryCraftingAccessor {
    @Accessor("eventHandler")
    Container nm$getEventHandler();
}
