package com.itlesports.nightmaremode.network;

import net.minecraft.src.EnumFacing;

public interface IPlayerDirectionTracker {
    EnumFacing nm$getHeldDirection();
    void nm$setHeldDirectionServer(EnumFacing dir);
}