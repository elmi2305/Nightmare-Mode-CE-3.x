package com.itlesports.nightmaremode.mixin.render;

import com.itlesports.nightmaremode.util.FenceGateShape;
import net.minecraft.src.Block;
import net.minecraft.src.BlockFenceGate;
import net.minecraft.src.RenderBlocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockFenceGate.class)
public abstract class FenceGateRendererMixin {
    @Inject(method = "renderBlock", at = @At("HEAD"), cancellable = true)
    private void renderCornerGate(RenderBlocks render, int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
        int mask = FenceGateShape.cornerMask(render.blockAccess, x, y, z);
        if (mask == 0) return;
        boolean open = BlockFenceGate.isFenceGateOpen(render.blockAccess.getBlockMetadata(x, y, z));
        render.setRenderAllFaces(true);
        try {
            for (FenceGateShape.Box box : FenceGateShape.parts(mask, open)) {
                render.setUVRotateTop(box.maxZ() - box.minZ() > box.maxX() - box.minX() ? 1 : 0);
                render.setRenderBounds(box.minX(), box.minY(), box.minZ(), box.maxX(), box.maxY(), box.maxZ());
                render.renderStandardBlock((Block)(Object)this, x, y, z);
            }
        } finally {
            render.setRenderAllFaces(false);
            render.setUVRotateTop(0);
            render.setRenderBounds(0, 0, 0, 1, 1, 1);
        }
        cir.setReturnValue(true);
    }
}
