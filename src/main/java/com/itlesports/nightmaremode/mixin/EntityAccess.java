package com.itlesports.nightmaremode.mixin;

import net.minecraft.src.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Entity.class)
public interface EntityAccess {
    @Accessor("isInWeb")
    boolean getIsInWeb();

    @Accessor("invulnerable")
    void setInvulnerable(boolean invulnerable);
}
