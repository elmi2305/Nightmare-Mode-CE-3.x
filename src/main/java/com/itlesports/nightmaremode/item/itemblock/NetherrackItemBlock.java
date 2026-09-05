package com.itlesports.nightmaremode.item.itemblock;

import net.minecraft.src.Block;
import btw.item.blockitems.NetherrackBlockItem;

public class NetherrackItemBlock extends NetherrackBlockItem {
    public NetherrackItemBlock(int itemId) {
        super(itemId);
        this.setHasSubtypes(true);
    }

    @Override
    public int getMetadata(int iItemDamage) {
        if(this.itemID == Block.netherrack.blockID){
            return iItemDamage;
        }
        return super.getMetadata(iItemDamage);
    }

}
