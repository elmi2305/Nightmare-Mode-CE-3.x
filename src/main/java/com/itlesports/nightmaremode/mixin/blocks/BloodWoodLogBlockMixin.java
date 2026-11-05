package com.itlesports.nightmaremode.mixin.blocks;

import btw.block.BTWBlocks;
import btw.block.blocks.BloodWoodLogBlock;
import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.block.blocks.BlockUnderworldLeaves;
import com.itlesports.nightmaremode.util.NMFields;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BloodWoodLogBlock.class)
public class BloodWoodLogBlockMixin {
    @Redirect(method = "growLeaves", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;setBlockAndMetadataWithNotify(IIIII)Z"))
    private boolean placeUnderworldBloodLeaves(World world, int x, int y, int z, int id, int metadata) {
        if (world.provider.dimensionId == NMFields.UNDERWORLD_DIMENSION && id == BTWBlocks.bloodWoodLeaves.blockID) {
            return world.setBlockAndMetadataWithNotify(x, y, z, NMBlocks.underworldLeaves.blockID, BlockUnderworldLeaves.BLOOD);
        }
        return world.setBlockAndMetadataWithNotify(x, y, z, id, metadata);
    }

    @Inject(method = "isBloodLeafBlock", at = @At("HEAD"), cancellable = true)
    private void recognizeUnderworldBloodLeaves(World world, int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
        if (world.provider.dimensionId == NMFields.UNDERWORLD_DIMENSION
                && world.getBlockId(x, y, z) == NMBlocks.underworldLeaves.blockID
                && world.getBlockMetadata(x, y, z) == BlockUnderworldLeaves.BLOOD) {
            cir.setReturnValue(true);
        }
    }
}
