package com.itlesports.nightmaremode.mixin;

import btw.community.nightmaremode.NightmareMode;
import btw.world.util.WorldUtils;
import btw.world.util.difficulty.Difficulties;
import com.itlesports.nightmaremode.NMUtils;
import com.itlesports.nightmaremode.TeleportScheduler;
import com.itlesports.nightmaremode.network.HandshakeServer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
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
        if (!NightmareMode.getInstance().shouldStackSizesIncrease) {
            NMUtils.setItemStackSizes(16);
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

        NightmareMode.isEclipse = false;
        NightmareMode.isBloodMoon = false;
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void nmOnServerTick(CallbackInfo ci) {
        TeleportScheduler.onServerTick();
        HandshakeServer.onServerTick();

        if (this.worldServers[0].getTotalWorldTime() % 30 != 0) return;
        
        int dayCount = (int) Math.ceil((double) this.worldServers[0].getWorldTime() / 24000) + this.isDawnOrDusk(this.worldServers[0].getWorldTime());
        if (!NightmareMode.bloodmare) {
            NightmareMode.setBloodmoon(this.getIsBloodMoon(this.worldServers[0], dayCount));
        } else {
            NightmareMode.setBloodmoon(this.getIsNightFromWorldTime(this.worldServers[0]));
        }
        if (!NightmareMode.totalEclipse) {
            NightmareMode.setEclipse(this.getIsEclipse(this.worldServers[0], dayCount));
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
        NightmareMode.sendWorldStateToAllPlayers();
        oldWorldState = NightmareMode.worldState;
        oldBloodMoon = NightmareMode.isBloodMoon;
        oldEclipse = NightmareMode.isEclipse;
    }
//    @Inject(method = "worldServerForDimension", at = @At("HEAD"),cancellable = true)
//    private void giveWorldServerForUnderworld(int par1, CallbackInfoReturnable<WorldServer> cir){
//
//        if(par1 == NightmareMode.UNDERWORLD_DIMENSION){
//            cir.setReturnValue(this.worldServers[3]);
//        }
//    }

// // TODO: DIMENSION STUFF
//    @ModifyConstant(method = "loadAllWorlds", constant = @Constant(intValue = 3))
//    private int attemptToIncreaseWorldServerSize(int constant){
//        return 4;
//    }
//    @Inject(method = "loadAllWorlds", at = @At(value = "FIELD", target = "Lnet/minecraft/server/MinecraftServer;difficultyLevel:Lbtw/world/util/difficulty/Difficulty;",ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD)
//    private void loadNightmareWorlds(String par1Str, String par2Str, long par3, WorldType par5WorldType, String par6Str, CallbackInfo ci, ISaveHandler var7, WorldInfo var9){
//        MinecraftServer server = (MinecraftServer) (Object)this;
//        WorldSettings var8 = new WorldSettings(var9);
//        this.worldServers[3] = new WorldServer(server, var7, par2Str, NightmareMode.UNDERWORLD_DIMENSION, var8, this.theProfiler, this.getLogAgent());
//
//        this.worldServers[3].addWorldAccess(new WorldManager(server, this.worldServers[3]));
//        if (!this.isSinglePlayer()) {
//            this.worldServers[3].getWorldInfo().setGameType(this.getGameType());
//        }
//    }



    @Unique
    private int isDawnOrDusk(long time){
        if(time % 24000 >= 23459) {
            return 1;
        }
        return 0;
    }

    @Unique private boolean getIsBloodMoon(World world, int dayCount){
        if(NMUtils.getWorldProgress() == 0){return false;}
        return this.getIsNightFromWorldTime(world) && (world.getMoonPhase() == 0  && (dayCount % 16 == 9)) || NightmareMode.bloodmare;
    }
    @Unique private boolean getIsNightFromWorldTime(World world){
        return world.getWorldTime() % 24000 >= 12541 && world.getWorldTime() % 24000 <= 23459;
    }

    @Unique private boolean getIsEclipse(World world, int dayCount){
        if(NMUtils.getWorldProgress() <= 2){return false;}
        return !this.getIsNightFromWorldTime(world) && dayCount % 2 == 0;
    }
}
