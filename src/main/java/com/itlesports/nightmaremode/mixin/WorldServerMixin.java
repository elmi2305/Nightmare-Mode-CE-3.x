package com.itlesports.nightmaremode.mixin;

import api.world.data.DataEntry;
import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.util.NMUtils;
import com.itlesports.nightmaremode.util.elements.NMEvents;
import com.itlesports.nightmaremode.util.interfaces.WorldServerExt;
import com.itlesports.nightmaremode.world.JourneyProfile;
import com.itlesports.nightmaremode.world.ChunkLoaderManager;
import com.itlesports.nightmaremode.agriculture.ChunkPollutionManager;
import com.itlesports.nightmaremode.network.SkylightSync;
import com.itlesports.nightmaremode.util.interfaces.WorldSkylightSyncAccess;
import net.minecraft.src.*;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldServer.class)
public abstract class WorldServerMixin extends World implements WorldServerExt, WorldSkylightSyncAccess {
    @Unique
    private WorldServer sharedProgressWorld(DataEntry.WorldDataEntry<?> entry) {
        if (this.provider == null || this.provider.dimensionId == 0
                || entry != NightmareMode.WORLD_SKILL_TREE && entry != NightmareMode.JOURNEY_PROFILE) return null;
        MinecraftServer server = MinecraftServer.getServer();
        return server == null ? null : server.worldServerForDimension(0);
    }

    @Inject(method = "getData", at = @At("HEAD"), cancellable = true)
    private <T> void readSharedProgress(DataEntry.WorldDataEntry<T> entry, CallbackInfoReturnable<T> cir) {
        WorldServer overworld = this.sharedProgressWorld(entry);
        if (overworld != null) cir.setReturnValue(overworld.getData(entry));
    }

    @Inject(method = "setData", at = @At("HEAD"), cancellable = true)
    private <T> void writeSharedProgress(DataEntry.WorldDataEntry<T> entry, T value, CallbackInfo ci) {
        WorldServer overworld = this.sharedProgressWorld(entry);
        if (overworld != null) {
            overworld.setData(entry, value);
            ci.cancel();
        }
    }

    @Unique private final SkylightSync skylightSync = new SkylightSync();

    @Override
    public SkylightSync nm$getSkylightSync() {
        return this.skylightSync;
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void sendDeferredSkylightChanges(CallbackInfo ci) {
        this.skylightSync.flush((WorldServer)(Object)this);
    }

    @Shadow public abstract <T> void setData(DataEntry.WorldDataEntry<T> entry, T value);

    @Unique private boolean oldBlueMoon;
    @Unique private int earlyDayTick;



    public WorldServerMixin(ISaveHandler par1ISaveHandler, String par2Str, WorldProvider par3WorldProvider, WorldSettings par4WorldSettings, Profiler par5Profiler, ILogAgent par6ILogAgent) {
        super(par1ISaveHandler, par2Str, par3WorldProvider, par4WorldSettings, par5Profiler, par6ILogAgent);
    }

//    @Inject(method = "initialize", at = @At("TAIL"))
//    private void resetBlueMoonStuff(WorldSettings par1WorldSettings, CallbackInfo ci){
//        oldBlueMoon = NightmareMode.isBlueMoon;
//        NightmareMode.isBlueMoon = false;
//    }

    @Unique private boolean isBlueMoonWorld;
    @Inject(method = "tick", at = @At("TAIL"))
    private void onTickTail(CallbackInfo ci){
//        if(this.worldInfo.dimension == NMFields.UNDERWORLD_DIMENSION){
//            if(time % 100 != 1) return;
//            NightmareMode.setBlueMoon(
//                    this.getIsBlueMoon(this)
//            );
//            boolean shouldChangeBlueMoon   = NightmareMode.isBlueMoon   != oldBlueMoon;
//            if(shouldChangeBlueMoon){
//                System.out.println("sending packet");
//                NightmareMode.sendMoonAndSunEventsToAllPlayers();
//            }
//
//            oldBlueMoon   = NightmareMode.isBlueMoon;
//        }
        long time = this.getWorldTime();
//        if(this.worldInfo.dimension == NMFields.UNDERWORLD_DIMENSION && time % 20 == 1 && !this.isRemote) {
//            this.setBlueMoonWorld(this.calculateIsBlueMoon(this));             // only server logic for now
//
//            System.out.println("calculated blue moon: " + this.isBlueMoonWorld);
//        } else{
//            this.setBlueMoonWorld(false);
//        }


        if ((time & 15) == 0) {
            NMEvents.onServerTick((WorldServer)(Object)this);
        }
        if (time % 20 == 0) {
            ChunkPollutionManager.tickLoadedChunks((WorldServer)(Object)this);
        }
        if (time % 20 == 0) {
            ChunkLoaderManager.loadChargedChunks((WorldServer)(Object)this);
        }
        if (this.provider.dimensionId == 0 && time % 200 == 0) {
            JourneyProfile profile = JourneyProfile.getOrCreate((World)(Object)this);
            profile.playTicks = Math.max(profile.playTicks, this.getTotalWorldTime());
            profile.worldState = NMUtils.getWorldProgress();
            this.setData(NightmareMode.JOURNEY_PROFILE, profile);
        }
    }
    @Unique private void setBlueMoonWorld(boolean b){
        this.isBlueMoonWorld = b;
    }


//    @Unique private boolean calculateIsBlueMoon(World world){
////        if(NMUtils.getWorldProgress() <= 1){return false;}
//        // TODO include the upper condition. it's off for debugging
//
////        System.out.println(this.worldInfo.dimension == NMFields.UNDERWORLD_DIMENSION ? "UNDERWORLD" : "SOMETHING ELSE");
////        System.out.println("Night: " + getIsNightFromWorldTime(world));
////        System.out.println("Moon Phase: " + world.getMoonPhase());
////        System.out.println("OUTPUT: " + (this.worldInfo.dimension == NMFields.UNDERWORLD_DIMENSION && this.getIsNightFromWorldTime(world) && world.getMoonPhase() == 0));
////        System.out.println(" ");
//        // works as expected
//        return this.worldInfo.dimension == NMFields.UNDERWORLD_DIMENSION && this.getIsNightFromWorldTime(world) && world.getMoonPhase() == 0;
//    }


    @ModifyArg(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/WorldInfo;setWorldTime(J)V"))
    private long extendFirstTwoDays(long worldTime){
        long currentTime = this.getWorldTime();
        if (this.provider.dimensionId == 0 && currentTime < 48000L
                && currentTime % 24000L < 12000L
                && ++this.earlyDayTick % 2 != 0) {
            return worldTime - 1L;
        }
        return worldTime;
    }

    @Override
    public boolean nightmareMode$getIsBlueMoon() {
        return this.isBlueMoonWorld;
    }

    @Override
    public void nightmareMode$setIsBlueMoon(boolean isBlueMoon) {
        this.setBlueMoonWorld(isBlueMoon);
        // probably won't be used - no need to call it externally
    }
}
