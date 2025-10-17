package com.itlesports.nightmaremode.mixin.blocks;

import btw.community.nightmaremode.NightmareMode;
import net.minecraft.src.Block;
import net.minecraft.src.BlockTrapDoor;
import net.minecraft.src.Material;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Random;

@Mixin(BlockTrapDoor.class)
public class BlockTrapDoorMixin extends Block {
    protected BlockTrapDoorMixin(int par1, Material par2Material) {
        super(par1, par2Material);
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
