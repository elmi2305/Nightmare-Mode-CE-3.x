package com.itlesports.nightmaremode.network;

import com.itlesports.nightmaremode.NightmareModeAddon;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.IntegratedServer;
import net.minecraft.src.NetServerHandler;
import net.minecraft.src.Packet250CustomPayload;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles Nightmaremode MOD presence and version handshake on the server.
 * Ensures matching Nightmare MOD installation and version before allowing gameplay.
 */
public class HandshakeServer {
    // Tracks server-side players awaiting version ACK
    private static final Map<NetServerHandler, Integer> awaitingAckTicks = new HashMap<>();
    private static final int MAX_TICKS_FOR_ACK_WAIT = 10;
    public static final String VERSION_CHECK_CHANNEL = "nm|VC";
    public static final String VERSION_ACK_CHANNEL = "nm|VC_Ack";

    /** Returns current Nightmaremode mod version. */
    public static String getModVersion() {
        return NightmareModeAddon.MOD_VERSION;
    }

    /** Called when player joins the server; initiates handshake. */
    public static void onPlayerJoin(NetServerHandler handler, EntityPlayerMP player) {
        if (handler.mcServer != null && handler.mcServer.isSinglePlayer()) {
            if (handler.mcServer instanceof IntegratedServer integratedServer) {
                if (!integratedServer.getPublic()) {
                    return;
                }
            } else {
                return;
            }
        }
        sendVersionCheckPacket(handler);
        awaitingAckTicks.put(handler, 0);
    }

    /** Called every server tick; kicks players failing handshake. */
    public static void onServerTick() {
        awaitingAckTicks.entrySet().removeIf(entry -> {
            NetServerHandler handler = entry.getKey();
            int ticks = entry.getValue() + 1;
            if (ticks > MAX_TICKS_FOR_ACK_WAIT) {
                handler.kickPlayerFromServer("You need the Nightmaremode mod mod installed (version " + getModVersion() + ") to join this server.");
                return true;
            }
            entry.setValue(ticks);
            return false;
        });
    }

    /** Handles ACK packet on server; checks version match. */
    public static void handleVersionAckPacket(NetServerHandler handler, Packet250CustomPayload packet) {
        try {
            DataInputStream dataStream = new DataInputStream(new ByteArrayInputStream(packet.data));
            String clientVersion = dataStream.readUTF();
            if (!getModVersion().equals(clientVersion)) {
                handler.kickPlayerFromServer("Nightmaremode mod mod version mismatch!\nServer: " + getModVersion() + "\nClient: " + clientVersion);
                awaitingAckTicks.remove(handler);
                return;
            }
            // Version match
            onAckReceived(handler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Removes player from handshake tracking on ACK success. */
    public static void onAckReceived(NetServerHandler handler) {
        awaitingAckTicks.remove(handler);
    }

    /** Sends version check packet to client. */
    private static void sendVersionCheckPacket(NetServerHandler handler) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(out);
            dos.writeUTF(getModVersion());
            dos.close();
            byte[] data = out.toByteArray();

            Packet250CustomPayload packet = new Packet250CustomPayload(VERSION_CHECK_CHANNEL, data);
            handler.sendPacketToPlayer(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}