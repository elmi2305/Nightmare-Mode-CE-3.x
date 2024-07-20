package com.itlesports.nightmaremode.mixin;

import btw.world.util.WorldUtils;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// SETS THE TIME TO NIGHT UPON WORLD CREATION

@Mixin(WorldInfo.class)
public abstract class WorldInfoMixin {
    @Shadow
    private long worldTime;

    private long totalTime;
    @Inject(method = "getWorldTime()J", at = @At("HEAD"))
    private void nightSetter(CallbackInfoReturnable<Long> cir) {
        if (this.totalTime == 0L) {
            worldTime = 18000L;
        }
    }
}
