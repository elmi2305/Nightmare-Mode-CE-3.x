package com.itlesports.nightmaremode.rendering;

import com.itlesports.nightmaremode.block.tileEntities.TileEntityHammerAnvil;
import com.itlesports.nightmaremode.util.HammerAnvilAnimation;
import com.itlesports.nightmaremode.util.HammerAnvilHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;

@Environment(EnvType.CLIENT)
public final class TileEntityHammerAnvilRenderer extends TileEntitySpecialRenderer {
    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTicks) {
        if (!(tile instanceof TileEntityHammerAnvil anvil) || tile.isInvalid()
                || !HammerAnvilHelper.isAnvil(tile.worldObj.getBlockId(tile.xCoord, tile.yCoord, tile.zCoord))) return;
        HammerAnvilAnimation animation = anvil.getAnimation();
        if (animation == null) return;

        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();
        try {
            GL11.glTranslated(x + 0.5D, y, z + 0.5D);
            GL11.glRotatef(90.0F * ((tile.getBlockMetadata() & 3) + 1), 0, 1, 0);
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
            int light = tile.worldObj.getLightBrightnessForSkyBlocks(tile.xCoord, tile.yCoord + 1, tile.zCoord, 0);

            GL11.glPushMatrix();
            GL11.glTranslatef(0, 1.015F, 0);
            GL11.glRotatef(-90, 1, 0, 0);
            drawSprite(animation.input, -0.24F, -0.24F, 0.48F, light);
            GL11.glPopMatrix();

            float phase = animation.getSwingProgress(partialTicks);
            // a quick eased downstroke, brief contact, then a slower recovery.
            float strike = phase < 0.35F ? smooth(phase / 0.35F)
                    : phase < 0.45F ? 1 : 1 - smooth((phase - 0.45F) / 0.55F);
            GL11.glTranslatef(-0.43F, 1.21F, 0);
            GL11.glRotatef(45.0F - 110.0F * strike, 0, 0, 1);
            drawSprite(animation.hammer, -0.08F, -0.08F, 0.65F, light);
        } finally {
            GL11.glPopMatrix();
            GL11.glPopAttrib();
        }
    }

    private static float smooth(float value) {
        return value * value * (3 - 2 * value);
    }

    private void drawSprite(ItemStack stack, float x, float y, float size, int light) {
        this.bindTexture(stack.getItemSpriteNumber() == 0 ? TextureMap.locationBlocksTexture : TextureMap.locationItemsTexture);
        int passes = stack.getItem().requiresMultipleRenderPasses() ? 2 : 1;
        for (int pass = 0; pass < passes; ++pass) {
            Icon icon = passes == 1 ? stack.getIconIndex()
                    : stack.getItem().getIconFromDamageForRenderPass(stack.getItemDamage(), pass);
            if (icon == null) continue;
            int color = stack.getItem().getColorFromItemStack(stack, pass);
            Tessellator tessellator = Tessellator.instance;
            tessellator.startDrawingQuads();
            tessellator.setBrightness(light);
            tessellator.setColorOpaque_I(color);
            tessellator.addVertexWithUV(x, y, 0, icon.getMinU(), icon.getMaxV());
            tessellator.addVertexWithUV(x + size, y, 0, icon.getMaxU(), icon.getMaxV());
            tessellator.addVertexWithUV(x + size, y + size, 0, icon.getMaxU(), icon.getMinV());
            tessellator.addVertexWithUV(x, y + size, 0, icon.getMinU(), icon.getMinV());
            tessellator.draw();
        }
    }
}
