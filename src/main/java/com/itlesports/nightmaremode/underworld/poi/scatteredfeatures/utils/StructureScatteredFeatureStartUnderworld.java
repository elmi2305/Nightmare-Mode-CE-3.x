package com.itlesports.nightmaremode.underworld.poi.scatteredfeatures.utils;

import com.itlesports.nightmaremode.underworld.poi.scatteredfeatures.BigMushroom;
import com.itlesports.nightmaremode.underworld.poi.scatteredfeatures.RibcageClosed;
import com.itlesports.nightmaremode.underworld.poi.scatteredfeatures.RibcageOpen;
import net.minecraft.src.StructureComponent;
import net.minecraft.src.StructureBoundingBox;
import net.minecraft.src.StructureStart;

import java.util.Random;
import java.util.Arrays;
import java.util.function.IntBinaryOperator;

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

    // only new starts are aligned; loaded component boxes retain their saved positions.
    public void alignToTerrain(IntBinaryOperator surfaceHeight, int burialDepth) {
        if (this.components.isEmpty()) return;
        StructureBoundingBox bounds = this.getBoundingBox();
        int centerX = bounds.minX + (bounds.maxX - bounds.minX) / 2;
        int centerZ = bounds.minZ + (bounds.maxZ - bounds.minZ) / 2;
        int[] heights = {
                surfaceHeight.applyAsInt(bounds.minX, bounds.minZ),
                surfaceHeight.applyAsInt(bounds.maxX, bounds.minZ),
                surfaceHeight.applyAsInt(bounds.minX, bounds.maxZ),
                surfaceHeight.applyAsInt(bounds.maxX, bounds.maxZ),
                surfaceHeight.applyAsInt(centerX, centerZ)
        };
        Arrays.sort(heights);
        int height = bounds.maxY - bounds.minY + 1;
        int targetY = Math.max(1, Math.min(256 - height, heights[2] + 1 - burialDepth));
        int offsetY = targetY - bounds.minY;
        for (Object value : this.components) {
            ((StructureComponent)value).getBoundingBox().offset(0, offsetY, 0);
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
