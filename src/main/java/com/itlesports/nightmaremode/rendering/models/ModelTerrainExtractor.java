package com.itlesports.nightmaremode.rendering.models;

import net.minecraft.src.Tessellator;

public class ModelTerrainExtractor {
    private static final Box BASE = new Box(0, 0, 0, 16, 10, 16);
    private static final Box TOP = new Box(0, 10, 0, 16, 12, 16);
    private static final Box[] BODY = {BASE, TOP};
    private static final Box[] STUBS = {
            new Box(0, 12, 0, 2, 16, 2),
            new Box(14, 12, 0, 16, 16, 2),
            new Box(0, 12, 14, 2, 16, 16),
            new Box(14, 12, 14, 16, 16, 16)
    };

    public void renderSides(int brightness) {
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.setBrightness(brightness);
        for (Box box : BODY) {
            wrappedSideFaces(tessellator, box);
        }
        tessellator.draw();
    }

    public void renderTop(int brightness) {
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.setBrightness(brightness);
        face(tessellator, TOP.minX, TOP.maxY, TOP.minZ, 0.0D, 0.0D,
                TOP.maxX, TOP.maxY, TOP.minZ, 1.0D, 0.0D,
                TOP.maxX, TOP.maxY, TOP.maxZ, 1.0D, 1.0D,
                TOP.minX, TOP.maxY, TOP.maxZ, 0.0D, 1.0D);
        tessellator.draw();
    }

    public void renderBottom(int brightness) {
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.setBrightness(brightness);
        face(tessellator, BASE.minX, BASE.minY, BASE.maxZ, 0.0D, 1.0D,
                BASE.maxX, BASE.minY, BASE.maxZ, 1.0D, 1.0D,
                BASE.maxX, BASE.minY, BASE.minZ, 1.0D, 0.0D,
                BASE.minX, BASE.minY, BASE.minZ, 0.0D, 0.0D);
        tessellator.draw();
    }

    public void renderStubs(int brightness) {
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.setBrightness(brightness);
        for (Box stub : STUBS) {
            individualBoxFaces(tessellator, stub);
        }
        tessellator.draw();
    }

    private void wrappedSideFaces(Tessellator tessellator, Box box) {
        double minX = box.minX / 16.0D;
        double maxX = box.maxX / 16.0D;
        double minZ = box.minZ / 16.0D;
        double maxZ = box.maxZ / 16.0D;
        double minV = 1.0D - box.minY / 16.0D;
        double maxV = 1.0D - box.maxY / 16.0D;

        face(tessellator, box.minX, box.minY, box.minZ, minX, minV,
                box.maxX, box.minY, box.minZ, maxX, minV,
                box.maxX, box.maxY, box.minZ, maxX, maxV,
                box.minX, box.maxY, box.minZ, minX, maxV);
        face(tessellator, box.maxX, box.minY, box.maxZ, minX, minV,
                box.minX, box.minY, box.maxZ, maxX, minV,
                box.minX, box.maxY, box.maxZ, maxX, maxV,
                box.maxX, box.maxY, box.maxZ, minX, maxV);
        face(tessellator, box.minX, box.minY, box.maxZ, minZ, minV,
                box.minX, box.minY, box.minZ, maxZ, minV,
                box.minX, box.maxY, box.minZ, maxZ, maxV,
                box.minX, box.maxY, box.maxZ, minZ, maxV);
        face(tessellator, box.maxX, box.minY, box.minZ, minZ, minV,
                box.maxX, box.minY, box.maxZ, maxZ, minV,
                box.maxX, box.maxY, box.maxZ, maxZ, maxV,
                box.maxX, box.maxY, box.minZ, minZ, maxV);
    }

    private void individualBoxFaces(Tessellator tessellator, Box box) {
        face(tessellator, box.minX, box.minY, box.minZ, 0.0D, 1.0D,
                box.maxX, box.minY, box.minZ, 1.0D, 1.0D,
                box.maxX, box.maxY, box.minZ, 1.0D, 0.0D,
                box.minX, box.maxY, box.minZ, 0.0D, 0.0D);
        face(tessellator, box.maxX, box.minY, box.maxZ, 0.0D, 1.0D,
                box.minX, box.minY, box.maxZ, 1.0D, 1.0D,
                box.minX, box.maxY, box.maxZ, 1.0D, 0.0D,
                box.maxX, box.maxY, box.maxZ, 0.0D, 0.0D);
        face(tessellator, box.minX, box.minY, box.maxZ, 0.0D, 1.0D,
                box.minX, box.minY, box.minZ, 1.0D, 1.0D,
                box.minX, box.maxY, box.minZ, 1.0D, 0.0D,
                box.minX, box.maxY, box.maxZ, 0.0D, 0.0D);
        face(tessellator, box.maxX, box.minY, box.minZ, 0.0D, 1.0D,
                box.maxX, box.minY, box.maxZ, 1.0D, 1.0D,
                box.maxX, box.maxY, box.maxZ, 1.0D, 0.0D,
                box.maxX, box.maxY, box.minZ, 0.0D, 0.0D);
        face(tessellator, box.minX, box.maxY, box.minZ, 0.0D, 0.0D,
                box.maxX, box.maxY, box.minZ, 1.0D, 0.0D,
                box.maxX, box.maxY, box.maxZ, 1.0D, 1.0D,
                box.minX, box.maxY, box.maxZ, 0.0D, 1.0D);
    }

    private void face(Tessellator tessellator, float x1, float y1, float z1, double u1, double v1,
                      float x2, float y2, float z2, double u2, double v2,
                      float x3, float y3, float z3, double u3, double v3,
                      float x4, float y4, float z4, double u4, double v4) {
        tessellator.addVertexWithUV(x1 / 16.0F, y1 / 16.0F, z1 / 16.0F, u1, v1);
        tessellator.addVertexWithUV(x2 / 16.0F, y2 / 16.0F, z2 / 16.0F, u2, v2);
        tessellator.addVertexWithUV(x3 / 16.0F, y3 / 16.0F, z3 / 16.0F, u3, v3);
        tessellator.addVertexWithUV(x4 / 16.0F, y4 / 16.0F, z4 / 16.0F, u4, v4);
    }

    private static final class Box {
        private final float minX;
        private final float minY;
        private final float minZ;
        private final float maxX;
        private final float maxY;
        private final float maxZ;

        private Box(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
            this.minX = minX;
            this.minY = minY;
            this.minZ = minZ;
            this.maxX = maxX;
            this.maxY = maxY;
            this.maxZ = maxZ;
        }
    }
}
