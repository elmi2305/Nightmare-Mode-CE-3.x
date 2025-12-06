package com.itlesports.nightmaremode.mixin.blocks;

import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.block.blocks.NMBlock;
import net.minecraft.src.Block;
import net.minecraft.src.BlockFire;
import net.minecraft.src.Material;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockFire.class)
public abstract class BlockFireMixin extends Block {
    protected BlockFireMixin(int par1, Material par2Material) {
        super(par1, par2Material);
    }

    @Shadow protected abstract boolean canNeighborBurn(World par1World, int par2, int par3, int par4);

    @Inject(method = "onBlockAdded", at = @At("TAIL"))
    private void checkForCrudePortalToLight(World par1World, int par2, int par3, int par4, CallbackInfo ci){
        if (par1World.provider.dimensionId > 0 || par1World.getBlockId(par2, par3 - 1, par4) != NMBlocks.crudeObsidian.blockID || !Block.portal.tryToCreatePortal(par1World, par2, par3, par4)) {
            if (!par1World.doesBlockHaveSolidTopSurface(par2, par3 - 1, par4) && !this.canNeighborBurn(par1World, par2, par3, par4)) {
                par1World.setBlockToAir(par2, par3, par4);
            } else {
                par1World.scheduleBlockUpdate(par2, par3, par4, this.blockID, this.tickRate(par1World) + par1World.rand.nextInt(10));
            }
        }
    }
}
