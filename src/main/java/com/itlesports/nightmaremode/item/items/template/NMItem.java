package com.itlesports.nightmaremode.item.items.template;

import api.item.PlaceableAsItem;
import com.itlesports.nightmaremode.util.NMFields;
import net.minecraft.src.Item;

public class NMItem extends Item implements PlaceableAsItem {
    private boolean indestructible;

    public NMItem(int id) {
        super(id);
    }

    @Override
    public boolean isDamageable() {
        return !this.indestructible;
    }

    public String getModId() {
        return NMFields.modID;
    }

    public NMItem setIndestructible(){
        this.indestructible = true;
        return this;
    }

    public boolean isIndestructible(){
        return this.indestructible;
    }

    @Override public float getVisualVerticalOffsetAsBlock() { return 0.09F; }
    @Override public float getVisualHorizontalOffsetAsBlock() { return 0.5F; }
    @Override public float getVisualRollOffsetAsBlock() { return -90.0F; }
    @Override public float getBlockBoundingBoxHeight() { return 0.25F; }
    @Override public float getBlockBoundingBoxWidth() { return 0.25F; }
}
