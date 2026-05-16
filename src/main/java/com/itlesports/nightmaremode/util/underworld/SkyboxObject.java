package com.itlesports.nightmaremode.util.underworld;

public class SkyboxObject {
    public float yaw;
    public float pitch;
    public long lastRenderMs = System.currentTimeMillis();

    public float openness = 1.0F; // 1=open, 0=closed

    public SkyboxObject(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public enum EyeState { CLOSED, OPENING, OPEN, CLOSING }

    public EyeState state = EyeState.CLOSED;
    public long stateTimer = System.currentTimeMillis();

    // randomize so eyes don't all blink in sync
    public long nextOpenDelay = (long)(4000 + Math.random() * 4000); // ms closed before opening
    public long openDuration  = (long)(1200  + Math.random() * 800);  // ms to stay open
}
