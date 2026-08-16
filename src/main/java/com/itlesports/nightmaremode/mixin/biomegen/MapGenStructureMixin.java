package com.itlesports.nightmaremode.mixin.biomegen;

import net.minecraft.src.ChunkPosition;
import net.minecraft.src.MapGenStronghold;
import net.minecraft.src.MapGenStructure;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MapGenStructure.class)
public class MapGenStructureMixin {
    @Inject(method = "getNearestInstance", at = @At("HEAD"), cancellable = true)
    private void locateOuterStrongholdRing(World world, int x, int y, int z,
                                           CallbackInfoReturnable<ChunkPosition> cir) {
        if (!((Object)this instanceof MapGenStronghold) || world.provider.dimensionId != 0) return;
        ChunkPosition nearest = null;
        double nearestDistance = Double.MAX_VALUE;
        for (ChunkPosition position : com.itlesports.nightmaremode.worldgen.OverworldTierHelper.getStrongholdPositions(world)) {
            double dx = position.x - x;
            double dz = position.z - z;
            double distance = dx * dx + dz * dz;
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearest = position;
            }
        }
        cir.setReturnValue(nearest);
    }
}
