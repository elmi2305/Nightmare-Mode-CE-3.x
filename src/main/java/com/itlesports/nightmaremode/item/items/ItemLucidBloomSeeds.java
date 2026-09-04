package com.itlesports.nightmaremode.item.items;

import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.block.blocks.BlockTallFlower;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;

public class ItemLucidBloomSeeds extends Item {
    public ItemLucidBloomSeeds(int id) {
        super(id);
        this.setCreativeTab(CreativeTabs.tabMaterials);
        this.setUnlocalizedName("nmLucidBloomSeeds");
        this.setTextureName("nightmare:nmLucidBloomSeeds");
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z,
                             int side, float hitX, float hitY, float hitZ) {
        if (side != 1 || !player.canPlayerEdit(x, y, z, side, stack)
                || !player.canPlayerEdit(x, y + 1, z, side, stack)) return false;
        int ground = world.getBlockId(x, y, z);
        int groundMeta = world.getBlockMetadata(x, y, z);
        boolean fertile = ground == NMBlocks.underFlowerDirts.blockID
                && (groundMeta == NMBlocks.META_FLOWER_GRASS || groundMeta == NMBlocks.META_FLOWER_DIRT);
        if (!fertile || !world.isAirBlock(x, y + 1, z)) return false;
        if (world.setBlock(x, y + 1, z, NMBlocks.yellowFlowerRoots.blockID, BlockTallFlower.LUCID_BLOOM, 3)
                && !player.capabilities.isCreativeMode) stack.stackSize--;
        return true;
    }

    @Override public String getModId() { return "nightmare"; }
}
