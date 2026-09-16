package com.itlesports.nightmaremode.mixin;

import emi.dev.emi.emi.runtime.EmiDrawContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(targets = "emi.dev.emi.emi.screen.BoMScreen$Node", remap = false)
public interface EmiTreeNodeAccessor {
    @Accessor("x") int nm$getX();
    @Accessor("y") int nm$getY();
    @Accessor("width") int nm$getWidth();
    @Invoker("render") void nm$render(EmiDrawContext context, int mouseX, int mouseY, float delta);
}
