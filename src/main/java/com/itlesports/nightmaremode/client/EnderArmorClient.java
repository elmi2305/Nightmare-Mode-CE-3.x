package com.itlesports.nightmaremode.client;

import com.itlesports.nightmaremode.util.EnderArmorNet;
import net.minecraft.src.Minecraft;

public final class EnderArmorClient {
    private EnderArmorClient() {}

    public static boolean consumeEmptyHandUse(Minecraft minecraft) {
        if (minecraft.thePlayer == null || minecraft.currentScreen != null
                || minecraft.thePlayer.getHeldItem() != null
                || !minecraft.thePlayer.isUsingSpecialKey()
                || !EnderArmorNet.hasFullSet(minecraft.thePlayer)) return false;
        EnderArmorNet.sendUse();
        return true;
    }
}
