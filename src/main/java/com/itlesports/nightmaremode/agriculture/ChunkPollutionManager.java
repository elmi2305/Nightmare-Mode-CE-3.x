package com.itlesports.nightmaremode.agriculture;

import btw.block.BTWBlocks;
import btw.block.blocks.AestheticOpaqueEarthBlock;
import btw.block.blocks.GearBoxBlock;
import btw.block.blocks.MillstoneBlock;
import btw.block.blocks.SawBlock;
import btw.block.tileentity.MillstoneTileEntity;
import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.block.blocks.BlockBloodSaw;
import com.itlesports.nightmaremode.block.tileEntities.CisternDrainTileEntity;
import com.itlesports.nightmaremode.block.tileEntities.MinerDrillTileEntity;
import com.itlesports.nightmaremode.block.tileEntities.ObsidianMillstoneTileEntity;
import com.itlesports.nightmaremode.block.tileEntities.TerrainExtractorTileEntity;
import com.itlesports.nightmaremode.util.interfaces.ChunkAttributesAccess;
import net.minecraft.src.Block;
import net.minecraft.src.Chunk;
import net.minecraft.src.ChunkCoordIntPair;
import net.minecraft.src.MathHelper;
import net.minecraft.src.World;
import net.minecraft.src.TileEntity;

/** Persistent, non-random industrial pollution for actively ticking chunks. */
public final class ChunkPollutionManager {
    public static final float GRASS_STOPS_SPREADING = 1000.0F;
    public static final float GRASS_DECAYS = 1800.0F;
    public static final float BLIGHT_STARTS = 2600.0F;
    public static final float BIOLOGICAL_DAMAGE = 4000.0F;
    public static final float GEARBOX_FAILURE = 7000.0F;
    public static final float PASSIVE_DRAIN_PER_TICK = 0.0125F;
    private static final float NEIGHBOR_SHARE = 0.15F;

    private ChunkPollutionManager() {}

    public static float get(World world, int x, int z) {
        return ChunkAttributeManager.get(world, x, z).getPollution();
    }

    public static boolean isAtLeast(World world, int x, int z, float threshold) {
        return get(world, x, z) >= threshold;
    }

    /** Adds pollution only when the source can reach the surface in the overworld. */
    public static void pollute(World world, int x, int y, int z, float amount) {
        if (world == null || world.isRemote || amount <= 0.0F || !canReachSurface(world, x, y, z)) return;
        addToChunk(world, x >> 4, z >> 4, amount);
        float spill = amount * NEIGHBOR_SHARE;
        if (spill <= 0.0F) return;
        for (int dx = -1; dx <= 1; ++dx) {
            for (int dz = -1; dz <= 1; ++dz) {
                if ((dx == 0 && dz == 0) || (dx != 0 && dz != 0)) continue;
                int chunkX = (x >> 4) + dx;
                int chunkZ = (z >> 4) + dz;
                if (world.isChunkActive(chunkX, chunkZ)) addToChunk(world, chunkX, chunkZ, spill);
            }
        }
    }

    public static void tickLoadedChunks(World world) {
        for (ChunkCoordIntPair coords : world.getActiveChunksCoordsList()) {
            if (!world.isChunkActive(coords.chunkXPos, coords.chunkZPos)) continue;
            Chunk chunk = world.getChunkFromChunkCoords(coords.chunkXPos, coords.chunkZPos);
            ChunkAttributes attributes = ((ChunkAttributesAccess)chunk).nightmareMode$getChunkAttributes();
            if (!attributes.belongsTo(chunk.xPosition, chunk.zPosition, world.provider.dimensionId)) continue;
            if (attributes.getPollution() <= 0.0F) continue;
            attributes.addPollution(-PASSIVE_DRAIN_PER_TICK * 20.0F);
            chunk.setChunkModified();
            if (attributes.getPollution() >= BLIGHT_STARTS && world.rand.nextInt(20) == 0) {
                seedBlight(world, chunk.xPosition * 16 + 8, 64, chunk.zPosition * 16 + 8);
            }
        }
    }

    public static void tickBlight(World world, int x, int y, int z) {
        if (world.isRemote) return;
        int meta = world.getBlockMetadata(x, y, z);
        if (meta < 0 || meta > 5) return;
        float pollution = get(world, x, z);
        if (pollution < BLIGHT_STARTS) {
            if (meta == 0) world.setBlockWithNotify(x, y, z, Block.dirt.blockID);
            else if (meta == 1 || meta == 3) world.setBlockAndMetadataWithNotify(x, y, z, BTWBlocks.aestheticEarth.blockID, 0);
            else world.setBlockAndMetadataWithNotify(x, y, z, BTWBlocks.aestheticEarth.blockID, 1);
            return;
        }

        int stage = pollution >= GEARBOX_FAILURE ? 1 : 0;
        if (meta < stage) world.setBlockMetadataWithNotify(x, y, z, stage);
        if (world.rand.nextInt(3) != 0) return;
        int targetX = x + world.rand.nextInt(3) - 1;
        int targetY = y + world.rand.nextInt(3) - 1;
        int targetZ = z + world.rand.nextInt(3) - 1;
        int targetId = world.getBlockId(targetX, targetY, targetZ);
        if ((targetId == Block.dirt.blockID || targetId == Block.grass.blockID)
                && Block.lightOpacity[world.getBlockId(targetX, targetY + 1, targetZ)] <= 2) {
            world.setBlockAndMetadataWithNotify(targetX, targetY, targetZ, BTWBlocks.aestheticEarth.blockID, stage);
            // Blight carries a little contamination across a chunk boundary.
            if ((targetX >> 4) != (x >> 4) || (targetZ >> 4) != (z >> 4)) {
                pollute(world, targetX, targetY, targetZ, 35.0F);
            }
        }
    }

    public static void seedBlight(World world, int x, int y, int z) {
        if (world.isRemote || world.provider.dimensionId != 0 || get(world, x, z) < BLIGHT_STARTS) return;
        for (int attempt = 0; attempt < 6; ++attempt) {
            int targetX = x + world.rand.nextInt(17) - 8;
            int targetZ = z + world.rand.nextInt(17) - 8;
            int targetY = world.getHeightValue(targetX, targetZ) - 1;
            int id = world.getBlockId(targetX, targetY, targetZ);
            if (id == Block.dirt.blockID || id == Block.grass.blockID) {
                world.setBlockAndMetadataWithNotify(targetX, targetY, targetZ, BTWBlocks.aestheticEarth.blockID, 0);
                return;
            }
        }
    }

    public static void affectGearbox(World world, int x, int y, int z, GearBoxBlock gearbox) {
        float pollution = get(world, x, z);
        if (pollution >= BIOLOGICAL_DAMAGE && world.rand.nextInt(3) == 0) {
            world.spawnParticle("largesmoke", x + 0.5D, y + 0.8D, z + 0.5D, 0.0D, 0.03D, 0.0D);
        }
        if (pollution >= GEARBOX_FAILURE && world.rand.nextInt(80) == 0) gearbox.breakGearBox(world, x, y, z);
    }

    /** Human-readable source rate for the mechanical wrench. */
    public static String getSourceDescription(World world, int x, int y, int z) {
        Block block = Block.blocksList[world.getBlockId(x, y, z)];
        TileEntity tile = world.getBlockTileEntity(x, y, z);
        if (block instanceof MillstoneBlock && tile instanceof MillstoneTileEntity millstone) {
            if (!millstone.isGrinding()) return "Pollution: idle";
            float amount = millstone.stackMilling != null && millstone.stackMilling.itemID == Block.netherrack.blockID ? 12.0F : 2.0F;
            if (millstone instanceof ObsidianMillstoneTileEntity) amount *= 0.3F;
            return String.format("Pollution: %.1f / second while milling", amount);
        }
        if (block instanceof BlockBloodSaw) return "Pollution: 0 / block | 10 / creature killed";
        if (block instanceof SawBlock) return "Pollution: 6 / block | 45 / creature killed";
        if (tile instanceof MinerDrillTileEntity drill) {
            float amount = switch (drill.getMachineTier()) { case 1 -> 90.0F; case 2 -> 55.0F; case 3 -> 25.0F; default -> 0.0F; };
            return String.format("Pollution: %.0f / coal consumed", amount);
        }
        if (tile instanceof TerrainExtractorTileEntity) return "Pollution: 70 / extraction completed";
        if (tile instanceof CisternDrainTileEntity) return "Pollution: 2-50 / fluid drain";
        if (block == Block.fire) return "Pollution: 8 / flammable block burned (18 on hibachi)";
        if (block == BTWBlocks.stokedFire) return "Pollution: 30 / flammable block burned";
        return null;
    }

    private static boolean canReachSurface(World world, int x, int y, int z) {
        return world.provider.dimensionId != 0 || y >= 40 || world.canBlockSeeTheSky(x, y, z);
    }

    private static void addToChunk(World world, int chunkX, int chunkZ, float amount) {
        Chunk chunk = world.getChunkFromChunkCoords(chunkX, chunkZ);
        ChunkAttributeManager.get(chunk).addPollution(amount);
        chunk.setChunkModified();
    }
}
