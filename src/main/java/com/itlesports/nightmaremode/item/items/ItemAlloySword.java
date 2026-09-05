package com.itlesports.nightmaremode.item.items;

import api.item.items.SwordItem;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.src.AttributeModifier;
import net.minecraft.src.EnumToolMaterial;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.SharedMonsterAttributes;

public class ItemAlloySword extends SwordItem {
    private final double weaponDamage;
    private final int enchantability;
    private final int repairItemID;

    public ItemAlloySword(int id, EnumToolMaterial material, int durability, double weaponDamage,
                          int enchantability, int repairItemID) {
        super(id, material);
        this.setMaxDamage(durability);
        this.weaponDamage = weaponDamage;
        this.enchantability = enchantability;
        this.repairItemID = repairItemID;
    }

    @Override
    public Multimap getItemAttributeModifiers() {
        Multimap modifiers = HashMultimap.create();
        modifiers.put(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(),
                new AttributeModifier(Item.field_111210_e, "Weapon modifier", this.weaponDamage, 0));
        return modifiers;
    }

    @Override
    public int getItemEnchantability() {
        return this.enchantability;
    }

    @Override
    public boolean getIsRepairable(ItemStack tool, ItemStack material) {
        return material != null && material.itemID == this.repairItemID;
    }
}
