package com.itlesports.nightmaremode.block;

import btw.block.BTWBlocks;
import btw.block.blocks.BedrockBlock;
import btw.block.blocks.FallingFullBlock;
import btw.block.blocks.FullBlock;
import btw.item.items.StoneItem;
import com.itlesports.nightmaremode.block.blocks.*;
import com.itlesports.nightmaremode.block.tileEntities.*;
import com.itlesports.nightmaremode.item.ItemVillagerContainer;
import com.itlesports.nightmaremode.item.NMItemBlock;
import com.itlesports.nightmaremode.mixin.render.BTWRenderMapperMixin;
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


    public static void initNightmareBlocks(){
        steelOre = (new SteelOre(2305)).setHardness(13.0F).setResistance(200.0F).setStepSound(BTWBlocks.oreStepSound).setUnlocalizedName("nmSteelOre").setTextureName("nightmare_mode:steel_ore");
        StoneItem.itemsList[steelOre.blockID] = new NMItemBlock(NMBlocks.steelOre.blockID - 256);

        bloodBones = (BloodBoneBlock) new BloodBoneBlock(2306).setHardness(4f).setTextureName("nightmare_mode:nmBloodBone").setUnlocalizedName("nmBloodBone");
        Item.itemsList[bloodBones.blockID] = new NMItemBlock(NMBlocks.bloodBones.blockID - 256);

        cryingObsidian = (BlockCryingObsidian) new BlockCryingObsidian(2307).setTextureName("nightmare_mode:nmCryingObsidian").setUnlocalizedName("nmCryingObsidian").setHardness(-1.0F).setResistance(6000000.0F).setCreativeTab(CreativeTabs.tabBlock);
        Item.itemsList[cryingObsidian.blockID] = new NMItemBlock(NMBlocks.cryingObsidian.blockID - 256);

        specialObsidian = ((BedrockBlock) new BedrockBlock(2308).setTextureName("nightmare_mode:nmSpecialObsidian").setUnlocalizedName("nmSpecialObsidian").setHardness(-1.0F).setResistance(6000000.0F).setCreativeTab(CreativeTabs.tabBlock));
        Item.itemsList[specialObsidian.blockID] = new NMItemBlock(NMBlocks.specialObsidian.blockID - 256);

        underworldPortal = (BlockUnderworldPortal) new BlockUnderworldPortal(2309).setUnlocalizedName("underworld_portal");
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



    }
}
