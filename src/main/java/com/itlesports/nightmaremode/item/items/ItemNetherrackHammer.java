package com.itlesports.nightmaremode.item.items;

import com.itlesports.nightmaremode.util.interfaces.INetherItem;
import net.minecraft.src.EnumToolMaterial;

public class ItemNetherrackHammer extends ItemHammer implements INetherItem {
    public ItemNetherrackHammer(int id) {
        super(id, EnumToolMaterial.STONE);
    }
}
