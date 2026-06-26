package com.itlesports.nightmaremode.util.elements;

public class LogSettings {
    public final boolean logChests;
    public final boolean logContainers;
    public final boolean logAllTileEntities;
    public final boolean logItemRemoval;
    public final boolean logIndirectBreaks;

    public LogSettings(int flags) {

        this.logChests = (flags & 0b1) == 1;
        this.logContainers = (flags & 0b10) >> 1 == 1;
        this.logAllTileEntities = (flags & 0b11) == 0b11;

        this.logItemRemoval = (flags & 0b100) != 0;
        this.logIndirectBreaks = (flags & 0b1000) != 0;
    }
}