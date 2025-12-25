package com.itlesports.nightmaremode.item;

import btw.block.BTWBlocks;
import net.minecraft.src.Block;
import net.minecraft.src.ItemBlock;
import net.minecraft.src.ItemStack;

public class ObsidianItemBlock extends ItemBlock {
    public ObsidianItemBlock(int par1) {
        super(par1);
        this.setHasSubtypes(true);
    }


    @Override
    public int getMetadata(int iItemDamage) {
        if(this.itemID == Block.obsidian.blockID){
            return iItemDamage;
        }
        return super.getMetadata(iItemDamage);
    }


    @Override
    public String getUnlocalizedName(ItemStack stack) {
        int meta = stack.getItemDamage();
        if(meta == 0){
            return ("tile.obsidian");
        } else{
            return ("tile.nmCrudeObsidian");
        }
    }



    @Override
    public int getBlockIDToPlace(int iItemDamage, int iFacing, float fClickX, float fClickY, float fClickZ) {
        if (iItemDamage == 2) {
            return BTWBlocks.soulforgedSteelBlock.blockID;
        }
        return super.getBlockIDToPlace(iItemDamage, iFacing, fClickX, fClickY, fClickZ);
    }
}
