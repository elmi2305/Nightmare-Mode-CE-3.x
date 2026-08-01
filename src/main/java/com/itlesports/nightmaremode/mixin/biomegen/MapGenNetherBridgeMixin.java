package com.itlesports.nightmaremode.mixin.biomegen;

import com.itlesports.nightmaremode.mixin.interfaces.MapGenBaseAccess;
import com.itlesports.nightmaremode.worldgen.NetherTierHelper;
import net.minecraft.src.MapGenNetherBridge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MapGenNetherBridge.class)
public class MapGenNetherBridgeMixin {
    @Inject(method = "canSpawnStructureAtCoords", at = @At("HEAD"), cancellable = true)
    private void preventFortressesOutsideInnerNether(int chunkX, int chunkZ, CallbackInfoReturnable<Boolean> cir) {
        if (!NetherTierHelper.isChunkEntirelyTierZero(
                ((MapGenBaseAccess) this).nightmareMode$getWorld(), chunkX, chunkZ)) {
            cir.setReturnValue(false);
        }
    }
}
