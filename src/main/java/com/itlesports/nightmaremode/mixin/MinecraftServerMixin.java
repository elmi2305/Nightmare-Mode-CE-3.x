package com.itlesports.nightmaremode.mixin;

import api.achievement.AchievementEventDispatcher;
import api.world.WorldUtils;
import btw.community.nightmaremode.NightmareMode;
import btw.world.BTWDifficulties;
import com.itlesports.nightmaremode.NMUtils;
import com.itlesports.nightmaremode.tpa.TeleportScheduler;
import com.itlesports.nightmaremode.achievements.NMAchievementEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
    @Mutable
    @Shadow public WorldServer[] worldServers;
    @Shadow @Final public Profiler theProfiler;

    @Shadow public abstract ILogAgent getLogAgent();

    @Shadow private ServerConfigurationManager serverConfigManager;
    @Unique private boolean oldBloodMoon;
    @Unique private boolean oldEclipse;

    @Inject(method = "initialWorldChunkLoad", at = @At("RETURN"))
    private void initialWorldChunkLoadMixin(CallbackInfo ci) {
    boolean shouldStackSizesIncrease = this.worldServers[0].worldInfo.getData(NightmareMode.DRAGON_DEFEATED);
        if (shouldStackSizesIncrease) {
            NMUtils.setItemStackSizes(32);
        } else{
            NMUtils.setItemStackSizes(16);
        }
        if (this.worldServers[0].worldInfo.getDifficulty() == BTWDifficulties.HOSTILE){
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

        oldBloodMoon = NightmareMode.isBloodMoon;
        oldEclipse = NightmareMode.isEclipse;

        NightmareMode.isEclipse = false;
        NightmareMode.isBloodMoon = false;
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void nmOnServerTick(CallbackInfo ci) {
        TeleportScheduler.onServerTick();

        if (this.worldServers[0].getTotalWorldTime() % 30 != 0) return;
        long worldTime = this.worldServers[0].getWorldTime();

        for (Object o : this.worldServers[0].playerEntities) {
            EntityPlayer player = (EntityPlayer)o;
            AchievementEventDispatcher.triggerEvent(NMAchievementEvents.TimeEvent.class, player, this.worldServers[0].getWorldTime());
            AchievementEventDispatcher.triggerEvent(NMAchievementEvents.TimeItemEvent.class, player, new NMAchievementEvents.TimeItemEvent.Context(player, worldTime));
            AchievementEventDispatcher.triggerEvent(NMAchievementEvents.MiscPlayerEvent.class, player, player);
        }
        
        int dayCount = (int) Math.ceil((double) this.worldServers[0].getWorldTime() / 24000) + this.isDawnOrDusk(this.worldServers[0].getWorldTime());
        if (!NightmareMode.bloodmare) {
            NightmareMode.setBloodmoon(this.getIsBloodMoon(this.worldServers[0], dayCount));
        } else {
            NightmareMode.setBloodmoon(this.getIsNightFromWorldTime(this.worldServers[0]));
        }
        if (!NightmareMode.totalEclipse) {
            NightmareMode.setEclipse(this.getIsEclipse(this.worldServers[0], dayCount));
        } else{
            NightmareMode.setEclipse(!this.getIsNightFromWorldTime(this.worldServers[0]));
        }
        boolean shouldChangeBloodMoon = NightmareMode.isBloodMoon != oldBloodMoon;
        boolean shouldChangeEclipse = NightmareMode.isEclipse != oldEclipse;
        if (shouldChangeBloodMoon || shouldChangeEclipse) {
            for (Object o : this.worldServers[0].playerEntities) {
                EntityPlayer player = (EntityPlayer)o;
                if (shouldChangeBloodMoon) {
                    AchievementEventDispatcher.triggerEvent(NMAchievementEvents.BloodMoonEvent.class, player, NightmareMode.isBloodMoon);
                }
                if (shouldChangeEclipse) {
                    AchievementEventDispatcher.triggerEvent(NMAchievementEvents.EclipseEvent.class, player, NightmareMode.isEclipse);
                }
            }
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
        oldBloodMoon = NightmareMode.isBloodMoon;
        oldEclipse = NightmareMode.isEclipse;
    }
    @Inject(method = "worldServerForDimension", at = @At("HEAD"),cancellable = true)
    private void giveWorldServerForUnderworld(int par1, CallbackInfoReturnable<WorldServer> cir){

        if(par1 == NightmareMode.UNDERWORLD_DIMENSION){
            cir.setReturnValue(this.worldServers[3]);
        }
    }

    @Inject(method = "loadAllWorlds", at = @At(value = "FIELD", target = "Lnet/minecraft/server/MinecraftServer;worldServers:[Lnet/minecraft/src/WorldServer;",ordinal = 0,shift = At.Shift.AFTER))
    private void setWorldServerSize(String par1Str, String par2Str, long par3, WorldType par5WorldType, String par6Str, CallbackInfo ci){
        this.worldServers = new WorldServer[4];
    }


//    @Inject(method = "loadAllWorlds", at = @At("TAIL"))
//    private void nm_addUnderworld(CallbackInfo ci) {
//        if (this.worldServers.length > 3 && this.worldServers[3] == null) {
//            MinecraftServer serv = (MinecraftServer) (Object)this;
//
//            System.out.println("[nm] Creating Underworld WorldServer for dim 2");
//
//            this.worldServers[3] = new WorldServer(
//                    (MinecraftServer)(Object)this,
//                    (ISaveHandler) this.anvilConverterForAnvilFile,    // or saveHandler
//                    this.getFolderName(),               // world name
//                    2,
//                    new WorldSettings(0L, EnumGameType.SURVIVAL, true, false, WorldType.DEFAULT),
//                    this.theProfiler,
//                    this.getLogAgent()
//            );
//
//            this.worldServers[3].addWorldAccess(new WorldManager(serv, this.worldServers[3]));
//            this.worldServers[3].getWorldInfo().setGameType(this.getGameType());
//        }
//    }

    @Inject(method = "loadAllWorlds", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;initialWorldChunkLoad()V"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void customWorldDimensionCode(String par1Str, String par2Str, long par3, WorldType par5WorldType, String par6Str, CallbackInfo ci, ISaveHandler var7, WorldInfo var9, WorldSettings var8){
        MinecraftServer serv = (MinecraftServer) (Object)this;
        this.worldServers[3] =  new WorldServerMulti(
                serv,
                var7,
                par2Str,
                2,
                var8,
                this.worldServers[3],
                this.theProfiler,
                this.getLogAgent());
        this.serverConfigManager.setPlayerManager(this.worldServers);
        this.worldServers[3].addWorldAccess(new WorldManager(serv, this.worldServers[3]));


    }

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
