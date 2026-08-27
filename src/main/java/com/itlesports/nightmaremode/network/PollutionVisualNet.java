package com.itlesports.nightmaremode.network;

import api.BTWAddon;
import api.network.CustomPacketHandler;
import com.itlesports.nightmaremode.agriculture.ChunkAttributeManager;
import com.itlesports.nightmaremode.agriculture.ChunkPollutionManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.ChunkCoordIntPair;
import net.minecraft.src.EntityClientPlayerMP;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.Minecraft;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.World;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/** Synchronizes coarse pollution visual bands without exposing server chunk attributes to clients. */
public final class PollutionVisualNet {
    private static final byte UPDATE = 0;
    private static final byte REQUEST = 1;
    private static final int POLLUTION_TINT = 0x695A13;
    private static final Map<Long, Byte> clientBands = new HashMap<>();
    public static String CHANNEL;

    private PollutionVisualNet() {}

    public static void register(BTWAddon addon) {
        CHANNEL = addon.getModID() + "|pl";
        addon.registerPacketHandler(CHANNEL, new CustomPacketHandler() {
            @Override
            public void handleCustomPacket(Packet250CustomPayload packet, EntityPlayer player) {
                if (packet == null || packet.data == null) return;
                try (DataInputStream input = new DataInputStream(new ByteArrayInputStream(packet.data))) {
                    byte action = input.readByte();
                    int chunkX = input.readInt();
                    int chunkZ = input.readInt();
                    if (player.worldObj.isRemote && action == UPDATE) {
                        applyClientPollution(player.worldObj, chunkX, chunkZ, input.readFloat());
                    } else if (!player.worldObj.isRemote && action == REQUEST && player instanceof EntityPlayerMP serverPlayer) {
                        sendPollution(serverPlayer, chunkX, chunkZ,
                                ChunkPollutionManager.get(serverPlayer.worldObj, chunkX << 4, chunkZ << 4));
                    }
                } catch (IOException ignored) {
                }
            }
        });
    }

    public static byte getBand(float pollution) {
        if (pollution >= ChunkPollutionManager.BIOLOGICAL_DAMAGE) return 4;
        if (pollution >= ChunkPollutionManager.BLIGHT_STARTS) return 3;
        if (pollution >= ChunkPollutionManager.GRASS_DECAYS) return 2;
        if (pollution >= ChunkPollutionManager.GRASS_STOPS_SPREADING) return 1;
        return pollution >= ChunkPollutionManager.PASSIVE_SPREAD_THRESHOLD ? (byte)1 : 0;
    }

    public static void broadcastPollution(World world, int chunkX, int chunkZ, float pollution) {
        ChunkCoordIntPair coords = new ChunkCoordIntPair(chunkX, chunkZ);
        for (Object entry : world.playerEntities) {
            // loadedChunks is the server's pending-send list; watching clients have already left it.
            if (entry instanceof EntityPlayerMP player && !player.loadedChunks.contains(coords)) {
                sendPollution(player, chunkX, chunkZ, pollution);
            }
        }
    }

    @Environment(EnvType.CLIENT)
    public static void requestBand(int chunkX, int chunkZ) {
        if (CHANNEL == null || Minecraft.getMinecraft().thePlayer == null) return;
        Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue(createPacket(REQUEST, chunkX, chunkZ, 0.0F));
    }

    @Environment(EnvType.CLIENT)
    public static int tintColor(int baseColor, int blockX, int blockZ) {
        byte band = clientBands.getOrDefault(ChunkCoordIntPair.chunkXZ2Int(blockX >> 4, blockZ >> 4), (byte)0);
        if (band == 0) return baseColor;
        int amount = band * 20;
        int red = ((baseColor >> 16 & 255) * (100 - amount) + (POLLUTION_TINT >> 16 & 255) * amount) / 100;
        int green = ((baseColor >> 8 & 255) * (100 - amount) + (POLLUTION_TINT >> 8 & 255) * amount) / 100;
        int blue = ((baseColor & 255) * (100 - amount) + (POLLUTION_TINT & 255) * amount) / 100;
        return red << 16 | green << 8 | blue;
    }

    @Environment(EnvType.CLIENT)
    private static void applyClientPollution(World world, int chunkX, int chunkZ, float pollution) {
        ChunkAttributeManager.get(world, chunkX << 4, chunkZ << 4).setPollution(pollution);
        byte band = getBand(pollution);
        long key = ChunkCoordIntPair.chunkXZ2Int(chunkX, chunkZ);
        Byte previous = clientBands.put(key, band);
        if (previous == null || previous != band) {
            world.markBlockRangeForRenderUpdate(chunkX << 4, 0, chunkZ << 4, (chunkX << 4) + 15, 256, (chunkZ << 4) + 15);
        }
    }

    private static void sendPollution(EntityPlayerMP player, int chunkX, int chunkZ, float pollution) {
        player.playerNetServerHandler.sendPacketToPlayer(createPacket(UPDATE, chunkX, chunkZ, pollution));
    }

    private static Packet250CustomPayload createPacket(byte action, int chunkX, int chunkZ, float pollution) {
        try (ByteArrayOutputStream bytes = new ByteArrayOutputStream(); DataOutputStream output = new DataOutputStream(bytes)) {
            output.writeByte(action);
            output.writeInt(chunkX);
            output.writeInt(chunkZ);
            if (action == UPDATE) output.writeFloat(pollution);
            return new Packet250CustomPayload(CHANNEL, bytes.toByteArray());
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to create pollution visual packet", exception);
        }
    }
}
