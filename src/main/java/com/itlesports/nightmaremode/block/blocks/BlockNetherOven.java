package com.itlesports.nightmaremode.block.blocks;

import btw.block.blocks.OvenBlock;
import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.block.tileEntities.NetherOvenTileEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

public class BlockNetherOven extends OvenBlock {
    public BlockNetherOven(int id, boolean active) {
        super(id, active, true);
        this.setUnlocalizedName("ifhyNetherOven");
        this.setCreativeTab(active ? null : CreativeTabs.tabDecorations);
    }

    @Override
    public TileEntity createNewTileEntity(World world) {
        return new NetherOvenTileEntity();
    }

    @Override
    public void updateFurnaceBlockState(boolean burning, World world, int x, int y, int z, boolean hasContents) {
        int metadata = world.getBlockMetadata(x, y, z);
        TileEntity tile = world.getBlockTileEntity(x, y, z);
        metadata = hasContents ? metadata | 8 : metadata & 7;
        keepFurnaceInventory = true;
        world.setBlockAndMetadata(x, y, z,
                burning ? NMBlocks.burningNetherOven.blockID : NMBlocks.netherOven.blockID, metadata);
        keepFurnaceInventory = false;
        if (tile != null) {
            tile.validate();
            world.setBlockTileEntity(x, y, z, tile);
        }
    }

    @Override
    public void dropBlockAsItemWithChance(World world, int x, int y, int z, int metadata, float chance, int fortune) {
        if (!world.isRemote) {
            this.dropBlockAsItem_do(world, x, y, z, new ItemStack(NMBlocks.netherOven));
        }
    }

    @Override
    protected int getIDDroppedOnSilkTouch() {
        return NMBlocks.netherOven.blockID;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public int idPicked(World world, int x, int y, int z) {
        return NMBlocks.netherOven.blockID;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons(IconRegister register) {
        super.registerIcons(register);
        this.blockIcon = register.registerIcon("nether_brick");
        this.furnaceIconTop = this.blockIcon;
        this.furnaceIconFront = register.registerIcon(this.isActive ? "furnace_front_lit" : "furnace_front");
    }
}
