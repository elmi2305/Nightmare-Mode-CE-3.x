package com.itlesports.nightmaremode.util;

import net.minecraft.src.Block;
import net.minecraft.src.World;
import java.util.Random;

public final class NetherMushroomGrowth {
    private NetherMushroomGrowth() {}

    public static void tick(Block mushroom, World world, int x, int y, int z, Random random) {
        if (world.isRemote || random.nextInt(140) != 0 || world.getFullBlockLightValue(x, y, z) != 0) return;
        int neighbors = 0;
        for (int dx = -4; dx <= 4; ++dx) for (int dz = -4; dz <= 4; ++dz) {
            for (int dy = -1; dy <= 1; ++dy) {
                int id = world.getBlockId(x + dx, y + dy, z + dz);
                if ((id == Block.mushroomBrown.blockID || id == Block.mushroomRed.blockID) && ++neighbors >= 5) return;
            }
        }
        int targetX = x + random.nextInt(3) - 1;
        int targetY = y + random.nextInt(3) - 1;
        int targetZ = z + random.nextInt(3) - 1;
        if (world.isAirBlock(targetX, targetY, targetZ)
                && world.getFullBlockLightValue(targetX, targetY, targetZ) == 0
                && mushroom.canBlockStay(world, targetX, targetY, targetZ)) {
            world.setBlock(targetX, targetY, targetZ, mushroom.blockID, 0, 3);
        }
    }
}
