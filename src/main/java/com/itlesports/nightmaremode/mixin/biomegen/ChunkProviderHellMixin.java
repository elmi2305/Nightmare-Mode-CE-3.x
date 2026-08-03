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
import java.util.ArrayList;
import com.itlesports.nightmaremode.entity.variants.EntityAshGhast;
import com.itlesports.nightmaremode.entity.variants.EntityCinderBlaze;
import com.itlesports.nightmaremode.entity.variants.EntityCinderPigman;
import com.itlesports.nightmaremode.entity.variants.EntityDeadzonePigman;
import com.itlesports.nightmaremode.entity.variants.EntityHellfireBlaze;
import com.itlesports.nightmaremode.entity.variants.EntitySiegeGhast;
import java.util.Random;

@Mixin(ChunkProviderHell.class)
public class ChunkProviderHellMixin {
    @Shadow private Random hellRNG;
    @Shadow private World worldObj;

    @Unique private static WorldGenFlowers worldGenShrubs = new WorldGenFlowers(NMBlocks.netherShrub.blockID);
    @Unique private static WorldGenMinable tungsten = new WorldGenMinable(NMBlocks.tungstenOre.blockID, 6, Block.netherrack.blockID);
    @Unique private static WorldGenOreNode tungstenNodes = new WorldGenOreNode(
            NMBlocks.tungstenOreNode.blockID, Block.netherrack.blockID, 1, 4);
    @Unique private static WorldGenOreNode coalNodes = new WorldGenOreNode(
            NMBlocks.coalOreNode.blockID, Block.netherrack.blockID, 1, 1);
    @Unique private static WorldGenMinable denseCoreOre = new WorldGenMinable(
            NMBlocks.denseNetherrackCoreOre.blockID, 3, Block.netherrack.blockID);
    @Unique private static WorldGenOreNode denseCoreNodes = new WorldGenOreNode(
            NMBlocks.denseNetherrackCoreNode.blockID, Block.netherrack.blockID, 1, 1);
    @Unique private static WorldGenMinable deadzoneShardOre = new WorldGenMinable(
            NMBlocks.deadzoneShardOre.blockID, 2, Block.netherrack.blockID).setNeedsAirExposure();
    @Unique private static WorldGenOreNode deadzoneShardNodes = new WorldGenOreNode(
            NMBlocks.deadzoneShardNode.blockID, Block.netherrack.blockID, 1, 1).setNeedsAirExposure();
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

        int tier = NetherTierHelper.getTier(this.worldObj, baseX + 8, baseZ + 8);
        if (tier >= 1) {
            this.generateNodeInChunk(coalNodes, baseX, baseZ, 12);
        }
        if (tier == 2) {
            for (int attempt = 0; attempt < 3; ++attempt) {
                denseCoreOre.generate(this.worldObj, this.hellRNG,
                        baseX + this.hellRNG.nextInt(16), this.hellRNG.nextInt(56) + 4,
                        baseZ + this.hellRNG.nextInt(16));
            }
            if (this.hellRNG.nextInt(24) == 0) {
                this.generateNodeInChunk(denseCoreNodes, baseX, baseZ, 12);
            }
        } else if (tier == 3) {
            deadzoneShardOre.generate(this.worldObj, this.hellRNG,
                    baseX + this.hellRNG.nextInt(16), this.hellRNG.nextInt(56) + 4,
                    baseZ + this.hellRNG.nextInt(16));
            if (this.hellRNG.nextInt(64) == 0) {
                this.generateNodeInChunk(deadzoneShardNodes, baseX, baseZ, 12);
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

    @Unique
    private void generateNodeInChunk(WorldGenOreNode generator, int baseX, int baseZ, int attempts) {
        for (int attempt = 0; attempt < attempts; ++attempt) {
            if (generator.generate(this.worldObj, this.hellRNG,
                    baseX + this.hellRNG.nextInt(16), this.hellRNG.nextInt(56) + 4,
                    baseZ + this.hellRNG.nextInt(16))) {
                return;
            }
        }
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

    @Inject(method = "getPossibleCreatures", at = @At("RETURN"), cancellable = true)
    private void addTieredNetherAmbientSpawns(EnumCreatureType creatureType, int x, int y, int z,
                                              CallbackInfoReturnable<List> cir) {
        if (creatureType != EnumCreatureType.monster || netherDesertTempleGenerator.hasTempleAt(x, y, z)) {
            return;
        }
        int tier = NetherTierHelper.getTier(this.worldObj, x, z);
        if (tier < 2) {
            return;
        }
        List result = new ArrayList(cir.getReturnValue());
        result.add(new SpawnListEntry(EntityCinderPigman.class, 30, 2, 4));
        result.add(new SpawnListEntry(EntityCinderBlaze.class, 10, 1, 2));
        result.add(new SpawnListEntry(EntityAshGhast.class, 5, 1, 1));
        if (tier >= 3) {
            result.add(new SpawnListEntry(EntityDeadzonePigman.class, 20, 1, 3));
            result.add(new SpawnListEntry(EntityHellfireBlaze.class, 8, 1, 2));
            result.add(new SpawnListEntry(EntitySiegeGhast.class, 3, 1, 1));
        }
        cir.setReturnValue(result);
    }
}
