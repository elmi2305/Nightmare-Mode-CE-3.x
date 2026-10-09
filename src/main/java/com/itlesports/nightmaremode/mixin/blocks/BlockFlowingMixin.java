package com.itlesports.nightmaremode.mixin.blocks;

import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.block.blocks.CrystalTorchBlock;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockFlowing.class)
public abstract class BlockFlowingMixin extends BlockFluid {
    protected BlockFlowingMixin(int id, Material material) { super(id, material); }

    @Inject(method = "flowIntoBlock", at = @At("HEAD"), cancellable = true)
    private void submergeCrystalTorch(World world, int x, int y, int z, int waterMetadata, CallbackInfo ci) {
        if (this.blockMaterial == Material.water && world.getBlockId(x, y, z) == NMBlocks.crystalTorch.blockID) {
            if (((CrystalTorchBlock)NMBlocks.crystalTorch).submerge(world, x, y, z, waterMetadata)) ci.cancel();
        }
    }
}
