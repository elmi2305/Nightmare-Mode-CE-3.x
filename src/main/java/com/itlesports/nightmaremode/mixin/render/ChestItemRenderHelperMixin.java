package com.itlesports.nightmaremode.mixin.render;

import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.block.tileEntities.TileEntityBloodChest;
import net.minecraft.src.Block;
import net.minecraft.src.ChestItemRenderHelper;
import net.minecraft.src.TileEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChestItemRenderHelper.class)
public class ChestItemRenderHelperMixin {
    @Unique private TileEntityBloodChest bloodChest = new TileEntityBloodChest(1);

    @Inject(method = "renderChest", at = @At("HEAD"),cancellable = true)
    private void renderBloodChestItem(Block block, int f, float par3, CallbackInfo ci){
        if(block.blockID == NMBlocks.bloodChest.blockID){
            TileEntityRenderer.instance.renderTileEntityAt(this.bloodChest, 0.0, 0.0, 0.0, 0.0f);
            ci.cancel();
        }
    }
}
