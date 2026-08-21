package com.itlesports.nightmaremode.nmgui;

import net.minecraft.src.Minecraft;
import net.minecraft.src.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A title-screen palette and its matching artwork. Add future themes here; a
 * theme only enters the startup rotation once every one of its supplied assets
 * is present, so partially-added artwork can never break the main menu.
 */
public final class JourneyTitleTheme {
    public static final JourneyTitleTheme NETHER = new JourneyTitleTheme("nether", panorama("nether"),
            "betterThanWolvesNether.png", "journeyModeNether.png", "world_basic_nether.png", 6,
            0x00211415, 0x309B342E, 0xB02C1718, 0xFF9E4238,
            0xC0422221, 0xD060302B, 0xFFF0B7AB, 0xFFFFD2C4, 0xFFC89086);

    public static final JourneyTitleTheme JOURNEY = new JourneyTitleTheme("journey", panorama("journey"),
            "betterThanWolvesJourney.png", "journeyModeJourney.png", "world_basic_journey.png", 6,
            0x001D3514, 0x306CB53D, 0xB0284C25, 0xFF75B849,
            0xC03C6F31, 0xD0558C42, 0xFFD0F0A4, 0xFFE5FFBE, 0xFFAACD7E);

    public static final JourneyTitleTheme BRASS = new JourneyTitleTheme("brass", panorama("brass"),
            "betterThanWolvesBrass.png", "journeyModeBrass.png", "world_basic_brass.png", 7,
            0x003B2D1B, 0x30AA8040, 0xB05A4326, 0xFFB58C4D,
            0xC07A5A32, 0xD09A7340, 0xFFF0D6A3, 0xFFFFE6B5, 0xFFD1B57D);

    public static final JourneyTitleTheme SAND = new JourneyTitleTheme("sand", panorama("sand"),
            "betterThanWolvesSand.png", "journeyModeSand.png", "world_basic_sand.png", 6,
            0x003C2D19, 0x30A67638, 0xB05B4126, 0xFF9A7137,
            0xC075532D, 0xD08F6939, 0xFFE6C98C, 0xFFFFE3A8, 0xFFC1A773);

    public static final JourneyTitleTheme SNOW = new JourneyTitleTheme("snow", panorama("snow"),
            "betterThanWolvesSnow.png", "journeyModeSnow.png", "world_basic_snow.png", 6,
            0x001F3039, 0x307EBDD3, 0xB0405B68, 0xFFA6D8E8,
            0xC0557583, 0xD0789CAA, 0xFFE0F5F8, 0xFFFFFFFF, 0xFFB9DBE3);

    public static final JourneyTitleTheme CAVE = new JourneyTitleTheme("cave", panorama("cave"),
            "betterThanWolvesCave.png", "journeyModeCave.png", "world_basic_cave.png", 6,
            0x001B1E20, 0x305D6C72, 0xB02D3437, 0xFF6D878D,
            0xC0404A4F, 0xD05A666B, 0xFFD1DCDD, 0xFFF0F8F8, 0xFFAABABC);

    private static final JourneyTitleTheme[] THEMES = new JourneyTitleTheme[]{NETHER, JOURNEY, BRASS, SAND, SNOW, CAVE};

    private static JourneyTitleTheme active;

    public final String id;
    public final int weight;
    public final ResourceLocation[] panorama;
    public final ResourceLocation betterThanWolves;
    public final ResourceLocation journeyMode;
    public final ResourceLocation worldIcon;
    public final int panelRgb;
    public final int divider;
    public final int cardFill;
    public final int edge;
    public final int buttonFill;
    public final int buttonHoverFill;
    public final int text;
    public final int textHighlight;
    public final int textMuted;

    private JourneyTitleTheme(String id, ResourceLocation[] panorama, String betterThanWolves, String journeyMode, String worldIcon, int weight,
                              int panelRgb, int divider, int cardFill, int edge, int buttonFill, int buttonHoverFill,
                              int text, int textHighlight, int textMuted) {
        this.id = id;
        this.weight = weight;
        this.panorama = panorama;
        this.betterThanWolves = menuTexture(betterThanWolves);
        this.journeyMode = menuTexture(journeyMode);
        this.worldIcon = new ResourceLocation("nightmare:textures/gui/" + worldIcon);
        this.panelRgb = panelRgb;
        this.divider = divider;
        this.cardFill = cardFill;
        this.edge = edge;
        this.buttonFill = buttonFill;
        this.buttonHoverFill = buttonHoverFill;
        this.text = text;
        this.textHighlight = textHighlight;
        this.textMuted = textMuted;
    }

    /** Chosen once per client process, deliberately not whenever the menu is reopened. */
    public static JourneyTitleTheme getActive(Minecraft minecraft) {
        if (active == null) {
            List<JourneyTitleTheme> available = new ArrayList<JourneyTitleTheme>();
            for (JourneyTitleTheme theme : THEMES) if (theme.isComplete(minecraft)) available.add(theme);
            if (available.isEmpty()) throw new IllegalStateException("No complete Journey Mode title-screen themes are available");
            int totalWeight = 0;
            for (JourneyTitleTheme theme : available) totalWeight += theme.weight;
            int roll = new Random().nextInt(totalWeight);
            for (JourneyTitleTheme theme : available) {
                roll -= theme.weight;
                if (roll < 0) { active = theme; break; }
            }
        }
        return active;
    }

    private boolean isComplete(Minecraft minecraft) {
        try {
            minecraft.getResourceManager().getResource(this.betterThanWolves);
            minecraft.getResourceManager().getResource(this.journeyMode);
            minecraft.getResourceManager().getResource(this.worldIcon);
            for (ResourceLocation face : this.panorama) minecraft.getResourceManager().getResource(face);
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static ResourceLocation menuTexture(String name) { return new ResourceLocation("nightmare:textures/menu/" + name); }
    private static ResourceLocation[] panorama(String theme) {
        ResourceLocation[] faces = new ResourceLocation[6];
        for (int i = 0; i < faces.length; i++) faces[i] = new ResourceLocation("nightmare:textures/menu/panoramas/" + theme + "/panorama_" + i + ".png");
        return faces;
    }
}
