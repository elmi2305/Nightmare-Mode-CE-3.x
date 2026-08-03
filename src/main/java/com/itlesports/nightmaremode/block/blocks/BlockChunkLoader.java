package com.itlesports.nightmaremode.block.blocks;

import com.itlesports.nightmaremode.world.ChunkLoaderManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.Block;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Icon;
import net.minecraft.src.IconRegister;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Material;
import net.minecraft.src.World;

import java.util.Random;

/** A charged loader permanently keeps only its containing chunk active. */
public class BlockChunkLoader extends Block {
    private Icon sideIcon;
    private Icon topIcon;
    private Icon chargedTopIcon;

    public BlockChunkLoader(int blockID) {
        super(blockID, Material.rock);
        this.setHardness(3.5F);
        this.setResistance(10.0F);
        this.setPicksEffectiveOn();
        this.setCreativeTab(CreativeTabs.tabBlock);
        this.setUnlocalizedName("ifhyChunkLoader");
        this.setTickRandomly(true);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        if (world.getBlockMetadata(x, y, z) != 0) {
            return true;
        }
        ItemStack held = player.getHeldItem();
        if (held == null || held.itemID != Item.diamond.itemID) {
            return true;
        }
        if (!world.isRemote) {
            if (!player.capabilities.isCreativeMode) {
                --held.stackSize;
                if (held.stackSize <= 0) {
                    player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                }
            }
            world.setBlockMetadataWithNotify(x, y, z, 1, 3);
            ChunkLoaderManager.addLoader(world, x, y, z);
        }
        return true;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, int blockID, int metadata) {
        if (!world.isRemote && metadata == 1) {
            ChunkLoaderManager.removeLoader(world, x, y, z);
        }
        super.breakBlock(world, x, y, z, blockID, metadata);
    }

    /** Migrates already-charged loaders whose old dimension-local save entry was lost. */
    @Override
    public void updateTick(World world, int x, int y, int z, Random random) {
        if (!world.isRemote && world.getBlockMetadata(x, y, z) == 1) {
            ChunkLoaderManager.addLoader(world, x, y, z);
        }
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons(IconRegister register) {
        this.sideIcon = register.registerIcon("nightmare:ifhyChunkLoaderSide");
        this.topIcon = register.registerIcon("nightmare:ifhyChunkLoaderTop");
        this.chargedTopIcon = register.registerIcon("nightmare:ifhyChunkLoaderTopCharged");
        this.blockIcon = this.sideIcon;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon(int side, int metadata) {
        if (side == 1) {
            return metadata == 1 ? this.chargedTopIcon : this.topIcon;
        }
        return this.sideIcon;
    }
}
