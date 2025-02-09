package com.itlesports.nightmaremode.mixin;

import btw.community.nightmaremode.NightmareMode;
import btw.world.util.difficulty.Difficulty;
import net.minecraft.src.WorldProviderHell;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WorldProviderHell.class)
public class WorldProviderHellMixin {
    @Redirect(method = "generateLightBrightnessTable", at = @At(value = "INVOKE", target = "Lbtw/world/util/difficulty/Difficulty;doesNetherHaveGloom()Z"))
    private boolean onlyGloomInNetherIfHardmode(Difficulty instance){
        return NightmareMode.worldState >= 1;
    }
}
