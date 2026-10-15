package com.itlesports.nightmaremode.util;

import net.minecraft.src.ItemStack;

public final class HammerAnvilAnimation {
    public final ItemStack input;
    public final ItemStack hammer;
    public final int swings;
    public final int ticksPerSwing;
    private int age;

    public HammerAnvilAnimation(ItemStack input, ItemStack hammer, int hits) {
        this.input = input.copy();
        this.hammer = hammer.copy();
        this.swings = Math.min(6, Math.max(1, hits));
        this.ticksPerSwing = Math.max(5, 13 - Math.min(8, hits));
    }

    public boolean tick() {
        return ++this.age >= this.swings * this.ticksPerSwing;
    }

    public float getSwingProgress(float partialTicks) {
        return ((this.age + partialTicks) / this.ticksPerSwing) % 1.0F;
    }
}
