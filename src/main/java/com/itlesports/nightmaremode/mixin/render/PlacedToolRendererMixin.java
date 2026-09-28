package com.itlesports.nightmaremode.mixin.render;

import btw.block.tileentity.PlacedToolTileEntity;
import btw.client.render.tileentity.PlacedToolRenderer;
import com.itlesports.nightmaremode.util.NMPlacedItemHelper;
import net.minecraft.src.Icon;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Minecraft;
import net.minecraft.src.RenderManager;
import net.minecraft.src.Tessellator;
import net.minecraft.src.TextureMap;
import net.minecraft.src.TileEntity;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlacedToolRenderer.class)
public class PlacedToolRendererMixin {
    @Inject(method = "renderTileEntityAt", at = @At("HEAD"), cancellable = true)
    private void renderCup(TileEntity tile, double x, double y, double z, float partialTicks, CallbackInfo ci) {
        ItemStack stack = ((PlacedToolTileEntity)tile).getToolStack();
        if (stack == null || !NMPlacedItemHelper.isCup(stack.getItem())) return;
        Icon icon = stack.getIconIndex();
        if (icon == null) return;

        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationItemsTexture);
        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glTranslated(x + 0.5D, y + 0.04D, z + 0.5D);
        GL11.glRotatef(180.0F - RenderManager.instance.playerViewY, 0.0F, 1.0F, 0.0F);

        double halfWidth = 0.35D;
        double height = 0.70D;
        Tessellator tess = Tessellator.instance;
        tess.startDrawingQuads();
        tess.setNormal(0.0F, 0.0F, 1.0F);
        tess.addVertexWithUV(-halfWidth, 0.0D, 0.0D, icon.getMinU(), icon.getMaxV());
        tess.addVertexWithUV(halfWidth, 0.0D, 0.0D, icon.getMaxU(), icon.getMaxV());
        tess.addVertexWithUV(halfWidth, height, 0.0D, icon.getMaxU(), icon.getMinV());
        tess.addVertexWithUV(-halfWidth, height, 0.0D, icon.getMinU(), icon.getMinV());
        tess.draw();

        GL11.glPopAttrib();
        GL11.glPopMatrix();
        ci.cancel();
    }
}
