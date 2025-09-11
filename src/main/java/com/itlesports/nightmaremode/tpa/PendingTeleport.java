package com.itlesports.nightmaremode.tpa;

import net.minecraft.src.EntityPlayerMP;

public class PendingTeleport {
    public EntityPlayerMP playerToTeleport;
    public EntityPlayerMP targetPlayer;
    public int ticksRemaining;  // e.g. 10 seconds * 20 ticks = 200 ticks

    public PendingTeleport(EntityPlayerMP player, EntityPlayerMP target, int delaySeconds) {
        this.playerToTeleport = player;
        this.targetPlayer = target;
        this.ticksRemaining = delaySeconds * 20;
    }
}
