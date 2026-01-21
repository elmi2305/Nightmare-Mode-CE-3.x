package com.itlesports.nightmaremode.item.items;

import btw.block.BTWBlocks;
import btw.item.items.BucketItemFull;
import net.minecraft.src.World;

public class BucketItemMoltenGlass extends BucketItemFull {
    public BucketItemMoltenGlass(int iBlockID) {
        super(iBlockID);
    }

    @Override
    public int getBlockID() {
        return BTWBlocks.placedCementBucket.blockID;
    }

    @Override
    protected boolean attemptPlaceContentsAtLocation(World world, int i, int j, int k) {
        if (world.isAirBlock(i, j, k) || !world.getBlockMaterial(i, j, k).isSolid()) {
            if (!world.isRemote) {
                world.playSoundEffect(i, j, k, "mob.ghast.moan", 0.5f, 2.6f + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8f);
                world.setBlockWithNotify(i, j, k, BTWBlocks.cement.blockID);
            }
            return true;
        }
        return false;
    }
}