package com.itlesports.nightmaremode.item.items;

import api.item.items.ShovelItem;
import com.itlesports.nightmaremode.util.underworld.IUnderworldTieredTool;
import com.itlesports.nightmaremode.util.underworld.UnderworldToolTier;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.EnumToolMaterial;

public class ItemUnderworldShovel extends ShovelItem implements IUnderworldTieredTool {
    private final UnderworldToolTier tier;

    public ItemUnderworldShovel(int id, UnderworldToolTier tier, int durability) {
        super(id, EnumToolMaterial.SOULFORGED_STEEL, durability);
        this.tier = tier;
        this.setDamageVsEntity(tier == UnderworldToolTier.TUNGSTEN ? 6 : 5);
        this.setCreativeTab(CreativeTabs.tabTools);
        this.setBuoyant();
        this.setInfernalMaxNumEnchants(4);
    }

    @Override public UnderworldToolTier getUnderworldToolTier() { return tier; }
    @Override public String getModId() { return "nightmare"; }
}
