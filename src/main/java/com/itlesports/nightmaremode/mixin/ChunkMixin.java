package com.itlesports.nightmaremode.mixin;

import net.minecraft.src.Chunk;
import net.minecraft.src.ExtendedBlockStorage;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Chunk.class)
public class ChunkMixin {
    @Shadow private ExtendedBlockStorage[] storageArrays;

//    @Inject(method = "<init>(Lnet/minecraft/src/World;II)V", at = @At("TAIL"))
//    private void setChunkBank(World par1World, int par2, int par3, CallbackInfo ci){
//        this.storageArrays = new ExtendedBlockStorage[32];
//    }
}
