package com.itlesports.nightmaremode.nmgui;

import com.itlesports.nightmaremode.util.NMConfUtils;
import net.minecraft.src.FontRenderer;
import net.minecraft.src.GuiButton;
import net.minecraft.src.Minecraft;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class GuiColoredButton extends GuiButton {
    private final int textColorDisabled;
    private final int textColorHover;
    private int textColorUnpressed;
    private int textColorActive;
    private int baseColor;
    private int baseColorActive;
    private String tooltipText;
    private boolean state;
    public boolean shouldDrawToolTip;
    private NMConfUtils.CONFIG configValue;

    public GuiColoredButton(int id, int x, int y, int width, int height, String label,
                            int hoverTextColor, int unpressedTextColor, int baseColor) {
        this(id, x, y, width, height, label, hoverTextColor, unpressedTextColor, 0, baseColor);
        // Used in selectWorldMixin to render the NM Config button
    }

    public GuiColoredButton(int id, int x, int y, int width, int height, String label,
                            int hoverTextColor, int unpressedTextColor, int activeTextColor, int baseColor) {
        super(id, x, y, width, height, label);
        this.textColorDisabled = -6250336; // Default disabled text color
        this.textColorHover = hoverTextColor;
        this.textColorUnpressed = unpressedTextColor;
        this.textColorActive = activeTextColor;
        this.baseColor = baseColor;
    }

    public GuiColoredButton(int id, int x, int y, int width, int height, String label,
                            int hoverTextColor, int unpressedTextColor, int activeTextColor,
                            int baseColorInactive, int baseColorActive) {
        this(id, x, y, width, height, label, hoverTextColor, unpressedTextColor, activeTextColor, baseColorInactive);
        this.baseColorActive = baseColorActive;
        // Used for the configs
    }

    // New constructor with default white text colors
    public GuiColoredButton(int id, int x, int y, int width, int height, String label,
                            int baseColorInactive, int baseColorActive) {
        this(id, x, y, width, height, label, 0xFFFFFF, 0xD4D4D4, 0xFFFFFF, baseColorInactive, baseColorActive);
    }


    public void updateState(boolean state){
        this.state = state;
    }
    public void changeColors(int baseColor, int baseColorActive, int textColorUnpressed, int textColorActive){
        this.baseColor = baseColor;
        this.baseColorActive = baseColorActive;
        this.textColorUnpressed = textColorUnpressed;
        this.textColorActive = textColorActive;
    }

    @Override
    public void drawButton(Minecraft par1Minecraft, int par2, int par3) {
        if (this.drawButton) {
            FontRenderer var4 = par1Minecraft.fontRenderer;
            par1Minecraft.getTextureManager().bindTexture(buttonTextures);

            // Apply base color
            float r, g, b;
            if (this.baseColorActive != 0 && this.state) {
                r = ((this.baseColorActive >> 16) & 0xFF) / 255.0F;
                g = ((this.baseColorActive >> 8) & 0xFF) / 255.0F;
                b = (this.baseColorActive & 0xFF) / 255.0F;
            } else {
                r = ((this.baseColor >> 16) & 0xFF) / 255.0F;
                g = ((this.baseColor >> 8) & 0xFF) / 255.0F;
                b = (this.baseColor & 0xFF) / 255.0F;
            }
            GL11.glColor4f(r, g, b, 1.0F);

            this.field_82253_i = par2 >= this.xPosition && par3 >= this.yPosition &&
                    par2 < this.xPosition + this.width && par3 < this.yPosition + this.height;
            int var5 = this.getHoverState(this.field_82253_i);

            this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 46 + var5 * 20, this.width / 2, this.height);
            this.drawTexturedModalRect(this.xPosition + this.width / 2, this.yPosition, 200 - this.width / 2, 46 + var5 * 20, this.width / 2, this.height);
            this.mouseDragged(par1Minecraft, par2, par3);

            // Determine text color
            int textColor = -6250336;
            if (!this.enabled) {
                textColor = this.textColorDisabled; // button cannot be clicked
            } else if (this.field_82253_i) {
                this.shouldDrawToolTip = true;
                textColor = this.textColorHover; // button is being hovered on
            } else{
                this.shouldDrawToolTip = false;
                if (this.state) {
                    textColor = this.textColorActive; // button is active
                } else if (this.textColorUnpressed != 0) {
                    textColor = this.textColorUnpressed; // button is not pressed
                }
            }
            this.drawCenteredString(var4, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, textColor);
        }
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
                0xAAFFFFFF
        );

        drawRect(boxX, boxY, boxX + boxWidth, boxY + boxHeight, 0xDF000000);

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

    private List<String> wrapTextByChars(String text, FontRenderer fontRenderer) {
        List<String> lines = new ArrayList<>();
        String[] words = text.split(" ");

        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            if (fontRenderer.getStringWidth(currentLine + word) > 200) {
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


    public void setTooltipText(String tooltipText) {
        this.tooltipText = tooltipText;
    }
    public String getTooltipText() {
        return this.tooltipText;
    }
    public NMConfUtils.CONFIG getConfigValue(){
        return this.configValue;
    }

    public void setConfigValue(NMConfUtils.CONFIG conf){
        this.configValue = conf;
    }
}
