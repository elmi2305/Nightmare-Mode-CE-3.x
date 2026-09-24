package com.itlesports.nightmaremode.mixin.gui;

import net.minecraft.src.GuiScreenBook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GuiScreenBook.class)
public interface GuiScreenBookAccessor {
    @Accessor("currPage") int nm$getCurrentPage();
}
