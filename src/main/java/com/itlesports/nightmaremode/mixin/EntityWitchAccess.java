package com.itlesports.nightmaremode.mixin;

import net.minecraft.src.EntityWitch;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityWitch.class)
public interface EntityWitchAccess {
    @Accessor("witchAttackTimer")
    void setWitchAttackTimer(int time);
}
