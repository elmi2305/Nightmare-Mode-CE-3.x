package com.itlesports.nightmaremode.block.blocks;

import btw.block.blocks.LadderBlock;
import btw.block.util.Flammability;
import com.itlesports.nightmaremode.block.NMBlocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.IconRegister;
import net.minecraft.src.World;

import java.util.Random;

public class BlockCustomLadder extends LadderBlock {
    private final double speedModifier;
    private final int dropItemID;
    public BlockCustomLadder(int iBlockID, double speedModifier) {
        super(iBlockID);
        this.setPicksEffectiveOn();
        this.setAxesEffectiveOn(false);
        this.setFireProperties(Flammability.NONE);
        this.setCreativeTab(CreativeTabs.tabDecorations);
        this.speedModifier = 0.2 * speedModifier;
        this.dropItemID = iBlockID;
    }
    @Override
    public int idDropped(int iMetadata, Random rand, int iFortuneModifier) {
        return this.dropItemID;
    }

    @Override
    public boolean setOnFireDirectly(World world, int i, int j, int k) {return false;}
    @Override
    public boolean getCanBeSetOnFireDirectly(IBlockAccess blockAccess, int i, int j, int k) {
        return false;
    }
    @Override
    public int getChanceOfFireSpreadingDirectlyTo(IBlockAccess blockAccess, int i, int j, int k) {
        return 0;
    }


    public double getSpeedModifier() {
        return speedModifier;
    }
    @Override
    @Environment(value= EnvType.CLIENT)
    public void registerIcons(IconRegister register) {
        if (this.isStoneLadder()) {
            this.blockIcon = register.registerIcon("nightmare_mode:nmStoneLadder");
        } else{
            this.blockIcon = register.registerIcon("nightmare_mode:nmIronLadder");
        }
    }

    private boolean isStoneLadder(){
        return this.blockID == NMBlocks.stoneLadder.blockID;
    }
}
