package com.itlesports.nightmaremode.mixin;

import net.minecraft.src.Entity;
import net.minecraft.src.EntityEnderman;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EntityEnderman.class)
public interface EntityEndermanAccess {
    @Invoker("teleportToEntity")
    boolean invokeTeleportToEntity(Entity par1Entity);
}
