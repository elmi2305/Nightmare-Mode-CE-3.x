package com.itlesports.nightmaremode.item.items.template;

public class NMItemIndestructible extends NMItem{
    public NMItemIndestructible(int id) {
        super(id);
    }
    @Override
    public boolean isDamageable() {
        return false;
    }
}
