package com.itlesports.nightmaremode.util.interfaces;

/** Lets child menu screens reuse the title screen's already-initialized themed panorama. */
public interface JourneyMenuBackdrop {
    void nightmareMode$drawJourneyBackdrop(int mouseX, int mouseY, float partialTicks, int width, int height);
}
