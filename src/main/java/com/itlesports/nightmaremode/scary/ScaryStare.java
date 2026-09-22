package com.itlesports.nightmaremode.scary;

/** frame-rate independent buildup while the eye panel is being watched. */
public final class ScaryStare {
    private long previous = -1;
    private long watchedMillis;
    private float intensity;
    private boolean watching;

    public float update(long now, boolean visible, boolean allowed) {
        if (!allowed) { reset(); return 0; }
        long delta = previous < 0 ? 0 : Math.max(0, Math.min(100, now - previous));
        previous = now;
        watching = visible;
        watchedMillis = visible ? watchedMillis + delta : 0;
        intensity = Math.max(0, Math.min(1, intensity + delta * (visible ? 1f / 2500 : -1f / 450)));
        return intensity;
    }

    public float glitch() {
        if (!watching || watchedMillis < 1200 || intensity < .35f) return 0;
        long phase = (watchedMillis - 1200) % 2300;
        return phase < 120 ? intensity : 0;
    }

    public void reset() {
        previous = -1;
        watchedMillis = 0;
        intensity = 0;
        watching = false;
    }
}
