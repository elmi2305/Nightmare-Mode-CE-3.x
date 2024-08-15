package com.itlesports.nightmaremode.mixin;

import btw.world.util.difficulty.Difficulty;
import net.minecraft.src.CallableLevelSpawnLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CallableLevelSpawnLocation.class)
public class CallableLevelSpawnLocationMixin {
    @Redirect(method = "callLevelSpawnLocation", at = @At(value = "INVOKE", target = "Lbtw/world/util/difficulty/Difficulty;isHostile()Z"))
    private boolean noWeirdWorldStuffAgain(Difficulty instance){
        return false;
    }
}
