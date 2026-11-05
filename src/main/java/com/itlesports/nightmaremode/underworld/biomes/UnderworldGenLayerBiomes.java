package com.itlesports.nightmaremode.underworld.biomes;
import com.itlesports.nightmaremode.underworld.BiomeGenUnderworld;
import net.minecraft.src.*;

public class UnderworldGenLayerBiomes extends GenLayer {
    private static final BiomeGenBase[] underworldBiomes = new BiomeGenBase[] {
            BiomeGenUnderworld.blightlands,
            BiomeGenUnderworld.highlands,
            BiomeGenUnderworld.flowerFields,
            BiomeGenUnderworld.shadowRealm,
            BiomeGenUnderworld.underHell
    };

    public UnderworldGenLayerBiomes(long seed) {
        super(seed);
    }

    public UnderworldGenLayerBiomes(long par1, GenLayer par3GenLayer, WorldType par4WorldType) {
        this(par1);
        this.parent = par3GenLayer;
    }

    @Override
    public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
        int[] biomes = IntCache.getIntCache(areaWidth * areaHeight);
        for (int z = 0; z < areaHeight; z++) {
            for (int x = 0; x < areaWidth; x++) {
                this.initChunkSeed(areaX + x, areaY + z);
                biomes[x + z * areaWidth] = underworldBiomes[this.nextInt(underworldBiomes.length)].biomeID;
            }
        }
        return biomes;
    }
}
