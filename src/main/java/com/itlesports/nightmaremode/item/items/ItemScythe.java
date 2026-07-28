package com.itlesports.nightmaremode.item.items;

import api.item.items.SwordItem;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.src.AttributeModifier;
import net.minecraft.src.Enchantment;
import net.minecraft.src.EnumToolMaterial;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.SharedMonsterAttributes;

public class ItemScythe extends SwordItem {
    private final float weaponDamage;

    public ItemScythe(int id, EnumToolMaterial material, float weaponDamage) {
        super(id, material);
        this.weaponDamage = weaponDamage;
    }

    @Override
    public boolean isEnchantmentApplicable(Enchantment enchantment) {
        return enchantment == Enchantment.sharpness
                || enchantment == Enchantment.smite
                || enchantment == Enchantment.looting
                || enchantment == Enchantment.unbreaking;
    }

    @Override
    public Multimap getItemAttributeModifiers() {
        Multimap modifiers = HashMultimap.create();
        modifiers.put(
                SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(),
                new AttributeModifier(Item.field_111210_e, "Weapon modifier", this.weaponDamage, 0)
        );
        return modifiers;
    }
}
