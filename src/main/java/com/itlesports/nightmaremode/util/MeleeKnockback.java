package com.itlesports.nightmaremode.util;

import com.itlesports.nightmaremode.util.interfaces.NoMeleeKnockback;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;

public final class MeleeKnockback {
    private MeleeKnockback() {
    }

    public static boolean isPreventedBy(Entity attacker) {
        if (!(attacker instanceof EntityPlayer player)) {
            return false;
        }
        ItemStack held = player.getHeldItem();
        return held != null && held.getItem() instanceof NoMeleeKnockback;
    }

    public static float getScale(float damage) {
        return Math.max(0.1F, Math.min(1.0F, damage / 10.0F));
    }
}
