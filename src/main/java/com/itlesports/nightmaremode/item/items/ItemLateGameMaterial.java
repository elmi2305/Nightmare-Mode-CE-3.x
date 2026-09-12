package com.itlesports.nightmaremode.item.items;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.Icon;
import net.minecraft.src.IconRegister;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;

import java.util.List;

public class ItemLateGameMaterial extends Item {
    public static final int GRAVITITE_CHUNK = 0;
    public static final int GRAVITITE_NUGGET = 1;
    public static final int GRAVITITE_INGOT = 2;
    public static final int SOLAR_QUARTZ_CHUNK = 3;
    public static final int SOLAR_QUARTZ_NUGGET = 4;
    public static final int SOLAR_QUARTZ_INGOT = 5;
    public static final int AETHER_CHUNK = 6;
    public static final int AETHER_NUGGET = 7;
    public static final int AETHER_INGOT = 8;
    public static final int ABYSS_CHUNK = 9;
    public static final int ABYSS_NUGGET = 10;
    public static final int ABYSS_INGOT = 11;
    public static final int CRYOLITE_CHUNK = 12;
    public static final int CRYOLITE_NUGGET = 13;
    public static final int CRYOLITE_INGOT = 14;
    public static final int SHADOW_DUST = 15;
    public static final int ENDER_BONE = 16;
    public static final int VERTEBRAE = 17;
    public static final int BLACKWIDOW_GLAND = 18;
    public static final int DEADZONE_TEAR = 19;
    public static final int VOID_POWDER = 20;
    public static final int DISPLACED_PEARL = 21;
    public static final int CHARRED_STRING = 22;
    public static final int CINDER_BONE = 23;
    public static final int DESICCATED_FLESH = 24;
    public static final int LUMINOUS_INK_SAC = 25;
    public static final int HALO_TEAR = 26;
    public static final int ANGEL_BREATH = 27;
    public static final int CAUSTIC_TEAR = 28;
    public static final int ACID_INK_SAC = 29;
    public static final int FROZEN_BONE = 30;
    public static final int FROZEN_FLESH = 31;
    public static final int DEADZONE_ESSENCE = 32;
    public static final int SOLAR_ESSENCE = 33;
    public static final int VOID_ESSENCE = 34;
    public static final int ABYSSAL_ESSENCE = 35;
    public static final int FROZEN_ESSENCE = 36;
    public static final int CONVERGENCE_ESSENCE = 37;

    private static final String[] NAMES = {
            "GravititeChunk", "GravititeNugget", "GravititeIngot",
            "SolarQuartzChunk", "SolarQuartzNugget", "SolarQuartzIngot",
            "AetherChunk", "AetherNugget", "AetherIngot",
            "AbyssChunk", "AbyssNugget", "AbyssIngot",
            "CryoliteChunk", "CryoliteNugget", "CryoliteIngot",
            "ShadowDust", "EnderBone", "Vertebrae", "BlackwidowGland", "DeadzoneTear",
            "VoidPowder", "DisplacedPearl", "CharredString", "CinderBone", "DesiccatedFlesh",
            "LuminousInkSac", "HaloTear", "AngelBreath", "CausticTear", "AcidInkSac",
            "FrozenBone", "FrozenFlesh", "DeadzoneEssence", "SolarEssence", "VoidEssence",
            "AbyssalEssence", "FrozenEssence", "ConvergenceEssence"
    };
    private static final int[] COLORS = {
            0x7866A8,0xA18FC7,0xC0AFE8, 0xD8832D,0xF0B95A,0xFFD87A,
            0xC8E7FF,0xDDF2FF,0xF2FAFF, 0x16445A,0x2B718A,0x49A7BA,
            0x76CFE8,0xA7E8F6,0xD4F7FF, 0x302746,0x7456A0,0x3B3337,0x8E1E31,0x4A3B67,
            0x15131E,0x7D4DA4,0xB64620,0xE15A26,0xA8895A, 0xE8E5FF,0xE0C9FF,0xFFF4D0,
            0x74B929,0x84D33D,0xBDEEFF,0x96D7EF, 0x594472,0xCF812E,0xE7E7FF,0x25647A,0x8FDFF2,0xD6B4FF
    };
    @Environment(EnvType.CLIENT) private Icon chunkIcon;
    @Environment(EnvType.CLIENT) private Icon nuggetIcon;
    @Environment(EnvType.CLIENT) private Icon ingotIcon;
    @Environment(EnvType.CLIENT) private Icon[] dropIcons;
    @Environment(EnvType.CLIENT) private Icon essenceIcon;

    public ItemLateGameMaterial(int id) {
        super(id);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
        this.setCreativeTab(CreativeTabs.tabMaterials);
        this.setUnlocalizedName("ifhyLateGameMaterial");
    }

    @Override public String getUnlocalizedName(ItemStack stack) {
        return "item.ifhy" + NAMES[Math.max(0, Math.min(NAMES.length - 1, stack.getItemDamage()))];
    }

    @Override @Environment(EnvType.CLIENT)
    public void registerIcons(IconRegister register) {
        this.chunkIcon = register.registerIcon("nightmare:ifhyTungstenChunk");
        this.nuggetIcon = register.registerIcon("nightmare:ifhyTungstenNugget");
        this.ingotIcon = register.registerIcon("nightmare:ifhyTungstenIngot");
        this.dropIcons = new Icon[DEADZONE_ESSENCE - SHADOW_DUST];
        for (int metadata = SHADOW_DUST; metadata < DEADZONE_ESSENCE; ++metadata) {
            String name = this.getUnlocalizedName(new ItemStack(this, 1, metadata)).substring("item.".length());
            this.dropIcons[metadata - SHADOW_DUST] = register.registerIcon("nightmare:" + name);
        }
        this.essenceIcon = register.registerIcon("nightmare:ifhyAutomationEssence");
        this.itemIcon = this.dropIcons[0];
    }

    @Override @Environment(EnvType.CLIENT)
    public Icon getIconFromDamage(int metadata) {
        if (metadata >= DEADZONE_ESSENCE) return this.essenceIcon;
        if (metadata < SHADOW_DUST) return switch (metadata % 3) {
            case 0 -> this.chunkIcon;
            case 1 -> this.nuggetIcon;
            default -> this.ingotIcon;
        };
        return this.dropIcons[metadata - SHADOW_DUST];
    }

    @Override @Environment(EnvType.CLIENT)
    public int getColorFromItemStack(ItemStack stack, int pass) {
        if (stack.getItemDamage() >= SHADOW_DUST && stack.getItemDamage() < DEADZONE_ESSENCE) return 0xFFFFFF;
        return COLORS[Math.max(0, Math.min(COLORS.length - 1, stack.getItemDamage()))];
    }

    @Override public void getSubItems(int id, CreativeTabs tab, List list) {
        for (int metadata = 0; metadata < NAMES.length; ++metadata) list.add(new ItemStack(id, 1, metadata));
    }
}
