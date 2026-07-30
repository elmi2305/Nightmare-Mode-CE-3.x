package com.itlesports.nightmaremode.mixin.biomegen;

import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.structure.MapGenNetherDesertTemple;
import com.itlesports.nightmaremode.worldgen.WorldGenOreNode;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Random;

@Mixin(ChunkProviderHell.class)
public class ChunkProviderHellMixin {
    @Shadow private Random hellRNG;
    @Shadow private World worldObj;

    @Unique private static WorldGenFlowers worldGenShrubs = new WorldGenFlowers(NMBlocks.netherShrub.blockID);
    @Unique private static WorldGenMinable tungsten = new WorldGenMinable(NMBlocks.tungstenOre.blockID, 6, Block.netherrack.blockID);
    @Unique private static WorldGenOreNode tungstenNodes = new WorldGenOreNode(
            NMBlocks.tungstenOreNode.blockID, Block.netherrack.blockID, 1, 4);
    @Unique private final MapGenNetherDesertTemple netherDesertTempleGenerator = new MapGenNetherDesertTemple();



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

        ChunkCoordinates spawn = this.worldObj.getSpawnPoint();
        for (int attempt = 0; attempt < 12; ++attempt) {
            int nodeX = baseX + this.hellRNG.nextInt(16);
            int nodeZ = baseZ + this.hellRNG.nextInt(16);
            long distanceX = nodeX - spawn.posX;
            long distanceZ = nodeZ - spawn.posZ;
            if (distanceX * distanceX + distanceZ * distanceZ <= 200L * 200L
                    && tungstenNodes.generate(this.worldObj, this.hellRNG, nodeX,
                    this.hellRNG.nextInt(56) + 4, nodeZ)) {
                break;
            }
        }

        worldGenShrubs.generate(this.worldObj, this.hellRNG,
                baseX + this.hellRNG.nextInt(16) + 8,
                this.hellRNG.nextInt(128),
                baseZ + this.hellRNG.nextInt(16) + 8);

        netherDesertTempleGenerator.generateStructuresInChunk(this.worldObj, this.hellRNG, chunkX, chunkZ);
    }

    @Inject(method = "provideChunk", at = @At("TAIL"))
    private void prepareNetherDesertTemples(int chunkX, int chunkZ, CallbackInfoReturnable<Chunk> cir) {
        netherDesertTempleGenerator.generate((ChunkProviderHell) (Object) this, this.worldObj, chunkX, chunkZ, null, null);
    }

    @Inject(method = "getPossibleCreatures", at = @At("HEAD"), cancellable = true)
    private void useNetherTempleSpawnTable(EnumCreatureType creatureType, int x, int y, int z, CallbackInfoReturnable<List> cir) {
        if (creatureType == EnumCreatureType.monster && netherDesertTempleGenerator.hasTempleAt(x, y, z)) {
            cir.setReturnValue(netherDesertTempleGenerator.getSpawnList());
        }
    }
}
