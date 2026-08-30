package com.itlesports.nightmaremode.item;

import api.item.tag.Tag;
import btw.item.BTWItems;
import com.itlesports.nightmaremode.block.NMBlocks;
import net.minecraft.src.Item;
import net.minecraft.src.ResourceLocation;


public class NMTags {
    public static final Tag sandwichMeats = Tag.of(loc("sandwich_meats"), Item.beefCooked, Item.chickenCooked, Item.porkCooked, BTWItems.cookedMutton, BTWItems.cookedCheval, BTWItems.cookedWolfChop, BTWItems.cookedMysteryMeat, BTWItems.hamAndEggs);
    public static final Tag eclipseDrops = Tag.of(loc("eclipse_drops"),
            NMItems.darksunFragment,
            NMItems.magicFeather,
            NMItems.bloodMilk,
            NMItems.creeperChop,
            NMItems.voidSack,
            NMItems.charredFlesh,
            NMItems.spiderFangs,
            NMItems.fireRod,
            NMItems.waterRod,
            NMItems.sulfur,
            NMItems.creeperTear,
            NMItems.silverLump,
            NMItems.witheredBone,
            NMItems.voidMembrane,
            NMItems.decayedFlesh,
            NMItems.ghastTentacle,
            NMItems.elementalRod,
            NMItems.shadowRod,
            NMItems.speedCoil
    );
    public static final Tag bookLeather = Tag.of(loc("book_leather"),
            BTWItems.cutTannedLeather,
            BTWItems.cutScouredLeather
    );
    public static final Tag knifeStrings = Tag.of(loc("knife_strings"),
            Item.silk,
            NMItems.pighideString
    );
    public static final Tag ironTungstenIngots = Tag.of(loc("iron_tungsten_ingots"),
            Item.ingotIron,
            NMItems.carbonIronIngot,
            NMItems.reinforcedIronIngot,
            NMItems.tungstenIngot
    );
    public static final Tag ironTungstenNuggets = Tag.of(loc("iron_tungsten_nuggets"),
            BTWItems.ironNugget,
            NMItems.carbonIronNugget,
            NMItems.tungstenNugget
    );
    public static final Tag netherSignalBinders = Tag.of(loc("nether_signal_binders"),
            NMItems.azureSalt,
            NMItems.searingSilverScale
    );
    public static final Tag hammers = Tag.of(loc("hammers"),
            NMItems.woodHammer,
            NMItems.stoneHammer,
            NMItems.ironHammer,
            NMItems.diamondHammer,
            NMItems.goldHammer,
            NMItems.steelHammer,
            NMItems.netherrackHammer
    );
    public static final Tag anvils = Tag.of(loc("anvils"),
            net.minecraft.src.Block.anvil,
            NMBlocks.stoneAnvil,
            NMBlocks.ironAnvil,
            NMBlocks.diamondAnvil,
            NMBlocks.netherrackAnvil
    );

    private static ResourceLocation loc(String name) {
        return new ResourceLocation("nightmare", name);
    }


    public static void initTags(){}
}
