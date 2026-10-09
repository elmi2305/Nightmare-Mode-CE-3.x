package com.itlesports.nightmaremode.item.items;

import com.itlesports.nightmaremode.item.items.template.NMItem;

public class ItemPlaceableStick extends NMItem {
    public ItemPlaceableStick(int id) {
        super(id);
    }

    @Override public float getVisualVerticalOffsetAsBlock() { return -0.15f; }
    @Override public float getVisualRollOffsetAsBlock() { return 45.0F + 90f; }
    @Override public float getBlockBoundingBoxHeight() { return 0.75F; }
    @Override public float getVisualHorizontalOffsetAsBlock() { return 0.225F; }

}
