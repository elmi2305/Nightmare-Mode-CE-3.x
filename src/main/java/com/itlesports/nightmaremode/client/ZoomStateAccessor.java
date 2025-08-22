package com.itlesports.nightmaremode.client;

/**
 * Original interface kept unchanged to satisfy existing mixins (e.g. MinecraftMixin).
 */
public interface ZoomStateAccessor {
    boolean nightmareMode$isToggleZoomActive();
    boolean nightmareMode$isToggleZoomKeyHeld();
}