package com.itlesports.nightmaremode.underworld.poi.scatteredfeatures.utils;

import btw.block.blocks.StairsBlockBase;
import net.minecraft.src.*;

/** target block types own metadata meaning; material variants are never treated as facings. */
public final class StructureBlockRotation {
    private StructureBlockRotation() {}

    public static boolean isStairs(Block block) {
        return block instanceof BlockStairs || block instanceof StairsBlockBase;
    }

    public static int rotate(Block block, int metadata, int rotation) {
        rotation &= 3;
        if (block == null || rotation == 0) return metadata;
        if (isStairs(block)) return StructureRotation.stairs(metadata, rotation);
        if (block instanceof BlockRailBase) {
            return StructureRotation.rail(metadata, ((BlockRailBase) block).isPowered(), rotation);
        }
        if (block instanceof BlockDoor) {
            return (metadata & 8) != 0 ? metadata : (metadata & ~3) | ((metadata + rotation) & 3);
        }
        if (block instanceof BlockTrapDoor) return StructureRotation.trapdoor(metadata, rotation);
        if (block instanceof BlockLever) return StructureRotation.lever(metadata, rotation);
        if (block instanceof BlockSign) {
            return ((BlockSign) block).isFreestanding ? (metadata + rotation * 4) & 15
                    : StructureRotation.facing(metadata, rotation);
        }
        if (block.blockID == Block.signWall.blockID) return StructureRotation.facing(metadata, rotation);
        if (block instanceof BlockRotatedPillar) {
            int axis = metadata & 12;
            if ((rotation & 1) != 0 && (axis == 4 || axis == 8)) {
                return (metadata & ~12) | (axis == 4 ? 8 : 4);
            }
            return metadata;
        }
        if (block.blockID == Block.blockNetherQuartz.blockID && (rotation & 1) != 0) {
            if (metadata == 3) return 4;
            if (metadata == 4) return 3;
        }
        for (int turn = 0; turn < rotation; turn++) {
            // btw's reverse turn matches (-z, x) in template coordinates.
            metadata = block.rotateMetadataAroundYAxis(metadata, true);
        }
        return metadata & 15;
    }
}
