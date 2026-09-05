package com.itlesports.nightmaremode.item.items;

import api.item.items.ShovelItem;
import net.minecraft.src.Block;
import net.minecraft.src.EnumToolMaterial;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;

public class ItemAlloyShovel extends ShovelItem {
    private final float speedMultiplier;
    private final int enchantability;
    private final int repairItemID;

    public ItemAlloyShovel(int id, int durability, int damage, float speedMultiplier, int enchantability, int repairItemID) {
        super(id, EnumToolMaterial.EMERALD);
        this.setMaxDamage(durability);
        this.setDamageVsEntity(damage);
        this.speedMultiplier = speedMultiplier;
        this.enchantability = enchantability;
        this.repairItemID = repairItemID;
    }

    @Override
    public float getStrVsBlock(ItemStack stack, World world, Block block, int x, int y, int z) {
        return super.getStrVsBlock(stack, world, block, x, y, z) * this.speedMultiplier;
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
