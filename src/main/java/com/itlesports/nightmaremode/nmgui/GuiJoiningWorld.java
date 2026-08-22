package com.itlesports.nightmaremode.nmgui;

import net.minecraft.src.GuiScreen;
import net.minecraft.src.I18n;

/**
 * Briefly displayed after an integrated server has started and before its
 * login packet creates a client world. GuiDownloadTerrain takes over once the
 * packet arrives.
 */
public class GuiJoiningWorld extends GuiScreen {
    @Override
    public void initGui() {
        this.buttonList.clear();
    }

    @Override
    protected void keyTyped(char character, int keyCode) {
        // Joining is not cancellable at this stage; ignore input rather than
        // exposing the main-menu controls while the world is incomplete.
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawBackground(0);
        this.drawCenteredString(this.fontRenderer, I18n.getString("multiplayer.downloadingTerrain"),
                this.width / 2, this.height / 2 - 50, 0xFFFFFF);
    }
}
