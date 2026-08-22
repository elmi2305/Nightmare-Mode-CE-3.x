package com.itlesports.nightmaremode.mixin.blocks;

import btw.block.blocks.SaplingBlock;
import btw.community.nightmaremode.NightmareMode;
import net.minecraft.src.WorldInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SaplingBlock.class)
public class SaplingBlockMixin {
    @Redirect(method = "attemptToGrow", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/WorldInfo;getWorldTime()J"))
    private long useActiveGrowthClock(WorldInfo worldInfo) {
        if (NightmareMode.realTime) {
            return worldInfo.getWorldTotalTime();
        }
        return worldInfo.getWorldTime();
    }
}
