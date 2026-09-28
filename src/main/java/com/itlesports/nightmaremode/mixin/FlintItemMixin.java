package com.itlesports.nightmaremode.mixin;

import api.item.PlaceableAsItem;
import btw.item.items.FlintItem;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(FlintItem.class)
public abstract class FlintItemMixin implements PlaceableAsItem {
    @Override public float getVisualVerticalOffsetAsBlock() { return 0.3F; }
    @Override public float getVisualHorizontalOffsetAsBlock() { return 0.5F; }
    @Override public float getVisualRollOffsetAsBlock() { return -45.0F; }
    @Override public float getBlockBoundingBoxHeight() { return 0.5F; }
    @Override public float getBlockBoundingBoxWidth() { return 0.3F; }
}
