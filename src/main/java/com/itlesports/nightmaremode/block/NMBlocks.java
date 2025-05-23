package com.itlesports.nightmaremode.block;

import btw.block.BTWBlocks;
import btw.block.blocks.BedrockBlock;
import btw.item.items.StoneItem;
import com.itlesports.nightmaremode.block.blocks.BlockCryingObsidian;
import com.itlesports.nightmaremode.block.blocks.BloodBoneBlock;
import com.itlesports.nightmaremode.block.blocks.SteelOre;
import net.minecraft.src.*;

public class NMBlocks {
    public static Block steelOre;
    public static BloodBoneBlock bloodBones;
    public static BlockCryingObsidian cryingObsidian;
    public static BedrockBlock specialObsidian;

    public static void initNightmareBlocks(){
        steelOre = (new SteelOre(2305)).setHardness(13.0F).setResistance(200.0F).setStepSound(BTWBlocks.oreStepSound).setUnlocalizedName("nmSteelOre").setTextureName("steel_ore");
        StoneItem.itemsList[steelOre.blockID] = new ItemBlock(NMBlocks.steelOre.blockID - 256);
        bloodBones = (BloodBoneBlock) new BloodBoneBlock(2306).setHardness(4f).setTextureName("nmBloodBone").setUnlocalizedName("nmBloodBone");
        Item.itemsList[bloodBones.blockID] = new ItemBlock(NMBlocks.bloodBones.blockID - 256);
        cryingObsidian = (BlockCryingObsidian) new BlockCryingObsidian(2307).setTextureName("nmCryingObsidian").setUnlocalizedName("nmCryingObsidian").setHardness(-1.0F).setResistance(6000000.0F).setCreativeTab(CreativeTabs.tabBlock);
        Item.itemsList[cryingObsidian.blockID] = new ItemBlock(NMBlocks.cryingObsidian.blockID - 256);
        specialObsidian = ((BedrockBlock) new BedrockBlock(2308).setTextureName("nmSpecialObsidian").setUnlocalizedName("nmSpecialObsidian").setHardness(-1.0F).setResistance(6000000.0F).setCreativeTab(CreativeTabs.tabBlock));
        Item.itemsList[specialObsidian.blockID] = new ItemBlock(NMBlocks.specialObsidian.blockID - 256);

    }
}
