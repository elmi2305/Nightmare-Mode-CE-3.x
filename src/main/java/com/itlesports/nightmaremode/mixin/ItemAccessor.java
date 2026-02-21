package com.itlesports.nightmaremode.mixin;

import net.minecraft.src.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Item.class)
public interface ItemAccessor {
    @Accessor("maxStackSize")
    int getMaxStackSize();
}
