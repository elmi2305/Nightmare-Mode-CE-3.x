package com.itlesports.nightmaremode.mixin.blocks;

import btw.block.blocks.PlantsBlock;
import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.util.elements.NMEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(PlantsBlock.class)
public class PlantsBlockMixin {
    @ModifyConstant(method = "isInBrightLight", constant = @Constant(intValue = 15))
    private int lowerLightRequirementToGrow(int constant){
        if(NMEvents.SimpleEvent.GREAT_HARVEST.isActive() || NightmareMode.darkStormyNightmare){
            return 1;
        }
        return constant - 4;
    }
}
