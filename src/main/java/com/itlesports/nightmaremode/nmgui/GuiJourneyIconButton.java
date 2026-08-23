package com.itlesports.nightmaremode.nmgui;

import net.minecraft.src.GuiButton;
import net.minecraft.src.Minecraft;

/** Small hand-drawn-style icon button for the Journey Mode title screen. */
public class GuiJourneyIconButton extends GuiButton {
    public enum Icon { OPTIONS, LANGUAGE, QUIT }
    private final Icon icon;
    public GuiJourneyIconButton(int id, int x, int y, Icon icon) { super(id, x, y, 24, 24, ""); this.icon = icon; }
    @Override public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (!this.drawButton) return;
        JourneyTitleTheme theme = JourneyTitleTheme.getActive(mc);
        this.field_82253_i = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
        int fill = this.enabled ? (this.field_82253_i ? theme.buttonHoverFill : theme.buttonFill) : 0x80503B26;
        int edge = this.field_82253_i && this.enabled ? theme.textHighlight : theme.edge;
        drawRect(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + this.height, fill);
        drawRect(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + 1, edge);
        drawRect(this.xPosition, this.yPosition, this.xPosition + 1, this.yPosition + this.height, edge);
        drawRect(this.xPosition + this.width - 1, this.yPosition, this.xPosition + this.width, this.yPosition + this.height, 0xAA3C2918);
        drawIcon(mc, this.xPosition + 12, this.yPosition + 12, this.field_82253_i ? theme.textHighlight : theme.text);
    }
    private void drawIcon(Minecraft mc, int cx, int cy, int color) {
        if (this.icon == Icon.LANGUAGE) mc.fontRenderer.drawString("A", cx - 3, cy - 4, color);
        else if (this.icon == Icon.QUIT) {
            for (int offset = -4; offset <= 4; offset++) {
                drawRect(cx + offset, cy + offset, cx + offset + 1, cy + offset + 1, color);
                drawRect(cx - offset, cy + offset, cx - offset + 1, cy + offset + 1, color);
            }
        }
        else { drawRect(cx - 5, cy - 4, cx + 5, cy - 3, color); drawRect(cx - 5, cy - 1, cx + 5, cy, color); drawRect(cx - 5, cy + 2, cx + 5, cy + 3, color); drawRect(cx - 2, cy - 5, cx - 1, cy - 2, color); drawRect(cx + 2, cy - 2, cx + 3, cy + 1, color); drawRect(cx, cy + 1, cx + 1, cy + 4, color); }
    }
}
