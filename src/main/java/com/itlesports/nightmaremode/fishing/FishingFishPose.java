package com.itlesports.nightmaremode.fishing;

/** source orientation is resolved before the rear-body weights are applied. */
public final class FishingFishPose {
    public float x, y;

    public void vertex(FishingGame.Spec spec, int u, int v, float time, float fear, int id) {
        x = (u - v) * 0.70710678F * (spec.reverse ? -1 : 1);
        y = (u + v - 16) * 0.70710678F;
        // keep the entire forward half rigid, including eyes, mouth and lure stalks.
        float tail = FishingGame.clamp((-x - 1) / 9, 0, 1);
        y += (float)Math.sin(time * (5 + spec.flutter * 12 + fear * 13) - tail * 3 + id)
                * tail * tail * (2.2F + spec.flutter * 4 + fear * 3);
        float finBand = FishingGame.clamp(1 - Math.abs(x + 1) / 4, 0, 1);
        float fin = Math.max(0, Math.abs(y) - 2.5F) * finBand;
        x += (float)Math.sin(time * 7 + id) * fin * 0.12F;
    }
}
