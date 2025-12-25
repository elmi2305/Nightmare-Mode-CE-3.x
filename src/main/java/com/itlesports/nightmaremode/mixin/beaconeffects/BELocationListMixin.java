package com.itlesports.nightmaremode.mixin.beaconeffects;

import api.block.beacon.BeaconEffectLocation;
import api.block.beacon.BeaconEffectLocationList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Objects;

@Mixin(BeaconEffectLocationList.class)
public class BELocationListMixin {
    @Shadow public List effectLocations;

    @Inject(method = "getMostPowerfulBeaconEffectForLocation", at = @At("HEAD"), cancellable = true, remap = false)
    public void getMostPowerfulBeaconEffectForLocation(String effectID, int iIPos, int iKPos, CallbackInfoReturnable<Integer> cir) {
        int maxLevel = 0;

        for (Object effectLocation : this.effectLocations) {
            BeaconEffectLocation point = (BeaconEffectLocation) effectLocation;

            if (!Objects.equals(point.effectID, effectID)) continue;
            if (point.effectLevel <= maxLevel) continue;

            maxLevel = point.effectLevel;
        }

        cir.setReturnValue(maxLevel);
    }
}
