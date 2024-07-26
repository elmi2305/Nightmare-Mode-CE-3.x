package com.itlesports.nightmaremode.mixin;

import net.minecraft.src.EntityFireball;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(EntityFireball.class)
public class EntityFireballMixin {
    @ModifyConstant(method = "onUpdate", constant = @Constant(floatValue = 0.2f))
    private float accelerationModifier(float constant){
        return 0.3f;
    }
}
