package com.itlesports.nightmaremode.mixin;

import btw.world.util.difficulty.Difficulty;
import com.itlesports.nightmaremode.NightmareUtils;
import net.minecraft.src.WorldProviderHell;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WorldProviderHell.class)
public class WorldProviderHellMixin {
    @Redirect(method = "generateLightBrightnessTable", at = @At(value = "INVOKE", target = "Lbtw/world/util/difficulty/Difficulty;isHostile()Z"))
    private boolean onlyGloomInNetherIfHardmode(Difficulty instance){
        WorldProviderHell thisObj = (WorldProviderHell)(Object)this;
        return NightmareUtils.getGameProgressMobsLevel(thisObj.worldObj) == 1;
    }
}
