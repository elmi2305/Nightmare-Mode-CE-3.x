package com.itlesports.nightmaremode.util.underworld;

import btw.block.BTWBlocks;
import net.minecraft.src.Block;
import net.minecraft.src.World;

/**
 * Checks whether the 5×5 altar structure around a portal core is intact.
 * <p>
 * Layout (top-down, Y = core level):
 * <p>
 *   S . . . S      Y = core
 *   . . . . .
 *   . . C . .      C = portal core block
 *   . . . . .
 *   S . . . S
 * Required steel blocks:
 *   Corner bottoms (cy+0):  (+-2, 0, +-2)  - 4 blocks
 *   Corner tops    (cy+1):  (+-2, +1, +-2) - 4 blocks
 *   Center pillar  (cy+1):  (0, +1, 0)     - 1 block
 *   Center pillar  (cy+2):  (0, +2, 0)     - 1 block
 *   Center pillar  (cy+3):  (0, +3, 0)     - 1 block
 */
// TODO: decide final ritual shape
public class RitualStructureValidator {

    /**
     * Full structure validation. Call when the core is first placed
     * or when doing a periodic re-check in INVALID state.
     */
    public static boolean validate(World world, int cx, int cy, int cz) {
        return checkCenterPillar(world, cx, cy, cz)
                && checkCornerPillars(world, cx, cy, cz)
                && checkInnerSpaceClear(world, cx, cy, cz)
                && checkSkyExposure(world, cx, cy, cz);
    }

    /**
     * Lighter intact-check used every tick during VALID_IDLE and ACTIVE states.
     * Only re-validates the steel blocks — skips the open-space check for performance.
     */
    public static boolean isIntact(World world, int cx, int cy, int cz) {
        return checkCenterPillar(world, cx, cy, cz)
                && checkCornerPillars(world, cx, cy, cz)
                && checkSkyExposure(world, cx, cy, cz);
    }

    private static boolean checkCenterPillar(World world, int cx, int cy, int cz) {
        return isSteelAt(world, cx, cy - 1, cz)
                && isSteelAt(world, cx, cy - 2, cz)
                && isSteelAt(world, cx, cy - 3, cz);
    }

    private static boolean checkCornerPillars(World world, int cx, int cy, int cz) {
        int[] offsets = { -2, 2 };
        for (int dx : offsets) {
            for (int dz : offsets) {
                // bottom tier - same Y as the core
                if (!isSteelAt(world, cx + dx, cy, cz + dz)) return false;
                // top tier - one above core
                if (!isSteelAt(world, cx + dx, cy - 1, cz + dz)) return false;
            }
        }
        return true;
    }

    /**
     * The inner ring (everything between the corner pillars) must be air
     * at ground level and at Y+1, so the ritual space stays open.
     * Skips the core position itself (that's the portal core block, not air).
     */
    private static boolean checkInnerSpaceClear(World world, int cx, int cy, int cz) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (dx == 0 && dz == 0) continue;

                if (!isPassableAt(world, cx + dx, cy, cz + dz))     return false;
                if (!isPassableAt(world, cx + dx, cy + 1, cz + dz)) return false;
            }
        }
        return true;
    }

    private static boolean checkSkyExposure(World world, int cx, int cy, int cz) {
        return world.canBlockSeeTheSky(cx, cy + 4, cz);
    }

    private static boolean isSteelAt(World world, int x, int y, int z) {
        int id = world.getBlockId(x, y, z);
        return id == BTWBlocks.soulforgedSteelBlock.blockID;
    }
    private static boolean isPassableAt(World world, int x, int y, int z) {
        int id = world.getBlockId(x, y, z);
        Block block = Block.blocksList[id];
        if (block == null) return true;
        return !block.isOpaqueCube();
    }
}