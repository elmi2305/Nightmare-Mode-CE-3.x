package com.itlesports.nightmaremode.mixin.blocks;

import btw.block.blocks.NetherBrickBlock;
import net.minecraft.src.Block;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Material;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.EnumToolMaterial;
import net.minecraft.src.StatList;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(NetherBrickBlock.class)
public abstract class NetherBrickBlockMixin extends Block {
    protected NetherBrickBlockMixin(int id, Material material) {
        super(id, material);
    }

    @Override public int getHarvestToolLevel(IBlockAccess world, int x, int y, int z) {
        return EnumToolMaterial.SOULFORGED_STEEL.getHarvestLevel();
    }

    @Override public int getEfficientToolLevel(IBlockAccess world, int x, int y, int z) {
        return this.getHarvestToolLevel(world, x, y, z);
    }

    @Override
    public void harvestBlock(World world, EntityPlayer player, int x, int y, int z, int metadata) {
        ItemStack held = player.getCurrentEquippedItem();
        if (com.itlesports.nightmaremode.util.NMBlockBreakingRules.isSteelMiningTool(held)) {
            super.harvestBlock(world, player, x, y, z, metadata);
        } else if (!world.isRemote) {
            player.addStat(StatList.mineBlockStatArray[this.blockID], 1);
        }
    }

    @Override
    public void onBlockDestroyedWithImproperTool(World world, EntityPlayer player,
                                                 int x, int y, int z, int metadata) {
        if (!world.isRemote) {
            player.addStat(StatList.mineBlockStatArray[this.blockID], 1);
        }
    }
}
