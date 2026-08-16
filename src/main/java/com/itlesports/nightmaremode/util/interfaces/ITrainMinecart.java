package com.itlesports.nightmaremode.util.interfaces;

import java.util.UUID;

/**
 * Persistent coupling data for the carts that make up a player-driven train.
 */
public interface ITrainMinecart {
    UUID nightmareMode$getTrainEngineId();

    UUID nightmareMode$getTrainPreviousCartId();

    void nightmareMode$joinTrain(UUID engineId, UUID previousCartId);

    void nightmareMode$leaveTrain();

    int nightmareMode$getTrainLength();
}
