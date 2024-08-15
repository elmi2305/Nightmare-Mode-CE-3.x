package com.itlesports.nightmaremode.mixin;

import btw.world.util.difficulty.Difficulty;
import net.minecraft.src.NetServerHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(NetServerHandler.class)
public class NetServerHandlerMixin {
    @Redirect(method = "handleSlashCommand", at = @At(value = "INVOKE", target = "Lbtw/world/util/difficulty/Difficulty;isHostile()Z"))
    private boolean allowCheatingInHostile(Difficulty instance){
        return false;
    }
}
