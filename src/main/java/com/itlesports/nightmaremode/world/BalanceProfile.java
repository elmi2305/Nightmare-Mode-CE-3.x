package com.itlesports.nightmaremode.world;

import net.fabricmc.loader.api.FabricLoader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class BalanceProfile {
    public static final String HARD = "hard";
    public static final String EASY = "easy";
    public static final String TAG = "NightmareBalanceProfile";
    private static final Path CONFIG = FabricLoader.getInstance().getConfigDir().resolve("nightmare-balance.properties");
    private static final String ACTIVE = load();
    private static String pending = ACTIVE;

    private BalanceProfile() {}

    private static String load() {
        Properties properties = new Properties();
        if (Files.exists(CONFIG)) {
            try (Reader reader = Files.newBufferedReader(CONFIG)) {
                properties.load(reader);
            } catch (IOException exception) {
                throw new IllegalStateException("Cannot read Journey difficulty", exception);
            }
        }
        return EASY.equals(properties.getProperty("profile")) ? EASY : HARD;
    }

    public static String active() { return ACTIVE; }
    public static boolean isEasy() { return EASY.equals(ACTIVE); }
    public static String fromSave(net.minecraft.src.NBTTagCompound tag) {
        return tag.hasKey(TAG) ? tag.getString(TAG) : HARD;
    }
    public static boolean matches(String profile) { return ACTIVE.equals(profile); }
    public static byte[] wireProfile() { return new byte[]{(byte)(isEasy() ? 1 : 0)}; }
    public static String decodeWireProfile(byte[] data) {
        return data != null && data.length == 1 && data[0] == 0 ? HARD
                : data != null && data.length == 1 && data[0] == 1 ? EASY : "unknown";
    }
    public static boolean acceptsWireProfile(byte[] data) { return matches(decodeWireProfile(data)); }
    public static String name(String profile) { return EASY.equals(profile) ? "Easy" : HARD.equals(profile) ? "Hard" : "Unknown"; }
    public static String mismatch(String worldProfile) {
        return "This world requires " + name(worldProfile) + " difficulty. Select it in the main menu and restart the game. Current: " + name(ACTIVE) + ".";
    }

    public static String buttonText() {
        return "Difficulty: " + name(pending);
    }

    public static boolean restartRequired() { return !pending.equals(ACTIVE); }

    public static void togglePending() {
        String selected = EASY.equals(pending) ? HARD : EASY;
        Properties properties = new Properties();
        properties.setProperty("profile", selected);
        try {
            Files.createDirectories(CONFIG.getParent());
            try (Writer writer = Files.newBufferedWriter(CONFIG)) {
                properties.store(writer, "difficulty changes require a game restart");
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Cannot save Journey difficulty", exception);
        }
        pending = selected;
    }
}
