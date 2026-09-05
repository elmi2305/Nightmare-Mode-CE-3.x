package com.itlesports.nightmaremode.item.items;

import net.minecraft.src.EnumToolMaterial;

public class ItemAlloyScythe extends ItemScythe {
    public ItemAlloyScythe(int id, float weaponDamage, int durability) {
        super(id, EnumToolMaterial.EMERALD, weaponDamage);
        this.setMaxDamage(durability);
    }
}
