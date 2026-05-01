package com.itlesports.nightmaremode.block.blocks;

import api.block.util.Flammability;
import btw.block.BTWBlocks;
import com.itlesports.nightmaremode.block.NMBlocks;
import net.minecraft.src.*;

public class BlockSteelFrame extends Block {

    public BlockSteelFrame(int id) {
        super(id, Material.rock);
//        setHardness(1.5F); // Reduced hardness to allow fire ignition
//        setResistance(10.0F);
        this.setFireProperties(Flammability.NONE);
        this.setFireProperties(0,0);
        setStepSound(soundMetalFootstep);
        setUnlocalizedName("nmSteelFrame");
        setTextureName("nightmare:steel_frame");
    }

    @Override
    public boolean getCanBeSetOnFireDirectly(IBlockAccess blockAccess, int i, int j, int k) {
        return true;
    }

    @Override
    public boolean getCanBeSetOnFireDirectlyByItem(IBlockAccess blockAccess, int i, int j, int k) {
        return false;
    }



    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, int neighborBlockID) {
        // check if fire is placed adjacent
        if (neighborBlockID == Block.fire.blockID) {
            // check all adjacent positions for fire
            int[][] offsets = {{0, -1, 0}, {0, 1, 0}, {0, 0, -1}, {0, 0, 1}, {-1, 0, 0}, {1, 0, 0}};
            for (int[] offset : offsets) {
                int dx = x + offset[0];
                int dy = y + offset[1];
                int dz = z + offset[2];

                if (world.getBlockId(dx, dy, dz) == Block.fire.blockID) {
                    // try to create portal
                    if (tryToCreatePortal(world, dx, dy, dz)) {
                        return;
                    }
                }
            }
        }
        super.onNeighborBlockChange(world, x, y, z, neighborBlockID);
    }

    private boolean tryToCreatePortal(World world, int fireX, int fireY, int fireZ) {
        int xDistPos = 0;
        int xDistNeg = 0;
        int zDistPos = 0;
        int zDistNeg = 0;

        for (int i2 = 0; i2 < 22; ++i2) {
            if (isFrameBlock(world, fireX + i2, fireY, fireZ) && xDistPos == 0) {
                xDistPos = i2;
            }
            if (isFrameBlock(world, fireX - i2, fireY, fireZ) && xDistNeg == 0) {
                xDistNeg = -i2;
            }
            if (isFrameBlock(world, fireX, fireY, fireZ + i2) && zDistPos == 0) {
                zDistPos = i2;
            }
            if (!isFrameBlock(world, fireX, fireY, fireZ - i2) || zDistNeg != 0) continue;
            zDistNeg = -i2;
        }

        int xDiff = xDistPos - xDistNeg + 1;
        int zDiff = zDistPos - zDistNeg + 1;

        if (xDiff < 4 && zDiff < 4 || xDiff > 23 && zDiff > 23 || xDiff > 23 && zDiff < 4 || zDiff > 23 && xDiff < 4) {
            return false;
        }

        int isX = 0;
        int isZ = 0;
        if (xDistPos != 0 && xDistNeg != 0) {
            zDistPos = 0;
            zDistNeg = 0;
            isX = 1;
        } else if (zDistPos != 0 && zDistNeg != 0) {
            xDistPos = 0;
            xDistNeg = 0;
            isZ = 1;
        }

        int yDist = 0;
        for (int i3 = 3; i3 < 22; ++i3) {
            if (!isFrameBlock(world, fireX, fireY + i3, fireZ)) continue;
            yDist = i3 + 1;
            break;
        }

        if (yDist == 0) {
            return false;
        }

        int lowerBound = xDistNeg + zDistNeg;
        int upperBound = xDistPos + zDistPos;

        for (int i = lowerBound; i <= upperBound; ++i) {
            for (int j = -1; j < yDist; ++j) {
                int id = world.getBlockId(fireX + isX * i, fireY + j, fireZ + isZ * i);
                if ((i == lowerBound || i == upperBound) && (j == -1 || j == yDist - 1) || !(i == lowerBound || i == upperBound || j == -1 || j == yDist - 1 ? !isFrameBlock(world, fireX + isX * i, fireY + j, fireZ + isZ * i) : !world.isAirBlock(fireX + isX * i, fireY + j, fireZ + isZ * i) && id != Block.fire.blockID && id != BTWBlocks.largeCampfire.blockID && id != BTWBlocks.mediumCampfire.blockID && id != BTWBlocks.smallCampfire.blockID && id != BTWBlocks.unlitCampfire.blockID)) continue;
                return false;
            }
        }

        for (int i = lowerBound + 1; i < upperBound; ++i) {
            for (int j = 0; j < yDist - 1; ++j) {
                world.setBlock(fireX + isX * i, fireY + j, fireZ + isZ * i, NMBlocks.underworldPortal.blockID, 0, 2);
            }
        }

        world.playSoundEffect(fireX, fireY, fireZ, "mob.ghast.scream", 1.0F, 1.0F);
        return true;
    }

    private boolean isFrameBlock(World world, int x, int y, int z) {
        int blockID = world.getBlockId(x, y, z);
        return blockID == BTWBlocks.soulforgedSteelBlock.blockID || blockID == this.blockID;
    }
}