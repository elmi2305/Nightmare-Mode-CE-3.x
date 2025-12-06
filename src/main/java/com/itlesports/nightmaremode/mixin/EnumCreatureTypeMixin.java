package com.itlesports.nightmaremode.mixin;

import btw.community.nightmaremode.NightmareMode;
import net.minecraft.src.EnumCreatureType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnumCreatureType.class)
public class EnumCreatureTypeMixin {
    @Mutable
    @Shadow @Final private int maxNumberOfCreature;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void changeMobCap(CallbackInfo ci){
        if(this.maxNumberOfCreature == 90){
            this.maxNumberOfCreature = (NightmareMode.worldState == 0 ? 100 : (NightmareMode.worldState == 1 ? 110 : (NightmareMode.worldState == 2 ? 120 : 130)));
                                                        // it used to be 105                 ->                 115                                -> 130-> 140
        }
    }
}
