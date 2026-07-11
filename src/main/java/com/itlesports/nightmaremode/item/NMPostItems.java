package com.itlesports.nightmaremode.item;

import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.achievements.NMAchievements;
import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.block.blocks.templates.NMPlaceAsBlockItem;
import com.itlesports.nightmaremode.item.items.template.ItemAchievementGranter;
import com.itlesports.nightmaremode.item.items.bloodItems.ItemEventController;
import com.itlesports.nightmaremode.item.items.template.NMItem;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.Item;

import static com.itlesports.nightmaremode.item.items.bloodItems.ItemEventController.EVENT_BLOODMOON;
import static com.itlesports.nightmaremode.item.items.bloodItems.ItemEventController.EVENT_ECLIPSE;


public class NMPostItems {
    // these items require the NMAchievements to be initialized, which requires NMItems to initialize ...
    // this gets initialized right after items and achievements
    public static ItemAchievementGranter timeBottle;
    public static ItemEventController bloodMoonBottle;
    public static ItemEventController eclipseBottle;
    public static final Item washedIronMix;
    public static final Item stompedCrushedIronStoneMix;

    static {
        timeBottle = (ItemAchievementGranter) new ItemAchievementGranter(2500, NMAchievements.GREED, NMAchievements.CRAFT_OVEN_FAST).setUnlocalizedName("nmTimeBottle").setTextureName("nightmare:nmTimeBottle").setCreativeTab(CreativeTabs.tabFood);
        bloodMoonBottle = (ItemEventController) new ItemEventController(2501, EVENT_BLOODMOON).setUnlocalizedName("nmBloodBottle").setTextureName("nightmare:nmBloodBottle").setCreativeTab(CreativeTabs.tabFood);
        eclipseBottle = (ItemEventController) new ItemEventController(2502, EVENT_ECLIPSE).setUnlocalizedName("nmEclipseBottle").setTextureName("nightmare:nmEclipseBottle").setCreativeTab(CreativeTabs.tabFood);
        washedIronMix = new NMItem(2608).setTextureName("nightmare:ifhyCrushedIron").setUnlocalizedName("ifhyCrushedIron").setCreativeTab(CreativeTabs.tabMaterials);
        stompedCrushedIronStoneMix = new NMPlaceAsBlockItem(2609, NMBlocks.blockCrushedIronLayer.blockID).setWashable(NMPostItems.washedIronMix.itemID).setTextureName("nightmare:ifhyDriedCrushedIron").setUnlocalizedName("ifhyDriedCrushedIron").setCreativeTab(CreativeTabs.tabMaterials);

        if(!NightmareMode.devMode){
            eclipseBottle = (ItemEventController) eclipseBottle.hideFromEMI().setCreativeTab(null);
        }
    }
    public static void runPostInit(){}
}
