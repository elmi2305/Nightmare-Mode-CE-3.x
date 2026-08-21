package com.itlesports.nightmaremode.mixin.gui;

import api.world.difficulty.Difficulty;
import btw.client.gui.LockButton;
import com.itlesports.nightmaremode.nmgui.GuiJourneyActionButton;
import com.itlesports.nightmaremode.nmgui.JourneyTitleTheme;
import com.itlesports.nightmaremode.util.interfaces.JourneyMenuBackdrop;
import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiCreateWorld;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.GuiTextField;
import net.minecraft.src.I18n;
import net.minecraft.src.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiCreateWorld.class)
public abstract class GuiCreateWorldMixin extends GuiScreen {
    @Shadow private GuiScreen parentGuiScreen;
    @Shadow private boolean lockDifficulty;
    @Shadow private int difficultyID;
    @Shadow private LockButton buttonLockDifficulty;
    @Shadow private GuiTextField textboxWorldName;
    @Shadow private GuiTextField textboxSeed;
    @Shadow private String folderName;
    @Shadow private String seed;
    @Shadow private boolean moreOptions;
    @Shadow private GuiButton buttonGameMode;
    @Shadow private GuiButton buttonDifficultyLevel;
    @Shadow private GuiButton moreWorldOptions;
    @Shadow private GuiButton buttonGenerateStructures;
    @Shadow private GuiButton buttonBonusItems;
    @Shadow private GuiButton buttonWorldType;
    @Shadow private GuiButton buttonAllowCommands;
    @Shadow private GuiButton buttonCustomize;
    @Unique boolean onlyOnce = true;
    @Unique private boolean journeyMode$recreatingWorld;

    @Inject(method = "initGui", at = @At("TAIL"))
    private void journeyMode$styleCreationScreen(CallbackInfo ci) {
        this.journeyMode$recreatingWorld = !MathHelper.stringNullOrLengthZero(this.seed);
        JourneyTitleTheme theme = JourneyTitleTheme.getActive(this.mc);
        this.textboxWorldName = journeyMode$centerTextBox(this.textboxWorldName, theme);
        this.textboxSeed = journeyMode$centerTextBox(this.textboxSeed, theme);
        // Difficulty is forced elsewhere; do not leave a disabled lock control in the UI.
        this.buttonLockDifficulty.drawButton = false;
        for (int index = 0; index < this.buttonList.size(); index++) {
            GuiButton original = (GuiButton)this.buttonList.get(index);
            if (original instanceof LockButton) continue;
            GuiJourneyActionButton themed = new GuiJourneyActionButton(original.id, original.xPosition, original.yPosition, original.width, original.displayString);
            themed.enabled = original.enabled;
            themed.drawButton = original.drawButton;
            this.buttonList.set(index, themed);
            journeyMode$replaceButtonReference(original, themed);
        }
        for (Object entry : this.buttonList) {
            GuiButton button = (GuiButton)entry;
            if (button.id == 0) button.displayString = "Create World";
            else if (button.id == 1) button.displayString = "Back to Worlds";
        }
    }

    @Unique private void journeyMode$replaceButtonReference(GuiButton original, GuiButton replacement) {
        if (original == this.buttonGameMode) this.buttonGameMode = replacement;
        if (original == this.buttonDifficultyLevel) this.buttonDifficultyLevel = replacement;
        if (original == this.moreWorldOptions) this.moreWorldOptions = replacement;
        if (original == this.buttonGenerateStructures) this.buttonGenerateStructures = replacement;
        if (original == this.buttonBonusItems) this.buttonBonusItems = replacement;
        if (original == this.buttonWorldType) this.buttonWorldType = replacement;
        if (original == this.buttonAllowCommands) this.buttonAllowCommands = replacement;
        if (original == this.buttonCustomize) this.buttonCustomize = replacement;
    }

    @Unique private GuiTextField journeyMode$centerTextBox(GuiTextField original, JourneyTitleTheme theme) {
        // GuiTextField without its vanilla background draws at its raw X/Y. Give
        // the themed border a normal inner padding and vertically center the font.
        GuiTextField centered = new GuiTextField(this.fontRenderer, this.width / 2 - 96, 66, 192, 20);
        centered.setText(original.getText());
        centered.setFocused(original.isFocused());
        centered.setEnableBackgroundDrawing(false);
        centered.setTextColor(theme.text);
        return centered;
    }

    @Inject(method = "updateButtonText", at = @At("HEAD"))
    private void manageDifficulty(CallbackInfo ci){
        if(this.difficultyID == 0 && onlyOnce){
            this.difficultyID = 2;
            onlyOnce = false;
        }
    }

    @Inject(method = "actionPerformed", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/GuiCreateWorld;updateButtonText()V", ordinal = 8))
    private void manageDifficulty2(GuiButton par1GuiButton, CallbackInfo ci){
        this.difficultyID = 2; // always hostile
    }
    @Inject(method = "actionPerformed", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/GuiCreateWorld;updateButtonText()V", ordinal = 9))
    private void alwaysLockedDifficulty(CallbackInfo ci){
        this.lockDifficulty = true;
    }

    @Redirect(method = "updateButtonText", at = @At(value = "INVOKE", target = "Lapi/world/difficulty/Difficulty;getLocalizedName()Ljava/lang/String;", remap = false))
    private String customDifficultyName(Difficulty difficulty){
        return I18n.getString("difficulty.nightmare.name");
    }
    @Inject(method = "updateButtonText", at = @At("TAIL"))
    private void lockButtonCannotBeClicked(CallbackInfo ci){
        // this fixes the issue TdL had right at the start, where he clicked the lock button thinking it'd make any difference
        this.buttonLockDifficulty.enabled = false;
    }

    @Redirect(method = "drawScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/I18n;getString(Ljava/lang/String;)Ljava/lang/String;",ordinal = 10))
    private String customText(String string){
        if (this.difficultyID == 2) {
            return I18n.getString("difficulty.nightmare.description1");
        }
        return I18n.getString("difficulty.baddream.description1");
    }
    @Redirect(method = "drawScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/I18n;getString(Ljava/lang/String;)Ljava/lang/String;",ordinal = 11))
    private String customText1(String string){
        if (this.difficultyID == 2) {
            return I18n.getString("difficulty.nightmare.description2");
        }
        return I18n.getString("difficulty.baddream.description2");
    }
    @Redirect(method = "drawScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/I18n;getString(Ljava/lang/String;)Ljava/lang/String;",ordinal = 12))
    private String customText2(String string){
        if (this.difficultyID == 2) {
            return I18n.getString("difficulty.nightmare.description3");
        }
        return I18n.getString("difficulty.baddream.description3");
    }

    /** Escape is a real back action here: it never strands the player outside the embedded browser. */
    @Inject(method = "keyTyped", at = @At("HEAD"), cancellable = true)
    private void journeyMode$safeBack(char character, int keyCode, CallbackInfo ci) {
        if (keyCode != 1) return;
        this.mc.displayGuiScreen(this.parentGuiScreen);
        ci.cancel();
    }

    @Inject(method = "drawScreen", at = @At("HEAD"), cancellable = true)
    private void journeyMode$drawThemedCreationScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        ci.cancel();
        JourneyTitleTheme theme = JourneyTitleTheme.getActive(this.mc);
        if (this.parentGuiScreen instanceof JourneyMenuBackdrop) {
            ((JourneyMenuBackdrop)this.parentGuiScreen).nightmareMode$drawJourneyBackdrop(mouseX, mouseY, partialTicks, this.width, this.height);
        } else {
            this.drawDefaultBackground();
        }

        int cardWidth = Math.min(360, this.width - 24);
        int cardX = (this.width - cardWidth) / 2;
        int cardBottom = this.height - 8;
        drawRect(cardX, 8, cardX + cardWidth, cardBottom, 0x92000000 | (theme.cardFill & 0x00FFFFFF));
        drawRect(cardX, 8, cardX + cardWidth, 9, theme.edge);
        drawRect(cardX, 8, cardX + 1, cardBottom, theme.edge);
        drawRect(cardX + cardWidth - 1, 8, cardX + cardWidth, cardBottom, theme.edge);

        String title = this.journeyMode$recreatingWorld ? "Recreate World" : "Create a New World";
        this.drawCenteredString(this.fontRenderer, title, this.width / 2, 18, theme.textHighlight);
        this.drawCenteredString(this.fontRenderer, this.journeyMode$recreatingWorld ? "A copy will be created with these settings" : "Choose how your next journey begins", this.width / 2, 31, theme.textMuted);

        if (this.moreOptions) {
            journeyMode$drawTextBox(this.textboxSeed, this.width / 2 - 100, 60, 200, theme);
            this.drawString(this.fontRenderer, I18n.getString("selectWorld.enterSeed"), this.width / 2 - 100, 47, theme.textMuted);
            this.drawString(this.fontRenderer, I18n.getString("selectWorld.seedInfo"), this.width / 2 - 100, 85, theme.textMuted);
            this.drawString(this.fontRenderer, "World generation and optional rules", this.width / 2 - 150, 122, theme.textMuted);
            this.drawString(this.fontRenderer, "These settings can be changed before creation.", this.width / 2 - 150, 172, theme.textMuted);
        } else {
            journeyMode$drawTextBox(this.textboxWorldName, this.width / 2 - 100, 60, 200, theme);
            this.drawString(this.fontRenderer, I18n.getString("selectWorld.enterName"), this.width / 2 - 100, 47, theme.textMuted);
            this.drawString(this.fontRenderer, I18n.getString("selectWorld.resultFolder") + " " + this.folderName, this.width / 2 - 100, 85, theme.textMuted);
            this.drawString(this.fontRenderer, "Choose a game mode. Nightmare difficulty is always locked.", this.width / 2 - 153, 125, theme.textMuted);
            this.drawString(this.fontRenderer, "World settings can be adjusted under More World Options.", this.width / 2 - 153, 139, theme.textMuted);
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Unique private void journeyMode$drawTextBox(GuiTextField textBox, int x, int y, int width, JourneyTitleTheme theme) {
        drawRect(x - 1, y - 1, x + width + 1, y + 21, theme.edge);
        drawRect(x, y, x + width, y + 20, 0xB0000000 | (theme.panelRgb & 0x00FFFFFF));
        textBox.drawTextBox();
    }
    
}
