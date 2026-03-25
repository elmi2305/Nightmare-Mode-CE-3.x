package com.itlesports.nightmaremode.item.items;

import net.minecraft.src.*;

public class ItemLightningBolt extends NMItem {

    public ItemLightningBolt(int id) {
        super(id);
    }

    @Override
    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World w, int par4, int par5, int par6, int par7, float par8, float par9, float par10) {
        if (w.isRemote) {
            return true;
        }
        int var11 = w.getBlockId(par4, par5, par6);
        par4 += Facing.offsetsXForSide[par7];
        par5 += Facing.offsetsYForSide[par7];
        par6 += Facing.offsetsZForSide[par7];
        double var12 = 0.0;
        if (par7 == 1 && Block.blocksList[var11] != null && Block.blocksList[var11].getRenderType() == 11) {
            var12 = 0.5;
        }
        Entity bolt = new EntityLightningBolt(w,par4 + 0.5, (double)par5 + var12 + 1.5, (double)par6 + 0.5);
        w.addWeatherEffect(bolt);
        return true;
    }
}
