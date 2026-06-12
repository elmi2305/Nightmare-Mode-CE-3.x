package com.itlesports.nightmaremode.mixin.interfaces;

import net.minecraft.src.FontRenderer;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.GuiSelectWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GuiScreen.class)
public interface GuiScreenAccess {
    @Accessor("fontRenderer")
    FontRenderer getFontRenderer();
}
