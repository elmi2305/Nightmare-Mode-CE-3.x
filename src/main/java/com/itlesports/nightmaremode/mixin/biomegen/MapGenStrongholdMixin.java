package com.itlesports.nightmaremode.mixin.biomegen;

import com.itlesports.nightmaremode.mixin.interfaces.MapGenBaseAccess;
import com.itlesports.nightmaremode.worldgen.OverworldTierHelper;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Mixin(MapGenStronghold.class)
public class MapGenStrongholdMixin {
    @Inject(method = "canSpawnStructureAtCoords", at = @At("HEAD"), cancellable = true)
    private void placeStrongholdsAtFiftyThousand(int chunkX, int chunkZ, CallbackInfoReturnable<Boolean> cir) {
        World world = ((MapGenBaseAccess)this).nightmareMode$getWorld();
        if (world == null || world.provider.dimensionId != 0) return;
        for (ChunkCoordIntPair coordinate : getOuterStrongholdCoordinates(world)) {
            if (coordinate.chunkXPos == chunkX && coordinate.chunkZPos == chunkZ) {
                cir.setReturnValue(true);
                return;
            }
        }
        cir.setReturnValue(false);
    }

    @Inject(method = "getCoordList", at = @At("HEAD"), cancellable = true)
    private void exposeOuterStrongholdCoordinates(CallbackInfoReturnable<List> cir) {
        World world = ((MapGenBaseAccess)this).nightmareMode$getWorld();
        if (world == null || world.provider.dimensionId != 0) return;
        List<ChunkPosition> positions = new ArrayList<>();
        for (ChunkCoordIntPair coordinate : getOuterStrongholdCoordinates(world)) {
            positions.add(coordinate.getChunkPosition(64));
        }
        cir.setReturnValue(positions);
    }

    private static ChunkCoordIntPair[] getOuterStrongholdCoordinates(World world) {
        ChunkCoordinates spawn = world.getSpawnPoint();
        Random random = new Random(world.getSeed() ^ 0x5354524F4E47484FL);
        double initialAngle = random.nextDouble() * Math.PI * 2.0D;
        ChunkCoordIntPair[] result = new ChunkCoordIntPair[3];
        for (int i = 0; i < result.length; ++i) {
            double angle = initialAngle + i * Math.PI * 2.0D / result.length;
            int blockX = spawn.posX + (int)Math.round(Math.cos(angle) * OverworldTierHelper.STRONGHOLD_RADIUS);
            int blockZ = spawn.posZ + (int)Math.round(Math.sin(angle) * OverworldTierHelper.STRONGHOLD_RADIUS);
            result[i] = new ChunkCoordIntPair(blockX >> 4, blockZ >> 4);
        }
        return result;
    }
}
