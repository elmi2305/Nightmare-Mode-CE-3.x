package com.itlesports.nightmaremode.mixin;

import btw.world.util.BTWWorldData;
import btw.world.util.difficulty.Difficulty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BTWWorldData.class)
public class BTWWorldDataMixin {
    @Redirect(method = "createDefaultGlobalData", at = @At(value = "INVOKE", target = "Lbtw/world/util/difficulty/Difficulty;isHostile()Z"),remap = false)
    private boolean noWeirdWorldStuff(Difficulty instance){
        return false;
    }
}
