package com.itlesports.nightmaremode.block.blocks;

import com.itlesports.nightmaremode.block.blocks.templates.NMBlock;
import com.itlesports.nightmaremode.util.underworld.IUnderworldTieredBlock;
import com.itlesports.nightmaremode.util.underworld.UnderworldToolTier;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.Material;
import net.minecraft.src.Explosion;

public class BlockTieredUnderworld extends NMBlock implements IUnderworldTieredBlock {
    private final UnderworldToolTier requiredTier;

    public BlockTieredUnderworld(int id, Material material, UnderworldToolTier requiredTier) {
        super(id, material);
        this.requiredTier = requiredTier;
        this.setPicksEffectiveOn();
    }

    @Override
    public UnderworldToolTier getRequiredUnderworldTier(IBlockAccess world, int x, int y, int z) {
        return requiredTier;
    }

    @Override
    public int getHarvestToolLevel(IBlockAccess world, int x, int y, int z) {
        return requiredTier == UnderworldToolTier.STEEL ? 3 : 4 + requiredTier.level();
    }

    @Override
    public boolean canDropFromExplosion(Explosion explosion) {
        return false;
    }
}
