package com.itlesports.nightmaremode.scary;

import java.util.Random;

/** a single budget of active play time for all natural events. no saved state. */
public final class ScaryClock {
    private final Random random;
    private long remaining;
    private long quietRemaining;

    public ScaryClock(Random random) {
        this.random = random;
        afterEvent();
    }

    public void afterEvent() {
        remaining = (120L + random.nextInt(181)) * 60_000L;
    }

    public void disturb() { quietRemaining = 120_000L; }

    public boolean advance(long elapsed, boolean playing, boolean quiet, boolean active) {
        if (!quiet) disturb();
        if (!playing) return false;
        long delta = Math.max(0L, Math.min(250L, elapsed));
        quietRemaining = Math.max(0L, quietRemaining - delta);
        if (!active) remaining = Math.max(0L, remaining - delta);
        return !active && quiet && quietRemaining == 0L && remaining == 0L;
    }
}
