package com.itlesports.nightmaremode.item.itemblock;

import com.itlesports.nightmaremode.block.blocks.BlockTerrainExtractor;
import net.minecraft.src.ItemBlock;
import net.minecraft.src.ItemStack;

public class ItemBlockTerrainExtractor extends ItemBlock {
    public ItemBlockTerrainExtractor(int id) {
        super(id);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        int metadata = Math.max(0, Math.min(BlockTerrainExtractor.TYPES.length - 1, stack.getItemDamage()));
        return "tile.ifhy" + BlockTerrainExtractor.TYPES[metadata] + "Extractor";
    }
}
