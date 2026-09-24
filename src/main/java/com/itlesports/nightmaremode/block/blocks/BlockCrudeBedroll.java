package com.itlesports.nightmaremode.block.blocks;

import btw.block.blocks.BedrollBlock;
import com.itlesports.nightmaremode.block.tileEntities.CrudeBedrollTileEntity;
import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.*;

import java.util.Random;

public class BlockCrudeBedroll extends BedrollBlock implements ITileEntityProvider {
    public BlockCrudeBedroll(int blockId) {
        super(blockId);
        this.isBlockContainer = true;
        this.setUnlocalizedName("ifhyCrudeBedroll");
        this.setTextureName("nightmare:ifhyCrudeBedroll");
    }

    @Override
    public TileEntity createNewTileEntity(World world) {
        return new CrudeBedrollTileEntity();
    }

    @Override
    public int idDropped(int meta, Random random, int fortune) {
        return 0;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, int oldId, int oldMeta) {
        TileEntity tile = world.getBlockTileEntity(x, y, z);
        if (!world.isRemote && !isBlockHeadOfBed(oldMeta)
                && tile instanceof CrudeBedrollTileEntity bedroll && bedroll.getUses() < 3) {
            this.dropBlockAsItem_do(world, x, y, z, new ItemStack(NMItems.crudeBedroll, 1, bedroll.getUses()));
        }
        world.removeBlockTileEntity(x, y, z);
        super.breakBlock(world, x, y, z, oldId, oldMeta);
    }

    @Override
    public int idPicked(World world, int x, int y, int z) {
        return NMItems.crudeBedroll.itemID;
    }

    @Override
    public void registerIcons(IconRegister register) {
        this.blockIcon = register.registerIcon("nightmare:ifhyCrudeBedroll");
    }
}
