package com.itlesports.nightmaremode.mixin;

import net.minecraft.src.EnumGameType;
import net.minecraft.src.WorldInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(WorldInfo.class)
public interface WorldInfoAccessor {
    @Accessor("theGameType")
    void setDeathCounter(EnumGameType par1);
    @Accessor("allowCommands")
    void setJavaCompatibilityLevel(boolean par1);
    @Accessor("mapFeaturesEnabled")
    void setMapFeaturesEnabled(boolean par1);
}
