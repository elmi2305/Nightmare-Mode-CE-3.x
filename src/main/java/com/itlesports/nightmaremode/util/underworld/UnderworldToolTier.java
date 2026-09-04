package com.itlesports.nightmaremode.util.underworld;

public enum UnderworldToolTier {
    STEEL(0), TITANIUM(1), TUNGSTEN(2), ECLIPSE(3);

    private final int level;

    UnderworldToolTier(int level) { this.level = level; }
    public int level() { return level; }
    public boolean canHarvest(UnderworldToolTier required) { return level >= required.level; }
}
