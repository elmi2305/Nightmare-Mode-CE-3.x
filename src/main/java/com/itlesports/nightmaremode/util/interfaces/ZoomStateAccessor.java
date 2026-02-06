package com.itlesports.nightmaremode.util.interfaces;

/**
 * Original interface kept unchanged to satisfy existing mixins (e.g. MinecraftMixin).
 */
public interface ZoomStateAccessor {
    boolean nightmareMode$isToggleZoomActive();
    boolean nightmareMode$isToggleZoomKeyHeld();
}