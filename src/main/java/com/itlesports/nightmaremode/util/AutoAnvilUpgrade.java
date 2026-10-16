package com.itlesports.nightmaremode.util;

import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.Item;

public enum AutoAnvilUpgrade {
    SIGNAL(20, "Signal", 0xF04B42),
    BLOOD(10, "Blood", 0xA91842),
    DEADZONE(5, "Deadzone", 0xAA70E8),
    PHASE(2, "Phase", 0x55E5E0);

    public final int ticksPerHit;
    public final String name;
    public final int color;

    AutoAnvilUpgrade(int ticksPerHit, String name, int color) {
        this.ticksPerHit = ticksPerHit;
        this.name = name;
        this.color = color;
    }

    public int tier() { return this.ordinal() + 1; }
    public String textureName() { return "ifhy" + this.name + "AnvilUpgrade"; }
    public boolean preventsAnvilWear() { return this.tier() >= DEADZONE.tier(); }
    public boolean needsHammer() { return this != PHASE; }
    public Item item() {
        return switch (this) {
            case SIGNAL -> NMItems.signalAnvilUpgrade;
            case BLOOD -> NMItems.bloodAnvilUpgrade;
            case DEADZONE -> NMItems.deadzoneAnvilUpgrade;
            case PHASE -> NMItems.phaseAnvilUpgrade;
        };
    }
    public Item salvageMaterial() {
        return switch (this) {
            case SIGNAL -> NMItems.signalAlloyPlate;
            case BLOOD -> NMItems.bloodIngot;
            case DEADZONE -> NMItems.deadzoneAlloyPlate;
            case PHASE -> NMItems.phaseSteelPlate;
        };
    }
    public static AutoAnvilUpgrade fromTier(int tier) {
        return tier > 0 && tier <= values().length ? values()[tier - 1] : null;
    }
}
