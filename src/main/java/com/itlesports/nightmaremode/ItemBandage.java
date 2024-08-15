package com.itlesports.nightmaremode;

import net.minecraft.src.*;

public class ItemBandage extends ItemFood {

    public ItemBandage(int par1, int par2, float par3, boolean par4) {
        super(par1, par2, par3, par4);
        this.setUnlocalizedName("nmBandage");
        this.setAlwaysEdible();
        this.maxStackSize = 8;
        this.setCreativeTab(CreativeTabs.tabMisc);
    }

    @Override
    public ItemStack onEaten(ItemStack itemStack, World world, EntityPlayer player) {
        if (!player.capabilities.isCreativeMode) {
            --itemStack.stackSize;
        }
        if (!world.isRemote) {
            player.addPotionEffect(new PotionEffect(Potion.heal.id, 1, 0));
        }
        return itemStack;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack par1ItemStack) {
        return 200;
    }
}
