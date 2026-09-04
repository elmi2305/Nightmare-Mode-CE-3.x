package com.itlesports.nightmaremode.item.itemblock;

import com.itlesports.nightmaremode.block.blocks.BlockUnderworldOre;
import net.minecraft.src.ItemStack;

public class ItemBlockUnderworldOre extends NMItemBlock {
    public ItemBlockUnderworldOre(int id) {
        super(id);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
    }

    @Override public int getMetadata(int damage) { return damage; }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return BlockUnderworldOre.getOreType(stack.getItemDamage()) == BlockUnderworldOre.TUNGSTEN
                ? "tile.nmTungstenOre" : "tile.nmTitaniumOre";
    }
}
