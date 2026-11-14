package com.itlesports.nightmaremode.network;

import api.BTWAddon;
import com.itlesports.nightmaremode.client.FishingClient;
import com.itlesports.nightmaremode.fishing.FishingGame;
import com.itlesports.nightmaremode.fishing.FishingHookControl;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;
import java.io.*;

public final class FishingNet {
    private static String channel;
    private FishingNet() {}

    public static void register(BTWAddon addon) {
        channel = addon.getModID() + "|fish";
        addon.registerPacketHandler(channel, (packet, player) -> {
            if (packet.data == null || packet.data.length > 2048) return;
            if (player.worldObj.isRemote) receive(packet.data);
            else if (packet.data.length == 19 && player.fishEntity != null) {
                try (DataInputStream in = new DataInputStream(new ByteArrayInputStream(packet.data))) {
                    if (in.readByte() != 0 || in.readInt() != player.fishEntity.entityId) return;
                    float x = in.readFloat(), y = in.readFloat();
                    ((FishingHookControl)player.fishEntity).nm$fishingInput(x, y, in.readBoolean(), in.readBoolean(), in.readFloat());
                } catch (IOException ignored) {}
            }
        });
    }

    @Environment(EnvType.CLIENT)
    private static void receive(byte[] bytes) { FishingClient.receive(bytes); }

    public static void sendState(EntityPlayerMP player, int hook, FishingGame game, boolean lava) {
        player.playerNetServerHandler.sendPacketToPlayer(statePacket(hook, game, lava));
    }

    private static Packet250CustomPayload statePacket(int hook, FishingGame game, boolean lava) {
        return packet(out -> {
            out.writeByte(0);
            out.writeInt(hook);
            out.writeInt(game.age);
            out.writeFloat(game.stock);
            out.writeFloat(game.depth);
            out.writeFloat(game.maxDepth);
            out.writeFloat(game.lureX);
            out.writeFloat(game.lureY);
            out.writeInt(game.fighting == null ? 0 : game.fighting.id);
            out.writeInt(game.fightAge);
            out.writeByte(game.catchLimit - game.catches);
            out.writeBoolean(lava);
            out.writeBoolean(game.depthWinch && !game.winchLocked());
            out.writeBoolean(game.twinHook);
            out.writeFloat(game.lureRadius);
            out.writeByte(game.targets.size());
            for (FishingGame.Target t : game.targets) {
                out.writeInt(t.id);
                out.writeShort(t.spec.itemId);
                out.writeFloat(t.x);
                out.writeFloat(t.y);
                out.writeFloat(t.angle);
                out.writeFloat(t.progress);
                out.writeFloat(t.fright);
            }
        });
    }

    public static void sendCatch(EntityPlayerMP player, int hook, int item) {
        send(player, out -> { out.writeByte(2); out.writeInt(hook); out.writeShort(item); });
    }

    public static void sendEnd(EntityPlayerMP player, int hook, int item) {
        send(player, out -> { out.writeByte(1); out.writeInt(hook); out.writeShort(item); });
    }

    private static void send(EntityPlayerMP player, Writer writer) {
        player.playerNetServerHandler.sendPacketToPlayer(packet(writer));
    }

    @Environment(EnvType.CLIENT)
    public static void input(int hook, float x, float y, boolean cancel, boolean relative, float scroll) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer != null && channel != null) mc.thePlayer.sendQueue.addToSendQueue(packet(out -> {
            out.writeByte(0); out.writeInt(hook); out.writeFloat(x); out.writeFloat(y); out.writeBoolean(cancel); out.writeBoolean(relative); out.writeFloat(scroll);
        }));
    }

    private static Packet250CustomPayload packet(Writer writer) {
        try (ByteArrayOutputStream bytes = new ByteArrayOutputStream(); DataOutputStream out = new DataOutputStream(bytes)) {
            writer.write(out);
            return new Packet250CustomPayload(channel, bytes.toByteArray());
        } catch (IOException exception) { throw new IllegalStateException("Unable to encode fishing state", exception); }
    }

    private interface Writer { void write(DataOutputStream out) throws IOException; }
}
