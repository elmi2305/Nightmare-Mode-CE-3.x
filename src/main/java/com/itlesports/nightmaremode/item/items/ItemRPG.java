package com.itlesports.nightmaremode.item.items;

import btw.community.nightmaremode.NightmareMode;
import net.minecraft.src.*;

public class ItemRPG extends Item {

    public ItemRPG(int par1) {
        super(par1);
        this.maxStackSize = 1;
        this.setMaxDamage(2000);
        this.setCreativeTab(CreativeTabs.tabCombat);
        this.setBuoyant();
        this.setUnlocalizedName("nmRPG");
    }

    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!NightmareMode.noHit) {
            world.playSoundAtEntity(player, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

            if (!world.isRemote) {
                EntityTNTPrimed missile = new EntityTNTPrimed(world);
                EntitySnowball dummy = new EntitySnowball(world, player);
                missile.motionX = dummy.motionX;
                missile.motionY = dummy.motionY;
                missile.motionZ = dummy.motionZ;
                missile.fuse = 14;
                missile.copyLocationAndAnglesFrom(dummy);
                dummy.setDead();
                world.spawnEntityInWorld(missile);
            }
        }
        return stack;
    }
}
