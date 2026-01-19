package com.itlesports.nightmaremode.mixin.entity;

import net.minecraft.src.EntitySlime;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EntitySlime.class)
public interface EntitySlimeAccessor {
    @Invoker("setSlimeSize")
    void invokeSetSlimeSize(int a);
}
