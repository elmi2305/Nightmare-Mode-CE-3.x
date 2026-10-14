package com.itlesports.nightmaremode.util;

import net.minecraft.src.NBTTagCompound;

public final class MachineSpeedState {
    public static final String NBT_KEY = "NmProcessingSpeedBonus";
    private float bonus;

    public float getBonus() {
        return this.bonus;
    }

    public boolean raiseBonus(float candidate) {
        if (!Float.isNaN(candidate) && !Float.isInfinite(candidate) && candidate > this.bonus) {
            this.bonus = candidate;
            return true;
        }
        return false;
    }

    public void read(NBTTagCompound tag) {
        float saved = tag.getFloat(NBT_KEY);
        this.bonus = Float.isNaN(saved) || Float.isInfinite(saved) ? 0.0F : Math.max(0.0F, saved);
    }

    public void write(NBTTagCompound tag) {
        tag.setFloat(NBT_KEY, this.bonus);
    }
}
