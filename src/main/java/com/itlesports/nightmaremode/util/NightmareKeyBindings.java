package com.itlesports.nightmaremode.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.KeyBinding;

@Environment(EnvType.CLIENT)
public final class NightmareKeyBindings {

    public static KeyBinding nmZoomHold;
    public static KeyBinding nmZoomToggle;

    private static boolean registered;

    private NightmareKeyBindings() {}

    public static boolean markRegistered() {
        if (!registered) {
            registered = true;
            return true;
        }
        return false;
    }

    public static KeyBinding[] all() {
        return new KeyBinding[]{ nmZoomHold, nmZoomToggle };
    }
}
