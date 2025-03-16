package com.itlesports.nightmaremode.mixin.gui;

import btw.community.nightmaremode.NightmareMode;
import btw.util.status.StatusEffect;
import com.itlesports.nightmaremode.NightmareUtils;
import net.minecraft.src.*;
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

@Mixin(GuiIngame.class)
public class GuiIngameMixin {

    @Final @Shadow private Minecraft mc;

    @Unique
    private int amountRendered = 0;

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
            String textToShow = secToTime((int)(Minecraft.getMinecraft().theWorld.getTotalWorldTime() / 20));
            int stringWidth = fontRenderer.getStringWidth(textToShow);
            ArrayList<StatusEffect> activeStatuses = mc.thePlayer.getAllActiveStatusEffects();

            if(NightmareMode.shouldShowRealTimer){
                renderText(textToShow, stringWidth, iScreenX, iScreenY, fontRenderer, activeStatuses);
            }

            textToShow = String.format(period, ((int)Math.ceil((double) Minecraft.getMinecraft().theWorld.getWorldTime() / 24000)) + dawnOffset);
            stringWidth = fontRenderer.getStringWidth(textToShow);
            if(NightmareMode.shouldShowDateTimer){
                renderText(textToShow, stringWidth, iScreenX, iScreenY, fontRenderer, activeStatuses);
            }

            textToShow = this.getTextForActiveConfig();
            stringWidth = fontRenderer.getStringWidth(textToShow);

            if(NightmareMode.configOnHud){
                renderText(textToShow, stringWidth, iScreenX, iScreenY, fontRenderer, activeStatuses);
            }
        }
    }

    @Unique
    private int isDawnOrDusk(long time){
        if(time % 24000 >= 23459) {
            return 1;
        }
        return 0;
    }
    @Unique private String getTextForActiveConfig(){
        String string = "";
        int count = 0;
        if(NightmareMode.bloodmare){
            string += "BM";
            count++;
        }
        if(NightmareMode.totalEclipse){
            if(count > 0){
                string += "+";
            }
            string += "TE";
            count++;
        }
        if(NightmareMode.evolvedMobs){
            if(count > 0){
                string += "+";
            }
            string += "EM";
            count++;
        }
        if(NightmareMode.buffedSquids){
            if(count > 0){
                string += "+";
            }
            string += "BS";
            count++;
        }
        if(NightmareMode.magicMonsters){
            if(count > 0){
                string += "+";
            }
            string += "MM";
        }
        if(NightmareMode.nite){
            if(count > 0){
                string += "+";
            }
            string += "NITE";
        }
        if(NightmareMode.noSkybases){
            if(count > 0){
                string += "+";
            }
            string += "NS";
        }
        if(NightmareMode.unkillableMobs){
            if(count > 0){
                string += "+";
            }
            string += "UM";
        }
        if(NightmareMode.moreVariants){
            if(count > 0){
                string += "+";
            }
            string += "MV";
        }
        if(NightmareMode.noHit){
            if (count > 0) {
                string = "NoHit " + string;
            } else{
                string += "NoHit";
            }
        }
        return string;
    }

    @ModifyConstant(method = "drawFoodOverlay", constant = @Constant(intValue = 10, ordinal = 0))
    private int modifyFoodOverlay(int original) {
        if(!NightmareMode.nite || mc.thePlayer == null){return original;}
        return (int) (NightmareUtils.getFoodShanksFromLevel(mc.thePlayer) / 6F);
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
