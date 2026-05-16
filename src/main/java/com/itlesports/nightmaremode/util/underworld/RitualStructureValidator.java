package com.itlesports.nightmaremode.util.underworld;

import btw.block.BTWBlocks;
import btw.community.nightmaremode.NightmareMode;
import net.minecraft.src.Block;
import net.minecraft.src.World;

/**
 * Checks whether the 5×5 altar structure around a portal core is intact.
 * <p>
 * Layout (top-down, Y = core level):
 *   S . . . S
 *   . S . S .
 *   . . C . .
 *   . S . S .
 *   S . . . S
 * Side view
 *   . . C . .
 *   . . S . .
 *   . S S S .
 *   S . S . S
 *   S . S . S
 */
public class RitualStructureValidator {

    /**
     * Full structure validation. Call when the core is first placed
     * or when doing a periodic re-check in INVALID state.
     */
    public static boolean validate(World world, int cx, int cy, int cz) {
//        boolean check1 = checkCenterPillar(world, cx, cy, cz);
//        boolean check2 = checkCornerPillars(world, cx, cy, cz);
//        boolean check3 = checkCornerBridges(world, cx, cy, cz);
//        boolean check4 = checkSkyExposure(world, cx, cy, cz);
        return checkCenterPillar(world, cx, cy, cz)
                && checkCornerPillars(world, cx, cy, cz)
                && checkCornerBridges(world, cx, cy, cz)
                && checkSkyExposure(world, cx, cy, cz);
    }

    /**
     * Lighter intact-check used every tick during VALID_IDLE and ACTIVE states.
     * Only re-validates the steel blocks — skips the open-space check for performance.
     */
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
                // bottom tier - same Y as the core
                if (!isSteelAt(world, cx + dx, cy - 3, cz + dz)) return false;
                // top tier - one above core
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