package com.itlesports.nightmaremode.item.items;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EnumChatFormatting;
import net.minecraft.src.I18n;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;

import java.util.List;

/** Carbon Iron armor with a simple per-stack wax seal. */
public class ItemCarbonIronArmor extends ItemAlloyArmor {
    public static final String WAXED_TAG = "ifhyWaxed";

    public ItemCarbonIronArmor(int id, int armorType, int protection, int weight, int maxUses,
                               int enchantability, double knockbackResistance, int repairItemID,
                               String wornTexturePrefix, String setBonusKey) {
        super(id, armorType, protection, weight, maxUses, enchantability, knockbackResistance,
                repairItemID, wornTexturePrefix, setBonusKey);
    }

    public static boolean isWaxed(ItemStack stack) {
        return stack != null && stack.getItem() instanceof ItemCarbonIronArmor
                && stack.hasTagCompound() && stack.getTagCompound().getBoolean(WAXED_TAG);
    }

    public ItemStack createWaxedStack() {
        ItemStack stack = new ItemStack(this);
        NBTTagCompound tag = new NBTTagCompound();
        tag.setBoolean(WAXED_TAG, true);
        stack.setTagCompound(tag);
        return stack;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        if (isWaxed(stack)) {
            tooltip.add(EnumChatFormatting.GREEN + I18n.getString("item.ifhyCarbonIronArmor.waxed"));
        }
    }
}
