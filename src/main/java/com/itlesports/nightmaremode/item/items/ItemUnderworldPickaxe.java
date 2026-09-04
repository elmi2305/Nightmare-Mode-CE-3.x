package com.itlesports.nightmaremode.item.items;

import api.item.items.PickaxeItem;
import com.itlesports.nightmaremode.util.underworld.IUnderworldTieredBlock;
import com.itlesports.nightmaremode.util.underworld.IUnderworldTieredTool;
import com.itlesports.nightmaremode.util.underworld.UnderworldToolTier;
import net.minecraft.src.*;

public class ItemUnderworldPickaxe extends PickaxeItem implements IUnderworldTieredTool {
    private final UnderworldToolTier tier;

    public ItemUnderworldPickaxe(int id, UnderworldToolTier tier, int durability, float efficiency) {
        super(id, EnumToolMaterial.SOULFORGED_STEEL);
        this.tier = tier;
        this.maxStackSize = 1;
        this.setMaxDamage(durability);
        this.efficiencyOnProperMaterial = efficiency;
        this.setCreativeTab(CreativeTabs.tabTools);
        this.setBuoyant();
        this.setInfernalMaxNumEnchants(4);
    }

    @Override
    public UnderworldToolTier getUnderworldToolTier() { return tier; }

    @Override
    public boolean canHarvestBlock(ItemStack stack, World world, Block block, int x, int y, int z) {
        if (block instanceof IUnderworldTieredBlock tiered) {
            return tier.canHarvest(tiered.getRequiredUnderworldTier(world, x, y, z));
        }
        return super.canHarvestBlock(stack, world, block, x, y, z);
    }

    @Override
    public float getStrVsBlock(ItemStack stack, World world, Block block, int x, int y, int z) {
        if (block instanceof IUnderworldTieredBlock tiered) {
            return tier.canHarvest(tiered.getRequiredUnderworldTier(world, x, y, z)) ? efficiencyOnProperMaterial : 0.2F;
        }
        return super.getStrVsBlock(stack, world, block, x, y, z);
    }

    @Override
    public String getModId() { return "nightmare"; }
}
