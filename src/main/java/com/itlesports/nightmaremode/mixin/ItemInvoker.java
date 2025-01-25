package com.itlesports.nightmaremode.mixin;

import net.minecraft.src.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
@Mixin(Item.class)
public interface ItemInvoker {
    @Invoker("setMaxStackSize")
    Item invokeSetMaxStackSize(int par1);
}
