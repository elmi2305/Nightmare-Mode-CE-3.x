package com.itlesports.nightmaremode.util;

public final class NMGhastRules {
    private NMGhastRules() {}

    public static double attackRange() {
        return NMUtils.getWorldProgress() >= NMFields.HARDMODE ? 36.0D : 24.0D;
    }
}
