package com.itlesports.nightmaremode.util;

import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.*;

import java.util.Map;
import java.util.WeakHashMap;

public final class EnderArmorNet {
    public static final String CHANNEL = "nm|enderarmor";
    private static final Map<EntityPlayerMP, Integer> LAST_USE = new WeakHashMap<>();

    private EnderArmorNet() {}

    public static void sendUse() {
        Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue(new Packet250CustomPayload(CHANNEL, new byte[0]));
    }

    public static void handle(EntityPlayerMP player) {
        if (player == null || player.getHeldItem() != null || !hasFullSet(player)) return;
        int previous = LAST_USE.getOrDefault(player, -1000);
        if (player.ticksExisted - previous < 10) return;
        LAST_USE.put(player, player.ticksExisted);
        player.worldObj.playSoundAtEntity(player, "random.bow", 0.5F, 0.4F / (player.worldObj.rand.nextFloat() * 0.4F + 0.8F));
        player.worldObj.spawnEntityInWorld(new EntityEnderPearl(player.worldObj, player));
    }

    public static boolean hasFullSet(EntityPlayer player) {
        ItemStack[] armor = player.inventory.armorInventory;
        return armor[0] != null && armor[0].getItem() == NMItems.enderBoots
                && armor[1] != null && armor[1].getItem() == NMItems.enderLeggings
                && armor[2] != null && armor[2].getItem() == NMItems.enderChestplate
                && armor[3] != null && armor[3].getItem() == NMItems.enderHelmet;
    }
}
