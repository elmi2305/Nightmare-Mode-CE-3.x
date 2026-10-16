package com.itlesports.nightmaremode.nmgui;

import com.itlesports.nightmaremode.block.tileEntities.TileEntityHammerAnvil;
import com.itlesports.nightmaremode.util.AutoAnvilUpgrade;
import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;

import java.util.Collections;
import java.util.Locale;

public abstract class GuiAutomaticAnvil extends GuiContainer {
    private final ResourceLocation texture;
    private final TileEntityHammerAnvil anvil;
    private final int progressX;

    protected GuiAutomaticAnvil(Container container, TileEntityHammerAnvil anvil, String texture, int progressX) {
        super(container);
        this.anvil = anvil;
        this.texture = new ResourceLocation("nightmare:textures/gui/" + texture + ".png");
        this.progressX = progressX;
        this.ySize = 178;
    }

    @Override public void initGui() {
        super.initGui();
        this.buttonList.add(new AnvilToggleButton(0, this.guiLeft + 132, this.guiTop + 5));
        this.buttonList.add(new AnvilToggleButton(1, this.guiLeft + 152, this.guiTop + 5));
    }

    @Override protected void actionPerformed(GuiButton button) {
        if (button instanceof AnvilToggleButton && button.enabled && (button.id == 0 || button.id == 1)) {
            this.mc.playerController.sendEnchantPacket(this.inventorySlots.windowId, button.id);
        }
    }

    @Override protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        AutoAnvilUpgrade upgrade = this.anvil.getUpgrade();
        String key = upgrade == null ? "container.ifhyAutoAnvil"
                : "container.ifhyAutoAnvil." + upgrade.name().toLowerCase(Locale.ROOT);
        this.fontRenderer.drawString(I18n.getString(key), 8, 10, 0x404040);
        boolean unlimited = upgrade != null && upgrade.preventsAnvilWear() || this.anvil.getMaxUses() < 0;
        if (!unlimited) {
            String wear = I18n.getStringParams("gui.ifhyAutoAnvil.uses", this.anvil.getUsesRemaining(), this.anvil.getMaxUses());
            this.fontRenderer.drawString(wear, 8, 62, 0x404040);
        }
        this.fontRenderer.drawString(I18n.getString("container.inventory"), 8, 85, 0x404040);
    }

    @Override protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GL11.glColor4f(1, 1, 1, 1);
        this.mc.getTextureManager().bindTexture(this.texture);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        int required = this.anvil.getProcessTicksRequired();
        if (required > 0) {
            int width = Math.min(24, this.anvil.getProcessTicks() * 24 / required);
            AutoAnvilUpgrade upgrade = this.anvil.getUpgrade();
            drawRect(this.guiLeft + this.progressX, this.guiTop + 43,
                    this.guiLeft + this.progressX + width, this.guiTop + 49,
                    0xFF000000 | (upgrade == null ? 0xFFFFFF : upgrade.color));
        }
    }

    @Override public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        for (Object entry : this.buttonList) {
            if (!(entry instanceof AnvilToggleButton button)) continue;
            if (mouseX >= button.xPosition && mouseX < button.xPosition + 18
                    && mouseY >= button.yPosition && mouseY < button.yPosition + 18) {
                String key = button.id == 0 ? this.anvil.isSoundMuted() ? "unmute" : "mute"
                        : this.anvil.isHammerHidden() ? "showHammer" : "hideHammer";
                this.func_102021_a(Collections.singletonList(I18n.getString("gui.ifhyAutoAnvil." + key)), mouseX, mouseY);
            }
        }
    }

    private class AnvilToggleButton extends GuiButton {
        private final ResourceLocation icons = new ResourceLocation("nightmare:textures/gui/ifhyAnvilControls.png");

        AnvilToggleButton(int id, int x, int y) { super(id, x, y, 18, 18, ""); }

        @Override public void drawButton(Minecraft mc, int mouseX, int mouseY) {
            super.drawButton(mc, mouseX, mouseY);
            if (!this.drawButton) return;
            boolean off = this.id == 0 ? anvil.isSoundMuted() : anvil.isHammerHidden();
            GL11.glColor4f(1, 1, 1, 1);
            mc.getTextureManager().bindTexture(this.icons);
            this.drawTexturedModalRect(this.xPosition + 1, this.yPosition + 1,
                    (this.id * 2 + (off ? 1 : 0)) * 16, 0, 16, 16);
        }
    }
}
