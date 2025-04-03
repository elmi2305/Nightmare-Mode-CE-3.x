package com.itlesports.nightmaremode.item.items;

import net.minecraft.src.*;

public class ItemBandage extends ItemFood {

    public ItemBandage(int par1, int par2, float par3, boolean par4) {
        super(par1, par2, par3, par4);
        this.setUnlocalizedName("nmBandage");
        this.setAlwaysEdible();
        this.maxStackSize = 2;
        this.setCreativeTab(CreativeTabs.tabCombat);
    }
    private long timeUntilUsage;

    @Override
    public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) {
        // safety check in case timeUntilUsage is kept from previous world
        if(this.timeUntilUsage - par2World.getTotalWorldTime() > 200){
            this.timeUntilUsage = 0;
        }
        if (par2World.getTotalWorldTime() >= this.timeUntilUsage) {
            par3EntityPlayer.setItemInUse(par1ItemStack, this.getMaxItemUseDuration(par1ItemStack));
        } else {
            par3EntityPlayer.onCantConsume();
        }
        return par1ItemStack;
    }

    @Override
    public ItemStack onEaten(ItemStack itemStack, World world, EntityPlayer player) {
        if (!player.capabilities.isCreativeMode) {
            --itemStack.stackSize;
        }
        if (!world.isRemote) {
            player.heal(1f);
            player.addPotionEffect(new PotionEffect(Potion.regeneration.id,40,1));
            this.timeUntilUsage = world.getTotalWorldTime() + 200;
        }
        return itemStack;
    }

    @Override
    public int getItemUseWarmupDuration(){
        return 150;
    }

    @Override
    public void updateUsingItem(ItemStack stack, World world, EntityPlayer player) {
        int iUseCount = player.getItemInUseCount();
        if (this.getMaxItemUseDuration(stack) - iUseCount > this.getItemUseWarmupDuration()) {
            if (!world.isRemote && iUseCount % 4 == 0) {
                int iDamage = stack.getItemDamage();
                --iDamage;
                if (iDamage > 0) {
                    stack.setItemDamage(iDamage);
                } else {
                    player.setItemInUseCount(1);
                }
            }
        }
    }

    @Override
    public int getMaxItemUseDuration(ItemStack par1ItemStack) {
        return 140;
    }
}
