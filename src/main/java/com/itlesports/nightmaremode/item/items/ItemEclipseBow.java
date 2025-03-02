package com.itlesports.nightmaremode.item.items;

import btw.entity.BroadheadArrowEntity;
import btw.item.BTWItems;
import btw.item.items.CompositeBowItem;
import com.itlesports.nightmaremode.entity.EntityMagicArrow;
import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.*;

public class ItemEclipseBow extends CompositeBowItem {
    public ItemEclipseBow(int iItemID) {
        super(iItemID);
        this.setMaxDamage(1000);
        this.setUnlocalizedName("nmEclipseBow");
        this.setTextureName("nmEclipseBow");
    }
    @Override
    public Icon getDrawIcon(int itemInUseDuration) {
        if (itemInUseDuration >= 10) {
            return this.getItemIconForUseDuration(2);
        }
        if (itemInUseDuration > 6) {
            return this.getItemIconForUseDuration(1);
        }
        if (itemInUseDuration > 0) {
            return this.getItemIconForUseDuration(0);
        }
        return this.itemIcon;
    }
    public boolean canItemBeFiredAsArrow(int iItemID) {
        return iItemID == Item.arrow.itemID || iItemID == BTWItems.rottenArrow.itemID || iItemID == NMItems.magicArrow.itemID || iItemID == BTWItems.broadheadArrow.itemID;
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack itemStack, World world, EntityPlayer player, int iTicksInUseRemaining) {
        ItemStack arrowStack = this.getFirstArrowStackInHotbar(player);
        if (arrowStack != null && arrowStack.itemID == NMItems.magicArrow.itemID) {
            boolean infiniteArrows = player.capabilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, itemStack) > 0;

            float fPullStrength = this.getCurrentPullStrength(player, itemStack, iTicksInUseRemaining);
            if (fPullStrength < 0.1f) {
                return;
            }

            float arrowVelocity = fPullStrength * 4f;
            float spreadAngle = world.rand.nextFloat() * 3 + 2; // Angle for left and right arrows
            float spreadAngleVertical = world.rand.nextFloat() * 3 + 1;

            spawnArrowWithSpread(world, player, arrowStack.itemID, arrowVelocity, 0, fPullStrength, 0);
            if (arrowStack.stackSize > 1 || infiniteArrows) {
                spawnArrowWithSpread(world, player, arrowStack.itemID, arrowVelocity, -spreadAngle, fPullStrength, (world.rand.nextBoolean() ? 1 : -1) * spreadAngleVertical) ;
            }
            if (arrowStack.stackSize > 2 || infiniteArrows) {
                spawnArrowWithSpread(world, player, arrowStack.itemID, arrowVelocity, spreadAngle, fPullStrength, (world.rand.nextBoolean() ? 1 : -1) * spreadAngleVertical);
            }
            if (!infiniteArrows) {
                player.inventory.consumeInventoryItem(arrowStack.itemID);
                if (arrowStack.stackSize > 0) {
                    player.inventory.consumeInventoryItem(arrowStack.itemID);
                }
                if (arrowStack.stackSize > 0) {
                    player.inventory.consumeInventoryItem(arrowStack.itemID);
                }
            }

            itemStack.damageItem(1, player);
            this.playerBowSound(world, player, fPullStrength);

            if (itemStack.stackSize == 0) {
                player.inventory.mainInventory[player.inventory.currentItem] = null;
            }
        } else{
            super.onPlayerStoppedUsing(itemStack,world,player,iTicksInUseRemaining);
        }
    }

    /**
     * Spawns an arrow with a given yaw offset to create spread effect.
     */
    private void spawnArrowWithSpread(World world, EntityPlayer player, int arrowItemID, float velocity, float yawOffset, float fPullStrength, float pitchOffset) {
        EntityArrow arrow = this.createArrowEntityForItem(world, player, arrowItemID, velocity / this.getPullStrengthToArrowVelocityMultiplier());

        // Calculate the directional vectors with adjusted yaw
        float yaw = player.rotationYaw + yawOffset;
        float pitch = player.rotationPitch + pitchOffset;
        float motionX = -MathHelper.sin(yaw * (float)Math.PI / 180.0F) * MathHelper.cos(pitch * (float)Math.PI / 180.0F);
        float motionY = -MathHelper.sin(pitch * (float)Math.PI / 180.0F);
        float motionZ = MathHelper.cos(yaw * (float)Math.PI / 180.0F) * MathHelper.cos(pitch * (float)Math.PI / 180.0F);

        arrow.setThrowableHeading(motionX, motionY, motionZ, velocity, 1.0F);
        arrow.setSize(1f, 1f);
        if (fPullStrength == 1.0) {
            arrow.setIsCritical(true);
        }
        if (!world.isRemote) {
            world.spawnEntityInWorld(arrow);
        }
    }

    @Override
    protected EntityArrow createArrowEntityForItem(World world, EntityPlayer player, int iItemID, float fPullStrength) {
        if (iItemID == BTWItems.broadheadArrow.itemID) {
            return new BroadheadArrowEntity(world, player, fPullStrength * 3);
        } else if(iItemID == NMItems.magicArrow.itemID){
            return new EntityMagicArrow(world, player, fPullStrength * 3);
        }
        if (iItemID == BTWItems.rottenArrow.itemID) {
            world.playSoundAtEntity(player, "random.break", 0.8f, 0.8f + world.rand.nextFloat() * 0.4f);
            if (world.isRemote) {
                float motionX = -MathHelper.sin(player.rotationYaw / 180.0f * (float)Math.PI) * MathHelper.cos(player.rotationPitch / 180.0f * (float)Math.PI) * fPullStrength;
                float motionZ = MathHelper.cos(player.rotationYaw / 180.0f * (float)Math.PI) * MathHelper.cos(player.rotationPitch / 180.0f * (float)Math.PI) * fPullStrength;
                float motionY = -MathHelper.sin(player.rotationPitch / 180.0f * (float)Math.PI) * fPullStrength;
                for (int i = 0; i < 32; ++i) {
                    world.spawnParticle("iconcrack_333", player.posX, player.posY + (double)player.getEyeHeight(), player.posZ, (double)motionX + (double)((float)(Math.random() * 2.0 - 1.0) * 0.4f), (double)motionY + (double)((float)(Math.random() * 2.0 - 1.0) * 0.4f), (double)motionZ + (double)((float)(Math.random() * 2.0 - 1.0) * 0.4f));
                }
            }
            return null;
        }
        return super.createArrowEntityForItem(world, player, iItemID, fPullStrength);
    }



    @Override
    protected float getCurrentPullStrength(EntityPlayer player, ItemStack itemStack, int iTicksInUseRemaining) {
        int iTicksInUse = this.getMaxItemUseDuration(itemStack) - iTicksInUseRemaining;
        float fPullStrength = (float)iTicksInUse / 10.0f;
        if ((fPullStrength = (fPullStrength * fPullStrength + fPullStrength * 2.0f) / 3.0f) > 1.0f) {
            fPullStrength = 1.0f;
        }
        return fPullStrength * player.getBowPullStrengthModifier();
    }
}
