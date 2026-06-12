package com.itlesports.nightmaremode.mixin.entity;

import net.minecraft.src.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Entity.class)
public interface EntityAccessor {
    @Accessor("isInWeb")
    boolean getIsInWeb();
    @Accessor("invulnerable")
    void setInvulnerable(boolean par1);
    @Invoker("setSize")
    void invokeSetSize(float width, float height);
}
