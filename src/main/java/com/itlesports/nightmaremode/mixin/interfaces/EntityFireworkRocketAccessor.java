package com.itlesports.nightmaremode.mixin.interfaces;

import net.minecraft.src.EntityFireworkRocket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityFireworkRocket.class)
public interface EntityFireworkRocketAccessor {
    @Accessor("lifetime")
    int getLifetime();

    @Accessor("lifetime")
    void setLifetime(int lifetime);
}
