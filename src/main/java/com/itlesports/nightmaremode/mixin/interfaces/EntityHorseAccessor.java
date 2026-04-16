package com.itlesports.nightmaremode.mixin.interfaces;

import net.minecraft.src.AnimalChest;
import net.minecraft.src.EntityHorse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityHorse.class)
public interface EntityHorseAccessor {
    @Accessor("horseChest")
    AnimalChest getHorseChest();
}
