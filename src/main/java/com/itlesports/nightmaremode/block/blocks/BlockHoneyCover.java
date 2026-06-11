package com.itlesports.nightmaremode.block.blocks;

import api.block.blocks.GroundCoverBlock;
import btw.BTWMod;
import net.minecraft.src.*;

import java.util.Random;

public class BlockHoneyCover extends GroundCoverBlock {
    public BlockHoneyCover(int iBlockID, Material material) {
        super(iBlockID, material);
        this.setUnlocalizedName("nmHoneyCover");
        this.setTextureName("nightmare:honey");
    }

    public void harvestBlock(World world, EntityPlayer player, int i, int j, int k, int iMetadata) {
        int iItemID = Item.snowball.itemID;
        int amount = iMetadata / 2 + 1;
        this.dropBlockAsItem_do(world, i, j, k, new ItemStack(iItemID, amount, 0));
        world.setBlockToAir(i, j, k);
        player.addStat(StatList.mineBlockStatArray[this.blockID], 1);
    }

    public int idDropped(int par1, Random par2Random, int par3) {
        return Item.snowball.itemID;
    }

    public int quantityDropped(Random par1Random) {
        return 1;
    }

    public boolean canDropFromExplosion(Explosion explosion) {
        return false;
    }

    public void onEntityCollidedWithBlock(World world, int i, int j, int k, Entity entity) {
        if (entity.isAffectedByMovementModifiers() && entity.onGround) {
            entity.motionX *= 0.5d;
            entity.motionZ *= 0.5d;
        }
    }
}
