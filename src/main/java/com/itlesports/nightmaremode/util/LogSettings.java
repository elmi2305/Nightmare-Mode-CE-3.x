package com.itlesports.nightmaremode.util;

public class LogSettings {
    public final boolean logChests;
    public final boolean logContainers;
    public final boolean logAllTileEntities;
    public final boolean logItemRemoval;
    public final boolean logIndirectBreaks;

    public LogSettings(int flags) {
        int loggingLevel = flags & 0b11;

        this.logChests = loggingLevel == 1;
        this.logContainers = loggingLevel == 2;
        this.logAllTileEntities = loggingLevel == 3;

        this.logItemRemoval = (flags & 0b100) != 0;
        this.logIndirectBreaks = (flags & 0b1000) != 0;
    }
}