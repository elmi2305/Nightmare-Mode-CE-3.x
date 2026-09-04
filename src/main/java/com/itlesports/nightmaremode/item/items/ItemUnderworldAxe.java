package com.itlesports.nightmaremode.item.items;

import api.item.items.AxeItem;
import com.itlesports.nightmaremode.util.underworld.IUnderworldTieredTool;
import com.itlesports.nightmaremode.util.underworld.UnderworldToolTier;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.EnumToolMaterial;

public class ItemUnderworldAxe extends AxeItem implements IUnderworldTieredTool {
    private final UnderworldToolTier tier;

    public ItemUnderworldAxe(int id, UnderworldToolTier tier, int durability) {
        super(id, EnumToolMaterial.SOULFORGED_STEEL);
        this.tier = tier;
        this.setMaxDamage(durability);
        this.setDamageVsEntity(tier == UnderworldToolTier.TUNGSTEN ? 9 : 8);
        this.setCreativeTab(CreativeTabs.tabTools);
        this.setBuoyant();
        this.setInfernalMaxNumEnchants(4);
    }

    @Override public UnderworldToolTier getUnderworldToolTier() { return tier; }
    @Override public String getModId() { return "nightmare"; }
}
