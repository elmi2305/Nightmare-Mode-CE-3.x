package com.itlesports.nightmaremode.agriculture;

public enum ChunkAttribute {
    MOISTURE("Moisture"),
    NITROGEN("Nitrogen"),
    POTASSIUM("Potassium"),
    ACIDITY("Acidity"),
    POROSITY("Porosity");

    private final String displayName;

    ChunkAttribute(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return this.displayName;
    }
}
