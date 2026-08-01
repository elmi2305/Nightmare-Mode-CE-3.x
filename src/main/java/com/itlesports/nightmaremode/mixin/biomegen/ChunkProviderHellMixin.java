package com.itlesports.nightmaremode.mixin.biomegen;

import btw.world.structure.NetherBridgeMapGen;
import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.structure.MapGenNetherDesertTemple;
import com.itlesports.nightmaremode.structure.MapGenNetherVillagerPost;
import com.itlesports.nightmaremode.worldgen.WorldGenOreNode;
import com.itlesports.nightmaremode.worldgen.NetherTierHelper;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
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
    @Unique private final MapGenNetherVillagerPost netherVillagerPostGenerator = new MapGenNetherVillagerPost();

    @Inject(method = "populate", at = @At("TAIL"))
    private void generateTungstenAndShrubs(IChunkProvider provider, int chunkX, int chunkZ, CallbackInfo ci) {
        int baseX = chunkX * 16;
        int baseZ = chunkZ * 16;
        if (NetherTierHelper.isChunkEntirelyTierZero(this.worldObj, chunkX, chunkZ)) {
            for (int attempt = 0; attempt < 6; ++attempt) {
                tungsten.generate(this.worldObj, this.hellRNG,
                        baseX + this.hellRNG.nextInt(16),
                        this.hellRNG.nextInt(60) + 3,
                        baseZ + this.hellRNG.nextInt(16));
            }
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

        if (NetherTierHelper.isChunkEntirelyTierZero(this.worldObj, chunkX, chunkZ)) {
            netherDesertTempleGenerator.generateStructuresInChunk(this.worldObj, this.hellRNG, chunkX, chunkZ);
        }
        netherVillagerPostGenerator.generateStructuresInChunk(this.worldObj, this.hellRNG, chunkX, chunkZ);
    }

    @Inject(method = "provideChunk", at = @At("TAIL"))
    private void prepareNetherDesertTemples(int chunkX, int chunkZ, CallbackInfoReturnable<Chunk> cir) {
        if (NetherTierHelper.isChunkEntirelyTierZero(this.worldObj, chunkX, chunkZ)) {
            netherDesertTempleGenerator.generate((ChunkProviderHell) (Object) this, this.worldObj, chunkX, chunkZ, null, null);
        }
        netherVillagerPostGenerator.generate((ChunkProviderHell) (Object) this, this.worldObj, chunkX, chunkZ, null, null);
        this.applyDistanceBasedNetherrack(cir.getReturnValue(), chunkX, chunkZ);
    }

    @Unique
    private void applyDistanceBasedNetherrack(Chunk chunk, int chunkX, int chunkZ) {
        for (int localX = 0; localX < 16; ++localX) {
            int worldX = chunkX * 16 + localX;
            for (int localZ = 0; localZ < 16; ++localZ) {
                int worldZ = chunkZ * 16 + localZ;
                int metadata = NetherTierHelper.getNetherrackMetadata(this.worldObj, worldX, worldZ);
                if (metadata == 0) {
                    continue;
                }
                for (int y = 0; y < 128; ++y) {
                    if (chunk.getBlockID(localX, y, localZ) == Block.netherrack.blockID) {
                        chunk.setBlockMetadata(localX, y, localZ, metadata);
                    }
                }
            }
        }
    }

    @Redirect(method = "populate", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/WorldGenMinable;generate(Lnet/minecraft/src/World;Ljava/util/Random;III)Z"))
    private boolean preventQuartzOutsideInnerNether(WorldGenMinable generator, World world, Random random,
                                                    int x, int y, int z, IChunkProvider provider,
                                                    int chunkX, int chunkZ) {
        return NetherTierHelper.isChunkEntirelyTierZero(world, chunkX, chunkZ)
                && generator.generate(world, random, x, y, z);
    }

    @Redirect(method = {"provideChunk", "recreateStructures"}, at = @At(value = "INVOKE", target = "Lbtw/world/structure/NetherBridgeMapGen;generate(Lnet/minecraft/src/IChunkProvider;Lnet/minecraft/src/World;II[S[B)V"))
    private void preventFortressPiecesOutsideInnerNether(NetherBridgeMapGen generator, IChunkProvider provider,
                                                         World world, int chunkX, int chunkZ,
                                                         short[] blockIDs, byte[] metadata) {
        if (NetherTierHelper.isChunkEntirelyTierZero(world, chunkX, chunkZ)) {
            generator.generate(provider, world, chunkX, chunkZ, blockIDs, metadata);
        }
    }

    @Redirect(method = "populate", at = @At(value = "INVOKE", target = "Lbtw/world/structure/NetherBridgeMapGen;generateStructuresInChunk(Lnet/minecraft/src/World;Ljava/util/Random;II)Z"))
    private boolean preventFortressPopulationOutsideInnerNether(NetherBridgeMapGen generator, World world,
                                                                Random random, int chunkX, int chunkZ) {
        return NetherTierHelper.isChunkEntirelyTierZero(world, chunkX, chunkZ)
                && generator.generateStructuresInChunk(world, random, chunkX, chunkZ);
    }

    @Inject(method = "getPossibleCreatures", at = @At("HEAD"), cancellable = true)
    private void useNetherTempleSpawnTable(EnumCreatureType creatureType, int x, int y, int z, CallbackInfoReturnable<List> cir) {
        if (creatureType == EnumCreatureType.monster && netherDesertTempleGenerator.hasTempleAt(x, y, z)) {
            cir.setReturnValue(netherDesertTempleGenerator.getSpawnList());
        }
    }
}
