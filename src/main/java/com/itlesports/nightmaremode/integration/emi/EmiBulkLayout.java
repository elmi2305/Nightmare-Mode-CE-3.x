package com.itlesports.nightmaremode.integration.emi;

public final class EmiBulkLayout {
    private EmiBulkLayout() {}

    public static int rows(int inputs) {
        return Math.max(2, (inputs + 2) / 3);
    }

    public static int baseHeight(int inputs, int outputs) {
        if (inputs <= 6) return 52;
        return Math.max(52 + (rows(inputs) - 2) * 18, ((outputs + 1) / 2) * 26 + 8);
    }

    public static int inputY(int inputs, int outputs) {
        return inputs <= 6 ? 4 : baseHeight(inputs, outputs) / 2 - 4 - rows(inputs) * 9;
    }

    public static int outputY(int inputs, int outputs) {
        return inputs <= 6 ? 0 : baseHeight(inputs, outputs) / 2 - 4 - ((outputs + 1) / 2) * 13;
    }

    public static int arrowY(int inputs, int outputs) {
        return inputs <= 6 ? 5 : baseHeight(inputs, outputs) / 2 - 12;
    }

    public static int heatY(int inputs, int outputs) {
        return inputs <= 6 ? 25 : baseHeight(inputs, outputs) / 2 + 8;
    }
}
