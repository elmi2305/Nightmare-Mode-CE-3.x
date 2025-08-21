package com.itlesports.nightmaremode.client;

import net.minecraft.src.KeyBinding;

/**
 * Holds custom Nightmare Mode key bindings.
 * Appended once to GameSettings via GameSettingsMixin.
 */
public final class NightmareKeyBindings {

    public static KeyBinding nightmareZoom;

    private static boolean registered;

    private NightmareKeyBindings() {}

    /**
     * Returns true only the first time this is called (used to guard array expansion).
     */
    public static boolean markRegistered() {
        if (!registered) {
            registered = true;
            return true;
        }
        return false;
    }

    /**
     * All custom bindings to append.
     */
    public static KeyBinding[] all() {
        return new KeyBinding[]{ nightmareZoom };
    }
}