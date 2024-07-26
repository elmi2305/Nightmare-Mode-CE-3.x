package com.itlesports.nightmaremode.mixin;

import net.minecraft.src.Entity;
import net.minecraft.src.EntityEnderman;
import net.minecraft.src.EntityLivingBase;
import net.minecraft.src.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EntityEnderman.class)
public interface EntityEndermanAccess {
    @Accessor("isAggressive")
    void setIsAggressive(boolean aggressive);
    @Invoker("teleportToEntity")
    boolean invokeTeleportToEntity(Entity par1Entity);
}
