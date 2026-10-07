package com.itlesports.nightmaremode.item.items;

import api.item.items.PlaceAsBlockItem;
import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.util.interfaces.INetherItem;
import net.minecraft.src.CreativeTabs;

public class ItemPlaceableNetherBrick extends PlaceAsBlockItem implements INetherItem {
    public ItemPlaceableNetherBrick(int id) {
        super(id);
        this.setUnlocalizedName("fcItemBrickNether");
        this.setTextureName("btw:nether_brick");
        this.setCreativeTab(CreativeTabs.tabMaterials);
    }

    @Override
    public int getBlockID() {
        return NMBlocks.placedNetherBrick.blockID;
    }
}
