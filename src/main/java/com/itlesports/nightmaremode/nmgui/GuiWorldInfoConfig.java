package com.itlesports.nightmaremode.nmgui;

import btw.community.nightmaremode.NightmareMode;
import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;

public class GuiWorldInfoConfig extends GuiScreen {

    private static final String DEFAULT_CODE = "STDHF";

    private final GuiScreen parentGuiScreen;

    private String encodedSettings;

    private final int[] stateIndices = new int[SettingType.values().length];
    private final Map<SettingType, SettingButton> buttons = new HashMap<SettingType, SettingButton>();

    private ResourceLocation survivalIcon;
    private ResourceLocation creativeIcon;

    private ResourceLocation structuresTrueIcon;
    private ResourceLocation structuresFalseIcon;

    private ResourceLocation worldTypeDefaultIcon;
    private ResourceLocation worldTypeFlatIcon;
    private ResourceLocation worldTypeLargeBiomesIcon;

    private ResourceLocation hostileIcon;
    private ResourceLocation standardIcon;

    private ResourceLocation cheatsFalseIcon;
    private ResourceLocation cheatsTrueIcon;

    private enum SettingType {
        GAME_MODE(20, purgeLastNonletter(I18n.getString("selectWorld.gameMode")), new char[] {'S', 'C'}, new String[] {I18n.getString("gameMode.survival"), I18n.getString("gameMode.creative")}),
        STRUCTURES(21, getSecondWordAndPurge(I18n.getString("selectWorld.mapFeatures")), new char[] {'T', 'F'}, new String[] {I18n.getString("options.on"), I18n.getString("options.off")}),
        WORLD_TYPE(22, purgeLastNonletter(I18n.getString("selectWorld.mapType")), new char[] {'D', 'F', 'L'}, new String[] {I18n.getString("generator.default"), I18n.getString("generator.flat"), I18n.getString("generator.largeBiomes")}),
        DIFFICULTY(23, I18n.getString("options.difficulty"), new char[] {'H', 'S'}, new String[] {I18n.getString("difficulty.nightmare.name"), I18n.getString("difficulty.baddream.name")}),
        CHEATS(24, I18n.getString("selectWorld.cheats"), new char[] {'F', 'T'}, new String[] {I18n.getString("options.off"), I18n.getString("options.on")});

        private final int buttonId;
        private final String label;
        private final char[] codes;
        private final String[] displayNames;

        SettingType(int buttonId, String label, char[] codes, String[] displayNames) {
            this.buttonId = buttonId;
            this.label = label;
            this.codes = codes;
            this.displayNames = displayNames;
        }

        public int getButtonId() {
            return this.buttonId;
        }

        public String getLabel() {
            return this.label;
        }

        public int getStateCount() {
            return this.codes.length;
        }

        public char getCode(int index) {
            return this.codes[index];
        }

        public String getDisplayName(int index) {
            return this.displayNames[index];
        }

        public int indexFromCode(char code) {
            for (int i = 0; i < this.codes.length; i++) {
                if (this.codes[i] == code) {
                    return i;
                }
            }
            return 0;
        }
    }
    private static String purgeLastNonletter(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }

        char last = str.charAt(str.length() - 1);

        if (!Character.isLetter(last)) {
            return str.substring(0, str.length() - 1);
        }

        return str;
    }
    private static String getSecondWordAndPurge(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }

        str = str.trim();

        int firstSpace = str.indexOf(' ');
        if (firstSpace == -1 || firstSpace == str.length() - 1) {
            return str; // keep original if no second word
        }

        String second = str.substring(firstSpace + 1).trim();

        return purgeLastNonletter(second);
    }

    private final class SettingButton extends GuiColoredButton {
        private final SettingType type;

        private SettingButton(int id, int x, int y, int width, int height, SettingType type) {
            super(id, x, y, width, height, type.getLabel(), 0xFFFFFF, 0xFFFFFF, getActiveColorFor(type));
            this.type = type;
        }

        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY) {
            if (!this.drawButton) {
                return;
            }

            super.drawButton(mc, mouseX, mouseY);
            int stateIndex = GuiWorldInfoConfig.this.getStateIndex(this.type);
            String stateText = this.type.getDisplayName(stateIndex);


//            int labelColor = 0xFFFFA0;
//
//            GuiWorldInfoConfig.this.drawString(GuiWorldInfoConfig.this.fontRenderer, this.type.getLabel(), this.xPosition + (80 - GuiWorldInfoConfig.this.fontRenderer.getStringWidth(this.type.getLabel())) / 2, this.yPosition + 6, labelColor);

            int valueColor = 0xFFFFFF;
            int stateWidth = GuiWorldInfoConfig.this.fontRenderer.getStringWidth(stateText);
            GuiWorldInfoConfig.this.drawString(GuiWorldInfoConfig.this.fontRenderer, stateText, this.xPosition + this.width - stateWidth - 6 + 100, this.yPosition + 6, valueColor);

            ResourceLocation icon = GuiWorldInfoConfig.this.getIconFor(this.type, stateIndex);
            if (icon != null) {
                int iconSize = 16;
                int iconX = this.xPosition + (this.width / 2) - (iconSize / 2);
                int iconY = this.yPosition + (this.height / 2) - (iconSize / 2);
                GuiWorldInfoConfig.this.drawIcon(icon, iconX, iconY, iconSize, iconSize);
            }

            this.mouseDragged(mc, mouseX, mouseY);
        }

        private static int getActiveColorFor(SettingType type) {
            return switch (type) {
                case GAME_MODE  -> 0x3dfc3d; // green
                case STRUCTURES -> 0x45b2f5; // blue
                case WORLD_TYPE -> 0xffd038; // gold
                case DIFFICULTY -> 0xff2b2b; // red
                case CHEATS     -> 0xa936ff; // purple
            };
        }
    }

    public GuiWorldInfoConfig(GuiScreen parent) {
        this(parent, DEFAULT_CODE);
    }

    public GuiWorldInfoConfig(GuiScreen parent, String initialCode) {
        this.parentGuiScreen = parent;
        this.encodedSettings = sanitizeCode(initialCode);
        this.applyCodeToState(this.encodedSettings);
    }

    public String getEncodedSettings() {
        return this.encodedSettings;
    }

    public void setEncodedSettings(String code) {
        this.encodedSettings = sanitizeCode(code);
        this.applyCodeToState(this.encodedSettings);
    }

    public void setGamemodeIcons(ResourceLocation survival, ResourceLocation creative) {
        this.survivalIcon = survival;
        this.creativeIcon = creative;
    }

    public void setStructuresIcons(ResourceLocation trueIcon, ResourceLocation falseIcon) {
        this.structuresTrueIcon = trueIcon;
        this.structuresFalseIcon = falseIcon;
    }

    public void setWorldTypeIcons(ResourceLocation defaultIcon, ResourceLocation flatIcon, ResourceLocation largeBiomesIcon) {
        this.worldTypeDefaultIcon = defaultIcon;
        this.worldTypeFlatIcon = flatIcon;
        this.worldTypeLargeBiomesIcon = largeBiomesIcon;
    }

    public void setDifficultyIcons(ResourceLocation hostileIcon, ResourceLocation standardIcon) {
        this.hostileIcon = hostileIcon;
        this.standardIcon = standardIcon;
    }

    public void setCheatsIcons(ResourceLocation falseIcon, ResourceLocation trueIcon) {
        this.cheatsFalseIcon = falseIcon;
        this.cheatsTrueIcon = trueIcon;
    }

    @Override
    public void initGui() {
        this.buttonList.clear();
        this.buttons.clear();

        int buttonWidth = 80;
        int buttonHeight = 20;
        int startX = this.width / 2 - buttonWidth / 2 - 100;
        int startY = this.height / 2 - 62;
        int gap = 28;

        for (SettingType type : SettingType.values()) {
            int y = startY + (type.getButtonId() - 20) * gap;
            SettingButton button = new SettingButton(type.getButtonId(), startX, y, buttonWidth, buttonHeight, type);
            this.buttonList.add(button);
            this.buttons.put(type, button);
        }

        this.buttonList.add(new GuiButton(1, startX, this.height * 7 / 8, 80, 20, I18n.getString("gui.cancel")));
        this.buttonList.add(new GuiButton(2, startX + 100, this.height * 7 / 8, 140, 20, I18n.getString("config.emi.presets.defaults")));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);

        this.drawCenteredString(this.fontRenderer, "World Info Config", this.width / 2, 18, 0xFFFFFF);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 1) {
            this.mc.displayGuiScreen(this.parentGuiScreen);
            return;
        } else if (button.id == 2) {
            this.setEncodedSettings(DEFAULT_CODE);
            NightmareMode.getInstance().modifyConfigProperty("WorldInfoString", DEFAULT_CODE, NightmareMode.getInstance().addonConfig, false);
            return;
        }

        SettingType type = this.getTypeById(button.id);
        if (type != null) {
            this.cycleState(type);
            NightmareMode.getInstance().modifyConfigProperty("WorldInfoString", this.getEncodedSettings(), NightmareMode.getInstance().addonConfig, false);
        }
    }

    @Override
    protected void keyTyped(char character, int keyCode) {
        if (keyCode == 1) {
            this.mc.displayGuiScreen(this.parentGuiScreen);
            return;
        }

        super.keyTyped(character, keyCode);
    }

    private void cycleState(SettingType type) {
        int index = this.getStateIndex(type);
        index++;
        if (index >= type.getStateCount()) {
            index = 0;
        }

        this.stateIndices[type.ordinal()] = index;
        this.encodedSettings = this.buildCodeFromState();

        this.applyCodeToState(this.encodedSettings);
    }

    private void applyCodeToState(String code) {
        if (code == null || code.length() < SettingType.values().length) {
            code = DEFAULT_CODE;
        }

        for (SettingType type : SettingType.values()) {
            char c = code.charAt(type.getButtonId() - 20);
            this.stateIndices[type.ordinal()] = type.indexFromCode(c);
        }
    }

    private String buildCodeFromState() {
        StringBuilder builder = new StringBuilder();

        for (SettingType type : SettingType.values()) {
            builder.append(type.getCode(this.stateIndices[type.ordinal()]));
        }

        return builder.toString();
    }

    private String sanitizeCode(String code) {
        if (code == null || code.length() < SettingType.values().length) {
            return DEFAULT_CODE;
        }

        StringBuilder builder = new StringBuilder();

        for (SettingType type : SettingType.values()) {
            char c = code.charAt(type.getButtonId() - 20);
            int index = type.indexFromCode(c);
            builder.append(type.getCode(index));
        }

        return builder.toString();
    }

    private int getStateIndex(SettingType type) {
        return this.stateIndices[type.ordinal()];
    }

    private String getCurrentDisplayName(SettingType type) {
        return type.getDisplayName(this.getStateIndex(type));
    }

    private SettingType getTypeById(int id) {
        for (SettingType type : SettingType.values()) {
            if (type.getButtonId() == id) {
                return type;
            }
        }
        return null;
    }

    private ResourceLocation getIconFor(SettingType type, int stateIndex) {
        return switch (type) {
            case GAME_MODE -> stateIndex == 0 ? this.survivalIcon : this.creativeIcon;
            case STRUCTURES -> stateIndex == 0 ? this.structuresTrueIcon : this.structuresFalseIcon;
            case WORLD_TYPE -> {
                if (stateIndex == 0) {
                    yield this.worldTypeDefaultIcon;
                } else if (stateIndex == 1) {
                    yield this.worldTypeFlatIcon;
                }
                yield this.worldTypeLargeBiomesIcon;
            }
            case DIFFICULTY -> stateIndex == 0 ? this.hostileIcon : this.standardIcon;
            case CHEATS -> stateIndex == 0 ? this.cheatsFalseIcon : this.cheatsTrueIcon;
        };

    }

    private void drawIcon(ResourceLocation texture, int x, int y, int width, int height) {
        if (texture == null) {
            return;
        }

        this.mc.renderEngine.bindTexture(texture);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(1F, 1F, 1F, 1F);

        Tessellator t = Tessellator.instance;
        t.startDrawingQuads();
        t.addVertexWithUV(x, y + height, 0, 0, 1);
        t.addVertexWithUV(x + width, y + height, 0, 1, 1);
        t.addVertexWithUV(x + width, y, 0, 1, 0);
        t.addVertexWithUV(x, y, 0, 0, 0);
        t.draw();

        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_BLEND);
    }
}