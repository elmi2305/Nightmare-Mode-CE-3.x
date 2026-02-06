package com.itlesports.nightmaremode.block;

import btw.block.BTWBlocks;
import btw.block.blocks.BedrockBlock;
import btw.item.items.StoneItem;
import com.itlesports.nightmaremode.block.blocks.*;
import com.itlesports.nightmaremode.block.tileEntities.*;
import com.itlesports.nightmaremode.item.ItemVillagerContainer;
import com.itlesports.nightmaremode.item.itemblock.ItemBlockTallFlower;
import com.itlesports.nightmaremode.item.itemblock.NMItemBlock;
import net.minecraft.src.*;

public class NMBlocks {
    public static Block steelOre;
    public static BloodBoneBlock bloodBones;
    public static BlockCryingObsidian cryingObsidian;
    public static BedrockBlock specialObsidian;
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
    public static BlockMultiTextured underDirt;
    public static BlockMultiTextured flowerGrass;
    public static BlockMultiTextured flowerDirt;

    public static Block yellowFlowerRoots;
    public static Block plantMatter;
    public static Block disenchantmentTable;
    public static Block mushroomStem;
    public static Block mushroomFloorPartialYellow;
    public static Block mushroomPurple;
    public static Block mushInnardsBreakable;
    public static Block mushInnardsBreakableExplosive;
    public static Block mushroomTopFloorYellow;
    public static Block mushroomFloorYellow;
    public static Block mushroomWallYellow;
    public static Block mushroomFloorPartialPurple;
    public static Block mushroomWallPurple;
    public static Block mushroomTopFloorPurple;
    public static Block mushroomCapYellow;
    public static Block mushroomCapWhite;

    public static Block mushBookshelf;
    public static Block darkSandstone;
    public static Block bloodsidian;


    public static void initNightmareBlocks(){
        steelOre = (new SteelOre(2305)).setHardness(13.0F).setResistance(200.0F).setStepSound(BTWBlocks.oreStepSound).setUnlocalizedName("nmSteelOre").setTextureName("nightmare:steel_ore");
        StoneItem.itemsList[steelOre.blockID] = new NMItemBlock(NMBlocks.steelOre.blockID - 256);

        bloodBones = (BloodBoneBlock) new BloodBoneBlock(2306).setHardness(4f).setTextureName("nightmare:nmBloodBone").setUnlocalizedName("nmBloodBone");
        Item.itemsList[bloodBones.blockID] = new NMItemBlock(NMBlocks.bloodBones.blockID - 256);

        cryingObsidian = (BlockCryingObsidian) new BlockCryingObsidian(2307).setTextureName("nightmare:nmCryingObsidian").setUnlocalizedName("nmCryingObsidian").setHardness(-1.0F).setResistance(6000000.0F).setCreativeTab(CreativeTabs.tabBlock);
        Item.itemsList[cryingObsidian.blockID] = new NMItemBlock(NMBlocks.cryingObsidian.blockID - 256);

        specialObsidian = ((BedrockBlock) new BedrockBlock(2308).setTextureName("nightmare:nmSpecialObsidian").setUnlocalizedName("nmSpecialObsidian").setHardness(-1.0F).setResistance(6000000.0F).setCreativeTab(CreativeTabs.tabBlock));
        Item.itemsList[specialObsidian.blockID] = new NMItemBlock(NMBlocks.specialObsidian.blockID - 256);

        underworldPortal = (BlockUnderworldPortal) new BlockUnderworldPortal(2309).setUnlocalizedName("underworld_portal").setTextureName("nightmare:underworld_portal");
        Item.itemsList[underworldPortal.blockID] = new NMItemBlock(NMBlocks.underworldPortal.blockID - 256);

        bloodChest = (BlockBloodChest) new BlockBloodChest(2310, 0).setTextureName("nightmare:chestBlood").setUnlocalizedName("nmBloodChest");
        Item.itemsList[bloodChest.blockID] = new NMItemBlock(NMBlocks.bloodChest.blockID - 256);
        TileEntity.addMapping(TileEntityBloodChest.class, "BloodChest");

        steelLocker = (BlockSteelLocker) new BlockSteelLocker(2355, 1).setTextureName("nightmare:lockerSteel").setUnlocalizedName("nmSteelLocker");
        Item.itemsList[steelLocker.blockID] = new NMItemBlock(NMBlocks.steelLocker.blockID - 256);
        TileEntity.addMapping(TileEntitySteelLocker.class, "SteelLocker");

        blockRoad = (BlockRoad) new BlockRoad(2311, 1.5f).setUnlocalizedName("nmRoad").setTextureName("nightmare:nmRoad");
        Item.itemsList[blockRoad.blockID] = new NMItemBlock(NMBlocks.blockRoad.blockID - 256);

        blockAsphalt =  (BlockRoad) new BlockRoad(2312, 1.85f).setUnlocalizedName("nmAsphalt").setTextureName("nightmare:nmAsphalt");
        Item.itemsList[blockAsphalt.blockID] = new NMItemBlock(NMBlocks.blockAsphalt.blockID - 256);

        stoneLadder = (BlockCustomLadder) new BlockCustomLadder(2313, 1.5f).setUnlocalizedName("nmStoneLadder").setTextureName("nightmare:nmStoneLadder");
        Item.itemsList[stoneLadder.blockID] = new NMItemBlock(NMBlocks.stoneLadder.blockID - 256);

        ironLadder = (BlockCustomLadder) new BlockCustomLadder(2314, 2.2f).setUnlocalizedName("nmIronLadder").setTextureName("nightmare:nmIronLadder");
        Item.itemsList[ironLadder.blockID] = new NMItemBlock(NMBlocks.ironLadder.blockID - 256);


        hellforge = new BlockHellforge(2315, true);
        Item.itemsList[hellforge.blockID] = new NMItemBlock(NMBlocks.hellforge.blockID - 256);
        TileEntity.addMapping(HellforgeTileEntity.class, "Hellforge");


        customWickerBasket = new CustomBasketBlock(2316);
        Item.itemsList[customWickerBasket.blockID] = new NMItemBlock(NMBlocks.customWickerBasket.blockID - 256);
        TileEntity.addMapping(CustomBasketTileEntity.class, "CustomBasket");

        asphaltLayer = (BlockAsphaltCarpet) new BlockAsphaltCarpet(2318).setHardness(2f).setUnlocalizedName("nmAsphaltLayer").setCreativeTab(CreativeTabs.tabDecorations);
        Item.itemsList[asphaltLayer.blockID] = new NMItemBlock(NMBlocks.asphaltLayer.blockID - 256);


        bloodSaw = (BlockBloodSaw) new BlockBloodSaw(2319).setHardness(8f);
        Item.itemsList[bloodSaw.blockID] = new NMItemBlock(NMBlocks.bloodSaw.blockID - 256);

        villagerBlock = new BlockVillagerBase(2320);
        Item.itemsList[villagerBlock.blockID] = new ItemVillagerContainer(NMBlocks.villagerBlock.blockID - 256);
        TileEntity.addMapping(TileEntityVillagerContainer.class, "VillagerContainer");


        underrock = new NMBlock(2321, Material.rock).setTextureName("nightmare:nmUnderworldRock").setUnlocalizedName("nmUnderworldRock").setCreativeTab(CreativeTabs.tabBlock).setHardness(50f).setResistance(300f);
        Item.itemsList[underrock.blockID] = new NMItemBlock(NMBlocks.underrock.blockID - 256);

        understoneSmooth = new NMBlock(2322, Material.rock).setTextureName("nightmare:nmUnderworldRockSmooth").setUnlocalizedName("nmUnderworldRockSmooth").setCreativeTab(CreativeTabs.tabBlock).setHardness(20f).setResistance(10f);
        Item.itemsList[understoneSmooth.blockID] = new NMItemBlock(NMBlocks.understoneSmooth.blockID - 256);

        underCobble = new NMBlock(2323, Material.rock).setTextureName("nightmare:nmUnderworldRockCobble").setUnlocalizedName("nmUnderworldRockCobble").setCreativeTab(CreativeTabs.tabBlock).setHardness(10f).setResistance(5f);
        Item.itemsList[underCobble.blockID] = new NMItemBlock(NMBlocks.underCobble.blockID - 256);

        underGrass = new BlockUnderGrass(2324, Material.grass);
        Item.itemsList[underGrass.blockID] = new NMItemBlock(NMBlocks.underGrass.blockID - 256);

        underDirt = (BlockMultiTextured) new BlockMultiTextured(2325, Material.grass, "nightmare:blight_level_4_roots").setUnlocalizedName("nmUnderDirt");
        Item.itemsList[underDirt.blockID] = new NMItemBlock(NMBlocks.underDirt.blockID - 256);

        flowerGrass = ((BlockMultiTextured) new BlockMultiTextured(2326, Material.grass, "nightmare:nmFlowerGrassTop", "nightmare:nmFlowerDirt", "nightmare:nmFlowerGrassSide").setUnlocalizedName("nmFlowerGrass")).setGrowsVegetation(true);
        Item.itemsList[flowerGrass.blockID] = new NMItemBlock(NMBlocks.flowerGrass.blockID - 256);

        flowerDirt = ((BlockMultiTextured) new BlockMultiTextured(2327, Material.grass, "nightmare:nmFlowerDirt").setUnlocalizedName("nmFlowerDirt")).setGrowsVegetation(true);
        Item.itemsList[flowerDirt.blockID] = new NMItemBlock(NMBlocks.flowerDirt.blockID - 256);

        yellowFlowerRoots = new BlockTallFlower(2328);
        Item.itemsList[yellowFlowerRoots.blockID] = new ItemBlockTallFlower(NMBlocks.yellowFlowerRoots.blockID - 256);

        plantMatter = new NMBlock(2329, Material.wood).setTextureName("nightmare:nmPlantMatter").setUnlocalizedName("nmPlantMatter").setCreativeTab(CreativeTabs.tabBlock);
        Item.itemsList[plantMatter.blockID] = new NMItemBlock(NMBlocks.plantMatter.blockID - 256);

        disenchantmentTable = new BlockDisenchantmentTable(2330).setTextureName("nightmare:nmDisenchantmentTable").setUnlocalizedName("nmDisenchantmentTable").setCreativeTab(CreativeTabs.tabBlock);
        Item.itemsList[disenchantmentTable.blockID] = new NMItemBlock(NMBlocks.disenchantmentTable.blockID - 256);
        TileEntity.addMapping(TileEntityDisenchantmentTable.class, "TileEntityDisenchantmentTable");

        mushroomStem = new BlockMultiTextured(2331, Material.rock, "nightmare:nmMushStem").setHardness(-1f).setResistance(1000f).setUnlocalizedName("nmMushStem");
        Item.itemsList[mushroomStem.blockID] = new NMItemBlock(NMBlocks.mushroomStem.blockID - 256);

        mushroomFloorPartialYellow = new BlockMultiTextured(2332, Material.rock, "nightmare:nmMushFloorYellow", "nightmare:nmMushStem", "nightmare:nmMushFloorYellow").setHardness(-1f).setResistance(1000f).setUnlocalizedName("mushroomFloorPartial");
        Item.itemsList[mushroomFloorPartialYellow.blockID] = new NMItemBlock(NMBlocks.mushroomFloorPartialYellow.blockID - 256);

        mushroomPurple = new BlockMultiTextured(2333, Material.rock, "nightmare:nmMushPurple").setHardness(-1f).setResistance(1000f).setUnlocalizedName("mushroomFloor");
        Item.itemsList[mushroomPurple.blockID] = new NMItemBlock(NMBlocks.mushroomPurple.blockID - 256);

        mushInnardsBreakable = new BlockMultiTextured(2334, Material.rock, "nightmare:nmMushStemBreakable").setHardness(5f).setResistance(10f).setUnlocalizedName("mushInnardsBreakable");
        Item.itemsList[mushInnardsBreakable.blockID] = new NMItemBlock(NMBlocks.mushInnardsBreakable.blockID - 256);

        mushInnardsBreakableExplosive = new BlockMultiTextured(2335, Material.rock, "nightmare:nmMushStemBreakable").setIsExplosive(true).setHardness(2f).setResistance(0f).setUnlocalizedName("mushInnardsBreakableExplosive");
        Item.itemsList[mushInnardsBreakableExplosive.blockID] = new NMItemBlock(NMBlocks.mushInnardsBreakableExplosive.blockID - 256);

        mushroomTopFloorYellow = new BlockMultiTextured(2336, Material.rock, "nightmare:nmMushPurple", "nightmare:nmMushInnardsYellow", "nightmare:nmMushPurple").setHardness(-1f).setResistance(1000f).setUnlocalizedName("mushroomFloorSecond");
        Item.itemsList[mushroomTopFloorYellow.blockID] = new NMItemBlock(NMBlocks.mushroomTopFloorYellow.blockID - 256);

        mushroomFloorYellow = new BlockMultiTextured(2337, Material.rock, "nightmare:nmMushYellow").setHardness(-1f).setResistance(1000f).setUnlocalizedName("mushroomFloorYellow");
        Item.itemsList[mushroomFloorYellow.blockID] = new NMItemBlock(NMBlocks.mushroomFloorYellow.blockID - 256);

        mushroomWallYellow = new BlockMultiTextured(2338, Material.rock, "nightmare:nmMushYellow").setHardness(-1f).setResistance(1000f).setUnlocalizedName("mushroomWall");
        Item.itemsList[mushroomWallYellow.blockID] = new NMItemBlock(NMBlocks.mushroomWallYellow.blockID - 256);

        mushroomFloorPartialPurple = new BlockMultiTextured(2339, Material.rock, "nightmare:nmMushPurple", "nightmare:nmMushStem", "nightmare:nmMushPurple").setHardness(-1f).setResistance(1000f).setUnlocalizedName("mushroomFloorPartial");
        Item.itemsList[mushroomFloorPartialPurple.blockID] = new NMItemBlock(NMBlocks.mushroomFloorPartialPurple.blockID - 256);

        mushroomWallPurple = new BlockMultiTextured(2340, Material.rock, "nightmare:nmMushPurple").setHardness(-1f).setResistance(1000f).setUnlocalizedName("nmMushWallPurple");
        Item.itemsList[mushroomWallPurple.blockID] = new NMItemBlock(NMBlocks.mushroomWallPurple.blockID - 256);

        mushroomTopFloorPurple = new BlockMultiTextured(2341, Material.rock, "nightmare:nmMushPurple", "nightmare:nmMushPurple", "nightmare:nmMushPurple").setHardness(-1f).setResistance(1000f).setUnlocalizedName("nmMushTopPurple");
        Item.itemsList[mushroomTopFloorPurple.blockID] = new NMItemBlock(NMBlocks.mushroomTopFloorPurple.blockID - 256);

        mushroomCapYellow = new BlockMultiTextured(2342, Material.rock, "nightmare:nmMushCapYellow").setHardness(-1f).setResistance(1000f).setUnlocalizedName("nmMushCapYellow");
        Item.itemsList[mushroomCapYellow.blockID] = new NMItemBlock(NMBlocks.mushroomCapYellow.blockID - 256);

        mushroomCapWhite = new BlockMultiTextured(2343, Material.rock, "nightmare:nmMushCapWhite").setHardness(-1f).setResistance(1000f).setUnlocalizedName("nmMushCapWhite");
        Item.itemsList[mushroomCapWhite.blockID] = new NMItemBlock(NMBlocks.mushroomCapWhite.blockID - 256);

        mushBookshelf = new BlockMushBookshelf(2344).setHardness(-1f).setResistance(10f).setUnlocalizedName("nmMushBookshelf").setTextureName("nightmare:nmMushBookshelf");
        Item.itemsList[mushBookshelf.blockID] = new NMItemBlock(NMBlocks.mushBookshelf.blockID - 256);

//        darkSandstone = new BlockMultiTextured(2345, Material.rock, "nightmare:nmSandstone_top", "nightmare:nmSandstone_bottom", "nightmare:nmSandstone_side").setHardness(10).setResistance(10f).setUnlocalizedName("nmDarkSandstone");
//        Item.itemsList[darkSandstone.blockID] = new NMItemBlock(NMBlocks.darkSandstone.blockID - 256);

//        bloodsidian = new BlockMultiTextured(2346, Material.rock, "nightmare:nmBloodsidian").setHardness()
    }
}
