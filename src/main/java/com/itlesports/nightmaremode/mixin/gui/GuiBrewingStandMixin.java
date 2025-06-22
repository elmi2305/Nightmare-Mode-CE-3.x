package com.itlesports.nightmaremode.mixin.gui;

import net.minecraft.src.GuiBrewingStand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(GuiBrewingStand.class)
public class GuiBrewingStandMixin {
    @ModifyConstant(method = "drawGuiContainerBackgroundLayer", constant = @Constant(floatValue = 400.0F))
    private float reduceBrewTimeVisual(float constant){
        return 20f;
    }
}
