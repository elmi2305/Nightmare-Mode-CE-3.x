package com.itlesports.nightmaremode.util.underworld;

public final class CrackFragment {
    public float x, y;
    public float vx, vy;
    public float rotation;
    public float rotationSpeed;
    public float size;
    public float life;
    public float maxLife;

    public CrackFragment(float x, float y, float vx, float vy, float rotation, float rotationSpeed, float size, float maxLife) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.rotation = rotation;
        this.rotationSpeed = rotationSpeed;
        this.size = size;
        this.life = maxLife;
        this.maxLife = maxLife;
    }
}
