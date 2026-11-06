package com.itlesports.nightmaremode.util;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Material;

public final class NMOxygenRules {
    private NMOxygenRules() {}

    public static boolean isDeepCave(EntityPlayer player) {
        return isVulnerable(player) && (player.dimension == 1 || player.boundingBox.minY < 54.0D)
                && !player.isInsideOfMaterial(Material.water);
    }

    public static boolean isHighAltitude(EntityPlayer player) {
        return isVulnerable(player) && player.dimension == 0 && player.boundingBox.minY > 120.0D
                && !player.isInsideOfMaterial(Material.water);
    }

    private static boolean isVulnerable(EntityPlayer player) {
        return player != null && (player.dimension == 0 || player.dimension == 1) && player.isEntityAlive()
                && !player.capabilities.disableDamage;
    }
}
