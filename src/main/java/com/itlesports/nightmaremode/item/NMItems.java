package com.itlesports.nightmaremode.item;

import btw.item.BTWTags;
import btw.item.items.*;
import com.itlesports.nightmaremode.agriculture.ChunkAttribute;
import com.itlesports.nightmaremode.block.blocks.templates.NMPlaceAsBlockItem;
import com.itlesports.nightmaremode.block.blocks.templates.NMNetherPlaceAsBlockItem;
import com.itlesports.nightmaremode.item.items.*;
import com.itlesports.nightmaremode.item.items.bloodItems.*;
import com.itlesports.nightmaremode.item.items.template.*;
import com.itlesports.nightmaremode.skill.item.ItemSkillBook;
import com.itlesports.nightmaremode.util.NMFields;
import net.minecraft.src.*;

public class NMItems {
    public static final int BLOOD_MOON_DURABILITY = 1200;


    public static ItemRPG rpg;
    public static ItemAR rifle;
    public static ItemBandage bandage;
    public static ItemIronKnittingNeedles ironKnittingNeedles;
    public static ItemStructureLocator witchLocator;

    public static ItemBloodOrb bloodOrb;
    public static ItemBloodPickaxe bloodPickaxe;
    public static ItemBloodAxe bloodAxe;
    public static ItemBloodShovel bloodShovel;
    public static ItemBloodHoe bloodHoe;
    public static ItemBloodSword bloodSword;

    public static ItemBloodArmor bloodHelmet;
    public static ItemBloodArmor bloodChestplate;
    public static ItemBloodArmor bloodLeggings;
    public static ItemBloodArmor bloodBoots;
    public static Item bloodIngot;

    public static Item darksunFragment;
    public static Item magicFeather;
    public static ItemBucketMilk bloodMilk;
    public static FoodItem creeperChop;
    public static Item voidSack;
    public static RottenFleshItem charredFlesh;
    public static Item spiderFangs;
    public static Item fireRod;
    public static Item waterRod;
    public static Item sulfur;
    public static Item creeperTear;
    public static Item silverLump;
    public static Item witheredBone;
    public static Item voidMembrane;
    public static RottenFleshItem decayedFlesh;
    public static Item ghastTentacle;
    public static Item elementalRod;
    public static Item shadowRod;
    public static Item speedCoil;
    public static NetherStarItem starOfTheBloodGod;
    public static FoodItem calamari;
    public static FoodItem calamariRoast;
    public static FoodItem friedCalamari;
    public static Item steelBunch;

    public static ItemEclipseBow eclipseBow;
    public static ItemMagicArrow magicArrow;
    public static ItemUpgradeableFishingRod ironFishingPole;
    public static ItemUpgradeableFishingRod ironFishingPoleBaited;
    public static ItemUpgradeableFishingRod diamondFishingPole;
    public static ItemUpgradeableFishingRod diamondFishingPoleBaited;
    public static ItemUpgradeableFishingRod steelFishingPole;
    public static ItemUpgradeableFishingRod steelFishingPoleBaited;
    public static Item fishingBellUpgrade;
    public static Item fishingLureUpgrade;
    public static Item fishingAutoReelUpgrade;
    public static Item rareFishLureUpgrade;
    public static Item fishingEssence;

    public static FoodItem dungApple;
    public static FoodItem creeperBallSoup;

    public static Item ACHIEVEMENT_SPECIAL_SNOWBALL;
    public static Item ACHIEVEMENT_SPECIAL_HARDMODE;
    public static Item ACHIEVEMENT_SPECIAL_BLOODMOON;
    public static Item ACHIEVEMENT_SPECIAL_BLOODMOON_WITHER;
    public static Item ACHIEVEMENT_SPECIAL_ECLIPSE;
    public static Item ACHIEVEMENT_SPECIAL_MERCHANT;
    public static Item ACHIEVEMENT_SPECIAL_CHICKEN;
    public static Item ACHIEVEMENT_SPECIAL_DIAMOND;
    public static Item ACHIEVEMENT_SPECIAL_SKULL;
    public static Item ACHIEVEMENT_SPECIAL_ARROW_TRIPLE;
    public static Item ACHIEVEMENT_SPECIAL_ARROW_RED;
    public static Item ACHIEVEMENT_SPECIAL_TRIPLE_TEAR;
    public static Item ACHIEVEMENT_SPECIAL_BLOOD_ZOMBIE;

    public static ItemStructureLocator templeLocator;
    public static Item refinedDiamondIngot;
    public static Item lightningBolt;
    public static Item villagerOrb;
    public static Item refinedElement;
    public static Item witherSoul;

    public static Item obsidianShard;
    public static Item honeyBall;

    public static Item lifeFruit;
    public static Item honeyMelon;
    public static Item awakenedStar;
    public static Item hellGem;



    // IFHY

    public static final Item bonusChestLoot;
    public static final Item twig;
    public static final Item sharpTwig;
    public static final Item sharpBarkTwig;
    public static final Item woodClump;
    public static final Item leaf;
    public static final Item twigSharpening;
    public static final Item sharpTwigBarkWrapping;
    public static final Item flintChip;
    public static final ItemKnowledgeBook knowledgeBook;

    public static Item woodHammer;
    public static Item stoneHammer;
    public static Item ironHammer;
    public static Item diamondHammer;
    public static Item goldHammer;
    public static Item steelHammer;

    public static Item ironBloom;
    public static Item scrapedBark;
    public static Item woodCup;
    public static Item cupOfSap;
    public static Item thickenedSap;
    public static Item ovenPart;
    public static Item drill;

    public static Item nickelRawRock;
    public static Item nickelCrushedRock;
    public static Item nickelWashedConcentrate;
    public static Item nickelRoastedConcentrate;
    public static Item nickelIngot;
    public static Item nickelPlate;
    public static Item nickelBinding;
    public static Item nickelMachinePart;
    public static Item nickelHeatComponent;

    public static Item lithiumRaw;
    public static Item lithiumHammered;
    public static Item lithiumWashed;
    public static Item lithiumRefined;
    public static Item lithiumSalt;
    public static Item lithiumStabilizer;
    public static Item lithiumHeatCompound;

    public static Item crystalUncleanedShard;
    public static Item crystalCleanShard;
    public static Item crystalPolishedShard;
    public static Item crystalLens;
    public static Item crystalPrecisionGear;

    public static Item diamondBearingRock;
    public static Item crackedDiamondBearingRock;
    public static Item washedDiamondGrit;
    public static Item stabilizedDiamondSlurry;
    public static Item seededDiamondMatrix;
    public static Item nickelBoundDiamondMatrix;
    public static Item diamondBearingMaterial;
    public static Item failedDiamondRefinement;
    public static Item refinementWaste;
    public static ItemOxygenGear oxygenMask;
    public static ItemOxygenGear oxygenTank;
    public static Item plantFiber;
    public static Item driedPlantFiber;
    public static Item skillBook;
    public static Item flintAxe;
    public static NMProgressiveItem flintAxeCrafting;
    public static Item crudeString;
    public static NMProgressiveItem crudeStringCrafting;
    public static Item primitiveGlue;
    public static Item spiderSilk;
    public static NMProgressiveItem stringCrafting;
    public static NMProgressiveItem woodCupCrafting;
    public static NMProgressiveItem unshapedWetClayBrick;
    public static Item reedStem;
    public static NMProgressiveItem reedPeeling;
    public static Item washedPith;
    public static Item wetFusedPlantSheet;
    public static Item plantSheet;
    public static Item washedSugarCane;
    public static Item pileOfSticks;
    public static Item boneShard;
    public static ItemKnife stoneKnife;
    public static ItemKnife ironKnife;
    public static ItemKnife diamondKnife;
    public static ItemKnife goldKnife;
    public static ItemKnife tungstenKnife;
    public static Item ash;
    public static Item ashClump;
    public static Item soulChip;
    public static Item soulFlint;
    public static Item pigHide;
    public static Item pighideString;
    public static NMProgressiveItem pighideStringCrafting;
    public static Item quartzDust;
    public static Item tungstenDust;
    public static Item netherrackChunk;
    public static Item netherWorkbenchPart;
    public static Item netherStick;
    public static Item netherrackPickaxe;
    public static Item tungstenPickaxe;
    public static Item tungstenShovel;
    public static Item ironScythe;
    public static Item diamondScythe;
    public static Item tungstenScythe;
    public static Item aquamarine;
    public static Item netherFishingRod;
    public static Item netherFishingRodBaited;
    public static Item lavafish;
    public static Item netherrackHammer;
    public static Item tungstenChunk;
    public static Item crushedTungsten;
    public static Item tungstenConcentrate;
    public static Item brittleTungstenCake;
    public static Item tungstenPowder;
    public static Item pureTungstenChunk;
    public static Item tungstenNugget;
    public static Item tungstenIngot;
    public static Item tungstenBucket;
    public static Item tungstenLavaBucket;
    public static Item obsidianPowder;
    public static Item obsidianPaste;
    public static Item obsidianBrick;
    public static Item rettedHemp;
    public static Item washedHemp;
    public static Item driedHemp;
    public static Item washedScouredLeather;
    public static Item workedScouredLeather;
    public static ItemChunkFertilizer moistureFertilizer;
    public static ItemChunkFertilizer potassiumFertilizer;
    public static ItemChunkFertilizer acidityFertilizer;
    public static ItemChunkFertilizer porosityFertilizer;
    public static Item highSpeedMinecart;
    public static Item highSpeedChestMinecart;
    public static Item highSpeedFurnaceMinecart;

    public static FoodItem mackerel;
    public static FoodItem cod;
    public static FoodItem tuna;
    public static FoodItem swordfish;
    public static FoodItem bass;
    public static FoodItem trout;
    public static FoodItem carp;
    public static FoodItem goldenCarp;
    public static FoodItem mudfish;
    public static FoodItem catfish;
    public static FoodItem swampEel;
    public static FoodItem alligatorGar;
    public static FoodItem piranha;
    public static FoodItem neonTetra;
    public static FoodItem jungleCatfish;
    public static FoodItem arapaima;
    public static FoodItem salmon;
    public static FoodItem perch;
    public static FoodItem icefish;
    public static FoodItem frostfish;
    public static FoodItem desertMinnow;
    public static FoodItem sandfish;
    public static FoodItem tilapia;
    public static FoodItem duneKoi;
    public static NMProgressiveItem fishFlesh;
    public static FoodItem debonedRawFish;
    private static Item[] rawFish;


    public static Item redstoneCrystal;
    public static Item refinedRedstone;
    public static Item azureSalt;
    public static Item azureSlag;
    public static Item brittleAzureCake;
    public static Item rawAzureStone;
    public static Item crushedAzureStone;
    public static Item washedAzureSediment;
    public static Item lapisPrecipitate;
    public static Item hydraulicLens;
    public static Item fluidGauge;
    public static Item searingSilverScale;
    public static Item denseNetherrackCore;
    public static Item deadzoneShard;
//    public static Item netherTradePlaceholder;
    public static Item invocationFragment;
    public static Item invocationSeal;
    public static Item endAccordFragment;
    public static Item endAccord;
    public static Item debugVillagerLevel;
    public static Item debugVillagerProgress;
    public static Item debugVillagerReroll;
    public static Item librarianEnderTreatise;
    public static Item automationEssence;
    public static Item husbandryEssence;
    public static Item infernalEssence;
    public static Item artisanEssence;
    public static Item stoneStick;
    public static Item ironStick;
    public static Item diamondStick;
    public static Item glueSlurry;
    public static Item pressedGlueCake;
    public static Item roughStoneBrick;
    public static Item hammeredStoneBrick;
    public static Item mortaredStoneBrick;
    public static Item ironBrick;
    public static Item diamondBrick;
    public static Item crystalPowder;
    public static Item glassBatch;
    public static Item dyeBlend;
    public static Item potassiumCrystal;
    public static Item nitrogenCrystal;
    public static Item acidCrystal;
    public static Item porosityAggregate;
    public static Item soilSample;
    public static Item brokenHoeFragment;
    public static Item farmersFavoriteHoe;
    public static Item unbakedChocolateCake;
    public static Item chocolateCake;
    public static Item burnedChocolateCake;
    public static Item brokenPickaxeFragment;
    public static Item blacksmithFavoritePickaxe;
    public static Item mechanicalWrench;

    // Post-dragon End / Eclipse progression
    public static Item rawMercuryCrystal;
    public static Item mercuryPowder;
    public static Item washedMercuryConcentrate;
    public static Item mercuryAmalgam;
    public static Item enderCrystal;
    public static Item enderDust;
    public static Item enderShell;
    public static Item enderShellPowder;
    public static Item paleRoot;
    public static Item paleRootSeeds;
    public static Item paleRootPulp;
    public static Item paleRootResin;
    public static Item firedCrucibleLiner;
    public static Item phaseSteelCharge;
    public static Item phaseSteelIngot;
    public static Item phaseSteelPlate;
    public static Item enderMechanism;
    public static ItemEnderSword enderSword;
    public static ItemEnderPickaxe enderPickaxe;
    public static ItemEnderAxe enderAxe;
    public static ItemEnderShovel enderShovel;
    public static ItemEnderHoe enderHoe;
    public static Item snowPile;
    public static ItemEnderArmor enderHelmet;
    public static ItemEnderArmor enderChestplate;
    public static ItemEnderArmor enderLeggings;
    public static ItemEnderArmor enderBoots;

    // IFHY alloy armor expansion
    public static Item carbonRichIronMix;
    public static Item carburizedIronBloom;
    public static Item carbonIronNugget;
    public static Item carbonIronIngot;
    public static Item carbonIronPlate;
    public static Item lithiumTreatedIronBlank;
    public static Item reinforcedIronIngot;
    public static Item reinforcedIronPlate;
    public static Item wetGasket;
    public static Item waxedGasket;
    public static Item refractoryPaste;
    public static Item wetRefractoryCloth;
    public static Item refractoryCloth;
    public static Item pressureRegulator;
    public static Item thermalLaminate;
    public static ItemCarbonIronArmor carbonIronHelmet;
    public static ItemCarbonIronArmor carbonIronChestplate;
    public static ItemCarbonIronArmor carbonIronLeggings;
    public static ItemCarbonIronArmor carbonIronBoots;
    public static ItemAlloyArmor reinforcedIronHelmet;
    public static ItemAlloyArmor reinforcedIronChestplate;
    public static ItemAlloyArmor reinforcedIronLeggings;
    public static ItemAlloyArmor reinforcedIronBoots;
    public static ItemAlloyArmor nickelWorkLeggings;
    public static ItemAlloyArmor nickelWorkBoots;
    public static ItemHeatResistantArmor heatResistantHelmet;
    public static ItemHeatResistantArmor heatResistantChestplate;
    public static ItemHeatResistantArmor heatResistantLeggings;
    public static ItemHeatResistantArmor heatResistantBoots;
    public static ItemDivingGear divingMask;
    public static ItemDivingGear divingTank;
    public static Item tungstenPlate;
    public static Item thermalChestLining;
    public static Item tankReinforcementCradle;
    public static Item moltenQuartzCompound;
    public static Item quartzglassIngot;
    public static Item quartzglassPlate;
    public static Item crackedEmerald;
    public static Item emeraldGrit;
    public static Item washedEmeraldPowder;
    public static Item verdantIngot;
    public static Item verdantPlate;
    public static Item blackglassCharge;
    public static Item blackglassIngot;
    public static Item blackglassPlate;
    public static Item saturatedCoresteelCharge;
    public static Item cooledCoresteelCharge;
    public static Item coresteelIngot;
    public static Item coresteelPlate;
    public static Item unstableDeadzoneCharge;
    public static Item deadzoneAlloyIngot;
    public static Item deadzoneAlloyPlate;
    public static Item solarBaffle;
    public static ItemNetherAlloyArmor tungstenHelmet;
    public static ItemNetherAlloyArmor tungstenChestplate;
    public static ItemNetherAlloyArmor tungstenLeggings;
    public static ItemNetherAlloyArmor tungstenBoots;
    public static ItemCoresteelArmor coresteelHelmet;
    public static ItemCoresteelArmor coresteelChestplate;
    public static ItemCoresteelArmor coresteelLeggings;
    public static ItemCoresteelArmor coresteelBoots;
    public static ItemNetherAlloyArmor deadzoneHelmet;
    public static ItemNetherAlloyArmor deadzoneChestplate;
    public static ItemNetherAlloyArmor deadzoneLeggings;
    public static ItemNetherAlloyArmor deadzoneBoots;
    public static ItemHeatResistantArmor sunHelmet;
    public static ItemHeatResistantArmor sunChestplate;
    public static ItemHeatResistantArmor sunLeggings;
    public static ItemHeatResistantArmor sunBoots;
    public static ItemSunDivingGear sunVisor;
    public static ItemSunDivingGear sunReservoir;
    public static Item signalConductiveCharge;
    public static Item signalAlloyIngot;
    public static Item signalAlloyPlate;
    public static Item azureSlip;
    public static Item azureCeramicIngot;
    public static Item azureCeramicPlate;
    public static Item crystalLatticeCharge;
    public static Item setCrystalLattice;
    public static Item prismaticIngot;
    public static Item prismaticPlate;
    public static Item sealedQuicksilverIngot;
    public static Item sealedQuicksilverPlate;
    public static Item endstonePowder;
    public static Item endstoneClay;
    public static Item endstoneIngot;
    public static Item endstonePlate;
    public static Item darkIngot;
    public static ItemChargedArmor signalHelmet;
    public static ItemChargedArmor signalChestplate;
    public static ItemChargedArmor signalLeggings;
    public static ItemChargedArmor signalBoots;
    public static ItemAlloyArmor azureHelmet;
    public static ItemAlloyArmor azureChestplate;
    public static ItemAlloyArmor azureLeggings;
    public static ItemAlloyArmor azureBoots;
    public static ItemAlloyArmor prismaticHelmet;
    public static ItemAlloyArmor prismaticChestplate;
    public static ItemAlloyArmor prismaticLeggings;
    public static ItemAlloyArmor prismaticBoots;
    public static ItemAlloyArmor refinedPrismaHelmet;
    public static ItemAlloyArmor refinedPrismaChestplate;
    public static ItemAlloyArmor refinedPrismaLeggings;
    public static ItemAlloyArmor refinedPrismaBoots;
    public static ItemAlloyArmor verdantHelmet;
    public static ItemAlloyArmor verdantChestplate;
    public static ItemAlloyArmor verdantLeggings;
    public static ItemAlloyArmor verdantBoots;
    public static ItemGlassArmor glassHelmet;
    public static ItemGlassArmor glassChestplate;
    public static ItemGlassArmor glassLeggings;
    public static ItemGlassArmor glassBoots;
    public static ItemNetherAlloyArmor blackglassHelmet;
    public static ItemNetherAlloyArmor blackglassChestplate;
    public static ItemNetherAlloyArmor blackglassLeggings;
    public static ItemNetherAlloyArmor blackglassBoots;
    public static ItemNetherAlloyArmor quartzglassHelmet;
    public static ItemNetherAlloyArmor quartzglassChestplate;
    public static ItemNetherAlloyArmor quartzglassLeggings;
    public static ItemNetherAlloyArmor quartzglassBoots;
    public static ItemNetherAlloyArmor darkHelmet;
    public static ItemNetherAlloyArmor darkChestplate;
    public static ItemNetherAlloyArmor darkLeggings;
    public static ItemNetherAlloyArmor darkBoots;
    public static ItemAlloyArmor quicksilverHelmet;
    public static ItemAlloyArmor quicksilverChestplate;
    public static ItemAlloyArmor quicksilverLeggings;
    public static ItemAlloyArmor quicksilverBoots;
    public static ItemAlloyArmor anchorHelmet;
    public static ItemAlloyArmor anchorChestplate;
    public static ItemAlloyArmor anchorLeggings;
    public static ItemAlloyArmor anchorBoots;


    static {
        doNightmareModeItems();
        // info: due to the order of initialization, NMBlocks fields are null at this time, so NMPlaceAsBlockItem or other initializers that require NMBlocks must be passed a copy of the block ID int registered in NMFields
        // instead of getting the block id from the NMBlocks field. attempting to do so crashes the game with a nullptr

        bonusChestLoot = new NMItem(2600).setTextureName("nightmare:ifhyGarbage").setUnlocalizedName("ifhyGarbage");
        twig = new NMItem(2601).setTextureName("nightmare:ifhyTwig").setUnlocalizedName("ifhyTwig").setCreativeTab(CreativeTabs.tabMaterials);
        sharpTwig = new NMToolItem(2602, EnumToolMaterial.WOOD, new Block[]{Block.wood}, 1, 0.5f).setTextureName("nightmare:ifhyTwigSharp").setUnlocalizedName("ifhyTwigSharp");
        sharpBarkTwig = new NMToolItem(2603, EnumToolMaterial.WOOD, new Block[]{Block.wood}, 3, 0.75f).setTextureName("nightmare:ifhyTwigSharpBark").setUnlocalizedName("ifhyTwigSharpBark");
        woodClump = new NMProgressiveItem(2604, Item.stick.itemID).setTargetDurability(200).setTextureName("nightmare:ifhyWoodClump").setUnlocalizedName("ifhyWoodClump").setCreativeTab(CreativeTabs.tabMaterials);
        leaf = new NMItem(2605).setTextureName("nightmare:ifhyLeaf").setUnlocalizedName("ifhyLeaf").setCreativeTab(CreativeTabs.tabMaterials);
        twigSharpening = new NMProgressiveItem(2606, NMItems.sharpTwig.itemID).setTargetDurability(200).setTextureName("nightmare:ifhyTwigSharpen").setUnlocalizedName("ifhyTwigSharpen").setCreativeTab(CreativeTabs.tabMaterials);
        sharpTwigBarkWrapping = new NMProgressiveItem(2607, NMItems.sharpBarkTwig.itemID).setTargetDurability(50).setTextureName("nightmare:ifhyTwigWrap").setUnlocalizedName("ifhyTwigWrap").setCreativeTab(CreativeTabs.tabMaterials);
        knowledgeBook = new ItemKnowledgeBook(2741);

        // NMPostItems takes 2608 2609
        diamondHammer = new ItemHammer(2610, EnumToolMaterial.EMERALD).setUnlocalizedName("ifhyDiamondHammer").setTextureName("nightmare:ifhyDiamondHammer").setCreativeTab(CreativeTabs.tabTools);
        goldHammer = new ItemHammer(2611, EnumToolMaterial.GOLD).setUnlocalizedName("ifhyGoldHammer").setTextureName("nightmare:ifhyGoldHammer").setCreativeTab(CreativeTabs.tabTools);
        ironHammer = new ItemHammer(2612, EnumToolMaterial.IRON).setUnlocalizedName("ifhyIronHammer").setTextureName("nightmare:ifhyIronHammer").setCreativeTab(CreativeTabs.tabTools);
        steelHammer = new ItemHammer(2613, EnumToolMaterial.SOULFORGED_STEEL).setUnlocalizedName("ifhySteelHammer").setTextureName("nightmare:ifhySteelHammer").setCreativeTab(CreativeTabs.tabTools);
        woodHammer = new ItemHammer(2614, EnumToolMaterial.WOOD).setUnlocalizedName("ifhyWoodHammer").setTextureName("nightmare:ifhyWoodHammer").setCreativeTab(CreativeTabs.tabTools);
        stoneHammer = new ItemHammer(2615, EnumToolMaterial.STONE).setUnlocalizedName("ifhyStoneHammer").setTextureName("nightmare:ifhyStoneHammer").setCreativeTab(CreativeTabs.tabTools);

        ironBloom = new NMPlaceAsBlockItem(2616, NMFields.BLOCK_IRON_BLOOM).setUnlocalizedName("ifhyIronBloom").setTextureName("nightmare:ifhyIronBloom").setCreativeTab(CreativeTabs.tabMaterials);
        scrapedBark = new NMItem(2617).setUnlocalizedName("ifhyScrapedBark").setTextureName("nightmare:ifhyScrapedBark").setCreativeTab(CreativeTabs.tabMaterials);
        woodCup = new NMItem(2618).setUnlocalizedName("ifhyWoodCup").setTextureName("nightmare:ifhyWoodCup").setCreativeTab(CreativeTabs.tabMaterials);
        cupOfSap = new NMItem(2619).setUnlocalizedName("ifhyCupOfSap").setTextureName("nightmare:ifhyCupOfSap").setCreativeTab(CreativeTabs.tabMaterials);
        thickenedSap = new NMItem(2620).setUnlocalizedName("ifhyThickenedSap").setTextureName("nightmare:ifhyThickenedSap").setCreativeTab(CreativeTabs.tabMaterials);
        ovenPart = new NMItem(2621).setMaxStackSize(4).setUnlocalizedName("ifhyOvenPart").setTextureName("nightmare:ifhyOvenPart").setCreativeTab(CreativeTabs.tabMaterials);
        drill = new ItemDrill(2622).setUnlocalizedName("ifhyDrill").setTextureName("nightmare:ifhyDrill").setCreativeTab(CreativeTabs.tabTools);

        nickelRawRock = new NMItem(2623).setUnlocalizedName("ifhyRawNickelRock").setTextureName("nightmare:ifhyRawNickelRock").setCreativeTab(CreativeTabs.tabMaterials);
        nickelCrushedRock = new NMItem(2624).setUnlocalizedName("ifhyCrushedNickelRock").setTextureName("nightmare:ifhyCrushedNickelRock").setCreativeTab(CreativeTabs.tabMaterials);
        nickelWashedConcentrate = new NMItem(2625).setUnlocalizedName("ifhyWashedNickelConcentrate").setTextureName("nightmare:ifhyWashedNickelConcentrate").setCreativeTab(CreativeTabs.tabMaterials);
        nickelRoastedConcentrate = new NMItem(2626).setUnlocalizedName("ifhyRoastedNickelConcentrate").setTextureName("nightmare:ifhyRoastedNickelConcentrate").setCreativeTab(CreativeTabs.tabMaterials);
        nickelIngot = new NMItem(2627).setUnlocalizedName("ifhyNickelIngot").setTextureName("nightmare:ifhyNickelIngot").setCreativeTab(CreativeTabs.tabMaterials);
        nickelPlate = new NMItem(2628).setUnlocalizedName("ifhyNickelPlate").setTextureName("nightmare:ifhyNickelPlate").setCreativeTab(CreativeTabs.tabMaterials);
        nickelBinding = new NMItem(2629).setUnlocalizedName("ifhyNickelBinding").setTextureName("nightmare:ifhyNickelBinding").setCreativeTab(CreativeTabs.tabMaterials);
        nickelMachinePart = new NMItem(2630).setUnlocalizedName("ifhyNickelMachinePart").setTextureName("nightmare:ifhyNickelMachinePart").setCreativeTab(CreativeTabs.tabMaterials);
        nickelHeatComponent = new NMItem(2631).setUnlocalizedName("ifhyNickelHeatComponent").setTextureName("nightmare:ifhyNickelHeatComponent").setCreativeTab(CreativeTabs.tabMaterials);

        lithiumRaw = new NMItem(2632).setUnlocalizedName("ifhyRawLithium").setTextureName("nightmare:ifhyRawLithium").setCreativeTab(CreativeTabs.tabMaterials);
        lithiumHammered = new NMItem(2633).setUnlocalizedName("ifhyHammeredLithium").setTextureName("nightmare:ifhyHammeredLithium").setCreativeTab(CreativeTabs.tabMaterials);
        lithiumWashed = new NMItem(2634).setUnlocalizedName("ifhyWashedLithium").setTextureName("nightmare:ifhyWashedLithium").setCreativeTab(CreativeTabs.tabMaterials);
        lithiumRefined = new NMItem(2635).setUnlocalizedName("ifhyRefinedLithium").setTextureName("nightmare:ifhyRefinedLithium").setCreativeTab(CreativeTabs.tabMaterials);
        lithiumSalt = new NMItem(2636).setUnlocalizedName("ifhyLithiumSalt").setTextureName("nightmare:ifhyLithiumSalt").setCreativeTab(CreativeTabs.tabMaterials);
        lithiumStabilizer = new NMItem(2637).setUnlocalizedName("ifhyLithiumStabilizer").setTextureName("nightmare:ifhyLithiumStabilizer").setCreativeTab(CreativeTabs.tabMaterials);
        lithiumHeatCompound = new NMItem(2638).setUnlocalizedName("ifhyLithiumHeatCompound").setTextureName("nightmare:ifhyLithiumHeatCompound").setCreativeTab(CreativeTabs.tabMaterials);

        crystalUncleanedShard = new NMItem(2639).setUnlocalizedName("ifhyUncleanedCrystalShard").setTextureName("nightmare:ifhyUncleanedCrystalShard").setCreativeTab(CreativeTabs.tabMaterials);
        crystalCleanShard = new NMProgressiveItem(2640, 2641).setTargetDurability(80).setUnlocalizedName("ifhyCleanCrystalShard").setTextureName("nightmare:ifhyCleanCrystalShard").setCreativeTab(CreativeTabs.tabMaterials);
        crystalPolishedShard = new NMItem(2641).setUnlocalizedName("ifhyPolishedCrystalShard").setTextureName("nightmare:ifhyPolishedCrystalShard").setCreativeTab(CreativeTabs.tabMaterials);
        crystalLens = new NMItem(2642).setUnlocalizedName("ifhyCrystalLens").setTextureName("nightmare:ifhyCrystalLens").setCreativeTab(CreativeTabs.tabMaterials);
        crystalPrecisionGear = new NMItem(2643).setUnlocalizedName("ifhyPrecisionCrystalGear").setTextureName("nightmare:ifhyPrecisionCrystalGear").setCreativeTab(CreativeTabs.tabMaterials);

        diamondBearingRock = new NMItem(2644).setUnlocalizedName("ifhyDiamondBearingRock").setTextureName("nightmare:ifhyDiamondBearingRock").setCreativeTab(CreativeTabs.tabMaterials);
        crackedDiamondBearingRock = new NMItem(2645).setUnlocalizedName("ifhyCrackedDiamondBearingRock").setTextureName("nightmare:ifhyCrackedDiamondBearingRock").setCreativeTab(CreativeTabs.tabMaterials);
        washedDiamondGrit = new NMItem(2646).setUnlocalizedName("ifhyWashedDiamondGrit").setTextureName("nightmare:ifhyWashedDiamondGrit").setCreativeTab(CreativeTabs.tabMaterials);
        stabilizedDiamondSlurry = new NMItem(2647).setUnlocalizedName("ifhyStabilizedDiamondSlurry").setTextureName("nightmare:ifhyStabilizedDiamondSlurry").setCreativeTab(CreativeTabs.tabMaterials);
        seededDiamondMatrix = new NMItem(2648).setUnlocalizedName("ifhySeededDiamondMatrix").setTextureName("nightmare:ifhySeededDiamondMatrix").setCreativeTab(CreativeTabs.tabMaterials);
        nickelBoundDiamondMatrix = new NMItem(2649).setUnlocalizedName("ifhyNickelBoundDiamondMatrix").setTextureName("nightmare:ifhyNickelBoundDiamondMatrix").setCreativeTab(CreativeTabs.tabMaterials);
        diamondBearingMaterial = new NMItem(2650).setUnlocalizedName("ifhyDiamondBearingMaterial").setTextureName("nightmare:ifhyDiamondBearingMaterial").setCreativeTab(CreativeTabs.tabMaterials);
        failedDiamondRefinement = new NMItem(2651).setUnlocalizedName("ifhyFailedDiamondRefinement").setTextureName("nightmare:ifhyFailedDiamondRefinement").setCreativeTab(CreativeTabs.tabMaterials);
        refinementWaste = new NMItem(2652).setUnlocalizedName("ifhyRefinementWaste").setTextureName("nightmare:ifhyRefinementWaste").setCreativeTab(CreativeTabs.tabMaterials);
        oxygenMask = (ItemOxygenGear) new ItemOxygenGear(2653, 0, 3, 320, 0.35F).setUnlocalizedName("ifhyOxygenMask").setTextureName("nightmare:ifhyOxygenMask");
        oxygenTank = (ItemOxygenGear) new ItemOxygenGear(2654, 1, 7, 480, 0.45F).setUnlocalizedName("ifhyOxygenTank").setTextureName("nightmare:ifhyOxygenTank");
        plantFiber = new NMPlaceAsBlockItem(2656, NMFields.BLOCK_DRYING_GRASS).setUnlocalizedName("ifhyPlantFiber").setTextureName("nightmare:ifhyPlantFiber").setCreativeTab(CreativeTabs.tabMaterials);
        driedPlantFiber = new NMItem(2657).setUnlocalizedName("ifhyDriedPlantFiber").setTextureName("nightmare:ifhyDriedPlantFiber").setCreativeTab(CreativeTabs.tabMaterials);
        skillBook = new ItemSkillBook(2655).setUnlocalizedName("ifhySkillBook").setTextureName("nightmare:ifhySkillBook");

        mackerel = createRawFish(2658, "nmMackerel");
        cod = createRawFish(2659, "nmCod");
        tuna = createRawFish(2660, "nmTuna");
        swordfish = createRawFish(2661, "nmSwordfish");
        bass = createRawFish(2662, "nmBass");
        trout = createRawFish(2663, "nmTrout");
        carp = createRawFish(2664, "nmCarp");
        goldenCarp = createRawFish(2665, "nmGoldenCarp");
        mudfish = createRawFish(2666, "nmMudfish");
        catfish = createRawFish(2667, "nmCatfish");
        swampEel = createRawFish(2668, "nmSwampEel");
        alligatorGar = createRawFish(2669, "nmAlligatorGar");
        piranha = createRawFish(2670, "nmPiranha");
        neonTetra = createRawFish(2671, "nmNeonTetra");
        jungleCatfish = createRawFish(2672, "nmJungleCatfish");
        arapaima = createRawFish(2673, "nmArapaima");
        salmon = createRawFish(2674, "nmSalmon");
        perch = createRawFish(2675, "nmPerch");
        icefish = createRawFish(2676, "nmIcefish");
        frostfish = createRawFish(2677, "nmFrostfish");
        desertMinnow = createRawFish(2678, "nmDesertMinnow");
        sandfish = createRawFish(2679, "nmSandfish");
        tilapia = createRawFish(2680, "nmTilapia");
        duneKoi = createRawFish(2681, "nmDuneKoi");
        debonedRawFish = createRawFish(2682, "nmDebonedRawFish");
        fishFlesh = (NMProgressiveItem) new NMProgressiveItem(2683, debonedRawFish.itemID).setTargetDurability(100).setSoundID("mob.chicken.hurt").setTextureName("fish_raw").setUnlocalizedName("nmFishFlesh").setCreativeTab(CreativeTabs.tabFood);
        rawFish = new Item[]{mackerel, cod, tuna, swordfish, bass, trout, carp, goldenCarp, mudfish, catfish, swampEel, alligatorGar, piranha, neonTetra, jungleCatfish, arapaima, salmon, perch, icefish, frostfish, desertMinnow, sandfish, tilapia, duneKoi, debonedRawFish};


        flintAxe = new ItemFlintAxe(2684).setTextureName("nightmare:ifhyFlintAxe").setUnlocalizedName("ifhyFlintAxe").setCreativeTab(CreativeTabs.tabTools);
        flintAxeCrafting = (NMProgressiveItem) new NMProgressiveItem(2685, flintAxe.itemID).setTargetDurability(100).setTextureName("nightmare:ifhyFlintAxeCrafting").setUnlocalizedName("ifhyFlintAxeCrafting").setCreativeTab(CreativeTabs.tabMaterials);
        crudeString = new NMItem(2686).setTextureName("nightmare:ifhyCrudeString").setUnlocalizedName("ifhyCrudeString").setCreativeTab(CreativeTabs.tabMaterials);
        crudeStringCrafting = (NMProgressiveItem) new NMProgressiveItem(2687, crudeString.itemID).setTargetDurability(100).setTextureName("nightmare:ifhyCrudeStringCrafting").setUnlocalizedName("ifhyCrudeStringCrafting").setCreativeTab(CreativeTabs.tabMaterials);
        primitiveGlue = new NMItem(2688).setTextureName("nightmare:ifhyPrimitiveGlue").setUnlocalizedName("ifhyPrimitiveGlue").setCreativeTab(CreativeTabs.tabMaterials);
        spiderSilk = new NMItem(2689).setTextureName("nightmare:ifhySpiderSilk").setUnlocalizedName("ifhySpiderSilk").setCreativeTab(CreativeTabs.tabMaterials);
        stringCrafting = (NMProgressiveItem) new NMProgressiveItem(2690, Item.silk.itemID).setTargetDurability(100).setTextureName("nightmare:ifhyStringCrafting").setUnlocalizedName("ifhyStringCrafting").setCreativeTab(CreativeTabs.tabMaterials);
        woodCupCrafting = (NMProgressiveItem) new NMProgressiveItem(2691, woodCup.itemID).setTargetDurability(100).setTextureName("nightmare:ifhyWoodCup").setUnlocalizedName("ifhyWoodCupCrafting").setCreativeTab(CreativeTabs.tabMaterials);
        unshapedWetClayBrick = (NMProgressiveItem) new NMProgressiveItem(2692, 22355 /* the ID of the crude brick block */).setTargetDurability(100).setTextureName("nightmare:ifhyUnshapedWetClayBrick").setUnlocalizedName("ifhyUnshapedWetClayBrick").setCreativeTab(CreativeTabs.tabMaterials);
        reedStem = new NMItem(2693).setTextureName("nightmare:ifhyReedStem").setUnlocalizedName("ifhyReedStem").setCreativeTab(CreativeTabs.tabMaterials);
        reedPeeling = (NMProgressiveItem) new NMProgressiveItem(2694, reedStem.itemID).setTargetDurability(100).setTextureName("nightmare:ifhyReedPeeling").setUnlocalizedName("ifhyReedPeeling").setCreativeTab(CreativeTabs.tabMaterials);
        washedPith = new NMItem(2695).setTextureName("nightmare:ifhyWashedPith").setUnlocalizedName("ifhyWashedPith").setCreativeTab(CreativeTabs.tabMaterials);
        wetFusedPlantSheet = new NMItem(2696).setTextureName("nightmare:ifhyWetFusedPlantSheet").setUnlocalizedName("ifhyWetFusedPlantSheet").setCreativeTab(CreativeTabs.tabMaterials);
        plantSheet = new NMItem(2697).setTextureName("nightmare:ifhyPlantSheet").setUnlocalizedName("ifhyPlantSheet").setCreativeTab(CreativeTabs.tabMaterials);
        washedSugarCane = new NMItem(2698).setTextureName("nightmare:ifhyWashedSugarCane").setUnlocalizedName("ifhyWashedSugarCane").setCreativeTab(CreativeTabs.tabMaterials);
        pileOfSticks = new NMItem(2699).setTextureName("nightmare:ifhyPileOfSticks").setUnlocalizedName("ifhyPileOfSticks").setCreativeTab(CreativeTabs.tabMaterials);
        boneShard = new NetherItem(2700).setTextureName("nightmare:ifhyBoneShard").setUnlocalizedName("ifhyBoneShard").setCreativeTab(CreativeTabs.tabMaterials);
        stoneKnife = (ItemKnife) new ItemKnife(2701, 300, ItemKnife.TIER_STONE, 32).setTextureName("nightmare:ifhyStoneKnife").setUnlocalizedName("ifhyStoneKnife");
        ironKnife = (ItemKnife) new ItemKnife(2702, 160, ItemKnife.TIER_IRON, 96).setTextureName("nightmare:ifhyIronKnife").setUnlocalizedName("ifhyIronKnife");
        diamondKnife = (ItemKnife) new ItemKnife(2703, 80, ItemKnife.TIER_DIAMOND, 256).setTextureName("nightmare:ifhyDiamondKnife").setUnlocalizedName("ifhyDiamondKnife");
        goldKnife = (ItemKnife) new ItemKnife(2742, 60, ItemKnife.TIER_DIAMOND, 32).setTextureName("nightmare:ifhyGoldKnife").setUnlocalizedName("ifhyGoldKnife");
        tungstenKnife = (ItemKnife) new ItemNetherKnife(2743, 80, ItemKnife.TIER_DIAMOND, 256).setTextureName("nightmare:ifhyTungstenKnife").setUnlocalizedName("ifhyTungstenKnife");

        ash = new NetherItem(2704).setTextureName("nightmare:ifhyAsh").setUnlocalizedName("ifhyAsh").setCreativeTab(CreativeTabs.tabMaterials);
        ashClump = new NetherItem(2705).setTextureName("nightmare:ifhyAshClump").setUnlocalizedName("ifhyAshClump").setCreativeTab(CreativeTabs.tabMaterials);
        soulChip = new NetherItem(2706).setTextureName("nightmare:ifhySoulChip").setUnlocalizedName("ifhySoulChip").setCreativeTab(CreativeTabs.tabMaterials);
        soulFlint = new ItemSoulFlint(2707).setTextureName("nightmare:ifhySoulFlint").setUnlocalizedName("ifhySoulFlint");
        pigHide = new NetherItem(2708).setTextureName("nightmare:ifhyPigHide").setUnlocalizedName("ifhyPigHide").setCreativeTab(CreativeTabs.tabMaterials);
        pighideString = new NetherItem(2709).setTextureName("nightmare:ifhyPighideString").setUnlocalizedName("ifhyPighideString").setCreativeTab(CreativeTabs.tabMaterials);
        pighideStringCrafting = (NMProgressiveItem)new NetherProgressiveItem(2710, pighideString.itemID).setTargetDurability(100).setTextureName("nightmare:ifhyPighideStringCrafting").setUnlocalizedName("ifhyPighideStringCrafting").setCreativeTab(CreativeTabs.tabMaterials);
        quartzDust = new NetherItem(2711).setTextureName("nightmare:ifhyQuartzDust").setUnlocalizedName("ifhyQuartzDust").setCreativeTab(CreativeTabs.tabMaterials);
        tungstenDust = new NetherItem(2712).setTextureName("nightmare:ifhyTungstenDust").setUnlocalizedName("ifhyTungstenDust").setCreativeTab(CreativeTabs.tabMaterials);
        netherrackChunk = new NetherItem(2713).setTextureName("nightmare:ifhyNetherrackChunk").setUnlocalizedName("ifhyNetherrackChunk").setCreativeTab(CreativeTabs.tabMaterials);
        netherWorkbenchPart = new NetherItem(2714).setTextureName("nightmare:ifhyNetherWorkbenchPart").setUnlocalizedName("ifhyNetherWorkbenchPart").setCreativeTab(CreativeTabs.tabMaterials);
        netherStick = new NetherItem(2715).setTextureName("nightmare:ifhyNetherStick").setUnlocalizedName("ifhyNetherStick").setCreativeTab(CreativeTabs.tabMaterials);
        netherrackPickaxe = new ItemNetherrackPickaxe(2716).setTextureName("nightmare:ifhyNetherrackPickaxe").setUnlocalizedName("ifhyNetherrackPickaxe").setCreativeTab(CreativeTabs.tabTools);
        netherFishingRod = new ItemNetherFishingRod(2717, false).setTextureName("nightmare:ifhyNetherFishingRod").setUnlocalizedName("ifhyNetherFishingRod").setCreativeTab(CreativeTabs.tabTools);
        netherFishingRodBaited = new ItemNetherFishingRod(2718, true).setTextureName("nightmare:ifhyNetherFishingRodBaited").setUnlocalizedName("ifhyNetherFishingRodBaited").setCreativeTab(null);
        lavafish = new ItemLavafish(2719).setTextureName("nightmare:ifhyLavafish").setCreativeTab(CreativeTabs.tabFood);
        tungstenPickaxe = new ItemTungstenPickaxe(2720).setTextureName("nightmare:ifhyTungstenPickaxe").setUnlocalizedName("ifhyTungstenPickaxe").setCreativeTab(CreativeTabs.tabTools);
        netherrackHammer = new ItemNetherrackHammer(2721).setTextureName("nightmare:ifhyNetherrackHammer").setUnlocalizedName("ifhyNetherrackHammer").setCreativeTab(CreativeTabs.tabTools);
        tungstenChunk = new NetherItem(2722).setTextureName("nightmare:ifhyTungstenChunk").setUnlocalizedName("ifhyTungstenChunk").setCreativeTab(CreativeTabs.tabMaterials);
        crushedTungsten = new NetherItem(2723).setTextureName("nightmare:ifhyCrushedTungsten").setUnlocalizedName("ifhyCrushedTungsten").setCreativeTab(CreativeTabs.tabMaterials);
        tungstenConcentrate = new NetherItem(2724).setTextureName("nightmare:ifhyTungstenConcentrate").setUnlocalizedName("ifhyTungstenConcentrate").setCreativeTab(CreativeTabs.tabMaterials);
        brittleTungstenCake = new NetherItem(2725).setTextureName("nightmare:ifhyBrittleTungstenCake").setUnlocalizedName("ifhyBrittleTungstenCake").setCreativeTab(CreativeTabs.tabMaterials);
        tungstenPowder = new NetherItem(2726).setTextureName("nightmare:ifhyTungstenPowder").setUnlocalizedName("ifhyTungstenPowder").setCreativeTab(CreativeTabs.tabMaterials);
        pureTungstenChunk = new NetherItem(2727).setTextureName("nightmare:ifhyPureTungstenChunk").setUnlocalizedName("ifhyPureTungstenChunk").setCreativeTab(CreativeTabs.tabMaterials);
        tungstenNugget = new NetherItem(2728).setTextureName("nightmare:ifhyTungstenNugget").setUnlocalizedName("ifhyTungstenNugget").setCreativeTab(CreativeTabs.tabMaterials);
        tungstenIngot = new NetherItem(2729).setTextureName("nightmare:ifhyTungstenIngot").setUnlocalizedName("ifhyTungstenIngot").setCreativeTab(CreativeTabs.tabMaterials);
        tungstenBucket = new ItemTungstenBucket(2730, false).setTextureName("nightmare:ifhyTungstenBucket").setUnlocalizedName("ifhyTungstenBucket");
        tungstenLavaBucket = new ItemTungstenBucket(2731, true).setTextureName("nightmare:ifhyTungstenLavaBucket").setUnlocalizedName("ifhyTungstenLavaBucket");
        obsidianPowder = new NetherItem(2732).setTextureName("nightmare:ifhyObsidianPowder").setUnlocalizedName("ifhyObsidianPowder").setCreativeTab(CreativeTabs.tabMaterials);
        obsidianPaste = new NetherItem(2733).setTextureName("nightmare:ifhyObsidianPaste").setUnlocalizedName("ifhyObsidianPaste").setCreativeTab(CreativeTabs.tabMaterials);
        obsidianBrick = new NetherItem(2734).setTextureName("nightmare:ifhyObsidianBrick").setUnlocalizedName("ifhyObsidianBrick").setCreativeTab(CreativeTabs.tabMaterials);
        rettedHemp = new NMItem(2735).setTextureName("nightmare:ifhyRettedHemp").setUnlocalizedName("ifhyRettedHemp").setCreativeTab(CreativeTabs.tabMaterials);
        washedHemp = new NMItem(2736).setTextureName("nightmare:ifhyWashedHemp").setUnlocalizedName("ifhyWashedHemp").setCreativeTab(CreativeTabs.tabMaterials);
        driedHemp = new NMItem(2737).setTextureName("nightmare:ifhyDriedHemp").setUnlocalizedName("ifhyDriedHemp").setCreativeTab(CreativeTabs.tabMaterials);
        washedScouredLeather = new NMItem(2738).setTextureName("nightmare:ifhyWashedScouredLeather").setUnlocalizedName("ifhyWashedScouredLeather").setCreativeTab(CreativeTabs.tabMaterials);
        workedScouredLeather = new NMItem(2739).setTextureName("nightmare:ifhyWorkedScouredLeather").setUnlocalizedName("ifhyWorkedScouredLeather").setCreativeTab(CreativeTabs.tabMaterials);
        flintChip = new NMItem(2740).setTextureName("nightmare:ifhyFlintChip").setUnlocalizedName("ifhyFlintChip").setCreativeTab(CreativeTabs.tabMaterials);
        tungstenShovel = new NMShovelItem(2741, EnumToolMaterial.IRON, 500, 0.7f).setTextureName("nightmare:ifhyTungstenShovel").setUnlocalizedName("ifhyTungstenShovel").setCreativeTab(CreativeTabs.tabTools);
        ironScythe = new ItemScythe(2748, EnumToolMaterial.IRON, 4.0F).setTextureName("nightmare:ifhyIronScythe").setUnlocalizedName("ifhyIronScythe").setCreativeTab(CreativeTabs.tabCombat);
        diamondScythe = new ItemScythe(2749, EnumToolMaterial.EMERALD, 5.0F).setTextureName("nightmare:ifhyDiamondScythe").setUnlocalizedName("ifhyDiamondScythe").setCreativeTab(CreativeTabs.tabCombat);
        tungstenScythe = new ItemTungstenScythe(2750).setTextureName("nightmare:ifhyTungstenScythe").setUnlocalizedName("ifhyTungstenScythe").setCreativeTab(CreativeTabs.tabCombat);
        aquamarine = new NMItem(2751).setTextureName("nightmare:ifhyAquamarine").setUnlocalizedName("ifhyAquamarine").setCreativeTab(CreativeTabs.tabMaterials);
        highSpeedMinecart = new ItemHighSpeedMinecart(2752, 0).setTextureName("nightmare:ifhyHighSpeedMinecart").setUnlocalizedName("ifhyHighSpeedMinecart");
        highSpeedChestMinecart = new ItemHighSpeedMinecart(2753, 1).setTextureName("nightmare:ifhyHighSpeedChestMinecart").setUnlocalizedName("ifhyHighSpeedChestMinecart");
        highSpeedFurnaceMinecart = new ItemHighSpeedMinecart(3257, 2).setTextureName("minecart_furnace").setUnlocalizedName("ifhyHighSpeedFurnaceMinecart");
        moistureFertilizer = (ItemChunkFertilizer)new ItemChunkFertilizer(2744, ChunkAttribute.MOISTURE)
                .setTextureName("nightmare:ifhyMoistureFertilizer")
                .setUnlocalizedName("ifhyMoistureFertilizer")
                .setCreativeTab(CreativeTabs.tabMaterials);
        potassiumFertilizer = (ItemChunkFertilizer)new ItemChunkFertilizer(2745, ChunkAttribute.POTASSIUM)
                .setTextureName("nightmare:ifhyPotassiumFertilizer")
                .setUnlocalizedName("ifhyPotassiumFertilizer")
                .setCreativeTab(CreativeTabs.tabMaterials);
        acidityFertilizer = (ItemChunkFertilizer)new ItemChunkFertilizer(2746, ChunkAttribute.ACIDITY)
                .setTextureName("nightmare:ifhyAcidityFertilizer")
                .setUnlocalizedName("ifhyAcidityFertilizer")
                .setCreativeTab(CreativeTabs.tabMaterials);
        porosityFertilizer = (ItemChunkFertilizer)new ItemChunkFertilizer(2747, ChunkAttribute.POROSITY)
                .setTextureName("nightmare:ifhyPorosityFertilizer")
                .setUnlocalizedName("ifhyPorosityFertilizer")
                .setCreativeTab(CreativeTabs.tabMaterials);

        redstoneCrystal = new NMItem(2754).setTextureName("nightmare:ifhyRedstoneCrystal").setUnlocalizedName("ifhyRedstoneCrystal").setCreativeTab(CreativeTabs.tabMaterials);
        refinedRedstone = new NMItem(2755).setTextureName("nightmare:ifhyRefinedRedstone").setUnlocalizedName("ifhyRefinedRedstone").setCreativeTab(CreativeTabs.tabMaterials);
        azureSalt = new NetherItem(2756).setTextureName("nightmare:ifhyAzureSalt").setUnlocalizedName("ifhyAzureSalt").setCreativeTab(CreativeTabs.tabMaterials);
        azureSlag = new NetherItem(2757).setTextureName("nightmare:ifhyAzureSlag").setUnlocalizedName("ifhyAzureSlag").setCreativeTab(CreativeTabs.tabMaterials);
        brittleAzureCake = new NetherItem(2758).setTextureName("nightmare:ifhyBrittleAzureCake").setUnlocalizedName("ifhyBrittleAzureCake").setCreativeTab(CreativeTabs.tabMaterials);
        rawAzureStone = new NMItem(2759).setTextureName("nightmare:ifhyRawAzureStone").setUnlocalizedName("ifhyRawAzureStone").setCreativeTab(CreativeTabs.tabMaterials);
        crushedAzureStone = new NMItem(2760).setTextureName("nightmare:ifhyCrushedAzureStone").setUnlocalizedName("ifhyCrushedAzureStone").setCreativeTab(CreativeTabs.tabMaterials);
        washedAzureSediment = new NMItem(2761).setTextureName("nightmare:ifhyWashedAzureSediment").setUnlocalizedName("ifhyWashedAzureSediment").setCreativeTab(CreativeTabs.tabMaterials);
        lapisPrecipitate = new NMItem(2762).setTextureName("nightmare:ifhyLapisPrecipitate").setUnlocalizedName("ifhyLapisPrecipitate").setCreativeTab(CreativeTabs.tabMaterials);
        hydraulicLens = new NMItem(2763).setTextureName("nightmare:ifhyHydraulicLens").setUnlocalizedName("ifhyHydraulicLens").setCreativeTab(CreativeTabs.tabMaterials);
        fluidGauge = new NMItem(2764).setTextureName("nightmare:ifhyFluidGauge").setUnlocalizedName("ifhyFluidGauge").setCreativeTab(CreativeTabs.tabMaterials);
        searingSilverScale = new NetherItem(2765).setTextureName("nightmare:ifhySearingSilverScale").setUnlocalizedName("ifhySearingSilverScale").setCreativeTab(CreativeTabs.tabMaterials);
        denseNetherrackCore = new NetherItem(2766).setTextureName("nightmare:ifhyDenseNetherrackCore").setUnlocalizedName("ifhyDenseNetherrackCore").setCreativeTab(CreativeTabs.tabMaterials);
        deadzoneShard = new NetherItem(2767).setTextureName("nightmare:ifhyDeadzoneShard").setUnlocalizedName("ifhyDeadzoneShard").setCreativeTab(CreativeTabs.tabMaterials);
//        netherTradePlaceholder = new NetherItem(2768).setTextureName("nightmare:ifhyNetherTradePlaceholder").setUnlocalizedName("ifhyNetherTradePlaceholder").setCreativeTab(CreativeTabs.tabMaterials);
        invocationFragment = new NetherItem(2769).setTextureName("nightmare:ifhyInvocationFragment").setUnlocalizedName("ifhyInvocationFragment").setCreativeTab(CreativeTabs.tabMaterials);
        invocationSeal = new NetherItem(2770).setTextureName("nightmare:ifhyInvocationSeal").setUnlocalizedName("ifhyInvocationSeal").setCreativeTab(CreativeTabs.tabMaterials);
        endAccordFragment = new NetherItem(2771).setTextureName("nightmare:ifhyEndAccordFragment").setUnlocalizedName("ifhyEndAccordFragment").setCreativeTab(CreativeTabs.tabMaterials);
        endAccord = new NetherItem(2772).setTextureName("nightmare:ifhyEndAccord").setUnlocalizedName("ifhyEndAccord").setCreativeTab(CreativeTabs.tabMaterials);
        debugVillagerLevel = new ItemVillagerDebugTool(2773, ItemVillagerDebugTool.Action.INCREASE_LEVEL)
                .setTextureName("nightmare:ifhyDebugVillagerLevel").setUnlocalizedName("ifhyDebugVillagerLevel");
        debugVillagerProgress = new ItemVillagerDebugTool(2774, ItemVillagerDebugTool.Action.INCREASE_PROGRESS)
                .setTextureName("nightmare:ifhyDebugVillagerProgress").setUnlocalizedName("ifhyDebugVillagerProgress");
        debugVillagerReroll = new ItemVillagerDebugTool(2775, ItemVillagerDebugTool.Action.REROLL_TRADES)
                .setTextureName("nightmare:ifhyDebugVillagerReroll").setUnlocalizedName("ifhyDebugVillagerReroll");
        // Item adds 256 to constructor IDs. Keep these custom IDs away from the densely
        // occupied legacy block/item range; registration is verified before recipes are added.
        librarianEnderTreatise = new NMItem(3200)
                .setMaxStackSize(1)
                .setTextureName("nightmare:ifhyLibrarianEnderTreatise")
                .setUnlocalizedName("ifhyLibrarianEnderTreatise")
                .setCreativeTab(CreativeTabs.tabMaterials);
        automationEssence = new NMItem(3201)
                .setTextureName("nightmare:ifhyAutomationEssence")
                .setUnlocalizedName("ifhyAutomationEssence")
                .setCreativeTab(CreativeTabs.tabMaterials);
        husbandryEssence = new NMItem(3202)
                .setTextureName("nightmare:ifhyHusbandryEssence")
                .setUnlocalizedName("ifhyAgrarianEssence")
                .setCreativeTab(CreativeTabs.tabMaterials);
        infernalEssence = new NetherItem(3203)
                .setTextureName("nightmare:ifhyInfernoEssence")
                .setUnlocalizedName("ifhyInfernalEssence")
                .setCreativeTab(CreativeTabs.tabMaterials);
        artisanEssence = new NMItem(3204)
                .setTextureName("nightmare:ifhyArtificeEssence")
                .setUnlocalizedName("ifhyArtisanEssence")
                .setCreativeTab(CreativeTabs.tabMaterials);
        stoneStick = new NMItem(3205)
                .setTextureName("nightmare:ifhyStoneStick")
                .setUnlocalizedName("ifhyStoneStick")
                .setCreativeTab(CreativeTabs.tabMaterials);
        ironStick = new NMItem(3206)
                .setTextureName("nightmare:ifhyIronStick")
                .setUnlocalizedName("ifhyIronStick")
                .setCreativeTab(CreativeTabs.tabMaterials);
        diamondStick = new NMItem(3207)
                .setTextureName("nightmare:ifhyDiamondStick")
                .setUnlocalizedName("ifhyDiamondStick")
                .setCreativeTab(CreativeTabs.tabMaterials);
        glueSlurry = new NMItem(3208)
                .setTextureName("nightmare:ifhyGlueSlurry")
                .setUnlocalizedName("ifhyGlueSlurry")
                .setCreativeTab(CreativeTabs.tabMaterials);
        pressedGlueCake = new NMItem(3209)
                .setTextureName("nightmare:ifhyPressedGlueCake")
                .setUnlocalizedName("ifhyPressedGlueCake")
                .setCreativeTab(CreativeTabs.tabMaterials);
        roughStoneBrick = new NMItem(3210)
                .setTextureName("nightmare:ifhyRoughStoneBrick")
                .setUnlocalizedName("ifhyRoughStoneBrick")
                .setCreativeTab(CreativeTabs.tabMaterials);
        hammeredStoneBrick = new NMItem(3211)
                .setTextureName("nightmare:ifhyHammeredStoneBrick")
                .setUnlocalizedName("ifhyHammeredStoneBrick")
                .setCreativeTab(CreativeTabs.tabMaterials);
        mortaredStoneBrick = new NMItem(3212)
                .setTextureName("nightmare:ifhyMortaredStoneBrick")
                .setUnlocalizedName("ifhyMortaredStoneBrick")
                .setCreativeTab(CreativeTabs.tabMaterials);
        ironBrick = new NMItem(3213)
                .setTextureName("nightmare:ifhyIronBrick")
                .setUnlocalizedName("ifhyIronBrick")
                .setCreativeTab(CreativeTabs.tabMaterials);
        diamondBrick = new NMItem(3214)
                .setTextureName("nightmare:ifhyDiamondBrick")
                .setUnlocalizedName("ifhyDiamondBrick")
                .setCreativeTab(CreativeTabs.tabMaterials);
        crystalPowder = new NMItem(3215)
                .setTextureName("nightmare:ifhyCrystalPowder")
                .setUnlocalizedName("ifhyCrystalPowder")
                .setCreativeTab(CreativeTabs.tabMaterials);
        glassBatch = new NMItem(3216)
                .setTextureName("nightmare:ifhyGlassBatch")
                .setUnlocalizedName("ifhyGlassBatch")
                .setCreativeTab(CreativeTabs.tabMaterials);
        dyeBlend = new NMItem(3217)
                .setTextureName("nightmare:ifhyDyeBlend")
                .setUnlocalizedName("ifhyDyeBlend")
                .setCreativeTab(CreativeTabs.tabMaterials);
        potassiumCrystal = new NMItem(3218).setTextureName("nightmare:ifhyPotassiumCrystal")
                .setUnlocalizedName("ifhyPotassiumCrystal").setCreativeTab(CreativeTabs.tabMaterials);
        nitrogenCrystal = new NMItem(3219).setTextureName("nightmare:ifhyNitrogenCrystal")
                .setUnlocalizedName("ifhyNitrogenCrystal").setCreativeTab(CreativeTabs.tabMaterials);
        acidCrystal = new NMItem(3220).setTextureName("nightmare:ifhyAcidCrystal")
                .setUnlocalizedName("ifhyAcidCrystal").setCreativeTab(CreativeTabs.tabMaterials);
        porosityAggregate = new NMItem(3221).setTextureName("nightmare:ifhyPorosityAggregate")
                .setUnlocalizedName("ifhyPorosityAggregate").setCreativeTab(CreativeTabs.tabMaterials);
        soilSample = new ItemSoilSample(3222).setTextureName("nightmare:ifhySoilSample")
                .setUnlocalizedName("ifhySoilSample").setCreativeTab(CreativeTabs.tabMaterials);
        brokenHoeFragment = new ItemQuestFragment(3223, 8)
                .setTextureName("nightmare:ifhyBrokenHoeFragment").setUnlocalizedName("ifhyBrokenHoeFragment")
                .setCreativeTab(CreativeTabs.tabTools);
        farmersFavoriteHoe = new ItemHoe(3224, EnumToolMaterial.IRON).setTextureName("nightmare:ifhyFarmersFavoriteHoe")
                .setUnlocalizedName("ifhyFarmersFavoriteHoe").setCreativeTab(CreativeTabs.tabTools);
        unbakedChocolateCake = new NMItem(3225).setMaxStackSize(1).setTextureName("nightmare:ifhyUnbakedChocolateCake")
                .setUnlocalizedName("ifhyUnbakedChocolateCake").setCreativeTab(CreativeTabs.tabFood);
        chocolateCake = new NMItem(3226).setMaxStackSize(1).setTextureName("nightmare:ifhyChocolateCake")
                .setUnlocalizedName("ifhyChocolateCake").setCreativeTab(CreativeTabs.tabFood);
        burnedChocolateCake = new NMItem(3227).setMaxStackSize(1).setTextureName("nightmare:ifhyBurnedChocolateCake")
                .setUnlocalizedName("ifhyBurnedChocolateCake").setCreativeTab(CreativeTabs.tabFood);
        brokenPickaxeFragment = new ItemQuestFragment(3228, 4)
                .setTextureName("nightmare:ifhyBrokenPickaxeFragment").setUnlocalizedName("ifhyBrokenPickaxeFragment")
                .setCreativeTab(CreativeTabs.tabTools);
        blacksmithFavoritePickaxe = new ItemQuestPickaxe(3229).setTextureName("nightmare:ifhyBlacksmithFavoritePickaxe")
                .setUnlocalizedName("ifhyBlacksmithFavoritePickaxe").setCreativeTab(CreativeTabs.tabTools);
        mechanicalWrench = new ItemMechanicalWrench(3230).setTextureName("nightmare:ifhyMechanicalWrench")
                .setUnlocalizedName("ifhyMechanicalWrench").setCreativeTab(CreativeTabs.tabTools);

        rawMercuryCrystal = material(3231, "ifhyRawMercuryCrystal");
        mercuryPowder = material(3232, "ifhyMercuryPowder");
        washedMercuryConcentrate = material(3233, "ifhyWashedMercuryConcentrate");
        mercuryAmalgam = material(3234, "ifhyMercuryAmalgam");
        enderCrystal = material(3235, "ifhyEnderCrystal");
        enderDust = material(3236, "ifhyEnderDust");
        enderShell = material(3237, "ifhyEnderShell");
        enderShellPowder = material(3238, "ifhyEnderShellPowder");
        paleRoot = material(3239, "ifhyPaleRoot");
        paleRootSeeds = new api.item.items.SeedItem(3240, 2443)
                .setTextureName("nightmare:ifhyPaleRootSeeds").setUnlocalizedName("ifhyPaleRootSeeds");
        paleRootPulp = material(3241, "ifhyPaleRootPulp");
        paleRootResin = material(3242, "ifhyPaleRootResin");
        firedCrucibleLiner = material(3243, "ifhyFiredCrucibleLiner").setMaxStackSize(1);
        phaseSteelCharge = material(3244, "ifhyPhaseSteelCharge");
        phaseSteelIngot = material(3245, "ifhyPhaseSteelIngot");
        phaseSteelPlate = material(3246, "ifhyPhaseSteelPlate");
        enderMechanism = material(3247, "ifhyEnderMechanism");

        enderSword = (ItemEnderSword)new ItemEnderSword(3248)
                .setTextureName("nightmare:ifhyEnderSword").setUnlocalizedName("ifhyEnderSword");
        enderPickaxe = (ItemEnderPickaxe)new ItemEnderPickaxe(3249)
                .setTextureName("nightmare:ifhyEnderPickaxe").setUnlocalizedName("ifhyEnderPickaxe");
        enderAxe = (ItemEnderAxe)new ItemEnderAxe(3250)
                .setTextureName("nightmare:ifhyEnderAxe").setUnlocalizedName("ifhyEnderAxe");
        enderShovel = (ItemEnderShovel)new ItemEnderShovel(3251)
                .setTextureName("nightmare:ifhyEnderShovel").setUnlocalizedName("ifhyEnderShovel");
        enderHoe = (ItemEnderHoe)new ItemEnderHoe(3252)
                .setTextureName("nightmare:ifhyEnderHoe").setUnlocalizedName("ifhyEnderHoe");
        enderHelmet = (ItemEnderArmor)new ItemEnderArmor(3253, 0, 2)
                .setTextureName("nightmare:ifhyEnderHelmet").setUnlocalizedName("ifhyEnderHelmet");
        enderChestplate = (ItemEnderArmor)new ItemEnderArmor(3254, 1, 4)
                .setTextureName("nightmare:ifhyEnderChestplate").setUnlocalizedName("ifhyEnderChestplate");
        enderLeggings = (ItemEnderArmor)new ItemEnderArmor(3255, 2, 3)
                .setTextureName("nightmare:ifhyEnderLeggings").setUnlocalizedName("ifhyEnderLeggings");
        enderBoots = (ItemEnderArmor)new ItemEnderArmor(3256, 3, 1)
                .setTextureName("nightmare:ifhyEnderBoots").setUnlocalizedName("ifhyEnderBoots");
        snowPile = new NMItem(3258).setTextureName("nightmare:ifhySnowPile").setUnlocalizedName("ifhySnowPile")
                .setCreativeTab(CreativeTabs.tabMaterials);

        carbonRichIronMix = material(3259, "ifhyCarbonRichIronMix");
        carburizedIronBloom = new NMPlaceAsBlockItem(3260, NMFields.BLOCK_CARBURIZED_IRON_BLOOM)
                .setTextureName("nightmare:ifhyCarburizedIronBloom").setUnlocalizedName("ifhyCarburizedIronBloom")
                .setCreativeTab(CreativeTabs.tabMaterials);
        carbonIronNugget = material(3261, "ifhyCarbonIronNugget");
        carbonIronIngot = material(3262, "ifhyCarbonIronIngot");
        carbonIronPlate = material(3263, "ifhyCarbonIronPlate");
        lithiumTreatedIronBlank = material(3264, "ifhyLithiumTreatedIronBlank");
        reinforcedIronIngot = material(3265, "ifhyReinforcedIronIngot");
        reinforcedIronPlate = material(3266, "ifhyReinforcedIronPlate");
        wetGasket = material(3267, "ifhyWetGasket");
        waxedGasket = material(3268, "ifhyWaxedGasket");
        refractoryPaste = material(3269, "ifhyRefractoryPaste");
        wetRefractoryCloth = material(3270, "ifhyWetRefractoryCloth");
        refractoryCloth = material(3271, "ifhyRefractoryCloth");
        pressureRegulator = material(3272, "ifhyPressureRegulator");
        thermalLaminate = material(3273, "ifhyThermalLaminate");

        carbonIronHelmet = carbonIronArmor(3274, 0, 2, 5, 480, 5, 0.0D,
                carbonIronNugget, "ifhyCarbonIronArmor", "item.ifhyCarbonIronArmor.bonus", "ifhyCarbonIronHelmet");
        carbonIronChestplate = carbonIronArmor(3275, 1, 7, 9, 480, 5, 0.0D,
                carbonIronNugget, "ifhyCarbonIronArmor", "item.ifhyCarbonIronArmor.bonus", "ifhyCarbonIronChestplate");
        carbonIronLeggings = carbonIronArmor(3276, 2, 5, 7, 480, 5, 0.0D,
                carbonIronNugget, "ifhyCarbonIronArmor", "item.ifhyCarbonIronArmor.bonus", "ifhyCarbonIronLeggings");
        carbonIronBoots = carbonIronArmor(3277, 3, 2, 4, 480, 5, 0.0D,
                carbonIronNugget, "ifhyCarbonIronArmor", "item.ifhyCarbonIronArmor.bonus", "ifhyCarbonIronBoots");

        reinforcedIronHelmet = alloyArmor(3278, 0, 2, 4, 720, 8, 0.025D,
                reinforcedIronIngot, "ifhyReinforcedIronArmor", "item.ifhyReinforcedIronArmor.bonus", "ifhyReinforcedIronHelmet");
        reinforcedIronChestplate = alloyArmor(3279, 1, 6, 7, 720, 8, 0.025D,
                reinforcedIronIngot, "ifhyReinforcedIronArmor", "item.ifhyReinforcedIronArmor.bonus", "ifhyReinforcedIronChestplate");
        reinforcedIronLeggings = alloyArmor(3280, 2, 5, 5, 720, 8, 0.025D,
                reinforcedIronIngot, "ifhyReinforcedIronArmor", "item.ifhyReinforcedIronArmor.bonus", "ifhyReinforcedIronLeggings");
        reinforcedIronBoots = alloyArmor(3281, 3, 2, 3, 720, 8, 0.025D,
                reinforcedIronIngot, "ifhyReinforcedIronArmor", "item.ifhyReinforcedIronArmor.bonus", "ifhyReinforcedIronBoots");

        nickelWorkLeggings = alloyArmor(3282, 2, 3, 4, 560, 10, 0.0D,
                nickelPlate, "oxygenGear","ifhyNickelWorkLeggings");
        nickelWorkBoots = alloyArmor(3283, 3, 1, 2, 400, 10, 0.0D,
                nickelPlate, "oxygenGear", "ifhyNickelWorkBoots");

        heatResistantHelmet = heatArmor(3284, 0, 3, 4, EnumArmorMaterial.DIAMOND.getDurability(0),
                "ifhyHeatResistantHelmet");
        heatResistantChestplate = heatArmor(3285, 1, 8, 7, EnumArmorMaterial.DIAMOND.getDurability(1),
                "ifhyHeatResistantChestplate");
        heatResistantLeggings = heatArmor(3286, 2, 6, 6, EnumArmorMaterial.DIAMOND.getDurability(2),
                "ifhyHeatResistantLeggings");
        heatResistantBoots = heatArmor(3287, 3, 3, 3, EnumArmorMaterial.DIAMOND.getDurability(3),
                "ifhyHeatResistantBoots");

        tungstenPlate = new NetherItem(3290).setTextureName("nightmare:ifhyTungstenPlate")
                .setUnlocalizedName("ifhyTungstenPlate").setCreativeTab(CreativeTabs.tabMaterials);

        thermalChestLining = material(3291, "ifhyThermalChestLining");
        tankReinforcementCradle = netherMaterial(3292, "ifhyTankReinforcementCradle");
        moltenQuartzCompound = netherMaterial(3293, "ifhyMoltenQuartzCompound");
        quartzglassIngot = netherMaterial(3294, "ifhyQuartzglassIngot");
        quartzglassPlate = netherMaterial(3295, "ifhyQuartzglassPlate");
        divingMask = (ItemDivingGear)new ItemDivingGear(3288, 0, 2, 3, 600, 8, 0.0D,
                quartzglassPlate.itemID, 0.50F, 0, "ifhyDivingGear", "item.ifhyDivingGear.bonus")
                .setTextureName("nightmare:ifhyDivingMask").setUnlocalizedName("ifhyDivingMask");
        divingTank = (ItemDivingGear)new ItemDivingGear(3289, 1, 6, 8, 900, 8, 0.0D,
                tungstenPlate.itemID, 0.45F, 20 * 60 * 4, "ifhyDivingGear", "item.ifhyDivingGear.bonus")
                .setTextureName("nightmare:ifhyDivingTank").setUnlocalizedName("ifhyDivingTank");
        crackedEmerald = material(3296, "ifhyCrackedEmerald");
        emeraldGrit = material(3297, "ifhyEmeraldGrit");
        washedEmeraldPowder = material(3298, "ifhyWashedEmeraldPowder");
        verdantIngot = material(3299, "ifhyVerdantIngot");
        verdantPlate = material(3300, "ifhyVerdantPlate");
        blackglassCharge = netherMaterial(3301, "ifhyBlackglassCharge");
        blackglassIngot = netherMaterial(3302, "ifhyBlackglassIngot");
        blackglassPlate = netherMaterial(3303, "ifhyBlackglassPlate");
        saturatedCoresteelCharge = new NMNetherPlaceAsBlockItem(3304, NMFields.BLOCK_SATURATED_CORESTEEL_CHARGE)
                .setTextureName("nightmare:ifhySaturatedCoresteelCharge")
                .setUnlocalizedName("ifhySaturatedCoresteelCharge").setCreativeTab(CreativeTabs.tabMaterials);
        cooledCoresteelCharge = netherMaterial(3305, "ifhyCooledCoresteelCharge");
        coresteelIngot = netherMaterial(3306, "ifhyCoresteelIngot");
        coresteelPlate = netherMaterial(3307, "ifhyCoresteelPlate");
        unstableDeadzoneCharge = netherMaterial(3308, "ifhyUnstableDeadzoneCharge");
        deadzoneAlloyIngot = netherMaterial(3309, "ifhyDeadzoneAlloyIngot");
        deadzoneAlloyPlate = netherMaterial(3310, "ifhyDeadzoneAlloyPlate");
        solarBaffle = netherMaterial(3311, "ifhySolarBaffle");

        tungstenHelmet = netherArmor(3312, 0, 3, 7, 1100, 6, 0.055D, tungstenIngot,
                "ifhyTungstenArmor", "item.ifhyTungstenArmor.bonus", "ifhyTungstenHelmet");
        tungstenChestplate = netherArmor(3313, 1, 8, 12, 1100, 6, 0.055D, tungstenIngot,
                "ifhyTungstenArmor", "item.ifhyTungstenArmor.bonus", "ifhyTungstenChestplate");
        tungstenLeggings = netherArmor(3314, 2, 6, 10, 1100, 6, 0.055D, tungstenIngot,
                "ifhyTungstenArmor", "item.ifhyTungstenArmor.bonus", "ifhyTungstenLeggings");
        tungstenBoots = netherArmor(3315, 3, 3, 6, 1100, 6, 0.055D, tungstenIngot,
                "ifhyTungstenArmor", "item.ifhyTungstenArmor.bonus", "ifhyTungstenBoots");

        coresteelHelmet = coresteelArmor(3316, 0, 3, 6, 1300, 4800, "ifhyCoresteelHelmet");
        coresteelChestplate = coresteelArmor(3317, 1, 8, 10, 1300, 9600, "ifhyCoresteelChestplate");
        coresteelLeggings = coresteelArmor(3318, 2, 6, 8, 1300, 7200, "ifhyCoresteelLeggings");
        coresteelBoots = coresteelArmor(3319, 3, 3, 5, 1300, 4800, "ifhyCoresteelBoots");

        deadzoneHelmet = netherArmor(3320, 0, 3, 4, 1500, 8, 0.05D, deadzoneAlloyIngot,
                "ifhyDeadzoneArmor", "item.ifhyDeadzoneArmor.bonus", "ifhyDeadzoneHelmet");
        deadzoneChestplate = netherArmor(3321, 1, 9, 7, 1500, 8, 0.05D, deadzoneAlloyIngot,
                "ifhyDeadzoneArmor", "item.ifhyDeadzoneArmor.bonus", "ifhyDeadzoneChestplate");
        deadzoneLeggings = netherArmor(3322, 2, 7, 6, 1500, 8, 0.05D, deadzoneAlloyIngot,
                "ifhyDeadzoneArmor", "item.ifhyDeadzoneArmor.bonus", "ifhyDeadzoneLeggings");
        deadzoneBoots = netherArmor(3323, 3, 3, 3, 1500, 8, 0.05D, deadzoneAlloyIngot,
                "ifhyDeadzoneArmor", "item.ifhyDeadzoneArmor.bonus", "ifhyDeadzoneBoots");

        sunHelmet = sunArmorPiece(3324, 0, 3, 3, "ifhySunHelmet");
        sunChestplate = sunArmorPiece(3325, 1, 8, 6, "ifhySunChestplate");
        sunLeggings = sunArmorPiece(3326, 2, 6, 5, "ifhySunLeggings");
        sunBoots = sunArmorPiece(3327, 3, 3, 3, "ifhySunBoots");
        sunVisor = (ItemSunDivingGear)new ItemSunDivingGear(3328, 0, 3, 3, 1400, 10, 0.05D,
                deadzoneAlloyIngot.itemID, 0.50F, 0, "ifhySunArmor", "item.ifhySunArmor.bonus", 0.15F)
                .setTextureName("nightmare:ifhySunVisor").setUnlocalizedName("ifhySunVisor");
        sunReservoir = (ItemSunDivingGear)new ItemSunDivingGear(3329, 1, 8, 6, 1400, 10, 0.05D,
                coresteelIngot.itemID, 0.45F, 20 * 60 * 6, "ifhySunArmor", "item.ifhySunArmor.bonus", 0.15F)
                .setTextureName("nightmare:ifhySunReservoir").setUnlocalizedName("ifhySunReservoir");

        signalConductiveCharge = material(3330, "ifhySignalConductiveCharge");
        signalAlloyIngot = material(3331, "ifhySignalAlloyIngot");
        signalAlloyPlate = material(3332, "ifhySignalAlloyPlate");
        azureSlip = material(3333, "ifhyAzureSlip");
        azureCeramicIngot = material(3334, "ifhyAzureCeramicIngot");
        azureCeramicPlate = material(3335, "ifhyAzureCeramicPlate");
        crystalLatticeCharge = material(3336, "ifhyCrystalLatticeCharge");
        setCrystalLattice = material(3337, "ifhySetCrystalLattice");
        prismaticIngot = material(3338, "ifhyPrismaticIngot");
        prismaticPlate = material(3339, "ifhyPrismaticPlate");
        sealedQuicksilverIngot = material(3340, "ifhySealedQuicksilverIngot");
        sealedQuicksilverPlate = material(3341, "ifhySealedQuicksilverPlate");
        endstonePowder = material(3342, "ifhyEndstonePowder");
        endstoneClay = material(3343, "ifhyEndstoneClay");
        endstoneIngot = material(3344, "ifhyEndstoneIngot");
        endstonePlate = material(3345, "ifhyEndstonePlate");
        darkIngot = netherMaterial(3346, "ifhyDarkIngot");

        signalHelmet = chargedArmor(3347, 0, 2, 2, 430, signalAlloyIngot, "ifhySignalArmor", "item.ifhySignalArmor.bonus", "ifhySignalHelmet");
        signalChestplate = chargedArmor(3348, 1, 6, 4, 430, signalAlloyIngot, "ifhySignalArmor", "item.ifhySignalArmor.bonus", "ifhySignalChestplate");
        signalLeggings = chargedArmor(3349, 2, 5, 3, 430, signalAlloyIngot, "ifhySignalArmor", "item.ifhySignalArmor.bonus", "ifhySignalLeggings");
        signalBoots = chargedArmor(3350, 3, 2, 1, 430, signalAlloyIngot, "ifhySignalArmor", "item.ifhySignalArmor.bonus", "ifhySignalBoots");
        azureHelmet = alloyArmor(3351, 0, 2, 2, 360, 22, 0.0D, azureCeramicIngot, "ifhyAzureArmor", "item.ifhyAzureArmor.bonus", "ifhyAzureHelmet");
        azureChestplate = alloyArmor(3352, 1, 5, 4, 360, 22, 0.0D, azureCeramicIngot, "ifhyAzureArmor", "item.ifhyAzureArmor.bonus", "ifhyAzureChestplate");
        azureLeggings = alloyArmor(3353, 2, 4, 3, 360, 22, 0.0D, azureCeramicIngot, "ifhyAzureArmor", "item.ifhyAzureArmor.bonus", "ifhyAzureLeggings");
        azureBoots = alloyArmor(3354, 3, 2, 1, 360, 22, 0.0D, azureCeramicIngot, "ifhyAzureArmor", "item.ifhyAzureArmor.bonus", "ifhyAzureBoots");
        prismaticHelmet = alloyArmor(3355, 0, 2, 1, 440, 16, 0.0D, prismaticIngot, "ifhyPrismaticArmor", "item.ifhyPrismaticArmor.bonus", "ifhyPrismaticHelmet");
        prismaticChestplate = alloyArmor(3356, 1, 5, 3, 440, 16, 0.0D, prismaticIngot, "ifhyPrismaticArmor", "item.ifhyPrismaticArmor.bonus", "ifhyPrismaticChestplate");
        prismaticLeggings = alloyArmor(3357, 2, 4, 2, 440, 16, 0.0D, prismaticIngot, "ifhyPrismaticArmor", "item.ifhyPrismaticArmor.bonus", "ifhyPrismaticLeggings");
        prismaticBoots = alloyArmor(3358, 3, 2, 1, 440, 16, 0.0D, prismaticIngot, "ifhyPrismaticArmor", "item.ifhyPrismaticArmor.bonus", "ifhyPrismaticBoots");
        refinedPrismaHelmet = alloyArmor(3359, 0, 3, 3, 760, 24, 0.02D, prismaticIngot, "ifhyRefinedPrismaArmor", "item.ifhyRefinedPrismaArmor.bonus", "ifhyRefinedPrismaHelmet");
        refinedPrismaChestplate = alloyArmor(3360, 1, 8, 5, 760, 24, 0.02D, prismaticIngot, "ifhyRefinedPrismaArmor", "item.ifhyRefinedPrismaArmor.bonus", "ifhyRefinedPrismaChestplate");
        refinedPrismaLeggings = alloyArmor(3361, 2, 6, 4, 760, 24, 0.02D, prismaticIngot, "ifhyRefinedPrismaArmor", "item.ifhyRefinedPrismaArmor.bonus", "ifhyRefinedPrismaLeggings");
        refinedPrismaBoots = alloyArmor(3362, 3, 3, 2, 760, 24, 0.02D, prismaticIngot, "ifhyRefinedPrismaArmor", "item.ifhyRefinedPrismaArmor.bonus", "ifhyRefinedPrismaBoots");
        verdantHelmet = alloyArmor(3363, 0, 2, 1, 300, 30, 0.0D, verdantIngot, "ifhyVerdantArmor", "item.ifhyVerdantArmor.bonus", "ifhyVerdantHelmet");
        verdantChestplate = alloyArmor(3364, 1, 5, 3, 300, 30, 0.0D, verdantIngot, "ifhyVerdantArmor", "item.ifhyVerdantArmor.bonus", "ifhyVerdantChestplate");
        verdantLeggings = alloyArmor(3365, 2, 3, 2, 300, 30, 0.0D, verdantIngot, "ifhyVerdantArmor", "item.ifhyVerdantArmor.bonus", "ifhyVerdantLeggings");
        verdantBoots = alloyArmor(3366, 3, 1, 1, 300, 30, 0.0D, verdantIngot, "ifhyVerdantArmor", "item.ifhyVerdantArmor.bonus", "ifhyVerdantBoots");
        glassHelmet = glassArmor(3367, 0, 1, "ifhyGlassHelmet");
        glassChestplate = glassArmor(3368, 1, 3, "ifhyGlassChestplate");
        glassLeggings = glassArmor(3369, 2, 2, "ifhyGlassLeggings");
        glassBoots = glassArmor(3370, 3, 1, "ifhyGlassBoots");
        blackglassHelmet = netherArmor(3371, 0, 3, 9, 1050, 6, 0.08D, blackglassIngot, "ifhyBlackglassArmor", "item.ifhyBlackglassArmor.bonus", "ifhyBlackglassHelmet");
        blackglassChestplate = netherArmor(3372, 1, 8, 15, 1050, 6, 0.08D, blackglassIngot, "ifhyBlackglassArmor", "item.ifhyBlackglassArmor.bonus", "ifhyBlackglassChestplate");
        blackglassLeggings = netherArmor(3373, 2, 6, 12, 1050, 6, 0.08D, blackglassIngot, "ifhyBlackglassArmor", "item.ifhyBlackglassArmor.bonus", "ifhyBlackglassLeggings");
        blackglassBoots = netherArmor(3374, 3, 3, 7, 1050, 6, 0.08D, blackglassIngot, "ifhyBlackglassArmor", "item.ifhyBlackglassArmor.bonus", "ifhyBlackglassBoots");
        quartzglassHelmet = netherArmor(3375, 0, 2, 1, 300, 14, 0.0D, quartzglassIngot, "ifhyQuartzglassArmor", "item.ifhyQuartzglassArmor.bonus", "ifhyQuartzglassHelmet");
        quartzglassChestplate = netherArmor(3376, 1, 4, 2, 300, 14, 0.0D, quartzglassIngot, "ifhyQuartzglassArmor", "item.ifhyQuartzglassArmor.bonus", "ifhyQuartzglassChestplate");
        quartzglassLeggings = netherArmor(3377, 2, 3, 2, 300, 14, 0.0D, quartzglassIngot, "ifhyQuartzglassArmor", "item.ifhyQuartzglassArmor.bonus", "ifhyQuartzglassLeggings");
        quartzglassBoots = netherArmor(3378, 3, 1, 1, 300, 14, 0.0D, quartzglassIngot, "ifhyQuartzglassArmor", "item.ifhyQuartzglassArmor.bonus", "ifhyQuartzglassBoots");
        darkHelmet = netherArmor(3379, 0, 3, 2, 1200, 12, 0.06D, darkIngot, "ifhyDarkArmor", "item.ifhyDarkArmor.bonus", "ifhyDarkHelmet");
        darkChestplate = netherArmor(3380, 1, 9, 4, 1200, 12, 0.06D, darkIngot, "ifhyDarkArmor", "item.ifhyDarkArmor.bonus", "ifhyDarkChestplate");
        darkLeggings = netherArmor(3381, 2, 7, 3, 1200, 12, 0.06D, darkIngot, "ifhyDarkArmor", "item.ifhyDarkArmor.bonus", "ifhyDarkLeggings");
        darkBoots = netherArmor(3382, 3, 3, 2, 1200, 12, 0.06D, darkIngot, "ifhyDarkArmor", "item.ifhyDarkArmor.bonus", "ifhyDarkBoots");
        quicksilverHelmet = alloyArmor(3383, 0, 2, 0, 500, 18, 0.0D, sealedQuicksilverIngot, "ifhyQuicksilverArmor", "item.ifhyQuicksilverArmor.bonus", "ifhyQuicksilverHelmet");
        quicksilverChestplate = alloyArmor(3384, 1, 5, 1, 500, 18, 0.0D, sealedQuicksilverIngot, "ifhyQuicksilverArmor", "item.ifhyQuicksilverArmor.bonus", "ifhyQuicksilverChestplate");
        quicksilverLeggings = alloyArmor(3385, 2, 4, 1, 500, 18, 0.0D, sealedQuicksilverIngot, "ifhyQuicksilverArmor", "item.ifhyQuicksilverArmor.bonus", "ifhyQuicksilverLeggings");
        quicksilverBoots = alloyArmor(3386, 3, 2, 0, 500, 18, 0.0D, sealedQuicksilverIngot, "ifhyQuicksilverArmor", "item.ifhyQuicksilverArmor.bonus", "ifhyQuicksilverBoots");
        anchorHelmet = alloyArmor(3387, 0, 3, 9, 1300, 6, 0.10D, endstoneIngot, "ifhyAnchorArmor", "item.ifhyAnchorArmor.bonus", "ifhyAnchorHelmet");
        anchorChestplate = alloyArmor(3388, 1, 8, 15, 1300, 6, 0.10D, endstoneIngot, "ifhyAnchorArmor", "item.ifhyAnchorArmor.bonus", "ifhyAnchorChestplate");
        anchorLeggings = alloyArmor(3389, 2, 6, 12, 1300, 6, 0.10D, endstoneIngot, "ifhyAnchorArmor", "item.ifhyAnchorArmor.bonus", "ifhyAnchorLeggings");
        anchorBoots = alloyArmor(3390, 3, 3, 7, 1300, 6, 0.10D, endstoneIngot, "ifhyAnchorArmor", "item.ifhyAnchorArmor.bonus", "ifhyAnchorBoots");

        ironFishingPoleBaited = (ItemUpgradeableFishingRod) new ItemUpgradeableFishingRod(3405, 2352, true, 250)
                .setTextureName("nightmare:ifhyIronFishingRodBaited").setUnlocalizedName("ifhyIronFishingRodBaited");
        diamondFishingPole = (ItemUpgradeableFishingRod) new ItemUpgradeableFishingRod(3406, 3407, false, 768)
                .setTextureName("nightmare:ifhyDiamondFishingRod").setUnlocalizedName("ifhyDiamondFishingRod").setCreativeTab(CreativeTabs.tabTools);
        diamondFishingPoleBaited = (ItemUpgradeableFishingRod) new ItemUpgradeableFishingRod(3407, 3406, true, 768)
                .setTextureName("nightmare:ifhyDiamondFishingRodBaited").setUnlocalizedName("ifhyDiamondFishingRodBaited");
        steelFishingPole = (ItemUpgradeableFishingRod) new ItemUpgradeableFishingRod(3408, 3409, false, 1024)
                .setTextureName("nightmare:ifhySteelFishingRod").setUnlocalizedName("ifhySteelFishingRod").setCreativeTab(CreativeTabs.tabTools);
        steelFishingPoleBaited = (ItemUpgradeableFishingRod) new ItemUpgradeableFishingRod(3409, 3408, true, 1024)
                .setTextureName("nightmare:ifhySteelFishingRodBaited").setUnlocalizedName("ifhySteelFishingRodBaited");
        fishingBellUpgrade = material(3410, "ifhyFishingBellUpgrade");
        fishingLureUpgrade = material(3411, "ifhyFishingLureUpgrade");
        fishingAutoReelUpgrade = material(3412, "ifhyFishingAutoReelUpgrade");
        rareFishLureUpgrade = material(3413, "ifhyRareFishLureUpgrade");
        fishingEssence = material(3414, "ifhyFishingEssence");
    }

    private static Item material(int id, String name) {
        return new NMItem(id).setTextureName("nightmare:" + name).setUnlocalizedName(name).setCreativeTab(CreativeTabs.tabMaterials);
    }

    private static Item netherMaterial(int id, String name) {
        return new NetherItem(id).setTextureName("nightmare:" + name).setUnlocalizedName(name).setCreativeTab(CreativeTabs.tabMaterials);
    }

    private static ItemAlloyArmor alloyArmor(int id, int armorType, int protection, int weight, int maxUses, int enchantability, double knockbackResistance, Item repairItem, String wornTexture, String bonusKey, String name) {
        return (ItemAlloyArmor)new ItemAlloyArmor(id, armorType, protection, weight, maxUses, enchantability, knockbackResistance, repairItem.itemID, wornTexture, bonusKey).setTextureName("nightmare:" + name).setUnlocalizedName(name);
    }
    private static ItemAlloyArmor alloyArmor(int id, int armorType, int protection, int weight, int maxUses, int enchantability, double knockbackResistance, Item repairItem, String wornTexture, String name) {
        return (ItemAlloyArmor)new ItemAlloyArmor(id, armorType, protection, weight, maxUses, enchantability,
                knockbackResistance, repairItem.itemID, wornTexture, null)
                .setTextureName("nightmare:" + name).setUnlocalizedName(name);
    }

    private static ItemCarbonIronArmor carbonIronArmor(int id, int armorType, int protection, int weight, int maxUses, int enchantability, double knockbackResistance, Item repairItem, String wornTexture, String bonusKey, String name) {
        return (ItemCarbonIronArmor)new ItemCarbonIronArmor(id, armorType, protection, weight, maxUses,
                enchantability, knockbackResistance, repairItem.itemID, wornTexture, bonusKey)
                .setTextureName("nightmare:" + name).setUnlocalizedName(name);
    }

    private static ItemHeatResistantArmor heatArmor(int id, int armorType, int protection, int weight, int maxUses, String name) {
        return (ItemHeatResistantArmor)new ItemHeatResistantArmor(id, armorType, protection, weight, maxUses,
                10, 0.0D, nickelPlate.itemID, "ifhyHeatResistantArmor",
                "item.ifhyHeatResistantArmor.bonus", 0.12F)
                .setTextureName("nightmare:" + name).setUnlocalizedName(name);
    }

    private static ItemNetherAlloyArmor netherArmor(int id, int armorType, int protection, int weight,
                                                     int maxUses, int enchantability, double knockbackResistance,
                                                     Item repairItem, String wornTexture, String bonusKey, String name) {
        return (ItemNetherAlloyArmor)new ItemNetherAlloyArmor(id, armorType, protection, weight, maxUses,
                enchantability, knockbackResistance, repairItem.itemID, wornTexture, bonusKey)
                .setTextureName("nightmare:" + name).setUnlocalizedName(name);
    }

    private static ItemCoresteelArmor coresteelArmor(int id, int armorType, int protection, int weight,
                                                      int maxUses, int heatCapacity, String name) {
        return (ItemCoresteelArmor)new ItemCoresteelArmor(id, armorType, protection, weight, maxUses,
                7, 0.055D, coresteelIngot.itemID, "ifhyCoresteelArmor",
                "item.ifhyCoresteelArmor.bonus", heatCapacity)
                .setTextureName("nightmare:" + name).setUnlocalizedName(name);
    }

    private static ItemChargedArmor chargedArmor(int id, int armorType, int protection, int weight, int maxUses,
                                                  Item repairItem, String wornTexture, String bonusKey, String name) {
        return (ItemChargedArmor)new ItemChargedArmor(id, armorType, protection, weight, maxUses, 12, 0.0D,
                repairItem.itemID, wornTexture, bonusKey, 4000)
                .setTextureName("nightmare:" + name).setUnlocalizedName(name);
    }

    private static ItemGlassArmor glassArmor(int id, int armorType, int protection, String name) {
        return (ItemGlassArmor)new ItemGlassArmor(id, armorType, protection, 0, 90, 8, 0.0D,
                Block.glass.blockID, "ifhyGlassArmor", "item.ifhyGlassArmor.bonus")
                .setTextureName("nightmare:" + name).setUnlocalizedName(name);
    }

    private static ItemHeatResistantArmor sunArmorPiece(int id, int armorType, int protection, int weight,
                                                         String name) {
        return (ItemHeatResistantArmor)new ItemHeatResistantArmor(id, armorType, protection, weight, 1400,
                10, 0.05D, deadzoneAlloyIngot.itemID, "ifhySunArmor", "item.ifhySunArmor.bonus", 0.15F)
                .setTextureName("nightmare:" + name).setUnlocalizedName(name);
    }

    private static FoodItem createRawFish(int id, String name) {
        return (FoodItem) new NMFoodItem(id, 1, 0.1f, false, name, true)
                .setStandardFoodPoisoningEffect()
                .setIconName(("nightmare:" + name))
                .setCreativeTab(CreativeTabs.tabFood);
    }

    public static Item[] getRawFish() {
        return rawFish.clone();
    }
    private static void doNightmareModeItems() {
        rpg = (ItemRPG) (new ItemRPG(2309)).setTextureName("nightmare:nmRPG");
        rifle = (ItemAR) (new ItemAR(2310)).setTextureName("nightmare:nmRifle");
        bandage = (ItemBandage) (new ItemBandage(2311, 0, 0f, false)).setTextureName("nightmare:nmBandage");
        ironKnittingNeedles = (ItemIronKnittingNeedles) (new ItemIronKnittingNeedles(2312)).setTextureName("nightmare:nmNeedles");
        witchLocator = (ItemStructureLocator) (new ItemStructureLocator(2314, true, 0x84bdb8)).setTextureName("nightmare:nmWitchDust").setUnlocalizedName("nmItemWitchLocator");

        bloodOrb = (ItemBloodOrb) (new ItemBloodOrb(2315)).setTextureName("nightmare:nmBloodOrb");
        bloodPickaxe = (ItemBloodPickaxe) (new ItemBloodPickaxe(2316, EnumToolMaterial.EMERALD, BLOOD_MOON_DURABILITY)).setTextureName("nightmare:nmBloodPickaxe");
        bloodAxe = (ItemBloodAxe) (new ItemBloodAxe(2317, EnumToolMaterial.EMERALD, BLOOD_MOON_DURABILITY)).setTextureName("nightmare:nmBloodAxe");
        bloodShovel = (ItemBloodShovel) (new ItemBloodShovel(2318, EnumToolMaterial.EMERALD, BLOOD_MOON_DURABILITY)).setTextureName("nightmare:nmBloodShovel");
        bloodHoe = (ItemBloodHoe) (new ItemBloodHoe(2319, EnumToolMaterial.EMERALD, BLOOD_MOON_DURABILITY)).setTextureName("nightmare:nmBloodHoe");
        bloodSword = (ItemBloodSword) (new ItemBloodSword(2320, EnumToolMaterial.EMERALD, BLOOD_MOON_DURABILITY)).setTextureName("nightmare:nmBloodSword");

        bloodHelmet = (ItemBloodArmor) (new ItemBloodArmor(2321, EnumArmorMaterial.IRON, 0, 2, BLOOD_MOON_DURABILITY, 0.05d)).setTextureName("nightmare:nmBloodHelmet").setUnlocalizedName("nmBloodHelmet");
        bloodChestplate = (ItemBloodArmor) (new ItemBloodArmor(2322, EnumArmorMaterial.DIAMOND, 1, 4, BLOOD_MOON_DURABILITY, 0.05d)).setTextureName("nightmare:nmBloodChestplate").setUnlocalizedName("nmBloodChestplate");
        bloodLeggings = (ItemBloodArmor) (new ItemBloodArmor(2323, EnumArmorMaterial.IRON, 2, 3, BLOOD_MOON_DURABILITY, 0.05d)).setTextureName("nightmare:nmBloodLeggings").setUnlocalizedName("nmBloodLeggings");
        bloodBoots = (ItemBloodArmor) (new ItemBloodArmor(2324, EnumArmorMaterial.IRON, 3, 1, BLOOD_MOON_DURABILITY, 0d)).setTextureName("nightmare:nmBloodBoots").setUnlocalizedName("nmBloodBoots");

        bloodIngot = (new NMItem(2325)).setTextureName("nightmare:nmBloodIngot").setCreativeTab(CreativeTabs.tabMaterials).setUnlocalizedName("nmBloodIngot");

        darksunFragment = (new NMItem(2326)).setTextureName("nightmare:nmDarksunFragment").setCreativeTab(CreativeTabs.tabMaterials).setUnlocalizedName("nmDarksunFragment");

        magicFeather = (new NMItem(2327)).setIndestructible().setTextureName("nightmare:nmMagicFeather").setCreativeTab(CreativeTabs.tabMaterials).setUnlocalizedName("nmMagicFeather");
        bloodMilk = (NMItemBucketMilk) (new NMItemBucketMilk(2328)).setTextureName("nightmare:nmBloodMilk").setCreativeTab(CreativeTabs.tabMaterials).setUnlocalizedName("nmBloodMilk");
        creeperChop = (NMFoodItem) (new NMFoodItem(2329, 6, 0.25f, false, "nmCreeperChop", false)).setTextureName("nightmare:nmCreeperChop").setCreativeTab(CreativeTabs.tabFood);
        voidSack = (new NMItem(2330)).setTextureName("nightmare:nmVoidSack").setCreativeTab(CreativeTabs.tabMaterials).setUnlocalizedName("nmVoidSack");
        voidMembrane = (new NMItem(2331)).setTextureName("nightmare:nmVoidMembrane").setCreativeTab(CreativeTabs.tabMaterials).setUnlocalizedName("nmVoidMembrane");
        charredFlesh = (NMRottenFleshItem) (new NMRottenFleshItem(2332)).setMaxStackSize(64).setTextureName("nightmare:nmCharredFlesh").setCreativeTab(CreativeTabs.tabMaterials).setUnlocalizedName("nmCharredFlesh");
        spiderFangs = (new NMItem(2333)).setTextureName("nightmare:nmSpiderFangs").setCreativeTab(CreativeTabs.tabMaterials).setUnlocalizedName("nmSpiderFangs");
        fireRod = (new NMItem(2334)).setTextureName("nightmare:nmHellFireRod").setCreativeTab(CreativeTabs.tabMaterials).setUnlocalizedName("nmHellFireRod");
        waterRod = (new NMItem(2335)).setTextureName("nightmare:nmWaterRod").setCreativeTab(CreativeTabs.tabMaterials).setUnlocalizedName("nmWaterRod");
        sulfur = (new NMItem(2336)).setTextureName("nightmare:nmSulfur").setCreativeTab(CreativeTabs.tabMaterials).setUnlocalizedName("nmSulfur");
        creeperTear = (new NMItem(2337)).setTextureName("nightmare:nmCreeperTear").setCreativeTab(CreativeTabs.tabMaterials).setUnlocalizedName("nmCreeperTear");
        silverLump = (new NMItem(2338)).setTextureName("nightmare:nmSilverLump").setCreativeTab(CreativeTabs.tabMaterials).setUnlocalizedName("nmSilverLump");
        witheredBone = (new NMItem(2339)).setTextureName("nightmare:nmWitheredBone").setCreativeTab(CreativeTabs.tabMaterials).setUnlocalizedName("nmWitheredBone");
        decayedFlesh = (NMRottenFleshItem) (new NMRottenFleshItem(2340)).setMaxStackSize(64).setTextureName("nightmare:nmDecayedFlesh").setCreativeTab(CreativeTabs.tabMaterials).setUnlocalizedName("nmDecayedFlesh");
        ghastTentacle = (new NMItem(2341)).setTextureName("nightmare:nmGhastTentacle").setCreativeTab(CreativeTabs.tabMaterials).setUnlocalizedName("nmGhastTentacle");
        elementalRod = (new NMItem(2342)).setTextureName("nightmare:nmElementalRod").setCreativeTab(CreativeTabs.tabMaterials).setUnlocalizedName("nmElementalRod");
        shadowRod = (new NMItem(2343)).setTextureName("nightmare:nmShadowRod").setCreativeTab(CreativeTabs.tabMaterials).setUnlocalizedName("nmShadowRod");
        speedCoil = (new NMItem(2344)).setTextureName("nightmare:nmSpeedCoil").setCreativeTab(CreativeTabs.tabMaterials).setUnlocalizedName("nmSpeedCoil");
        starOfTheBloodGod = (NMBloodStarItem) new NMBloodStarItem(2345).setMaxStackSize(1).setTextureName("nightmare:nmStarOfTheBloodGod").setCreativeTab(CreativeTabs.tabMaterials).setUnlocalizedName("nmStarOfTheBloodGod");

        calamari = (NMFoodItem) new NMFoodItem(2346, 2, 0f, true, "nmCalamari", true).setStandardFoodPoisoningEffect().setTextureName("nightmare:nmCalamari").setCreativeTab(CreativeTabs.tabFood);
        calamariRoast = (NMFoodItem) new NMFoodItem(2347, 5, 0.25f, true, "nmCalamariRoast", true).setTextureName("nightmare:nmCalamariRoast").setCreativeTab(CreativeTabs.tabFood);
        friedCalamari = (NMFoodItem) new NMFoodItem(2348, 9, 0.5f, true, "nmFriedCalamari", true).setTextureName("nightmare:nmFriedCalamari").setCreativeTab(CreativeTabs.tabFood);

        steelBunch = new NMItem(2349).setTextureName("nightmare:nmSteelBunch").setUnlocalizedName("nmSteelBunch").setCreativeTab(CreativeTabs.tabMaterials);
        eclipseBow = (ItemEclipseBow) new ItemEclipseBow(2350).setCreativeTab(CreativeTabs.tabCombat);
        magicArrow = (ItemMagicArrow) new ItemMagicArrow(2351).setTextureName("nightmare:nmMagicArrow").setUnlocalizedName("nmMagicArrow").setCreativeTab(CreativeTabs.tabCombat);
        ironFishingPole = (ItemUpgradeableFishingRod) new ItemUpgradeableFishingRod(2352, 3405, false, 250)
                .setTextureName("nightmare:ifhyIronFishingRod").setUnlocalizedName("ifhyIronFishingRod").setCreativeTab(CreativeTabs.tabTools);

        dungApple = (NMFoodItem) new NMFoodItem(2353, 2, 0.25f, false, "nmDungApple", false).setPotionEffect(Potion.poison.id, 1, 128, 1.0f).setTextureName("nightmare:nmDungApple").setCreativeTab(CreativeTabs.tabFood);
        creeperBallSoup = (NMFoodItem) new NMFoodItem(2354, 6, 1f, false, "nmOysterSoup", false).setPotionEffect(Potion.regeneration.id, 10, 4, 1.0f).setTextureName("nightmare:nmOysterSoup").hideFromEMI();

        templeLocator = (ItemStructureLocator) (new ItemStructureLocator(2355, false, 0xFFFF00)).setTextureName("nightmare:nmTempleDust").setUnlocalizedName("nmTempleDust");


        obsidianShard = new NetherItem(2356).setTextureName("nightmare:nmObsidianShard").setUnlocalizedName("nmObsidianShard").setCreativeTab(CreativeTabs.tabMaterials);

        // this code is done in ItemMixin to replace horse armor
    //        ironHorseArmorAdvanced      = new ItemAdvancedHorseArmor(2357, ItemAdvancedHorseArmor.ArmorTier.IRON).setUnlocalizedName("nmHorseArmorIron").setTextureName("nmHorseArmorIron");
    //        goldHorseArmorAdvanced      = new ItemAdvancedHorseArmor(2358, ItemAdvancedHorseArmor.ArmorTier.GOLD).setUnlocalizedName("nmHorseArmorGold").setTextureName("nmHorseArmorGold");
    //        diamondHorseArmorAdvanced   = new ItemAdvancedHorseArmor(2359, ItemAdvancedHorseArmor.ArmorTier.DIAMOND).setUnlocalizedName("nmHorseArmorDiamond").setTextureName("nmHorseArmorDiamond");

        refinedDiamondIngot = new NMItem(2360).setTextureName("nightmare:nmRefinedDiamondIngot").setUnlocalizedName("nmRefinedDiamondIngot").setCreativeTab(CreativeTabs.tabMaterials);

        lightningBolt = new ItemLightningBolt(2361).setTextureName("nightmare:nmLightning").setUnlocalizedName("nmLightning").setCreativeTab(CreativeTabs.tabMisc);

        villagerOrb = new ItemVillagerOrb(2362).setUnlocalizedName("nmVillagerOrb");

        refinedElement = new NMItem(2363).setTextureName("nightmare:refinedElement").setUnlocalizedName("nmRefinedElement").setCreativeTab(CreativeTabs.tabMaterials).hideFromEMI();

        witherSoul = new NMItem(2364).setIndestructible().setTextureName("nightmare:nmWitherSoul").setUnlocalizedName("nmWitherSoul").setCreativeTab(CreativeTabs.tabMaterials);

        honeyBall = new NMItem(2365).setTextureName("nightmare:nmHoneyBall").setUnlocalizedName("nmHoneyBall").setCreativeTab(CreativeTabs.tabMaterials);

        lifeFruit = new ItemLifeFruit(2366, "nmLifeFruit").setTextureName("nightmare:nmLifeFruit").setCreativeTab(CreativeTabs.tabFood);

        honeyMelon = new ItemLifeFruit(2367, "nmHoneyMelon").setTextureName("nightmare:nmHoneyMelon").setCreativeTab(CreativeTabs.tabFood);

        awakenedStar = new NMBloodStarItem(2368).setTextureName("nightmare:nmAwakenedStar").setUnlocalizedName("nmAwakenedStar").setCreativeTab(CreativeTabs.tabMaterials);

        hellGem = new NMItem(2369).setIndestructible().setTextureName("nightmare:nmHellGem").setUnlocalizedName("nmHellGem").setCreativeTab(CreativeTabs.tabMaterials);





        ACHIEVEMENT_SPECIAL_SNOWBALL = new NMItem(2400).setTextureName("nightmare:nmAchievementSpecialSnowball").hideFromEMI();
        ACHIEVEMENT_SPECIAL_HARDMODE = new NMItem(2405).setTextureName("nightmare:nmAchievementHardmode").hideFromEMI();
        ACHIEVEMENT_SPECIAL_BLOODMOON = new NMItem(2406).setTextureName("nightmare:nmAchievementBloodMoon").hideFromEMI();
        ACHIEVEMENT_SPECIAL_BLOODMOON_WITHER = new NMItem(2409).setTextureName("nightmare:nmAchievementBloodMoonWither").hideFromEMI();
        ACHIEVEMENT_SPECIAL_ECLIPSE = new NMItem(2410).setTextureName("nightmare:nmAchievementEclipse").hideFromEMI();
        ACHIEVEMENT_SPECIAL_MERCHANT = new NMItem(2411).setTextureName("nightmare:nmAchievementMerchant").hideFromEMI();
        ACHIEVEMENT_SPECIAL_CHICKEN = new NMItem(2412).setTextureName("nightmare:nmAchievementChicken").hideFromEMI();
        ACHIEVEMENT_SPECIAL_DIAMOND = new NMItem(2413).setTextureName("nightmare:nmAchievementDiamond").hideFromEMI();
        ACHIEVEMENT_SPECIAL_SKULL = new NMItem(2415).setTextureName("nightmare:nmAchievementBloodSkull").hideFromEMI();
        ACHIEVEMENT_SPECIAL_ARROW_TRIPLE = new NMItem(2416).setTextureName("nightmare:nmAchievementTripleArrow").hideFromEMI();
        ACHIEVEMENT_SPECIAL_ARROW_RED = new NMItem(2417).setTextureName("nightmare:nmAchievementArrowRed").hideFromEMI();
        ACHIEVEMENT_SPECIAL_TRIPLE_TEAR = new NMItem(2418).setTextureName("nightmare:nmAchievementTripleTear").hideFromEMI();
        ACHIEVEMENT_SPECIAL_BLOOD_ZOMBIE = new NMItem(2419).setTextureName("nightmare:nmAchievementBloodZombie").hideFromEMI();
    }

    public static void runItemInit(){}





    public static void hideItems(){
        lightningBolt.hideFromEMI().setCreativeTab(null);
        refinedElement.hideFromEMI().setCreativeTab(null);
        honeyBall.hideFromEMI().setCreativeTab(null);
        honeyMelon.hideFromEMI().setCreativeTab(null);
        lifeFruit.hideFromEMI().setCreativeTab(null);
        awakenedStar.hideFromEMI().setCreativeTab(null);
    }

    public static void addItemsToTags(){
        // adds all the NM items to their respective tag. mostly food

        BTWTags.foods.add(calamari);
        BTWTags.foods.add(calamariRoast);
        BTWTags.foods.add(friedCalamari);
        BTWTags.foods.add(creeperChop);
        BTWTags.foods.add(dungApple);
        BTWTags.foods.add(creeperBallSoup);
    }

}
