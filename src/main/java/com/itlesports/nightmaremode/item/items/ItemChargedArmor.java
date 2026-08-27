package com.itlesports.nightmaremode.item.items;

import com.itlesports.nightmaremode.util.interfaces.IArmorStatus;
import net.minecraft.src.*;

import java.util.List;

/** Armor whose redstone charge is stored on the individual stack, not on the item type. */
public class ItemChargedArmor extends ItemAlloyArmor implements IArmorStatus {
    public static final String CHARGE_TAG = "ifhySignalCharge";
    private final int chargeCapacity;

    public ItemChargedArmor(int id, int armorType, int protection, int weight, int maxUses,
                            int enchantability, double knockbackResistance, int repairItemID,
                            String wornTexturePrefix, String setBonusKey, int chargeCapacity) {
        super(id, armorType, protection, weight, maxUses, enchantability, knockbackResistance,
                repairItemID, wornTexturePrefix, setBonusKey);
        this.chargeCapacity = Math.max(1, chargeCapacity);
    }

    public int getCharge(ItemStack stack) {
        return stack != null && stack.hasTagCompound()
                ? MathHelper.clamp_int(stack.getTagCompound().getInteger(CHARGE_TAG), 0, this.chargeCapacity) : 0;
    }

    public void setCharge(ItemStack stack, int charge) {
        if (stack == null) return;
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
            stack.setTagCompound(tag);
        }
        tag.setInteger(CHARGE_TAG, MathHelper.clamp_int(charge, 0, this.chargeCapacity));
    }

    public void addCharge(ItemStack stack, int amount) {
        this.setCharge(stack, this.getCharge(stack) + Math.max(0, amount));
    }

    public int removeCharge(ItemStack stack, int amount) {
        int removed = Math.min(this.getCharge(stack), Math.max(0, amount));
        this.setCharge(stack, this.getCharge(stack) - removed);
        return removed;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(EnumChatFormatting.RED + I18n.getStringParams("item.ifhySignalCharge",
                this.getCharge(stack), this.chargeCapacity));
    }

    @Override public float getStatusFraction(ItemStack stack) { return (float)this.getCharge(stack) / this.chargeCapacity; }
    @Override public int getStatusColor(ItemStack stack) { return 0xE13A2D; }
    @Override public int getStatusBackgroundColor() { return 0x3E1210; }
}
