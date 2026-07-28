package com.itlesports.nightmaremode.agriculture;

public enum ChunkAttribute {
    MOISTURE("Moisture"),
    NITROGEN("Nitrogen reserves"),
    POTASSIUM("Potassium richness"),
    ACIDITY("Soil acidity"),
    POROSITY("Land porosity");

    private final String displayName;

    ChunkAttribute(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return this.displayName;
    }
}
