package com.itlesports.nightmaremode.item.items;

import api.item.items.AxeItem;
import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.EnumToolMaterial;
import net.minecraft.src.ItemStack;

public class ItemEnderAxe extends AxeItem {
    public ItemEnderAxe(int id) {
        super(id, EnumToolMaterial.SOULFORGED_STEEL);
        this.setMaxDamage(3200);
        this.efficiencyOnProperMaterial = 16.0F;
        this.setDamageVsEntity(9);
        this.setCreativeTab(CreativeTabs.tabTools);
    }
    @Override public boolean getIsRepairable(ItemStack tool, ItemStack material) { return material != null && material.getItem() == NMItems.phaseSteelIngot; }
}
