package com.itlesports.nightmaremode.mixin.blocks;

import net.minecraft.src.BlockContainer;
import net.minecraft.src.Entity;
import net.minecraft.src.Material;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(net.minecraft.src.BlockMobSpawner.class)
public abstract class BlockMobSpawnerMixin extends BlockContainer {
    protected BlockMobSpawnerMixin(int blockID, Material material) {
        super(blockID, material);
    }

    @Override
    public float getExplosionResistance(Entity entity, World world, int x, int y, int z) {
        if (world.provider.dimensionId == -1) {
            return 2000.0f;
        }
        return super.getExplosionResistance(entity, world, x, y, z);
    }
}
