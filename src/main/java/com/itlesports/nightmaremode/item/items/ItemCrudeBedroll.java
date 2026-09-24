package com.itlesports.nightmaremode.item.items;

import btw.item.items.BedItem;
import com.itlesports.nightmaremode.block.tileEntities.CrudeBedrollTileEntity;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;

public class ItemCrudeBedroll extends BedItem {
    public ItemCrudeBedroll(int itemId, int blockId) {
        super(itemId, blockId);
        this.setMaxStackSize(1);
        this.setMaxDamage(3);
        this.setUnlocalizedName("ifhyCrudeBedroll");
        this.setTextureName("nightmare:ifhyCrudeBedroll");
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z,
                             int side, float hitX, float hitY, float hitZ) {
        int uses = stack.getItemDamage();
        boolean placed = super.onItemUse(stack, player, world, x, y, z, side, hitX, hitY, hitZ);
        if (placed && !world.isRemote && side == 1) {
            int bedY = y + 1;
            TileEntity tile = world.getBlockTileEntity(x, bedY, z);
            if (tile instanceof CrudeBedrollTileEntity bedroll) {
                bedroll.setUses(uses);
            }
        }
        return placed;
    }
}
