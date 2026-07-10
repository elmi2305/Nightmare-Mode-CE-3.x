package com.itlesports.nightmaremode.item.items.template;

import com.itlesports.nightmaremode.util.NMFields;
import net.minecraft.src.*;

public class NMToolItem extends ItemTool {
    private final float strength;

    public NMToolItem(int id, EnumToolMaterial material, Block[] blocksEffectiveAgainst, int durability, float strMultiplier) {
        super(id, 0, material, blocksEffectiveAgainst);
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
    public float getStrVsBlock(ItemStack stack, Block block) {
        return super.getStrVsBlock(stack, block) * strength;
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
