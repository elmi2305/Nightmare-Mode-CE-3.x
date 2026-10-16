package com.itlesports.nightmaremode.rendering;

import com.itlesports.nightmaremode.block.tileEntities.TileEntityHammerAnvil;
import com.itlesports.nightmaremode.util.HammerAnvilAnimation;
import com.itlesports.nightmaremode.util.HammerAnvilHelper;
import com.itlesports.nightmaremode.util.AutoAnvilUpgrade;
import com.itlesports.nightmaremode.item.NMItems;
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
        if (animation == null && !anvil.isAutomatic()) return;

        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();
        try {
            GL11.glTranslated(x + 0.5D, y, z + 0.5D);
            GL11.glRotatef(90.0F * (tile.getBlockMetadata() & 3), 0, 1, 0);
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
            int light = tile.worldObj.getLightBrightnessForSkyBlocks(tile.xCoord, tile.yCoord + 1, tile.zCoord, 0);
            if (anvil.isAutomatic()) this.drawUpgradeOverlay(anvil.getUpgrade(), light);
            GL11.glRotatef(90, 0, 1, 0);

            ItemStack input = anvil.isAutomatic() ? anvil.getStackInSlot(TileEntityHammerAnvil.INPUT_SLOT) : animation.input;
            ItemStack hammer = anvil.isAutomatic() ? anvil.getStackInSlot(TileEntityHammerAnvil.HAMMER_SLOT) : animation.hammer;
            if (anvil.getUpgrade() == AutoAnvilUpgrade.PHASE) hammer = new ItemStack(NMItems.steelHammer);

            if (input != null) {
                GL11.glPushMatrix();
                GL11.glTranslatef(0, 1.015F, 0);
                GL11.glRotatef(-90, 1, 0, 0);
                drawSprite(input, -0.24F, -0.24F, 0.48F, light);
                GL11.glPopMatrix();
            }

            float phase = anvil.isAutomatic() ? anvil.isAutomaticActive()
                    ? ((anvil.getProcessTicks() + partialTicks) % anvil.getUpgrade().ticksPerHit) / anvil.getUpgrade().ticksPerHit : 0
                    : animation.getSwingProgress(partialTicks);
            // a quick eased downstroke, brief contact, then a slower recovery.
            float strike = phase < 0.35F ? smooth(phase / 0.35F)
                    : phase < 0.45F ? 1 : 1 - smooth((phase - 0.45F) / 0.55F);
            GL11.glTranslatef(-0.43F, 1.21F, 0);
            GL11.glRotatef(45.0F - 110.0F * strike, 0, 0, 1);
            if (hammer != null && !anvil.isHammerHidden()) drawSprite(hammer, -0.08F, -0.08F, 0.65F, light);
        } finally {
            GL11.glPopMatrix();
            GL11.glPopAttrib();
        }
    }

    private void drawUpgradeOverlay(AutoAnvilUpgrade upgrade, int light) {
        this.bindTexture(new ResourceLocation("nightmare:textures/blocks/" + upgrade.textureName() + "Overlay.png"));
        // these four shells follow the btw anvil model, leaving the original material visible.
        double[][] boxes = {
                {0.125, 0, 0.125, 0.875, 0.25, 0.875},
                {0.25, 0.25, 0.1875, 0.75, 0.3125, 0.8125},
                {0.375, 0.3125, 0.25, 0.625, 0.625, 0.75},
                {0.1875, 0.625, 0, 0.8125, 1, 1}
        };
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.setBrightness(light);
        tessellator.setColorOpaque_I(0xFFFFFF);
        for (double[] box : boxes) {
            double x0 = box[0] - 0.501, y0 = box[1] - 0.001, z0 = box[2] - 0.501;
            double x1 = box[3] - 0.499, y1 = box[4] + 0.001, z1 = box[5] - 0.499;
            overlayFace(tessellator, x0,y0,z0, x1,y0,z0, x1,y1,z0, x0,y1,z0);
            overlayFace(tessellator, x1,y0,z1, x0,y0,z1, x0,y1,z1, x1,y1,z1);
            overlayFace(tessellator, x0,y0,z1, x0,y0,z0, x0,y1,z0, x0,y1,z1);
            overlayFace(tessellator, x1,y0,z0, x1,y0,z1, x1,y1,z1, x1,y1,z0);
            overlayFace(tessellator, x0,y1,z0, x1,y1,z0, x1,y1,z1, x0,y1,z1);
            overlayFace(tessellator, x0,y0,z1, x1,y0,z1, x1,y0,z0, x0,y0,z0);
        }
        tessellator.draw();
    }

    private static void overlayFace(Tessellator t, double x0,double y0,double z0, double x1,double y1,double z1,
                                    double x2,double y2,double z2, double x3,double y3,double z3) {
        t.addVertexWithUV(x0,y0,z0, 0,1);
        t.addVertexWithUV(x1,y1,z1, 1,1);
        t.addVertexWithUV(x2,y2,z2, 1,0);
        t.addVertexWithUV(x3,y3,z3, 0,0);
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
