package com.itlesports.nightmaremode.item.items;

import com.itlesports.nightmaremode.block.blocks.templates.NMPlaceAsBlockItem;
import api.item.PlaceableAsItem;
import net.minecraft.src.*;

public class ItemWoodCup extends NMPlaceAsBlockItem implements PlaceableAsItem {
    public ItemWoodCup(int itemID, int blockID) {
        super(itemID, blockID);
    }

    @Override public int getMetadata(int damage) { return damage; }

    // legacy saves can still contain cups in a placed-tool tile entity.
    @Override public float getVisualVerticalOffsetAsBlock() { return 0.0F; }
    @Override public float getVisualHorizontalOffsetAsBlock() { return 0.0F; }
    @Override public float getVisualRollOffsetAsBlock() { return 0.0F; }
    @Override public float getBlockBoundingBoxHeight() { return 0.70F; }
    @Override public float getBlockBoundingBoxWidth() { return 0.70F; }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z,
                              int side, float clickX, float clickY, float clickZ) {
        return (player == null || !player.isUsingSpecialKey())
                && super.onItemUse(stack, player, world, x, y, z, side, clickX, clickY, clickZ);
    }

    @Override
    public boolean canPlaceItemBlockOnSide(World world, int x, int y, int z, int side,
                                            EntityPlayer player, ItemStack stack) {
        return (player == null || !player.isUsingSpecialKey())
                && super.canPlaceItemBlockOnSide(world, x, y, z, side, player, stack);
    }
}
