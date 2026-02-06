package com.itlesports.nightmaremode.util.interfaces;

import net.minecraft.src.EnumFacing;

public interface IPlayerDirectionTracker {
    EnumFacing nm$getHeldDirection();
    void nm$setHeldDirectionServer(EnumFacing dir);
}