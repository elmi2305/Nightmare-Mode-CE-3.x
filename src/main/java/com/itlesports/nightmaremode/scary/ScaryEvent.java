package com.itlesports.nightmaremode.scary;

import java.util.Arrays;
import java.util.stream.Collectors;

public enum ScaryEvent {
    FOOTSTEPS("footsteps"), SOUND("sound"), CHAT_LAN("chat-lan"), CHAT_CRASH("chat-crash"),
    CHAT_WHEEL("chat-wheel"), CHAT_JOIN("chat-join"), TAB_EYE("tab-eye"), FIGURE("figure"),
    BLINK("blink"), ACHIEVEMENT("achievement"), SKY_EYES("sky-eyes"), SKY_NIGHT("sky-night"),
    MENU_LABELS("menu-labels"), MENU_BLACKOUT("menu-blackout"), MENU_EXIT_SPAM("menu-exit-spam"),
    CHAT_ACHIEVEMENT("chat-achievement"), CHAT_COMMAND("chat-command"), CHAT_TIME("chat-time"),
    CHAT_SCREENSHOT("chat-screenshot"), CHAT_GAMEMODE("chat-gamemode"),
    DISCORD_NOTIFICATION("discord-notification"), STEAM_NOTIFICATION("steam-notification");

    public final String id;

    ScaryEvent(String id) { this.id = id; }

    public boolean isMenu() { return this == MENU_LABELS || this == MENU_BLACKOUT || this == MENU_EXIT_SPAM; }

    public boolean isSky() { return this == SKY_EYES || this == SKY_NIGHT; }

    public static ScaryEvent find(String id) {
        for (ScaryEvent event : values()) if (event.id.equals(id)) return event;
        return null;
    }

    public static String help() {
        return "/scary <on|off|" + Arrays.stream(values()).map(event -> event.id).collect(Collectors.joining("|")) + ">";
    }
}
