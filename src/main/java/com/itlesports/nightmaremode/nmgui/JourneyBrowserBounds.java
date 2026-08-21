package com.itlesports.nightmaremode.nmgui;

/** Immutable screen-space bounds for the embedded title-screen browser. */
public final class JourneyBrowserBounds {
    public final int x;
    public final int right;
    public final int listTop;
    public final int listBottom;

    public JourneyBrowserBounds(int x, int right, int listTop, int listBottom) {
        this.x = x;
        this.right = right;
        this.listTop = listTop;
        this.listBottom = listBottom;
    }
}
