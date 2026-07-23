package com.itlesports.nightmaremode.item;

import btw.item.BTWItems;
import btw.item.BTWTags;
import btw.item.items.*;
import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.block.blocks.templates.NMPlaceAsBlockItem;
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
    public static ItemIronFishingPole ironFishingPole;

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

    public static Item rawNickelRock;
    public static Item crushedNickelRock;
    public static Item washedNickelConcentrate;
    public static Item roastedNickelConcentrate;
    public static Item nickelIngot;
    public static Item nickelPlate;
    public static Item nickelBinding;
    public static Item nickelMachinePart;
    public static Item nickelHeatComponent;

    public static Item rawLithium;
    public static Item hammeredLithium;
    public static Item washedLithium;
    public static Item refinedLithium;
    public static Item lithiumSalt;
    public static Item lithiumStabilizer;
    public static Item lithiumHeatCompound;

    public static Item uncleanedCrystalShard;
    public static Item cleanCrystalShard;
    public static Item polishedCrystalShard;
    public static Item crystalLens;
    public static Item precisionCrystalGear;

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
    public static Item netherFishingRod;
    public static Item netherFishingRodBaited;
    public static Item lavafish;
    public static Item netherOvenPart;
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

        rawNickelRock = new NMItem(2623).setUnlocalizedName("ifhyRawNickelRock").setTextureName("nightmare:ifhyRawNickelRock").setCreativeTab(CreativeTabs.tabMaterials);
        crushedNickelRock = new NMItem(2624).setUnlocalizedName("ifhyCrushedNickelRock").setTextureName("nightmare:ifhyCrushedNickelRock").setCreativeTab(CreativeTabs.tabMaterials);
        washedNickelConcentrate = new NMItem(2625).setUnlocalizedName("ifhyWashedNickelConcentrate").setTextureName("nightmare:ifhyWashedNickelConcentrate").setCreativeTab(CreativeTabs.tabMaterials);
        roastedNickelConcentrate = new NMItem(2626).setUnlocalizedName("ifhyRoastedNickelConcentrate").setTextureName("nightmare:ifhyRoastedNickelConcentrate").setCreativeTab(CreativeTabs.tabMaterials);
        nickelIngot = new NMItem(2627).setUnlocalizedName("ifhyNickelIngot").setTextureName("nightmare:ifhyNickelIngot").setCreativeTab(CreativeTabs.tabMaterials);
        nickelPlate = new NMItem(2628).setUnlocalizedName("ifhyNickelPlate").setTextureName("nightmare:ifhyNickelPlate").setCreativeTab(CreativeTabs.tabMaterials);
        nickelBinding = new NMItem(2629).setUnlocalizedName("ifhyNickelBinding").setTextureName("nightmare:ifhyNickelBinding").setCreativeTab(CreativeTabs.tabMaterials);
        nickelMachinePart = new NMItem(2630).setUnlocalizedName("ifhyNickelMachinePart").setTextureName("nightmare:ifhyNickelMachinePart").setCreativeTab(CreativeTabs.tabMaterials);
        nickelHeatComponent = new NMItem(2631).setUnlocalizedName("ifhyNickelHeatComponent").setTextureName("nightmare:ifhyNickelHeatComponent").setCreativeTab(CreativeTabs.tabMaterials);

        rawLithium = new NMItem(2632).setUnlocalizedName("ifhyRawLithium").setTextureName("nightmare:ifhyRawLithium").setCreativeTab(CreativeTabs.tabMaterials);
        hammeredLithium = new NMItem(2633).setUnlocalizedName("ifhyHammeredLithium").setTextureName("nightmare:ifhyHammeredLithium").setCreativeTab(CreativeTabs.tabMaterials);
        washedLithium = new NMItem(2634).setUnlocalizedName("ifhyWashedLithium").setTextureName("nightmare:ifhyWashedLithium").setCreativeTab(CreativeTabs.tabMaterials);
        refinedLithium = new NMItem(2635).setUnlocalizedName("ifhyRefinedLithium").setTextureName("nightmare:ifhyRefinedLithium").setCreativeTab(CreativeTabs.tabMaterials);
        lithiumSalt = new NMItem(2636).setUnlocalizedName("ifhyLithiumSalt").setTextureName("nightmare:ifhyLithiumSalt").setCreativeTab(CreativeTabs.tabMaterials);
        lithiumStabilizer = new NMItem(2637).setUnlocalizedName("ifhyLithiumStabilizer").setTextureName("nightmare:ifhyLithiumStabilizer").setCreativeTab(CreativeTabs.tabMaterials);
        lithiumHeatCompound = new NMItem(2638).setUnlocalizedName("ifhyLithiumHeatCompound").setTextureName("nightmare:ifhyLithiumHeatCompound").setCreativeTab(CreativeTabs.tabMaterials);

        uncleanedCrystalShard = new NMItem(2639).setUnlocalizedName("ifhyUncleanedCrystalShard").setTextureName("nightmare:ifhyUncleanedCrystalShard").setCreativeTab(CreativeTabs.tabMaterials);
        cleanCrystalShard = new NMProgressiveItem(2640, 2641).setTargetDurability(80).setUnlocalizedName("ifhyCleanCrystalShard").setTextureName("nightmare:ifhyCleanCrystalShard").setCreativeTab(CreativeTabs.tabMaterials);
        polishedCrystalShard = new NMItem(2641).setUnlocalizedName("ifhyPolishedCrystalShard").setTextureName("nightmare:ifhyPolishedCrystalShard").setCreativeTab(CreativeTabs.tabMaterials);
        crystalLens = new NMItem(2642).setUnlocalizedName("ifhyCrystalLens").setTextureName("nightmare:ifhyCrystalLens").setCreativeTab(CreativeTabs.tabMaterials);
        precisionCrystalGear = new NMItem(2643).setUnlocalizedName("ifhyPrecisionCrystalGear").setTextureName("nightmare:ifhyPrecisionCrystalGear").setCreativeTab(CreativeTabs.tabMaterials);

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
        skillBook = new ItemSkillBook(2655).setUnlocalizedName("nmSkillBook").setTextureName("nightmare:nmSkillBook");

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
        reedPeeling = (NMProgressiveItem) new NMProgressiveItem(2694, reedStem.itemID).setTargetDurability(100).setTextureName("reeds").setUnlocalizedName("ifhyReedPeeling").setCreativeTab(CreativeTabs.tabMaterials);
        washedPith = new NMItem(2695).setTextureName("nightmare:ifhyWashedPith").setUnlocalizedName("ifhyWashedPith").setCreativeTab(CreativeTabs.tabMaterials);
        wetFusedPlantSheet = new NMItem(2696).setTextureName("nightmare:ifhyWetFusedPlantSheet").setUnlocalizedName("ifhyWetFusedPlantSheet").setCreativeTab(CreativeTabs.tabMaterials);
        plantSheet = new NMItem(2697).setTextureName("nightmare:ifhyPlantSheet").setUnlocalizedName("ifhyPlantSheet").setCreativeTab(CreativeTabs.tabMaterials);
        washedSugarCane = new NMItem(2698).setTextureName("nightmare:ifhyWashedSugarCane").setUnlocalizedName("ifhyWashedSugarCane").setCreativeTab(CreativeTabs.tabMaterials);
        pileOfSticks = new NMItem(2699).setTextureName("nightmare:ifhyPileOfSticks").setUnlocalizedName("ifhyPileOfSticks").setCreativeTab(CreativeTabs.tabMaterials);
        boneShard = new NetherItem(2700).setTextureName("nightmare:ifhyBoneShard").setUnlocalizedName("ifhyBoneShard").setCreativeTab(CreativeTabs.tabMaterials);
        stoneKnife = (ItemKnife) new ItemKnife(2701, 300, ItemKnife.TIER_STONE, 32).setTextureName("nightmare:ifhyStoneKnife").setUnlocalizedName("ifhyStoneKnife");
        ironKnife = (ItemKnife) new ItemKnife(2702, 160, ItemKnife.TIER_IRON, 96).setTextureName("nightmare:ifhyIronKnife").setUnlocalizedName("ifhyIronKnife");
        diamondKnife = (ItemKnife) new ItemKnife(2703, 80, ItemKnife.TIER_DIAMOND, 256).setTextureName("nightmare:ifhyDiamondKnife").setUnlocalizedName("ifhyDiamondKnife");

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
        netherOvenPart = new NetherItem(2720).setTextureName("nightmare:ifhyNetherOvenPart").setUnlocalizedName("ifhyNetherOvenPart").setCreativeTab(CreativeTabs.tabMaterials);
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
        ironFishingPole = (ItemIronFishingPole) new ItemIronFishingPole(2352).setCreativeTab(CreativeTabs.tabTools);

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
