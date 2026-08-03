package com.itlesports.nightmaremode.item.itemblock;

import com.itlesports.nightmaremode.block.blocks.templates.BlockMetaMultiTextured;
import com.itlesports.nightmaremode.util.interfaces.INetherItem;

/** Metadata-preserving ItemBlock whose variants survive Nether entry. */
public class NMNetherItemBlockMeta extends NMItemBlockMeta implements INetherItem {
    public NMNetherItemBlockMeta(int itemId, BlockMetaMultiTextured block) {
        super(itemId, block);
    }
}
