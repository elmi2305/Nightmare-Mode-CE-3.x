package com.itlesports.nightmaremode.util;

import net.minecraft.src.Block;
import net.minecraft.src.BlockFenceGate;
import net.minecraft.src.IBlockAccess;

import java.util.ArrayList;
import java.util.List;

public final class FenceGateShape {
    // north, south, west, east; connections never depend on the open bit.
    private static final int[] DX = {0, 0, -1, 1};
    private static final int[] DZ = {-1, 1, 0, 0};
    private static final List<Box>[] CLOSED = new List[16];
    private static final List<Box>[] OPEN = new List[16];
    private static final List<Box>[] COLLISION = new List[16];

    static {
        for (int mask : new int[]{5, 6, 9, 10}) {
            CLOSED[mask] = build(mask, false);
            OPEN[mask] = build(mask, true);
            List<Box> collision = new ArrayList<>();
            for (int side = 0; side < 4; side++) {
                if ((mask & (1 << side)) == 0) continue;
                collision.add(new Box(DX[side] < 0 ? 0 : 0.375, 0,
                        DZ[side] < 0 ? 0 : 0.375,
                        DX[side] > 0 ? 1 : 0.625, 1.5,
                        DZ[side] > 0 ? 1 : 0.625));
            }
            COLLISION[mask] = List.copyOf(collision);
        }
    }

    private FenceGateShape() {}

    public record Box(double minX, double minY, double minZ,
                      double maxX, double maxY, double maxZ) {}

    public static int cornerMask(IBlockAccess world, int x, int y, int z) {
        int mask = 0;
        for (int side = 0; side < 4; side++) {
            if (Block.blocksList[world.getBlockId(x + DX[side], y, z + DZ[side])] instanceof BlockFenceGate) {
                mask |= 1 << side;
            }
        }
        // ambiguous junctions and straight runs retain the original gate shape.
        return CLOSED[mask] == null ? 0 : mask;
    }

    public static List<Box> parts(int mask, boolean open) {
        return (open ? OPEN : CLOSED)[mask];
    }

    public static List<Box> collision(int mask) {
        return COLLISION[mask];
    }

    public static Box collisionBounds(int mask) {
        return new Box((mask & 4) != 0 ? 0 : 0.375, 0, (mask & 1) != 0 ? 0 : 0.375,
                (mask & 8) != 0 ? 1 : 0.625, 1.5, (mask & 2) != 0 ? 1 : 0.625);
    }

    public static Box bounds(int mask, boolean open) {
        double minX = 1, minZ = 1, maxX = 0, maxZ = 0;
        for (Box box : parts(mask, open)) {
            minX = Math.min(minX, box.minX);
            minZ = Math.min(minZ, box.minZ);
            maxX = Math.max(maxX, box.maxX);
            maxZ = Math.max(maxZ, box.maxZ);
        }
        return new Box(minX, 0, minZ, maxX, 1, maxZ);
    }

    private static List<Box> build(int mask, boolean open) {
        List<Box> boxes = new ArrayList<>();
        int cornerX = (mask & 4) != 0 ? -1 : 1;
        int cornerZ = (mask & 1) != 0 ? -1 : 1;
        for (int side = 0; side < 4; side++) {
            if ((mask & (1 << side)) == 0) continue;
            double hingeX = 0.5 + DX[side] * 0.4375;
            double hingeZ = 0.5 + DZ[side] * 0.4375;
            boxes.add(post(hingeX, hingeZ, 0.3125, 1));
            // each leaf turns 90 degrees around its boundary hinge, away from the corner.
            double tipX = open ? hingeX - (DX[side] == 0 ? cornerX * 0.4375 : 0) : 0.5;
            double tipZ = open ? hingeZ - (DZ[side] == 0 ? cornerZ * 0.4375 : 0) : 0.5;
            if (open) boxes.add(post(tipX, tipZ, 0.375, 0.9375));
            double minX = Math.min(hingeX, tipX);
            double maxX = Math.max(hingeX, tipX);
            double minZ = Math.min(hingeZ, tipZ);
            double maxZ = Math.max(hingeZ, tipZ);
            if (hingeX == tipX) {
                minX -= 0.0625;
                maxX += 0.0625;
                minZ += 0.0625;
                maxZ -= 0.0625;
            } else {
                minZ -= 0.0625;
                maxZ += 0.0625;
                minX += 0.0625;
                maxX -= 0.0625;
            }
            boxes.add(new Box(minX, 0.375, minZ, maxX, 0.5625, maxZ));
            boxes.add(new Box(minX, 0.75, minZ, maxX, 0.9375, maxZ));
        }
        // one meeting stile avoids coincident faces at the closed corner.
        if (!open) boxes.add(post(0.5, 0.5, 0.375, 0.9375));
        return List.copyOf(boxes);
    }

    private static Box post(double x, double z, double minY, double maxY) {
        return new Box(x - 0.0625, minY, z - 0.0625, x + 0.0625, maxY, z + 0.0625);
    }
}
