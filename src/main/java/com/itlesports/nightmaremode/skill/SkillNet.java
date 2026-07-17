package com.itlesports.nightmaremode.skill;

import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.Minecraft;
import net.minecraft.src.Packet250CustomPayload;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SkillNet {
    public static final String UNLOCK_CHANNEL = "nm|skill";
    private static final byte ACTION_SYNC_REQUEST = 0;
    private static final byte ACTION_UNLOCK_REQUEST = 1;

    public static void sendSyncRequest() {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        try (DataOutputStream data = new DataOutputStream(byteStream)) {
            data.writeByte(ACTION_SYNC_REQUEST);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue(new Packet250CustomPayload(UNLOCK_CHANNEL, byteStream.toByteArray()));
    }

    public static void sendUnlockRequest(String nodeId) {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        try (DataOutputStream data = new DataOutputStream(byteStream)) {
            data.writeByte(ACTION_UNLOCK_REQUEST);
            data.writeUTF(nodeId);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue(new Packet250CustomPayload(UNLOCK_CHANNEL, byteStream.toByteArray()));
    }

    public static void handleUnlockRequest(byte[] payload, EntityPlayerMP player) {
        try (java.io.DataInputStream data = new java.io.DataInputStream(new java.io.ByteArrayInputStream(payload))) {
            byte action = data.readByte();
            if (action == ACTION_SYNC_REQUEST) {
                SkillHandler.sync(player);
            } else if (action == ACTION_UNLOCK_REQUEST) {
                SkillHandler.tryUnlock(player, data.readUTF());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
