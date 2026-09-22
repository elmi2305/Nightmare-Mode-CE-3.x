package com.itlesports.nightmaremode.util.interfaces;

/** Persistent dye state for vanilla and trapped chests. */
public interface IColoredChest {
    boolean nm$hasChestColor();

    int nm$getChestColor();

    void nm$setChestColor(int color);
}
