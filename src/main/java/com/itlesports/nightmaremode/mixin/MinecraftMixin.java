package com.itlesports.nightmaremode.mixin;

import btw.world.util.difficulty.Difficulty;
import net.minecraft.src.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Redirect(method = "refreshResources", at = @At(value = "INVOKE", target = "Lbtw/world/util/difficulty/Difficulty;isHostile()Z"))
    private boolean allowResourcePacks(Difficulty instance){
        return false;
    }
}
