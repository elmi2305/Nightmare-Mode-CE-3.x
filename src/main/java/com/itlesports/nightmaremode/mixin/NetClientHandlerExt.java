package com.itlesports.nightmaremode.mixin;

import net.minecraft.src.Minecraft;
import net.minecraft.src.NetClientHandler;
import net.minecraft.src.WorldClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(NetClientHandler.class)
public interface NetClientHandlerExt {
    @Accessor("worldClient")
    WorldClient getClient();
    @Accessor("mc")
    Minecraft getMc();
}