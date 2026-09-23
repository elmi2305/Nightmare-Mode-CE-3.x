package com.itlesports.nightmaremode.network;

import api.BTWAddon;
import api.network.CustomPacketHandler;
import btw.community.nightmaremode.NightmareMode;
import net.minecraft.src.*;

import java.io.*;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public final class SkylightSync {
    private static String channel;
    private int depth;
    private final Map<Chunk, Changes> pending = new LinkedHashMap<>();

    private static final class Changes {
        int sections;
        final byte[][] before = new byte[16][];
    }

    public static void register(BTWAddon addon) {
        channel = addon.getModID() + "|sky";
        addon.registerPacketHandler(channel, new CustomPacketHandler() {
            @Override
            public void handleCustomPacket(Packet250CustomPayload packet, EntityPlayer player) {
                if (NightmareMode.enableLightingFix && player.worldObj.isRemote && !player.worldObj.provider.hasNoSky) {
                    apply(player.worldObj, packet.data);
                }
            }
        });
    }

    public void begin() {
        ++this.depth;
    }

    public void end() {
        --this.depth;
    }

    public void beforeChange(Chunk chunk, int x, int y, int z, int value) {
        if (this.depth == 0 || chunk.getSavedLightValue(EnumSkyBlock.Sky, x, y, z) == value) return;
        Changes changes = this.pending.computeIfAbsent(chunk, ignored -> new Changes());
        int section = y >> 4;
        int bit = 1 << section;
        if ((changes.sections & bit) != 0) return;
        changes.sections |= bit;
        ExtendedBlockStorage storage = chunk.getBlockStorageArray()[section];
        if (storage != null) changes.before[section] = storage.getSkylightArray().data.clone();
    }

    public void flush(WorldServer world) {
        if (!NightmareMode.enableLightingFix) {
            this.pending.clear();
            this.depth = 0;
            return;
        }
        if (this.pending.isEmpty()) return;
        for (Map.Entry<Chunk, Changes> entry : this.pending.entrySet()) {
            Chunk chunk = entry.getKey();
            if (!chunk.isChunkLoaded) continue;
            Changes changes = entry.getValue();
            ExtendedBlockStorage[] storage = chunk.getBlockStorageArray();
            int mask = 0;
            for (int section = 0; section < 16; ++section) {
                if ((changes.sections & 1 << section) != 0 && storage[section] != null
                        && !Arrays.equals(changes.before[section], storage[section].getSkylightArray().data)) {
                    mask |= 1 << section;
                }
            }
            if (mask == 0) continue;
            // at most eight sections per payload, below the custom packet size limit.
            Packet250CustomPayload lower = null;
            Packet250CustomPayload upper = null;
            for (Object entity : world.playerEntities) {
                EntityPlayerMP player = (EntityPlayerMP)entity;
                if (!world.getChunkTracker().isChunkWatchedByPlayerAndSentToClient(
                        player, chunk.xPosition, chunk.zPosition)) continue;
                if ((mask & 255) != 0) {
                    if (lower == null) lower = createPacket(chunk, mask & 255);
                    player.playerNetServerHandler.sendPacketToPlayer(lower);
                }
                if ((mask & 65280) != 0) {
                    if (upper == null) upper = createPacket(chunk, mask & 65280);
                    player.playerNetServerHandler.sendPacketToPlayer(upper);
                }
            }
        }
        this.pending.clear();
    }

    private static Packet250CustomPayload createPacket(Chunk chunk, int mask) {
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            DataOutputStream output = new DataOutputStream(bytes);
            output.writeInt(chunk.xPosition);
            output.writeInt(chunk.zPosition);
            output.writeShort(mask);
            for (int section = 0; section < 16; ++section) {
                if ((mask & 1 << section) != 0) {
                    output.write(chunk.getBlockStorageArray()[section].getSkylightArray().data);
                }
            }
            return new Packet250CustomPayload(channel, bytes.toByteArray());
        } catch (IOException exception) {
            throw new IllegalStateException("could not encode skylight changes", exception);
        }
    }

    private static void apply(World world, byte[] data) {
        if (data == null || data.length < 10) return;
        try {
            DataInputStream input = new DataInputStream(new ByteArrayInputStream(data));
            int x = input.readInt();
            int z = input.readInt();
            int mask = input.readUnsignedShort();
            if (data.length != 10 + Integer.bitCount(mask) * 2048) return;
            // the client provider reports absent chunks as existing; reject its placeholder.
            Chunk chunk = world.getChunkFromChunkCoords(x, z);
            if (chunk instanceof EmptyChunk || !chunk.isChunkLoaded) return;
            ExtendedBlockStorage[] storage = chunk.getBlockStorageArray();
            for (int section = 0; section < 16; ++section) {
                if ((mask & 1 << section) == 0) continue;
                byte[] light = new byte[2048];
                input.readFully(light);
                if (storage[section] != null && Arrays.equals(storage[section].getSkylightArray().data, light)) continue;
                if (storage[section] == null) storage[section] = new ExtendedBlockStorage(section << 4, true);
                System.arraycopy(light, 0, storage[section].getSkylightArray().data, 0, light.length);
                world.markBlockRangeForRenderUpdate(x << 4, section << 4, z << 4,
                        (x << 4) + 15, (section << 4) + 15, (z << 4) + 15);
            }
        } catch (IOException exception) {
            throw new IllegalArgumentException("invalid skylight changes", exception);
        }
    }
}
