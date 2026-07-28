package com.itlesports.nightmaremode.agriculture;

import btw.block.BTWBlocks;
import btw.block.blocks.CarrotBlockBase;
import btw.block.blocks.HempCropBlock;
import btw.block.blocks.PotatoBlock;
import btw.block.blocks.WheatCropBlock;
import btw.block.blocks.WheatCropTopBlock;
import com.itlesports.nightmaremode.util.interfaces.ChunkAttributesAccess;
import net.minecraft.src.BiomeGenBase;
import net.minecraft.src.Block;
import net.minecraft.src.BlockTallGrass;
import net.minecraft.src.Chunk;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EnumCreatureType;
import net.minecraft.src.MathHelper;
import net.minecraft.src.World;

import java.util.EnumMap;
import java.util.Locale;
import java.util.Random;

public final class ChunkAttributeManager {
    public static final float GROWTH_COST = 1.5F;
    public static final float FERTILIZER_GAIN = 0.25F;
    public static final float FERTILIZER_PENALTY = 0.05F;

    private ChunkAttributeManager() {
    }

    public static ChunkAttributes get(World world, int blockX, int blockZ) {
        return get(world.getChunkFromChunkCoords(blockX >> 4, blockZ >> 4));
    }

    public static ChunkAttributes get(Chunk chunk) {
        ChunkAttributes attributes = ((ChunkAttributesAccess)chunk).nightmareMode$getChunkAttributes();
        if (!attributes.belongsTo(
                chunk.xPosition,
                chunk.zPosition,
                chunk.worldObj.provider.dimensionId
        )) {
            initialize(chunk, attributes);
        }
        return attributes;
    }

    public static void initialize(Chunk chunk) {
        ChunkAttributes attributes = ((ChunkAttributesAccess)chunk).nightmareMode$getChunkAttributes();
        if (!attributes.belongsTo(
                chunk.xPosition,
                chunk.zPosition,
                chunk.worldObj.provider.dimensionId
        )) {
            initialize(chunk, attributes);
        }
    }

    public static boolean canGrow(World world, int x, int z, Block crop) {
        ChunkAttributes attributes = get(world, x, z);
        ChunkAttribute[] requirements = getRequirements(crop);
        if (requirements.length == 0) {
            return true;
        }
        for (ChunkAttribute requirement : requirements) {
            if (attributes.get(requirement) < GROWTH_COST) {
                return false;
            }
        }
        return true;
    }

    public static void consumeForGrowth(World world, int x, int z, Block crop) {
        if (world.isRemote) {
            return;
        }
        ChunkAttributes attributes = get(world, x, z);
        for (ChunkAttribute requirement : getRequirements(crop)) {
            attributes.consume(requirement, GROWTH_COST);
        }
        world.getChunkFromChunkCoords(x >> 4, z >> 4).setChunkModified();
    }

    public static float adjustGrowthChance(float baseChance, World world, int x, int y, int z, Block crop) {
        if (!canGrow(world, x, z, crop)) {
            return 0.0F;
        }

        FarmlandPosition farmland = findFarmland(world, x, y, z);
        if (farmland == null || world.getBlockId(farmland.x, farmland.y, farmland.z) != BTWBlocks.fertilizedFarmland.blockID) {
            return baseChance;
        }
        return baseChance;
    }

    public static boolean hasEffectiveFertilizer(World world, int x, int y, int z, Block crop) {
        FarmlandPosition farmland = findFarmland(world, x, y, z);
        return farmland != null
                && world.getBlockId(farmland.x, farmland.y, farmland.z) == BTWBlocks.fertilizedFarmland.blockID
                && fertilizerFeedsCrop(getFertilizer(world, farmland.x, farmland.y, farmland.z), crop);
    }

    public static boolean applyFertilizer(World world, int x, int y, int z, ChunkAttribute fertilizer) {
        FarmlandPosition farmland = findFarmland(world, x, y, z);
        if (farmland == null) {
            return false;
        }
        if (world.isRemote) {
            return true;
        }

        int metadata = world.getBlockMetadata(farmland.x, farmland.y, farmland.z);
        if (world.getBlockId(farmland.x, farmland.y, farmland.z) != BTWBlocks.fertilizedFarmland.blockID) {
            world.setBlockAndMetadataWithNotify(
                    farmland.x,
                    farmland.y,
                    farmland.z,
                    BTWBlocks.fertilizedFarmland.blockID,
                    metadata
            );
        }

        Chunk chunk = world.getChunkFromChunkCoords(farmland.x >> 4, farmland.z >> 4);
        ChunkAttributes attributes = get(chunk);
        for (ChunkAttribute attribute : ChunkAttribute.values()) {
            attributes.add(attribute, attribute == fertilizer ? FERTILIZER_GAIN : -FERTILIZER_PENALTY);
        }
        attributes.setFarmlandFertilizer(farmland.x & 15, farmland.y, farmland.z & 15, fertilizer);
        chunk.setChunkModified();
        world.playAuxSFX(2005, farmland.x, farmland.y + 1, farmland.z, 0);
        return true;
    }

    public static ChunkAttribute getFertilizer(World world, int x, int y, int z) {
        return get(world, x, z).getFarmlandFertilizer(x & 15, y, z & 15);
    }

    public static void clearFertilizer(World world, int x, int y, int z) {
        if (world.isRemote) {
            return;
        }
        Chunk chunk = world.getChunkFromChunkCoords(x >> 4, z >> 4);
        get(chunk).clearFarmlandFertilizer(x & 15, y, z & 15);
        chunk.setChunkModified();
    }

    public static boolean isSupportedPlant(Block block) {
        return getRequirements(block).length > 0;
    }

    public static String getDebugText(EntityPlayer player) {
        int x = MathHelper.floor_double(player.posX);
        int z = MathHelper.floor_double(player.posZ);
        Chunk chunk = player.worldObj.getChunkFromChunkCoords(x >> 4, z >> 4);
        ChunkAttributes attributes = get(chunk);
        BiomeGenBase biome = chunk.getBiomeGenForWorldCoords(x & 15, z & 15, player.worldObj.getWorldChunkManager());
        StringBuilder text = new StringBuilder();
        text.append("Chunk [").append(chunk.xPosition).append(", ").append(chunk.zPosition)
                .append("] block [").append(x).append(", ").append(z).append("] ")
                .append(biome.biomeName).append(" roll ")
                .append(Long.toHexString(attributes.getRollSeed())).append(": ");
        for (ChunkAttribute attribute : ChunkAttribute.values()) {
            if (attribute.ordinal() > 0) {
                text.append(" | ");
            }
            text.append(attribute.getDisplayName()).append(' ')
                    .append(String.format(Locale.ROOT, "%.2f", attributes.get(attribute)));
        }
        return text.toString();
    }

    private static void initialize(Chunk chunk, ChunkAttributes attributes) {
        BiomeGenBase biome = chunk.getBiomeGenForWorldCoords(8, 8, chunk.worldObj.getWorldChunkManager());
        int height = chunk.getHeightValue(8, 8);
        long rollSeed = createRollSeed(chunk);
        Random random = new Random(rollSeed);
        EnumMap<ChunkAttribute, Float> values = new EnumMap<>(ChunkAttribute.class);
        for (ChunkAttribute attribute : ChunkAttribute.values()) {
            Range range = getRange(attribute, biome, height);
            values.put(attribute, range.min + random.nextFloat() * (range.max - range.min));
        }
        attributes.initialize(
                values,
                chunk.xPosition,
                chunk.zPosition,
                chunk.worldObj.provider.dimensionId,
                rollSeed
        );
        chunk.setChunkModified();
    }

    private static long createRollSeed(Chunk chunk) {
        long seed = chunk.worldObj.getSeed();
        seed ^= (long)chunk.xPosition * 341873128712L;
        seed ^= (long)chunk.zPosition * 132897987541L;
        seed ^= (long)chunk.worldObj.provider.dimensionId * 42317861L;
        seed ^= chunk.worldObj.rand.nextLong();
        seed ^= seed >>> 33;
        seed *= 0xff51afd7ed558ccdl;
        seed ^= seed >>> 33;
        seed *= 0xc4ceb9fe1a85ec53l;
        return seed ^ seed >>> 33;
    }

    private static Range getRange(ChunkAttribute attribute, BiomeGenBase biome, int height) {
        String name = biome.biomeName == null ? "" : biome.biomeName.toLowerCase(Locale.ROOT);
        boolean ocean = name.contains("ocean");
        boolean river = name.contains("river");
        boolean swamp = name.contains("swamp");
        boolean jungle = name.contains("jungle");
        boolean forest = name.contains("forest");
        boolean taiga = name.contains("taiga");
        boolean plains = name.contains("plain");
        boolean desert = name.contains("desert");
        boolean hills = name.contains("hill") || name.contains("mountain");
        boolean animals = biome.getSpawnableList(EnumCreatureType.creature) != null
                && !biome.getSpawnableList(EnumCreatureType.creature).isEmpty();
        float temperature = biome.getFloatTemperature();
        float rainfall = biome.getFloatRainfall();

        return switch (attribute) {
            case MOISTURE -> {
                if (ocean || river || swamp) yield new Range(80, 100);
                if (jungle) yield new Range(72, 96);
                if (desert) yield new Range(4, 18);
                if (forest || taiga) yield new Range(48, 82);
                if (plains) yield new Range(32, 64);
                float center = 20 + rainfall * 65 - Math.max(0, temperature - 0.8F) * 18;
                yield Range.around(center, 16);
            }
            case NITROGEN -> {
                float bonus = (animals ? 10 : 0) + (height >= 90 ? 12 : height >= 70 ? 6 : 0);
                if (swamp) yield new Range(62 + bonus, 88 + bonus);
                if (taiga || hills) yield new Range(50 + bonus, 78 + bonus);
                if (desert) yield new Range(10 + bonus, 30 + bonus);
                yield new Range(30 + bonus, 60 + bonus);
            }
            case POTASSIUM -> {
                if (swamp) yield new Range(68, 96);
                if (desert || hills) yield new Range(58, 88);
                if (ocean || river) yield new Range(18, 42);
                if (forest || taiga) yield new Range(35, 66);
                yield new Range(38, 72);
            }
            case ACIDITY -> {
                if (swamp || jungle) yield new Range(68, 96);
                if (forest || taiga) yield new Range(58, 88);
                if (plains) yield new Range(50, 76);
                if (desert) yield new Range(8, 28);
                float center = 35 + rainfall * 42 + Math.max(0, temperature - 0.7F) * 8;
                yield Range.around(center, 15);
            }
            case POROSITY -> {
                if (swamp || ocean || river) yield new Range(10, 36);
                if (plains) yield new Range(66, 92);
                if (forest || taiga) yield new Range(56, 84);
                if (desert) yield new Range(76, 96);
                if (hills) yield new Range(62, 90);
                yield new Range(40, 72);
            }
        };
    }

    private static ChunkAttribute[] getRequirements(Block crop) {
        if (crop instanceof CarrotBlockBase) {
            return new ChunkAttribute[]{ChunkAttribute.MOISTURE, ChunkAttribute.POTASSIUM};
        }
        if (crop instanceof HempCropBlock) {
            return new ChunkAttribute[]{ChunkAttribute.MOISTURE, ChunkAttribute.ACIDITY};
        }
        if (crop instanceof WheatCropBlock || crop instanceof WheatCropTopBlock) {
            return new ChunkAttribute[]{ChunkAttribute.NITROGEN};
        }
        if (crop instanceof PotatoBlock) {
            return new ChunkAttribute[]{ChunkAttribute.POTASSIUM};
        }
        if (crop instanceof BlockTallGrass) {
            return new ChunkAttribute[]{ChunkAttribute.NITROGEN, ChunkAttribute.POROSITY};
        }
        return new ChunkAttribute[0];
    }

    private static boolean fertilizerFeedsCrop(ChunkAttribute fertilizer, Block crop) {
        if (fertilizer == null) {
            return false;
        }
        for (ChunkAttribute requirement : getRequirements(crop)) {
            if (requirement == fertilizer) {
                return true;
            }
        }
        return false;
    }

    private static FarmlandPosition findFarmland(World world, int x, int y, int z) {
        int blockId = world.getBlockId(x, y, z);
        if (blockId == BTWBlocks.farmland.blockID || blockId == BTWBlocks.fertilizedFarmland.blockID) {
            return new FarmlandPosition(x, y, z);
        }

        Block target = Block.blocksList[blockId];
        if (target == null || !isSupportedPlant(target)) {
            return null;
        }
        for (int offset = 1; offset <= 2; ++offset) {
            int belowId = world.getBlockId(x, y - offset, z);
            if (belowId == BTWBlocks.farmland.blockID || belowId == BTWBlocks.fertilizedFarmland.blockID) {
                return new FarmlandPosition(x, y - offset, z);
            }
        }
        return null;
    }

    private static final class FarmlandPosition {
        private final int x;
        private final int y;
        private final int z;

        private FarmlandPosition(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    private static final class Range {
        private final float min;
        private final float max;

        private Range(float min, float max) {
            this.min = Math.max(0, Math.min(ChunkAttributes.MAX_VALUE, min));
            this.max = Math.max(this.min, Math.min(ChunkAttributes.MAX_VALUE, max));
        }

        private static Range around(float center, float radius) {
            return new Range(center - radius, center + radius);
        }
    }
}
