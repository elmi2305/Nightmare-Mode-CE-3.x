package com.itlesports.nightmaremode.block.blocks;

import btw.block.blocks.FarmlandBlock;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

public class BlockEndFarmland extends FarmlandBlock {
    @Environment(EnvType.CLIENT) private Icon top;

    public BlockEndFarmland(int id) {
        super(id);
        this.setUnlocalizedName("ifhyEndFarmland");
        this.setCreativeTab(CreativeTabs.tabDecorations);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons(IconRegister register) {
        this.blockIcon = register.registerIcon("nightmare:ifhyHardenedEndstone");
        this.top = register.registerIcon("nightmare:ifhyEndFarmland");
    }

    @Override @Environment(EnvType.CLIENT)
    public Icon getIcon(int side, int metadata) {
        if (side == 0) {
            return this.top;
        }
        return this.blockIcon;
    }

    @Override protected boolean hasIrrigatingBlocks(World world, int x, int y, int z) { return true; }
    @Override protected void checkForSoilReversion(World world, int x, int y, int z) {}
    @Override public void notifyOfPlantAboveRemoved(World world, int x, int y, int z, Block plant) {}
    @Override public void onNeighborBlockChange(World world, int x, int y, int z, int neighborId) {}
    @Override public void onFallenUpon(World world, int x, int y, int z, Entity entity, float distance) {}
    @Override public void onVegetationAboveGrazed(World world, int x, int y, int z, EntityAnimal animal) {}
    @Override public int idDropped(int metadata, java.util.Random random, int fortune) { return Block.whiteStone.blockID; }
    @Override public int idPicked(World world, int x, int y, int z) { return Block.whiteStone.blockID; }
}
