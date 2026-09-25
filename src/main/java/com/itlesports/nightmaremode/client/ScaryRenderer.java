package com.itlesports.nightmaremode.client;

import com.itlesports.nightmaremode.scary.ScaryEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;

@Environment(EnvType.CLIENT)
public final class ScaryRenderer extends Gui {
    public static final int EYE_PANEL_ROWS = 18;
    private static final ScaryRenderer INSTANCE = new ScaryRenderer();
    private static final ResourceLocation TAB_EYE = new ResourceLocation("nightmare:textures/effects/scary_tab_eye.png");
    private static final ResourceLocation ACHIEVEMENT = new ResourceLocation("textures/gui/achievement/achievement_background.png");
    private static final ResourceLocation STEVE = new ResourceLocation("textures/entity/steve.png");
    private static final RenderItem ITEM_RENDERER = new RenderItem();

    private ScaryRenderer() {}

    public static void overlay() {
        ScaryEvent event = ScaryEvents.active();
        boolean eyePanel = ScaryEvents.eyePanelActive();
        float stare = ScaryEvents.stareIntensity();
        float glitch = ScaryEvents.stareGlitch();
        if (event == null && !eyePanel && stare == 0) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.currentScreen != null) return;
        ScaledResolution resolution = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
        int width = resolution.getScaledWidth();
        int height = resolution.getScaledHeight();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        try {
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glDisable(GL11.GL_LIGHTING);
            if (stare > 0) renderStareVignette(width, height, stare);
            if (eyePanel) {
                // match vanilla's single-column panel (150 wide, 9 pixels per row, top at 10).
                // the panel itself is drawn by guiingame; only its contents are replaced here.
                int panelWidth = 150;
                int panelHeight = EYE_PANEL_ROWS * 9;
                double pulse = 1 + .009 * Math.sin(ScaryEvents.now() * .004);
                double imageSize = Math.min(panelWidth - 8, panelHeight - 8) * pulse;
                double imageLeft = (width - panelWidth) / 2 + (panelWidth - imageSize) / 2;
                double imageTop = 10 + (panelHeight - imageSize) / 2;
                mc.getTextureManager().bindTexture(TAB_EYE);
                GL11.glEnable(GL11.GL_TEXTURE_2D);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                GL11.glDisable(GL11.GL_ALPHA_TEST);
                GL11.glDepthMask(false);
                GL11.glColor4f(1, 1, 1, .72f);
                quad(imageLeft, imageTop, imageSize, imageSize);
                if (glitch > 0) {
                    // displaced translucent eye fragments accompany the screen interference.
                    GL11.glColor4f(.5f, .8f, .85f, glitch * .19f);
                    quad(imageLeft + 5 * glitch, imageTop, imageSize, imageSize);
                    GL11.glColor4f(.65f, .2f, .2f, glitch * .16f);
                    quad(imageLeft - 7 * glitch, imageTop + 1, imageSize, imageSize);
                }
            }
            if (glitch > 0) renderStareGlitch(width, height, glitch);
            if (event == ScaryEvent.ACHIEVEMENT) {
                double phase = ScaryEvents.elapsed() / 3000.0;
                double edge = Math.max(0, 1 - Math.min(phase, 1 - phase) * 8);
                int x = width - 160;
                int y = -(int)(Math.pow(edge, 4) * 36);
                GL11.glEnable(GL11.GL_TEXTURE_2D);
                GL11.glColor4f(1, 1, 1, 1);
                mc.getTextureManager().bindTexture(ACHIEVEMENT);
                INSTANCE.drawTexturedModalRect(x, y, 96, 202, 160, 32);
                mc.fontRenderer.drawString(I18n.getString("achievement.get"), x + 30, y + 7, 0xffff00);
                // long titles fit the same reserved text area without running offscreen.
                GL11.glPushMatrix();
                GL11.glTranslatef(x + 30, y + 18, 0);
                float scale = Math.min(1, 125f / mc.fontRenderer.getStringWidth(ScaryEvents.title()));
                GL11.glScalef(scale, scale, 1);
                mc.fontRenderer.drawString(ScaryEvents.title(), 0, 0, 0xffffff);
                GL11.glPopMatrix();
                RenderHelper.enableGUIStandardItemLighting();
                ITEM_RENDERER.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.getTextureManager(), new ItemStack(Item.eyeOfEnder), x + 8, y + 8);
            }
            if (event == ScaryEvent.STEAM_NOTIFICATION) renderSteamNotification(mc, width, height);
        } finally {
            GL11.glPopAttrib();
        }
    }

    private static void renderSteamNotification(Minecraft mc, int width, int height) {
        double elapsed = ScaryEvents.elapsed();
        double entry = Math.min(1, elapsed / 320.0);
        double exit = Math.min(1, Math.max(0, (elapsed - 4400.0) / 450.0));
        double visible = Math.min(entry, 1 - exit);
        visible = visible * visible * (3 - 2 * visible);
        int x = width - 210;
        int y = height - 50 + (int)((1 - visible) * 52);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        drawRect(x, y, x + 210, y + 50, 0xF010151C);
        drawRect(x + 1, y + 1, x + 209, y + 47, 0xF01B222C);
        drawRect(x, y + 48, x + 210, y + 50, 0xFF4B5862);
        drawRect(x + 7, y + 7, x + 42, y + 42, 0xFF52616D);
        drawRect(x + 8, y + 8, x + 41, y + 41, 0xFF2C4150);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        mc.getTextureManager().bindTexture(STEVE);
        GL11.glColor4f(1, 1, 1, 1);
        INSTANCE.drawSkinFace(x + 9, y + 9, 31, 8, 8);
        String sender = ScaryEvents.steamSender();
        mc.fontRenderer.drawStringWithShadow(sender == null ? "Herobrine" : sender, x + 51, y + 13, 0xECEEF0);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        drawRect(x + 52, y + 29, x + 59, y + 35, 0xFF9AA4AF);
        drawRect(x + 53, y + 35, x + 55, y + 37, 0xFF9AA4AF);
        drawRect(x + 54, y + 32, x + 55, y + 33, 0xFF202731);
        drawRect(x + 56, y + 32, x + 57, y + 33, 0xFF202731);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        String message = ScaryEvents.steamMessage();
        mc.fontRenderer.drawStringWithShadow(mc.fontRenderer.trimStringToWidth(message == null ? "..." : message, 140),
                x + 64, y + 28, 0xD1D4D8);
    }

    private void drawSkinFace(int x, int y, int size, int u, int v) {
        Tessellator t = Tessellator.instance;
        t.startDrawingQuads();
        t.addVertexWithUV(x, y + size, 0, u / 64.0, (v + 8) / 64.0);
        t.addVertexWithUV(x + size, y + size, 0, (u + 8) / 64.0, (v + 8) / 64.0);
        t.addVertexWithUV(x + size, y, 0, (u + 8) / 64.0, v / 64.0);
        t.addVertexWithUV(x, y, 0, u / 64.0, v / 64.0);
        t.draw();
    }

    private static void renderStareVignette(int width, int height, float intensity) {
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDepthMask(false);
        Tessellator t = Tessellator.instance;
        t.startDrawingQuads();
        for (int y = 0; y < 18; y++) {
            for (int x = 0; x < 28; x++) {
                for (int corner = 0; corner < 4; corner++) {
                    double u = (x + (corner == 1 || corner == 2 ? 1 : 0)) / 28.0;
                    double v = (y + (corner >= 2 ? 1 : 0)) / 18.0;
                    double radius = Math.sqrt(Math.pow((u - .5) * 2, 2) + Math.pow((v - .5) * 2, 2));
                    double edge = Math.max(0, Math.min(1, (radius - .25) / .85));
                    double fade = edge * edge * (3 - 2 * edge);
                    t.setColorRGBA(10, 0, 2, (int)(225 * fade * intensity));
                    t.addVertex(u * width, v * height, 0);
                }
            }
        }
        t.draw();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(1, 1, 1, 1);
    }

    private static void renderStareGlitch(int width, int height, float intensity) {
        // short, dim horizontal dropouts; no full-screen flashes or persistent render targets.
        long burst = ScaryEvents.now() / 2300;
        java.util.Random noise = new java.util.Random(burst);
        int dark = ((int)(85 * intensity) << 24) | 0x030507;
        int split = ((int)(40 * intensity) << 24) | 0x527c80;
        for (int band = 0; band < 3; band++) {
            int y = noise.nextInt(Math.max(1, height));
            int thickness = 1 + noise.nextInt(3);
            drawRect(0, y, width, Math.min(height, y + thickness), dark);
            int x = noise.nextInt(Math.max(1, width));
            drawRect(x, y, Math.min(width, x + width / 4), Math.min(height, y + 1), split);
        }
        for (int i = 0; i < 16; i++) {
            int x = noise.nextInt(Math.max(1, width));
            int y = noise.nextInt(Math.max(1, height));
            drawRect(x, y, Math.min(width, x + 2 + noise.nextInt(12)), Math.min(height, y + 1), split);
        }
    }

    private static void quad(double x, double y, double width, double height) {
        Tessellator t = Tessellator.instance;
        t.startDrawingQuads();
        t.addVertexWithUV(x, y + height, 0, 0, 1);
        t.addVertexWithUV(x + width, y + height, 0, 1, 1);
        t.addVertexWithUV(x + width, y, 0, 1, 0);
        t.addVertexWithUV(x, y, 0, 0, 0);
        t.draw();
    }

    public static void figure(float partialTicks) {
        if (ScaryEvents.active() != ScaryEvent.FIGURE || ScaryEvents.figure() == null) return;
        Vec3 position = ScaryEvents.figure();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();
        try {
            GL11.glTranslated(position.xCoord - RenderManager.renderPosX, position.yCoord - RenderManager.renderPosY,
                    position.zCoord - RenderManager.renderPosZ);
            GL11.glRotatef(-ScaryEvents.figureYaw(), 0, 1, 0);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glDisable(GL11.GL_FOG);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glColor4f(0, 0, 0, 1);
            // smooth anatomical masses form a tall stationary t-pose; nothing is added to the world.
            ellipsoid(0, 2.5, 0, .18, .25, .17);
            ellipsoid(0, 2.22, 0, .105, .15, .10);
            ellipsoid(0, 1.92, 0, .31, .37, .17);
            ellipsoid(0, 1.57, 0, .22, .30, .14);
            ellipsoid(0, 1.30, 0, .25, .22, .17);
            for (int side : new int[]{-1, 1}) {
                ellipsoid(side * .16, .96, 0, .13, .40, .14);
                ellipsoid(side * .18, .39, 0, .09, .34, .10);
                ellipsoid(side * .18, .08, -.09, .10, .08, .20);
                ellipsoid(side * .52, 2.12, 0, .31, .105, .11);
                ellipsoid(side * .99, 2.12, 0, .27, .073, .08);
                ellipsoid(side * 1.29, 2.12, 0, .13, .047, .085);
            }
        } finally {
            GL11.glPopMatrix();
            GL11.glPopAttrib();
        }
    }

    private static void ellipsoid(double x, double y, double z, double rx, double ry, double rz) {
        Tessellator t = Tessellator.instance;
        t.startDrawingQuads();
        for (int latitude = 0; latitude < 10; latitude++) {
            for (int longitude = 0; longitude < 16; longitude++) {
                for (int corner = 0; corner < 4; corner++) {
                    double a = Math.PI * ((latitude + (corner >= 2 ? 1 : 0)) / 10.0 - .5);
                    double b = Math.PI * 2 * (longitude + (corner == 1 || corner == 2 ? 1 : 0)) / 16.0;
                    t.addVertex(x + rx * Math.cos(a) * Math.cos(b), y + ry * Math.sin(a), z + rz * Math.cos(a) * Math.sin(b));
                }
            }
        }
        t.draw();
    }
}
