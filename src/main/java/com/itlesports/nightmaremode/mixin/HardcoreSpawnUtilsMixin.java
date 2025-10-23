package com.itlesports.nightmaremode.mixin;

import btw.community.nightmaremode.NightmareMode;
import btw.util.hardcorespawn.HardcoreSpawnUtils;
import btw.world.util.difficulty.Difficulties;
import com.itlesports.nightmaremode.NMDifficultyParam;
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

    @Inject(method = "handleHardcoreSpawn", at = @At("TAIL"))
    private static void handleExperienceLossOnDeath(MinecraftServer server, EntityPlayerMP oldPlayer, EntityPlayerMP newPlayer, CallbackInfo ci){
        newPlayer.addExperience(ensurePositive(Math.log10(oldPlayer.experienceTotal * 2) * 16));
        if(newPlayer.experienceLevel > 0){
            newPlayer.addExperienceLevel(-1);
        }
        if(newPlayer.experienceTotal < 0){
            newPlayer.experienceTotal = 0;
        }
    }
    @Unique private static int ensurePositive(double input){
        if(input >= 0){
            return (int) input;
        }
        return 0;
    }

    @ModifyArg(method = "assignNewHardcoreSpawnLocation", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/WorldServer;setWorldTime(J)V"))
    private static long setNightOnRespawn(long par1){
        if(NightmareMode.worldState == 2){
            return par1;
        }
        if(worldServer != null){
            int moonPhase = worldServer.getMoonPhase();
            return par1
                    + (worldServer.getDifficultyParameter(NMDifficultyParam.ShouldMobsBeBuffed.class) ? 18000 : 0)
                    + (moonPhase == 3 ? 24000 : 0);
        }
        return par1;
        // if the world server was somehow null
    }

    @ModifyConstant(method = "handleHardcoreSpawn", constant = @Constant(longValue = 10800L))
    private static long lowerCooldownForRandomSpawning(long constant){
        return 600L;
    }
    @ModifyArg(method = "onSoftRespawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityPlayerMP;setHealth(F)V"))
    private static float doNotLowerHealth(float par1){
        return 20f;
    }
    @ModifyArg(method = "onSoftRespawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/FoodStats;setFoodLevel(I)V"))
    private static int doNotLowerFood(int par1){
        return 60;
    }

    @Redirect(method = "assignNewHardcoreSpawnLocation", at = @At(value = "INVOKE", target = "Ljava/util/ArrayList;contains(Ljava/lang/Object;)Z"))
    private static boolean excludeJungle(ArrayList list, Object o){
        if((o.equals(BiomeGenBase.jungle) || o.equals(BiomeGenBase.jungleHills)) && NightmareMode.bloodmare){
            return true;
        }
        if((o.equals(BiomeGenBase.icePlains) || o.equals(BiomeGenBase.iceMountains))){
            return true;
        }
        return list.contains(o);
    }

    @Redirect(method = "handleHardcoreSpawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityPlayerMP;sendChatToPlayer(Lnet/minecraft/src/ChatMessageComponent;)V",ordinal = 0))
    private static void avoidSendingChatMessage(EntityPlayerMP instance, ChatMessageComponent par1ChatMessageComponent){}

    @Inject(method = "getPlayerSpawnRadius", at = @At("HEAD"),cancellable = true)
    private static void makePlayerSpawnCloser(World world, CallbackInfoReturnable<Double> cir){
        cir.setReturnValue(800 * HardcoreSpawnUtils.getWorldTypeRadiusMultiplier(world) * HardcoreSpawnUtils.getGameProgressRadiusMultiplier(world));
    }

    @Inject(method = "getPlayerSpawnExclusionRadius", at = @At("HEAD"),cancellable = true)
    private static void makePlayerSpawnCloser0(World world, CallbackInfoReturnable<Double> cir){
        cir.setReturnValue(400 * HardcoreSpawnUtils.getWorldTypeRadiusMultiplier(world) * HardcoreSpawnUtils.getGameProgressRadiusMultiplier(world));
    }
}
