package com.itlesports.nightmaremode.nmgui;

import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiMainMenu;
import net.minecraft.src.GuiScreen;

public final class GuiBalanceMessage extends GuiScreen {
    private final String message;
    public GuiBalanceMessage(String message) { this.message = message; }
    @Override public void initGui() {
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 2 + 55, "Back to main menu"));
    }
    @Override protected void actionPerformed(GuiButton button) { this.mc.displayGuiScreen(new GuiMainMenu()); }
    @Override public void drawScreen(int x, int y, float ticks) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, "Journey difficulty", this.width / 2, this.height / 2 - 65, 0xFFFFFF);
        this.fontRenderer.drawSplitString(this.message, this.width / 2 - 140, this.height / 2 - 35, 280, 0xFFFFFF);
        super.drawScreen(x, y, ticks);
    }
}
