package com.itlesports.nightmaremode.item.items;

import api.item.items.SwordItem;
import com.google.common.collect.Multimap;
import com.itlesports.nightmaremode.util.underworld.IUnderworldTieredTool;
import com.itlesports.nightmaremode.util.underworld.UnderworldToolTier;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.EnumToolMaterial;
import net.minecraft.src.AttributeModifier;
import net.minecraft.src.SharedMonsterAttributes;

public class ItemUnderworldSword extends SwordItem implements IUnderworldTieredTool {
    private final UnderworldToolTier tier;
    private final float underworldDamage;

    public ItemUnderworldSword(int id, UnderworldToolTier tier, int durability) {
        super(id, EnumToolMaterial.SOULFORGED_STEEL);
        this.tier = tier;
        this.underworldDamage = tier == UnderworldToolTier.TUNGSTEN ? 9.0F : 8.0F;
        this.setMaxDamage(durability);
        this.setCreativeTab(CreativeTabs.tabCombat);
        this.setBuoyant();
        this.setInfernalMaxNumEnchants(4);
    }

    @Override public UnderworldToolTier getUnderworldToolTier() { return tier; }
    @Override public String getModId() { return "nightmare"; }

    @Override
    public Multimap getItemAttributeModifiers() {
        Multimap modifiers = super.getItemAttributeModifiers();
        String attribute = SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName();
        modifiers.removeAll(attribute);
        modifiers.put(attribute, new AttributeModifier(field_111210_e, "Weapon modifier", underworldDamage, 0));
        return modifiers;
    }
}
