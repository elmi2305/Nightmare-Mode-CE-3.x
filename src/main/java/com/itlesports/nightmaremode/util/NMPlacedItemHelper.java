package com.itlesports.nightmaremode.util;

import api.util.MiscUtils;
import api.world.WorldUtils;
import btw.block.BTWBlocks;
import btw.block.tileentity.PlacedToolTileEntity;
import btw.item.BTWItems;
import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.item.items.template.ItemKnife;
import net.minecraft.src.Block;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;

public final class NMPlacedItemHelper {
    private NMPlacedItemHelper() {}

    public static boolean isSupported(Item item) {
        return item == Item.flint || item == NMItems.flintChip || item == NMItems.soulFlint
                || item == BTWItems.sharpStone
                || item == NMItems.sharpTwig || item == NMItems.sharpBarkTwig
                || item instanceof ItemKnife
                || item == NMItems.woodCup || item == NMItems.cupOfSap || item == NMItems.thickenedSap;
    }

    public static boolean place(ItemStack stack, EntityPlayer player, World world, int x, int y, int z,
                                int side) {
        if (!player.isUsingSpecialKey() || !isSupported(stack.getItem())
                || !player.canPlayerEdit(x, y, z, side, stack)) return false;

        int placeX = x;
        int placeY = y;
        int placeZ = z;
        boolean cup = isCup(stack.getItem());
        if (WorldUtils.isReplaceableBlock(world, x, y, z)) {
            --y;
            side = 1;
        } else {
            if (cup && side != 1) return false;
            switch (side) {
                case 0: --placeY; break;
                case 1: ++placeY; break;
                case 2: --placeZ; break;
                case 3: ++placeZ; break;
                case 4: --placeX; break;
                case 5: ++placeX; break;
                default: return false;
            }
        }
        if (!WorldUtils.doesBlockHaveCenterHardpointToFacing(world, x, y, z, side, true)
                || !BTWBlocks.placedTool.canPlaceBlockAt(world, placeX, placeY, placeZ)) return false;
        Block support = Block.blocksList[world.getBlockId(x, y, z)];
        if (support == null || !canStick(stack.getItem(), support, world, x, y, z)) return false;

        int facing = side >= 2 ? Block.getOppositeFacing(side) : MiscUtils.convertOrientationToFlatBlockFacing(player);
        int metadata = BTWBlocks.placedTool.setFacing(0, facing);
        metadata = BTWBlocks.placedTool.setVerticalOrientation(metadata, side >= 2 ? 2 : Block.getOppositeFacing(side));
        if (!world.setBlockAndMetadataWithNotify(placeX, placeY, placeZ, BTWBlocks.placedTool.blockID, metadata)) return false;
        TileEntity tile = world.getBlockTileEntity(placeX, placeY, placeZ);
        if (!(tile instanceof PlacedToolTileEntity)) return false;
        ItemStack placed = stack.copy();
        placed.stackSize = 1;
        ((PlacedToolTileEntity)tile).setToolStack(placed);
        if (!world.isRemote) {
            world.playSoundEffect(placeX + 0.5D, placeY + 0.5D, placeZ + 0.5D,
                    support.getPlaceSoundName(world, x, y, z), 1.0F, 0.8F);
        }
        --stack.stackSize;
        return true;
    }

    private static boolean canStick(Item item, Block block, World world, int x, int y, int z) {
        if (isCup(item)) return true;
        if (!block.canToolsStickInBlock(world, x, y, z)) return false;
        if (item == NMItems.sharpTwig || item == NMItems.sharpBarkTwig || item instanceof ItemKnife)
            return block.areAxesEffectiveOn() || block.areShovelsEffectiveOn();
        return block.blockMaterial == net.minecraft.src.Material.rock;
    }

    public static boolean isCup(Item item) {
        return item == NMItems.woodCup || item == NMItems.cupOfSap || item == NMItems.thickenedSap;
    }
}
