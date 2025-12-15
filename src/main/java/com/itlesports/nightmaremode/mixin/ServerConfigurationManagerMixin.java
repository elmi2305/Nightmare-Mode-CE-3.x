package com.itlesports.nightmaremode.mixin;

import btw.community.nightmaremode.NightmareMode;
import net.minecraft.src.Entity;
import net.minecraft.src.ServerConfigurationManager;
import net.minecraft.src.Teleporter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerConfigurationManager.class)
public class ServerConfigurationManagerMixin {

    @Redirect(method = "transferEntityToWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/Teleporter;placeInPortal(Lnet/minecraft/src/Entity;DDDF)V"))
    private void doNotGenerateNetherPortalForUnderworld(Teleporter instance, Entity d, double e, double f, double g, float v){
        if(d.dimension == NightmareMode.UNDERWORLD_DIMENSION) return;

        instance.placeInPortal(d,e,f,g,v);
    }
}
