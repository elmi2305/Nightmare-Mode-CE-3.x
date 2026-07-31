package com.itlesports.nightmaremode.underworld.poi.scatteredfeatures.utils;

import com.itlesports.nightmaremode.underworld.BiomeGenUnderworld;
import com.itlesports.nightmaremode.underworld.poi.scatteredfeatures.BigMushroom;
import com.itlesports.nightmaremode.underworld.poi.scatteredfeatures.RibcageClosed;
import com.itlesports.nightmaremode.underworld.poi.scatteredfeatures.RibcageOpen;
import net.minecraft.src.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public class StructureScatteredFeatureStartUnderworld extends StructureStart {
    public StructureScatteredFeatureStartUnderworld() {}
    public StructureScatteredFeatureStartUnderworld(World world, Random rand, int chunkX, int chunkZ) {
        super(chunkX, chunkZ);
        BiomeGenBase biome = world.getBiomeGenForCoords(chunkX * 16 + 8, chunkZ * 16 + 8);

        List<Supplier<ComponentScatteredFeature>> features = new ArrayList<>();
        if (biome == BiomeGenUnderworld.flowerFields) {
            features.add(() -> new BigMushroom(rand, chunkX * 16, chunkZ * 16));
        }
        if (biome == BiomeGenUnderworld.highlands) {
            features.add(() -> new RibcageClosed(rand, chunkX * 16, chunkZ * 16));
            features.add(() -> new RibcageOpen(rand, chunkX * 16, chunkZ * 16));
        }

        if (!features.isEmpty()) {
            this.components.add(features.get(rand.nextInt(features.size())).get());
        }
        this.updateBoundingBox();
    }
}
