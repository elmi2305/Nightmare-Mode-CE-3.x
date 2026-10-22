package com.itlesports.nightmaremode.util;

import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.item.items.ItemDivingGear;
import net.minecraft.src.*;

public final class DivingTankAttachment {
    public static final String TAG = "ifhyDivingTankAttachment";

    private DivingTankAttachment() {}

    public static boolean isChestplate(ItemStack stack) {
        return stack != null && stack.getItem() instanceof ItemArmor armor && armor.armorType == 1
                && stack.getItem() != NMItems.sunReservoir && stack.getItem() != NMItems.sunChestplate
                && !(stack.getItem() instanceof ItemDivingGear gear && gear.storesAir());
    }

    public static boolean hasAttachment(ItemStack stack) {
        return isChestplate(stack) && stack.hasTagCompound() && stack.getTagCompound().hasKey(TAG);
    }

    public static ItemStack getTank(ItemStack stack) {
        if (stack == null) return null;
        if (hasAttachment(stack)) {
            ItemStack tank = ItemStack.loadItemStackFromNBT(stack.getTagCompound().getCompoundTag(TAG));
            return tank != null && tank.getItem() == NMItems.divingTank ? tank : null;
        }
        return stack.getItem() instanceof ItemDivingGear gear && gear.storesAir() ? stack : null;
    }

    public static ItemDivingGear getGear(ItemStack stack) {
        ItemStack tank = getTank(stack);
        return tank != null && ArmorSetHelper.isIntact(tank) ? (ItemDivingGear)tank.getItem() : null;
    }

    public static void setTank(ItemStack chest, ItemStack tank) {
        if (!isChestplate(chest)) return;
        if (tank == null) {
            if (chest.hasTagCompound()) chest.getTagCompound().removeTag(TAG);
            return;
        }
        if (!chest.hasTagCompound()) chest.setTagCompound(new NBTTagCompound());
        chest.getTagCompound().setCompoundTag(TAG, tank.writeToNBT(new NBTTagCompound()));
    }

    public static int getAir(ItemStack host) {
        ItemStack tank = getTank(host);
        ItemDivingGear gear = getGear(host);
        return gear == null ? 0 : gear.getStoredAir(tank);
    }

    public static boolean fill(ItemStack host, int amount) {
        ItemStack tank = getTank(host);
        ItemDivingGear gear = getGear(host);
        if (gear == null || gear.getStoredAir(tank) >= gear.getAirCapacity()) return false;
        gear.setStoredAir(tank, gear.getStoredAir(tank) + amount);
        if (hasAttachment(host)) setTank(host, tank);
        return true;
    }

    public static void consumeAir(ItemStack host) {
        ItemStack tank = getTank(host);
        ItemDivingGear gear = getGear(host);
        if (gear == null) return;
        float remaining = 1.0F - (float)tank.getItemDamage() / tank.getMaxDamage();
        gear.consumeAir(tank, remaining < 0.1F ? 4 : remaining < 0.5F ? 2 : 1);
        if (hasAttachment(host)) setTank(host, tank);
    }

    public static void addTooltip(ItemStack host, java.util.List tooltip) {
        if (!hasAttachment(host)) return;
        ItemStack tank = getTank(host);
        if (tank == null) return;
        ItemDivingGear gear = (ItemDivingGear)tank.getItem();
        tooltip.add(EnumChatFormatting.BLUE + I18n.getStringParams("item.ifhyOxygenReduction",
                Math.round(gear.getOxygenDrainReduction() * 100.0F)));
        tooltip.add(EnumChatFormatting.AQUA + I18n.getStringParams("item.ifhyDivingAir",
                gear.getStoredAir(tank) / 20, gear.getAirCapacity() / 20));
        tooltip.add(EnumChatFormatting.GRAY + I18n.getStringParams("item.ifhyDivingTank.durability",
                Math.max(0, tank.getMaxDamage() - tank.getItemDamage()), tank.getMaxDamage()));
    }

    public static com.google.common.collect.Multimap combineTooltipHunger(com.google.common.collect.Multimap attributes) {
        com.google.common.collect.Multimap display = com.google.common.collect.HashMultimap.create(attributes);
        String key = btw.entity.attribute.BTWAttributes.hungerCost.getAttributeUnlocalizedName();
        java.util.Collection<AttributeModifier> hunger = display.get(key);
        double amount = 0.0D;
        java.util.Iterator<AttributeModifier> iterator = hunger.iterator();
        while (iterator.hasNext()) {
            AttributeModifier modifier = iterator.next();
            if (modifier.getOperation() != 0) continue;
            amount += modifier.getAmount();
            iterator.remove();
        }
        if (amount != 0.0D) {
            display.put(key, new api.entity.attribute.CustomDisplayAttributeModifier(
                    java.util.UUID.fromString("53b1eaac-e41b-44d0-93bb-fb04796ac5fb"), "Combined Hunger Cost",
                    amount, api.entity.attribute.AttributeOperation.ADDITIVE, modifier -> {
                        double magnitude = Math.abs(modifier.getAmount());
                        String tier = magnitude < 0.08D ? "tiny" : magnitude < 0.16D ? "small"
                                : magnitude < 0.24D ? "medium" : "large";
                        return "attribute.name." + key + "." + tier;
                    }));
        }
        return display;
    }
}
