package com.itlesports.nightmaremode.item.items;

import btw.community.nightmaremode.NightmareMode;
import btw.entity.InfiniteArrowEntity;
import net.minecraft.src.*;

public class ItemAR extends Item {

    public ItemAR(int par1) {
        super(par1);
        this.maxStackSize = 1;
        this.setMaxDamage(2000);
        this.setCreativeTab(CreativeTabs.tabCombat);
        this.setBuoyant();
        this.setUnlocalizedName("nmRifle");
    }

    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!NightmareMode.noHit) {
            world.playSoundAtEntity(player, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

            if (!world.isRemote) {
                InfiniteArrowEntity missile = new InfiniteArrowEntity(world,player,3f);
                missile.setDamage(10);
                world.spawnEntityInWorld(missile);
            }
        }
        return stack;
    }
}
