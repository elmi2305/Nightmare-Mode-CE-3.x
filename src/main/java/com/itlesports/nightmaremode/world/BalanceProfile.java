package com.itlesports.nightmaremode.world;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.src.NBTTagCompound;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class BalanceProfile {
    public static final String HARD = "hard";
    public static final String EASY = "easy";
    public static final String LOW = "low";
    public static final String HIGH = "high";
    public static final String TAG = "NightmareBalanceProfile";
    public static final String COMBAT_TAG = "JourneyCombatDifficulty";
    public static final String PROGRESSION_TAG = "JourneyProgression";
    public static final String CHANNEL = "NM|Balance2";
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
                throw new IllegalStateException("Cannot read Journey settings", exception);
            }
        }
        String legacyProgression = EASY.equals(properties.getProperty("profile")) ? LOW : HIGH;
        return profile(EASY.equals(properties.getProperty("combat")) ? EASY : HARD,
                LOW.equals(properties.getProperty("progression", legacyProgression)) ? LOW : HIGH);
    }

    public static String profile(String combat, String progression) {
        if ((!HARD.equals(combat) && !EASY.equals(combat)) || (!LOW.equals(progression) && !HIGH.equals(progression)))
            throw new IllegalArgumentException("Invalid Journey settings");
        return combat + ":" + progression;
    }

    public static String combat(String profile) {
        return profile != null && profile.startsWith(EASY + ":") ? EASY : HARD;
    }

    public static String progression(String profile) {
        return profile != null && profile.endsWith(":" + LOW) ? LOW : HIGH;
    }

    public static String active() { return ACTIVE; }
    public static String pending() { return pending; }
    public static boolean isCombatEasy() { return EASY.equals(combat(ACTIVE)); }
    public static boolean isLowProgression() { return LOW.equals(progression(ACTIVE)); }

    // legacy progression hooks keep their existing api; combat must use isCombatEasy.
    public static boolean isEasy() { return isLowProgression(); }

    public static String fromSave(NBTTagCompound tag) { return fromSave(tag, false); }

    public static String fromSave(NBTTagCompound tag, boolean legacyEasyDifficulty) {
        String legacy = tag.hasKey(TAG) ? tag.getString(TAG) : legacyEasyDifficulty ? EASY : HARD;
        String combat = tag.hasKey(COMBAT_TAG) ? tag.getString(COMBAT_TAG) : HARD;
        String progression = tag.hasKey(PROGRESSION_TAG) ? tag.getString(PROGRESSION_TAG) : EASY.equals(legacy) ? LOW : HIGH;
        if ((!HARD.equals(combat) && !EASY.equals(combat)) || (!LOW.equals(progression) && !HIGH.equals(progression))) return "unknown";
        return profile(combat, progression);
    }

    public static void writeSave(NBTTagCompound tag, String profile) {
        if (!isValid(profile)) throw new IllegalArgumentException("Invalid saved Journey settings");
        tag.setString(COMBAT_TAG, combat(profile));
        tag.setString(PROGRESSION_TAG, progression(profile));
        tag.setString(TAG, LOW.equals(progression(profile)) ? EASY : HARD);
    }

    public static boolean isValid(String profile) {
        return (HARD + ":" + HIGH).equals(profile) || (HARD + ":" + LOW).equals(profile)
                || (EASY + ":" + HIGH).equals(profile) || (EASY + ":" + LOW).equals(profile);
    }
    public static boolean matches(String profile) { return ACTIVE.equals(profile); }
    public static byte[] wireProfile() { return new byte[]{2, (byte)(isCombatEasy() ? 1 : 0), (byte)(isLowProgression() ? 1 : 0)}; }
    public static String decodeWireProfile(byte[] data) {
        if (data == null || data.length != 3 || data[0] != 2 || data[1] < 0 || data[1] > 1 || data[2] < 0 || data[2] > 1) return "unknown";
        return profile(data[1] == 1 ? EASY : HARD, data[2] == 1 ? LOW : HIGH);
    }
    public static boolean acceptsWireProfile(byte[] data) { return matches(decodeWireProfile(data)); }
    public static String name(String value) {
        return switch (value == null ? "" : value) {
            case EASY -> "Easy";
            case HARD -> "Hard";
            case LOW -> "Low";
            case HIGH -> "High";
            default -> "Unknown";
        };
    }

    public static String journeyName(String profile) {
        if (EASY.equals(combat(profile))) return LOW.equals(progression(profile)) ? "Adventure" : "Long Road";
        return LOW.equals(progression(profile)) ? "Bold Venture" : "Journey";
    }

    public static String mismatch(String worldProfile) {
        return "This world/server requires " + journeyName(worldProfile)
                + ". Select both in the main menu and restart the game. Current: " + journeyName(ACTIVE) + ".";
    }

    public static String buttonText() { return "Difficulty"; }
    public static boolean restartRequired() { return !pending.equals(ACTIVE); }

    public static void selectPending(String combat, String progression) {
        String selected = profile(combat, progression);
        Properties properties = new Properties();
        properties.setProperty("combat", combat);
        properties.setProperty("progression", progression);
        properties.setProperty("profile", LOW.equals(progression) ? EASY : HARD);
        try {
            Files.createDirectories(CONFIG.getParent());
            try (Writer writer = Files.newBufferedWriter(CONFIG)) {
                properties.store(writer, "combat and progression changes require a game restart");
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Cannot save Journey settings", exception);
        }
        pending = selected;
    }
}
