package com.itlesports.nightmaremode.mixin;

import btw.BTWMod;
import btw.world.util.difficulty.Difficulties;
import btw.world.util.difficulty.Difficulty;
import com.itlesports.nightmaremode.item.NMItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BTWMod.class)
public class BTWModMixin {

    @Inject(method = "initializeDifficultyCommon", at = @At("HEAD"),remap = false)
    private void increaseBloodPickaxeSpeed(Difficulty difficulty, CallbackInfo ci){
        if(difficulty == Difficulties.HOSTILE){
            float multiplier = 1.5f;
            NMItems.bloodPickaxe.addCustomEfficiencyMultiplier(multiplier);
            NMItems.bloodAxe.addCustomEfficiencyMultiplier(multiplier);
            NMItems.bloodHoe.addCustomEfficiencyMultiplier(multiplier);
            NMItems.bloodShovel.addCustomEfficiencyMultiplier(multiplier);
        }
    }
}
