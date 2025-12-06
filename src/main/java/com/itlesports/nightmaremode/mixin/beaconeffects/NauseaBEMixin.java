package com.itlesports.nightmaremode.mixin.beaconeffects;

import btw.block.tileentity.beacon.NauseaBeaconEffect;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Potion;
import net.minecraft.src.PotionEffect;
import net.minecraft.src.TileEntityBeacon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NauseaBeaconEffect.class)
public class NauseaBEMixin {
    @Inject(method = "applyDungCloudToPlayersInRange", at = @At("HEAD"), cancellable = true)
    private void allowFullMapRange(TileEntityBeacon beacon, CallbackInfo ci){
        for(Object player : beacon.worldObj.playerEntities) {
            if(player instanceof EntityPlayer p && beacon.getLevels() >= 4) {
                if (!p.isDead && !p.capabilities.isCreativeMode && !p.isWearingFullSuitSoulforgedArmor()) {
                    p.addPotionEffect(new PotionEffect(Potion.confusion.getId(), 180, 0, true));
                    int amplifier = 0;
                    if ((beacon.worldObj.getTotalWorldTime() + beacon.getUpdateOffset()) % (long) (Potion.poison.getTickEveryBase() >> amplifier) == 0L) {
                        p.addPotionEffect(new PotionEffect(Potion.poison.getId(), 180, amplifier, true));
                    }
                }
                ci.cancel();
            }
        }
    }
}
