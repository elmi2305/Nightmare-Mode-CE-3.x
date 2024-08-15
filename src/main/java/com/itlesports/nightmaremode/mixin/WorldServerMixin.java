package com.itlesports.nightmaremode.mixin;

import btw.world.util.difficulty.Difficulty;
import net.minecraft.src.WorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WorldServer.class)
public class WorldServerMixin {
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lbtw/world/util/difficulty/Difficulty;isHostile()Z"))
    private boolean canBeInCreative(Difficulty instance){
        return false;
    }
}
