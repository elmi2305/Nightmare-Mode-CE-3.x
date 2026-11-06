package com.itlesports.nightmaremode.nmgui;

import com.itlesports.nightmaremode.util.interfaces.JourneyMenuBackdrop;
import com.itlesports.nightmaremode.world.BalanceProfile;
import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiScreen;
import org.lwjgl.opengl.GL11;

public final class GuiJourneySettings extends GuiScreen {
    private final GuiScreen parent;
    private int cardX;
    private int cardWidth;
    private int top;

    public GuiJourneySettings(GuiScreen parent) { this.parent = parent; }

    @Override public void initGui() {
        this.buttonList.clear();
        this.cardWidth = Math.min(460, this.width - 24);
        this.cardX = (this.width - this.cardWidth) / 2;
        this.top = Math.max(2, (this.height - 270) / 2);
        this.buttonList.add(new GuiJourneySmallButton(1, this.cardX + this.cardWidth - 100, this.top + 74, 84,
                BalanceProfile.name(BalanceProfile.combat(BalanceProfile.pending()))));
        this.buttonList.add(new GuiJourneySmallButton(2, this.cardX + this.cardWidth - 100, this.top + 122, 84,
                BalanceProfile.name(BalanceProfile.progression(BalanceProfile.pending()))));
        this.buttonList.add(new GuiJourneySmallButton(0, this.width / 2 - 60, this.height - 32, 120, "Done"));
    }

    @Override protected void actionPerformed(GuiButton button) {
        if (button.id == 0) {
            this.mc.displayGuiScreen(this.parent);
            return;
        }
        String combat = BalanceProfile.combat(BalanceProfile.pending());
        String progression = BalanceProfile.progression(BalanceProfile.pending());
        if (button.id == 1) combat = BalanceProfile.EASY.equals(combat) ? BalanceProfile.HARD : BalanceProfile.EASY;
        if (button.id == 2) progression = BalanceProfile.LOW.equals(progression) ? BalanceProfile.HIGH : BalanceProfile.LOW;
        BalanceProfile.selectPending(combat, progression);
        this.initGui();
    }

    @Override protected void keyTyped(char character, int keyCode) {
        if (keyCode == 1) this.mc.displayGuiScreen(this.parent);
        else super.keyTyped(character, keyCode);
    }

    @Override public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        JourneyTitleTheme theme = JourneyTitleTheme.getActive(this.mc);
        if (this.parent instanceof JourneyMenuBackdrop backdrop) {
            backdrop.nightmareMode$drawJourneyBackdrop(mouseX, mouseY, partialTicks, this.width, this.height);
        } else this.drawDefaultBackground();
        drawRect(this.cardX, this.top, this.cardX + this.cardWidth, this.height - 8, 0xDC000000 | (theme.cardFill & 0xFFFFFF));
        drawRect(this.cardX, this.top, this.cardX + this.cardWidth, this.top + 1, theme.edge);
        drawJourneyTitle();

        String selected = BalanceProfile.pending();
        this.drawString(this.fontRenderer, "Combat difficulty", this.cardX + 16, this.top + 80, theme.textHighlight);
        this.fontRenderer.drawSplitString(BalanceProfile.EASY.equals(BalanceProfile.combat(selected))
                ? "Gentler survival. Weaker mobs, less hunger drain, slower spoilage and milder death penalties."
                : "The full combat and survival challenge. Intended baseline.", this.cardX + 16, this.top + 100, this.cardWidth - 32, theme.text);
        this.drawString(this.fontRenderer, "Progression effort", this.cardX + 16, this.top + 128, theme.textHighlight);
        this.fontRenderer.drawSplitString(BalanceProfile.LOW.equals(BalanceProfile.progression(selected))
                ? "Less grind, more resources, faster harvesting, lower pollution, easier recipes and skills."
                : "The full progression. Standard resources, recipes, work speeds and skill requirements.",
                this.cardX + 16, this.top + 148, this.cardWidth - 32, theme.text);

        int footer = Math.max(this.top + 177, this.height - 72);
        this.drawCenteredString(this.fontRenderer, BalanceProfile.restartRequired() ? "Saved - restart the game to apply" : "Current settings active",
                this.width / 2, footer, BalanceProfile.restartRequired() ? 0xFFD079 : theme.textHighlight);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void drawJourneyTitle() {
        String selected = BalanceProfile.pending();
        boolean easy = BalanceProfile.EASY.equals(BalanceProfile.combat(selected));
        boolean low = BalanceProfile.LOW.equals(BalanceProfile.progression(selected));
        int start = easy ? (low ? 0x70E8BC : 0x82CFFF) : (low ? 0xFFC578 : 0xFFD76C);
        int end = easy ? (low ? 0x72C8FF : 0xB8A0FF) : (low ? 0xFF797E : 0xF07555);
        String title = BalanceProfile.journeyName(selected);
        int textWidth = this.fontRenderer.getStringWidth("\u00a7l" + title);
        float scale = Math.min(2.4F, (this.cardWidth - 28) / (float)textWidth);
        GL11.glPushMatrix();
        GL11.glTranslatef(this.width / 2.0F - textWidth * scale / 2.0F, this.top + 25, 0);
        GL11.glScalef(scale, scale, 1);
        int x = 0;
        for (int i = 0; i < title.length(); i++) {
            float blend = i / (float)Math.max(1, title.length() - 1);
            int color = 0;
            for (int shift = 0; shift <= 16; shift += 8) {
                int a = (start >> shift) & 255;
                int b = (end >> shift) & 255;
                color |= ((int)(a + (b - a) * blend)) << shift;
            }
            String letter = "\u00a7l" + title.charAt(i);
            this.fontRenderer.drawStringWithShadow(letter, x, 0, color);
            x += this.fontRenderer.getStringWidth(letter);
        }
        GL11.glPopMatrix();
    }
}
