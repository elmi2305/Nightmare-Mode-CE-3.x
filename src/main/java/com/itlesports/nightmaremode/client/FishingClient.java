package com.itlesports.nightmaremode.client;

import com.itlesports.nightmaremode.fishing.FishingGame;
import com.itlesports.nightmaremode.fishing.FishingPool;
import com.itlesports.nightmaremode.network.FishingNet;
import com.itlesports.nightmaremode.nmgui.GuiFishing;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;
import java.io.*;
import java.util.concurrent.ConcurrentLinkedQueue;

@Environment(EnvType.CLIENT)
public final class FishingClient {
    private static final ConcurrentLinkedQueue<byte[]> pending = new ConcurrentLinkedQueue<>();
    private static int dismissedHook = Integer.MIN_VALUE;
    private FishingClient() {}

    public static void receive(byte[] bytes) {
        if (pending.size() < 32) pending.add(bytes);
    }

    public static void tick() {
        Minecraft mc = Minecraft.getMinecraft();
        byte[] bytes;
        while ((bytes = pending.poll()) != null) {
            if (mc.theWorld == null || mc.thePlayer == null || mc.thePlayer.isDead || mc.thePlayer.getHealth() <= 0) continue;
            try (DataInputStream in = new DataInputStream(new ByteArrayInputStream(bytes))) {
                int type = in.readUnsignedByte(), hook = in.readInt();
                if (hook == dismissedHook) continue;
                GuiFishing screen = mc.currentScreen instanceof GuiFishing gui && gui.hook == hook ? gui : null;
                if (screen != null) screen.resumeIfInterrupted();
                if (type == 1 || type == 2) {
                    int item = in.readShort();
                    if (screen != null) { if (type == 1) screen.finish(item); else screen.caught(item); }
                    continue;
                }
                if (type != 0) continue;
                if (screen == null && !(mc.theWorld.getEntityByID(hook) instanceof EntityFishHook)) continue;
                int age = in.readInt();
                float stock = in.readFloat();
                float depth = in.readFloat(), maxDepth = in.readFloat(), lureX = in.readFloat(), lureY = in.readFloat();
                int fighting = in.readInt(), fightAge = in.readInt(), remaining = in.readUnsignedByte();
                boolean lava = in.readBoolean(), winch = in.readBoolean();
                boolean twinHook = in.readBoolean();
                float lureRadius = in.readFloat();
                int count = in.readUnsignedByte();
                if (count > FishingGame.MAX_TARGETS) continue;
                java.util.List<FishingGame.Target> targets = new java.util.ArrayList<>();
                for (int i = 0; i < count; i++) {
                    int id = in.readInt(), item = in.readShort();
                    FishingGame.Spec spec = FishingPool.spec(item);
                    if (spec == null) throw new IOException("Unknown fishing creature");
                    FishingGame.Target t = new FishingGame.Target(id, spec, in.readFloat(), in.readFloat(), in.readFloat());
                    t.progress = in.readFloat();
                    t.fright = in.readFloat();
                    targets.add(t);
                }
                if (screen == null) {
                    if (mc.currentScreen instanceof GuiFishing previous) previous.allowClose();
                    if (mc.currentScreen != null) mc.thePlayer.closeScreen();
                    screen = new GuiFishing(hook, lava);
                    mc.displayGuiScreen(screen);
                }
                screen.snapshot(age, stock, depth, maxDepth, lureX, lureY, fighting, fightAge, remaining, winch, twinHook, lureRadius, targets);
            } catch (IOException ignored) {}
        }
    }

    public static void dismiss(int hook) { dismissedHook = hook; }
    public static void clear() { pending.clear(); dismissedHook = Integer.MIN_VALUE; }
}
