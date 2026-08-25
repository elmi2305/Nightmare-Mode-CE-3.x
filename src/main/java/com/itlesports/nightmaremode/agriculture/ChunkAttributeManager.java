package com.itlesports.nightmaremode.agriculture;

import btw.block.BTWBlocks;
import btw.block.blocks.CarrotBlockBase;
import btw.block.blocks.HempCropBlock;
import btw.block.blocks.PotatoBlock;
import btw.block.blocks.WheatCropBlock;
import btw.block.blocks.WheatCropTopBlock;
import com.itlesports.nightmaremode.util.interfaces.ChunkAttributesAccess;
import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.worldgen.NetherTierHelper;
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
    public static final float GENERAL_GROWTH_COST = 0.05F;
    public static final float PREFERRED_GROWTH_COST = 0.45F;
    public static final float MIN_PREFERRED_RESOURCE_TO_GROW = 2.5F;
    public static final float FERTILIZER_GAIN = 0.25F;
    public static final float FERTILIZER_PENALTY = 0.35F;
    public static final long FERTILIZER_DURATION = 24000L;

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
        } else if (!attributes.hasFishData()) {
            initializeFish(chunk, attributes);
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
            if (attributes.get(requirement) < MIN_PREFERRED_RESOURCE_TO_GROW) {
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
        ChunkAttribute[] requirements = getRequirements(crop);
        for (ChunkAttribute attribute : ChunkAttribute.values()) {
            float amount = GENERAL_GROWTH_COST;
            if (contains(requirements, attribute)) {
                amount += PREFERRED_GROWTH_COST;
            }
            attributes.consume(attribute, amount);
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
        FarmlandPosition farmland = findFarmlandForApplication(world, x, y, z);
        if (farmland == null) {
            return false;
        }
        if (world.isRemote) {
            return world.getBlockId(farmland.x, farmland.y, farmland.z) != BTWBlocks.fertilizedFarmland.blockID;
        }

        expireFertilizer(world, farmland.x, farmland.y, farmland.z);
        if (hasActiveFertilizer(world, farmland.x, farmland.y, farmland.z)) {
            return false;
        }

        int metadata = world.getBlockMetadata(farmland.x, farmland.y, farmland.z);
        int farmlandId = world.getBlockId(farmland.x, farmland.y, farmland.z);
        if (farmlandId != BTWBlocks.farmland.blockID && farmlandId != NMBlocks.netherFarmland.blockID) {
            return false;
        }
        if (farmlandId == BTWBlocks.farmland.blockID) {
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
            float before = attributes.get(attribute);
            attributes.add(attribute, attribute == fertilizer ? FERTILIZER_GAIN : -FERTILIZER_PENALTY);
            System.out.printf(
                    Locale.ROOT,
                    "%s: %.2f%% -> %.2f%%%n",
                    attribute.getDisplayName(),
                    before,
                    attributes.get(attribute)
            );
        }
        attributes.setFarmlandFertilizer(
                farmland.x & 15,
                farmland.y,
                farmland.z & 15,
                fertilizer,
                world.getTotalWorldTime() + FERTILIZER_DURATION
        );
        chunk.setChunkModified();
        world.playAuxSFX(2005, farmland.x, farmland.y + 1, farmland.z, 0);
        return true;
    }

    public static ChunkAttribute getFertilizer(World world, int x, int y, int z) {
        expireFertilizer(world, x, y, z);
        ChunkAttributes.FertilizerData data = get(world, x, z)
                .getFarmlandFertilizer(x & 15, y, z & 15);
        return data == null ? null : data.getAttribute();
    }

    public static boolean hasActiveFertilizer(World world, int x, int y, int z) {
        int farmlandId = world.getBlockId(x, y, z);
        if (farmlandId != BTWBlocks.fertilizedFarmland.blockID && farmlandId != NMBlocks.netherFarmland.blockID) {
            return false;
        }
        ChunkAttributes.FertilizerData data = get(world, x, z)
                .getFarmlandFertilizer(x & 15, y, z & 15);
        return data != null && !data.isExpired(world.getTotalWorldTime());
    }

    public static void expireFertilizer(World world, int x, int y, int z) {
        if (world.isRemote) {
            return;
        }
        Chunk chunk = world.getChunkFromChunkCoords(x >> 4, z >> 4);
        ChunkAttributes attributes = get(chunk);
        ChunkAttributes.FertilizerData data = attributes.getFarmlandFertilizer(x & 15, y, z & 15);
        int farmlandId = world.getBlockId(x, y, z);
        boolean isFertilizedFarmland = farmlandId == BTWBlocks.fertilizedFarmland.blockID
                || farmlandId == NMBlocks.netherFarmland.blockID;
        if (!isFertilizedFarmland) {
            if (data != null) {
                attributes.clearFarmlandFertilizer(x & 15, y, z & 15);
                chunk.setChunkModified();
            }
            return;
        }
        if (data == null || data.isExpired(world.getTotalWorldTime())) {
            int metadata = world.getBlockMetadata(x, y, z);
            attributes.clearFarmlandFertilizer(x & 15, y, z & 15);
            if (farmlandId == BTWBlocks.fertilizedFarmland.blockID) {
                world.setBlockAndMetadataWithNotify(x, y, z, BTWBlocks.farmland.blockID, metadata);
            }
            chunk.setChunkModified();
        }
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
        text.append(" | Fish ").append(attributes.getFish()).append('/').append(attributes.getMaxFish());
        text.append(" | Pollution ").append(String.format(Locale.ROOT, "%.0f", attributes.getPollution()));
        return text.toString();
    }

    public static float getFishAvailability(World world, int blockX, int blockZ) {
        FishTotals totals = getFishTotals(world, blockX, blockZ);
        if (totals.contributingChunks <= 0) {
            return 0.0F;
        }
        return Math.min(1.0F, (float)totals.fish / (float)(totals.contributingChunks * 100));
    }

    public static boolean hasFish(World world, int blockX, int blockZ) {
        return getFishTotals(world, blockX, blockZ).fish > 0;
    }

    public static int getLocalMaxFish(World world, int blockX, int blockZ) {
        return get(world, blockX, blockZ).getMaxFish();
    }

    public static boolean isFarmlandApplicationTarget(World world, int x, int y, int z) {
        return findFarmlandForApplication(world, x, y, z) != null;
    }

    public static boolean takeFish(World world, int blockX, int blockZ) {
        int centerChunkX = blockX >> 4;
        int centerChunkZ = blockZ >> 4;
        FishTotals totals = getFishTotals(world, blockX, blockZ);
        if (totals.fish <= 0) {
            return false;
        }

        int roll = world.rand.nextInt(totals.fish);
        for (int offsetX = -1; offsetX <= 1; ++offsetX) {
            for (int offsetZ = -1; offsetZ <= 1; ++offsetZ) {
                int chunkX = centerChunkX + offsetX;
                int chunkZ = centerChunkZ + offsetZ;
                if (!isContributingChunk(world, chunkX, chunkZ, centerChunkX, centerChunkZ)) {
                    continue;
                }
                Chunk chunk = world.getChunkFromChunkCoords(chunkX, chunkZ);
                ChunkAttributes attributes = get(chunk);
                if (roll < attributes.getFish()) {
                    if (attributes.takeFish()) {
                        chunk.setChunkModified();
                        return true;
                    }
                    return false;
                }
                roll -= attributes.getFish();
            }
        }
        return false;
    }

    private static void initialize(Chunk chunk, ChunkAttributes attributes) {
        BiomeGenBase biome = chunk.getBiomeGenForWorldCoords(8, 8, chunk.worldObj.getWorldChunkManager());
        int height = chunk.getHeightValue(8, 8);
        long rollSeed = createRollSeed(chunk);
        Random random = new Random(rollSeed);
        EnumMap<ChunkAttribute, Float> values = new EnumMap<>(ChunkAttribute.class);
        int netherTier = chunk.worldObj.provider.dimensionId == -1
                ? NetherTierHelper.getTier(chunk.worldObj, chunk.xPosition * 16 + 8, chunk.zPosition * 16 + 8)
                : -1;
        for (ChunkAttribute attribute : ChunkAttribute.values()) {
            Range range = netherTier >= 0 ? getNetherRange(attribute, netherTier) : getRange(attribute, biome, height);
            values.put(attribute, range.min + random.nextFloat() * (range.max - range.min));
        }
        int fishCapacity = getFishCapacityRange(biome).next(random);
        attributes.initialize(
                values,
                chunk.xPosition,
                chunk.zPosition,
                chunk.worldObj.provider.dimensionId,
                rollSeed,
                fishCapacity
        );
        chunk.setChunkModified();
    }

    private static void initializeFish(Chunk chunk, ChunkAttributes attributes) {
        BiomeGenBase biome = chunk.getBiomeGenForWorldCoords(8, 8, chunk.worldObj.getWorldChunkManager());
        Random random = new Random(attributes.getRollSeed() ^ 0x6a09e667f3bcc909L);
        attributes.initializeFish(getFishCapacityRange(biome).next(random));
        chunk.setChunkModified();
    }

    private static long createRollSeed(Chunk chunk) {
        long seed = chunk.worldObj.getSeed();
        seed ^= (long)chunk.xPosition * 341873128712L;
        seed ^= (long)chunk.zPosition * 132897987541L;
        seed ^= (long)chunk.worldObj.provider.dimensionId * 42317861L;
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

    /**
     * Nether soil is intentionally uneven: each ring has a useful strength and
     * a serious deficiency, so farms benefit from surveying, fertilizer freight,
     * and eventually the planned automatic fertilizer block. The deadzone is
     * hostile and erratic rather than a source of mandatory fertile land.
     */
    private static Range getNetherRange(ChunkAttribute attribute, int tier) {
        return switch (tier) {
            case 1 -> switch (attribute) {
                case MOISTURE -> new Range(18, 35);
                case NITROGEN -> new Range(10, 25);
                case POTASSIUM -> new Range(65, 90);
                case ACIDITY -> new Range(60, 85);
                case POROSITY -> new Range(55, 80);
            };
            case 2 -> switch (attribute) {
                case MOISTURE -> new Range(8, 22);
                case NITROGEN -> new Range(30, 55);
                case POTASSIUM -> new Range(75, 100);
                case ACIDITY -> new Range(70, 95);
                case POROSITY -> new Range(45, 75);
            };
            case 3 -> switch (attribute) {
                case MOISTURE -> new Range(0, 5);
                case NITROGEN -> new Range(0, 10);
                case POTASSIUM -> new Range(10, 95);
                case ACIDITY -> new Range(80, 100);
                case POROSITY -> new Range(5, 95);
            };
            default -> switch (attribute) {
                case MOISTURE -> new Range(2, 10);
                case NITROGEN -> new Range(8, 20);
                case POTASSIUM -> new Range(45, 70);
                case ACIDITY -> new Range(65, 90);
                case POROSITY -> new Range(60, 85);
            };
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

    private static boolean contains(ChunkAttribute[] attributes, ChunkAttribute target) {
        for (ChunkAttribute attribute : attributes) {
            if (attribute == target) {
                return true;
            }
        }
        return false;
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
        if (isFarmlandBlock(blockId)) {
            return new FarmlandPosition(x, y, z);
        }

        Block target = Block.blocksList[blockId];
        if (target == null || !isSupportedPlant(target)) {
            return null;
        }
        for (int offset = 1; offset <= 2; ++offset) {
            int belowId = world.getBlockId(x, y - offset, z);
            if (isFarmlandBlock(belowId)) {
                return new FarmlandPosition(x, y - offset, z);
            }
        }
        return null;
    }

    private static FarmlandPosition findFarmlandForApplication(World world, int x, int y, int z) {
        int blockId = world.getBlockId(x, y, z);
        if (isFarmlandBlock(blockId)) {
            return new FarmlandPosition(x, y, z);
        }
        int belowId = world.getBlockId(x, y - 1, z);
        if (isFarmlandBlock(belowId)) {
            return new FarmlandPosition(x, y - 1, z);
        }
        return null;
    }

    private static boolean isFarmlandBlock(int blockId) {
        return blockId == BTWBlocks.farmland.blockID || blockId == BTWBlocks.fertilizedFarmland.blockID
                || NMBlocks.netherFarmland != null && blockId == NMBlocks.netherFarmland.blockID;
    }

    private static FishTotals getFishTotals(World world, int blockX, int blockZ) {
        int centerChunkX = blockX >> 4;
        int centerChunkZ = blockZ >> 4;
        int fish = 0;
        int maxFish = 0;
        int contributingChunks = 0;
        for (int offsetX = -1; offsetX <= 1; ++offsetX) {
            for (int offsetZ = -1; offsetZ <= 1; ++offsetZ) {
                int chunkX = centerChunkX + offsetX;
                int chunkZ = centerChunkZ + offsetZ;
                if (!isContributingChunk(world, chunkX, chunkZ, centerChunkX, centerChunkZ)) {
                    continue;
                }
                ChunkAttributes attributes = get(world.getChunkFromChunkCoords(chunkX, chunkZ));
                fish += attributes.getFish();
                maxFish += attributes.getMaxFish();
                ++contributingChunks;
            }
        }
        return new FishTotals(fish, maxFish, contributingChunks);
    }

    private static boolean isContributingChunk(
            World world,
            int chunkX,
            int chunkZ,
            int centerChunkX,
            int centerChunkZ
    ) {
        return chunkX == centerChunkX && chunkZ == centerChunkZ || world.isChunkActive(chunkX, chunkZ);
    }

    private static IntRange getFishCapacityRange(BiomeGenBase biome) {
        String name = biome.biomeName == null ? "" : biome.biomeName.toLowerCase(Locale.ROOT);
        if (name.contains("ocean")) {
            return new IntRange(90, 140);
        }
        if (name.contains("river")) {
            return new IntRange(45, 75);
        }
        if (name.contains("swamp")) {
            return new IntRange(38, 65);
        }
        if (name.contains("beach")) {
            return new IntRange(25, 45);
        }
        if (name.contains("desert")) {
            return new IntRange(6, 12);
        }
        if (name.contains("jungle")) {
            return new IntRange(18, 32);
        }
        if (name.contains("taiga") || name.contains("ice") || name.contains("frozen")) {
            return new IntRange(12, 25);
        }
        return new IntRange(10, 20);
    }

    private record FarmlandPosition(int x, int y, int z) {}

    private record Range(float min, float max) {
            private Range(float min, float max) {
                this.min = Math.max(0, Math.min(ChunkAttributes.MAX_VALUE, min));
                this.max = Math.max(this.min, Math.min(ChunkAttributes.MAX_VALUE, max));
            }

            private static Range around(float center, float radius) {
                return new Range(center - radius, center + radius);
            }
        }

    private static final class IntRange {
        private final int min;
        private final int max;

        private IntRange(int min, int max) {
            this.min = min;
            this.max = Math.max(min, max);
        }

        private int next(Random random) {
            return this.min + random.nextInt(this.max - this.min + 1);
        }
    }

    private record FishTotals(int fish, int maxFish, int contributingChunks) {}
}
