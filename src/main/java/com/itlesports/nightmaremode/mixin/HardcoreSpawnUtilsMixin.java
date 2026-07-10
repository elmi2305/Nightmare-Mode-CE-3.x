package com.itlesports.nightmaremode.mixin;

import btw.community.nightmaremode.NightmareMode;
import btw.util.hardcorespawn.HardcoreSpawnUtils;
import com.itlesports.nightmaremode.util.elements.NMDifficultyParam;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.ArrayList;

@Mixin(HardcoreSpawnUtils.class)
public abstract class HardcoreSpawnUtilsMixin{
    @Unique private static WorldServer worldServer;
    @Inject(method = "assignNewHardcoreSpawnLocation", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/WorldServer;setWorldTime(J)V"),locals = LocalCapture.CAPTURE_FAILHARD)
    private static void declareWorldServer(World world, MinecraftServer server, EntityPlayerMP player, CallbackInfoReturnable<Boolean> cir, boolean locationFound, boolean blacklistedLocationFound, double spawnRadius, double exclusionRadius, double spawnDeltaRadius, double exclusionRadiusSq, double deltaSquaredRadii, int attempts, double spawnDistance, double spawnYaw, double xOffset, double zOffset, int newSpawnX, int newSpawnZ, int newSpawnY, BiomeGenBase respawnBiome, boolean isBiomeBlacklisted, Material targetMaterial, long overworldTime, int i, WorldServer tempServer){
        worldServer = tempServer;
    }



    @ModifyConstant(method = "handleHardcoreSpawn", constant = @Constant(longValue = 10800L))
    private static long lowerCooldownForRandomSpawning(long constant){
        return 67676767;
    }

}
