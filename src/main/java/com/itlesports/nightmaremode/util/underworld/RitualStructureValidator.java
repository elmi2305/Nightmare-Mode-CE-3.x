package com.itlesports.nightmaremode.util.underworld;

import btw.block.BTWBlocks;
import btw.community.nightmaremode.NightmareMode;
import net.minecraft.src.Block;
import net.minecraft.src.World;

public class RitualStructureValidator {

    public static boolean validate(World world, int cx, int cy, int cz) {

        return checkCenterPillar(world, cx, cy, cz)
                && checkCornerPillars(world, cx, cy, cz)
                && checkCornerBridges(world, cx, cy, cz)
                && checkSkyExposure(world, cx, cy, cz);
    }

    public static boolean isIntact(World world, int cx, int cy, int cz) {
        return checkCenterPillar(world, cx, cy, cz)
                && checkCornerPillars(world, cx, cy, cz)
                && checkCornerBridges(world, cx, cy, cz)
                && checkSkyExposure(world, cx, cy, cz);
    }

    private static boolean checkCenterPillar(World world, int cx, int cy, int cz) {
        return isSteelAt(world, cx, cy - 1, cz)
                && isSteelAt(world, cx, cy - 2, cz)
                && isSteelAt(world, cx, cy - 3, cz)
                && isSteelAt(world, cx, cy - 4, cz);
    }

    private static boolean checkCornerBridges(World world, int cx, int cy, int cz) {
        return isSteelAt(world, cx + 1, cy - 2, cz + 1)
                && isSteelAt(world, cx - 1, cy - 2, cz + 1)
                && isSteelAt(world, cx + 1, cy - 2, cz - 1)
                && isSteelAt(world, cx - 1, cy - 2, cz - 1);
    }

    private static boolean checkCornerPillars(World world, int cx, int cy, int cz) {
        int[] offsets = { -2, 2 };
        for (int dx : offsets) {
            for (int dz : offsets) {

                if (!isSteelAt(world, cx + dx, cy - 3, cz + dz)) return false;

                if (!isSteelAt(world, cx + dx, cy - 4, cz + dz)) return false;
            }
        }
        return true;
    }

    private static boolean checkSkyExposure(World world, int cx, int cy, int cz) {
        return world.canBlockSeeTheSky(cx, cy + 1, cz);
    }

    private static boolean isSteelAt(World world, int x, int y, int z) {
        int id = world.getBlockId(x, y, z);
        if(id == 0 && NightmareMode.devMode){world.setBlock(x,y,z, BTWBlocks.soulforgedSteelBlock.blockID); return true;}
        return id == BTWBlocks.soulforgedSteelBlock.blockID;
    }
    private static boolean isPassableAt(World world, int x, int y, int z) {
        int id = world.getBlockId(x, y, z);
        Block block = Block.blocksList[id];
        if (block == null) return true;
        return !block.isOpaqueCube();
    }
}
