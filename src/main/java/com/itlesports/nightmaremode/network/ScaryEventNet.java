package com.itlesports.nightmaremode.network;

import api.BTWAddon;
import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.client.ScaryEvents;
import com.itlesports.nightmaremode.scary.ScaryEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.Minecraft;

public final class ScaryEventNet {
    private static String channel;

    private ScaryEventNet() {}

    public static void register(BTWAddon addon) {
        channel = addon.getModID() + "|scary";
        addon.registerPacketHandler(channel, (packet, player) -> {
            if (packet.data == null || packet.data.length != 2) return;
            // a client can only request the current toggle; forcing an event requires the command.
            if (player.worldObj.isRemote) {
                receive(packet.data);
            } else if (packet.data[0] == 0 && packet.data[1] == (byte)255 && player instanceof EntityPlayerMP target) {
                sendState(target);
            }
        });
    }

    public static void sendState(EntityPlayerMP player) {
        send(player, 0, false);
    }

    public static void sendControl(EntityPlayerMP player) {
        send(player, 0, !NightmareMode.scaryEvents);
    }

    public static void force(EntityPlayerMP player, ScaryEvent event) {
        send(player, event.ordinal() + 1, false);
    }

    @Environment(EnvType.CLIENT)
    public static void requestState() {
        if (channel != null && Minecraft.getMinecraft().thePlayer != null) {
            Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue(
                    new Packet250CustomPayload(channel, new byte[]{0, (byte)255}));
        }
    }

    private static void send(EntityPlayerMP player, int event, boolean cancel) {
        player.playerNetServerHandler.sendPacketToPlayer(new Packet250CustomPayload(channel,
                new byte[]{(byte)((NightmareMode.scaryEvents ? 1 : 0) | (cancel ? 2 : 0)), (byte)event}));
    }

    @Environment(EnvType.CLIENT)
    private static void receive(byte[] data) {
        int event = data[1] & 255;
        if (data[0] < 0 || data[0] > 2 || event > ScaryEvent.values().length || (data[0] == 2 && event != 0)) return;
        ScaryEvents.receive(data[0] == 1, event == 0 ? null : ScaryEvent.values()[event - 1], data[0] == 2);
    }
}
