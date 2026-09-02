package com.itlesports.nightmaremode.worldgen;

import net.minecraft.src.*;

/**
 * IFHY's intentionally conservative overworld topology change.
 *
 * The vanilla biome roster, biome assignment, terrain noise, and decorators
 * remain untouched. Two extra AddIsland passes happen before mushroom islands
 * are considered, so land coalesces and mushroom-island frequency naturally
 * follows the smaller remaining ocean field.
 */
public final class IFHYOverworldGenLayer {
    private IFHYOverworldGenLayer() {}

    public static GenLayer[] initializeAllBiomeGenerators(long seed, WorldType worldType) {
        GenLayer continents = new GenLayerIsland(1L);
        continents = new GenLayerFuzzyZoom(2000L, continents);
        continents = new GenLayerAddIsland(1L, continents);
        continents = new GenLayerZoom(2001L, continents);
        continents = new GenLayerAddIsland(2L, continents);
        continents = new GenLayerAddSnow(2L, continents);
        continents = new GenLayerZoom(2002L, continents);
        continents = new GenLayerAddIsland(3L, continents);
        continents = new GenLayerZoom(2003L, continents);
        continents = new GenLayerAddIsland(4L, continents);

        // The only deliberate topology change: split deep ocean interiors
        // before the ocean-only mushroom-island roll. It never erodes land.
        continents = new IFHYGenLayerBreakUpOceans(6L, continents);
        continents = new GenLayerAddMushroomIsland(5L, continents);

        int zoomCount = worldType == WorldType.LARGE_BIOMES ? 6 : 4;

        GenLayer riverLayer = GenLayerZoom.magnify(1000L, continents, 0);
        riverLayer = new GenLayerRiverInit(100L, riverLayer);
        riverLayer = GenLayerZoom.magnify(1000L, riverLayer, zoomCount + 2);
        riverLayer = new GenLayerRiver(1L, riverLayer);
        riverLayer = new GenLayerSmooth(1000L, riverLayer);

        GenLayer biomeLayer = GenLayerZoom.magnify(1000L, continents, 0);
        biomeLayer = new GenLayerBiome(200L, biomeLayer, worldType);
        biomeLayer = GenLayerZoom.magnify(1000L, biomeLayer, 2);
        biomeLayer = new GenLayerHills(1000L, biomeLayer);

        for (int index = 0; index < zoomCount; ++index) {
            biomeLayer = new GenLayerZoom(1000L + index, biomeLayer);
            if (index == 0) biomeLayer = new GenLayerAddIsland(3L, biomeLayer);
            if (index == 1) {
                biomeLayer = new GenLayerShore(1000L, biomeLayer);
                biomeLayer = new GenLayerSwampRivers(1000L, biomeLayer);
            }
        }

        biomeLayer = new GenLayerSmooth(1000L, biomeLayer);
        GenLayer mixedLayer = new GenLayerRiverMix(100L, biomeLayer, riverLayer);
        GenLayer biomeIndexLayer = new GenLayerVoronoiZoom(10L, mixedLayer);
        mixedLayer.initWorldGenSeed(seed);
        biomeIndexLayer.initWorldGenSeed(seed);
        return new GenLayer[]{mixedLayer, biomeIndexLayer, mixedLayer};
    }
}
