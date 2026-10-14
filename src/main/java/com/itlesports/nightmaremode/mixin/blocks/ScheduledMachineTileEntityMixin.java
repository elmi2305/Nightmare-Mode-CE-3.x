package com.itlesports.nightmaremode.mixin.blocks;

import btw.block.blocks.KilnBlock;
import btw.block.blocks.SawBlock;
import btw.block.blocks.ScrewPumpBlock;
import com.itlesports.nightmaremode.block.tileEntities.MachineSpeedTileEntity;
import net.minecraft.src.Block;
import net.minecraft.src.ITileEntityProvider;
import net.minecraft.src.Material;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({SawBlock.class, ScrewPumpBlock.class, KilnBlock.class})
public abstract class ScheduledMachineTileEntityMixin extends Block implements ITileEntityProvider {
    protected ScheduledMachineTileEntityMixin(int id, Material material) {
        super(id, material);
    }

    @Override
    public boolean hasTileEntity() {
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World world) {
        return new MachineSpeedTileEntity();
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, int blockId, int metadata) {
        super.breakBlock(world, x, y, z, blockId, metadata);
        world.removeBlockTileEntity(x, y, z);
    }
}
