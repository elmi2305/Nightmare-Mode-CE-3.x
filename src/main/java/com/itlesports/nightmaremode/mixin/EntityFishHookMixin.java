package com.itlesports.nightmaremode.mixin;

import com.itlesports.nightmaremode.NightmareUtils;
import net.minecraft.src.EntityFishHook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(EntityFishHook.class)
public class EntityFishHookMixin {
    @ModifyConstant(method = "checkForBite", constant = @Constant(intValue = 8))
    private int increaseBiteOdds(int constant){
        return 10;
    }
}
