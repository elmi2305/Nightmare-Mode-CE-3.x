package com.itlesports.nightmaremode.util.interfaces;

public interface PhaseTransitEntity {
    int nightmareMode$getPhaseOrigin();
    void nightmareMode$setPhaseOrigin(int origin);
    boolean nm$mustLeavePhasePortal();
    void nm$setMustLeavePhasePortal(boolean mustLeave);
}
