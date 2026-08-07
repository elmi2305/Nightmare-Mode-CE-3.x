package com.itlesports.nightmaremode.block.blocks;

import com.itlesports.nightmaremode.block.blocks.templates.BlockCustomHopperModel;
import com.itlesports.nightmaremode.block.tileEntities.ChuteHopperTileEntity;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;

public class BlockChuteHopper extends BlockCustomHopperModel {
    public BlockChuteHopper(int id) {
        super(id,
                "nightmare:ifhyChuteHopperTop",
                "nightmare:ifhyChuteHopperBottom",
                "nightmare:ifhyChuteHopper");
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
