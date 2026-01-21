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
    public static Block mushroomFloorPartial;
    public static Block mushroomFloor;
    public static Block mushInnardsBreakable;

    public static Block moltenGlass;

    public static void initNightmareBlocks(){
        steelOre = (new SteelOre(2305)).setHardness(13.0F).setResistance(200.0F).setStepSound(BTWBlocks.oreStepSound).setUnlocalizedName("nmSteelOre").setTextureName("nightmare_mode:steel_ore");
        StoneItem.itemsList[steelOre.blockID] = new NMItemBlock(NMBlocks.steelOre.blockID - 256);

        bloodBones = (BloodBoneBlock) new BloodBoneBlock(2306).setHardness(4f).setTextureName("nightmare_mode:nmBloodBone").setUnlocalizedName("nmBloodBone");
        Item.itemsList[bloodBones.blockID] = new NMItemBlock(NMBlocks.bloodBones.blockID - 256);

        cryingObsidian = (BlockCryingObsidian) new BlockCryingObsidian(2307).setTextureName("nightmare_mode:nmCryingObsidian").setUnlocalizedName("nmCryingObsidian").setHardness(-1.0F).setResistance(6000000.0F).setCreativeTab(CreativeTabs.tabBlock);
        Item.itemsList[cryingObsidian.blockID] = new NMItemBlock(NMBlocks.cryingObsidian.blockID - 256);

        specialObsidian = ((BedrockBlock) new BedrockBlock(2308).setTextureName("nightmare_mode:nmSpecialObsidian").setUnlocalizedName("nmSpecialObsidian").setHardness(-1.0F).setResistance(6000000.0F).setCreativeTab(CreativeTabs.tabBlock));
        Item.itemsList[specialObsidian.blockID] = new NMItemBlock(NMBlocks.specialObsidian.blockID - 256);

        underworldPortal = (BlockUnderworldPortal) new BlockUnderworldPortal(2309).setUnlocalizedName("underworld_portal").setTextureName("underworld_portal");
        Item.itemsList[underworldPortal.blockID] = new NMItemBlock(NMBlocks.underworldPortal.blockID - 256);

        bloodChest = (BlockBloodChest) new BlockBloodChest(2310, 0).setTextureName("nightmare_mode:chestBlood").setUnlocalizedName("nmBloodChest");
        Item.itemsList[bloodChest.blockID] = new NMItemBlock(NMBlocks.bloodChest.blockID - 256);
        TileEntity.addMapping(TileEntityBloodChest.class, "BloodChest");

        steelLocker = (BlockSteelLocker) new BlockSteelLocker(2355, 1).setTextureName("nightmare_mode:lockerSteel").setUnlocalizedName("nmSteelLocker");
        Item.itemsList[steelLocker.blockID] = new NMItemBlock(NMBlocks.steelLocker.blockID - 256);
        TileEntity.addMapping(TileEntitySteelLocker.class, "SteelLocker");

        blockRoad = (BlockRoad) new BlockRoad(2311, 1.5f).setUnlocalizedName("nmRoad").setTextureName("nightmare_mode:nmRoad");
        Item.itemsList[blockRoad.blockID] = new NMItemBlock(NMBlocks.blockRoad.blockID - 256);

        blockAsphalt =  (BlockRoad) new BlockRoad(2312, 1.85f).setUnlocalizedName("nmAsphalt").setTextureName("nightmare_mode:nmAsphalt");
        Item.itemsList[blockAsphalt.blockID] = new NMItemBlock(NMBlocks.blockAsphalt.blockID - 256);

        stoneLadder = (BlockCustomLadder) new BlockCustomLadder(2313, 1.5f).setUnlocalizedName("nmStoneLadder").setTextureName("nightmare_mode:nmStoneLadder");
        Item.itemsList[stoneLadder.blockID] = new NMItemBlock(NMBlocks.stoneLadder.blockID - 256);

        ironLadder = (BlockCustomLadder) new BlockCustomLadder(2314, 2.2f).setUnlocalizedName("nmIronLadder").setTextureName("nightmare_mode:nmIronLadder");
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


        underrock = new NMBlock(2321, Material.rock).setTextureName("nmUnderworldRock").setUnlocalizedName("nmUnderworldRock").setCreativeTab(CreativeTabs.tabBlock).setHardness(50f).setResistance(300f);
        Item.itemsList[underrock.blockID] = new NMItemBlock(NMBlocks.underrock.blockID - 256);

        understoneSmooth = new NMBlock(2322, Material.rock).setTextureName("nmUnderworldRockSmooth").setUnlocalizedName("nmUnderworldRockSmooth").setCreativeTab(CreativeTabs.tabBlock).setHardness(20f).setResistance(10f);
        Item.itemsList[understoneSmooth.blockID] = new NMItemBlock(NMBlocks.understoneSmooth.blockID - 256);

        underCobble = new NMBlock(2323, Material.rock).setTextureName("nmUnderworldRockCobble").setUnlocalizedName("nmUnderworldRockCobble").setCreativeTab(CreativeTabs.tabBlock).setHardness(10f).setResistance(5f);
        Item.itemsList[underCobble.blockID] = new NMItemBlock(NMBlocks.underCobble.blockID - 256);

        underGrass = new BlockUnderGrass(2324, Material.grass);
        Item.itemsList[underGrass.blockID] = new NMItemBlock(NMBlocks.underGrass.blockID - 256);

        underDirt = (BlockMultiTextured) new BlockMultiTextured(2325, Material.grass, "blight_level_4_roots").setUnlocalizedName("nmUnderDirt");
        Item.itemsList[underDirt.blockID] = new NMItemBlock(NMBlocks.underDirt.blockID - 256);

        flowerGrass = ((BlockMultiTextured) new BlockMultiTextured(2326, Material.grass, "nmFlowerGrassTop", "nmFlowerDirt", "nmFlowerGrassSide").setUnlocalizedName("nmFlowerGrass")).setGrowsVegetation(true);
        Item.itemsList[flowerGrass.blockID] = new NMItemBlock(NMBlocks.flowerGrass.blockID - 256);

        flowerDirt = ((BlockMultiTextured) new BlockMultiTextured(2327, Material.grass, "nmFlowerDirt").setUnlocalizedName("nmFlowerDirt")).setGrowsVegetation(true);
        Item.itemsList[flowerDirt.blockID] = new NMItemBlock(NMBlocks.flowerDirt.blockID - 256);

        yellowFlowerRoots = new BlockTallFlower(2328);
        Item.itemsList[yellowFlowerRoots.blockID] = new ItemBlockTallFlower(NMBlocks.yellowFlowerRoots.blockID - 256);

        plantMatter = new NMBlock(2329, Material.wood).setTextureName("nmPlantMatter").setUnlocalizedName("nmPlantMatter").setCreativeTab(CreativeTabs.tabBlock);
        Item.itemsList[plantMatter.blockID] = new NMItemBlock(NMBlocks.plantMatter.blockID - 256);

        disenchantmentTable = new BlockDisenchantmentTable(2330).setTextureName("nmDisenchantmentTable").setUnlocalizedName("nmDisenchantmentTable").setCreativeTab(CreativeTabs.tabBlock);
        Item.itemsList[disenchantmentTable.blockID] = new NMItemBlock(NMBlocks.disenchantmentTable.blockID - 256);
        TileEntity.addMapping(TileEntityDisenchantmentTable.class, "TileEntityDisenchantmentTable");

        mushroomStem = new BlockMultiTextured(2331, Material.wood, "nmMushStem").setHardness(-1f).setResistance(1000f).setUnlocalizedName("nmMushStem");
        Item.itemsList[mushroomStem.blockID] = new NMItemBlock(NMBlocks.mushroomStem.blockID - 256);

        mushroomFloorPartial = new BlockMultiTextured(2332, Material.wood, "nmMushFloor", "nmMushStem", "nmMushFloor").setHardness(-1f).setResistance(1000f).setUnlocalizedName("nmMushStem");
        Item.itemsList[mushroomFloorPartial.blockID] = new NMItemBlock(NMBlocks.mushroomFloorPartial.blockID - 256);

        mushroomFloor = new BlockMultiTextured(2333, Material.wood, "nmMushFloor").setHardness(-1f).setResistance(1000f).setUnlocalizedName("nmMushFloor");
        Item.itemsList[mushroomFloor.blockID] = new NMItemBlock(NMBlocks.mushroomFloor.blockID - 256);

        mushInnardsBreakable = new BlockMultiTextured(2334, Material.wood, "nmMushInnards").setHardness(5f).setResistance(10f).setUnlocalizedName("nmMushInnards");
        Item.itemsList[mushInnardsBreakable.blockID] = new NMItemBlock(NMBlocks.mushroomFloor.blockID - 256);

    }
}
