package com.itlesports.nightmaremode.mixin.gui;

import com.itlesports.nightmaremode.nmgui.GuiJourneyIconButton;
import com.itlesports.nightmaremode.nmgui.GuiJourneyRowButton;
import com.itlesports.nightmaremode.nmgui.GuiJourneySmallButton;
import com.itlesports.nightmaremode.nmgui.JourneyBrowserBounds;
import com.itlesports.nightmaremode.nmgui.JourneyBrowserMode;
import com.itlesports.nightmaremode.nmgui.JourneyServerDialogAction;
import com.itlesports.nightmaremode.nmgui.JourneyTitleTheme;
import com.itlesports.nightmaremode.util.NMUtils;
import com.itlesports.nightmaremode.util.interfaces.JourneyBrowserInput;
import com.itlesports.nightmaremode.util.interfaces.JourneyMenuBackdrop;
import com.itlesports.nightmaremode.world.JourneyProfile;
import api.AddonHandler;
import btw.BTWMod;
import net.minecraft.src.*;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;

@Mixin(GuiMainMenu.class)
public class GuiMainMenuMixin extends GuiScreen implements JourneyBrowserInput, JourneyMenuBackdrop {
    @Shadow private String splashText;
    @Shadow private void renderSkybox(int mouseX, int mouseY, float partialTicks) {}
    @Shadow private int panoramaTimer;

    @Unique private JourneyTitleTheme titleTheme;
    @Unique private long titleOpenedAt;
    @Unique private long journeyMode$backdropLastTick;
    @Unique private NMUtils.JourneyWorldSummary recentWorld;
    @Unique private int worldCardTop;
    @Unique private int worldCardBottom;
    @Unique private JourneyBrowserMode browserMode = JourneyBrowserMode.NONE;
    @Unique private final List<SaveFormatComparator> browserWorlds = new ArrayList<SaveFormatComparator>();
    @Unique private ServerList browserServers;
    @Unique private final Set<String> browserFavorites = new HashSet<String>();
    @Unique private int browserSelected = -1;
    @Unique private static final int BROWSER_ROW_HEIGHT = 80;
    @Unique private float browserScroll;
    @Unique private float browserScrollVelocity;
    @Unique private boolean browserDraggingScrollbar;
    @Unique private int browserLastDragY;
    @Unique private long browserLastClick;
    @Unique private int browserLastClicked = -1;
    @Unique private JourneyServerDialogAction pendingServerAction = JourneyServerDialogAction.NONE;
    @Unique private ServerData pendingServerData;
    @Unique private int pendingServerIndex = -1;
    @Unique private int renamingWorldIndex = -1;
    @Unique private String inlineWorldName = "";
    @Unique private static final int CONFIRM_DELETE_WORLD = 9101;
    @Unique private static final int CONFIRM_DELETE_SERVER = 9102;
    // Supply this as a 256x16 horizontal atlas: each 16x16 cell maps to JourneyProfile's progression index.
    @Unique private static final ResourceLocation PROGRESS_ICONS = new ResourceLocation("nightmare:textures/menu/journeyProgressIcons.png");

    @Inject(method = "initGui", at = @At("TAIL"))
    private void journeyMode$layout(CallbackInfo ci) {
        this.titleTheme = JourneyTitleTheme.getActive(this.mc);
        this.titleOpenedAt = Minecraft.getSystemTime();
        rebuildJourneyLayout();
    }

    @Unique private void rebuildJourneyLayout() {
        int panelWidth = getPanelWidth();
        int x = 12;
        // In the full-screen browser these controls belong to its header.  Keeping
        // them there avoids covering either the list or the browser action buttons.
        int iconY = isCompactBrowser() ? 12 : this.height - 52;
        boolean compactLayout = iconY < 215 && iconY + 24 > 185;
        int rowWidth = compactLayout ? panelWidth - 120 : panelWidth - 24;
        int iconX = isCompactBrowser() ? this.width - 96 : compactLayout ? panelWidth - 96 : x;
        this.buttonList.clear();
        if (this.mc.isDemo() && !isCompactBrowser()) {
            this.buttonList.add(new GuiJourneyRowButton(11, x, 150, rowWidth, "Play Demo", "Begin your journey"));
            GuiButton resetDemo = new GuiJourneyRowButton(12, x, 185, rowWidth, "Reset Demo", "Start the demo anew");
            resetDemo.enabled = this.mc.getSaveLoader().getWorldInfo("Demo_World") != null;
            this.buttonList.add(resetDemo);
        } else if (!this.mc.isDemo() && !isCompactBrowser()) {
            this.buttonList.add(new GuiJourneyRowButton(1, x, 150, rowWidth, "Singleplayer", "Continue your journey"));
            this.buttonList.add(new GuiJourneyRowButton(2, x, 185, rowWidth, "Multiplayer", "Journey with friends"));
        }

        this.buttonList.add(new GuiJourneyIconButton(0, iconX, iconY, GuiJourneyIconButton.Icon.OPTIONS));
        this.buttonList.add(new GuiJourneyIconButton(5, iconX + 30, iconY, GuiJourneyIconButton.Icon.LANGUAGE));
        this.buttonList.add(new GuiJourneyIconButton(4, iconX + 60, iconY, GuiJourneyIconButton.Icon.QUIT));
        this.refreshRecentWorld();
        this.worldCardTop = 225;
        this.worldCardBottom = iconY - 8;
        if (!isCompactBrowser() && this.recentWorld != null && this.worldCardBottom - this.worldCardTop >= 100) {
            this.buttonList.add(new GuiJourneySmallButton(33, x, this.worldCardBottom - 24, 72, "Jump In"));
        }
        if (this.browserMode != JourneyBrowserMode.NONE && canShowBrowser()) addBrowserButtons();
    }

    /** Retain the existing title-screen anti-xray safeguard without depending on button-list indices. */
    @Inject(method = "updateScreen", at = @At("TAIL"))
    private void journeyMode$disableForXray(CallbackInfo ci) {
        updateBrowserScroll();
        if (AddonHandler.modList.keySet().toString().toLowerCase().contains("xray")) {
            this.splashText = "Probably Shouldn't Xray!";
            for (Object button : this.buttonList) ((GuiButton) button).enabled = false;
        }
    }

    /** Keep world/server selection in this screen whenever the resolution can support the browser. */
    @Inject(method = "actionPerformed", at = @At("HEAD"), cancellable = true)
    private void journeyMode$handleBrowserActions(GuiButton button, CallbackInfo ci) {
        if (button.id == 1 && !this.mc.isDemo() && canShowBrowser()) {
            openBrowser(JourneyBrowserMode.WORLDS);
            ci.cancel();
            return;
        }
        if (button.id == 2 && !this.mc.isDemo() && canShowBrowser()) {
            openBrowser(JourneyBrowserMode.SERVERS);
            ci.cancel();
            return;
        }
        if (button.id >= 100 && button.id <= 206) {
            performBrowserAction(button.id);
            ci.cancel();
        }
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void journeyMode$clickBrowserRow(int mouseX, int mouseY, int mouseButton, CallbackInfo ci) {
        if (this.browserMode == JourneyBrowserMode.NONE || mouseButton != 0 || !canShowBrowser()) return;
        JourneyBrowserBounds bounds = getBrowserBounds();
        if (mouseX >= bounds.right - 13 && mouseX < bounds.right && mouseY >= bounds.listTop && mouseY < bounds.listBottom && browserCanScroll(bounds)) {
            this.browserDraggingScrollbar = true;
            this.browserLastDragY = mouseY;
            this.browserScrollVelocity = 0.0F;
            ci.cancel();
            return;
        }
        if (mouseX < bounds.x || mouseX >= bounds.right || mouseY < bounds.listTop || mouseY >= bounds.listBottom) return;
        int row = (int) ((mouseY - bounds.listTop + this.browserScroll) / BROWSER_ROW_HEIGHT);
        int size = browserSize();
        if (row < 0 || row >= size) {
            ci.cancel();
            return;
        }
        if (this.browserMode == JourneyBrowserMode.WORLDS && mouseX < bounds.x + 24) {
            toggleBrowserFavorite(this.browserWorlds.get(row).getFileName());
            refreshWorldBrowser();
            ci.cancel();
            return;
        }
        boolean doubleClick = this.browserLastClicked == row && Minecraft.getSystemTime() - this.browserLastClick < 250L;
        this.browserSelected = row;
        this.browserLastClicked = row;
        this.browserLastClick = Minecraft.getSystemTime();
        updateBrowserButtonState();
        if (doubleClick) performBrowserAction(this.browserMode == JourneyBrowserMode.WORLDS ? 101 : 201);
        ci.cancel();
    }

    @Override
    public void nightmareMode$handleJourneyBrowserWheel(int mouseX, int mouseY, int wheel) {
        if (this.browserMode == JourneyBrowserMode.NONE || !canShowBrowser()) return;
        JourneyBrowserBounds bounds = getBrowserBounds();
        if (mouseX >= bounds.x && mouseX < bounds.right && mouseY >= bounds.listTop && mouseY < bounds.listBottom) {
            this.browserScrollVelocity += wheel > 0 ? -38.0F : 38.0F;
        }
    }

    @Override
    public void nightmareMode$handleJourneyBrowserDrag(int mouseX, int mouseY, int button) {
        if (!this.browserDraggingScrollbar || button != 0 || this.browserMode == JourneyBrowserMode.NONE) return;
        JourneyBrowserBounds bounds = getBrowserBounds();
        int max = browserMaximumScroll(bounds);
        int visibleHeight = bounds.listBottom - bounds.listTop;
        int thumbHeight = browserThumbHeight(bounds);
        int track = Math.max(1, visibleHeight - thumbHeight);
        float delta = (mouseY - this.browserLastDragY) * max / (float) track;
        this.browserScroll += delta;
        this.browserScrollVelocity = delta;
        this.browserLastDragY = mouseY;
        clampBrowserScroll(bounds);
    }

    @Override
    public void nightmareMode$releaseJourneyBrowserMouse(int mouseX, int mouseY, int button) {
        if (button == 0) this.browserDraggingScrollbar = false;
    }

    @Inject(method = "keyTyped", at = @At("HEAD"), cancellable = true)
    private void journeyMode$closeBrowserWithEscape(char typedChar, int keyCode, CallbackInfo ci) {
        if (this.renamingWorldIndex >= 0) {
            if (keyCode == 1) cancelInlineRename();
            else if (keyCode == 28 || keyCode == 156) commitInlineRename();
            else if (keyCode == 14 && !this.inlineWorldName.isEmpty()) this.inlineWorldName = this.inlineWorldName.substring(0, this.inlineWorldName.length() - 1);
            else if (typedChar >= 32 && typedChar != 127 && ChatAllowedCharacters.allowedCharacters.indexOf(typedChar) >= 0 && this.inlineWorldName.length() < 64) this.inlineWorldName += typedChar;
            ci.cancel();
            return;
        }
        if (keyCode == 1 && this.browserMode != JourneyBrowserMode.NONE) {
            closeBrowser();
            ci.cancel();
        }
    }

    @Inject(method = "confirmClicked", at = @At("TAIL"))
    private void journeyMode$confirmBrowserAction(boolean confirmed, int id, CallbackInfo ci) {
        if (id == CONFIRM_DELETE_WORLD) {
            if (confirmed && this.browserSelected >= 0 && this.browserSelected < this.browserWorlds.size()) {
                try {
                    this.browserFavorites.remove(this.browserWorlds.get(this.browserSelected).getFileName());
                    writeBrowserFavorites();
                    this.mc.getSaveLoader().flushCache();
                    this.mc.getSaveLoader().deleteWorldDirectory(this.browserWorlds.get(this.browserSelected).getFileName());
                } catch (Throwable ignored) { }
            }
            refreshWorldBrowser();
            this.browserSelected = -1;
            this.mc.displayGuiScreen(this);
        } else if (id == CONFIRM_DELETE_SERVER) {
            if (confirmed && this.browserServers != null && this.browserSelected >= 0 && this.browserSelected < this.browserServers.countServers()) {
                this.browserServers.removeServerData(this.browserSelected);
                this.browserServers.saveServerList();
            }
            refreshServerBrowser();
            this.browserSelected = -1;
            this.mc.displayGuiScreen(this);
        } else if (id == 0 && this.pendingServerAction != JourneyServerDialogAction.NONE) {
            completeServerDialog(confirmed);
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

    @Override
    public void nightmareMode$drawJourneyBackdrop(int mouseX, int mouseY, float partialTicks, int width, int height) {
        // Child screens do not call this menu's updateScreen(), so advance its
        // panorama at the normal game-tick cadence while they borrow it.
        long now = Minecraft.getSystemTime();
        if (this.journeyMode$backdropLastTick == 0L) this.journeyMode$backdropLastTick = now;
        while (now - this.journeyMode$backdropLastTick >= 50L) {
            ++this.panoramaTimer;
            this.journeyMode$backdropLastTick += 50L;
        }
        // A child can be resized while its parent is inactive. Render using the
        // child's live dimensions without disturbing the browser's own layout.
        int oldWidth = this.width;
        int oldHeight = this.height;
        this.width = width;
        this.height = height;
        this.renderSkybox(mouseX, mouseY, partialTicks);
        this.width = oldWidth;
        this.height = oldHeight;
    }

    @Inject(method = "drawScreen", at = @At("HEAD"), cancellable = true)
    private void journeyMode$drawScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        ci.cancel();
        this.renderSkybox(mouseX, mouseY, partialTicks);
        JourneyTitleTheme theme = this.titleTheme == null ? JourneyTitleTheme.getActive(this.mc) : this.titleTheme;
        if (isCompactBrowser()) {
            drawTintedPanel(this.width, theme);
            drawEmbeddedBrowser(mouseX, mouseY, theme);
            super.drawScreen(mouseX, mouseY, partialTicks);
            return;
        }
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
        if (!isCompactBrowser() && this.recentWorld != null && this.worldCardBottom - this.worldCardTop >= 100) drawRecentWorldCard(panelWidth);
        this.drawString(this.fontRenderer, "Minecraft 1.6.4 - BTW CE V" + BTWMod.instance.getVersionString(), 12, this.height - 22, theme.textMuted);
        this.drawString(this.fontRenderer, "Copyright Mojang AB. Do not distribute!", 12, this.height - 12, theme.textMuted);
        if (this.browserMode != JourneyBrowserMode.NONE && canShowBrowser()) drawEmbeddedBrowser(mouseX, mouseY, theme);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    // GuiScreen dimensions are scaled: the 856x512 default window commonly arrives here as about 427x240.
    @Unique private boolean canShowBrowser() { return this.width >= 360 && this.height >= 220; }
    @Unique private boolean isCompactBrowser() { return this.browserMode != JourneyBrowserMode.NONE && canShowBrowser() && this.width < 960; }
    @Unique private int getPanelWidth() { return Math.min(this.width - 20, Math.max(320, this.width * 35 / 100)); }
    @Unique private void openBrowser(JourneyBrowserMode mode) {
        this.browserMode = mode;
        this.browserSelected = -1;
        this.browserScroll = 0.0F;
        this.browserScrollVelocity = 0.0F;
        if (mode == JourneyBrowserMode.WORLDS) refreshWorldBrowser(); else refreshServerBrowser();
        rebuildJourneyLayout();
    }

    @Unique private void closeBrowser() {
        cancelInlineRename();
        this.browserMode = JourneyBrowserMode.NONE;
        this.browserSelected = -1;
        this.browserScroll = 0.0F;
        this.browserScrollVelocity = 0.0F;
        rebuildJourneyLayout();
    }

    @Unique private void refreshWorldBrowser() {
        this.browserWorlds.clear();
        loadBrowserFavorites();
        try {
            List saves = this.mc.getSaveLoader().getSaveList();
            if (saves != null) for (Object save : saves) if (save instanceof SaveFormatComparator) this.browserWorlds.add((SaveFormatComparator) save);
            Collections.sort(this.browserWorlds, new Comparator<SaveFormatComparator>() {
                @Override public int compare(SaveFormatComparator a, SaveFormatComparator b) {
                    boolean aFavorite = browserFavorites.contains(a.getFileName());
                    boolean bFavorite = browserFavorites.contains(b.getFileName());
                    if (aFavorite != bFavorite) return aFavorite ? -1 : 1;
                    return a.compareTo(b);
                }
            });
        } catch (Throwable ignored) { }
        this.browserSelected = -1;
    }

    @Unique private void refreshServerBrowser() {
        if (this.browserServers == null) this.browserServers = new ServerList(this.mc);
        else this.browserServers.loadServerList();
        this.browserSelected = -1;
    }

    @Unique private void addBrowserButtons() {
        JourneyBrowserBounds bounds = getBrowserBounds();
        int width = bounds.right - bounds.x;
        if (this.browserMode == JourneyBrowserMode.WORLDS) {
            addBrowserButton(101, bounds.x, this.height - 54, width / 3 - 3, "Play");
            addBrowserButton(102, bounds.x + width / 3 + 2, this.height - 54, width / 3 - 4, "Create New");
            addBrowserButton(103, bounds.x + width * 2 / 3 + 2, this.height - 54, width / 3 - 2, "Rename");
            addBrowserButton(104, bounds.x, this.height - 30, width / 3 - 3, "Delete");
            addBrowserButton(105, bounds.x + width / 3 + 2, this.height - 30, width / 3 - 4, "Recreate");
            addBrowserButton(106, bounds.x + width * 2 / 3 + 2, this.height - 30, width / 3 - 2, "Back");
        } else {
            addBrowserButton(201, bounds.x, this.height - 54, width / 3 - 3, "Join");
            addBrowserButton(202, bounds.x + width / 3 + 2, this.height - 54, width / 3 - 4, "Direct");
            addBrowserButton(203, bounds.x + width * 2 / 3 + 2, this.height - 54, width / 3 - 2, "Add Server");
            addBrowserButton(204, bounds.x, this.height - 30, width / 4 - 3, "Edit");
            addBrowserButton(205, bounds.x + width / 4 + 2, this.height - 30, width / 4 - 3, "Delete");
            addBrowserButton(206, bounds.x + width / 2 + 2, this.height - 30, width / 4 - 3, "Refresh");
            addBrowserButton(106, bounds.x + width * 3 / 4 + 2, this.height - 30, width / 4 - 2, "Back");
        }
        updateBrowserButtonState();
    }

    @Unique private void addBrowserButton(int id, int x, int y, int width, String text) { this.buttonList.add(new GuiJourneySmallButton(id, x, y, Math.max(42, width), text)); }

    @Unique private void updateBrowserButtonState() {
        boolean selected = this.browserSelected >= 0 && this.browserSelected < browserSize();
        for (Object object : this.buttonList) {
            GuiButton button = (GuiButton) object;
            if (button.id == 101 || button.id == 103 || button.id == 104 || button.id == 105 || button.id == 201 || button.id == 204 || button.id == 205) button.enabled = selected;
        }
    }

    @Unique private void performBrowserAction(int id) {
        if (id == 106) { closeBrowser(); return; }
        if (this.browserMode == JourneyBrowserMode.WORLDS) {
            if (id == 102) { this.mc.displayGuiScreen(new GuiCreateWorld(this)); return; }
            if (this.browserSelected < 0 || this.browserSelected >= this.browserWorlds.size()) return;
            SaveFormatComparator save = this.browserWorlds.get(this.browserSelected);
            if (id == 101) launchBrowserWorld(save);
            else if (id == 103) beginInlineRename(save, this.browserSelected);
            else if (id == 104) this.mc.displayGuiScreen(GuiSelectWorld.getDeleteWorldScreen(this, browserWorldName(save, this.browserSelected), CONFIRM_DELETE_WORLD));
            else if (id == 105) recreateBrowserWorld(save);
        } else {
            if (id == 202) openServerDialog(JourneyServerDialogAction.DIRECT, new ServerData(I18n.getString("selectServer.defaultName"), ""), -1);
            else if (id == 203) openServerDialog(JourneyServerDialogAction.ADD, new ServerData(I18n.getString("selectServer.defaultName"), ""), -1);
            else if (id == 206) { refreshServerBrowser(); rebuildJourneyLayout(); }
            else if (this.browserSelected >= 0 && this.browserServers != null && this.browserSelected < this.browserServers.countServers()) {
                ServerData server = this.browserServers.getServerData(this.browserSelected);
                if (id == 201) this.mc.displayGuiScreen(new GuiConnecting(this, this.mc, server));
                else if (id == 204) openServerDialog(JourneyServerDialogAction.EDIT, copyServerData(server), this.browserSelected);
                else if (id == 205) this.mc.displayGuiScreen(new GuiYesNo(this, I18n.getString("selectServer.deleteQuestion"), "'" + server.serverName + "' " + I18n.getString("selectServer.deleteWarning"), I18n.getString("selectServer.deleteButton"), I18n.getString("gui.cancel"), CONFIRM_DELETE_SERVER));
            }
        }
    }

    @Unique private void launchBrowserWorld(SaveFormatComparator save) {
        String folder = save.getFileName();
        String displayName = browserWorldName(save, this.browserSelected);
        if (!this.mc.getSaveLoader().canLoadWorld(folder)) return;
        this.mc.displayGuiScreen(null);
        if (this.mc.getSaveLoader().isWorldGlobal(folder)) this.mc.launchIntegratedServerHostile(folder, displayName, null);
        else this.mc.launchIntegratedServer(folder, displayName, null);
        this.mc.statFileWriter.readStat(StatList.loadWorldStat, 1);
    }

    @Unique private void recreateBrowserWorld(SaveFormatComparator save) {
        try {
            ISaveHandler handler = this.mc.getSaveLoader().getSaveLoader(save.getFileName(), false);
            WorldInfo info = handler.loadWorldInfo();
            handler.flush();
            GuiCreateWorld screen = new GuiCreateWorld(this);
            screen.func_82286_a(info);
            this.mc.displayGuiScreen(screen);
        } catch (Throwable ignored) { }
    }

    @Unique private void openServerDialog(JourneyServerDialogAction action, ServerData data, int index) {
        this.pendingServerAction = action;
        this.pendingServerData = data;
        this.pendingServerIndex = index;
        if (action == JourneyServerDialogAction.DIRECT) this.mc.displayGuiScreen(new GuiScreenServerList(this, data));
        else this.mc.displayGuiScreen(new GuiScreenAddServer(this, data));
    }

    @Unique private ServerData copyServerData(ServerData source) {
        ServerData copy = new ServerData(source.serverName, source.serverIP);
        copy.setHideAddress(source.isHidingAddress());
        return copy;
    }

    @Unique private void completeServerDialog(boolean confirmed) {
        JourneyServerDialogAction action = this.pendingServerAction;
        ServerData data = this.pendingServerData;
        int index = this.pendingServerIndex;
        this.pendingServerAction = JourneyServerDialogAction.NONE;
        this.pendingServerData = null;
        this.pendingServerIndex = -1;
        if (confirmed && data != null) {
            if (action == JourneyServerDialogAction.DIRECT) { this.mc.displayGuiScreen(new GuiConnecting(this, this.mc, data)); return; }
            if (this.browserServers == null) this.browserServers = new ServerList(this.mc);
            if (action == JourneyServerDialogAction.ADD) this.browserServers.addServerData(data);
            else if (action == JourneyServerDialogAction.EDIT && index >= 0 && index < this.browserServers.countServers()) {
                ServerData previous = this.browserServers.getServerData(index);
                previous.serverName = data.serverName;
                previous.serverIP = data.serverIP;
                previous.setHideAddress(data.isHidingAddress());
            }
            this.browserServers.saveServerList();
        }
        refreshServerBrowser();
        this.mc.displayGuiScreen(this);
    }

    @Unique private void drawEmbeddedBrowser(int mouseX, int mouseY, JourneyTitleTheme theme) {
        JourneyBrowserBounds bounds = getBrowserBounds();
        clampBrowserScroll(bounds);
        drawRect(bounds.x, 8, bounds.right, this.height - 8, 0x8A000000 | (theme.cardFill & 0x00FFFFFF));
        drawRect(bounds.x, 8, bounds.right, 9, theme.edge);
        drawRect(bounds.x, 8, bounds.x + 1, this.height - 8, theme.edge);
        drawRect(bounds.right - 1, 8, bounds.right, this.height - 8, theme.edge);
        String title = this.browserMode == JourneyBrowserMode.WORLDS ? "Your Worlds" : "Multiplayer Servers";
        this.drawString(this.fontRenderer, title, bounds.x + 9, 18, theme.textHighlight);
        this.drawString(this.fontRenderer, this.browserMode == JourneyBrowserMode.WORLDS ? "Favorites rise to the top" : "Saved servers", bounds.x + 9, 30, theme.textMuted);
        int size = browserSize();
        beginBrowserListClip(bounds);
        for (int index = 0; index < size; index++) {
            int y = (int) (bounds.listTop + index * BROWSER_ROW_HEIGHT - this.browserScroll);
            // Draw rows that intersect the viewport, including partial rows at either
            // edge.  The scissor box keeps their content beneath the fixed UI chrome.
            if (y + BROWSER_ROW_HEIGHT - 4 <= bounds.listTop || y >= bounds.listBottom) continue;
            boolean selected = index == this.browserSelected;
            int fill = 0xE0000000 | ((selected ? theme.buttonHoverFill : theme.buttonFill) & 0x00FFFFFF);
            drawRect(bounds.x + 5, y, bounds.right - 11, y + BROWSER_ROW_HEIGHT - 4, fill);
            drawRect(bounds.x + 5, y, bounds.right - 11, y + 1, selected ? theme.textHighlight : theme.edge);
            if (this.browserMode == JourneyBrowserMode.WORLDS) drawWorldBrowserRow(index, bounds, y, theme);
            else drawServerBrowserRow(index, bounds, y, theme);
        }
        endBrowserListClip();
        // A narrow, opaque seam gives the fixed header and footer a clear edge while
        // the partially visible rows continue to scroll smoothly behind them.
        drawRect(bounds.x + 1, bounds.listTop - 1, bounds.right - 1, bounds.listTop, theme.edge);
        drawRect(bounds.x + 1, bounds.listBottom, bounds.right - 1, bounds.listBottom + 1, theme.edge);
        if (size == 0) this.drawCenteredString(this.fontRenderer, this.browserMode == JourneyBrowserMode.WORLDS ? "No worlds yet" : "No saved servers", (bounds.x + bounds.right) / 2, bounds.listTop + 18, theme.textMuted);
        drawBrowserScrollbar(bounds, size, theme);
    }

    @Unique private void drawWorldBrowserRow(int index, JourneyBrowserBounds bounds, int y, JourneyTitleTheme theme) {
        SaveFormatComparator save = this.browserWorlds.get(index);
        boolean favorite = this.browserFavorites.contains(save.getFileName());
        drawScaledString(favorite ? "★" : "☆", bounds.x + 11, y + 25, 3.0F, favorite ? theme.textHighlight : theme.textMuted);
        drawTexture(theme.worldIcon, bounds.x + 48, y + 10, 56, 56);
        int textX = bounds.x + 114;
        String name = this.renamingWorldIndex == index ? this.inlineWorldName + "_" : browserWorldName(save, index);
        drawScaledString(trimToWidth(name, (int) ((bounds.right - textX - 16) / 1.2F)), textX, y + 11, 1.2F, theme.text);
        this.drawString(this.fontRenderer, trimToWidth(save.getFileName() + "  " + formatDate(save.getLastTimePlayed()), bounds.right - textX - 16), textX, y + 32, theme.textMuted);
        String mode = save.isHardcoreModeEnabled() ? "Hardcore" : save.getEnumGameType().getName();
        this.drawString(this.fontRenderer, mode, textX, y + 46, save.isHardcoreModeEnabled() ? 0xFFD86C64 : theme.textMuted);
    }

    @Unique private void drawServerBrowserRow(int index, JourneyBrowserBounds bounds, int y, JourneyTitleTheme theme) {
        ServerData server = this.browserServers.getServerData(index);
        int textX = bounds.x + 13;
        drawScaledString(trimToWidth(server.serverName, (int) ((bounds.right - textX - 16) / 1.2F)), textX, y + 11, 1.2F, theme.text);
        String address = server.isHidingAddress() ? I18n.getString("selectServer.hiddenAddress") : server.serverIP;
        this.drawString(this.fontRenderer, trimToWidth(address, bounds.right - textX - 16), textX, y + 34, theme.textMuted);
        this.drawString(this.fontRenderer, server.serverMOTD == null ? "" : trimToWidth(server.serverMOTD, bounds.right - textX - 16), textX, y + 48, theme.textMuted);
    }

    /** Clips moving rows to the list viewport, leaving the header and action area fixed. */
    @Unique private void beginBrowserListClip(JourneyBrowserBounds bounds) {
        ScaledResolution resolution = new ScaledResolution(this.mc.gameSettings, this.mc.displayWidth, this.mc.displayHeight);
        int scale = resolution.getScaleFactor();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor((bounds.x + 5) * scale, this.mc.displayHeight - bounds.listBottom * scale,
                (bounds.right - bounds.x - 16) * scale, (bounds.listBottom - bounds.listTop) * scale);
    }

    @Unique private void endBrowserListClip() {
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    @Unique private void drawBrowserScrollbar(JourneyBrowserBounds bounds, int size, JourneyTitleTheme theme) {
        int contentHeight = size * BROWSER_ROW_HEIGHT;
        int visibleHeight = bounds.listBottom - bounds.listTop;
        if (contentHeight <= visibleHeight) return;
        int thumbHeight = browserThumbHeight(bounds);
        int track = visibleHeight - thumbHeight;
        int max = Math.max(1, contentHeight - visibleHeight);
        int thumbY = bounds.listTop + (int) (this.browserScroll * track / max);
        drawRect(bounds.right - 11, bounds.listTop, bounds.right - 5, bounds.listBottom, 0xB0000000);
        drawRect(bounds.right - 11, thumbY, bounds.right - 5, thumbY + thumbHeight, theme.edge);
    }

    @Unique private JourneyBrowserBounds getBrowserBounds() {
        int x = isCompactBrowser() ? 12 : Math.max(getPanelWidth() + 18, this.width * 49 / 100);
        return new JourneyBrowserBounds(x, this.width - 12, 44, this.height - 62);
    }

    @Unique private int browserSize() { return this.browserMode == JourneyBrowserMode.WORLDS ? this.browserWorlds.size() : this.browserServers == null ? 0 : this.browserServers.countServers(); }
    @Unique private void clampBrowserScroll(JourneyBrowserBounds bounds) {
        int max = browserMaximumScroll(bounds);
        this.browserScroll = Math.max(0.0F, Math.min(this.browserScroll, max));
    }

    @Unique private int browserMaximumScroll(JourneyBrowserBounds bounds) {
        return Math.max(0, browserSize() * BROWSER_ROW_HEIGHT - (bounds.listBottom - bounds.listTop));
    }

    @Unique private boolean browserCanScroll(JourneyBrowserBounds bounds) { return browserMaximumScroll(bounds) > 0; }

    @Unique private int browserThumbHeight(JourneyBrowserBounds bounds) {
        int visibleHeight = bounds.listBottom - bounds.listTop;
        int contentHeight = Math.max(1, browserSize() * BROWSER_ROW_HEIGHT);
        return Math.min(visibleHeight, Math.max(22, visibleHeight * visibleHeight / contentHeight));
    }

    @Unique private void updateBrowserScroll() {
        if (this.browserMode == JourneyBrowserMode.NONE || this.browserDraggingScrollbar || Math.abs(this.browserScrollVelocity) < 0.15F) return;
        this.browserScroll += this.browserScrollVelocity;
        this.browserScrollVelocity *= 0.78F;
        clampBrowserScroll(getBrowserBounds());
    }

    @Unique private void beginInlineRename(SaveFormatComparator save, int index) {
        this.renamingWorldIndex = index;
        this.inlineWorldName = browserWorldName(save, index);
        Keyboard.enableRepeatEvents(true);
    }

    @Unique private void commitInlineRename() {
        if (this.renamingWorldIndex >= 0 && this.renamingWorldIndex < this.browserWorlds.size() && !this.inlineWorldName.trim().isEmpty()) {
            this.mc.getSaveLoader().renameWorld(this.browserWorlds.get(this.renamingWorldIndex).getFileName(), this.inlineWorldName.trim());
        }
        cancelInlineRename();
        refreshWorldBrowser();
        rebuildJourneyLayout();
    }

    @Unique private void cancelInlineRename() {
        this.renamingWorldIndex = -1;
        this.inlineWorldName = "";
        Keyboard.enableRepeatEvents(false);
    }

    @Unique private String browserWorldName(SaveFormatComparator save, int index) {
        String name = save.getDisplayName();
        return name == null || name.trim().isEmpty() ? I18n.getString("selectWorld.world") + " " + (index + 1) : name;
    }

    @Unique private File getBrowserFavoritesFile() { return new File(this.mc.mcDataDir, "nmfavoritedworlds.txt"); }
    @Unique private void loadBrowserFavorites() {
        this.browserFavorites.clear();
        File file = getBrowserFavoritesFile();
        if (!file.exists()) return;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) if (!line.trim().isEmpty()) this.browserFavorites.add(line.trim());
            reader.close();
        } catch (IOException ignored) { }
    }

    @Unique private void toggleBrowserFavorite(String folder) {
        if (!this.browserFavorites.add(folder)) this.browserFavorites.remove(folder);
        writeBrowserFavorites();
    }

    @Unique private void writeBrowserFavorites() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(getBrowserFavoritesFile(), false));
            for (String favorite : this.browserFavorites) { writer.write(favorite); writer.newLine(); }
            writer.close();
        } catch (IOException ignored) { }
    }

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
