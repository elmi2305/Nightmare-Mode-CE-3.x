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
