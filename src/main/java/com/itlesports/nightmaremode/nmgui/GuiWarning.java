package com.itlesports.nightmaremode.nmgui;

import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiScreen;


public class GuiWarning extends GuiScreen {
    private static boolean playerHasAgreed = false;
    private final GuiScreen parentGuiScreen;

    public GuiWarning(GuiScreen par1GuiScreen) {
        this.parentGuiScreen = par1GuiScreen;
    }

    public static boolean hasPlayerAgreed() {
        return playerHasAgreed;
    }

    public static void setPlayerHasAgreed(boolean playerHasAgreed) {
        GuiWarning.playerHasAgreed = playerHasAgreed;
    }

    @Override
    public void drawScreen(int par1, int par2, float par3) {
        this.drawDefaultBackground();

        String textToDisplay = "bro";
        this.drawCenteredString(this.fontRenderer, textToDisplay, this.width / 2, this.height / 2 - 35, 0xFFFFFF);
        textToDisplay = "you are about to join a world with cancer mode enabled";
        this.drawCenteredString(this.fontRenderer, textToDisplay, this.width / 2, this.height / 2 - 20, 0xFFFFFF);
        textToDisplay = "if this isn’t a backup or a throwaway world, turn back now";
        this.drawCenteredString(this.fontRenderer, textToDisplay, this.width / 2, this.height / 2 - 5, 0xFFFFFF);
        textToDisplay = "cause it WILL probably get destroyed";
        this.drawCenteredString(this.fontRenderer, textToDisplay, this.width / 2, this.height / 2 + 10, 0xFFFFFF);
        textToDisplay = "are you absolutely certain you want to do this? this is your only warning";
        this.drawCenteredString(this.fontRenderer, textToDisplay, this.width / 2, this.height / 2 + 40, 0xFFFFFF);

        super.drawScreen(par1, par2, par3);
    }

    @Override
    protected void actionPerformed(GuiButton par1GuiButton) {
        if(par1GuiButton.id == 0){
            setPlayerHasAgreed(true);
            this.mc.displayGuiScreen(this.parentGuiScreen);
        } else if(par1GuiButton.id == 1){
            this.mc.displayGuiScreen(this.parentGuiScreen);
        }
    }

    @Override
    public void initGui() {
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, this.width / 2 - 155, this.height / 2 + 60, 150, 20, "yes I know what I'm doing"));
        this.buttonList.add(new GuiButton(1, this.width / 2 + 5, this.height / 2 + 60, 150, 20, "go back"));
    }
}
