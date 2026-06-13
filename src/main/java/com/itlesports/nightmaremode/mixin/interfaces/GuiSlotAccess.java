package com.itlesports.nightmaremode.mixin.interfaces;

import net.minecraft.src.GuiSlot;
import net.minecraft.src.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GuiSlot.class)
public interface GuiSlotAccess {
    @Accessor("mc")
    Minecraft getMc();
}
