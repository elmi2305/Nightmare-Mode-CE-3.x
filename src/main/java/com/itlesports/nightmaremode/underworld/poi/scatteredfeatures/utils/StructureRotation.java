package com.itlesports.nightmaremode.underworld.poi.scatteredfeatures.utils;

/** clockwise quarter turns in the template's x/z plane. */
public final class StructureRotation {
    private StructureRotation() {}

    public static int x(int x, int z, int width, int depth, int rotation) {
        switch (rotation & 3) {
            case 1: return depth - 1 - z;
            case 2: return width - 1 - x;
            case 3: return z;
            default: return x;
        }
    }

    public static int z(int x, int z, int width, int depth, int rotation) {
        switch (rotation & 3) {
            case 1: return x;
            case 2: return depth - 1 - z;
            case 3: return width - 1 - x;
            default: return z;
        }
    }

    public static int stairs(int metadata, int rotation) {
        int facing = metadata & 3;
        for (int turn = 0; turn < (rotation & 3); turn++) {
            switch (facing) {
                case 0: facing = 2; break;
                case 1: facing = 3; break;
                case 2: facing = 1; break;
                default: facing = 0;
            }
        }
        return (metadata & ~3) | facing;
    }

    public static int facing(int facing, int rotation) {
        for (int turn = 0; turn < (rotation & 3); turn++) {
            switch (facing) {
                case 2: facing = 5; break;
                case 3: facing = 4; break;
                case 4: facing = 2; break;
                case 5: facing = 3; break;
                default: break;
            }
        }
        return facing;
    }

    public static int rail(int metadata, boolean powered, int rotation) {
        int shape = powered ? metadata & 7 : metadata;
        int flags = powered ? metadata & ~7 : 0;
        for (int turn = 0; turn < (rotation & 3); turn++) {
            switch (shape) {
                case 0: shape = 1; break;
                case 1: shape = 0; break;
                case 2: shape = 5; break;
                case 3: shape = 4; break;
                case 4: shape = 2; break;
                case 5: shape = 3; break;
                case 6: shape = 7; break;
                case 7: shape = 8; break;
                case 8: shape = 9; break;
                case 9: shape = 6; break;
                default: break;
            }
        }
        return flags | shape;
    }

    public static int lever(int metadata, int rotation) {
        int direction = metadata & 7;
        for (int turn = 0; turn < (rotation & 3); turn++) {
            switch (direction) {
                case 0: direction = 7; break;
                case 7: direction = 0; break;
                case 5: direction = 6; break;
                case 6: direction = 5; break;
                case 1: direction = 3; break;
                case 2: direction = 4; break;
                case 3: direction = 2; break;
                case 4: direction = 1; break;
                default: break;
            }
        }
        return (metadata & ~7) | direction;
    }

    public static int trapdoor(int metadata, int rotation) {
        int direction = metadata & 3;
        for (int turn = 0; turn < (rotation & 3); turn++) {
            switch (direction) {
                case 0: direction = 3; break;
                case 1: direction = 2; break;
                case 2: direction = 0; break;
                default: direction = 1;
            }
        }
        return (metadata & ~3) | direction;
    }
}
