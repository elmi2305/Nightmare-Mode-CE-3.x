package com.itlesports.nightmaremode.mixin;

import com.itlesports.nightmaremode.util.NMFields;
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

    @Inject(method = "<init>(Lnet/minecraft/src/World;[S[BII)V", at = @At("TAIL"))
    private void fix256HeightConstructor(
            World par1World,
            short[] blockIDs,
            byte[] metadata,
            int par3,
            int par4,
            CallbackInfo ci
    ) {
        // ONLY run for your custom dimension
        if (par1World.provider.dimensionId != NMFields.UNDERWORLD_DIMENSION) {
            return; // let vanilla 128-height code handle Overworld, Nether, End, etc.
        }

//        System.out.println("[Underworld] Applying 256-height chunk fix | blockIDs.length = " + blockIDs.length);

        int var5 = blockIDs.length / 256;
        int shiftX = blockIDs.length == 32768 ? 11 : 12;
        int shiftY = blockIDs.length == 32768 ? 7 : 8;
        for (int var6 = 0; var6 < 16; ++var6) {
            for (int var7 = 0; var7 < 16; ++var7) {
                for (int var8 = 0; var8 < var5; ++var8) {
                    short blockID = blockIDs[var6 << shiftX | var7 << shiftY | var8]; // Changed from << 11, << 7 to << 12, << 8 for 256 height support

//                    if (blockID == 0) continue;

                    int var10 = var8 >> 4; // 0–15 for y 0–255
                    if (this.storageArrays[var10] == null) {
                        this.storageArrays[var10] = new ExtendedBlockStorage(var10 << 4, !par1World.provider.hasNoSky);
                    }

                    byte meta = metadata[var6 << shiftX | var7 << shiftY | var8]; // Changed from << 11, << 7 to << 12, << 8 for 256 height support
                    this.storageArrays[var10].setExtBlockID(var6, var8 & 0xF, var7, blockID);
                    this.storageArrays[var10].setExtBlockMetadata(var6, var8 & 0xF, var7, meta);
                }
            }
        }
    }
}
