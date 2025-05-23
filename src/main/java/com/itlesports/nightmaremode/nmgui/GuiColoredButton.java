package com.itlesports.nightmaremode.nmgui;

import net.minecraft.src.FontRenderer;
import net.minecraft.src.GuiButton;
import net.minecraft.src.Minecraft;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class GuiColoredButton extends GuiButton {
    public static final int STANDARD_DISABLED = -6250336;
    public static final int STANDARD_ENABLED = 16777120;
    public static final int STANDARD_HOVER = 14737632;
    public static final int STANDARD_BASE = 0xFFFFFF;
    public static final int OLD_HOVER = 0xC74C50;


    private final int textColorDisabled;
    private final int textColorHover;
    private final int textColorUnpressed;
    private final int textColorActive;
    private final int baseColor;
    private int baseColorActive;
    private String tooltipText;
    private boolean state;
    public boolean shouldDrawToolTip;

    public GuiColoredButton(int par1, int par2, int par3, int par4, int par5, String par6Str,
                            int textColorHover, int textColorUnpressed, int baseColor) {
        this(par1, par2, par3, par4, par5, par6Str, textColorHover, textColorUnpressed, 0, baseColor);
        // only used in selectWorldMixin to render the NM Config button
    }

    public GuiColoredButton(int par1, int par2, int par3, int par4, int par5, String par6Str,
                            int textColorHover, int textColorUnpressed, int textColorActive, int baseColor) {
        super(par1, par2, par3, par4, par5, par6Str);
        this.textColorDisabled = -6250336;
        this.textColorHover = textColorHover;
        this.textColorUnpressed = textColorUnpressed;
        this.textColorActive = textColorActive;
        this.baseColor = baseColor;
    }

    public GuiColoredButton(int par1, int par2, int par3, int par4, int par5, String par6Str,
                            int hover, int unpressed, int active, int baseColor, int baseColorActive) {
        this(par1, par2, par3, par4, par5, par6Str, hover, unpressed, active, baseColor);
        this.baseColorActive = baseColorActive;
        // used for the configs
    }
    // New constructor with forced text colors
    public GuiColoredButton(int par1, int par2, int par3, int par4, int par5, String par6Str, int baseColor, int baseColorActive) {
        this(par1, par2, par3, par4, par5, par6Str, 0xFFFFFF, 0xD4D4D4, 0xFFFFFF, baseColor, baseColorActive);
    }

    public void updateState(boolean state){
        this.state = state;
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

        drawRect(boxX, boxY, boxX + boxWidth, boxY + boxHeight, 0xCC000000);

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
}
