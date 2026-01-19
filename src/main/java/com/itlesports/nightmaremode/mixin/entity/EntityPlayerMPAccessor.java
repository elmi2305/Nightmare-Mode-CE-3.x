package com.itlesports.nightmaremode.mixin.entity;

import net.minecraft.src.EntityPlayerMP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EntityPlayerMP.class)
public interface EntityPlayerMPAccessor {
    @Invoker("incrementWindowID")
    void invokeIncrementWindowID();
    @Accessor("currentWindowId")
    int getCurrentWindowId();

}
