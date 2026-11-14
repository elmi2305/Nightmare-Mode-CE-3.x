package com.itlesports.nightmaremode.fishing;

/** continuous articulated curves shared by the renderer and animation checks. */
public final class FishingKrakenPose {
    public static final int LIMBS = 10, POINTS = 49;
    public final float[][] x = new float[LIMBS][POINTS], y = new float[LIMBS][POINTS], width = new float[LIMBS][POINTS];
    public float angle, scale, contraction, bend, finStroke, jet, coil;

    public void update(float time, float heading, float fright, float fightTicks) {
        boolean fighting = fightTicks >= 0;
        float blend = fighting ? FishingStruggle.smooth(fightTicks / 12) : 0;
        float idleCoil = 0.25F + (float)Math.sin(time * 0.8) * 0.13F;
        coil = idleCoil + (FishingStruggle.coil(fightTicks) - idleCoil) * blend;
        float breath = 0.5F + 0.5F * (float)Math.sin(time * 2.2);
        float idleJet = (float)Math.pow(breath, 5);
        jet = idleJet + (FishingStruggle.surge(fightTicks) - idleJet) * blend;
        contraction = 1.05F - jet * 0.24F + coil * blend * 0.08F;
        bend = (float)Math.sin(time * 1.1) * 7 + (float)Math.sin(time * 2.2 - 0.8) * jet * 8;
        finStroke = (float)Math.sin(time * 2.2 - 0.6) * (9 + jet * 8);
        // squid jet mantle-first; the trailing arm crown points away from travel.
        angle = heading + (float)Math.PI + (float)Math.sin(time * 0.8) * 0.045F;
        float c = (float)Math.cos(angle), s = (float)Math.sin(angle);
        float maxY = 0;
        for (int arm = 0; arm < LIMBS; arm++) {
            boolean feeding = arm >= 8;
            float lane = feeding ? (arm == 8 ? -1 : 1) : (arm - 3.5F) / 3.5F;
            float length = feeding ? (arm == 8 ? 264 : 279) : 108 + arm * 6;
            float step = length / (POINTS - 1) * (1 - jet * 0.05F);
            x[arm][0] = -5; y[arm][0] = lane * 6;
            for (int i = 0; i < POINTS; i++) {
                float t = i / (POINTS - 1F);
                if (i > 0) {
                    float wave = (float)Math.sin(time * 2.2 - t * 6.5 + arm * 0.85);
                    float curl = coil * lane * (feeding ? 2.4F : 4.2F) * t * t;
                    float distal = FishingStruggle.smooth((t - 0.58F) / 0.42F);
                    float theta = lane * (feeding ? 0.13F : 0.25F) + curl
                            + wave * (0.10F + t * (feeding ? 0.32F : 0.65F))
                            + (float)Math.sin(time * 1.3 + arm) * distal * 0.48F
                            + lane * jet * 0.35F * t;
                    x[arm][i] = x[arm][i - 1] + (float)Math.cos(theta) * step;
                    y[arm][i] = y[arm][i - 1] + (float)Math.sin(theta) * step;
                }
                width[arm][i] = (feeding ? 2.5F : 5.3F) * (float)Math.pow(1 - t, feeding ? 0.8 : 1.2) + 0.4F;
                if (feeding && t > 0.79F) width[arm][i] += (float)Math.sin((t - 0.79F) / 0.21F * Math.PI) * 3.2F;
                maxY = Math.max(maxY, Math.abs(x[arm][i] * s + y[arm][i] * c) + width[arm][i]);
            }
        }
        // include the rear mantle and fins in the visibility envelope during turns.
        maxY = Math.max(maxY, Math.abs(-222 * s) + (50 + Math.abs(finStroke) + Math.abs(bend)) * Math.abs(c));
        scale = Math.min(0.9F, 198 / Math.max(1, maxY));
    }

    public float mantleY(float x, float y) {
        float t = FishingGame.clamp((-x - 48) / 174, 0, 1);
        return y * contraction + bend * t * t;
    }
}
