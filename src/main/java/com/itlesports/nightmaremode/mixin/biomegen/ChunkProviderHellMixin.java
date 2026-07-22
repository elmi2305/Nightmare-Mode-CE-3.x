package com.itlesports.nightmaremode.mixin.biomegen;

import com.itlesports.nightmaremode.block.NMBlocks;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(ChunkProviderHell.class)
public class ChunkProviderHellMixin {
    @Shadow private Random hellRNG;
    @Shadow private World worldObj;

    @Unique private static WorldGenFlowers worldGenShrubs = new WorldGenFlowers(NMBlocks.netherShrub.blockID);
    @Unique private static WorldGenMinable tungsten = new WorldGenMinable(NMBlocks.tungstenOre.blockID, 6, Block.netherrack.blockID);



    @Inject(method = "populate", at = @At("TAIL"))
    private void generateTungstenAndShrubs(IChunkProvider provider, int chunkX, int chunkZ, CallbackInfo ci) {
        int baseX = chunkX * 16;
        int baseZ = chunkZ * 16;
        for (int attempt = 0; attempt < 6; ++attempt) {
            tungsten.generate(this.worldObj, this.hellRNG,
                    baseX + this.hellRNG.nextInt(16),
                    this.hellRNG.nextInt(60) + 3,
                    baseZ + this.hellRNG.nextInt(16));
        }

        worldGenShrubs.generate(this.worldObj, this.hellRNG,
                baseX + this.hellRNG.nextInt(16) + 8,
                this.hellRNG.nextInt(128),
                baseZ + this.hellRNG.nextInt(16) + 8);
    }
}
