package com.itlesports.nightmaremode.mixin.blocks;

import btw.block.BTWBlocks;
import btw.community.nightmaremode.NightmareMode;
import btw.world.util.BlockPos;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(BlockLeaves.class)
public class BlockLeavesMixin extends BlockLeavesBase {
    @Shadow protected int[][][] adjacentTreeBlocks3D;

    public BlockLeavesMixin(int par1, Material par2Material, boolean par3) {
        super(par1, par2Material, par3);
    }


    @Environment(value= EnvType.CLIENT)
    @Inject(method = "getBlockColor", at = @At(value = "RETURN"), cancellable = true)
    private void redLeaves0(CallbackInfoReturnable<Integer> cir){
        if(NightmareMode.crimson){
            cir.setReturnValue(14163743);
        }
    }

    @Inject(method = "idDropped", at= @At("RETURN"),cancellable = true)
    private void allowAppleDrops(int metadata, Random rand, int fortuneModifier, CallbackInfoReturnable<Integer> cir){
        if(rand.nextInt(100000) == 0){
            cir.setReturnValue(Item.appleRed.itemID);
        }
    }

    @Environment(value= EnvType.CLIENT)
    @Inject(method = "getRenderColor", at = @At(value = "RETURN"), cancellable = true)
    private void redLeaves1(CallbackInfoReturnable<Integer> cir){
        if(NightmareMode.crimson){
            cir.setReturnValue(14163743);
        }
    }
    @Environment(value= EnvType.CLIENT)
    @Inject(method = "colorMultiplier", at = @At(value = "RETURN"), cancellable = true)
    private void redLeaves2(CallbackInfoReturnable<Integer> cir){
        if(NightmareMode.crimson){
            cir.setReturnValue(14163743);
        }
    }

    @Override
    public int tickRate(World par1World) {
        return 4;
    }

    @Redirect(method = "updateTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/BlockLeaves;updateAdjacentTreeBlockArray(Lnet/minecraft/src/World;III)V"))
    private void updateTreeArrayButFixed(BlockLeaves leaf, World world, int x, int y, int z){
        for (int i = -4; i <= 4; ++i) {
            for (int j = -4; j <= 4; ++j) {
                for (int k = -4; k <= 4; ++k) {
                    int blockID = world.getBlockId(x + i, y + j, z + k);
                    Block block = Block.blocksList[blockID];

                    this.adjacentTreeBlocks3D[i + 16][j + 16][k + 16] =
                            block != null && (block.canSupportLeaves(world, x + i, y + j, z + k) && !   this.getBlockIsStump(block, world, x + i, y + j, z + k)) ? 0 :
                                    (block != null && block.isLeafBlock(world, x + i, y + j, z + k) ? -2 : -1);
                }
            }
        }

        for (int distance = 1; distance <= 4; ++distance) {
            for (int i = -4; i <= 4; ++i) {
                for (int j = -4; j <= 4; ++j) {
                    for (int k = -4; k <= 4; ++k) {
                        if (this.adjacentTreeBlocks3D[i + 16][j + 16][k + 16] == distance - 1) {
                            if (this.adjacentTreeBlocks3D[i + 16 - 1][j + 16][k + 16] == -2) {
                                this.adjacentTreeBlocks3D[i + 16 - 1][j + 16][k + 16] = distance;
                            }
                            if (this.adjacentTreeBlocks3D[i + 16 + 1][j + 16][k + 16] == -2) {
                                this.adjacentTreeBlocks3D[i + 16 + 1][j + 16][k + 16] = distance;
                            }
                            if (this.adjacentTreeBlocks3D[i + 16][j + 16 - 1][k + 16] == -2) {
                                this.adjacentTreeBlocks3D[i + 16][j + 16 - 1][k + 16] = distance;
                            }
                            if (this.adjacentTreeBlocks3D[i + 16][j + 16 + 1][k + 16] == -2) {
                                this.adjacentTreeBlocks3D[i + 16][j + 16 + 1][k + 16] = distance;
                            }
                            if (this.adjacentTreeBlocks3D[i + 16][j + 16][k + 16 - 1] == -2) {
                                this.adjacentTreeBlocks3D[i + 16][j + 16][k + 16 - 1] = distance;
                            }
                            if (this.adjacentTreeBlocks3D[i + 16][j + 16][k + 16 + 1] == -2) {
                                this.adjacentTreeBlocks3D[i + 16][j + 16][k + 16 + 1] = distance;
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean getBlockIsStump(Block block, World world, int x, int y, int z) {
        return block instanceof BlockLog && ((BlockLog) block).getIsStump(world.getBlockMetadata(x, y, z));
    }
}
