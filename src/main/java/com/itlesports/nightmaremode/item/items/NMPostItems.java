package com.itlesports.nightmaremode.item.items;

import com.itlesports.nightmaremode.achievements.NMAchievements;
import net.minecraft.src.CreativeTabs;

public class NMPostItems {

    public static ItemAchievementGranter timeBottle;


    static {
        timeBottle = (ItemAchievementGranter) new ItemAchievementGranter(2500, NMAchievements.GREED, NMAchievements.CRAFT_OVEN_FAST).setUnlocalizedName("nmTimeBottle").setTextureName("nmTimeBottle").setCreativeTab(CreativeTabs.tabFood);
    }
    public static void runPostInit(){}
}
