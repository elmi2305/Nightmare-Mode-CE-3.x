package com.itlesports.nightmaremode.mixin.biomegen;

import btw.community.nightmaremode.NightmareMode;
import btw.entity.mob.villager.PriestVillagerEntity;
import com.itlesports.nightmaremode.agriculture.ChunkAttributeManager;
import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.structure.MapGenOceanDesertTemple;
import com.itlesports.nightmaremode.structure.MapGenSkyZiggurath;
import com.itlesports.nightmaremode.worldgen.OverworldTierHelper;
import net.minecraft.src.Block;
import net.minecraft.src.Chunk;
import net.minecraft.src.ChunkProviderGenerate;
import net.minecraft.src.EnumCreatureType;
import net.minecraft.src.IChunkProvider;
import net.minecraft.src.MathHelper;
import net.minecraft.src.World;
import net.minecraft.src.WorldGenLakes;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Random;
import java.util.List;

@Mixin(ChunkProviderGenerate.class)
public class ChunkProviderGenerateMixin {
    @Unique private static Random rand = new Random();
    @Unique private final MapGenOceanDesertTemple oceanDesertTempleGenerator = new MapGenOceanDesertTemple();
    @Unique private final MapGenSkyZiggurath skyZiggurathGenerator = new MapGenSkyZiggurath();
    @Shadow private World worldObj;
    @Shadow private Random structureRand;

    @Inject(method = "initializeNoiseField", at = @At("RETURN"))
    private void warpDeadzoneDensity(double[] densities, int startX, int startY, int startZ,
                                     int sizeX, int sizeY, int sizeZ,
                                     CallbackInfoReturnable<double[]> cir) {
        if (this.worldObj == null || this.worldObj.provider.dimensionId != 0) return;
        double[] result = cir.getReturnValue();
        int index = 0;
        for (int localX = 0; localX < sizeX; ++localX) {
            double worldX = (startX + localX) * 4.0D;
            for (int localZ = 0; localZ < sizeZ; ++localZ) {
                double worldZ = (startZ + localZ) * 4.0D;
                if (OverworldTierHelper.getRegion(this.worldObj, worldX, worldZ) == OverworldTierHelper.Region.DEADZONE) {
                    double progress = OverworldTierHelper.smoothstep(
                            OverworldTierHelper.getDeadzoneWarpProgress(this.worldObj, worldX, worldZ));
                    double broad = valueNoise2D(worldX, worldZ, 360.0D, 0xD34D20AEL);
                    double detail = valueNoise2D(worldX, worldZ, 78.0D, 0x57415250L);
                    double warp = (broad * 22.0D + detail * 7.0D) * progress;
                    for (int y = 0; y < sizeY; ++y) result[index++] += warp;
                } else {
                    index += sizeY;
                }
            }
        }
    }

    @Inject(method = "generateTerrain", at = @At("TAIL"))
    private void createOuterTerrain(int chunkX, int chunkZ, short[] blockIDs, byte[] metadata, CallbackInfo ci) {
        overwriteOuterTerrain(chunkX, chunkZ, blockIDs, metadata, false);
    }

    @Inject(method = "provideChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/Chunk;<init>(Lnet/minecraft/src/World;[S[BII)V"),
            locals = LocalCapture.CAPTURE_FAILHARD)
    private void eraseVanillaOuterCaves(int chunkX, int chunkZ, CallbackInfoReturnable<Chunk> cir,
                                        short[] blockIDs, byte[] metadata) {
        overwriteOuterTerrain(chunkX, chunkZ, blockIDs, metadata, true);
    }

    @Inject(method = "populate", at = @At("HEAD"), cancellable = true)
    private void suppressOuterDecoration(IChunkProvider provider, int chunkX, int chunkZ, CallbackInfo ci) {
        OverworldTierHelper.Region region = OverworldTierHelper.getRegion(this.worldObj, chunkX * 16 + 8, chunkZ * 16 + 8);
        if (region == OverworldTierHelper.Region.CRUEL_DESERT
                || region == OverworldTierHelper.Region.GREAT_VOID
                || region == OverworldTierHelper.Region.LOST_OCEAN) {
            ci.cancel();
        }
    }

    @Redirect(method = "populate", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/WorldGenLakes;generate(Lnet/minecraft/src/World;Ljava/util/Random;III)Z"))
    private boolean suppressDeadzonePools(WorldGenLakes generator, World world, Random random, int x, int y, int z) {
        OverworldTierHelper.Region region = OverworldTierHelper.getRegion(world, x, z);
        if (region == OverworldTierHelper.Region.DEADZONE || region == OverworldTierHelper.Region.CRUEL_DESERT) {
            return false;
        }
        return generator.generate(world, random, x, y, z);
    }
    @Redirect(method = "generateTerrain", at = @At(value = "FIELD", target = "Lnet/minecraft/src/Block;waterStill:Lnet/minecraft/src/Block;", opcode = Opcodes.GETSTATIC))
    private Block funnyLavaOcean(){
        if(NightmareMode.isAprilFools && rand.nextInt(8) == 0){
            return Block.lavaStill;
        }
        return Block.waterStill;
    }

    @Inject(method = "generateAdditionalBrownMushrooms", at = @At("HEAD"), cancellable = true)
    private void cancelOverworld(World worldObj, int iChunkX, int iChunkZ, CallbackInfo ci){
        if(worldObj.provider.dimensionId == 0) {
            ci.cancel();
        }
    }

    @Inject(method = "provideChunk", at = @At("RETURN"))
    private void initializeChunkAttributes(int chunkX, int chunkZ, CallbackInfoReturnable<Chunk> cir) {
        ChunkAttributeManager.initialize(cir.getReturnValue());
    }

    @Inject(method = "provideChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/MapGenVillage;generate(Lnet/minecraft/src/IChunkProvider;Lnet/minecraft/src/World;II[S[B)V"))
    private void prepareOceanDesertTemples(int chunkX, int chunkZ, CallbackInfoReturnable<Chunk> cir) {
        oceanDesertTempleGenerator.generate((ChunkProviderGenerate) (Object) this, worldObj, chunkX, chunkZ, null, null);
        skyZiggurathGenerator.generate((ChunkProviderGenerate) (Object) this, worldObj, chunkX, chunkZ, null, null);
    }

    @Inject(method = "populate",  at = @At(value = "INVOKE", target = "Lnet/minecraft/src/MapGenMineshaft;generateStructuresInChunk(Lnet/minecraft/src/World;Ljava/util/Random;II)Z"))
    private void generateOceanDesertTemples(IChunkProvider provider, int chunkX, int chunkZ, CallbackInfo ci) {
        oceanDesertTempleGenerator.generateStructuresInChunk(worldObj, structureRand, chunkX, chunkZ);
        skyZiggurathGenerator.generateStructuresInChunk(worldObj, structureRand, chunkX, chunkZ);
    }

    @Inject(method = "getPossibleCreatures", at = @At("HEAD"), cancellable = true)
    private void useOceanTempleSpawnTable(EnumCreatureType creatureType, int x, int y, int z, CallbackInfoReturnable<List> cir) {
        if (creatureType == EnumCreatureType.monster && oceanDesertTempleGenerator.hasTempleAt(x, y, z)) {
            cir.setReturnValue(oceanDesertTempleGenerator.getSpawnList());
        }
    }

    @Unique
    private void overwriteOuterTerrain(int chunkX, int chunkZ, short[] blocks, byte[] metadata, boolean afterCarvers) {
        if (this.worldObj == null || this.worldObj.provider.dimensionId != 0) return;
        for (int localX = 0; localX < 16; ++localX) {
            int worldX = chunkX * 16 + localX;
            for (int localZ = 0; localZ < 16; ++localZ) {
                int worldZ = chunkZ * 16 + localZ;
                OverworldTierHelper.Region region = OverworldTierHelper.getRegion(this.worldObj, worldX, worldZ);
                if (afterCarvers && region == OverworldTierHelper.Region.DEADZONE) {
                    blendDeadzoneSurface(blocks, metadata, localX, localZ, worldX, worldZ);
                    continue;
                }
                if (region == OverworldTierHelper.Region.INNER || region == OverworldTierHelper.Region.DEADZONE
                        || region == OverworldTierHelper.Region.BEYOND) continue;
                if (!afterCarvers && region != OverworldTierHelper.Region.FROZEN_WASTES) continue;
                if (afterCarvers && region == OverworldTierHelper.Region.FROZEN_WASTES) continue;

                double distance = OverworldTierHelper.getDistanceFromSpawn(this.worldObj, worldX, worldZ);
                if (region == OverworldTierHelper.Region.CRUEL_DESERT) {
                    int sourceHeight = findSurfaceHeight(blocks, localX, localZ);
                    int desertHeight = 70 + (int)Math.round(valueNoise2D(worldX, worldZ, 190.0D, 0x44554E45L) * 9.0D
                            + valueNoise2D(worldX, worldZ, 42.0D, 0x53414E44L) * 3.0D);
                    double transition = OverworldTierHelper.smoothstep((distance - OverworldTierHelper.CRUEL_DESERT_START)
                            / OverworldTierHelper.SURFACE_BLEND_LENGTH);
                    int height = (int)Math.round(sourceHeight + (desertHeight - sourceHeight) * transition);
                    writeDesertColumn(blocks, metadata, localX, localZ, worldX, worldZ, height, transition);
                } else if (region == OverworldTierHelper.Region.GREAT_VOID) {
                    int edgeHeight = 70 + (int)Math.round(valueNoise2D(worldX, worldZ, 190.0D, 0x44554E45L) * 9.0D
                            + valueNoise2D(worldX, worldZ, 42.0D, 0x53414E44L) * 3.0D);
                    double descent = OverworldTierHelper.smoothstep((distance - OverworldTierHelper.GREAT_VOID_START)
                            / OverworldTierHelper.VOID_SLOPE_LENGTH);
                    int height = (int)Math.round(edgeHeight * (1.0D - descent));
                    writeVoidColumn(blocks, metadata, localX, localZ, height);
                } else if (region == OverworldTierHelper.Region.LOST_OCEAN) {
                    double rise = OverworldTierHelper.smoothstep((distance - OverworldTierHelper.LOST_OCEAN_START)
                            / OverworldTierHelper.OCEAN_SLOPE_LENGTH);
                    writeOceanColumn(blocks, metadata, localX, localZ, worldX, worldZ, rise);
                } else if (region == OverworldTierHelper.Region.FROZEN_WASTES) {
                    int height = 88 + (int)Math.round(valueNoise2D(worldX, worldZ, 240.0D, 0x46524F5354L) * 20.0D
                            + Math.abs(valueNoise2D(worldX, worldZ, 68.0D, 0x5045414BL)) * 12.0D);
                    double transition = OverworldTierHelper.smoothstep((distance - OverworldTierHelper.FROZEN_WASTES_START)
                            / OverworldTierHelper.FROZEN_SLOPE_LENGTH);
                    writeOceanToFrozenColumn(blocks, metadata, localX, localZ, worldX, worldZ,
                            Math.min(122, height), transition);
                }
            }
        }
    }

    @Unique
    private static void clearColumn(short[] blocks, byte[] metadata, int localX, int localZ) {
        int base = (localX * 16 + localZ) * 128;
        for (int y = 0; y < 128; ++y) {
            blocks[base + y] = 0;
            metadata[base + y] = 0;
        }
    }

    @Unique
    private void writeDesertColumn(short[] blocks, byte[] metadata, int localX, int localZ,
                                   int worldX, int worldZ, int height, double transition) {
        clearColumn(blocks, metadata, localX, localZ);
        int base = (localX * 16 + localZ) * 128;
        blocks[base] = (short)Block.bedrock.blockID;
        boolean desertSurface = (hashNoise(worldX, 0, worldZ, 0x424C454E44L) + 1.0D) * 0.5D <= transition;
        for (int y = 1; y <= height && y < 128; ++y) {
            if (y == height) {
                blocks[base + y] = (short)(desertSurface ? Block.sand.blockID : NMBlocks.underGrass.blockID);
            } else if (y >= height - 4) {
                blocks[base + y] = (short)(desertSurface ? Block.sandStone.blockID : NMBlocks.underFlowerDirts.blockID);
                if (!desertSurface) metadata[base + y] = (byte)NMBlocks.META_UNDER_DIRT;
            } else {
                blocks[base + y] = (short)Block.stone.blockID;
            }
        }
    }

    @Unique
    private static void writeVoidColumn(short[] blocks, byte[] metadata, int localX, int localZ, int height) {
        clearColumn(blocks, metadata, localX, localZ);
        int base = (localX * 16 + localZ) * 128;
        for (int y = 0; y < height && y < 128; ++y) blocks[base + y] = (short)Block.stone.blockID;
    }

    @Unique
    private void writeOceanColumn(short[] blocks, byte[] metadata, int localX, int localZ,
                                  int worldX, int worldZ, double rise) {
        clearColumn(blocks, metadata, localX, localZ);
        int base = (localX * 16 + localZ) * 128;
        int waterTop = (int)Math.round(100.0D * rise);
        double floor = rise * (34.0D + valueNoise2D(worldX, worldZ, 210.0D, 0x4F4345414EL) * 20.0D);
        for (int y = 0; y < 128; ++y) {
            double archNoise = valueNoise3D(worldX, y, worldZ, 36.0D, 0x415243484553L);
            double fineNoise = valueNoise3D(worldX, y, worldZ, 15.0D, 0x434156495459L);
            double density = floor - y + archNoise * 15.0D + fineNoise * 3.5D;
            if (rise > 0.02D && (y == 0 || density > 0.0D)) {
                blocks[base + y] = (short)(y == 0 ? Block.bedrock.blockID : Block.stone.blockID);
            } else if (y <= waterTop) {
                blocks[base + y] = (short)Block.waterStill.blockID;
            }
        }
        for (int y = 126; y > 1; --y) {
            if (blocks[base + y] == Block.stone.blockID && blocks[base + y + 1] == Block.waterStill.blockID) {
                blocks[base + y] = (short)Block.gravel.blockID;
                break;
            }
        }
    }

    @Unique
    private static void writeFrozenColumn(short[] blocks, byte[] metadata, int localX, int localZ, int height) {
        clearColumn(blocks, metadata, localX, localZ);
        int base = (localX * 16 + localZ) * 128;
        blocks[base] = (short)Block.bedrock.blockID;
        for (int y = 1; y <= height && y < 128; ++y) {
            if (y == height) blocks[base + y] = (short)Block.grass.blockID;
            else if (y >= height - 3) blocks[base + y] = (short)Block.dirt.blockID;
            else blocks[base + y] = (short)Block.stone.blockID;
        }
    }

    @Unique
    private void writeOceanToFrozenColumn(short[] blocks, byte[] metadata, int localX, int localZ,
                                          int worldX, int worldZ, int frozenHeight, double transition) {
        clearColumn(blocks, metadata, localX, localZ);
        int base = (localX * 16 + localZ) * 128;
        double oceanFloor = 34.0D + valueNoise2D(worldX, worldZ, 210.0D, 0x4F4345414EL) * 20.0D;
        double floor = oceanFloor + (frozenHeight - oceanFloor) * transition;
        int waterTop = (int)Math.round(100.0D + (floor - 100.0D) * transition);
        for (int y = 0; y < 128; ++y) {
            double archNoise = valueNoise3D(worldX, y, worldZ, 36.0D, 0x415243484553L) * (1.0D - transition);
            double fineNoise = valueNoise3D(worldX, y, worldZ, 15.0D, 0x434156495459L) * (1.0D - transition);
            double density = floor - y + archNoise * 15.0D + fineNoise * 3.5D;
            if (y == 0 || density > 0.0D) {
                blocks[base + y] = (short)(y == 0 ? Block.bedrock.blockID : Block.stone.blockID);
            } else if (y <= waterTop) {
                blocks[base + y] = (short)Block.waterStill.blockID;
            }
        }
        for (int y = 126; y > 1; --y) {
            if (blocks[base + y] == Block.stone.blockID && blocks[base + y + 1] == 0) {
                blocks[base + y] = (short)Block.grass.blockID;
                for (int depth = 1; depth <= 3 && y - depth > 0; ++depth) {
                    blocks[base + y - depth] = (short)Block.dirt.blockID;
                }
                break;
            }
            if (blocks[base + y] == Block.stone.blockID && blocks[base + y + 1] == Block.waterStill.blockID) {
                blocks[base + y] = (short)Block.gravel.blockID;
                break;
            }
        }
    }

    @Unique
    private static int findSurfaceHeight(short[] blocks, int localX, int localZ) {
        int base = (localX * 16 + localZ) * 128;
        for (int y = 127; y > 0; --y) {
            int blockId = blocks[base + y];
            if (blockId != 0 && blockId != Block.waterMoving.blockID && blockId != Block.waterStill.blockID
                    && blockId != Block.lavaMoving.blockID && blockId != Block.lavaStill.blockID) return y;
        }
        return 64;
    }

    @Unique
    private void blendDeadzoneSurface(short[] blocks, byte[] metadata, int localX, int localZ, int worldX, int worldZ) {
        double distance = OverworldTierHelper.getDistanceFromSpawn(this.worldObj, worldX, worldZ);
        double transition = OverworldTierHelper.smoothstep((distance - OverworldTierHelper.DEADZONE_START)
                / OverworldTierHelper.SURFACE_BLEND_LENGTH);
        double sample = (hashNoise(worldX, 0, worldZ, 0x44454144424C454EL) + 1.0D) * 0.5D;
        if (sample <= transition) return;
        int base = (localX * 16 + localZ) * 128;
        for (int y = 127; y > 0; --y) {
            if (blocks[base + y] == NMBlocks.underGrass.blockID) {
                blocks[base + y] = (short)Block.grass.blockID;
                metadata[base + y] = 0;
                for (int depth = 1; depth <= 3 && y - depth > 0; ++depth) {
                    if (blocks[base + y - depth] == NMBlocks.underFlowerDirts.blockID) {
                        blocks[base + y - depth] = (short)Block.dirt.blockID;
                        metadata[base + y - depth] = 0;
                    }
                }
                return;
            }
            if (blocks[base + y] != 0) return;
        }
    }

    @Unique
    private double valueNoise2D(double x, double z, double scale, long salt) {
        double sx = x / scale;
        double sz = z / scale;
        int x0 = MathHelper.floor_double(sx);
        int z0 = MathHelper.floor_double(sz);
        double tx = fade(sx - x0);
        double tz = fade(sz - z0);
        double a = lerp(tx, hashNoise(x0, 0, z0, salt), hashNoise(x0 + 1, 0, z0, salt));
        double b = lerp(tx, hashNoise(x0, 0, z0 + 1, salt), hashNoise(x0 + 1, 0, z0 + 1, salt));
        return lerp(tz, a, b);
    }

    @Unique
    private double valueNoise3D(double x, double y, double z, double scale, long salt) {
        double sx = x / scale, sy = y / scale, sz = z / scale;
        int x0 = MathHelper.floor_double(sx), y0 = MathHelper.floor_double(sy), z0 = MathHelper.floor_double(sz);
        double tx = fade(sx - x0), ty = fade(sy - y0), tz = fade(sz - z0);
        double lowerA = lerp(tx, hashNoise(x0, y0, z0, salt), hashNoise(x0 + 1, y0, z0, salt));
        double lowerB = lerp(tx, hashNoise(x0, y0, z0 + 1, salt), hashNoise(x0 + 1, y0, z0 + 1, salt));
        double upperA = lerp(tx, hashNoise(x0, y0 + 1, z0, salt), hashNoise(x0 + 1, y0 + 1, z0, salt));
        double upperB = lerp(tx, hashNoise(x0, y0 + 1, z0 + 1, salt), hashNoise(x0 + 1, y0 + 1, z0 + 1, salt));
        return lerp(ty, lerp(tz, lowerA, lowerB), lerp(tz, upperA, upperB));
    }

    @Unique private static double fade(double value) { return value * value * (3.0D - 2.0D * value); }
    @Unique private static double lerp(double t, double a, double b) { return a + (b - a) * t; }

    @Unique
    private double hashNoise(int x, int y, int z, long salt) {
        long value = this.worldObj.getSeed() ^ salt;
        value ^= (long)x * 341873128712L;
        value ^= (long)y * 42317861L;
        value ^= (long)z * 132897987541L;
        value ^= value >>> 33;
        value *= 0xff51afd7ed558ccdL;
        value ^= value >>> 33;
        return ((value & 0x1fffffffffffffL) / (double)0x10000000000000L) - 1.0D;
    }
}
