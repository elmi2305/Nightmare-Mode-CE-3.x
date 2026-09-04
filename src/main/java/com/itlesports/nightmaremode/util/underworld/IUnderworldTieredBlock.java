package com.itlesports.nightmaremode.util.underworld;

import net.minecraft.src.IBlockAccess;

public interface IUnderworldTieredBlock {
    UnderworldToolTier getRequiredUnderworldTier(IBlockAccess world, int x, int y, int z);
}
