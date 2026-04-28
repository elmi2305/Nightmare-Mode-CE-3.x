package com.itlesports.nightmaremode.item.items;

import btw.item.items.NetherStarItem;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;

public class NMNetherStarItem extends NetherStarItem {
    public NMNetherStarItem(int iItemID) {
        super(iItemID);
    }

    public String getModId() {
        return "nightmare";
    }

    @Override
    public boolean isDamageable() {
        return false;
    }

    @Override
    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10) {
        System.out.println(par3World.isRemote);
        return super.onItemUse(par1ItemStack, par2EntityPlayer, par3World, par4, par5, par6, par7, par8, par9, par10);
    }
}
