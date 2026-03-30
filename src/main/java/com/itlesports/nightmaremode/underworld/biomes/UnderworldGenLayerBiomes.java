package com.itlesports.nightmaremode.underworld.biomes;
import com.itlesports.nightmaremode.underworld.BiomeGenUnderworld;
import net.minecraft.src.*;

public class UnderworldGenLayerBiomes extends GenLayer {
    private static final BiomeGenBase[] underworldBiomes = new BiomeGenBase[] {
            BiomeGenBase.biomeList[24], // Underworld Plains
            BiomeGenBase.biomeList[25], // Underworld Desert
            BiomeGenBase.biomeList[27], // Shadow Realm
    };

    public UnderworldGenLayerBiomes(long par1, GenLayer par3GenLayer, WorldType par4WorldType) {
        super(par1);
        this.parent = par3GenLayer;
    }

    @Override
    public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
        int[] ints = this.parent.getInts(areaX, areaY, areaWidth, areaHeight);
        int[] intCache = IntCache.getIntCache(areaWidth * areaHeight);
        for (int x = 0; x < areaHeight; ++x) {
            for (int y = 0; y < areaWidth; ++y) {
                this.initChunkSeed(y + areaX, x + areaY);
                int currentBiomeId = ints[y + x * areaWidth];
                if (currentBiomeId == 0) {
                    intCache[y + x * areaWidth] = BiomeGenUnderworld.flowerFields.biomeID;
//                    intCache[y + x * areaWidth] = 0;
                }
                else if (currentBiomeId == 1) {
                    if (this.nextInt(4) == 0) {
                        intCache[y + x * areaWidth] = underworldBiomes[2].biomeID; // shadow realm
                    }
                    else {
                        intCache[y + x * areaWidth] = underworldBiomes[this.nextInt(underworldBiomes.length)].biomeID; // everything other than flower fields
                    }
                }
                else {
                    intCache[y + x * areaWidth] = underworldBiomes[this.nextInt(underworldBiomes.length)].biomeID;
                }
            }
        }
        return intCache;
    }
}