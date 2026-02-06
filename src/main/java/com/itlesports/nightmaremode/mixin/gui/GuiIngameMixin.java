package com.itlesports.nightmaremode.mixin.gui;

import btw.community.nightmaremode.NightmareMode;
import api.util.status.StatusEffect;
import com.itlesports.nightmaremode.util.NMConfUtils;
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

import static btw.community.nightmaremode.NightmareMode.CONFIGS_CREATED;

@Mixin(GuiIngame.class)
public class GuiIngameMixin extends Gui{

    @Final @Shadow private Minecraft mc;

    @Unique
    private int amountRendered = 0;


    @Inject(method = "renderGameOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/GuiIngame;renderModSpecificPlayerSightEffects()V"))
    private void renderVignetteInUnderworld(float par1, boolean par2, int par3, int par4, CallbackInfo ci){

    }
    @Inject(method = "drawPenaltyText(II)V", at = @At("TAIL"))
    private void drawTimer(int iScreenX, int iScreenY, CallbackInfo cbi){
        if(!mc.thePlayer.isDead){
            amountRendered = 0;
            if(this.mc.thePlayer.isInsideOfMaterial(Material.water) || mc.thePlayer.getAir() < 300 ){
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
