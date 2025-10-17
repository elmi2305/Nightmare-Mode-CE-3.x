package com.itlesports.nightmaremode.network;

public interface IHorseTamingClient {
    void nm$setRequiredDirection(byte ordinal);
    byte nm$getRequiredDirection();
    int nm$getTamingProgress();
    void nm$setTamingProgress(int progress);
}
