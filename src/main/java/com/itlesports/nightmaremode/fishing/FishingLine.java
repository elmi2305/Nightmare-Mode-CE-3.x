package com.itlesports.nightmaremode.fishing;

/** damped verlet rope with pinned ends and distance constraints. */
public final class FishingLine {
    public static final int SEGMENTS = 28;
    public final float[] x = new float[SEGMENTS + 1], y = new float[SEGMENTS + 1];
    private final float[] oldX = new float[SEGMENTS + 1], oldY = new float[SEGMENTS + 1];
    private boolean initialized;

    public void step(float endX, float endY, float time) {
        if (!initialized) {
            for (int i = 0; i <= SEGMENTS; i++) {
                float t = (float)i / SEGMENTS;
                x[i] = oldX[i] = 400 + (endX - 400) * t;
                y[i] = oldY[i] = endY * t;
            }
            initialized = true;
        }
        float length = ((float)Math.hypot(endX - 400, endY) * 1.035F + 18) / SEGMENTS;
        for (int i = 1; i < SEGMENTS; i++) {
            float vx = (x[i] - oldX[i]) * 0.92F, vy = (y[i] - oldY[i]) * 0.92F;
            oldX[i] = x[i]; oldY[i] = y[i];
            x[i] += vx + (float)Math.sin(time * 0.8 + i * 0.3) * 0.025F;
            y[i] += vy + 0.035F;
        }
        for (int pass = 0; pass < 24; pass++) {
            x[0] = 400; y[0] = 0;
            x[SEGMENTS] = endX; y[SEGMENTS] = endY;
            for (int i = 0; i < SEGMENTS; i++) {
                float dx = x[i + 1] - x[i], dy = y[i + 1] - y[i];
                float distance = Math.max(0.001F, (float)Math.hypot(dx, dy));
                float correction = (distance - length) / distance;
                float a = i == 0 ? 0 : i + 1 == SEGMENTS ? 1 : 0.5F;
                float b = i + 1 == SEGMENTS ? 0 : i == 0 ? 1 : 0.5F;
                x[i] += dx * correction * a; y[i] += dy * correction * a;
                x[i + 1] -= dx * correction * b; y[i + 1] -= dy * correction * b;
            }
        }
    }
}
