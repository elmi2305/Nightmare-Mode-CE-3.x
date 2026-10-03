package com.itlesports.nightmaremode.util;

import com.itlesports.nightmaremode.block.tileEntities.TileEntityHammerAnvil;
import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.item.NMItems;
import btw.item.BTWItems;
import net.minecraft.src.Block;
import net.minecraft.src.EntityItem;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;

public abstract class HammerAnvilHelper {
    public static boolean isAnvil(int id) {
        return id == Block.anvil.blockID || NMBlocks.stoneAnvil != null && id == NMBlocks.stoneAnvil.blockID
                || NMBlocks.ironAnvil != null && id == NMBlocks.ironAnvil.blockID
                || NMBlocks.diamondAnvil != null && id == NMBlocks.diamondAnvil.blockID
                || NMBlocks.netherrackAnvil != null && id == NMBlocks.netherrackAnvil.blockID;
    }

    public static boolean isPristine(World world, int x, int y, int z) {
        TileEntity tile = world.getBlockTileEntity(x, y, z);
        return !(tile instanceof TileEntityHammerAnvil anvil) || anvil.isPristine();
    }

    public static void dropOnRemoval(World world, int x, int y, int z, int id) {
        if (world.isRemote) return;
        TileEntity tile = world.getBlockTileEntity(x, y, z);
        boolean pristine = !(tile instanceof TileEntityHammerAnvil anvil) || anvil.isPristine();
        if (tile instanceof TileEntityHammerAnvil anvil && !anvil.claimRemoval()) return;
        ItemStack drop;
        if (pristine) {
            drop = new ItemStack(id, 1, 0);
        } else {
            Item material;
            int count;
            if (id == NMBlocks.stoneAnvil.blockID) material = BTWItems.stone;
            else if (id == NMBlocks.ironAnvil.blockID) material = BTWItems.ironNugget;
            else if (id == NMBlocks.netherrackAnvil.blockID) material = NMItems.netherrackChunk;
            else if (id == NMBlocks.diamondAnvil.blockID) material = BTWItems.diamondIngot;
            else material = BTWItems.soulforgedSteelIngot;
            count = id == NMBlocks.diamondAnvil.blockID || id == Block.anvil.blockID
                    ? 5 + world.rand.nextInt(5) : 16 + world.rand.nextInt(9);
            drop = new ItemStack(material, count);
        }
        while (drop.stackSize > 0) {
            ItemStack stack = drop.splitStack(Math.min(drop.stackSize, drop.getMaxStackSize()));
            EntityItem entity = new EntityItem(world, x + 0.5D, y + 0.5D, z + 0.5D, stack);
            entity.delayBeforeCanPickup = 10;
            world.spawnEntityInWorld(entity);
        }
    }

    public static void collectPristineAnvil(World world, int x, int y, int z) {
        if (world.isRemote) return;
        TileEntity tile = world.getBlockTileEntity(x, y, z);
        if (!(tile instanceof TileEntityHammerAnvil)) {
            tile = new TileEntityHammerAnvil();
            world.setBlockTileEntity(x, y, z, tile);
        }
        ((TileEntityHammerAnvil)tile).claimRemoval();
    }

    public static boolean tryHammerHeldItem(World world, int x, int y, int z, EntityPlayer player, TileEntityHammerAnvil anvil) {
        return anvil != null && anvil.tryStartHammerOperation(player);
    }
}
