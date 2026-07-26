package com.itlesports.nightmaremode.item.items.template;

import api.item.items.ShovelItem;
import com.itlesports.nightmaremode.util.NMFields;
import net.minecraft.src.*;

public class NMShovelItem extends ShovelItem {
    private final float strength;

    public NMShovelItem(int id, EnumToolMaterial material, int durability, float strMultiplier) {
        super(id, material);
        this.setMaxDamage(durability);
        this.setMaxStackSize(1);
        this.setCreativeTab(CreativeTabs.tabTools);
        this.strength = strMultiplier;
    }

    @Override
    public float getStrVsBlock(ItemStack stack, World world, Block block, int i, int j, int k) {
        return super.getStrVsBlock(stack, world, block, i, j, k) * strength;
    }

    @Override
    public boolean isDamageable() {
        return true;
    }



    @Override
    public String getModId() {
        return NMFields.modID;
    }
}
