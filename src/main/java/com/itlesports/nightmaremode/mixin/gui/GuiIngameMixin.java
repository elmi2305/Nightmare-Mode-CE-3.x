package com.itlesports.nightmaremode.mixin.gui;

import btw.community.nightmaremode.NightmareMode;
import api.util.status.StatusEffect;
import com.itlesports.nightmaremode.util.NMConfUtils;
import com.itlesports.nightmaremode.util.NMSanityUtils;
import com.itlesports.nightmaremode.util.NMUtils;
import com.itlesports.nightmaremode.util.interfaces.IHorseTamingClient;
import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Random;

import static btw.community.nightmaremode.NightmareMode.CONFIGS_CREATED;
import static btw.community.nightmaremode.NightmareMode.SANITY;
import static com.itlesports.nightmaremode.util.NMSanityUtils.CRITICAL_SANITY;
import static com.itlesports.nightmaremode.util.NMSanityUtils.MAX_SANITY;

@Mixin(GuiIngame.class)
public class GuiIngameMixin extends Gui{
    @Final @Shadow private Minecraft mc;
    @Unique private int amountRendered = 0;

    @Inject(method = "renderGameOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/GuiIngame;renderModSpecificPlayerSightEffects()V"))
    private void renderUnderworldSanity(float partialTicks, boolean hasScreen, int mouseX, int mouseY, CallbackInfo ci){
        if(this.mc.thePlayer.dimension == NightmareMode.UNDERWORLD_DIMENSION){
            double sanity = this.mc.thePlayer.getData(SANITY);

            double sanityPercent = Math.min(sanity / MAX_SANITY, 1.0);
            double fillPercent = 1.0 - sanityPercent;

            // bar dimensions in pixels
            final int BAR_WIDTH = 81;
            final int BAR_HEIGHT = 9;
            final int TEXTURE_WIDTH = 256;
            final int TEXTURE_HEIGHT = 256;

            ScaledResolution scaledRes = new ScaledResolution(this.mc.gameSettings, this.mc.displayWidth, this.mc.displayHeight);
            int screenWidth = scaledRes.getScaledWidth();
            int screenHeight = scaledRes.getScaledHeight();

            // the position of the sanity meter
            int baseX = screenWidth / 2 - 91;
            int baseY = screenHeight - 49 - BAR_HEIGHT - 5;

            if(this.mc.thePlayer.capabilities.isCreativeMode){
                baseY += 24;
            } else if(this.mc.thePlayer.getTotalArmorValue() == 0){
                baseY += 10;
            }

            // shake at high sanity
            int shakeX = 0;
            int shakeY = 0;
            if (sanity > CRITICAL_SANITY) {
                long time = System.currentTimeMillis();
                double shakeIntensity = ((sanity - CRITICAL_SANITY) / (MAX_SANITY - CRITICAL_SANITY)) * 2.0;
                shakeX = (int)(Math.sin(time * 0.05) * shakeIntensity);
                shakeY = (int)(Math.cos(time * 0.07) * shakeIntensity);
            }

            int barX = baseX + shakeX;
            int barY = baseY + shakeY;

            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);



                // render background bar
            this.mc.renderEngine.bindTexture(new ResourceLocation("nightmare", "textures/gui/sanity_background.png"));
            Tessellator tessellator = Tessellator.instance;

            tessellator.startDrawingQuads();
            tessellator.addVertexWithUV(barX, barY + BAR_HEIGHT, 0, 0.0F, 1.0F);
            tessellator.addVertexWithUV(barX + BAR_WIDTH, barY + BAR_HEIGHT, 0, 1.0F, 1.0F);
            tessellator.addVertexWithUV(barX + BAR_WIDTH, barY, 0, 1.0F, 0.0F);
            tessellator.addVertexWithUV(barX, barY, 0, 0.0F, 0.0F);
            tessellator.draw();


                // render the actual sanity fill
            // drains from right to left
            int fillWidth = (int)(BAR_WIDTH * fillPercent);

            if (fillWidth > 0) {
                float[] color = calculateSanityColor(sanity, partialTicks);

                this.mc.renderEngine.bindTexture(new ResourceLocation("nightmare", "textures/gui/sanity_fill.png"));

                GL11.glColor4f(color[0], color[1], color[2], 1.0F);

                // render fill (from left, width determined by fillPercent)
                renderClippedTexture(barX, barY, 0, 0, fillWidth, BAR_HEIGHT, BAR_WIDTH, BAR_HEIGHT, TEXTURE_WIDTH, TEXTURE_HEIGHT);

                // sparkles
                if (fillPercent > 0.1) {
                    renderSparkles(barX, barY, fillWidth, BAR_HEIGHT, sanityPercent, partialTicks);
                }

                // glow effect
                if (sanity > (MAX_SANITY / 2)) {
                    renderInsanityGlow(barX, barY, fillWidth, BAR_HEIGHT, sanity, partialTicks);
                }
            }
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

                // render border around everything
            int offset = 5;
            int borderWidth = BAR_WIDTH + (offset * 2);  // 91
            int borderHeight = BAR_HEIGHT + (offset * 2); // 19

            this.mc.renderEngine.bindTexture(new ResourceLocation("nightmare", "textures/gui/sanity_border.png"));

            int x = barX - offset;
            int y = barY - offset;
            int z = 0;

            float uMin = 0.0F;
            float uMax = 1.0F;
            float vMin = 0.0F;
            float vMax = 1.0F;

            tessellator.startDrawingQuads();
            tessellator.addVertexWithUV(x, y + borderHeight, z, uMin, vMax);
            tessellator.addVertexWithUV(x + borderWidth, y + borderHeight, z, uMax, vMax);
            tessellator.addVertexWithUV(x + borderWidth, y, z, uMax, vMin);
            tessellator.addVertexWithUV(x, y, z, uMin, vMin);
            tessellator.draw();

            GL11.glDisable(GL11.GL_BLEND);
        }
    }


    /**
     * Calculate color based on sanity level with smooth transitions
     */
    @Unique
    private float[] calculateSanityColor(double sanity, float partialTicks) {
        float r, g, b;

        if (sanity < (MAX_SANITY / 4)) {
            // 75% - 100% sanity, astral blue
            r = 0.3F;
            g = 0.7F;
            b = 1.0F;
        } else if (sanity < (MAX_SANITY / 2)) {
            // 75% - 50%, blue/cyan
            float progress = (float)((sanity - (MAX_SANITY / 4)) / (MAX_SANITY / 4));
            r = 0.3F + (0.5F * progress); // 0.3 -> 0.8
            g = 0.7F + (0.2F * progress); // 0.7 → 0.9
            b = 1.0F;
        } else if (sanity < (MAX_SANITY * 3 / 4)) {
            // 50% - 25% sanity, purple
            float progress = (float)((sanity - (MAX_SANITY / 2)) / (MAX_SANITY / 4));
            r = 0.8F + (0.1F * progress); // 0.8 → 0.9
            g = 0.9F - (0.5F * progress); // 0.9 → 0.4
            b = 1.0F - (0.1F * progress); // 1.0 → 0.9
        } else {
            // 25% - 0% sanity, deep red
            float progress = (float)Math.min((sanity - (MAX_SANITY * 3 / 4)) / (MAX_SANITY / 4), 1.0);
            float pulse = (float)(Math.sin((System.currentTimeMillis() + partialTicks * 50) * 0.01) * 0.15 + 0.85);

            r = (0.9F + (0.1F * progress)) * pulse; // 0.9 → 1.0
            g = (0.4F - (0.3F * progress)) * pulse; // 0.4 → 0.1
            b = (0.9F - (0.6F * progress)) * pulse; // 0.9 → 0.3
        }

        return new float[]{r, g, b};
    }

    /**
     * Render texture with clipping for the drain effect
     */
    @Unique
    private void renderClippedTexture(int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight, int textureSheetWidth, int textureSheetHeight) {
        Tessellator tessellator = Tessellator.instance;

        float uMin = (float)u / textureSheetWidth;
        float uMax = (float)(u + textureWidth) / textureSheetWidth;
        float vMin = (float)v / textureSheetHeight;
        float vMax = (float)(v + textureHeight) / textureSheetHeight;

        // adjust uMax for clipping (bar drains from right)
        float uvWidth = (uMax - uMin) * ((float)width / textureWidth);
        uMax = uMin + uvWidth;

        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(x, y + height, 0.0D, uMin, vMax);
        tessellator.addVertexWithUV(x + width, y + height, 0.0D, uMax, vMax);
        tessellator.addVertexWithUV(x + width, y, 0.0D, uMax, vMin);
        tessellator.addVertexWithUV(x, y, 0.0D, uMin, vMin);
        tessellator.draw();
    }

    /**
     * Render sparkle/shine effects on the filled portion
     */
    @Unique
    private void renderSparkles(int barX, int barY, int fillWidth, int barHeight, double sanityPercent, float partialTicks) {
        long time = System.currentTimeMillis(); // used to seed the randomness

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE); // glow

        Tessellator tessellator = Tessellator.instance;

        int sparkleCount = Math.max(3, fillWidth / 10); // at least 3 sparkles, scales with width

        for (int i = 0; i < sparkleCount; i++) {
            // use unique seed for each sparkle based on index and time. this ensures even distribution across the bar
            long sparkleTimeSeed = (time / 150) + (i * 1000L);
            Random rand = new Random(sparkleTimeSeed);

            // distribute sparkles evenly with some randomness
            float basePosition = (float)i / (float)sparkleCount;
            float randomOffset = (rand.nextFloat() - 0.5F) * 0.15F;
            float normalizedX = Math.max(0.0F, Math.min(1.0F, basePosition + randomOffset));

            float sparkleX = barX + (normalizedX * fillWidth);
            float sparkleY = barY + (rand.nextFloat() * barHeight);
            float sparkleSize = 0.8F + (rand.nextFloat() * 1.2F);

            // Twinkle animation - each sparkle has unique phase
            long phaseOffset = i * 317L;
            float phase = (float)((time + phaseOffset) % 1500) / 1500.0F;
            float alpha = (float)(Math.sin(phase * Math.PI * 2) * 0.5 + 0.5);
            alpha *= (1.0 - sanityPercent * 0.3);

            float colorVariation = 0.9F + (rand.nextFloat() * 0.1F);

            // sparkle color - mostly white with slight blue tint at high sanity
            float r = colorVariation;
            float g = colorVariation;
            float b = 1.0F;

            if (sanityPercent > 0.5) {
                float tintStrength = (float)(sanityPercent - 0.5) * 1.5F;
                r = r * (1.0F - tintStrength) + tintStrength;
                g = g * (1.0F - tintStrength) + 0.3F * tintStrength;
                b = b * (1.0F - tintStrength) + 0.5F * tintStrength;
            }

            GL11.glColor4f(r, g, b, alpha * 0.7F);

            // actually do the render
            tessellator.startDrawingQuads();
            tessellator.addVertex(sparkleX - sparkleSize, sparkleY + sparkleSize, 0.0D);
            tessellator.addVertex(sparkleX + sparkleSize, sparkleY + sparkleSize, 0.0D);
            tessellator.addVertex(sparkleX + sparkleSize, sparkleY - sparkleSize, 0.0D);
            tessellator.addVertex(sparkleX - sparkleSize, sparkleY - sparkleSize, 0.0D);
            tessellator.draw();

            // add a cross-shaped highlight for extra sparkle
            if (alpha > 0.6F) {
                GL11.glColor4f(r, g, b, (alpha - 0.6F) * 0.5F);

                float crossSize = sparkleSize * 1.5F;
                // horizontal bar of cross
                tessellator.startDrawingQuads();
                tessellator.addVertex(sparkleX - crossSize, sparkleY + 0.5F, 0.0D);
                tessellator.addVertex(sparkleX + crossSize, sparkleY + 0.5F, 0.0D);
                tessellator.addVertex(sparkleX + crossSize, sparkleY - 0.5F, 0.0D);
                tessellator.addVertex(sparkleX - crossSize, sparkleY - 0.5F, 0.0D);
                tessellator.draw();

                // vertical bar of cross
                tessellator.startDrawingQuads();
                tessellator.addVertex(sparkleX - 0.5F, sparkleY + crossSize, 0.0D);
                tessellator.addVertex(sparkleX + 0.5F, sparkleY + crossSize, 0.0D);
                tessellator.addVertex(sparkleX + 0.5F, sparkleY - crossSize, 0.0D);
                tessellator.addVertex(sparkleX - 0.5F, sparkleY - crossSize, 0.0D);
                tessellator.draw();
            }
        }

        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    /**
     * Render pulsing glow effect at high insanity levels
     */
    @Unique
    private void renderInsanityGlow(int barX, int barY, int fillWidth, int barHeight, double sanity, float partialTicks) {
        if (sanity < (MAX_SANITY / 2)) return;

        float intensity = (float)Math.min((sanity - (MAX_SANITY / 2)) / (MAX_SANITY / 2), 1.0);
        float pulse = (float)(Math.sin((System.currentTimeMillis() + partialTicks * 50) * 0.008) * 0.3 + 0.7);

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

        // color based on insanity level
        float r = 0.8F + (intensity * 0.2F);
        float g = 0.3F - (intensity * 0.2F);
        float b = 0.6F - (intensity * 0.3F);
        float alpha = intensity * pulse * 0.4F;

        GL11.glColor4f(r, g, b, alpha);

        Tessellator tessellator = Tessellator.instance;

        // render glow slightly larger than the bar
        int glowExpand = 2;
        tessellator.startDrawingQuads();
        tessellator.addVertex(barX - glowExpand, barY + barHeight + glowExpand, 0.0D);
        tessellator.addVertex(barX + fillWidth + glowExpand, barY + barHeight + glowExpand, 0.0D);
        tessellator.addVertex(barX + fillWidth + glowExpand, barY - glowExpand, 0.0D);
        tessellator.addVertex(barX - glowExpand, barY - glowExpand, 0.0D);
        tessellator.draw();

        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }


    // unused inject
//    @Inject(method = "renderGameOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/GuiIngame;renderModSpecificPlayerSightEffects()V"))
//    private void renderVignetteInUnderworld(float par1, boolean par2, int par3, int par4, CallbackInfo ci){}


    @Inject(method = "drawPenaltyText(II)V", at = @At("TAIL"))
    private void drawTimer(int iScreenX, int iScreenY, CallbackInfo cbi){
        if(!mc.thePlayer.isDead){
            amountRendered = 0;
            if(this.mc.thePlayer.isInsideOfMaterial(Material.water) || mc.thePlayer.getAir() < 300){
                amountRendered++;
            }
            String period = this.mc.theWorld.isDaytime() ? I18n.getString("gui.nmTimer.day") : I18n.getString("gui.nmTimer.night");

            if(this.mc.thePlayer.dimension == -1){
                period = this.getIsActuallyDaytime(this.mc.theWorld) ? I18n.getString("gui.nmTimer.day") : I18n.getString("gui.nmTimer.night");
            }

            int dawnOffset = this.isDawnOrDusk(this.mc.theWorld.getWorldTime());
            FontRenderer fontRenderer = this.mc.fontRenderer;
            String textToShow = secToTime((int)(this.mc.theWorld.getTotalWorldTime() / 20));
            int stringWidth = fontRenderer.getStringWidth(textToShow);
            ArrayList<StatusEffect> activeStatuses = mc.thePlayer.getAllActiveStatusEffects();

            if(NightmareMode.shouldShowRealTimer){
                renderText(textToShow, stringWidth, iScreenX, iScreenY, fontRenderer, activeStatuses);
            }

            textToShow = String.format(period, ((int)Math.ceil((double) this.mc.theWorld.getWorldTime() / 24000)) + dawnOffset);
            stringWidth = fontRenderer.getStringWidth(textToShow);
            if(NightmareMode.shouldShowDateTimer){
                renderText(textToShow, stringWidth, iScreenX, iScreenY, fontRenderer, activeStatuses);
            }

            if(NightmareMode.bloodMoonHelper){
                textToShow = this.getBloodMoonText(this.mc.theWorld);
                stringWidth = fontRenderer.getStringWidth(textToShow);
                renderText(textToShow, stringWidth, iScreenX, iScreenY, fontRenderer, activeStatuses);
            }
            if(NightmareMode.configOnHud){

                textToShow = NMConfUtils.getTextForActiveConfig(this.mc.theWorld.worldInfo.getData(CONFIGS_CREATED));
                stringWidth = fontRenderer.getStringWidth(textToShow);
                renderText(textToShow, stringWidth, iScreenX, iScreenY, fontRenderer, activeStatuses);
            }


        }
    }
    @Unique private String getBloodMoonText(World world){
        if(this.shouldShowBloodMoonCountdown(world)){
            long deltaToNextBM = NMUtils.getNextBloodMoonTime(world.getWorldTime()) - world.getWorldTime();
            deltaToNextBM /= 24000;
            deltaToNextBM = (long) Math.floor(deltaToNextBM);

            String when = null;
            if(deltaToNextBM == 16){
                when = I18n.getString("gui.bloodmoon.tonight");
            }

            if(deltaToNextBM == 1 || deltaToNextBM == 0){
                when = I18n.getString("gui.bloodmoon.tomorrow");
            }
            String text;

            if(when == null){
                text = "\247c" + I18n.getString("gui.bloodmoon.in") +" "+ deltaToNextBM + " " + I18n.getString("gui.bloodmoon.days");
            } else{
                text = "\247c" + I18n.getString("gui.bloodmoon") +" " + I18n.getString(when);
            }
            return text;
        }
        return "";
    }

    @Unique private boolean shouldShowBloodMoonCountdown(World world){
        if(NMUtils.getWorldProgress() > 0) return true;

        long portalTime = world.worldInfo.getData(NightmareMode.PORTAL_TIME);
        if(portalTime > world.getWorldTime()){
            long timeToNextBloodMoon = NMUtils.getNextBloodMoonTime(world.getWorldTime());
            return portalTime < timeToNextBloodMoon;
        }
        return false;
    }



    @Unique
    private int isDawnOrDusk(long time){
        if(time % 24000 >= 23459) {
            return 1;
        }
        return 0;
    }
    @Unique int heightField = 13;
    @Unique int widthField = 6;

    @Inject(method = "renderGameOverlay", at = @At("TAIL"))
    private void drawTamingArrow(float partialTicks, boolean hasScreen, int mouseX, int mouseY, CallbackInfo ci) {
        Minecraft mc = Minecraft.getMinecraft();

        if (!(mc.thePlayer.ridingEntity instanceof EntityHorse horse)) return;


        // only show for untamed horses while riding
        if (!horse.isTame() && horse.riddenByEntity instanceof EntityPlayer) {
            // read the required direction stored by the horse (updated from packets)
            byte ordinal = ((IHorseTamingClient) horse).nm$getRequiredDirection();
            if (ordinal < 0 || ordinal >= EnumFacing.values().length) return;


            EnumFacing required = EnumFacing.values()[ordinal];

//            System.out.println("horse direction: " + ordinal + " | " + required + "| " + (horse.worldObj.isRemote ? "client" : "server"));

            float transparency = this.calcTransparencyForAngles(horse, mc.thePlayer);

            drawArrow(required, horse, transparency);

            FontRenderer fontRenderer = this.mc.fontRenderer;
            String textToShow = I18n.getString("gui.nm.horseTaming");
            int stringWidth = fontRenderer.getStringWidth(textToShow);

            ScaledResolution var5 = new ScaledResolution(this.mc.gameSettings, this.mc.displayWidth, this.mc.displayHeight);
            int iScreenX = var5.getScaledWidth();
            int iScreenY = var5.getScaledHeight();

            int textX = iScreenX / 2;
            int textY = iScreenY * 8 / 11;

            fontRenderer.drawStringWithShadow(textToShow, textX - stringWidth / 2, textY, 0XFFFFFF);
        }
//        if(Keyboard.isKeyDown(Keyboard.KEY_L)){
//            heightField -= 1;
//            System.out.println("height: " + heightField);
//        }
//        if(Keyboard.isKeyDown(Keyboard.KEY_K)){
//            widthField -= 1;
//            System.out.println("width: "+ widthField);
//        }
//        if(Keyboard.isKeyDown(Keyboard.KEY_O)){
//            heightField += 1;
//            System.out.println("height: " + heightField);
//        }
//        if(Keyboard.isKeyDown(Keyboard.KEY_P)){
//            widthField += 1;
//            System.out.println("width: "+ widthField);
//        }
//        if(Keyboard.isKeyDown(Keyboard.KEY_R)){
//            widthField = 6;
//            heightField = 14;
//        }
    }
    @Unique private void drawVerticalProgressBar(int x, int y, int width, int height, int progress, int max, float transparency) {
        Tessellator tess = Tessellator.instance;

//        System.out.println("progress: " + progress);

        // Clamp progress
        float pct = Math.max(0f, Math.min(1f, (float) progress / (float) max));
        int filledHeight = (int) (pct * height);



        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        // Draw background (dark gray)
        tess.startDrawingQuads();
        tess.setColorRGBA_F(0.2f, 0.2f, 0.2f, transparency);
        tess.addVertex(x, y + height, 0.0);
        tess.addVertex(x + width, y + height, 0.0);
        tess.addVertex(x + width, y, 0.0);
        tess.addVertex(x, y, 0.0);
        tess.draw();

        // Draw filled portion (green)
        tess.startDrawingQuads();
        tess.setColorRGBA_F(0f, 0.8f, 0f, transparency);
        tess.addVertex(x, y + height, 0.0);
        tess.addVertex(x + width, y + height, 0.0);
        tess.addVertex(x + width, y + height - filledHeight, 0.0);
        tess.addVertex(x, y + height - filledHeight, 0.0);
        tess.draw();

        // Optional: white outline
        GL11.glLineWidth(1.5f);
        tess.startDrawing(GL11.GL_LINE_LOOP);
        tess.setColorRGBA_F(1f, 1f, 1f, transparency);
        tess.addVertex(x, y + height, 0.0);
        tess.addVertex(x + width, y + height, 0.0);
        tess.addVertex(x + width, y, 0.0);
        tess.addVertex(x, y, 0.0);
        tess.draw();

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    @Unique private void drawArrow(EnumFacing dir, EntityHorse horse, float transparency) {
        if (dir == null) return;

        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution sr = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
        int sw = sr.getScaledWidth();
        int sh = sr.getScaledHeight();

        int cx = sw / 2;
        int cy = sh / 2 + 40; // below crosshair
        int arrowSize = 24;
        int shaftLength = heightField; // length of the arrow shaft
        int shaftWidth = widthField;   // width of the arrow shaft



        float angleDeg;
        switch (dir) {
            case NORTH: angleDeg = 0f; break;
            case SOUTH: angleDeg = 180f; break;
            case EAST:  angleDeg = 90f; break;
            case WEST:  angleDeg = -90f; break;
            default:    angleDeg = 0f; break;
        }

        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glTranslatef(cx, cy, 0f);
        GL11.glRotatef(angleDeg, 0f, 0f, 1f);

        Tessellator tess = Tessellator.instance;

        // ---- draw black fill first ----
        tess.startDrawingQuads();
        tess.setColorRGBA_F(0f, 0f, 0f, transparency);

        // shaft (centered vertically)
        tess.addVertex(-shaftWidth / 2f, shaftLength / 2f, 0.0);
        tess.addVertex(shaftWidth / 2f, shaftLength / 2f, 0.0);
        tess.addVertex(shaftWidth / 2f, -shaftLength / 2f, 0.0);
        tess.addVertex(-shaftWidth / 2f, -shaftLength / 2f, 0.0);
        tess.draw();

        // arrowhead (triangle tip)
        tess.startDrawing(GL11.GL_TRIANGLES);
        tess.setColorRGBA_F(0f, 0f, 0f, transparency);
        tess.addVertex(-arrowSize / 2.0, -shaftLength / 2.0, 0.0);
        tess.addVertex(arrowSize / 2.0, -shaftLength / 2.0, 0.0);
        tess.addVertex(0.0, -arrowSize / 2.0 - 6.0, 0.0); // tip
        tess.draw();

        // ---- draw white outline ----
        GL11.glLineWidth(2f);
        tess.startDrawing(GL11.GL_LINE_LOOP);
        tess.setColorRGBA_F(1f, 1f, 1f, transparency);
        // outline shaft
        tess.addVertex(-shaftWidth / 2f, shaftLength / 2f, 0.0);
        tess.addVertex(shaftWidth / 2f, shaftLength / 2f, 0.0);
        tess.addVertex(shaftWidth / 2f, -shaftLength / 2f, 0.0);
        tess.addVertex(-shaftWidth / 2f, -shaftLength / 2f, 0.0);
        tess.draw();

        tess.startDrawing(GL11.GL_LINE_LOOP);
        tess.setColorRGBA_F(1f, 1f, 1f, transparency);
        // outline arrowhead
        tess.addVertex(-arrowSize / 2.0, -shaftLength / 2.0, 0.0);
        tess.addVertex(arrowSize / 2.0, -shaftLength / 2.0, 0.0);
        tess.addVertex(0.0, -arrowSize / 2.0 - 6.0, 0.0);
        tess.draw();

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glPopMatrix();

        int barX = cx + 20;
        int barY = cy - (32/2); // top of bar
        int barWidth = 6;
        int barHeight = 32;
        drawVerticalProgressBar(barX, barY, barWidth, barHeight, ((IHorseTamingClient)horse).nm$getTamingProgress(), 1000, transparency);

    }

    @Unique private float calcTransparencyForAngles(EntityHorse horseHost, EntityPlayer player){
        double horseYawRad = Math.toRadians(horseHost.rotationYawHead);
        Vec3 horseForward = Vec3.createVectorHelper(
                -Math.sin(horseYawRad), // x
                0.0,
                Math.cos(horseYawRad)  // z
        ).normalize();

        double playerYawRad = Math.toRadians(player.rotationYawHead);
        Vec3 playerForward = Vec3.createVectorHelper(
                -Math.sin(playerYawRad), // x
                0.0,
                Math.cos(playerYawRad)  // z
        ).normalize();


        double dotProduct = horseForward.dotProduct(playerForward);

        if (dotProduct <= 0.15f){
            return 0.15f;
        }
        return (float) Math.min(dotProduct, 1.0f);
    }



    @ModifyConstant(method = "drawFoodOverlay", constant = @Constant(intValue = 10, ordinal = 0))
    private int modifyNiteFoodOverlay(int original) {
        if(!NightmareMode.nite || mc.thePlayer == null){return original;}
        return (int) (NMUtils.getFoodShanksFromLevel(mc.thePlayer) / 6F);
    }

    @Unique
    private void renderText(String text, int stringWidth, int iScreenX, int iScreenY, FontRenderer fontRenderer, ArrayList<StatusEffect> activeStatuses){
        fontRenderer.drawStringWithShadow(text, iScreenX - stringWidth, iScreenY-(10 * (activeStatuses.size()+amountRendered)), 0XFFFFFF);
        amountRendered++;
    }

    @Unique
    String secToTime(int sec) {
        int seconds = sec % 60;
        int minutes = sec / 60;
        if (minutes > 0){
            if (minutes >= 60) {
                int hours = minutes / 60;
                minutes %= 60;
                if(hours >= 24) {
                    int days = hours / 24;
                    return String.format("%02d:%02d:%02d:%02d", days,hours%24, minutes, seconds);
                }
                return String.format("%02d:%02d:%02d", hours, minutes, seconds);
            }
            return String.format("%02d:%02d", minutes, seconds);
        }
        return String.format("0:%02d", seconds);
    }

    @Unique
    boolean getIsActuallyDaytime(World world){
        long time = world.getWorldTime() % 24000;
        return time <= 12541 || time >= 23459;
    }
}
