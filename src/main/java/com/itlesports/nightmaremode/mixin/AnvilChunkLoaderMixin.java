package com.itlesports.nightmaremode.mixin;

import com.itlesports.nightmaremode.agriculture.ChunkAttributeManager;
import com.itlesports.nightmaremode.agriculture.ChunkAttributes;
import com.itlesports.nightmaremode.util.interfaces.ChunkAttributesAccess;
import net.minecraft.src.AnvilChunkLoader;
import net.minecraft.src.Chunk;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AnvilChunkLoader.class)
public class AnvilChunkLoaderMixin {
    @Inject(method = "writeChunkToNBT", at = @At("TAIL"))
    private void writeChunkAttributes(
            Chunk chunk,
            World world,
            NBTTagCompound tag,
            CallbackInfo ci
    ) {
        ChunkAttributeManager.get(chunk).writeToNBT(tag);
    }

    @Inject(method = "readChunkFromNBT", at = @At("RETURN"))
    private void readChunkAttributes(
            World world,
            NBTTagCompound tag,
            CallbackInfoReturnable<Chunk> cir
    ) {
        Chunk chunk = cir.getReturnValue();
        if (chunk == null) {
            return;
        }
        ChunkAttributes attributes = ((ChunkAttributesAccess)chunk).nightmareMode$getChunkAttributes();
        if (!attributes.readFromNBT(
                tag,
                chunk.xPosition,
                chunk.zPosition,
                world.provider.dimensionId,
                world.getTotalWorldTime()
        )) {
            ChunkAttributeManager.initialize(chunk);
        }
    }
}
