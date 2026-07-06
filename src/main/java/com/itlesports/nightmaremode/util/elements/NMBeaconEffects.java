package com.itlesports.nightmaremode.util.elements;

import api.block.beacon.AmbientBeaconEffect;
import api.block.beacon.BeaconEffectHandler;
import api.block.beacon.PotionBeaconEffect;
import com.itlesports.nightmaremode.block.NMBlocks;
import net.minecraft.src.Potion;

public abstract class NMBeaconEffects {
    public static final AmbientBeaconEffect STRENGTH_EFFECT = new PotionBeaconEffect("Strength", Potion.damageBoost.getId(), true);
    public static final AmbientBeaconEffect SPEED_EFFECT = new PotionBeaconEffect("Speed", Potion.moveSpeed.getId(), true);

    public static void initializeEffectsByBlockID() {
        BeaconEffectHandler.addBeaconEffect(NMBlocks.blockBloodIngot.blockID, STRENGTH_EFFECT);
        BeaconEffectHandler.addBeaconEffect(NMBlocks.blockRefinedDiamondIngot.blockID, SPEED_EFFECT);

    }
}
