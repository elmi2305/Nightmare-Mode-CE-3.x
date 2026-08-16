package com.itlesports.nightmaremode.mixin.biomegen;

import com.itlesports.nightmaremode.mixin.interfaces.MapGenBaseAccess;
import com.itlesports.nightmaremode.worldgen.OverworldTierHelper;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(MapGenStronghold.class)
public class MapGenStrongholdMixin {
    @Inject(method = "canSpawnStructureAtCoords", at = @At("HEAD"), cancellable = true)
    private void placeStrongholdsAtFiftyThousand(int chunkX, int chunkZ, CallbackInfoReturnable<Boolean> cir) {
        World world = ((MapGenBaseAccess)this).nightmareMode$getWorld();
        if (world == null || world.provider.dimensionId != 0) return;
        for (ChunkPosition position : OverworldTierHelper.getStrongholdPositions(world)) {
            if (position.x >> 4 == chunkX && position.z >> 4 == chunkZ) {
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
        cir.setReturnValue(OverworldTierHelper.getStrongholdPositions(world));
    }
}
