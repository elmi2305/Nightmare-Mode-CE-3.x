package com.itlesports.nightmaremode.item.items;

import api.item.items.ShovelItem;
import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.EnumToolMaterial;
import net.minecraft.src.ItemStack;

public class ItemEnderShovel extends ShovelItem {
    public ItemEnderShovel(int id) {
        super(id, EnumToolMaterial.SOULFORGED_STEEL);
        this.setMaxDamage(3200);
        this.efficiencyOnProperMaterial = 16.0F;
        this.setDamageVsEntity(6);
        this.setCreativeTab(CreativeTabs.tabTools);
    }
    @Override public boolean getIsRepairable(ItemStack tool, ItemStack material) { return material != null && material.getItem() == NMItems.phaseSteelIngot; }
}
