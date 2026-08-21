package com.itlesports.nightmaremode.mixin.gui;

import com.itlesports.nightmaremode.nmgui.GuiJourneyIconButton;
import com.itlesports.nightmaremode.nmgui.GuiJourneyRowButton;
import com.itlesports.nightmaremode.nmgui.GuiJourneySmallButton;
import com.itlesports.nightmaremode.nmgui.JourneyTitleTheme;
import com.itlesports.nightmaremode.util.NMUtils;
import com.itlesports.nightmaremode.world.JourneyProfile;
import api.AddonHandler;
import btw.BTWMod;
import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Date;
import java.util.List;
import java.text.SimpleDateFormat;

@Mixin(GuiMainMenu.class)
public class GuiMainMenuMixin extends GuiScreen {
    @Shadow private String splashText;
    @Shadow private void renderSkybox(int mouseX, int mouseY, float partialTicks) {}

    @Unique private JourneyTitleTheme titleTheme;
    @Unique private long titleOpenedAt;
    @Unique private NMUtils.JourneyWorldSummary recentWorld;
    @Unique private int worldCardTop;
    @Unique private int worldCardBottom;
    // Supply this as a 256x16 horizontal atlas: each 16x16 cell maps to JourneyProfile's progression index.
    @Unique private static final ResourceLocation PROGRESS_ICONS = new ResourceLocation("nightmare:textures/menu/journeyProgressIcons.png");

    @Inject(method = "initGui", at = @At("TAIL"))
    private void journeyMode$layout(CallbackInfo ci) {
        this.titleTheme = JourneyTitleTheme.getActive(this.mc);
        this.titleOpenedAt = Minecraft.getSystemTime();
        int panelWidth = getPanelWidth();
        int x = 12;
        int iconY = this.height - 52;
        boolean compactLayout = iconY < 215 && iconY + 24 > 185;
        int rowWidth = compactLayout ? panelWidth - 120 : panelWidth - 24;
        int iconX = compactLayout ? panelWidth - 96 : x;
        this.buttonList.clear();
        if (this.mc.isDemo()) {
            this.buttonList.add(new GuiJourneyRowButton(11, x, 150, rowWidth, "Play Demo", "Begin your journey"));
            GuiButton resetDemo = new GuiJourneyRowButton(12, x, 185, rowWidth, "Reset Demo", "Start the demo anew");
            resetDemo.enabled = this.mc.getSaveLoader().getWorldInfo("Demo_World") != null;
            this.buttonList.add(resetDemo);
        } else {
            this.buttonList.add(new GuiJourneyRowButton(1, x, 150, rowWidth, "Singleplayer", "Continue your journey"));
            this.buttonList.add(new GuiJourneyRowButton(2, x, 185, rowWidth, "Multiplayer", "Journey with friends"));
        }

        this.buttonList.add(new GuiJourneyIconButton(0, iconX, iconY, GuiJourneyIconButton.Icon.OPTIONS));
        this.buttonList.add(new GuiJourneyIconButton(5, iconX + 30, iconY, GuiJourneyIconButton.Icon.LANGUAGE));
        this.buttonList.add(new GuiJourneyIconButton(4, iconX + 60, iconY, GuiJourneyIconButton.Icon.QUIT));
        this.refreshRecentWorld();
        this.worldCardTop = 225;
        this.worldCardBottom = iconY - 8;
        if (this.recentWorld != null && this.worldCardBottom - this.worldCardTop >= 100) {
            this.buttonList.add(new GuiJourneySmallButton(33, x, this.worldCardBottom - 24, 72, "Jump In"));
        }
    }

    /** Retain the existing title-screen anti-xray safeguard without depending on button-list indices. */
    @Inject(method = "updateScreen", at = @At("TAIL"))
    private void journeyMode$disableForXray(CallbackInfo ci) {
        if (AddonHandler.modList.keySet().toString().toLowerCase().contains("xray")) {
            this.splashText = "Probably Shouldn't Xray!";
            for (Object button : this.buttonList) ((GuiButton) button).enabled = false;
        }
    }

    @Inject(method = "actionPerformed", at = @At("TAIL"))
    private void journeyMode$jumpIntoRecentWorld(GuiButton button, CallbackInfo ci) {
        if (button.id != 33 || this.recentWorld == null || !button.enabled) return;
        try {
            this.mc.launchIntegratedServer(this.recentWorld.folderName(), this.recentWorld.displayName(),
                    new WorldSettings(this.recentWorld.worldInfo()));
            this.mc.statFileWriter.readStat(StatList.createWorldStat, 1);
        } catch (Throwable ignored) {
            // A deleted or damaged save should fail like a normal singleplayer launch, never crash the title screen.
        }
    }

    @ModifyArg(method = "drawPanorama", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/TextureManager;bindTexture(Lnet/minecraft/src/ResourceLocation;)V"))
    private ResourceLocation journeyMode$selectPanorama(ResourceLocation vanillaFace) {
        if (this.titleTheme == null) return vanillaFace;
        for (int i = 0; i < this.titleTheme.panorama.length; i++) {
            if (vanillaFace.getResourcePath().endsWith("panorama_" + i + ".png")) return this.titleTheme.panorama[i];
        }
        return vanillaFace;
    }

    @Inject(method = "drawScreen", at = @At("HEAD"), cancellable = true)
    private void journeyMode$drawScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        ci.cancel();
        this.renderSkybox(mouseX, mouseY, partialTicks);
        JourneyTitleTheme theme = this.titleTheme == null ? JourneyTitleTheme.getActive(this.mc) : this.titleTheme;
        int panelWidth = getPanelWidth();
        drawTintedPanel(panelWidth, theme);
        drawRect(panelWidth - 1, 0, panelWidth, this.height, theme.divider);
        int available = panelWidth - 24;
        int btwWidth = Math.min(available, 250);
        int btwHeight = btwWidth * 326 / 1182;
        drawTexture(theme.betterThanWolves, 24, 18, btwWidth, btwHeight);
        int journeyWidth = available;
        int journeyHeight = journeyWidth * 164 / 1362;
        int journeyY = 18 + btwHeight + 3;
        drawTexture(theme.journeyMode, 12, journeyY, journeyWidth, journeyHeight);
        drawTypedSplash(12, journeyY + journeyHeight + 10);
        if (this.recentWorld != null && this.worldCardBottom - this.worldCardTop >= 100) drawRecentWorldCard(panelWidth);
        this.drawString(this.fontRenderer, "Minecraft 1.6.4 - BTW CE V" + BTWMod.instance.getVersionString(), 12, this.height - 22, theme.textMuted);
        this.drawString(this.fontRenderer, "Copyright Mojang AB. Do not distribute!", 12, this.height - 12, theme.textMuted);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Unique private int getPanelWidth() { return Math.min(this.width - 20, Math.max(320, this.width * 35 / 100)); }

    @Unique private void refreshRecentWorld() {
        this.recentWorld = null;
        if (this.mc.isDemo()) return;
        try {
            List saves = this.mc.getSaveLoader().getSaveList();
            if (saves == null || saves.isEmpty()) return;
            saves.sort(null);
            SaveFormatComparator save = (SaveFormatComparator)saves.get(0);
            WorldInfo info = this.mc.getSaveLoader().getWorldInfo(save.getFileName());
            if (info != null) this.recentWorld = new NMUtils.JourneyWorldSummary(save.getFileName(), save.getDisplayName(), info,
                    info.getData(btw.community.nightmaremode.NightmareMode.JOURNEY_PROFILE));
        } catch (Throwable ignored) {
            // The card is optional; malformed or unavailable saves simply leave the title screen unchanged.
        }
    }

    @Unique private void drawRecentWorldCard(int panelWidth) {
        JourneyTitleTheme theme = this.titleTheme == null ? JourneyTitleTheme.getActive(this.mc) : this.titleTheme;
        int x = 12;
        int width = panelWidth - 24;
        int height = this.worldCardBottom - this.worldCardTop;
        drawRect(x, this.worldCardTop, x + width, this.worldCardBottom, theme.cardFill);
        drawRect(x, this.worldCardTop, x + width, this.worldCardTop + 1, theme.edge);
        drawRect(x, this.worldCardTop, x + 1, this.worldCardBottom, theme.edge);
        drawRect(x + width - 1, this.worldCardTop, x + width, this.worldCardBottom, 0x803C2918);
        int iconSize = 56;
        int iconX = x + 7;
        int iconY = this.worldCardTop + 7;
        drawTexture(theme.worldIcon, iconX, iconY, iconSize, iconSize);
        int detailsX = iconX + iconSize + 7;
        drawScaledString(trimToWidth(this.recentWorld.displayName(), (int) ((x + width - detailsX - 7) / 1.25F)), detailsX, iconY + 2, 1.25F, theme.textHighlight);
        JourneyProfile data = this.recentWorld.profile();
        if (!data.valid) {
            this.drawString(this.fontRenderer, "World records: N/A", detailsX, iconY + 16, theme.textMuted);
            return;
        }
        // This four-part stack remains within the 56px high world art: title, state, total, then the achievement icon.
        this.drawString(this.fontRenderer, worldStateName(data.worldState), detailsX, iconY + 17, theme.text);
        this.drawString(this.fontRenderer, "Total completion: " + formatTotalCompletion(data) + "%", detailsX, iconY + 28, theme.textMuted);
        drawProgressIcon(detailsX, iconY + 40, data.progressIndex, theme);
        int statsY = iconY + iconSize + 6;
        int actionY = this.worldCardBottom - 24;
        if (statsY + 8 <= actionY) this.drawString(this.fontRenderer, "Playtime " + formatPlaytime(data.playTicks) + "  |  Created " + formatDate(data.createdAt), x + 8, statsY, theme.textMuted);
        if (statsY + 20 <= actionY) this.drawString(this.fontRenderer, "Deaths " + data.deaths + "  |  Kills " + data.kills + "  |  Joined " + data.joins + " times", x + 8, statsY + 12, theme.textMuted);
        if (statsY + 32 <= actionY) this.drawString(this.fontRenderer,
                "Progress " + (data.progressIndex + 1) + "/" + JourneyProfile.progressCount() + " | Skills " + data.getSkillCompletionPercent() + "%",
                x + 8, statsY + 24, theme.textMuted);
    }

    @Unique private void drawProgressIcon(int x, int y, int index, JourneyTitleTheme theme) {
        try {
            this.mc.getResourceManager().getResource(PROGRESS_ICONS);
            this.mc.getTextureManager().bindTexture(PROGRESS_ICONS);
            // The supplied atlas is 256x16, not the 256x256 sheet assumed by drawTexturedModalRect.
            int cell = Math.max(0, Math.min(15, index));
            float u0 = cell / 16.0F;
            float u1 = (cell + 1) / 16.0F;
            GL11.glColor4f(((theme.textHighlight >> 16) & 255) / 255.0F, ((theme.textHighlight >> 8) & 255) / 255.0F,
                    (theme.textHighlight & 255) / 255.0F, 1.0F);
            Tessellator tessellator = Tessellator.instance;
            tessellator.startDrawingQuads();
            tessellator.addVertexWithUV(x, y + 16, 0, u0, 1.0F);
            tessellator.addVertexWithUV(x + 16, y + 16, 0, u1, 1.0F);
            tessellator.addVertexWithUV(x + 16, y, 0, u1, 0.0F);
            tessellator.addVertexWithUV(x, y, 0, u0, 0.0F);
            tessellator.draw();
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        } catch (Throwable ignored) {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            drawRect(x, y, x + 16, y + 16, theme.cardFill);
            drawRect(x, y, x + 16, y + 1, theme.edge);
        }
    }

    @Unique private static String formatDate(long timestamp) { return timestamp <= 0 ? "N/A" : new SimpleDateFormat("dd/MM/yyyy").format(new Date(timestamp)); }
    @Unique private static String formatPlaytime(long ticks) {
        if (ticks <= 0) return "N/A";
        long seconds = ticks / 20L, days = seconds / 86400L;
        seconds %= 86400L;
        return days + "d " + String.format("%02d:%02d:%02d", seconds / 3600L, seconds / 60L % 60L, seconds % 60L);
    }
    @Unique private static int formatTotalCompletion(JourneyProfile data) {
        int milestoneTotal = JourneyProfile.progressCount();
        int milestoneComplete = Math.min(milestoneTotal, Math.max(0, data.progressIndex + 1));
        int skillTotal = Math.max(0, data.getSkillTotal());
        int total = milestoneTotal + skillTotal;
        return total == 0 ? 0 : (milestoneComplete + Math.min(skillTotal, Math.max(0, data.getCompletedSkillCount()))) * 100 / total;
    }
    @Unique private static String worldStateName(int state) { return new String[]{"Pre Hardmode", "Hardmode", "Post Wither", "Post Dragon"}[Math.max(0, Math.min(3, state))]; }

    @Unique private void drawScaledString(String text, int x, int y, float scale, int color) {
        GL11.glPushMatrix();
        GL11.glTranslatef(x, y, 0.0F);
        GL11.glScalef(scale, scale, 1.0F);
        this.drawString(this.fontRenderer, text, 0, 0, color);
        GL11.glPopMatrix();
    }

    @Unique private String trimToWidth(String text, int maximumWidth) {
        if (this.fontRenderer.getStringWidth(text) <= maximumWidth) return text;
        String ellipsis = "...";
        while (!text.isEmpty() && this.fontRenderer.getStringWidth(text + ellipsis) > maximumWidth) text = text.substring(0, text.length() - 1);
        return text + ellipsis;
    }

    @Unique private void drawTintedPanel(int panelWidth, JourneyTitleTheme theme) {
        panelWidth += 56;
        for (int left = 0; left < panelWidth; left += 4) {
            int right = Math.min(left + 4, panelWidth);
            int alpha = 170 * (panelWidth - left) / panelWidth;
            drawRect(left, 0, right, this.height, (alpha << 24) | theme.panelRgb);
        }
    }

    @Unique private void drawTexture(ResourceLocation texture, int x, int y, int width, int height) {
        this.mc.getTextureManager().bindTexture(texture); GL11.glColor4f(1, 1, 1, 1);
        Tessellator t = Tessellator.instance; t.startDrawingQuads();
        t.addVertexWithUV(x, y + height, 0, 0, 1); t.addVertexWithUV(x + width, y + height, 0, 1, 1);
        t.addVertexWithUV(x + width, y, 0, 1, 0); t.addVertexWithUV(x, y, 0, 0, 0); t.draw();
    }

    @Unique private void drawTypedSplash(int x, int y) {
        int shown = Math.min(this.splashText.length(), (int) ((Minecraft.getSystemTime() - this.titleOpenedAt) / 45L));
        String visible = this.splashText.substring(0, Math.max(0, shown));
        GL11.glPushMatrix(); GL11.glTranslatef(x, y, 0); GL11.glRotatef(-2.0F, 0, 0, 1);
        this.drawString(this.fontRenderer, visible, 0, 0, 0xFFE0B667); GL11.glPopMatrix();
    }
}
