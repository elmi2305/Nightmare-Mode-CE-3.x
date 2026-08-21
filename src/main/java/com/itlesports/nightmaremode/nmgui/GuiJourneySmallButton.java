package com.itlesports.nightmaremode.nmgui;

import net.minecraft.src.GuiButton;
import net.minecraft.src.Minecraft;

/** Small sandstone action used by the title-screen world card. */
public class GuiJourneySmallButton extends GuiButton {
    public GuiJourneySmallButton(int id, int x, int y, int width, String label) { super(id, x, y, width, 20, label); }
    @Override public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (!this.drawButton) return;
        JourneyTitleTheme theme = JourneyTitleTheme.getActive(mc);
        this.field_82253_i = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
        int fill = this.field_82253_i && this.enabled ? theme.buttonHoverFill : theme.buttonFill;
        int edge = this.field_82253_i && this.enabled ? theme.textHighlight : theme.edge;
        drawRect(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + this.height, fill);
        drawRect(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + 1, edge);
        drawRect(this.xPosition, this.yPosition, this.xPosition + 1, this.yPosition + this.height, edge);
        this.drawCenteredString(mc.fontRenderer, this.displayString, this.xPosition + this.width / 2, this.yPosition + 6, this.enabled ? theme.textHighlight : 0xFF8B7657);
    }
}
