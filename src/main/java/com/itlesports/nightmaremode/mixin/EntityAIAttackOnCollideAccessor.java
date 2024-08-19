package com.itlesports.nightmaremode.mixin;

import net.minecraft.src.EntityAIAttackOnCollide;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityAIAttackOnCollide.class)
public interface EntityAIAttackOnCollideAccessor {
    @Accessor("attackTick")
    void setAttackTick(int par1);
}
