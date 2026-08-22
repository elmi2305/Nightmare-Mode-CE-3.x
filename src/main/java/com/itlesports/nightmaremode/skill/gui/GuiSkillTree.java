package com.itlesports.nightmaremode.skill.gui;

import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.skill.SkillBranch;
import com.itlesports.nightmaremode.skill.SkillHandler;
import com.itlesports.nightmaremode.skill.SkillNet;
import com.itlesports.nightmaremode.skill.SkillNode;
import com.itlesports.nightmaremode.skill.SkillRegistry;
import com.itlesports.nightmaremode.util.NMFields;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.MathHelper;
import net.minecraft.src.RenderHelper;
import net.minecraft.src.RenderItem;
import net.minecraft.src.ResourceLocation;
import net.minecraft.src.ScaledResolution;
import net.minecraft.src.Tessellator;
import emi.shims.java.com.unascribed.retroemi.RetroEMI;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.Locale;

public class GuiSkillTree extends GuiScreen {
    private static final int GRID = 30;
    private static final int PANE_WIDTH = 320;
    private static final int PANE_HEIGHT = 220;
    private static final int VIEW_WIDTH = 292;
    private static final int VIEW_HEIGHT = 176;
    private static final int NODE_UNLOCKED_COLOR = 0xFF5AB96F;
    private static final int NODE_READY_COLOR = 0xFFE1C16E;
    private static final int NODE_AVAILABLE_COLOR = 0xFF6F91AC;
    private static final int NODE_PARENT_LOCKED_COLOR = 0xFF292929;
    private static final int NODE_FOCUSED_COLOR = 0xFFFF4FD8;
    private static final int NODE_SEARCH_MATCH_COLOR = 0xFF70D6FF;
    private static final ResourceLocation BORDER_TEXTURE = new ResourceLocation(NMFields.modID, "textures/gui/skill/border.png");
    private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(NMFields.modID, "textures/gui/skill/background.png");
    private static final ResourceLocation TAB_OUTLINE_TEXTURE = new ResourceLocation(NMFields.modID, "textures/gui/skill/tab_outline.png");
    private int branchIndex;
    private double mapX = -80.0D;
    private double mapY = -48.0D;
    private int lastMouseX;
    private int lastMouseY;
    private boolean dragging;
    private boolean movedWhileDragging;
    private SkillBranch hoveredBranch;
    private SkillNode hoveredNode;
    private SkillNode focusedNode;
    private final boolean[] keysHeldWhenOpened = new boolean[Keyboard.KEYBOARD_SIZE];
    private String searchText = "";
    private int searchResultIndex;

    @Override
    public void initGui() {
        for (int keyCode = 0; keyCode < Keyboard.KEYBOARD_SIZE; keyCode++) {
            this.keysHeldWhenOpened[keyCode] = Keyboard.isKeyDown(keyCode);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.hoveredBranch = null;
        this.hoveredNode = null;
        this.drawDefaultBackground();
        this.handleDragging(mouseX, mouseY);
        int left = (this.width - PANE_WIDTH) / 2;
        int top = (this.height - PANE_HEIGHT) / 2;
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture(BACKGROUND_TEXTURE);
//        this.drawFullTexturedRect(left - 4, top + 28 - 5, PANE_WIDTH - 16 + 16, PANE_HEIGHT - 38 + 10);
        this.drawFullTexturedRect(left , top + 28 - 5, PANE_WIDTH - 16 + 4, PANE_HEIGHT - 38 + 10);
        this.mc.renderEngine.bindTexture(BORDER_TEXTURE);
        this.drawFullTexturedRect(left - 4, top + 18, PANE_WIDTH + 8, PANE_HEIGHT - 14);
        this.drawSearchBar(left, top);
        this.drawTabs(left, top, mouseX, mouseY);
        this.drawMap(left + 14, top + 34, mouseX, mouseY, partialTicks);
        GL11.glDisable(2929);
        GL11.glDisable(2896);
        if (this.hoveredNode != null) {
            NodeVisualState state = this.getNodeVisualState(this.hoveredNode);

            this.drawNodeTooltip(this.hoveredNode,state, mouseX, mouseY);
        } else if (this.hoveredBranch != null) {
            this.drawTooltip(this.hoveredBranch.getName(), mouseX, mouseY);
        }
        GL11.glEnable(2929);
    }

    private void handleDragging(int mouseX, int mouseY) {
        if (!Mouse.isButtonDown(0)) {
            this.dragging = false;
            return;
        }
        if (!this.dragging) {
            this.dragging = true;
            this.movedWhileDragging = false;
            this.lastMouseX = mouseX;
            this.lastMouseY = mouseY;
            return;
        }
        int dx = mouseX - this.lastMouseX;
        int dy = mouseY - this.lastMouseY;
        if (Math.abs(dx) + Math.abs(dy) > 1) {
            this.mapX -= dx;
            this.mapY -= dy;
            this.movedWhileDragging = true;
        }
        this.lastMouseX = mouseX;
        this.lastMouseY = mouseY;
    }

    private void drawTabs(int left, int top, int mouseX, int mouseY) {
        RenderItem renderItem = new RenderItem();
        for (SkillBranch branch : SkillRegistry.getBranches()) {
            int x = left + 12 + branch.getIndex() * 30;
            int y = top - 18 + 11;
            boolean selected = branch.getIndex() == this.branchIndex;
            GL11.glColor4f(selected ? 1.0F : 0.65F, selected ? 1.0F : 0.65F, selected ? 1.0F : 0.65F, 1.0F);
            this.mc.renderEngine.bindTexture(TAB_OUTLINE_TEXTURE);
            this.drawFullTexturedRect(x, y, 26, 26);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            RenderHelper.enableGUIStandardItemLighting();
            renderItem.renderItemAndEffectIntoGUI(this.fontRenderer, this.mc.renderEngine, branch.getIcon(), x + 5, y + 5);
            RenderHelper.disableStandardItemLighting();
            if (mouseX >= x && mouseX <= x + 26 && mouseY >= y && mouseY <= y + 26) {
                this.hoveredBranch = branch;
            }
        }
    }

    private void drawMap(int left, int top, int mouseX, int mouseY, float partialTicks) {
        SkillBranch branch = this.getBranch();
        if (branch == null) {
            return;
        }
        GL11.glEnable(3089);
        this.applyScissor(left, top, VIEW_WIDTH, VIEW_HEIGHT);
        int windowX = MathHelper.floor_double(this.mapX);
        int windowY = MathHelper.floor_double(this.mapY);
//        this.drawGrid(left, top, windowX, windowY);
        GL11.glDisable(3553);
        for (SkillNode node : branch.getNodes()) {
            for (SkillNode parent : node.parents) {
                if (parent != null && parent.branch == branch) {
                    int x1 = left + node.displayColumn * GRID - windowX + 11;
                    int y1 = top + node.displayRow * GRID - windowY + 11;
                    int x2 = left + parent.displayColumn * GRID - windowX + 11;
                    int y2 = top + parent.displayRow * GRID - windowY + 11;
                    boolean unlocked = SkillHandler.isUnlocked(this.mc.thePlayer, node);
                    boolean parentLocked = !unlocked && !SkillHandler.hasUnlockedAllParents(this.mc.thePlayer, node);
                    int color = unlocked ? 0xFF70C174 : parentLocked ? 0xFF242424 : 0xFF4A6478;
                    drawRect(Math.min(x1, x2), y1, Math.max(x1, x2) + 1, y1 + 1, color);
                    drawRect(x2, Math.min(y1, y2), x2 + 1, Math.max(y1, y2) + 1, color);
                }
            }
        }
        GL11.glEnable(3553);
        SkillNode hovered = this.drawNodes(branch, left, top, windowX, windowY, mouseX, mouseY);
        GL11.glDisable(3089);
        if (hovered != null) {
            this.hoveredNode = hovered;
        }
    }

    private void drawGrid(int left, int top, int windowX, int windowY) {
        for (int x = -windowX % 16; x < VIEW_WIDTH; x += 16) {
            drawRect(left + x, top, left + x + 1, top + VIEW_HEIGHT, 0x22101010);
        }
        for (int y = -windowY % 16; y < VIEW_HEIGHT; y += 16) {
            drawRect(left, top + y, left + VIEW_WIDTH, top + y + 1, 0x22101010);
        }
    }

    private SkillNode drawNodes(SkillBranch branch, int left, int top, int windowX, int windowY, int mouseX, int mouseY) {
        RenderItem renderItem = new RenderItem();
        SkillNode hovered = null;
        for (SkillNode node : branch.getNodes()) {
            int x = left + node.displayColumn * GRID - windowX;
            int y = top + node.displayRow * GRID - windowY;
            if (x < left - 24 || y < top - 24 || x > left + VIEW_WIDTH || y > top + VIEW_HEIGHT) {
                continue;
            }
            NodeVisualState state = this.getNodeVisualState(node);
            int color = switch (state) {
                case UNLOCKED -> NODE_UNLOCKED_COLOR;
                case READY -> NODE_READY_COLOR;
                case AVAILABLE -> NODE_AVAILABLE_COLOR;
                case PARENT_LOCKED -> NODE_PARENT_LOCKED_COLOR;
            };
            if (this.isSearchMatch(node)) {
                drawRect(x - 4, y - 4, x + 26, y + 26, NODE_SEARCH_MATCH_COLOR);
            }
            if (node == this.focusedNode) {
                drawRect(x - 6, y - 6, x + 28, y + 28, NODE_FOCUSED_COLOR);
            }
            drawRect(x - 2, y - 2, x + 24, y + 24, color);
            drawRect(x, y, x + 22, y + 22, state == NodeVisualState.PARENT_LOCKED ? 0xFF080808 : 0xFF111111);
            RenderHelper.enableGUIStandardItemLighting();
            renderItem.renderItemAndEffectIntoGUI(this.fontRenderer, this.mc.renderEngine, node.icon, x + 3, y + 3);
            RenderHelper.disableStandardItemLighting();
            if (state == NodeVisualState.PARENT_LOCKED) {
                // Item rendering writes to the depth buffer, which can otherwise hide this 2D overlay.
                GL11.glDisable(GL11.GL_DEPTH_TEST);
                drawRect(x, y, x + 22, y + 22, 0xA8000000);
                this.drawParentLockBadge(x, y);
                GL11.glEnable(GL11.GL_DEPTH_TEST);
            }
            if (mouseX >= x && mouseX <= x + 22 && mouseY >= y && mouseY <= y + 22
                    && mouseX >= left && mouseX <= left + VIEW_WIDTH && mouseY >= top && mouseY <= top + VIEW_HEIGHT) {
                hovered = node;
            }
        }
        return hovered;
    }

    private void drawNodeTooltip(SkillNode node, NodeVisualState state, int mouseX, int mouseY) {
        String status = switch (state) {
            case UNLOCKED -> "Unlocked";
            case READY -> "Click to unlock";
            case AVAILABLE -> null;
            case PARENT_LOCKED -> "Requires [" + this.getMissingParentNames(node) + "]";
        };
        String reward = (state == NodeVisualState.UNLOCKED || NightmareMode.alwaysShowRewards) ? node.reward.getText() : "?";
        String body = (status == null ? "" : (status + "\n")) + (state != NodeVisualState.PARENT_LOCKED ? (node.requirementText + "\n" + "Reward: " + reward) : "");
        int width = Math.max(140, Math.max(this.fontRenderer.getStringWidth(node.name), this.fontRenderer.splitStringWidth(body, 180)));
        int height = 24 + this.fontRenderer.splitStringWidth(body, width);
        int x = mouseX + 12;
        int y = mouseY - 4;
        this.drawGradientRect(x - 3, y - 3, x + width + 3, y + height + 3, 0xE0000000, 0xE0000000);
        int titleColor = switch (state) {
            case UNLOCKED -> 0x70FF83;
            case READY -> 0xFFE07A;
            case AVAILABLE -> 0x9FC5E8;
            case PARENT_LOCKED -> 0x777777;
        };
        this.fontRenderer.drawStringWithShadow(node.name, x, y, titleColor);
        this.fontRenderer.drawSplitString(body, x, y + 14, width, 0xD0D0D0);
    }

    private NodeVisualState getNodeVisualState(SkillNode node) {
        if (SkillHandler.isUnlocked(this.mc.thePlayer, node)) {
            return NodeVisualState.UNLOCKED;
        }
        if (!SkillHandler.hasUnlockedAllParents(this.mc.thePlayer, node)) {
            return NodeVisualState.PARENT_LOCKED;
        }
        return SkillHandler.isEligible(this.mc.thePlayer, node) ? NodeVisualState.READY : NodeVisualState.AVAILABLE;
    }

    private String getMissingParentNames(SkillNode node) {
        StringBuilder missing = new StringBuilder();
        for (SkillNode parent : node.parents) {
            if (parent == null || SkillHandler.isUnlocked(this.mc.thePlayer, parent)) {
                continue;
            }
            if (missing.length() > 0) {
                missing.append(", ");
            }
            missing.append(parent.name);
        }
        return missing.length() == 0 ? "another skill" : missing.toString();
    }

    private void drawParentLockBadge(int x, int y) {
        int color = 0xFF777777;
        drawRect(x + 16, y + 7, x + 20, y + 8, color);
        drawRect(x + 15, y + 8, x + 16, y + 11, color);
        drawRect(x + 20, y + 8, x + 21, y + 11, color);
        drawRect(x + 15, y + 10, x + 21, y + 16, color);
        drawRect(x + 17, y + 12, x + 19, y + 15, 0xFF202020);
    }

    private void drawTooltip(String text, int mouseX, int mouseY) {
        int x = mouseX + 12;
        int y = mouseY - 4;
        int width = this.fontRenderer.getStringWidth(text);
        this.drawGradientRect(x - 3, y - 3, x + width + 3, y + 12, 0xE0000000, 0xE0000000);
        this.fontRenderer.drawStringWithShadow(text, x, y, 0xFFFFFF);
    }

    private void drawSearchBar(int left, int top) {
        int x = left + 166;
        int y = top + 3;
        int width = 138;
        drawRect(x, y, x + width, y + 15, 0xFF111111);
        drawRect(x + 1, y + 1, x + width - 1, y + 14, 0xFF303030);
        String text = "Search: " + this.searchText;
        this.fontRenderer.drawStringWithShadow(this.fontRenderer.trimStringToWidth(text, width - 6), x + 3, y + 4, 0xFFFFFF);
    }

    private boolean isSearchMatch(SkillNode node) {
        if (this.searchText.isEmpty()) {
            return false;
        }
        String query = this.searchText.toLowerCase(Locale.ROOT);
        return node.name.toLowerCase(Locale.ROOT).contains(query)
                || node.requirementText.toLowerCase(Locale.ROOT).contains(query);
    }

    private void focusNextSearchResult() {
        int matchCount = 0;
        for (SkillBranch branch : SkillRegistry.getBranches()) {
            for (SkillNode node : branch.getNodes()) {
                if (!this.isSearchMatch(node)) {
                    continue;
                }
                if (matchCount == this.searchResultIndex) {
                    this.focusNode(node);
                    this.searchResultIndex = (this.searchResultIndex + 1) % this.getSearchResultCount();
                    return;
                }
                matchCount++;
            }
        }
    }

    private int getSearchResultCount() {
        int count = 0;
        for (SkillBranch branch : SkillRegistry.getBranches()) {
            for (SkillNode node : branch.getNodes()) {
                if (this.isSearchMatch(node)) {
                    count++;
                }
            }
        }
        return count;
    }

    @Override
    public void handleKeyboardInput() {
        int keyCode = Keyboard.getEventKey();
        char character = Keyboard.getEventCharacter();
        boolean keyPressed = Keyboard.getEventKeyState();

        if (!keyPressed) {
            if (keyCode >= 0 && keyCode < this.keysHeldWhenOpened.length) {
                this.keysHeldWhenOpened[keyCode] = false;
            }
            return;
        }
        if (keyCode >= 0 && keyCode < this.keysHeldWhenOpened.length && this.keysHeldWhenOpened[keyCode]) {
            return;
        }
        if (keyCode == Keyboard.KEY_F11) {
            this.mc.toggleFullscreen();
            return;
        }
        RetroEMI.handleKeyboardInput();
        this.keyTyped(character, keyCode);
    }

    @Override
    protected void keyTyped(char character, int keyCode) {
        if (keyCode == Keyboard.KEY_BACK) {
            if (!this.searchText.isEmpty()) {
                this.searchText = this.searchText.substring(0, this.searchText.length() - 1);
                this.searchResultIndex = 0;
            }
            return;
        }
        if (keyCode == Keyboard.KEY_RETURN || keyCode == Keyboard.KEY_NUMPADENTER) {
            if (!this.searchText.isEmpty()) {
                this.focusNextSearchResult();
            }
            return;
        }
        if (character >= ' ' && character <= '~') {
            this.searchText += character;
            this.searchResultIndex = 0;
            return;
        }
        super.keyTyped(character, keyCode);
    }

    @Override
    public void onGuiClosed() {
        this.searchText = "";
        this.searchResultIndex = 0;
    }

    @Override
    protected void mouseMovedOrUp(int mouseX, int mouseY, int state) {
        if (state == 0) {
            int left = (this.width - PANE_WIDTH) / 2;
            int top = (this.height - PANE_HEIGHT) / 2;
            for (SkillBranch branch : SkillRegistry.getBranches()) {
                int x = left + 12 + branch.getIndex() * 30;
                int y = top - 18 + 11;
                if (mouseX >= x && mouseX <= x + 26 && mouseY >= y && mouseY <= y + 26) {
                    this.branchIndex = branch.getIndex();
                    this.mapX = -80.0D;
                    this.mapY = -48.0D;
                    return;
                }
            }
            if (!this.movedWhileDragging) {
                SkillNode clicked = this.getNodeAt(mouseX, mouseY);
                if (clicked != null) {
                    System.out.println("x:"+ clicked.displayColumn + " y:"+ clicked.displayRow);
                }
                if (clicked != null && this.getNodeVisualState(clicked) == NodeVisualState.PARENT_LOCKED) {
                    this.focusFirstLockedParent(clicked);
                } else if (clicked != null && !SkillHandler.isUnlocked(this.mc.thePlayer, clicked)) {
                    SkillNet.sendUnlockRequest(clicked.id.toString());
                }
            }
        }
        super.mouseMovedOrUp(mouseX, mouseY, state);
    }

    private SkillNode getNodeAt(int mouseX, int mouseY) {
        SkillBranch branch = this.getBranch();
        if (branch == null) {
            return null;
        }
        int left = (this.width - PANE_WIDTH) / 2 + 14;
        int top = (this.height - PANE_HEIGHT) / 2 + 34;
        int windowX = MathHelper.floor_double(this.mapX);
        int windowY = MathHelper.floor_double(this.mapY);
        for (SkillNode node : branch.getNodes()) {
            int x = left + node.displayColumn * GRID - windowX;
            int y = top + node.displayRow * GRID - windowY;
            if (mouseX >= x && mouseX <= x + 22 && mouseY >= y && mouseY <= y + 22) {
                return node;
            }
        }
        return null;
    }

    private void focusFirstLockedParent(SkillNode node) {
        for (SkillNode parent : node.parents) {
            if (parent != null && !SkillHandler.isUnlocked(this.mc.thePlayer, parent)) {
                this.focusNode(parent);
                return;
            }
        }
    }

    private void focusNode(SkillNode node) {
        this.focusedNode = node;
        if (node.branch != null) {
            this.branchIndex = node.branch.getIndex();
        }
        this.mapX = node.displayColumn * GRID - (VIEW_WIDTH - 22) / 2.0D;
        this.mapY = node.displayRow * GRID - (VIEW_HEIGHT - 22) / 2.0D;
    }

    private SkillBranch getBranch() {
        if (SkillRegistry.getBranches().isEmpty()) {
            return null;
        }
        return SkillRegistry.getBranches().get(Math.max(0, Math.min(this.branchIndex, SkillRegistry.getBranches().size() - 1)));
    }

    private void applyScissor(int x, int y, int width, int height) {
        ScaledResolution sr = new ScaledResolution(this.mc.gameSettings, this.mc.displayWidth, this.mc.displayHeight);
        int scale = sr.getScaleFactor();
        GL11.glScissor(x * scale, this.mc.displayHeight - (y + height) * scale, width * scale, height * scale);
    }

    private void drawFullTexturedRect(int x, int y, int width, int height) {
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(x, y + height, this.zLevel, 0.0D, 1.0D);
        tessellator.addVertexWithUV(x + width, y + height, this.zLevel, 1.0D, 1.0D);
        tessellator.addVertexWithUV(x + width, y, this.zLevel, 1.0D, 0.0D);
        tessellator.addVertexWithUV(x, y, this.zLevel, 0.0D, 0.0D);
        tessellator.draw();
    }

    private enum NodeVisualState {
        UNLOCKED,
        READY,
        AVAILABLE,
        PARENT_LOCKED
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
