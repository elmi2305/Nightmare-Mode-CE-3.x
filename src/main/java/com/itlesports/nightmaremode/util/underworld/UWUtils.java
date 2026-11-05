package com.itlesports.nightmaremode.util.underworld;

import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.underworld.BiomeGenUnderworld;
import com.itlesports.nightmaremode.underworld.BiomeGenUnderworld.TerrainProfile;
import com.itlesports.nightmaremode.underworld.ChunkProviderGenerateUnderworld.TerrainGenerator;
import net.minecraft.src.*;

public final class UWUtils {
    private static final ThreadLocal<DirectTransfer> DIRECT_TRANSFER = new ThreadLocal<>();

    private UWUtils() {}

    // keep the normal server transfer bookkeeping while bypassing portal search for explicit destinations.
    public static void transferPlayer(EntityPlayerMP player, int dimension, double x, double y, double z) {
        transferPlayer(player, dimension, x, y, z, false);
    }

    public static void travelPlayer(EntityPlayerMP player, int dimension, double x, double y, double z) {
        transferPlayer(player, dimension, x, y, z, true);
    }

    private static void transferPlayer(EntityPlayerMP player, int dimension, double x, double y, double z, boolean travelEvents) {
        WorldServer target = player.mcServer.worldServerForDimension(dimension);
        if (target == null || target.provider.dimensionId != dimension) throw new IllegalArgumentException("unknown dimension " + dimension);
        if (!Double.isFinite(x) || !Double.isFinite(y) || !Double.isFinite(z)
                || Math.abs(x) >= 29999872 || Math.abs(z) >= 29999872 || y < 1 || y >= target.getHeight() - 1) {
            throw new IllegalArgumentException("invalid dimension transfer destination");
        }
        if (DIRECT_TRANSFER.get() != null) throw new IllegalStateException("nested direct dimension transfer");
        player.closeScreen();
        if (player.ridingEntity != null) player.mountEntity(null);
        if (player.dimension == dimension) {
            player.setPositionAndUpdate(x, y, z);
        } else {
            DIRECT_TRANSFER.set(new DirectTransfer(player, target, x, y, z));
            try {
                if (travelEvents) player.travelToDimension(dimension);
                else player.mcServer.getConfigurationManager().transferPlayerToDimension(player, dimension);
            } finally {
                DIRECT_TRANSFER.remove();
            }
        }
        player.motionX = player.motionY = player.motionZ = 0.0;
        player.fallDistance = 0.0F;
        player.timeUntilPortal = 100;
    }

    public static boolean placeDirectTransfer(Entity entity, WorldServer target) {
        DirectTransfer transfer = DIRECT_TRANSFER.get();
        if (transfer == null || transfer.player != entity || transfer.world != target) return false;
        entity.setWorld(target);
        entity.setLocationAndAngles(transfer.x, transfer.y, transfer.z, entity.rotationYaw, entity.rotationPitch);
        if (entity.isEntityAlive()) {
            target.spawnEntityInWorld(entity);
            target.updateEntityWithOptionalForce(entity, false);
        }
        return true;
    }

    public static boolean isSafeStandingPosition(World world, int x, int y, int z) {
        if (y < 1 || y >= world.getHeight() - 1) return false;
        int groundId = world.getBlockId(x, y - 1, z);
        Block ground = Block.blocksList[groundId];
        return ground != null && ground.blockMaterial.blocksMovement() && !ground.blockMaterial.isLiquid()
                && ground.blockMaterial != Material.leaves && groundId != Block.cactus.blockID
                && world.isAirBlock(x, y, z) && world.isAirBlock(x, y + 1, z);
    }

    public static ChunkCoordinates findSafePosition(World world, int x, int preferredY, int z) {
        int y = MathHelper.clamp_int(preferredY, 1, world.getHeight() - 2);
        for (int distance = 0; distance < world.getHeight(); distance++) {
            if (isSafeStandingPosition(world, x, y + distance, z)) return new ChunkCoordinates(x, y + distance, z);
            if (distance > 0 && isSafeStandingPosition(world, x, y - distance, z)) return new ChunkCoordinates(x, y - distance, z);
        }
        return null;
    }

    public static boolean isBlightlandsLanding(World world, int x, int z) {
        for (int dx : new int[]{-3, 0, 3}) for (int dz : new int[]{-3, 0, 3}) {
            if (world.getBiomeGenForCoords(x + dx, z + dz) != BiomeGenUnderworld.blightlands) return false;
        }
        return true;
    }

    public static ChunkCoordinates findBlightlandsLanding(WorldServer world, int originX, int originZ) {
        WorldChunkManager manager = world.getWorldChunkManager();
        int centerX = (MathHelper.clamp_int(originX, -29995000, 29995000) & ~15) + 8;
        int centerZ = (MathHelper.clamp_int(originZ, -29995000, 29995000) & ~15) + 8;
        int checkedChunks = 0;
        for (int radius = 0; radius <= 4096; radius += 64) {
            for (int dx = -radius; dx <= radius; dx += 64) {
                for (int dz = -radius; dz <= radius; dz += 64) {
                    if (radius > 0 && Math.abs(dx) != radius && Math.abs(dz) != radius) continue;
                    int x = centerX + dx, z = centerZ + dz;
                    // biome-map queries do not generate terrain; only validate promising chunks.
                    if (manager.getBiomeGenAt(x, z) != BiomeGenUnderworld.blightlands
                            || manager.getBiomeGenAt(x - 3, z - 3) != BiomeGenUnderworld.blightlands
                            || manager.getBiomeGenAt(x + 3, z + 3) != BiomeGenUnderworld.blightlands) continue;
                    if (++checkedChunks > 64) return null;
                    if (!isBlightlandsLanding(world, x, z)) continue;
                    // this API already returns the first air block above the ground.
                    for (int y = Math.min(world.getHeight() - 2, world.getTopSolidOrLiquidBlock(x, z)); y > 1; y--) {
                        int floor = world.getBlockId(x, y - 1, z);
                        if (floor != NMBlocks.underGrass.blockID && floor != NMBlocks.underFlowerDirts.blockID
                                && floor != NMBlocks.underCobble.blockID && floor != NMBlocks.understoneSmooth.blockID) continue;
                        if (isSafeStandingPosition(world, x, y, z)) return new ChunkCoordinates(x, y, z);
                    }
                }
            }
        }
        return null;
    }

    public static void sampleTerrain(World world, TerrainGenerator sampler, int chunkX, int chunkZ) {
        int count = TerrainGenerator.BIOME_SAMPLES;
        int radius = TerrainGenerator.BIOME_BLEND_RADIUS;
        BiomeGenBase[] biomes = world.getWorldChunkManager().getBiomesForGeneration(
                null, chunkX * 4 - radius, chunkZ * 4 - radius, count, count);
        TerrainProfile[] profiles = new TerrainProfile[count * count];
        for (int i = 0; i < profiles.length; i++) {
            profiles[i] = biomes[i] instanceof BiomeGenUnderworld
                    ? ((BiomeGenUnderworld)biomes[i]).getTerrainProfile() : TerrainProfile.BLIGHTLANDS;
        }
        sampler.sampleDensity(chunkX, chunkZ, profiles);
    }

    private record DirectTransfer(EntityPlayerMP player, WorldServer world, double x, double y, double z) {}
}
