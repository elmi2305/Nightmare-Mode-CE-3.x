package com.itlesports.nightmaremode.client;

import btw.community.nightmaremode.NightmareMode;
import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;

public final class FrozenWastesHud {
    private static final ResourceLocation OUTLINE = texture("powder_snow_outline");
    private static final ResourceLocation[] HEARTS = {
            texture("frozen_full"), texture("frozen_half"),
            texture("frozen_full_blinking"), texture("frozen_half_blinking"),
            texture("frozen_hardcore_full"), texture("frozen_hardcore_half"),
            texture("frozen_hardcore_full_blinking"), texture("frozen_hardcore_half_blinking")
    };

    private FrozenWastesHud() {}

    private static ResourceLocation texture(String name) {
        return new ResourceLocation("nightmare:textures/gui/frozen/" + name + ".png");
    }

    private static int exposure(Minecraft mc) {
        if (mc.thePlayer == null || mc.theWorld == null || mc.thePlayer.dimension != 0
                || !mc.thePlayer.isEntityAlive() || mc.thePlayer.capabilities.disableDamage) return 0;
        return mc.thePlayer.getData(NightmareMode.OUTER_COLD_EXPOSURE);
    }

    public static void renderOverlay(Minecraft mc) {
        int cold = exposure(mc);
        if (cold <= 0) return;
        ScaledResolution resolution = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_CURRENT_BIT);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, Math.min(1.0F, cold / 600.0F));
        mc.getTextureManager().bindTexture(OUTLINE);
        drawTexture(0, 0, resolution.getScaledWidth(), resolution.getScaledHeight(), -90.0F);
        mc.getTextureManager().bindTexture(Gui.icons);
        GL11.glPopAttrib();
    }

    public static boolean drawFrozenHeart(Minecraft mc, int x, int y, int u, int v, int width, int height, float z) {
        if (exposure(mc) < 300 || width != 9 || height != 9 || (v != 0 && v != 45)
                || mc.thePlayer.isPotionActive(Potion.poison) || mc.thePlayer.isPotionActive(Potion.wither)) return false;
        // preserve vanilla positioning, empty containers, absorption and damage flashes.
        int variant = switch (u) {
            case 52 -> 0;
            case 61 -> 1;
            case 70 -> 2;
            case 79 -> 3;
            default -> -1;
        };
        if (variant < 0) return false;
        mc.getTextureManager().bindTexture(HEARTS[variant + (v == 45 ? 4 : 0)]);
        drawTexture(x, y, width, height, z);
        mc.getTextureManager().bindTexture(Gui.icons);
        return true;
    }

    private static void drawTexture(int x, int y, int width, int height, float z) {
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(x, y + height, z, 0.0D, 1.0D);
        tessellator.addVertexWithUV(x + width, y + height, z, 1.0D, 1.0D);
        tessellator.addVertexWithUV(x + width, y, z, 1.0D, 0.0D);
        tessellator.addVertexWithUV(x, y, z, 0.0D, 0.0D);
        tessellator.draw();
    }
}
