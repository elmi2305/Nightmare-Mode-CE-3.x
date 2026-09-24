package com.itlesports.nightmaremode.network;

import api.BTWAddon;
import btw.community.nightmaremode.NightmareMode;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.Packet250CustomPayload;

public final class TesterSettingsNet {
    private static String channel;

    private TesterSettingsNet() {}

    public static void register(BTWAddon addon) {
        channel = addon.getModID() + "|test";
        addon.registerPacketHandler(channel, (packet, player) -> {
            if (!player.worldObj.isRemote || packet.data == null || packet.data.length != 1) return;
            int mask = packet.data[0] & 255;
            NightmareMode.devMode = (mask & 1) != 0;
            NightmareMode.alwaysShowRewards = (mask & 2) != 0;
            NightmareMode.unlockSkillsWithClick = (mask & 4) != 0;
            NightmareMode.disableFatigue = (mask & 8) != 0;
            NightmareMode.fullInventoryCapacity = (mask & 16) != 0;
        });
    }

    public static int currentMask() {
        return (NightmareMode.devMode ? 1 : 0)
                | (NightmareMode.alwaysShowRewards ? 2 : 0)
                | (NightmareMode.unlockSkillsWithClick ? 4 : 0)
                | (NightmareMode.disableFatigue ? 8 : 0)
                | (NightmareMode.fullInventoryCapacity ? 16 : 0);
    }

    public static void sendState(EntityPlayerMP player) {
        player.playerNetServerHandler.sendPacketToPlayer(
                new Packet250CustomPayload(channel, new byte[]{(byte)currentMask()}));
    }
}
