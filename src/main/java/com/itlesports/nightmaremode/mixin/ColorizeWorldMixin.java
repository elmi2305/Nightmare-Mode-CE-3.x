package com.itlesports.nightmaremode.mixin;

import com.prupe.mcpatcher.cc.ColorizeWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ColorizeWorld.class)
public class ColorizeWorldMixin {
    @ModifyConstant(method = "reset", constant = @Constant(floatValue = 0.2F),remap = false)
    private static float changeFog0(float constant){
        return 0f;
    }
    @ModifyConstant(method = "reset", constant = @Constant(floatValue = 0.03F),remap = false)
    private static float changeFog1(float constant){
        return 0f;
    }
}
