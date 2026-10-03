package com.itlesports.nightmaremode.mixin;

import btw.util.hardcorespawn.HardcoreSpawnUtils;
import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.util.NMUtils;
import com.itlesports.nightmaremode.util.NMFields;
import com.itlesports.nightmaremode.util.NMHardcoreSpawnContext;
import com.itlesports.nightmaremode.util.interfaces.FoodStatsExt;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HardcoreSpawnUtils.class)
public abstract class HardcoreSpawnUtilsMixin{

    @Redirect(method = {"assignNewHardcoreSpawnLocation", "assignPlayerNearbyExistingSpawnPos"},
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;getTopSolidOrLiquidBlock(II)I"))
    private static int rejectHighSpawnCandidates(World world, int x, int z) {
        int y = world.getTopSolidOrLiquidBlock(x, z);
        return world.provider.dimensionId == 0 && y + 1.5D > NMFields.SOLAR_RADIATION_HEIGHT
                ? -1 : y;
    }

    @Inject(method = "assignPlayerToOldSpawnPos", at = @At("HEAD"), cancellable = true)
    private static void rejectHighOldSpawn(World world, EntityPlayerMP player, ChunkCoordinates spawn,
                                         long time, CallbackInfoReturnable<Boolean> cir) {
        if (world.provider.dimensionId == 0
                && world.getTopSolidOrLiquidBlock(spawn.posX, spawn.posZ) + 1.5D > NMFields.SOLAR_RADIATION_HEIGHT) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "bumpPlayerPosUpwardsUntilValidSpawnReached", at = @At("TAIL"))
    private static void protectFallbackSpawn(EntityPlayerMP player, CallbackInfo ci) {
        if (player.dimension != 0 || player.posY <= NMFields.SOLAR_RADIATION_HEIGHT) return;
        World world = player.worldObj;
        int originX = MathHelper.floor_double(player.posX);
        int originZ = MathHelper.floor_double(player.posZ);
        for (int radius = 16; radius <= 4096; radius += 16) {
            for (int direction = 0; direction < 8; ++direction) {
                double angle = direction * Math.PI / 4.0D;
                int x = originX + (int)Math.round(Math.cos(angle) * radius);
                int z = originZ + (int)Math.round(Math.sin(angle) * radius);
                int y = world.getTopSolidOrLiquidBlock(x, z);
                if (y < world.provider.getAverageGroundLevel() || y + 1.5D > NMFields.SOLAR_RADIATION_HEIGHT
                        || world.getBlockMaterial(x, y, z).isLiquid()
                        || world.getBlockMaterial(x, y - 1, z).isLiquid()) continue;
                player.setLocationAndAngles(x + 0.5D, y + 1.5D, z + 0.5D, player.rotationYaw, player.rotationPitch);
                if (world.getCollidingBoundingBoxes(player, player.boundingBox).isEmpty()) return;
            }
        }
        throw new IllegalStateException("No safe spawn below the solar radiation height within 4096 blocks");
    }

    @Shadow
    private static boolean canSpawnNearbyOtherPlayers(WorldServer world) {
        throw new AssertionError();
    }

    @Redirect(method = "assignNewHardcoreSpawnLocation", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/WorldInfo;getSpawnX()I"))
    private static int centerNewSpawnXOnDeath(WorldInfo info, World world, MinecraftServer server, EntityPlayerMP player) {
        ChunkCoordinates origin = NMHardcoreSpawnContext.getOrigin(world);
        return origin != null ? origin.posX : info.getSpawnX();
    }

    @Redirect(method = "assignNewHardcoreSpawnLocation", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/WorldInfo;getSpawnZ()I"))
    private static int centerNewSpawnZOnDeath(WorldInfo info, World world, MinecraftServer server, EntityPlayerMP player) {
        ChunkCoordinates origin = NMHardcoreSpawnContext.getOrigin(world);
        return origin != null ? origin.posZ : info.getSpawnZ();
    }

    @Redirect(method = "handleHardcoreSpawn", at = @At(value = "FIELD", target = "Lnet/minecraft/src/EntityPlayerMP;hardcoreSpawnChunk:Lnet/minecraft/src/ChunkCoordinates;"))
    private static ChunkCoordinates centerRapidRespawnOnDeath(EntityPlayerMP player, MinecraftServer server,
                                                            EntityPlayerMP oldPlayer, EntityPlayerMP newPlayer) {
        ChunkCoordinates origin = NMHardcoreSpawnContext.getOrigin(newPlayer.worldObj);
        return origin != null ? origin : player.hardcoreSpawnChunk;
    }

    @Redirect(method = "handleHardcoreSpawn", at = @At(value = "INVOKE", target = "Lbtw/util/hardcorespawn/HardcoreSpawnUtils;canSpawnNearbyOtherPlayers(Lnet/minecraft/src/WorldServer;)Z"))
    private static boolean keepDeathRespawnsLocal(WorldServer world) {
        return NMHardcoreSpawnContext.getOrigin(world) == null && canSpawnNearbyOtherPlayers(world);
    }

    @Redirect(method = "returnPlayerToOriginalSpawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;getSpawnPoint()Lnet/minecraft/src/ChunkCoordinates;"))
    private static ChunkCoordinates keepFallbackSpawnNearDeath(World world) {
        ChunkCoordinates origin = NMHardcoreSpawnContext.getOrigin(world);
        return origin != null ? new ChunkCoordinates(origin.posX,
                world.getTopSolidOrLiquidBlock(origin.posX, origin.posZ), origin.posZ) : world.getSpawnPoint();
    }

    @ModifyConstant(method = "handleHardcoreSpawn", constant = @Constant(longValue = 10800L))
    private static long lowerCooldownForRandomSpawning(long constant){
        return 1200L;
    }

    @Inject(method = "getPlayerSpawnRadius", at = @At("RETURN"), cancellable = true)
    private static void scaleHardcoreSpawnRadius(World world, CallbackInfoReturnable<Double> cir) {
        cir.setReturnValue(NMUtils.getWorldProgress() > NMFields.PREHARDMODE
                ? cir.getReturnValue() * 1.25D : 500.0D);
    }

    @Inject(method = "getPlayerSpawnExclusionRadius", at = @At("RETURN"), cancellable = true)
    private static void scaleHardcoreSpawnExclusionRadius(World world, CallbackInfoReturnable<Double> cir) {
        if (NMUtils.getWorldProgress() == NMFields.PREHARDMODE) {
            cir.setReturnValue(200.0D);
        }
    }

    @Inject(method = "onSoftRespawn", at = @At("TAIL"))
    private static void restoreFullStatsOnSoftRespawn(EntityPlayerMP oldPlayer, EntityPlayerMP newPlayer, CallbackInfo ci) {
        newPlayer.setHealth(newPlayer.getMaxHealth());
        int maxFood = NightmareMode.nite
                ? NMUtils.getFoodShanksFromLevel(newPlayer)
                : ((FoodStatsExt)newPlayer.foodStats).nightmareMode$getMaxFoodLevel();
        newPlayer.foodStats.setFoodLevel(maxFood);
    }

}
