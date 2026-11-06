package com.itlesports.nightmaremode.util;

import api.world.difficulty.DifficultyParam;
import com.itlesports.nightmaremode.worldgen.OverworldTierHelper;
import net.minecraft.src.ChunkCoordinates;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.MathHelper;
import net.minecraft.src.World;

public final class NMRespawn {
    private static final ThreadLocal<NMRespawn> CURRENT = new ThreadLocal<>();

    private final World world;
    private final ChunkCoordinates origin;

    private NMRespawn(EntityPlayerMP oldPlayer, EntityPlayerMP newPlayer) {
        world = newPlayer.worldObj;
        // respawnPlayer changes oldPlayer.dimension before calling hcs; its world still identifies the death dimension.
        int sourceDimension = oldPlayer.worldObj.provider.dimensionId;
        int targetDimension = world.provider.dimensionId;
        double scale = sourceDimension == -1 && targetDimension != -1 ? 8.0D
                : sourceDimension != -1 && targetDimension == -1 ? 0.125D : 1.0D;
        origin = new ChunkCoordinates(MathHelper.floor_double(oldPlayer.posX * scale),
                MathHelper.floor_double(oldPlayer.posY), MathHelper.floor_double(oldPlayer.posZ * scale));
    }

    public static long getRespawnTime(long deathTime, long currentTime, int totalDeaths) {
        if (deathTime < 120000L && totalDeaths < 50) return 0L;
        return ((currentTime / 24000L) + (currentTime % 24000L == 0L ? 0L : 1L)) * 24000L;
    }

    public static boolean returnsToSpawn(EntityPlayerMP player) {
        return !player.playerConqueredTheEnd && player.worldObj.provider.dimensionId == 0
                && OverworldTierHelper.isOuterOverworld(player.worldObj, player.posX, player.posZ);
    }

    public static void runWithDeathOrigin(EntityPlayerMP oldPlayer, EntityPlayerMP newPlayer, Runnable respawn) {
        NMRespawn previous = CURRENT.get();
        boolean enabled = !oldPlayer.playerConqueredTheEnd
                && (Boolean)newPlayer.worldObj.getDifficultyParameter(DifficultyParam.ShouldPlayersHardcoreSpawn.class);
        try {
            CURRENT.set(enabled ? new NMRespawn(oldPlayer, newPlayer) : null);
            respawn.run();
        } finally {
            if (previous == null) CURRENT.remove();
            else CURRENT.set(previous);
        }
    }

    public static ChunkCoordinates getOrigin(World world) {
        NMRespawn context = CURRENT.get();
        return context != null && context.world == world ? context.origin : null;
    }
}
