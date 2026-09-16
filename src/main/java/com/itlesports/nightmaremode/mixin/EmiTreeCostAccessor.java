package com.itlesports.nightmaremode.mixin;

import emi.dev.emi.emi.runtime.EmiDrawContext;
import emi.shims.java.net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(targets = "emi.dev.emi.emi.screen.BoMScreen$Cost", remap = false)
public interface EmiTreeCostAccessor {
    @Accessor("x") int nm$getX();
    @Accessor("y") int nm$getY();
    @Invoker("getAmountText") Text nm$getAmountText();
    @Invoker("render") void nm$render(EmiDrawContext context);
}
