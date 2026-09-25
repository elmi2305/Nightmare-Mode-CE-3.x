package com.itlesports.nightmaremode.mixin.entity;

import net.minecraft.src.Block;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityFallingSand;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityFallingSand.class)
public abstract class EntityFallingSandMixin extends Entity {
    @Shadow public int blockID;
    @Shadow public int metadata;
    @Shadow protected boolean hasBlockBrokenOnLand;

    protected EntityFallingSandMixin(World world) {
        super(world);
    }

    @Inject(method = "attemptToReplaceBlockAtPosition", at = @At("HEAD"), cancellable = true)
    private void crushObstructionUnderLog(int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
        Block log = Block.blocksList[this.blockID];
        if (this.hasBlockBrokenOnLand || log == null || !log.isLog(this.worldObj, x, y, z)) return;

        int landingY = y;
        int belowId = this.worldObj.getBlockId(x, y - 1, z);
        if (belowId != 0 && !Block.blocksList[belowId].canSupportFallingBlocks(this.worldObj, x, y - 1, z)) {
            landingY = y - 1;
        } else {
            int destinationId = this.worldObj.getBlockId(x, y, z);
            if (destinationId == 0 || Block.blocksList[destinationId].canSupportFallingBlocks(this.worldObj, x, y, z)) return;
        }

        if (this.worldObj.setBlock(x, landingY, z, this.blockID, this.metadata, 3)) {
            log.onFinishFalling(this.worldObj, x, landingY, z, this.metadata);
            this.hasBlockBrokenOnLand = true;
        }
        cir.setReturnValue(false);
    }
}
