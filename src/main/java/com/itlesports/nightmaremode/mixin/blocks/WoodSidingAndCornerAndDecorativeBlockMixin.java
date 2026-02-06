package com.itlesports.nightmaremode.mixin.blocks;

import api.block.blocks.SidingAndCornerAndDecorativeBlock;
import btw.block.blocks.WoodSidingAndCornerAndDecorativeBlock;
import btw.community.nightmaremode.NightmareMode;
import net.minecraft.src.Material;
import net.minecraft.src.StepSound;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Random;

@Mixin(WoodSidingAndCornerAndDecorativeBlock.class)
public class WoodSidingAndCornerAndDecorativeBlockMixin extends SidingAndCornerAndDecorativeBlock {

    public WoodSidingAndCornerAndDecorativeBlockMixin(int iBlockID, Material material, String sTextureName, float fHardness, float fResistance, StepSound stepSound, String name) {
        super(iBlockID, material, sTextureName, fHardness, fResistance, stepSound, name);
    }

    public boolean isFallingBlock() {
        return NightmareMode.noSkybases || super.isFallingBlock();
    }

    public void onBlockAdded(World world, int i, int j, int k) {
        if (NightmareMode.noSkybases) {
            this.scheduleCheckForFall(world, i, j, k);
        }
        super.onBlockAdded(world,i,j,k);
    }

    public void onNeighborBlockChange(World world, int i, int j, int k, int iNeighborBlockID) {
        if (NightmareMode.noSkybases) {
            this.scheduleCheckForFall(world, i, j, k);
        }
        super.onNeighborBlockChange(world,i,j,k,iNeighborBlockID);
    }

    public void updateTick(World world, int i, int j, int k, Random rand) {
        if (NightmareMode.noSkybases) {
            this.checkForFall(world, i, j, k);
        }
        super.updateTick(world,i,j,k,rand);
    }

    public int tickRate(World par1World) {
        if (NightmareMode.noSkybases) {
            return 4;
        }
        return super.tickRate(par1World);
    }
}
