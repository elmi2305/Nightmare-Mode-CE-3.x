package com.itlesports.nightmaremode.block.blocks;

import btw.block.BTWBlocks;
import java.util.List;
import java.util.Random;

import com.itlesports.nightmaremode.block.NMBlocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.Block;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntitySilverfish;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.Icon;
import net.minecraft.src.IconRegister;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Material;
import net.minecraft.src.World;

public class BlockZiggurathBrick
        extends Block {

    public BlockZiggurathBrick(int par1) {
        super(par1, Material.rock);
        this.setCreativeTab(CreativeTabs.tabBlock);
        this.setBlockUnbreakable();
        this.setResistance(6000000.0f);
        this.setPicksEffectiveOn();
        this.setStepSound(BTWBlocks.stoneBrickStepSound);
        this.setUnlocalizedName("ifhyZiggurathBrick");
        this.setTextureName("btw:stonebrick_strata_3");
        this.setTickRandomly(true);
    }

    @Override
    public int idDropped(int iMetadata, Random rand, int iFortuneModifier) {
        return NMBlocks.ziggurathBrick.blockID;
    }

}


