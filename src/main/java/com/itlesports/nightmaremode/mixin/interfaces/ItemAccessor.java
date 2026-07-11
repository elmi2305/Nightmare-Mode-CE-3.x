package com.itlesports.nightmaremode.mixin.interfaces;

import net.minecraft.src.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Item.class)
public interface ItemAccessor {
    @Accessor("maxStackSize")
    int getMaxStackSize();
    @Invoker("setMaxDamage")
    Item invSetMaxDamage(int maxDamage);
}
