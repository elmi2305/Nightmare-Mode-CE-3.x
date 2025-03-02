package com.itlesports.nightmaremode.item.items;

import net.minecraft.src.*;

public class ItemIronFishingPole extends ItemFishingRod {
    public ItemIronFishingPole(int par1) {
        super(par1);
        this.setTextureName("nmIronFishingPole");
        this.setUnlocalizedName("nmIronFishingPole");
        this.setMaxDamage(250);
    }
    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        player.swingItem();
        if (player.fishEntity != null) {
            int var4 = player.fishEntity.catchFish();
            stack.damageItem(var4, player);
            player.swingItem();
        } else {
            world.playSoundAtEntity(player, "random.bow", 0.5f, 0.4f / (itemRand.nextFloat() * 0.4f + 0.8f));
            if (!world.isRemote) {
                world.spawnEntityInWorld(new EntityFishHook(world, player));
            }
            player.swingItem();
        }
        return stack;
    }
}
