package com.itlesports.nightmaremode.tradetweaks;

public enum ApplyAction {
    KEEP,     // mutate builder in-place (or do nothing); let original method proceed
    DROP,     // cancel original method (don't add trade)
    REPLACED  // we added a replacement trade ourselves; cancel original method to avoid duplicate
}
