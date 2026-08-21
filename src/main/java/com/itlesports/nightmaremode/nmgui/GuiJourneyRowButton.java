package com.itlesports.nightmaremode.nmgui;

import net.minecraft.src.FontRenderer;
import net.minecraft.src.GuiButton;
import net.minecraft.src.Minecraft;

/** A two-line, left-aligned main-menu button. */
public class GuiJourneyRowButton extends GuiButton {
    private final String subtitle;

    public GuiJourneyRowButton(int id, int x, int y, int width, String title, String subtitle) {
        super(id, x, y, width, 30, title);
        this.subtitle = subtitle;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (!this.drawButton) return;
        JourneyTitleTheme theme = JourneyTitleTheme.getActive(mc);
        this.field_82253_i = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
        int fill = this.enabled ? (this.field_82253_i ? theme.buttonHoverFill : theme.buttonFill) : 0x80503B26;
        int edge = this.field_82253_i && this.enabled ? theme.textHighlight : theme.edge;
        drawRect(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + this.height, fill);
        drawRect(this.xPosition, this.yPosition, this.xPosition + 1, this.yPosition + this.height, edge);
        drawRect(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + 1, edge);
        drawRect(this.xPosition, this.yPosition + this.height - 1, this.xPosition + this.width, this.yPosition + this.height, 0xAA3C2918);
        FontRenderer font = mc.fontRenderer;
        int titleColor = this.enabled ? (this.field_82253_i ? theme.textHighlight : theme.text) : 0xFF8B7657;
        int subtitleColor = this.enabled ? (this.field_82253_i ? theme.textHighlight : theme.textMuted) : 0xFF78654B;
        font.drawString(this.displayString, this.xPosition + 9, this.yPosition + 6, titleColor);
        font.drawString(this.subtitle, this.xPosition + 9, this.yPosition + 18, subtitleColor);
    }
}
