package com.itlesports.nightmaremode.mixin;

import com.itlesports.nightmaremode.world.ChunkLoaderManager;
import net.minecraft.src.ChunkProviderServer;
import net.minecraft.src.WorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkProviderServer.class)
public class ChunkProviderServerMixin {
    @Shadow private WorldServer worldObj;

    @Inject(method = "unloadChunksIfNotNearSpawn", at = @At("HEAD"), cancellable = true)
    private void keepChargedLoaderChunksLoaded(int chunkX, int chunkZ, CallbackInfo ci) {
        if (ChunkLoaderManager.keepsChunkLoaded(this.worldObj, chunkX, chunkZ)) {
            ci.cancel();
        }
    }

    @Inject(method = "isSpawnChunk", at = @At("HEAD"), cancellable = true)
    private void disableAlwaysLoadedSpawnChunks(int chunkX, int chunkZ, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }
}
