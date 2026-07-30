package com.itlesports.nightmaremode.block.blocks;

import btw.block.blocks.HopperBlock;
import com.itlesports.nightmaremode.block.tileEntities.ChuteHopperTileEntity;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;

public class BlockChuteHopper extends HopperBlock {
    public BlockChuteHopper(int id) {
        super(id);
        this.setUnlocalizedName("ifhyChuteHopper");
    }

    @Override
    public TileEntity createNewTileEntity(World world) {
        return new ChuteHopperTileEntity();
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, int neighborId) {
        world.markBlockForUpdate(x, y, z);
    }

    @Override
    public void updateTick(World world, int x, int y, int z, java.util.Random random) {
    }

    @Override
    public boolean canInputMechanicalPower() {
        return false;
    }

    @Override
    public boolean isInputtingMechanicalPower(World world, int x, int y, int z) {
        return false;
    }
}
