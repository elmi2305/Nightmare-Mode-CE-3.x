package com.itlesports.nightmaremode.mixin.interfaces;

import net.minecraft.src.GuiWorldSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GuiWorldSlot.class)
public interface GuiWorldSlotAccessor {
    @Invoker("elementClicked")
    void invokeElementClicked(int i, boolean bl);
}
