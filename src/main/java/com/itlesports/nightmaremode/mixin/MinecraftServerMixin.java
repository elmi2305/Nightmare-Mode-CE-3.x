package com.itlesports.nightmaremode.mixin;

import btw.community.nightmaremode.NightmareMode;
import btw.world.util.WorldUtils;
import btw.world.util.difficulty.Difficulties;
import com.itlesports.nightmaremode.NightmareUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.World;
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
    @Unique
    private boolean oldBloodMoon;
    @Unique
    private boolean oldEclipse;

    @Inject(method = "initialWorldChunkLoad", at = @At("RETURN"))
    private void initialWorldChunkLoadMixin(CallbackInfo ci) {
        NightmareMode.getInstance().portalTime = this.worldServers[0].worldInfo.getData(NightmareMode.PORTAL_TIME);
        NightmareMode.getInstance().shouldStackSizesIncrease = this.worldServers[0].worldInfo.getData(NightmareMode.STACK_SIZE_INCREASE);
        if (!NightmareMode.getInstance().shouldStackSizesIncrease) {
            NightmareUtils.setItemStackSizes(16);
        }
        if(this.worldServers[0].getWorldTime() + 72000 < NightmareMode.getInstance().portalTime){
            NightmareMode.getInstance().portalTime = 0;
        }

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
        oldWorldState = NightmareMode.worldState;
        oldBloodMoon = NightmareMode.isBloodMoon;
        oldEclipse = NightmareMode.isEclipse;
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void tick(CallbackInfo ci) {
        if (this.worldServers[0].getTotalWorldTime() % 30 != 0) return;
        
        int dayCount = (int) Math.ceil((double) this.worldServers[0].getWorldTime() / 24000) + this.isDawnOrDusk(this.worldServers[0].getWorldTime());
        if (!NightmareMode.bloodmare) {
            NightmareMode.setBloodmoon(this.getIsBloodMoon(this.worldServers[0], dayCount));
        } else {
            NightmareMode.setBloodmoon(this.getIsNightFromWorldTime(this.worldServers[0]));
        }

        if (!NightmareMode.totalEclipse) {
            NightmareMode.setEclipse(this.getIsEclipse(this.worldServers[0], dayCount));
        } else {
            NightmareMode.setEclipse(!this.getIsNightFromWorldTime(this.worldServers[0]));
        }
        if ((NightmareMode.isBloodMoon != oldBloodMoon) || (NightmareMode.isEclipse != oldEclipse)) {
            NightmareMode.sendBloodmoonEclipseToAllPlayers();
        }

        if (WorldUtils.gameProgressHasEndDimensionBeenAccessedServerOnly()) {
            NightmareMode.worldState = 3;
        } else if (WorldUtils.gameProgressHasWitherBeenSummonedServerOnly()) {
            NightmareMode.worldState = 2;
        } else if (WorldUtils.gameProgressHasNetherBeenAccessedServerOnly()) {
            NightmareMode.worldState = 1;
        } else {
            NightmareMode.worldState = 0;
        }
        if (NightmareMode.worldState != oldWorldState) {
            NightmareMode.sendWorldStateToAllPlayers();
        }
        oldWorldState = NightmareMode.worldState;
        oldBloodMoon = NightmareMode.isBloodMoon;
        oldEclipse = NightmareMode.isEclipse;
    }


    @Unique
    private int isDawnOrDusk(long time){
        if(time % 24000 >= 23459) {
            return 1;
        }
        return 0;
    }

    @Unique private boolean getIsBloodMoon(World world, int dayCount){
        if(NightmareUtils.getWorldProgress(world) == 0){return false;}
        return this.getIsNightFromWorldTime(world) && (world.getMoonPhase() == 0  && (dayCount % 16 == 9)) || NightmareMode.bloodmare;
    }
    @Unique private boolean getIsNightFromWorldTime(World world){
        return world.getWorldTime() % 24000 >= 12541 && world.getWorldTime() % 24000 <= 23459;
    }

    @Unique private boolean getIsEclipse(World world, int dayCount){
        if(NightmareUtils.getWorldProgress(world) <= 2){return false;}
        return !this.getIsNightFromWorldTime(world) && dayCount % 2 == 0;
    }
}
