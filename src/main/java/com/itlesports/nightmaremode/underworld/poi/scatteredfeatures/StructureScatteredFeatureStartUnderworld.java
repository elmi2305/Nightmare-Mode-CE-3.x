package com.itlesports.nightmaremode.underworld.poi.scatteredfeatures;

import com.itlesports.nightmaremode.underworld.BiomeGenUnderworld;
import net.minecraft.src.*;

import java.util.ArrayList;
import java.util.Random;
import java.util.function.Supplier;

public class StructureScatteredFeatureStartUnderworld extends StructureStart {
    public StructureScatteredFeatureStartUnderworld() {}
    public StructureScatteredFeatureStartUnderworld(World world, Random rand, int chunkX, int chunkZ) {
        super(chunkX, chunkZ);
        BiomeGenBase biome = world.getBiomeGenForCoords(chunkX * 16 + 8, chunkZ * 16 + 8);

        ArrayList<Supplier<ComponentScatteredFeature>> feature = new ArrayList<Supplier<ComponentScatteredFeature>>();
        if (biome == BiomeGenUnderworld.flowerFields) {
            feature.add(() -> new BigMushroom(rand, chunkX * 16, chunkZ * 16));
        }

        if (!feature.isEmpty()) {
            int index = world.rand.nextInt(feature.size());
            this.components.add(((Supplier)feature.get(index)).get());
        }
        this.updateBoundingBox();
    }
}