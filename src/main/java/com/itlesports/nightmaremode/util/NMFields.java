package com.itlesports.nightmaremode.util;

import net.minecraft.src.Icon;

// a collection of static fields used around the codebase
public class NMFields {

    // mod ID
    public static final String modID = "nightmare";

    // dimension
    public static final int UNDERWORLD_DIMENSION = 2;

    // color (hexadecimal)
    public static final int CRIMSON_COLOR = 0xD81F1F;

    // creeper variants
    public static final int PACKET_CREEPER_FIRE = 18;
    public static final int PACKET_CREEPER_OBSIDIAN = 19;
    public static final int PACKET_CREEPER_SUPERCRITICAL = 20;
    public static final int PACKET_CREEPER_DUNG = 21;
    public static final int PACKET_CREEPER_LIGHTNING = 22;
    public static final int PACKET_CREEPER_FLOWER = 23;
    public static final int PACKET_CREEPER_VOID = 24;
    public static final int PACKET_CREEPER_GEL = 25;
    public static final int PACKET_CREEPER_GLITCH = 26;

    // other entities
    public static final int PACKET_SPORE = 40; // EntityPollenCloud
    public static final int PACKET_BLACKHOLE = 41; // EntityBlackHole
    public static final int PACKET_BLOOD_ALTAR = 42; // EntityBloodAltar
    public static final int PACKET_RITUAL_ENTITY = 43; // EntityRitualPortal
    public static final int PACKET_RIFT = 44; // EntityRift

    // vehicle types
    public static final int VEHICLE_SPORE = 2300;
    public static final int VEHICLE_MAGIC = 2301;

    // skeleton types
    public static final int SKELETON_WITHER = 1;
    public static final int SKELETON_ICE = 2;
    public static final int SKELETON_FIRE = 3;
    public static final int SKELETON_ENDER = 4;
    public static final int SKELETON_JUNGLE = 5;
    public static final int SKELETON_SUPERCRITICAL = 6;
    public static final int SKELETON_LIGHTNING = 7;

    // blaze types
    public static final int BLAZE_SHADOW = 1;
    public static final int BLAZE_AQUA = 2;

    // custom flower names
    public static final String[] FLOWER_NAMES = {"dandelion", "dandelion2", "dandelion3", "dandelion4", "dandelion5", "hellFlower", "voidShrub", "dandelion8"};

    // portal ritual duration
    public static final int UW_PORTAL_DURATION = 20 * 65;

    // world state ints for readability
    public static final int PREHARDMODE = 0;
    public static final int HARDMODE = 1;
    public static final int POSTWITHER = 2;
    public static final int POSTDRAGON = 3;

    // slime types
    public static final int SLIME_HONEY = 0;
    public static final int SLIME_VOID = 1;

    // food and hunger related
    public static final int MAX_FOOD_FROM_FRUITS = 90;
    public static final int MAX_HEALTH_FROM_FRUITS = 30;

    // sanity
    public static final double MAX_SANITY = 2000.0;
    public static final double CRITICAL_SANITY = 1500.0;

    // sanity drain
    public static final double LIGHT_DRAIN_MULTIPLIER = 0.02;
    public static final double HEIGHT_DRAIN_MULTIPLIER = 0.012;
    public static final double BIOME_DRAIN_MULTIPLIER = 0.04;
    public static final double ENEMY_DRAIN_MULTIPLIER = 0.015;

    // cached item IDs
    public static final int ITEM_WASHED_IRON = 2608;
    public static final int ITEM_STOMPED_CRUSHED_IRON = 2609;

    // cached block IDs
    public static final int BLOCK_IRON_BLOOM = 2403;
    public static final int BLOCK_STONE_ANVIL = 2404;
    public static final int BLOCK_DIAMOND_ANVIL = 2405;
    public static final int BLOCK_DRYING_GRASS = 2410;

    // icons - registered in TextureMapMixin
    public static Icon ICON_SLURRY;
    public static Icon ICON_BRINE;
    public static Icon ICON_ACID;
    public static Icon ICON_HEAT_1;
    public static Icon ICON_HEAT_2;
    public static Icon ICON_HEAT_3;
    public static Icon ICON_STIRRING;
    public static Icon ICON_SKILL_REQUIREMENT_BACKGROUND;

}
