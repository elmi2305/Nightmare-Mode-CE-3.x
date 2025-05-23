package com.itlesports.nightmaremode.mixin;

import btw.community.nightmaremode.NightmareMode;
import btw.util.hardcorespawn.HardcoreSpawnUtils;
import btw.world.util.difficulty.Difficulties;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.ArrayList;

// code is sloppy, there's probably a better way to do this.

@Mixin(HardcoreSpawnUtils.class)
public abstract class HardcoreSpawnUtilsMixin{
    @Unique private static WorldServer worldServer;
    @Inject(method = "assignNewHardcoreSpawnLocation", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/WorldServer;setWorldTime(J)V"),locals = LocalCapture.CAPTURE_FAILHARD)
    private static void declareWorldServer(World world, MinecraftServer server, EntityPlayerMP player, CallbackInfoReturnable<Boolean> cir, boolean locationFound, boolean blacklistedLocationFound, double spawnRadius, double exclusionRadius, double spawnDeltaRadius, double exclusionRadiusSq, double deltaSquaredRadii, int attempts, double spawnDistance, double spawnYaw, double xOffset, double zOffset, int newSpawnX, int newSpawnZ, int newSpawnY, BiomeGenBase respawnBiome, boolean isBiomeBlacklisted, Material targetMaterial, long overworldTime, int i, WorldServer tempServer){
        worldServer = tempServer;
    }

    @Inject(method = "handleHardcoreSpawn", at = @At("TAIL"))
    private static void handleExperienceLossOnDeath(MinecraftServer server, EntityPlayerMP oldPlayer, EntityPlayerMP newPlayer, CallbackInfo ci){
        newPlayer.addExperience((int) (Math.log10(oldPlayer.experienceTotal * 2) * 16));
        if(newPlayer.experienceLevel > 0){
            newPlayer.addExperienceLevel(-1);
        }
    }

    @ModifyArg(method = "assignNewHardcoreSpawnLocation", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/WorldServer;setWorldTime(J)V"))
    private static long setNightOnRespawn(long par1){
        if(NightmareMode.worldState == 2){
            return par1;
        }
        if(worldServer != null){
            int moonPhase = worldServer.getMoonPhase();
            return par1
                    + (worldServer.getDifficulty() == Difficulties.HOSTILE ? 18000 : 0)
                    + (moonPhase == 3 ? 24000 : 0);
        }
        return par1;
        // if the world server was somehow null
    }

    @ModifyConstant(method = "handleHardcoreSpawn", constant = @Constant(longValue = 10800L))
    private static long lowerCooldownForRandomSpawning(long constant){
        return 600L;
    }
    @ModifyArg(method = "handleHardcoreSpawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityPlayerMP;setHealth(F)V"))
    private static float doNotLowerHealth(float par1){
        return 20f;
    }
    @ModifyArg(method = "handleHardcoreSpawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/FoodStats;setFoodLevel(I)V"))
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

    @Unique
    private static boolean hasDeepWaterNearby(double x, double z, World world) {
        // Fixed water level
        int y1 = 62; // surface
        int y2 = 61; // one below surface

        // Check in cardinal directions, exactly 10 blocks away
        int[][] offsets = {
                {15, 0},   // East
                {-15, 0},  // West
                {0, 15},   // South
                {0, -15}   // North
        };

        for (int[] offset : offsets) {
            int checkX = (int) Math.floor(x + offset[0]);
            int checkZ = (int) Math.floor(z + offset[1]);

            int block1 = world.getBlockId(checkX, y1, checkZ);
            int block2 = world.getBlockId(checkX, y2, checkZ);
            System.out.println(block1 + ", " + block2);

            if (block1 == Block.waterStill.blockID && block2 == Block.waterStill.blockID) {
                return true;
            }
        }
        return false;
    }
}
