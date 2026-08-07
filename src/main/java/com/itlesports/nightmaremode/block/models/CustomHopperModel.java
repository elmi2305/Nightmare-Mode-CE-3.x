package com.itlesports.nightmaremode.block.models;

import btw.block.model.BlockModel;

/** three-piece, downward-facing hopper model using a single icon for every face. */
public class CustomHopperModel extends BlockModel {
    private static final double UPPER_INNER_MIN = 0.1875D;
    private static final double UPPER_INNER_MAX = 0.8125D;
    private static final double LOWER_OUTER_MIN = 0.125D;
    private static final double LOWER_OUTER_MAX = 0.875D;
    private static final double LOWER_INNER_MIN = 0.3125D;
    private static final double LOWER_INNER_MAX = 0.6875D;

    @Override
    protected void initModel() {
        // thick upper rim and funnel walls
        this.addSquareRing(0.0D, 1.0D, UPPER_INNER_MIN, UPPER_INNER_MAX, 0.75D, 1.0D);

        // inset middle point
        this.addSquareRing(LOWER_OUTER_MIN, LOWER_OUTER_MAX,
                LOWER_INNER_MIN, LOWER_INNER_MAX, 0.375D, 0.75D);

        // bottom nozzle
        this.addBox(LOWER_INNER_MIN, 0.0D, LOWER_INNER_MIN,
                LOWER_INNER_MAX, 0.375D, LOWER_INNER_MAX);
    }

    private void addSquareRing(double outerMin, double outerMax, double innerMin, double innerMax,
                               double minY, double maxY) {
        this.addBox(outerMin, minY, outerMin, outerMax, maxY, innerMin);
        this.addBox(outerMin, minY, innerMax, outerMax, maxY, outerMax);
        this.addBox(outerMin, minY, innerMin, innerMin, maxY, innerMax);
        this.addBox(innerMax, minY, innerMin, outerMax, maxY, innerMax);
    }
}
