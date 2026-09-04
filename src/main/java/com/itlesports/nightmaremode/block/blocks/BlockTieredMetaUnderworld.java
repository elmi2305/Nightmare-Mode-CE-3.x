package com.itlesports.nightmaremode.block.blocks;

import com.itlesports.nightmaremode.block.blocks.templates.BlockMetaMultiTextured;
import com.itlesports.nightmaremode.util.underworld.IUnderworldTieredBlock;
import com.itlesports.nightmaremode.util.underworld.UnderworldToolTier;
import net.minecraft.src.*;

public class BlockTieredMetaUnderworld extends BlockMetaMultiTextured implements IUnderworldTieredBlock {
    private final UnderworldToolTier requiredTier;

    public BlockTieredMetaUnderworld(int id, Material material, UnderworldToolTier requiredTier, Variant... variants) {
        super(id, material, variants);
        this.requiredTier = requiredTier;
        this.setPicksEffectiveOn();
    }

    @Override
    public UnderworldToolTier getRequiredUnderworldTier(IBlockAccess world, int x, int y, int z) { return requiredTier; }

    @Override
    public int getHarvestToolLevel(IBlockAccess world, int x, int y, int z) { return 4 + requiredTier.level(); }

    @Override
    public float getBlockHardness(World world, int x, int y, int z) {
        float configured = super.getBlockHardness(world, x, y, z);
        return configured < 0.0F ? 25.0F : configured;
    }

    @Override
    public boolean canDropFromExplosion(Explosion explosion) {
        return false;
    }
}
