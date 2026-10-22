package com.itlesports.nightmaremode.util;

import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.*;

public final class DivingMaskAttachment {
    public static final String TAG = "ifhyDivingMaskAttachment";

    private DivingMaskAttachment() {}

    public static boolean isHelmet(ItemStack stack) {
        return stack != null && stack.getItem() instanceof ItemArmor armor && armor.armorType == 0
                && stack.getItem() != NMItems.divingMask;
    }

    public static boolean hasAttachment(ItemStack stack) {
        return isHelmet(stack) && stack.hasTagCompound() && stack.getTagCompound().hasKey(TAG);
    }

    public static ItemStack getMask(ItemStack stack) {
        if (stack == null) return null;
        if (hasAttachment(stack)) {
            ItemStack mask = ItemStack.loadItemStackFromNBT(stack.getTagCompound().getCompoundTag(TAG));
            return mask != null && mask.stackSize == 1 && mask.getItem() == NMItems.divingMask ? mask : null;
        }
        return stack.getItem() == NMItems.divingMask ? stack : null;
    }

    public static boolean isActive(ItemStack helmet) {
        return helmet != null && helmet.stackSize > 0 && ArmorSetHelper.isIntact(helmet)
                && ArmorSetHelper.isIntact(getMask(helmet));
    }

    public static void setMask(ItemStack helmet, ItemStack mask) {
        if (!isHelmet(helmet)) return;
        if (mask == null) {
            if (helmet.hasTagCompound()) helmet.getTagCompound().removeTag(TAG);
            return;
        }
        if (mask.stackSize != 1 || mask.getItem() != NMItems.divingMask) return;
        if (!helmet.hasTagCompound()) helmet.setTagCompound(new NBTTagCompound());
        helmet.getTagCompound().setCompoundTag(TAG, mask.writeToNBT(new NBTTagCompound()));
    }

    public static void damage(ItemStack helmet, int amount, EntityLivingBase wearer) {
        if (amount <= 0 || wearer == null || wearer.worldObj.isRemote
                || wearer instanceof EntityPlayer player && player.capabilities.isCreativeMode
                || !hasAttachment(helmet)) return;
        ItemStack mask = getMask(helmet);
        if (mask == null) return;
        if (helmet.stackSize <= 0) {
            setMask(helmet, null);
            wearer.entityDropItem(mask, 0.0F);
        } else {
            mask.setItemDamage((int)Math.min(mask.getMaxDamage(), (long)mask.getItemDamage() + amount));
            setMask(helmet, mask);
        }
    }

    public static void addTooltip(ItemStack helmet, java.util.List tooltip) {
        if (!hasAttachment(helmet)) return;
        ItemStack mask = getMask(helmet);
        if (mask == null) return;
        tooltip.add(EnumChatFormatting.AQUA + I18n.getString("item.ifhyDivingMask.attached"));
        tooltip.add(EnumChatFormatting.BLUE + I18n.getStringParams("item.ifhyOxygenReduction",
                isActive(helmet) ? Math.round(NMItems.divingMask.getOxygenDrainReduction() * 100.0F) : 0));
        tooltip.add(EnumChatFormatting.GRAY + I18n.getStringParams("item.ifhyDivingMask.durability",
                Math.max(0, mask.getMaxDamage() - mask.getItemDamage()), mask.getMaxDamage()));
        NMUtils.addWrappedTooltip(tooltip, I18n.getString("item.ifhyDivingGear.bonus"));
    }
}
