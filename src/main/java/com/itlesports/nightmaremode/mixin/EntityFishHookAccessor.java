package com.itlesports.nightmaremode.mixin;

import net.minecraft.src.EntityFishHook;
import net.minecraft.src.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityFishHook.class)
public interface EntityFishHookAccessor {
    @Accessor("angler")
    EntityPlayer getAngler();
}
