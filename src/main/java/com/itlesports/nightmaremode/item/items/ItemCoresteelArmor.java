package com.itlesports.nightmaremode.item.items;

import com.itlesports.nightmaremode.util.interfaces.IArmorStatus;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EnumChatFormatting;
import net.minecraft.src.I18n;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;

import java.util.List;

public class ItemCoresteelArmor extends ItemNetherAlloyArmor implements IArmorStatus {
    public static final String HEAT_TAG = "ifhyHeat";
    private final int heatCapacity;

    public ItemCoresteelArmor(int id, int armorType, int protection, int weight, int maxUses,
                              int enchantability, double knockbackResistance, int repairItemID,
                              String wornTexturePrefix, String setBonusKey, int heatCapacity) {
        super(id, armorType, protection, weight, maxUses, enchantability, knockbackResistance,
                repairItemID, wornTexturePrefix, setBonusKey);
        this.heatCapacity = Math.max(1, heatCapacity);
    }

    public int getHeatCapacity() {
        return this.heatCapacity;
    }

    public int getStoredHeat(ItemStack stack) {
        return stack != null && stack.hasTagCompound()
                ? Math.max(0, Math.min(this.heatCapacity, stack.getTagCompound().getInteger(HEAT_TAG))) : 0;
    }

    public void setStoredHeat(ItemStack stack, int heat) {
        if (stack == null) return;
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
            stack.setTagCompound(tag);
        }
        tag.setInteger(HEAT_TAG, Math.max(0, Math.min(this.heatCapacity, heat)));
    }

    public int addHeat(ItemStack stack, int heat) {
        int accepted = Math.min(Math.max(0, heat), this.heatCapacity - this.getStoredHeat(stack));
        this.setStoredHeat(stack, this.getStoredHeat(stack) + accepted);
        return accepted;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(EnumChatFormatting.GOLD + I18n.getStringParams(
                "item.ifhyCoresteelHeat", this.getStoredHeat(stack), this.heatCapacity));
    }

    @Override
    public float getStatusFraction(ItemStack stack) {
        return (float)this.getStoredHeat(stack) / (float)this.heatCapacity;
    }

    @Override
    public int getStatusColor(ItemStack stack) {
        float fraction = this.getStatusFraction(stack);
        int red = Math.min(255, Math.round(255.0F * fraction + 40.0F));
        int green = Math.max(32, Math.round(190.0F * (1.0F - fraction)));
        return red << 16 | green << 8 | 0x20;
    }

    @Override
    public int getStatusBackgroundColor() {
        return 0x3B2412;
    }
}
