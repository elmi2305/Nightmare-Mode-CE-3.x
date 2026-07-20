package com.itlesports.nightmaremode.util;

import com.itlesports.nightmaremode.util.interfaces.CarcassAnimal;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityAnimal;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.Minecraft;
import net.minecraft.src.Packet250CustomPayload;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class CarcassHarvestNet {
    public static final String CHANNEL = "nm|carcass";
    private static final byte ACTION_CONTINUE = 0;
    private static final byte ACTION_CANCEL = 1;

    private CarcassHarvestNet() {
    }

    public static void sendContinue(int entityId) {
        send(ACTION_CONTINUE, entityId);
    }

    public static void sendCancel(int entityId) {
        send(ACTION_CANCEL, entityId);
    }

    private static void send(byte action, int entityId) {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        try (DataOutputStream data = new DataOutputStream(byteStream)) {
            data.writeByte(action);
            data.writeInt(entityId);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue(new Packet250CustomPayload(CHANNEL, byteStream.toByteArray()));
    }

    public static void handle(byte[] payload, EntityPlayerMP player) {
        try (DataInputStream data = new DataInputStream(new ByteArrayInputStream(payload))) {
            byte action = data.readByte();
            int entityId = data.readInt();
            Entity entity = player.worldObj.getEntityByID(entityId);
            if (!(entity instanceof EntityAnimal) || !(entity instanceof CarcassAnimal carcass)) {
                return;
            }
            if (action == ACTION_CONTINUE) {
                carcass.nm$continueHarvest(player);
            } else if (action == ACTION_CANCEL) {
                carcass.nm$cancelHarvest(player);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
