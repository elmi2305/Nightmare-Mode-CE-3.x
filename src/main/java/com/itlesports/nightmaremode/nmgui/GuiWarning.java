package com.itlesports.nightmaremode.nmgui;

import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.I18n;

public class GuiWarning extends GuiScreen {
    private static boolean playerHasAgreed = false;
    private final GuiScreen parentGuiScreen;
    private String line1 = I18n.getString("gui.warning.line1");
    private String line2 = I18n.getString("gui.warning.line2");
    private String line3 = I18n.getString("gui.warning.line3");
    private String line4 = I18n.getString("gui.warning.line4");
    private String line5 = I18n.getString("gui.warning.line5");

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

        this.drawCenteredString(this.fontRenderer, this.line1, this.width / 2, this.height / 2 - 35, 0xFFFFFF);
        this.drawCenteredString(this.fontRenderer, this.line2, this.width / 2, this.height / 2 - 20, 0xFFFFFF);
        this.drawCenteredString(this.fontRenderer, this.line3, this.width / 2, this.height / 2 - 5, 0xFFFFFF);
        this.drawCenteredString(this.fontRenderer, this.line4, this.width / 2, this.height / 2 + 10, 0xFFFFFF);
        this.drawCenteredString(this.fontRenderer, this.line5, this.width / 2, this.height / 2 + 40, 0xFFFFFF);


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
    public void setLine1(String line1) {
        this.line1 = line1;
    }

    public void setLine2(String line2) {
        this.line2 = line2;
    }

    public void setLine3(String line3) {
        this.line3 = line3;
    }

    public void setLine4(String line4) {
        this.line4 = line4;
    }

    public void setLine5(String line5) {
        this.line5 = line5;
    }


    @Override
    public void initGui() {
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, this.width / 2 - 155, this.height / 2 + 60, 150, 20, I18n.getString("gui.warning.iknow")));
        this.buttonList.add(new GuiButton(1, this.width / 2 + 5, this.height / 2 + 60, 150, 20, I18n.getString("gui.warning.goback")));
    }
}
