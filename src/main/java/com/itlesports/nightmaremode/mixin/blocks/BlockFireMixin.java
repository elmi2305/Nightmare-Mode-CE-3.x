package com.itlesports.nightmaremode.mixin.blocks;

import btw.block.BTWBlocks;
import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.block.blocks.BlockSteelFrame;
import net.minecraft.src.Block;
import net.minecraft.src.BlockFire;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockFire.class)
public class BlockFireMixin {

    @Inject(method = "onBlockAdded", at = @At("HEAD"), cancellable = true)
    private void onFireAdded(World world, int x, int y, int z, CallbackInfo ci) {
        int blockBelowID = world.getBlockId(x, y - 1, z);
        Block blockBelow = Block.blocksList[blockBelowID];

        if (blockBelow instanceof BlockSteelFrame) {
            BlockSteelFrame steelFrame = (BlockSteelFrame) blockBelow;
            if (tryCreatePortalFromFire(world, steelFrame, x, y, z)) {
                ci.cancel();
            }
        }
    }

    @ModifyVariable(method = "checkForFireSpreadToOneBlockLocation", at = @At(value = "STORE"), name = "spreadChance")
    private static int modifyFireSpreadChance(int spreadChance) {
        return 10000;
    }

    @Unique
    private boolean tryCreatePortalFromFire(World world, BlockSteelFrame steelFrame, int fireX, int fireY, int fireZ) {
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

    @Unique
    private boolean isFrameBlock(World world, int x, int y, int z) {
        int blockID = world.getBlockId(x, y, z);
        return blockID == BTWBlocks.soulforgedSteelBlock.blockID || blockID == NMBlocks.steelFrame.blockID;
    }
}