package com.itlesports.nightmaremode.mixin.interfaces;

import net.minecraft.src.MapGenBase;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MapGenBase.class)
public interface MapGenBaseAccess {
    @Accessor("worldObj")
    World nightmareMode$getWorld();
}
