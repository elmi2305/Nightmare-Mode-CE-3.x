package com.itlesports.nightmaremode.item.items;

import btw.block.BTWBlocks;
import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.item.items.template.NMItem;
import net.minecraft.src.Block;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Facing;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;

public class ItemDrill extends NMItem {
    public ItemDrill(int itemID) {
        super(itemID);
        this.maxStackSize = 1;
        this.setMaxDamage(1);
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z,
                             int facing, float clickX, float clickY, float clickZ) {
        int blockID = world.getBlockId(x, y, z);
        if (facing < 2 || facing > 5 || (blockID != Block.wood.blockID && blockID != BTWBlocks.bloodWoodLog.blockID)) {
            return false;
        }

        int tapX = x + Facing.offsetsXForSide[facing];
        int tapY = y + Facing.offsetsYForSide[facing];
        int tapZ = z + Facing.offsetsZForSide[facing];
        if (!player.canPlayerEdit(tapX, tapY, tapZ, facing, stack)
                || !world.canPlaceEntityOnSide(NMBlocks.sapTap.blockID, tapX, tapY, tapZ, false, facing, player, stack)) {
            return false;
        }

        if (world.isRemote) {
            return true;
        }

        Block tap = NMBlocks.sapTap;
        int metadata = tap.onBlockPlaced(world, tapX, tapY, tapZ, facing, clickX, clickY, clickZ, 0);
        metadata = tap.preBlockPlacedBy(world, tapX, tapY, tapZ, metadata, player);
        if (!world.setBlockAndMetadataWithNotify(tapX, tapY, tapZ, tap.blockID, metadata)) {
            return false;
        }

        tap.onBlockPlacedBy(world, tapX, tapY, tapZ, player, stack);
        tap.onPostBlockPlaced(world, tapX, tapY, tapZ, metadata);
        world.notifyNearbyAnimalsOfPlayerBlockAddOrRemove(player, tap, tapX, tapY, tapZ);
        world.playSoundEffect(tapX + 0.5D, tapY + 0.5D, tapZ + 0.5D,
                tap.getPlaceSoundName(world, tapX, tapY, tapZ), 1.0F, 0.8F);
        stack.damageItem(2, player);
        return true;
    }
}
