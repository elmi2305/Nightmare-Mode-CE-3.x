package com.itlesports.nightmaremode.item.items;

import api.item.items.SwordItem;
import com.itlesports.nightmaremode.item.NMItems;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.src.AttributeModifier;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.EnumToolMaterial;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.SharedMonsterAttributes;

public class ItemEnderSword extends SwordItem {
    public ItemEnderSword(int id) {
        super(id, EnumToolMaterial.SOULFORGED_STEEL);
        this.setMaxDamage(3200);
        this.setCreativeTab(CreativeTabs.tabCombat);
    }

    @Override public Multimap getItemAttributeModifiers() {
        Multimap modifiers = HashMultimap.create();
        modifiers.put(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(),
                new AttributeModifier(Item.field_111210_e, "Weapon modifier", 10.0D, 0));
        return modifiers;
    }
    @Override public boolean getIsRepairable(ItemStack tool, ItemStack material) { return material != null && material.getItem() == NMItems.phaseSteelIngot; }
}
