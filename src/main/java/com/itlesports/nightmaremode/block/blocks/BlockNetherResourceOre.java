package com.itlesports.nightmaremode.block.blocks;

import btw.block.BTWBlocks;
import net.minecraft.src.Block;
import net.minecraft.src.CreativeTabs;

public class BlockNetherResourceOre extends Block {
    private final int dropItemId;

    public BlockNetherResourceOre(int id, int dropItemId, float hardness, String name, String texture) {
        super(id, BTWBlocks.netherRockMaterial);
        this.dropItemId = dropItemId;
        this.setHardness(hardness);
        this.setResistance(20.0F);
        this.setPicksEffectiveOn();
        this.setStepSound(BTWBlocks.oreStepSound);
        this.setCreativeTab(CreativeTabs.tabBlock);
        this.setUnlocalizedName(name);
        this.setTextureName(texture);
    }

    @Override
    public int idDropped(int metadata, java.util.Random random, int fortune) {
        return this.dropItemId;
    }

    @Override
    public int quantityDropped(java.util.Random random) {
        return 1;
    }
}
