package btw.community.nightmaremode;

import btw.AddonHandler;
import btw.BTWAddon;

public class NightmareMode extends BTWAddon {
    public NightmareMode(){
        super();
    }
    @Override
    public void initialize() {
        AddonHandler.logMessage(this.getName() + " Version " + this.getVersionString() + " Initializing...");
    }
}
