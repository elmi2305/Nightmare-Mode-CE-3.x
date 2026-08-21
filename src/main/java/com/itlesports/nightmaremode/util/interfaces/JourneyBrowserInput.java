package com.itlesports.nightmaremode.util.interfaces;

/** Receives mouse-wheel input from the base GuiScreen event loop. */
public interface JourneyBrowserInput {
    void nightmareMode$handleJourneyBrowserWheel(int mouseX, int mouseY, int wheel);
    void nightmareMode$handleJourneyBrowserDrag(int mouseX, int mouseY, int button);
    void nightmareMode$releaseJourneyBrowserMouse(int mouseX, int mouseY, int button);
}
