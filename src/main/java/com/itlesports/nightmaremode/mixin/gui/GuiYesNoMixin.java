package com.itlesports.nightmaremode.mixin.gui;

import com.itlesports.nightmaremode.nmgui.GuiJourneyActionButton;
import com.itlesports.nightmaremode.nmgui.JourneyTitleTheme;
import com.itlesports.nightmaremode.util.interfaces.JourneyMenuBackdrop;
import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.GuiSelectWorld;
import net.minecraft.src.GuiYesNo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Gives world deletion confirmation the same protected, translucent Journey treatment. */
@Mixin(GuiYesNo.class)
public abstract class GuiYesNoMixin extends GuiScreen {
    @Shadow protected GuiScreen parentScreen;
    @Shadow protected String message1;
    @Shadow private String message2;
    @Shadow protected int worldNumber;

    @Inject(method = "initGui", at = @At("TAIL"))
    private void journeyMode$styleConfirmation(CallbackInfo ci) {
        if (!journeyMode$isWorldDialog()) return;
        for (int index = 0; index < this.buttonList.size(); index++) {
            GuiButton original = (GuiButton)this.buttonList.get(index);
            GuiJourneyActionButton themed = new GuiJourneyActionButton(original.id, original.xPosition, original.yPosition, original.width, original.displayString);
            themed.enabled = original.enabled;
            themed.drawButton = original.drawButton;
            this.buttonList.set(index, themed);
        }
    }

    @Override
    protected void keyTyped(char character, int keyCode) {
        if (keyCode == 1 && journeyMode$isWorldDialog()) {
            this.parentScreen.confirmClicked(false, this.worldNumber);
            return;
        }
        super.keyTyped(character, keyCode);
    }

    @Inject(method = "drawScreen", at = @At("HEAD"), cancellable = true)
    private void journeyMode$drawWorldConfirmation(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        if (!journeyMode$isWorldDialog()) return;
        ci.cancel();
        JourneyTitleTheme theme = JourneyTitleTheme.getActive(this.mc);
        if (this.parentScreen instanceof JourneyMenuBackdrop) {
            ((JourneyMenuBackdrop)this.parentScreen).nightmareMode$drawJourneyBackdrop(mouseX, mouseY, partialTicks, this.width, this.height);
        } else {
            this.drawDefaultBackground();
        }
        int cardWidth = Math.min(360, this.width - 24);
        int cardX = (this.width - cardWidth) / 2;
        int cardTop = 48;
        int cardBottom = this.height / 6 + 126;
        drawRect(cardX, cardTop, cardX + cardWidth, cardBottom, 0x98000000 | (theme.cardFill & 0x00FFFFFF));
        drawRect(cardX, cardTop, cardX + cardWidth, cardTop + 1, theme.edge);
        drawRect(cardX, cardTop, cardX + 1, cardBottom, theme.edge);
        drawRect(cardX + cardWidth - 1, cardTop, cardX + cardWidth, cardBottom, theme.edge);
        this.drawCenteredString(this.fontRenderer, this.message1, this.width / 2, 70, theme.textHighlight);
        this.drawCenteredString(this.fontRenderer, this.message2, this.width / 2, 90, theme.text);
        this.drawCenteredString(this.fontRenderer, "This cannot be undone.", this.width / 2, 108, theme.textMuted);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Unique private boolean journeyMode$isWorldDialog() {
        return this.worldNumber == 9101 || this.parentScreen instanceof GuiSelectWorld;
    }
}
