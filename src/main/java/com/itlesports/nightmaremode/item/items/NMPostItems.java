package com.itlesports.nightmaremode.item.items;

import com.itlesports.nightmaremode.achievements.NMAchievements;
import com.itlesports.nightmaremode.item.items.bloodItems.ItemEventController;
import net.minecraft.src.CreativeTabs;

import static com.itlesports.nightmaremode.item.items.bloodItems.ItemEventController.EVENT_BLOODMOON;
import static com.itlesports.nightmaremode.item.items.bloodItems.ItemEventController.EVENT_ECLIPSE;


public class NMPostItems {
    // these items require the NMAchievements to be initialized, which requires NMItems to initialize ...
    // this gets initialized right after items and achievements
    public static ItemAchievementGranter timeBottle;
    public static ItemEventController bloodMoonBottle;

    static {
        timeBottle = (ItemAchievementGranter) new ItemAchievementGranter(2500, NMAchievements.GREED, NMAchievements.CRAFT_OVEN_FAST).setUnlocalizedName("nmTimeBottle").setTextureName("nightmare:nmTimeBottle").setCreativeTab(CreativeTabs.tabFood);
        bloodMoonBottle = (ItemEventController) new ItemEventController(2501, EVENT_BLOODMOON).setUnlocalizedName("nmBloodBottle").setTextureName("nightmare:nmBloodBottle").setCreativeTab(CreativeTabs.tabFood);
        bloodMoonBottle = (ItemEventController) new ItemEventController(2502, EVENT_ECLIPSE).setUnlocalizedName("nmEclipseBottle").setTextureName("nightmare:nmEclipseBottle").setCreativeTab(CreativeTabs.tabFood);
    }
    public static void runPostInit(){}
}
