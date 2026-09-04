package com.itlesports.nightmaremode.underworld.poi.scatteredfeatures.utils;

import com.itlesports.nightmaremode.underworld.poi.scatteredfeatures.BigMushroom;
import com.itlesports.nightmaremode.underworld.poi.scatteredfeatures.RibcageClosed;
import com.itlesports.nightmaremode.underworld.poi.scatteredfeatures.RibcageOpen;
import net.minecraft.src.StructureComponent;
import net.minecraft.src.StructureStart;

import java.util.Random;

public class StructureScatteredFeatureStartUnderworld extends StructureStart {
    public StructureScatteredFeatureStartUnderworld() {}

    public StructureScatteredFeatureStartUnderworld(Random random, int chunkX, int chunkZ,
                                                     MapGenScatteredFeatureUnderworld.Feature feature) {
        super(chunkX, chunkZ);
        StructureComponent component = createComponent(feature, random, chunkX * 16, chunkZ * 16);
        if (component != null) {
            this.components.add(component);
        }
        this.updateBoundingBox();
    }

    private static StructureComponent createComponent(MapGenScatteredFeatureUnderworld.Feature feature,
                                                       Random random, int x, int z) {
        if (feature == null) return null;
        switch (feature) {
            case BIG_MUSHROOM:
                return new BigMushroom(random, x, z);
            case RIBCAGE_CLOSED:
                return new RibcageClosed(random, x, z);
            case RIBCAGE_OPEN:
                return new RibcageOpen(random, x, z);
            case OBSIDIAN_SPIKE:
            default:
                // obsidian spikes remain registered for old chunks but are intentionally disabled.
                return null;
        }
    }
}
