package com.itlesports.nightmaremode.mixin.render;

import com.itlesports.nightmaremode.util.StorageColor;
import com.itlesports.nightmaremode.util.interfaces.IColoredChest;
import net.minecraft.src.ModelChest;
import net.minecraft.src.TileEntityChest;
import net.minecraft.src.TileEntityChestRenderer;
import net.minecraft.src.ResourceLocation;
import net.minecraft.src.TileEntitySpecialRenderer;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TileEntityChestRenderer.class)
public class TileEntityChestRendererMixin {
    @Unique private TileEntityChest nightmareMode$renderingChest;

    @Inject(method = "renderTileEntityChestAt", at = @At("HEAD"))
    private void captureRenderedChest(TileEntityChest chest, double x, double y, double z, float partialTicks, CallbackInfo ci) {
        this.nightmareMode$renderingChest = chest;
    }

    @Redirect(method = "renderTileEntityChestAt", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/TileEntityChestRenderer;bindTexture(Lnet/minecraft/src/ResourceLocation;)V"))
    private void bindDyedChestTexture(TileEntityChestRenderer renderer, ResourceLocation original) {
        TileEntityChest chest = this.nightmareMode$renderingChest;
        IColoredChest coloredChest = (IColoredChest) chest;
        boolean dyed = coloredChest.nm$hasChestColor() || StorageColor.hasChestItemRenderColor()
                || StorageColor.hasChestRenderColorOverride();
        boolean doubleChest = chest.adjacentChestXPos != null || chest.adjacentChestZPosition != null;
        ((TileEntitySpecialRendererAccessor) renderer).nightmareMode$bindTexture(
                StorageColor.getTintableChestTexture(chest.getChestType(), doubleChest,
                        dyed, original));
    }

    @Redirect(method = "renderTileEntityChestAt", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/ModelChest;renderAll()V"))
    private void renderTintedChest(ModelChest model) {
        IColoredChest chest = (IColoredChest) this.nightmareMode$renderingChest;
        boolean dyed = chest.nm$hasChestColor() || StorageColor.hasChestItemRenderColor()
                || StorageColor.hasChestRenderColorOverride();
        int color = chest.nm$hasChestColor() ? chest.nm$getChestColor()
                : StorageColor.hasChestItemRenderColor() ? StorageColor.getChestItemRenderColor()
                : StorageColor.getChestRenderColorOverride();
        StorageColor.applyChestTint(color, dyed);
        model.chestLid.render(0.0625F);
        model.chestBelow.render(0.0625F);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        model.chestKnob.rotateAngleX = model.chestLid.rotateAngleX;
        model.chestKnob.render(0.0625F);
    }

    @Inject(method = "renderTileEntityChestAt", at = @At("TAIL"))
    private void resetChestTint(TileEntityChest chest, double x, double y, double z, float partialTicks, CallbackInfo ci) {
        this.nightmareMode$renderingChest = null;
    }
}
