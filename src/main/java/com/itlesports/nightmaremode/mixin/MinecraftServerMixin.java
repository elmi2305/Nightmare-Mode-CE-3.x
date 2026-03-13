package com.itlesports.nightmaremode.mixin;

import api.achievement.AchievementEventDispatcher;
import api.world.WorldUtils;
import api.world.difficulty.Difficulty;
import btw.community.nightmaremode.NightmareMode;
import btw.world.BTWDifficulties;
import com.itlesports.nightmaremode.util.NMFields;
import com.itlesports.nightmaremode.util.NMUtils;
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
    @Mutable @Shadow public WorldServer[] worldServers;
    @Shadow @Final public Profiler theProfiler;
    @Shadow public abstract ILogAgent getLogAgent();
    @Shadow public abstract ServerConfigurationManager getConfigurationManager();
    @Shadow public abstract void setDifficulty(Difficulty difficultyLevel);
    @Shadow public abstract ISaveFormat getActiveAnvilConverter();
    @Shadow public abstract EnumGameType getGameType();
    @Shadow public abstract boolean canStructuresSpawn();
    @Shadow public abstract boolean isHardcore();
    @Shadow protected Difficulty difficultyLevel;



    @Unique private boolean oldBloodMoon;
    @Unique private boolean oldBlueMoon;
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
//        oldBlueMoon = NightmareMode.isBlueMoon;

        NightmareMode.isEclipse = false;
        NightmareMode.isBloodMoon = false;
//        NightmareMode.isBlueMoon = false;
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void nmOnServerTick(CallbackInfo ci) {
        TeleportScheduler.onServerTick();

        WorldServer world = this.worldServers[0];

        if (world.getTotalWorldTime() % 30 != 0) return;

        long worldTime = world.getWorldTime();

        for (Object o : world.playerEntities) {
            EntityPlayer player = (EntityPlayer)o;
            AchievementEventDispatcher.triggerEvent(NMAchievementEvents.TimeEvent.class, player, world.getWorldTime());
            AchievementEventDispatcher.triggerEvent(NMAchievementEvents.TimeItemEvent.class, player, new NMAchievementEvents.TimeItemEvent.Context(player, worldTime));
            AchievementEventDispatcher.triggerEvent(NMAchievementEvents.MiscPlayerEvent.class, player, player);
        }

        int dayCount = (int) Math.ceil((double) worldTime / 24000) + this.isDawnOrDusk(worldTime);


        boolean isNight = this.getIsNightFromWorldTime(world);

        // blood moon
        NightmareMode.setBloodmoon(
                !NightmareMode.bloodmare
                        ? this.getIsBloodMoon(world, dayCount)
                        : isNight
        );

        // eclipse
        NightmareMode.setEclipse(
                !NightmareMode.totalEclipse
                        ? this.getIsEclipse(world, dayCount)
                        : !isNight
        );

        // blue moon


        boolean shouldChangeBloodMoon = NightmareMode.isBloodMoon != oldBloodMoon;
        boolean shouldChangeEclipse   = NightmareMode.isEclipse   != oldEclipse;
        boolean shouldChangeBlueMoon   = NightmareMode.isBlueMoon   != oldBlueMoon;

        if (shouldChangeBloodMoon || shouldChangeEclipse) {
            for (Object o : world.playerEntities) {
                EntityPlayer player = (EntityPlayer) o;

                if (shouldChangeBloodMoon) {
                    AchievementEventDispatcher.triggerEvent(NMAchievementEvents.BloodMoonEvent.class, player, NightmareMode.isBloodMoon);
                }

                if (shouldChangeEclipse) {
                    AchievementEventDispatcher.triggerEvent(NMAchievementEvents.EclipseEvent.class, player, NightmareMode.isEclipse);
                }

                // todo ACHIEVEMENT FOR BLUE MOON
            }

            NightmareMode.sendMoonAndSunEventsToAllPlayers();
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
        oldEclipse   = NightmareMode.isEclipse;
        oldBlueMoon   = NightmareMode.isBlueMoon;
    }
    @Inject(method = "worldServerForDimension", at = @At("HEAD"),cancellable = true)
    private void giveWorldServerForUnderworld(int par1, CallbackInfoReturnable<WorldServer> cir){

        if(par1 == NMFields.UNDERWORLD_DIMENSION){
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

    @Inject(method = "loadAllWorlds", at = @At(value = "INVOKE", target = "Lapi/AddonHandler;initializeDifficultyCommon(Lapi/world/difficulty/Difficulty;)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void customWorldDimensionCode(String par1Str, String par2Str, long par3, WorldType par5WorldType, String par6Str, CallbackInfo ci){
        MinecraftServer serv = (MinecraftServer) (Object)this;
        ISaveHandler var7;
        if (this.getActiveAnvilConverter().isWorldGlobal(par1Str)) {
            var7 = this.getActiveAnvilConverter().getSaveLoader2(par1Str, true);
            this.setDifficulty(BTWDifficulties.HOSTILE_LOCKED);
        } else {
            var7 = this.getActiveAnvilConverter().getSaveLoader(par1Str, true);
        }
        WorldSettings worldSettings;
        WorldInfo var9 = var7.loadWorldInfo();
        if (var9 == null) {
            worldSettings = new WorldSettings(par3, this.getGameType(), this.canStructuresSpawn(), this.isHardcore(), par5WorldType, this.difficultyLevel, false);
            worldSettings.func_82750_a(par6Str);
        } else {
            worldSettings = new WorldSettings(var9);
        }
        this.worldServers[3] =  new WorldServerMulti(
                serv,
                var7,
                par2Str,
                NMFields.UNDERWORLD_DIMENSION,
                worldSettings,
                this.worldServers[3],
                this.theProfiler,
                this.getLogAgent());
        this.getConfigurationManager().setPlayerManager(this.worldServers);
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
        if(world.provider.dimensionId == NMFields.UNDERWORLD_DIMENSION){return false;}
        return this.getIsNightFromWorldTime(world) && (world.getMoonPhase() == 0  && (dayCount % 16 == 9)) || NightmareMode.bloodmare;
    }

    @Unique private boolean getIsBlueMoon(World world){
//        if(NMUtils.getWorldProgress() <= 1){return false;}
        // TODO include the upper condition. it's off for debugging

//        World world1 = this.worldServerForDimension(NMFields.UNDERWORLD_DIMENSION);
//        if (world1 == null) return false;
        // this doesn't work
        return this.getIsNightFromWorldTime(world) && world.getMoonPhase() == 0;
    }
    @Unique private boolean getIsNightFromWorldTime(World world){
        return world.getWorldTime() % 24000 >= 12541 && world.getWorldTime() % 24000 <= 23459;
    }

    @Unique private boolean getIsEclipse(World world, int dayCount){
        if(NMUtils.getWorldProgress() <= 2){return false;}
        return !this.getIsNightFromWorldTime(world) && dayCount % 2 == 0;
    }
}
