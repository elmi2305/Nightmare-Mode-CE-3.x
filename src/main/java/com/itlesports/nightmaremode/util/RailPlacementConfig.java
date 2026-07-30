package com.itlesports.nightmaremode.util;

import net.minecraft.src.Block;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class RailPlacementConfig {
    private static final List<Integer> substrateBlockIds = new ArrayList<>();

    static {
        substrateBlockIds.add(Block.netherrack.blockID);
    }

    private RailPlacementConfig() {
    }

    public static List<Integer> getSubstrateBlockIds() {
        return Collections.unmodifiableList(substrateBlockIds);
    }

    public static void setSubstrateBlockIds(List<Integer> blockIds) {
        substrateBlockIds.clear();
        if (blockIds != null) {
            substrateBlockIds.addAll(blockIds);
        }
    }
}
