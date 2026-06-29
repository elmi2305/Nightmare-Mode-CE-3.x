package com.itlesports.nightmaremode.mixin;


import api.world.WorldUtils;
import com.itlesports.nightmaremode.util.NMFields;
import com.itlesports.nightmaremode.util.NMUtils;
import net.minecraft.src.WorldProviderHell;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WorldProviderHell.class)
public class WorldProviderHellMixin {
    @Redirect(method = "generateLightBrightnessTable", at = @At(value = "INVOKE", target = "Ljava/lang/Boolean;booleanValue()Z", remap = false))
    private boolean onlyGloomInNetherIfHardmode(Boolean instance){
        return NMUtils.getWorldProgress() >= NMFields.HARDMODE || WorldUtils.gameProgressHasNetherBeenAccessedServerOnly();
    }
}
