package com.itlesports.nightmaremode.block;

import btw.block.BTWBlocks;
import btw.block.blocks.BedrockBlock;
import btw.item.items.StoneItem;
import com.itlesports.nightmaremode.block.blocks.*;
import com.itlesports.nightmaremode.block.tileEntities.TileEntityBloodChest;
import net.minecraft.src.*;

public class NMBlocks {
    public static Block steelOre;
    public static BloodBoneBlock bloodBones;
    public static BlockCryingObsidian cryingObsidian;
    public static BedrockBlock specialObsidian;
    public static BlockUnderworldPortal underworldPortal;
    public static BlockBloodChest bloodChest;
    public static BlockRoad blockRoad;
    public static BlockRoad blockAsphalt;
    public static BlockCustomLadder stoneLadder;
    public static BlockCustomLadder ironLadder;

    public static void initNightmareBlocks(){
        steelOre = (new SteelOre(2305)).setHardness(13.0F).setResistance(200.0F).setStepSound(BTWBlocks.oreStepSound).setUnlocalizedName("nmSteelOre").setTextureName("steel_ore");
        StoneItem.itemsList[steelOre.blockID] = new ItemBlock(NMBlocks.steelOre.blockID - 256);
        bloodBones = (BloodBoneBlock) new BloodBoneBlock(2306).setHardness(4f).setTextureName("nmBloodBone").setUnlocalizedName("nmBloodBone");
        Item.itemsList[bloodBones.blockID] = new ItemBlock(NMBlocks.bloodBones.blockID - 256);
        cryingObsidian = (BlockCryingObsidian) new BlockCryingObsidian(2307).setTextureName("nmCryingObsidian").setUnlocalizedName("nmCryingObsidian").setHardness(-1.0F).setResistance(6000000.0F).setCreativeTab(CreativeTabs.tabBlock);
        Item.itemsList[cryingObsidian.blockID] = new ItemBlock(NMBlocks.cryingObsidian.blockID - 256);
        specialObsidian = ((BedrockBlock) new BedrockBlock(2308).setTextureName("nmSpecialObsidian").setUnlocalizedName("nmSpecialObsidian").setHardness(-1.0F).setResistance(6000000.0F).setCreativeTab(CreativeTabs.tabBlock));
        Item.itemsList[specialObsidian.blockID] = new ItemBlock(NMBlocks.specialObsidian.blockID - 256);

        underworldPortal = new BlockUnderworldPortal(2309);
        Item.itemsList[underworldPortal.blockID] = new ItemBlock(NMBlocks.underworldPortal.blockID - 256);


        bloodChest = (BlockBloodChest) new BlockBloodChest(2310, 1).setTextureName("chestBlood");
        Item.itemsList[bloodChest.blockID] = new ItemBlock(NMBlocks.bloodChest.blockID - 256);
        TileEntity.addMapping(TileEntityBloodChest.class, "BloodChest");

        blockRoad = (BlockRoad) new BlockRoad(2311, 1.5f).setUnlocalizedName("nmRoad").setTextureName("nmRoad");
        Item.itemsList[blockRoad.blockID] = new ItemBlock(NMBlocks.blockRoad.blockID - 256);
        blockAsphalt =  (BlockRoad) new BlockRoad(2312, 1.85f).setUnlocalizedName("nmAsphalt").setTextureName("nmAsphalt");
        Item.itemsList[blockAsphalt.blockID] = new ItemBlock(NMBlocks.blockAsphalt.blockID - 256);

        stoneLadder = (BlockCustomLadder) new BlockCustomLadder(2313, 1.5f).setUnlocalizedName("nmStoneLadder").setTextureName("nmStoneLadder");
        Item.itemsList[stoneLadder.blockID] = new ItemBlock(NMBlocks.stoneLadder.blockID - 256);

        ironLadder = (BlockCustomLadder) new BlockCustomLadder(2314, 2.2f).setUnlocalizedName("nmIronLadder").setTextureName("nmIronLadder");
        Item.itemsList[ironLadder.blockID] = new ItemBlock(NMBlocks.ironLadder.blockID - 256);

    }
}
