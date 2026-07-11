package com.itlesports.nightmaremode.item.items.template;

import com.itlesports.nightmaremode.util.NMFields;
import net.minecraft.src.Item;

public class NMItem extends Item {
    private boolean indestructible;
    private int washedItemID = -1;

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

    public NMItem setWashable(int washID){
        this.washedItemID = washID;
        return this;
    }
    public int getWashResult(){
        return this.washedItemID;
    }
    public boolean isIndestructible(){
        return this.indestructible;
    }
}