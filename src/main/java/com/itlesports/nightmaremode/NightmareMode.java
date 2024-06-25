package com.itlesports.nightmaremode;

import btw.AddonHandler;
import btw.BTWAddon;
import net.fabricmc.api.ModInitializer;

public class NightmareMode extends BTWAddon implements ModInitializer {
    private static NightmareMode instance;

//    private NightmareMode() {
//    }

    @Override
    public void initialize() {
        AddonHandler.logMessage(this.getName() + " Version " + this.getVersionString() + " Initializing...");
    }

    public static NightmareMode getInstance() {
        if (instance == null)
            instance = new NightmareMode();
        return instance;
    }

    @Override
    public void onInitialize() {
    }
}
