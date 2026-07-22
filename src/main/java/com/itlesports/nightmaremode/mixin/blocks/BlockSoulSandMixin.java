package com.itlesports.nightmaremode.mixin.blocks;

import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.Block;
import net.minecraft.src.BlockSoulSand;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Material;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(BlockSoulSand.class)
public abstract class BlockSoulSandMixin extends Block {
    protected BlockSoulSandMixin(int id, Material material) {
        super(id, material);
    }

    @Override
    public void harvestBlock(World world, EntityPlayer player, int x, int y, int z, int metadata) {
        super.harvestBlock(world, player, x, y, z, metadata);
        this.tryDropSoulChip(world, x, y, z);
    }

    @Override
    public void onBlockDestroyedWithImproperTool(World world, EntityPlayer player, int x, int y, int z, int metadata) {
        super.onBlockDestroyedWithImproperTool(world, player, x, y, z, metadata);
        this.tryDropSoulChip(world, x, y, z);
    }

    @Unique
    private void tryDropSoulChip(World world, int x, int y, int z) {
        if (!world.isRemote && world.rand.nextFloat() < 0.08F) {
            this.dropBlockAsItem_do(world, x, y, z, new ItemStack(NMItems.soulChip));
        }
    }
}
