package com.itlesports.nightmaremode.mixin;

import net.minecraft.src.EntityArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityArrow.class)
public interface EntityArrowAccessor {
    @Accessor("inGround")
    boolean getInGround();
}
