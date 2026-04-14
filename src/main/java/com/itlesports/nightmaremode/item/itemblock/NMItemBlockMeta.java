package com.itlesports.nightmaremode.item.itemblock;

import com.itlesports.nightmaremode.block.blocks.templates.BlockMetaMultiTextured;
import net.minecraft.src.ItemStack;

/**
 * ItemBlock companion for {@link BlockMetaMultiTextured}.
 *
 * <p>Item damage maps 1:1 to block metadata, so each variant in the group
 * is addressable as a distinct item stack (e.g. for give commands, loot
 * tables, and creative inventory if the group is not hidden from EMI).</p>
 *
 * <p>Registration (once per group, not once per variant):</p>
 * <pre>
 *   Item.itemsList[myGroup.blockID] =
 *       new NMItemBlockMeta(myGroup.blockID - 256, myGroup);
 * </pre>
 */
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
        // delegates the name to the BlockMetaMultiTextured instance. it handles the localization
        return metaBlock.getUnlocalizedName(stack.getItemDamage());
    }
}