package com.itlesports.nightmaremode.util;

import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.skill.SkillHandler;
import com.itlesports.nightmaremode.skill.SkillTreeData;
import com.itlesports.nightmaremode.world.BalanceProfile;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.ItemStack;

public final class EasyCombatStarter {
    private EasyCombatStarter() {}

    public static void giveBook(EntityPlayerMP player) {
        if (!BalanceProfile.isCombatEasy() || player.worldObj.isRemote || player.capabilities.isCreativeMode) return;
        SkillTreeData data = SkillHandler.getPlayerData(player);
        if (data.easyStarterBookGranted) return;
        for (ItemStack stack : player.inventory.mainInventory) {
            if (stack != null && stack.itemID == NMItems.skillBook.itemID) {
                data.easyStarterBookGranted = true;
                return;
            }
        }
        if (player.inventory.addItemStackToInventory(new ItemStack(NMItems.skillBook))) {
            data.easyStarterBookGranted = true;
            player.inventory.onInventoryChanged();
            player.inventoryContainer.detectAndSendChanges();
        }
    }
}
