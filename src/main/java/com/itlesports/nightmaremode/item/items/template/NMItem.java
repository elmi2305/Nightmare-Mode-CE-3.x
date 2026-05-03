package com.itlesports.nightmaremode.item.items.template;

import com.itlesports.nightmaremode.util.NMFields;
import net.minecraft.src.Item;

public class NMItem extends Item {
    public NMItem(int id) {
        super(id);
    }

    public String getModId() {
        return NMFields.modID;
    }
}