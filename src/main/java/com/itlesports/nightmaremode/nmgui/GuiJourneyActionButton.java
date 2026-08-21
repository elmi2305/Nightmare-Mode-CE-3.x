package com.itlesports.nightmaremode.nmgui;

import net.minecraft.src.GuiButton;
import net.minecraft.src.Minecraft;

/** Themed replacement for normal 20px-wide menu actions, retaining vanilla behaviour. */
public class GuiJourneyActionButton extends GuiButton {
    public GuiJourneyActionButton(int id, int x, int y, int width, String label) {
        super(id, x, y, width, 20, label);
    }

    @Override
    public void drawButton(Minecraft minecraft, int mouseX, int mouseY) {
        if (!this.drawButton) return;
        JourneyTitleTheme theme = JourneyTitleTheme.getActive(minecraft);
        this.field_82253_i = mouseX >= this.xPosition && mouseY >= this.yPosition
                && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
        int fill = this.enabled ? (this.field_82253_i ? theme.buttonHoverFill : theme.buttonFill) : 0x80503B26;
        int edge = this.field_82253_i && this.enabled ? theme.textHighlight : theme.edge;
        drawRect(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + this.height, fill);
        drawRect(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + 1, edge);
        drawRect(this.xPosition, this.yPosition, this.xPosition + 1, this.yPosition + this.height, edge);
        drawRect(this.xPosition, this.yPosition + this.height - 1, this.xPosition + this.width, this.yPosition + this.height, 0xAA3C2918);
        this.drawCenteredString(minecraft.fontRenderer, this.displayString, this.xPosition + this.width / 2,
                this.yPosition + 6, this.enabled ? (this.field_82253_i ? theme.textHighlight : theme.text) : 0xFF8B7657);
    }
}
