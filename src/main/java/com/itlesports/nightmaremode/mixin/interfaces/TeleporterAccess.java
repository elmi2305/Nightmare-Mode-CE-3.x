package com.itlesports.nightmaremode.mixin.interfaces;

import net.minecraft.src.Teleporter;
import net.minecraft.src.WorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Teleporter.class)
public interface TeleporterAccess {
    @Accessor("worldServerInstance")
    WorldServer getWorld();
}
