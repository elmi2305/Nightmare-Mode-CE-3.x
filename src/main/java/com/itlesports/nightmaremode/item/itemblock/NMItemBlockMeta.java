package com.itlesports.nightmaremode.item.itemblock;

import com.itlesports.nightmaremode.block.blocks.templates.BlockMetaMultiTextured;
import net.minecraft.src.ItemStack;

public class NMItemBlockMeta extends NMItemBlock {

    private final BlockMetaMultiTextured metaBlock;

    public NMItemBlockMeta(int itemId, BlockMetaMultiTextured block) {
        super(itemId);
        this.metaBlock = block;
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {

        return metaBlock.getUnlocalizedName(stack.getItemDamage());
    }
}
