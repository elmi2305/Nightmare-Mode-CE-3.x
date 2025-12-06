package com.itlesports.nightmaremode.mixin.beaconeffects;

import btw.block.tileentity.beacon.BeaconEffect;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.PotionEffect;
import net.minecraft.src.TileEntityBeacon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BeaconEffect.class)
public class BeaconEffectMixin {
    @Inject(method = "applyPotionEffectToPlayersInRange", at = @At("HEAD"), cancellable = true)
    private void ensureFullMapPotionGranting(int effectID, int effectLevel, TileEntityBeacon beacon, CallbackInfo ci){
        if (beacon.updatedPowerState && beacon.getLevels() >= 4) {
            for(Object o : beacon.worldObj.playerEntities) {
                EntityPlayer player = (EntityPlayer)o;
                if (!player.isDead) {
                    player.addPotionEffect(new PotionEffect(effectID, 180, effectLevel, true));
                }
            }
            ci.cancel();
        }
    }
}
