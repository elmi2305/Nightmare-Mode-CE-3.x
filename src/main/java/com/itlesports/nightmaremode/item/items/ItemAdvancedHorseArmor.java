package com.itlesports.nightmaremode.item.items;

import com.itlesports.nightmaremode.item.NMItem;
import com.itlesports.nightmaremode.mixin.entity.EntityPlayerMPAccessor;
import com.itlesports.nightmaremode.nmgui.ContainerHorseArmor;
import com.itlesports.nightmaremode.nmgui.InventoryHorseArmor;
import net.minecraft.src.*;

import java.util.List;
import java.util.Objects;

public class ItemAdvancedHorseArmor extends NMItem {
    private final ArmorTier tier;

    public enum ArmorTier {
        LEATHER(64, 1.0f), // maybe one day
        IRON(128, 1.5f),
        GOLD(128, 1.5f),
        DIAMOND(256, 2.0f);

        public final int maxWheat;
        public final float exhaustion;
        ArmorTier(int maxWheat, float exhaustionMod) {
            this.maxWheat = maxWheat;
            this.exhaustion = exhaustionMod;
        }
    }

    public ArmorTier getArmorTier(){
        return this.tier;
    }


    public ItemAdvancedHorseArmor(int id, ArmorTier tier) {
        super(id);
        this.tier = tier;
        this.setMaxStackSize(1);
        this.setMaxDamage(tier.maxWheat);
        this.setCreativeTab(CreativeTabs.tabMisc);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (world.isRemote) return stack;

        if (player instanceof EntityPlayerMP && stack.getItemDamage() != 0) {
            displayGUIAdvancedHorseArmor((EntityPlayerMP) player, stack);
        }

        return stack;
    }


    public int getFuelSlotCount() {
        if (Objects.requireNonNull(tier) == ArmorTier.DIAMOND) {
            return 9;
        }
        return 4;
    }

    @Override
    public void onCreated(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) {
        this.setWheatCount(par1ItemStack, 0);
    }

    public void displayGUIAdvancedHorseArmor(EntityPlayerMP player, ItemStack armorStack) {
        ((EntityPlayerMPAccessor) player).invokeIncrementWindowID();
        int windowId = ((EntityPlayerMPAccessor) player).getCurrentWindowId();

        // pick unused window type ID
        int windowType = 60;
        String windowTitle = I18n.getString("gui.nm.horseMenu");

        int slotCount = (armorStack.getItem() instanceof ItemAdvancedHorseArmor)
                ? ((ItemAdvancedHorseArmor) armorStack.getItem()).getFuelSlotCount()
                : 9;

        // open window packet
        player.playerNetServerHandler.sendPacketToPlayer(
                new Packet100OpenWindow(windowId, windowType, windowTitle, slotCount, true)
        );

        int ownerSlot = player.inventory.currentItem;
        ItemStack armorStack2 = player.inventory.getStackInSlot(ownerSlot); // this might be the same as armorStack
        InventoryHorseArmor inv = new InventoryHorseArmor(player, armorStack2, ownerSlot);
        player.openContainer = new ContainerHorseArmor(player.inventory, inv);


    }


    public void tickFuel(ItemStack stack, EntityHorse horse) {
        if (stack == null || !(stack.getItem() instanceof ItemAdvancedHorseArmor item)) return;


        int wheatCount = item.getWheatCount(stack);
        if (wheatCount > 0) {
            // technically redundant check, but it's better to be careful
            item.setWheatCount(stack, wheatCount - 1);
        }
    }

    // returns how many wheat units are currently stored (0 .. getMaxWheat())
    public int getWheatCount(ItemStack stack) {
        if (stack == null) return 0;
        int max = getMaxWheat();
        int damage = stack.getItemDamage();
        int wheat = max - damage;
        if (wheat < 0) wheat = 0;
        if (wheat > max) wheat = max;
        return wheat;
    }

    public void setWheatCount(ItemStack stack, int count) {
        if (stack == null) return;
        int max = getMaxWheat();
        // both of these methods manipulate item damage so that item damage = wheat, because they are inverse to each other
        count = Math.max(0, Math.min(count, max));
        int damage = max - count;
        damage = Math.max(0, Math.min(damage, max));
        stack.setItemDamage(damage);
    }

    public int addWheat(ItemStack stack, int amount) {
        if (stack == null || amount <= 0) return 0;
        int current = getWheatCount(stack);
        int max = getMaxWheat();
        int canAdd = Math.min(amount, max - current);
        setWheatCount(stack, current + canAdd);
        return canAdd;
        // add up to amount, return amount that was effectively added
    }


    public int getMaxWheat() {
        return tier == null ? 0 : tier.maxWheat;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
        int wheat = getWheatCount(stack);
        int max = getMaxWheat();
        list.add(I18n.getString("item.fcItemWheat.name") + ": " + wheat + " / " + max);
    }

    @Override
    public boolean isDamageable() {
        return true;
    }
}
