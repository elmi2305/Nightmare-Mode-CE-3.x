package com.itlesports.nightmaremode.mixin.entity;

import net.minecraft.src.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Entity.class)
public interface EntityAccessor {
    @Accessor("isInWeb")
    boolean getIsInWeb();
    @Accessor("invulnerable")
    void setInvulnerable(boolean par1);
}
