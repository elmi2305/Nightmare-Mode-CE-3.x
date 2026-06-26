package com.itlesports.nightmaremode.mixin.blocks;

import btw.block.blocks.WheatCropBlock;
import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.util.elements.NMEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WheatCropBlock.class)
public class WheatCropBlockMixin {
    @Inject(method = "requiresNaturalLight", at = @At("HEAD"),cancellable = true,remap = false)
    private void doesNotIfDarkNightOrHarvest(CallbackInfoReturnable<Boolean> cir){
        if(NMEvents.SimpleEvent.GREAT_HARVEST.isActive() || NightmareMode.darkStormyNightmare){
            cir.setReturnValue(false);
        }
    }
    @Inject(method = "getLightLevelForGrowth", at = @At("HEAD"),cancellable = true,remap = false)
    private void doesNotIfDarkNightOrHarvest1(CallbackInfoReturnable<Integer> cir){
        if(NMEvents.SimpleEvent.GREAT_HARVEST.isActive() || NightmareMode.darkStormyNightmare){
            cir.setReturnValue(1);
        }
    }
}
