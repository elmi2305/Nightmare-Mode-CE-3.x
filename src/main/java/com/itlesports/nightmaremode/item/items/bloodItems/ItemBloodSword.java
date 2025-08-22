package com.itlesports.nightmaremode.item.items.bloodItems;

import btw.item.items.SwordItem;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.EnumToolMaterial;

public class ItemBloodSword extends SwordItem implements IBloodTool{
    public ItemBloodSword(int par1, EnumToolMaterial par2EnumToolMaterial, int iMaxUses) {
        super(par1, par2EnumToolMaterial);
        this.maxStackSize = 1;
        this.setMaxDamage(iMaxUses);
        this.setCreativeTab(CreativeTabs.tabCombat);
        this.setInfernalMaxNumEnchants(4);
        this.setBuoyant();
        this.setUnlocalizedName("nmBloodSword");
    }
}
