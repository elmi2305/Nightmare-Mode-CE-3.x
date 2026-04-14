package com.itlesports.nightmaremode.block;

import btw.block.BTWBlocks;
import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.block.blocks.*;
import com.itlesports.nightmaremode.block.blocks.templates.BlockMetaMultiTextured;
import com.itlesports.nightmaremode.block.blocks.templates.NMBlock;
import com.itlesports.nightmaremode.item.items.ItemVillagerContainer;
import com.itlesports.nightmaremode.item.itemblock.ItemBlockTallFlower;
import com.itlesports.nightmaremode.item.itemblock.NMItemBlock;
import com.itlesports.nightmaremode.item.itemblock.NMItemBlockMeta;
import net.minecraft.src.*;

public class NMBlocks {
    public static Block steelOre;
    public static Block bloodBones;
    public static Block cryingObsidian;
    public static Block specialObsidian;
    public static BlockUnderworldPortal underworldPortal;
    public static BlockBloodChest bloodChest;
    public static BlockSteelLocker steelLocker;
    public static BlockRoad blockRoad;
    public static BlockRoad blockAsphalt;
    public static BlockCustomLadder stoneLadder;
    public static BlockCustomLadder ironLadder;

    public static Block hellforge;
    public static CustomBasketBlock customWickerBasket;

    public static BlockAsphaltCarpet asphaltLayer;
    public static BlockBloodSaw bloodSaw;
    public static BlockVillagerBase villagerBlock;

    public static Block underrock;
    public static Block understoneSmooth;
    public static Block underCobble;
    public static BlockUnderGrass underGrass;

    public static Block yellowFlowerRoots;
    public static Block plantMatter;
    public static Block disenchantmentTable;

    // -------------------------------------------------------------------------
    // Underworld grass / dirt  (Material.grass)
    // -------------------------------------------------------------------------

    public static BlockMetaMultiTextured underFlowerDirts;
    public static final int META_UNDER_DIRT   = 0;
    public static final int META_FLOWER_GRASS = 1;
    public static final int META_FLOWER_DIRT  = 2;

    // -------------------------------------------------------------------------
    // Mushroom structure blocks (Material.rock, indestructible)
    // -------------------------------------------------------------------------

    public static BlockMetaMultiTextured mushBlocks;
    public static final int META_MUSH_STEM                 = 0;
    public static final int META_MUSH_FLOOR_PARTIAL_YELLOW = 1;
    public static final int META_MUSH_PURPLE               = 2;
    public static final int META_MUSH_TOP_FLOOR_YELLOW     = 3;
    public static final int META_MUSH_FLOOR_YELLOW         = 4;
    public static final int META_MUSH_WALL_YELLOW          = 5;
    public static final int META_MUSH_FLOOR_PARTIAL_PURPLE = 6;
    public static final int META_MUSH_WALL_PURPLE          = 7;
    public static final int META_MUSH_TOP_FLOOR_PURPLE     = 8;
    public static final int META_MUSH_CAP_YELLOW           = 9;
    public static final int META_MUSH_CAP_WHITE            = 10;

    // -------------------------------------------------------------------------
    // Mushroom innards blocks (Material.rock, destructible)
    // -------------------------------------------------------------------------

    public static BlockMetaMultiTextured mushInnards;
    public static final int META_MUSH_INNARDS_BREAKABLE = 0;
    public static final int META_MUSH_INNARDS_EXPLOSIVE = 1;

    public static Block mushBookshelf;

    // -------------------------------------------------------------------------
    // Misc UW stones (Material.rock), mostly unused
    // -------------------------------------------------------------------------
    public static BlockMetaMultiTextured underStones;

    public static final int META_VOID_STONE  = 0;
    public static final int META_LIGHT_STONE = 1;

    // -------------------------------------------------------------------------
    // Misc hell stones (Material.rock)
    // -------------------------------------------------------------------------
    public static BlockMetaMultiTextured hellStones;

    public static final int META_HELLSTONE = 0;



    // -------------------------------------------------------------------------
    // INITIALIZE THE BLOCKS
    // -------------------------------------------------------------------------

    public static void initNightmareBlocks(){
        steelOre = (new SteelOre(2305)).setHardness(13.0F).setResistance(200.0F).setStepSound(BTWBlocks.oreStepSound).setUnlocalizedName("nmSteelOre").setTextureName("nightmare:steel_ore");
        Item.itemsList[steelOre.blockID] = new NMItemBlock(NMBlocks.steelOre.blockID - 256);

        bloodBones = new BloodBoneBlock(2306).setHardness(4f).setTextureName("nightmare:nmBloodBone").setUnlocalizedName("nmBloodBone").setCreativeTab(CreativeTabs.tabBlock);
        Item.itemsList[bloodBones.blockID] = new NMItemBlock(NMBlocks.bloodBones.blockID - 256);

        cryingObsidian = new BlockCryingObsidian(2307).setTextureName("nightmare:nmCryingObsidian").setUnlocalizedName("nmCryingObsidian").setHardness(-1.0F).setResistance(6000000.0F).setCreativeTab(CreativeTabs.tabBlock);
        Item.itemsList[cryingObsidian.blockID] = new NMItemBlock(NMBlocks.cryingObsidian.blockID - 256);

        specialObsidian = new NMBlock(2308, Material.rock).setTextureName("nightmare:nmSpecialObsidian").setUnlocalizedName("nmSpecialObsidian").setHardness(-1.0F).setResistance(6000000.0F).setCreativeTab(CreativeTabs.tabBlock);
        Item.itemsList[specialObsidian.blockID] = new NMItemBlock(NMBlocks.specialObsidian.blockID - 256);

        underworldPortal = (BlockUnderworldPortal) new BlockUnderworldPortal(2309).setUnlocalizedName("underworld_portal").setTextureName("nightmare:underworld_portal");
        Item.itemsList[underworldPortal.blockID] = new NMItemBlock(NMBlocks.underworldPortal.blockID - 256);

        bloodChest = (BlockBloodChest) new BlockBloodChest(2310, 1).setTextureName("nightmare:chestBlood").setUnlocalizedName("nmBloodChest");
        Item.itemsList[bloodChest.blockID] = new NMItemBlock(NMBlocks.bloodChest.blockID - 256);

        steelLocker = (BlockSteelLocker) new BlockSteelLocker(2355, 1).setTextureName("nightmare:lockerSteel").setUnlocalizedName("nmSteelLocker");
        Item.itemsList[steelLocker.blockID] = new NMItemBlock(NMBlocks.steelLocker.blockID - 256);

        blockRoad = (BlockRoad) new BlockRoad(2311, 1.5f).setUnlocalizedName("nmRoad").setTextureName("nightmare:nmRoad");
        Item.itemsList[blockRoad.blockID] = new NMItemBlock(NMBlocks.blockRoad.blockID - 256);

        blockAsphalt = (BlockRoad) new BlockRoad(2312, 1.85f).setUnlocalizedName("nmAsphalt").setTextureName("nightmare:nmAsphalt");
        Item.itemsList[blockAsphalt.blockID] = new NMItemBlock(NMBlocks.blockAsphalt.blockID - 256);

        stoneLadder = (BlockCustomLadder) new BlockCustomLadder(2313, 1.5f).setUnlocalizedName("nmStoneLadder").setTextureName("nightmare:nmStoneLadder");
        Item.itemsList[stoneLadder.blockID] = new NMItemBlock(NMBlocks.stoneLadder.blockID - 256);

        ironLadder = (BlockCustomLadder) new BlockCustomLadder(2314, 2.2f).setUnlocalizedName("nmIronLadder").setTextureName("nightmare:nmIronLadder");
        Item.itemsList[ironLadder.blockID] = new NMItemBlock(NMBlocks.ironLadder.blockID - 256);

        hellforge = new BlockHellforge(2315, true);
        Item.itemsList[hellforge.blockID] = new NMItemBlock(NMBlocks.hellforge.blockID - 256);

        customWickerBasket = new CustomBasketBlock(2316);
        Item.itemsList[customWickerBasket.blockID] = new NMItemBlock(NMBlocks.customWickerBasket.blockID - 256);

        asphaltLayer = (BlockAsphaltCarpet) new BlockAsphaltCarpet(2318).setHardness(2f).setUnlocalizedName("nmAsphaltLayer").setCreativeTab(CreativeTabs.tabDecorations);
        Item.itemsList[asphaltLayer.blockID] = new NMItemBlock(NMBlocks.asphaltLayer.blockID - 256);

        bloodSaw = (BlockBloodSaw) new BlockBloodSaw(2319).setHardness(8f);
        Item.itemsList[bloodSaw.blockID] = new NMItemBlock(NMBlocks.bloodSaw.blockID - 256);

        villagerBlock = new BlockVillagerBase(2320);
        Item.itemsList[villagerBlock.blockID] = new ItemVillagerContainer(NMBlocks.villagerBlock.blockID - 256);

        underrock = new NMBlock(2321, Material.rock).setTextureName("nightmare:nmUnderworldRock").setUnlocalizedName("nmUnderworldRock").setCreativeTab(CreativeTabs.tabBlock).setHardness(50f).setResistance(300f);
        Item.itemsList[underrock.blockID] = new NMItemBlock(NMBlocks.underrock.blockID - 256);

        understoneSmooth = new NMBlock(2322, Material.rock).setTextureName("nightmare:nmUnderworldRockSmooth").setUnlocalizedName("nmUnderworldRockSmooth").setCreativeTab(CreativeTabs.tabBlock).setHardness(20f).setResistance(10f);
        Item.itemsList[understoneSmooth.blockID] = new NMItemBlock(NMBlocks.understoneSmooth.blockID - 256);

        underCobble = new NMBlock(2323, Material.rock).setTextureName("nightmare:nmUnderworldRockCobble").setUnlocalizedName("nmUnderworldRockCobble").setCreativeTab(CreativeTabs.tabBlock).setHardness(10f).setResistance(5f);
        Item.itemsList[underCobble.blockID] = new NMItemBlock(NMBlocks.underCobble.blockID - 256);

        underGrass = new BlockUnderGrass(2324, Material.grass);
        Item.itemsList[underGrass.blockID] = new NMItemBlock(NMBlocks.underGrass.blockID - 256);

        // Underworld grass / dirt group
        underFlowerDirts = new BlockMetaMultiTextured(2325, Material.grass,
                /* 0: underDirt      */ BlockMetaMultiTextured.Variant
                .allSides("nightmare:blight_level_4_roots")
                .name("nmUnderDirt")
                .build(),

                /* 1: flowerGrass    */ BlockMetaMultiTextured.Variant
                .topBotSides("nightmare:nmFlowerGrassTop", "nightmare:nmFlowerDirt", "nightmare:nmFlowerGrassSide")
                .growsVegetation()
                .name("nmFlowerGrass")
                .build(),

                /* 2: flowerDirt     */ BlockMetaMultiTextured.Variant
                .allSides("nightmare:nmFlowerDirt")
                .growsVegetation()
                .name("nmFlowerDirt")
                .build()
        );
        Item.itemsList[underFlowerDirts.blockID] = new NMItemBlockMeta(underFlowerDirts.blockID - 256, underFlowerDirts);

        yellowFlowerRoots = new BlockTallFlower(2328);
        Item.itemsList[yellowFlowerRoots.blockID] = new ItemBlockTallFlower(NMBlocks.yellowFlowerRoots.blockID - 256);

        plantMatter = new NMBlock(2329, Material.wood).setTextureName("nightmare:nmPlantMatter").setUnlocalizedName("nmPlantMatter").setCreativeTab(CreativeTabs.tabBlock);
        Item.itemsList[plantMatter.blockID] = new NMItemBlock(NMBlocks.plantMatter.blockID - 256);

        disenchantmentTable = new BlockDisenchantmentTable(2330).setTextureName("nightmare:nmDisenchantmentTable").setUnlocalizedName("nmDisenchantmentTable").setCreativeTab(CreativeTabs.tabBlock);
        Item.itemsList[disenchantmentTable.blockID] = new NMItemBlock(NMBlocks.disenchantmentTable.blockID - 256);

        // Mushroom structure blocks
        mushBlocks = new BlockMetaMultiTextured(2331, Material.rock,
                /* 0: mushroomStem                */ BlockMetaMultiTextured.Variant
                .allSides("nightmare:nmMushStem")
                .hardness(-1f).resistance(1000f)
                .name("nmMushStem")
                .build(),
                /* 1: mushroomFloorPartialYellow  */ BlockMetaMultiTextured.Variant
                .topBotSides("nightmare:nmMushFloorYellow", "nightmare:nmMushStem", "nightmare:nmMushFloorYellow")
                .hardness(-1f).resistance(1000f)
                .name("mushroomFloorPartial")
                .build(),
                /* 2: mushroomPurple              */ BlockMetaMultiTextured.Variant
                .allSides("nightmare:nmMushPurple")
                .hardness(-1f).resistance(1000f)
                .name("mushroomFloor")
                .build(),
                /* 3: mushroomTopFloorYellow      */ BlockMetaMultiTextured.Variant
                .topBotSides("nightmare:nmMushPurple", "nightmare:nmMushInnardsYellow", "nightmare:nmMushPurple")
                .hardness(-1f).resistance(1000f)
                .name("mushroomFloorSecond")
                .build(),
                /* 4: mushroomFloorYellow         */ BlockMetaMultiTextured.Variant
                .allSides("nightmare:nmMushYellow")
                .hardness(-1f).resistance(1000f)
                .name("mushroomFloorYellow")
                .build(),
                /* 5: mushroomWallYellow          */ BlockMetaMultiTextured.Variant
                .allSides("nightmare:nmMushYellow")
                .hardness(-1f).resistance(1000f)
                .name("mushroomWall")
                .build(),
                /* 6: mushroomFloorPartialPurple  */ BlockMetaMultiTextured.Variant
                .topBotSides("nightmare:nmMushPurple", "nightmare:nmMushStem", "nightmare:nmMushPurple")
                .hardness(-1f).resistance(1000f)
                .name("mushroomFloorPartialPurple")
                .build(),
                /* 7: mushroomWallPurple          */ BlockMetaMultiTextured.Variant
                .allSides("nightmare:nmMushPurple")
                .hardness(-1f).resistance(1000f)
                .name("nmMushWallPurple")
                .build(),
                /* 8: mushroomTopFloorPurple      */ BlockMetaMultiTextured.Variant
                .allSides("nightmare:nmMushPurple")
                .hardness(-1f).resistance(1000f)
                .name("nmMushTopPurple")
                .build(),
                /* 9: mushroomCapYellow           */ BlockMetaMultiTextured.Variant
                .allSides("nightmare:nmMushCapYellow")
                .hardness(-1f).resistance(1000f)
                .name("nmMushCapYellow")
                .build(),
                /* 10: mushroomCapWhite           */ BlockMetaMultiTextured.Variant
                .allSides("nightmare:nmMushCapWhite")
                .hardness(-1f).resistance(1000f)
                .name("nmMushCapWhite")
                .build()
        );
        Item.itemsList[mushBlocks.blockID] = new NMItemBlockMeta(mushBlocks.blockID - 256, mushBlocks);

        // Mushroom innards block group
        mushInnards = new BlockMetaMultiTextured(2334, Material.rock,
                /* 0: mushInnardsBreakable         */ BlockMetaMultiTextured.Variant
                .allSides("nightmare:nmMushStemBreakable")
                .hardness(5f).resistance(10f)
                .name("mushInnardsBreakable")
                .build(),
                /* 1: mushInnardsBreakableExplosive */ BlockMetaMultiTextured.Variant
                .allSides("nightmare:nmMushStemBreakable")
                .hardness(2f).resistance(0f)
                .explosive()
                .name("mushInnardsBreakableExplosive")
                .build()
        );
        Item.itemsList[mushInnards.blockID] = new NMItemBlockMeta(mushInnards.blockID - 256, mushInnards);

        mushBookshelf = new BlockMushBookshelf(2344).setHardness(-1f).setResistance(10f).setUnlocalizedName("nmMushBookshelf").setTextureName("nightmare:nmMushBookshelf");
        Item.itemsList[mushBookshelf.blockID] = new NMItemBlock(NMBlocks.mushBookshelf.blockID - 256);

        // Underworld stones
        underStones = new BlockMetaMultiTextured(2345, Material.rock,
                /* 0: voidStone  */ BlockMetaMultiTextured.Variant
                .allSides("nightmare:nmVoidStone")
                .name("nmVoidStone")
                .build(),
                /* 1: lightStone */ BlockMetaMultiTextured.Variant
                .allSides("nightmare:nmLightStone")
                .name("nmLightStone")
                .build()
        );
        Item.itemsList[underStones.blockID] = new NMItemBlockMeta(underStones.blockID - 256, underStones);

        // Misc hell stones group
        hellStones = new BlockMetaMultiTextured(2346, Material.rock,
                /* 0: hellstone */ BlockMetaMultiTextured.Variant
                .allSides("nightmare:hellfire")
                .name("nmHellstone")
                .build()
        );
        Item.itemsList[hellStones.blockID] = new NMItemBlockMeta(hellStones.blockID - 256, hellStones);
    }

    public static void hideBlocks(){
        if(NightmareMode.devMode) return;

        // hiding by groups instead of individually. again, this is just to keep nosy people from asking me what certain things do
        yellowFlowerRoots = yellowFlowerRoots.hideFromEMI().setCreativeTab(null);
        plantMatter       = plantMatter.hideFromEMI().setCreativeTab(null);
        disenchantmentTable = disenchantmentTable.hideFromEMI().setCreativeTab(null);

        underFlowerDirts = (BlockMetaMultiTextured) underFlowerDirts.hideFromEMI().setCreativeTab(null);

        mushBlocks = (BlockMetaMultiTextured) mushBlocks.hideFromEMI().setCreativeTab(null);

        mushInnards = (BlockMetaMultiTextured) mushInnards.hideFromEMI().setCreativeTab(null);

        mushBookshelf = mushBookshelf.hideFromEMI().setCreativeTab(null);

        underStones = (BlockMetaMultiTextured) underStones.hideFromEMI().setCreativeTab(null);

        hellStones = (BlockMetaMultiTextured) hellStones.hideFromEMI().setCreativeTab(null);

        underGrass = (BlockUnderGrass) underGrass.hideFromEMI().setCreativeTab(null);
        underrock        = underrock.hideFromEMI().setCreativeTab(null);
        understoneSmooth = understoneSmooth.hideFromEMI().setCreativeTab(null);
        underCobble      = underCobble.hideFromEMI().setCreativeTab(null);
    }
}