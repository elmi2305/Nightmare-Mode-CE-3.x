package com.itlesports.nightmaremode.mixin.gui;

import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.util.NMUtils;
import com.itlesports.nightmaremode.nmgui.GuiColoredButton;
import com.itlesports.nightmaremode.nmgui.GuiConfig;
import com.itlesports.nightmaremode.nmgui.GuiWarning;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Mixin(GuiSelectWorld.class)
public class GuiSelectWorldMixin extends GuiScreen {

    @Unique
    private static int num = 0;
    @Unique private static boolean chaos = false;

    @Inject(method = "initButtons", at = @At("TAIL"))
    private void addBuffSquidsButton(CallbackInfo ci){
        if (NightmareMode.isAprilFools) {
            this.buttonList.add(new GuiButton(10, this.width / 12, this.height / 2 - 40, 98, 20, I18n.getString("gui.selectworld.buff_squids")));
            GuiButton chaosButton = new GuiButton(11, this.width / 12, this.height / 2 - 70, 130, 20, I18n.getString("gui.selectworld.toggle_cancer_worldgen"));
            this.buttonList.add(chaosButton);
        }
        this.buttonList.add(new GuiColoredButton(2305, 5, 5, 80, 20, I18n.getString("gui.selectworld.nm_config"), 0xFFFFFF, 0xd4d4d4, 0xFF0000));
    }

    @Inject(method = "drawScreen", at = @At("TAIL"))
    private void drawSquidText(int par1, int par2, float par3, CallbackInfo ci){
        if (NightmareMode.isAprilFools) {
            String timesText = I18n.getString("gui.selectworld.squid_buffed_times")
                    .replace("{0}", Integer.toString(num))
                    .replace("{1}", num == 1 ? "" : "s");
            this.drawCenteredString(this.fontRenderer, timesText, this.width / 16 + this.fontRenderer.getStringWidth(timesText) / 2 - 20, this.height / 2 + 25, 0xFFFFFF);

            String strengthText = I18n.getString("gui.selectworld.squid_strength")
                    .replace("{0}", Double.toString(roundIfNeeded(1 + num * 0.013)));
            this.drawCenteredString(this.fontRenderer, strengthText, this.width / 16 + this.fontRenderer.getStringWidth(strengthText) / 2 - 20, this.height / 2 + 35, 0xFFFFFF);

            String chaosText = Boolean.toString(chaos);
            this.drawCenteredString(this.fontRenderer, chaosText, this.width / 12 + this.fontRenderer.getStringWidth(chaosText) / 2 - 30, this.height / 2 - 25, 0xFFFFFF);
        }
    }

    @Inject(method = "actionPerformed", at = @At("TAIL"))
    private void squidButton(GuiButton par1GuiButton, CallbackInfo ci) {
        if (NightmareMode.isAprilFools) {
            if (par1GuiButton.id == 10) {
                num += 1;
                NMUtils.setBuffedSquidBonus(roundIfNeeded(1 + num * 0.013));
            }
            if(par1GuiButton.id == 11){
                chaos = !chaos;
                NMUtils.setIntenseCorruption(chaos);
            }
        }
        if(par1GuiButton.id == 2305){
            this.mc.displayGuiScreen(new GuiConfig(this));
        }
    }

    @Inject(method = "selectWorld", at = @At("HEAD"),cancellable = true)
    private void manageWarning(int par1, CallbackInfo ci){
        if(NightmareMode.isAprilFools){
            if (!GuiWarning.hasPlayerAgreed()) {
                GuiWarning screen = new GuiWarning(this);
                this.mc.displayGuiScreen(screen);
                ci.cancel();
            }
        }else if(NightmareMode.getInstance().wasConfigModified){
            if (!GuiWarning.hasPlayerAgreed()) {
                GuiWarning screen = new GuiWarning(this);
                screen.setLine1(I18n.getString("gui.selectworld.warning_title"));
                screen.setLine2(I18n.getString("gui.selectworld.warning_recommend_restart"));
                screen.setLine3(I18n.getString("gui.selectworld.warning_config_crash"));
                screen.setLine4(I18n.getString("gui.selectworld.warning_continue"));
                screen.setLine5(I18n.getString("gui.selectworld.warning_final"));
                this.mc.displayGuiScreen(screen);
                ci.cancel();
            }
        }
    }

    @Unique
    private static double roundIfNeeded(double value) {
        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.stripTrailingZeros();
        int scale = Math.max(0, bd.scale());
        if (scale > 3) {
            bd = bd.setScale(3, RoundingMode.HALF_UP);
        }
        return bd.doubleValue();
    }
}