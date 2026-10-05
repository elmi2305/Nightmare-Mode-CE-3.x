package com.itlesports.nightmaremode.item.items;

import api.item.items.PickaxeItem;
import com.itlesports.nightmaremode.util.NMBlockBreakingRules;
import net.minecraft.src.Block;
import net.minecraft.src.EnumToolMaterial;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;

import java.util.Arrays;

public class ItemAlloyPickaxe extends PickaxeItem {
    private final float speedMultiplier;
    private final int enchantability;
    private final int[] repairItemID;

    public ItemAlloyPickaxe(int id, EnumToolMaterial material, int durability, int damage, float speedMultiplier,
                            int enchantability, int ... repairItemID) {
        super(id, material);
        this.setMaxDamage(durability);
        this.setDamageVsEntity(damage);
        this.speedMultiplier = speedMultiplier;
        this.enchantability = enchantability;
        this.repairItemID = repairItemID;
    }

    @Override
    public float getStrVsBlock(ItemStack stack, World world, Block block, int x, int y, int z) {
        if (block == Block.anvil && NMBlockBreakingRules.isSteelMiningTool(stack)) {
            return this.efficiencyOnProperMaterial * this.speedMultiplier;
        }
        return super.getStrVsBlock(stack, world, block, x, y, z) * this.speedMultiplier;
    }

    @Override
    public boolean canHarvestBlock(ItemStack stack, World world, Block block, int x, int y, int z) {
        return block == Block.anvil && NMBlockBreakingRules.isSteelMiningTool(stack)
                || super.canHarvestBlock(stack, world, block, x, y, z);
    }

    @Override
    public boolean isEfficientVsBlock(ItemStack stack, World world, Block block, int x, int y, int z) {
        return block == Block.anvil && NMBlockBreakingRules.isSteelMiningTool(stack)
                || super.isEfficientVsBlock(stack, world, block, x, y, z);
    }

    @Override
    public int getItemEnchantability() {
        return this.enchantability;
    }

    @Override
    public boolean getIsRepairable(ItemStack tool, ItemStack material) {
        return material != null && Arrays.stream(this.repairItemID).anyMatch(a -> a == material.itemID);
    }
}
