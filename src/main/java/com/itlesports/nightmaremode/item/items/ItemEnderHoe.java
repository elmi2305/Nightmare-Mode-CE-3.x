package com.itlesports.nightmaremode.item.items;

import api.item.items.HoeItem;
import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.Block;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.EnumToolMaterial;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;

public class ItemEnderHoe extends HoeItem {
    public ItemEnderHoe(int id) {
        super(id, EnumToolMaterial.SOULFORGED_STEEL);
        this.setMaxDamage(3200);
        this.efficiencyOnProperMaterial = 16.0F;
        this.setDamageVsEntity(5);
        this.setCreativeTab(CreativeTabs.tabTools);
    }
    @Override
    public float getStrVsBlock(ItemStack stack, World world, Block block, int x, int y, int z) {
        if (block == Block.whiteStone && world.getBlockMetadata(x, y, z) == 1) {
            return this.efficiencyOnProperMaterial;
        }
        return super.getStrVsBlock(stack, world, block, x, y, z);
    }
    @Override public boolean getIsRepairable(ItemStack tool, ItemStack material) { return material != null && material.getItem() == NMItems.phaseSteelIngot; }
}
