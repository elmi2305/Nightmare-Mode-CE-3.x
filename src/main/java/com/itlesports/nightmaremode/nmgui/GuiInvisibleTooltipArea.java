package com.itlesports.nightmaremode.nmgui;

import net.minecraft.src.FontRenderer;
import net.minecraft.src.GuiButton;
import net.minecraft.src.Minecraft;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class GuiInvisibleTooltipArea extends GuiButton {
    private final String tooltipText;

    public GuiInvisibleTooltipArea(int id, int x, int y, int width, int height, String tooltip) {
        super(id, x, y, width, height, "");
        this.tooltipText = tooltip;
        this.drawButton = true;
        this.enabled = false;
    }

    public String getTooltipText() {
        return tooltipText;
    }
    public boolean getHover(){
        return this.field_82253_i;
    }
    @Override
    public boolean mousePressed(Minecraft par1Minecraft, int par2, int par3) {
        return this.enabled && par2 >= this.xPosition && par3 >= this.yPosition && par2 < this.xPosition + this.width && par3 < this.yPosition + this.height;
    }

    @Override
    public void drawButton(Minecraft par1Minecraft, int par2, int par3) {
        this.field_82253_i = par2 >= this.xPosition && par3 >= this.yPosition && par2 < this.xPosition + this.width && par3 < this.yPosition + this.height;
    }

    private List<String> wrapTextByChars(String text, FontRenderer fontRenderer) {
        List<String> lines = new ArrayList<>();
        String[] words = text.split(" ");

        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            if (fontRenderer.getStringWidth(currentLine + word) > 100) {
                // Move to the next line if the word would exceed max width
                lines.add(currentLine.toString().trim());
                currentLine = new StringBuilder();
            }
            currentLine.append(word).append(" ");
        }

        // Add the last line if not empty
        if (!currentLine.toString().trim().isEmpty()) {
            lines.add(currentLine.toString().trim());
        }

        return lines;
    }

    public void drawTooltip(Minecraft mc, int buttonX, int buttonY, int buttonWidth, int buttonHeight, String text) {
        FontRenderer fontRenderer = mc.fontRenderer;
        List<String> lines = wrapTextByChars(text, fontRenderer);

        int maxWidth = 0;
        for (String line : lines) {
            int lineWidth = fontRenderer.getStringWidth(line);
            if (lineWidth > maxWidth) {
                maxWidth = lineWidth;
            }
        }

        int boxWidth = maxWidth + 8;
        int boxHeight = (lines.size() * 10) + 4;

        // Center vertically on button
        int boxY = buttonY + (buttonHeight / 2) - (boxHeight / 2);

        // Decide whether to render tooltip on the left or right of the button
        boolean shouldRenderLeft = buttonX + buttonWidth / 2 > mc.currentScreen.width / 2;

        int boxX;
        if (shouldRenderLeft) {
            // Place to the left of the button
            boxX = buttonX - boxWidth - 6;
        } else {
            // Place to the right of the button
            boxX = buttonX + buttonWidth + 6;
        }

        // Save OpenGL state
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();

        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        int borderThickness = 1;
        drawRect(
                boxX - borderThickness,
                boxY - borderThickness,
                boxX + boxWidth + borderThickness,
                boxY + boxHeight + borderThickness,
                0x99FFFFFF
        );

        drawRect(boxX, boxY, boxX + boxWidth, boxY + boxHeight, 0xAA000000);

        GL11.glEnable(GL11.GL_TEXTURE_2D);

        int yOffset = 2;
        for (String line : lines) {
            fontRenderer.drawStringWithShadow(line, boxX + 4, boxY + yOffset, 0xFFFFFF);
            yOffset += 10;
        }

        GL11.glPopMatrix();
        GL11.glPopAttrib();
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }
}