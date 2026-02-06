package com.itlesports.nightmaremode.mixin.blocks;

import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.util.NMUtils;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Random;

@Mixin(BlockLog.class)
public abstract class BlockLogMixin extends BlockRotatedPillar {
    @Shadow public abstract boolean getIsStump(int iMetadata);

    protected BlockLogMixin(int i, Material material) {
        super(i, material);
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

    @Override
    public boolean canSupportLeaves(IBlockAccess blockAccess, int x, int y, int z) {
        int iMetadata = blockAccess.getBlockMetadata(x, y, z);
        return super.canSupportLeaves(blockAccess, x, y, z) && !this.getIsStump(iMetadata);
    }

    @Override
    public boolean isBreakableBarricade(World world, int i, int j, int k, boolean adv) {
        return NMUtils.getWorldProgress() > 0;
    }
}
