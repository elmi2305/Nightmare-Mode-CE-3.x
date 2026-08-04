package com.itlesports.nightmaremode.block.models;

import api.util.PrimitiveQuad;
import btw.block.model.BlockModel;
import net.minecraft.src.Vec3;

/**
 * Canonical drill model points upward. The block rotates a temporary copy to
 * its six-direction facing before rendering it.
 */
public class MinerDrillModel extends BlockModel {
    public MinerDrillModel() {
        this(0.0D, 0.75D, 1.5D);
    }

    public MinerDrillModel(double bodyMin, double bodyMax, double tip) {
        this.addBox(0.0D, bodyMin, 0.0D, 1.0D, bodyMax, 1.0D);

        Vec3 tipPoint = point(0.5D, tip, 0.5D);
        this.addPrimitive(triangle(point(0.0D, bodyMax, 0.0D), tipPoint, point(1.0D, bodyMax, 0.0D)));
        this.addPrimitive(triangle(point(1.0D, bodyMax, 0.0D), tipPoint, point(1.0D, bodyMax, 1.0D)));
        this.addPrimitive(triangle(point(1.0D, bodyMax, 1.0D), tipPoint, point(0.0D, bodyMax, 1.0D)));
        this.addPrimitive(triangle(point(0.0D, bodyMax, 1.0D), tipPoint, point(0.0D, bodyMax, 0.0D)));
    }

    private static PrimitiveQuad triangle(Vec3 first, Vec3 point, Vec3 last) {
        return new PrimitiveQuad(first, point, Vec3.createVectorHelper(point), last)
                .setIconIndex(1)
                .setUVFractions(0.0F, 0.0F, 1.0F, 1.0F);
    }

    private static Vec3 point(double x, double y, double z) {
        return Vec3.createVectorHelper(x, y, z);
    }
}
