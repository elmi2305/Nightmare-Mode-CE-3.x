package com.itlesports.nightmaremode.block.blocks;

import net.minecraft.src.*;

import java.util.Random;

public class BlockRoad extends Block {
    private final float movementModifier;
    public BlockRoad(int par1, float movementModifier) {
        super(par1, Material.rock);
        this.setCreativeTab(CreativeTabs.tabBlock);
        this.setPicksEffectiveOn();
        this.setHardness(1f);
        this.setStepSound(soundStoneFootstep);
        this.movementModifier = movementModifier;
    }
    @Override
    public float getMovementModifier(World world, int i, int j, int k) {
        return this.movementModifier;
    }
    @Override
    public boolean isFallingBlock() {
        return true;
    }
    @Override
    public void onNeighborBlockChange(World world, int i, int j, int k, int iNeighborBlockID) {
        this.scheduleCheckForFall(world, i, j, k);
    }
    @Override
    public void onBlockAdded(World par1World, int par2, int par3, int par4) {
        this.scheduleCheckForFall(par1World, par2, par3, par4);
        super.onBlockAdded(par1World, par2, par3, par4);
    }
    public void updateTick(World world, int i, int j, int k, Random rand) {
        this.checkForFall(world, i, j, k);
    }
}
