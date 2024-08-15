package com.itlesports.nightmaremode.mixin;

import btw.world.util.difficulty.Difficulty;
import net.minecraft.src.SaveHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SaveHandler.class)
public class SaveHandlerMixin {
    @Redirect(method = "saveWorldInfoWithPlayer", at = @At(value = "INVOKE", target = "Lbtw/world/util/difficulty/Difficulty;isHostile()Z"))
    private boolean noWeirdSavingStuff(Difficulty instance){
        return false;
    }
}
