package com.itlesports.nightmaremode.mixin;

import btw.community.nightmaremode.NightmareMode;
import btw.world.util.WorldUtils;
import btw.world.util.difficulty.Difficulties;
import com.itlesports.nightmaremode.NightmareUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.WorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
    @Shadow public WorldServer[] worldServers;

    @Unique
    private int oldWorldState;

    @Inject(method = "initialWorldChunkLoad", at = @At("RETURN"))
    private void initialWorldChunkLoadMixin(CallbackInfo ci) {
        if (this.worldServers[0].worldInfo.getDifficulty() == Difficulties.HOSTILE){
            if (WorldUtils.gameProgressHasEndDimensionBeenAccessedServerOnly()) {
                NightmareMode.worldState = 3;
            } else if (WorldUtils.gameProgressHasWitherBeenSummonedServerOnly()) {
                NightmareMode.worldState = 2;
            } else if (WorldUtils.gameProgressHasNetherBeenAccessedServerOnly()) {
                NightmareMode.worldState = 1;
            } else {
                NightmareMode.worldState = 0;
            }
        } else {
            NightmareMode.worldState = 0;
        }
        if (NightmareMode.worldState == 1 || NightmareMode.worldState == 2) {
            NightmareMode.postWitherSunTicks = 999;
            NightmareMode.postNetherMoonTicks = 999;
        }
        oldWorldState = NightmareMode.worldState;
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void tick(CallbackInfo ci) {
        if (MinecraftServer.getIsServer()) {
            if (NightmareMode.worldState == 2) {
                NightmareMode.postWitherSunTicks++;
            }
            else NightmareMode.postWitherSunTicks = 0;
            if (NightmareMode.worldState == 1 || NightmareMode.worldState == 2) {
                NightmareMode.postNetherMoonTicks++;
            }
            else NightmareMode.postNetherMoonTicks = 0;
        }
        if (this.worldServers[0].getTotalWorldTime() % 20 != 0) return;
        if (this.worldServers[0].worldInfo.getDifficulty() == Difficulties.HOSTILE){
            if (WorldUtils.gameProgressHasEndDimensionBeenAccessedServerOnly()) {
                NightmareMode.worldState = 3;
            }
            else if (WorldUtils.gameProgressHasWitherBeenSummonedServerOnly()) {
                NightmareMode.worldState = 2;
            }
            else if (WorldUtils.gameProgressHasNetherBeenAccessedServerOnly()) {
                NightmareMode.worldState = 1;
            }
            else
            {
                NightmareMode.worldState = 0;
            }
        }
        else {
            NightmareMode.worldState = 0;
        }
        if (NightmareMode.worldState != oldWorldState) {
            NightmareMode.sendWorldStateToAllPlayers();
        }
        oldWorldState = NightmareMode.worldState;
    }
}
