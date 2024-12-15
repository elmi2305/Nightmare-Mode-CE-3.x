package com.itlesports.nightmaremode.block;

import btw.block.BTWBlocks;
import com.itlesports.nightmaremode.block.blocks.SteelOre;
import net.minecraft.src.Block;
import net.minecraft.src.Item;
import net.minecraft.src.ItemBlock;

public class NMBlocks {
    public static Block steelOre;

    public static void initNightmareBlocks(){
        steelOre = (new SteelOre(2305)).setHardness(3.0F).setResistance(5.0F).setStepSound(BTWBlocks.oreStepSound).setUnlocalizedName("nmSteelOre").setTextureName("steel_ore");
        Item.itemsList[steelOre.blockID] = new ItemBlock(NMBlocks.steelOre.blockID - 256);
    }
}
