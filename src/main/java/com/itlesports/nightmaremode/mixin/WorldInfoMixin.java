package com.itlesports.nightmaremode.mixin;

import net.minecraft.src.WorldInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// SETS THE TIME TO NIGHT UPON WORLD CREATION

@Mixin(WorldInfo.class)
public class WorldInfoMixin {
    @Unique
    public boolean HasPlayerKilledDragon = false;
    @Unique
    public boolean getHasPlayerKilledDragon(){
        return HasPlayerKilledDragon;
    }
    @Unique
    public void setHasPlayerKilledDragon(boolean k){
        this.HasPlayerKilledDragon = k;
    }

    @Shadow
    private long worldTime;
    @Unique
    private long totalTime;

    @Inject(method = "getWorldTime()J", at = @At("HEAD"))
    private void nightSetter(CallbackInfoReturnable<Long> cir) {
        if (this.totalTime == 0L) {
            worldTime = 18000L;
        }
    }

}
