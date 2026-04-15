package com.itlesports.nightmaremode.nmgui;

import net.minecraft.src.GuiButton;
import net.minecraft.src.Minecraft;
import net.minecraft.src.ResourceLocation;
import net.minecraft.src.Tessellator;
import org.lwjgl.opengl.GL11;

public class GuiTexturedButton extends GuiButton {

    private final ResourceLocation texture;

    public GuiTexturedButton(int id, int x, int y, int width, int height, String texturePath) {
        super(id, x, y, width, height, "");
        this.texture = new ResourceLocation(texturePath);
    }

    public GuiTexturedButton(int id, int x, int y, int width, int height, ResourceLocation texturePath) {
        super(id, x, y, width, height, "");
        this.texture = texturePath;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (!this.drawButton) return;

        // detect hover
        this.field_82253_i = mouseX >= this.xPosition && mouseY >= this.yPosition &&
                mouseX < this.xPosition + this.width &&
                mouseY < this.yPosition + this.height;

        // draw default button background
        mc.getTextureManager().bindTexture(GuiButton.buttonTextures);
        GL11.glColor4f(1F, 1F, 1F, 1F);

        int hoverState = this.getHoverState(this.field_82253_i);

        this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 46 + hoverState * 20, this.width / 2, this.height);
        this.drawTexturedModalRect(this.xPosition + this.width / 2, this.yPosition, 200 - this.width / 2, 46 + hoverState * 20, this.width / 2, this.height);

        // draw centered custom texture
        drawCenteredTexture(mc);

        // mouse drag logic (vanilla)
        this.mouseDragged(mc, mouseX, mouseY);
    }

    private void drawCenteredTexture(Minecraft mc) {
        mc.getTextureManager().bindTexture(this.texture);

        // enable transparency
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL11.glColor4f(1F, 1F, 1F, 1F);

        int texSize = Math.min(this.width, this.height);
        int drawX = this.xPosition + (this.width - texSize) / 2;
        int drawY = this.yPosition + (this.height - texSize) / 2;

        Tessellator t = Tessellator.instance;

        t.startDrawingQuads();
        t.addVertexWithUV(drawX,           drawY + texSize, 0, 0, 1);
        t.addVertexWithUV(drawX + texSize, drawY + texSize, 0, 1, 1);
        t.addVertexWithUV(drawX + texSize, drawY,           0, 1, 0);
        t.addVertexWithUV(drawX,           drawY,           0, 0, 0);
        t.draw();

        // restore state
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_BLEND);
    }
}