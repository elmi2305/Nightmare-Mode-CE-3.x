package com.itlesports.nightmaremode.util;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.World;

public final class OreMiningContext {
    private record Attempt(EntityPlayer player, World world, int x, int y, int z) {}
    private static final ThreadLocal<Attempt> CURRENT = new ThreadLocal<>();
    private OreMiningContext() {}

    public static void begin(EntityPlayer player, World world, int x, int y, int z) {
        CURRENT.set(new Attempt(player, world, x, y, z));
    }
    public static void clear() { CURRENT.remove(); }
    public static EntityPlayer miner(World world, int x, int y, int z) {
        Attempt attempt = CURRENT.get();
        return attempt != null && attempt.world == world && attempt.x == x && attempt.y == y && attempt.z == z
                ? attempt.player : null;
    }
}
