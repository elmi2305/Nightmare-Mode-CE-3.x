package com.itlesports.nightmaremode.underworld.biomes;

import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.entity.underworld.EntityVoidSquid;
import com.itlesports.nightmaremode.entity.variants.EntityShadowZombie;
import com.itlesports.nightmaremode.mixin.interfaces.BiomeDecoratorAccess;
import com.itlesports.nightmaremode.underworld.BiomeGenUnderworld;
import net.minecraft.src.*;

public class BiomeGenShadowRealm extends BiomeGenUnderworld {
    public BiomeGenShadowRealm(int par1) {
        super(par1);
        this.spawnableCreatureList.clear();
        this.spawnableMonsterList.clear();

        this.spawnableMonsterList.add(new SpawnListEntry(EntityShadowZombie.class, 15, 2, 6));
        // add squids
        this.spawnableMonsterList.add(new SpawnListEntry(EntitySkeleton.class, 4, 1, 1)); // forced wither
        this.spawnableMonsterList.add(new SpawnListEntry(EntityEnderman.class, 5, 1, 4));
        this.spawnableMonsterList.add(new SpawnListEntry(EntityVoidSquid.class, 5, 1, 4));


        this.topBlock = (short) NMBlocks.underStones.blockID;
        this.fillerBlock = (short)NMBlocks.underStones.blockID;

        this.topBlockMetadata = (short) NMBlocks.META_VOID_STONE;
        this.fillerBlockMetadata = (short) NMBlocks.META_VOID_STONE;
        ((BiomeDecoratorAccess)this.theBiomeDecorator).setTreesPerChunk(2);

    }

    @Override
    public int getBiomeFoliageColor() {
        return 0x202020;
    }

    @Override
    public int getBiomeGrassColor() {
        return 0x202020;
    }
}
