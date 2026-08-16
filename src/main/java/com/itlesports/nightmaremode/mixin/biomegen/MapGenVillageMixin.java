package com.itlesports.nightmaremode.mixin.biomegen;

import com.itlesports.nightmaremode.mixin.interfaces.MapGenBaseAccess;
import com.itlesports.nightmaremode.worldgen.OverworldTierHelper;
import net.minecraft.src.MapGenVillage;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MapGenVillage.class)
public class MapGenVillageMixin {
    @Inject(method = "canSpawnStructureAtCoords", at = @At("HEAD"), cancellable = true)
    private void suppressOuterVillages(int chunkX, int chunkZ, CallbackInfoReturnable<Boolean> cir) {
        World world = ((MapGenBaseAccess)this).nightmareMode$getWorld();
        if (world == null || world.provider.dimensionId != 0) return;
        double distance = OverworldTierHelper.getDistanceFromSpawn(world, chunkX * 16 + 8, chunkZ * 16 + 8);
        if (distance >= OverworldTierHelper.DEADZONE_START - 256.0D
                && distance < OverworldTierHelper.GREAT_VOID_START) {
            cir.setReturnValue(false);
        }
    }
}
