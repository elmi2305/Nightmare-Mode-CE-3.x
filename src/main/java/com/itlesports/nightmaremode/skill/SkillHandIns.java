package com.itlesports.nightmaremode.skill;

import com.itlesports.nightmaremode.util.NMFoodSpoilage;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;

import java.util.function.BiPredicate;

public final class SkillHandIns {
    private SkillHandIns() {
    }

    public static BiPredicate<EntityPlayer, ItemStack> freshFood(int itemId) {
        return (player, stack) -> stack.itemID == itemId
                && (!NMFoodSpoilage.isPerishable(stack) || stack.getItemDamage() < stack.getMaxDamage());
    }

    public static BiPredicate<EntityPlayer, ItemStack> usableTool(int itemId) {
        return (player, stack) -> stack.itemID == itemId && stack.getItemDamage() < stack.getMaxDamage();
    }

    public static BiPredicate<EntityPlayer, ItemStack> emptyContainer(int itemId) {
        return (player, stack) -> stack.itemID == itemId && emptyContents(stack.getTagCompound());
    }

    private static boolean emptyContents(NBTTagCompound tag) {
        if (tag == null) return true;
        if (tag.hasKey("fcStorageStack") || tag.hasKey("Contents") || tag.hasKey("StoredItem")) return false;
        if (tag.hasKey("Items") && tag.getTagList("Items").tagCount() > 0) return false;
        return !tag.hasKey("BlockEntityTag") || emptyContents(tag.getCompoundTag("BlockEntityTag"));
    }

    public static boolean ownHead(EntityPlayer player, ItemStack stack) {
        return stack.itemID == Item.skull.itemID && stack.getItemDamage() == 3
                && stack.hasTagCompound()
                && player.getCommandSenderName().equalsIgnoreCase(stack.getTagCompound().getString("SkullOwner"));
    }
}
