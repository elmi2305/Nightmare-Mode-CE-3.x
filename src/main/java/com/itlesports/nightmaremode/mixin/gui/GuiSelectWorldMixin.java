package com.itlesports.nightmaremode.mixin.gui;

import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.nmgui.GuiColoredButton;
import com.itlesports.nightmaremode.nmgui.GuiConfig;
import com.itlesports.nightmaremode.nmgui.GuiWarning;
import com.itlesports.nightmaremode.NightmareUtils;
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

    @Unique private static int num = 0;
    @Unique private static boolean chaos = false;
    @Inject(method = "initButtons", at = @At("TAIL"))
    private void addBuffSquidsButton(CallbackInfo ci){
        if (NightmareMode.isAprilFools) {
            this.buttonList.add(new GuiButton(10, this.width / 12, this.height / 2 - 40, 98, 20, "Buff Squids"));
            GuiButton chaosButton = new GuiButton(11, this.width / 12, this.height / 2 - 70, 130, 20, "Toggle Cancer Worldgen");
            this.buttonList.add(chaosButton);
        }
        this.buttonList.add(new GuiColoredButton(2305, 5, this.height / 2, 80, 20, "NM Config", 0xFFFFFF, 0xd4d4d4, 0xFF0000));
    }
    @Inject(method = "drawScreen", at = @At("TAIL"))
    private void drawSquidText(int par1, int par2, float par3, CallbackInfo ci){
        if (NightmareMode.isAprilFools) {
            String textToDisplay = "You have buffed squids " + num + " time" + (num == 1 ? "." : "s.");
            this.drawCenteredString(this.fontRenderer, textToDisplay, this.width / 16 + this.fontRenderer.getStringWidth(textToDisplay) / 2 - 20, this.height / 2 + 25, 0xFFFFFF);
            textToDisplay = "Squid strength multiplier: " + roundIfNeeded(1 + num * 0.013) + "x";
            this.drawCenteredString(this.fontRenderer, textToDisplay, this.width / 16 + this.fontRenderer.getStringWidth(textToDisplay) / 2 - 20, this.height / 2 + 35, 0xFFFFFF);
            textToDisplay = Boolean.toString(chaos);
            this.drawCenteredString(this.fontRenderer, textToDisplay, this.width / 12 + this.fontRenderer.getStringWidth(textToDisplay) / 2 - 30, this.height / 2 - 25, 0xFFFFFF);

        }
    }
    @Unique
    private static double roundIfNeeded(double value) {
        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.stripTrailingZeros(); // Remove trailing zeros

        // Count decimal places
        int scale = Math.max(0, bd.scale());

        // If more than 3 decimal places, round to 3
        if (scale > 3) {
            bd = bd.setScale(3, RoundingMode.HALF_UP);
        }

        return bd.doubleValue();
    }
    @Inject(method = "actionPerformed", at = @At("TAIL"))
    private void squidButton(GuiButton par1GuiButton, CallbackInfo ci) {
        if (NightmareMode.isAprilFools) {
            if (par1GuiButton.id == 10) {
                num += 1;
                NightmareUtils.setBuffedSquidBonus(roundIfNeeded(1 + num * 0.013));
            }
            if(par1GuiButton.id == 11){
                chaos = !chaos;
                NightmareUtils.setIntenseCorruption(chaos);
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
        }
    }
}
