package com.itlesports.nightmaremode.fishing;

import net.minecraft.src.*;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.LinkedHashMap;

public final class FishingHabitat {
    private static final Map<World, Map<Long, Float>> OFFSHORE = new WeakHashMap<>();
    public final float sourceDepth, offshore, maxDepth;
    public final float hookSourceDepth, averageSourceDepth;
    public final float descentMultiplier, rarityMultiplier;
    public final String biome;

    public FishingHabitat(EntityFishHook hook, boolean lava) {
        World world = hook.worldObj;
        int x = MathHelper.floor_double(hook.posX), z = MathHelper.floor_double(hook.posZ);
        int surface = MathHelper.floor_double(hook.posY);
        Material fluid = lava ? Material.lava : Material.water;
        if (world.getBlockMaterial(x, surface, z) != fluid) surface--;
        float total = 0, hookDepth = 0;
        int[][] columns = {{0,0},{2,0},{-2,0},{0,2},{0,-2}};
        for (int[] column : columns) {
            int count = 0;
            for (int y = surface; y > 0 && count < 64; y--) {
                if (world.getBlockMaterial(x + column[0], y, z + column[1]) != fluid
                        || world.getBlockMetadata(x + column[0], y, z + column[1]) != 0) break;
                count++;
            }
            total += count;
            if (column[0] == 0 && column[1] == 0) hookDepth = count;
        }
        hookSourceDepth = hookDepth;
        averageSourceDepth = total / columns.length;
        sourceDepth = (hookSourceDepth + averageSourceDepth) * 0.5F;
        biome = world.getBiomeGenForCoords(x, z).biomeName.toLowerCase(Locale.ROOT);
        offshore = !lava && biome.contains("ocean") ? offshore(world, x, z) : 0;
        float habitatBonus = biome.contains("jungle") ? 30 : biome.contains("swamp") ? 10
                : biome.contains("river") || biome.contains("taiga") || biome.contains("ice") || biome.contains("frozen") ? 20 : 0;
        boolean ocean = !lava && biome.contains("ocean");
        maxDepth = FishingGame.depthCap(sourceDepth, offshore, habitatBonus, ocean);
        descentMultiplier = FishingGame.deepWaterSpeed(sourceDepth, ocean);
        rarityMultiplier = FishingGame.deepWaterRarity(sourceDepth, ocean);
    }

    public boolean supportsGiantSquid() {
        return biome.contains("ocean") && maxDepth >= 175
                && offshore * 256 >= FishingTuning.GIANT_SQUID_MIN_OFFSHORE_BLOCKS;
    }

    private static synchronized float offshore(World world, int x, int z) {
        Map<Long, Float> cache = OFFSHORE.computeIfAbsent(world, ignored -> new LinkedHashMap<Long, Float>(64, 0.75F, true) {
            @Override protected boolean removeEldestEntry(Map.Entry<Long, Float> eldest) { return size() > 1024; }
        });
        long key = (long)(x >> 4) << 32 | (z >> 4) & 0xFFFFFFFFL;
        Float found = cache.get(key);
        if (found != null) return found;
        int centerX = (x & ~15) + 8, centerZ = (z & ~15) + 8;
        float distance = 256;
        // biome-cache queries use the seed and never request or generate terrain chunks.
        for (int ray = 0; ray < 16; ray++) {
            double angle = ray * Math.PI / 8;
            for (int radius = 16; radius <= 256 && radius < distance; radius += 16) {
                BiomeGenBase sample = world.getWorldChunkManager().getBiomeGenAt(
                        centerX + (int)(Math.cos(angle) * radius), centerZ + (int)(Math.sin(angle) * radius));
                if (!sample.biomeName.toLowerCase(Locale.ROOT).contains("ocean")) { distance = radius; break; }
            }
        }
        float result = distance / 256;
        cache.put(key, result);
        return result;
    }

}
