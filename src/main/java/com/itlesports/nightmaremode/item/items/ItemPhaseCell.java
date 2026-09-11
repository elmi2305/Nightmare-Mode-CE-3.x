package com.itlesports.nightmaremode.item.items;

import com.itlesports.nightmaremode.block.blocks.BlockPhasePortalFrame;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.Block;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;

public class ItemPhaseCell extends Item {
    public ItemPhaseCell(int id) {
        super(id);
        this.setMaxStackSize(1);
        this.setMaxDamage(4);
        this.setCreativeTab(CreativeTabs.tabTools);
        this.setUnlocalizedName("ifhyPhaseCell");
        this.setTextureName("nightmare:ifhyPhaseSteelCharge");
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z,
                             int side, float hitX, float hitY, float hitZ) {
        if (!(Block.blocksList[world.getBlockId(x, y, z)] instanceof BlockPhasePortalFrame frame)) return false;
        if (!frame.tryToCreatePortal(world, x, y, z, player)) return true;
        if (!world.isRemote && !player.capabilities.isCreativeMode) stack.damageItem(1, player);
        return true;
    }
}
