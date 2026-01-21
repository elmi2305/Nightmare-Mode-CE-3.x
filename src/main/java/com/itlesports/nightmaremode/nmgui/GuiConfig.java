package com.itlesports.nightmaremode.nmgui;

import api.config.AddonConfig;
import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.NMConfUtils;
import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.I18n;
import net.minecraft.src.Tessellator;
import org.lwjgl.Sys;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.*;
import java.util.List;

public class GuiConfig extends GuiScreen {

    private static final int EASY_BASE = 0x8a8a00;
    private static final int EASY_ACTIVE = 0xFFFF00;

    private static final int MED_BASE = 0x9c5300;
    private static final int MED_ACTIVE = 0xff8800;

    private static final int HARD_BASE = 0x870101;
    private static final int HARD_ACTIVE = 0xFF0000;

    private static final int IMPOSSIBLE_BASE = 0x484848;
    private static final int IMPOSSIBLE_ACTIVE = 0xAAAAAA;

    private static final int AMBIENT_BASE = 0x264f91;
    private static final int AMBIENT_ACTIVE = 0x428BFF;

    private static final int HELPFUL_BASE = 0x429c32;
    private static final int HELPFUL_ACTIVE = 0x5fe647;


    private boolean isOnSecondPage = false;

    private enum Page { ONE, TWO }

    private enum Column { LEFT, RIGHT }




    public enum ConfigOption {
        MORE_VARIANTS(1, "moreVariants", "MoreVariants", "gui.config.more_variants", "gui.config.tooltip.more_variants", EASY_BASE, EASY_ACTIVE, Page.ONE, Column.LEFT, NMConfUtils.CONFIG.MORE_VARIANTS),
        EVOLVED_MOBS(2, "evolvedMobs", "EvolvedMobs", "gui.config.evolved_mobs", "gui.config.tooltip.evolved_mobs", EASY_BASE, EASY_ACTIVE, Page.ONE, Column.LEFT, NMConfUtils.CONFIG.EVOLVED_MOBS),
        BUFFED_SQUIDS(3, "buffedSquids", "BuffedSquids", "gui.config.buffed_squids", "gui.config.tooltip.buffed_squids", EASY_BASE, EASY_ACTIVE, Page.ONE, Column.LEFT, NMConfUtils.CONFIG.BUFFED_SQUIDS),
        NITE(4, "nite", "NITE", "gui.config.nite", "gui.config.tooltip.nite", MED_BASE, MED_ACTIVE, Page.ONE, Column.LEFT, NMConfUtils.CONFIG.NITE),
        DARK_STORMY_NIGHTMARE(5, "darkStormyNightmare", "DarkStormyNightmare", "gui.config.dark_stormy_night", "gui.config.tooltip.dark_stormy_night", MED_BASE, MED_ACTIVE, Page.ONE, Column.LEFT, NMConfUtils.CONFIG.DARK_STORMY_NIGHTMARE),
        NO_SKYBASES(6, "noSkybases", "NoSkybases", "gui.config.no_skybases", "gui.config.tooltip.no_skybases", MED_BASE, MED_ACTIVE, Page.ONE, Column.LEFT, NMConfUtils.CONFIG.NO_SKYBASES),
        CANCER_MODE(7, "isAprilFools", "AprilFoolsPatch", "gui.config.cancer_mode", "gui.config.tooltip.cancer_mode", MED_BASE, MED_ACTIVE, Page.ONE, Column.LEFT, NMConfUtils.CONFIG.CANCER_MODE),
        TOTAL_ECLIPSE(8, "totalEclipse", "TotalEclipse", "gui.config.total_eclipse", "gui.config.tooltip.total_eclipse", HARD_BASE, HARD_ACTIVE, Page.ONE, Column.RIGHT, NMConfUtils.CONFIG.TOTAL_ECLIPSE),
        BLOODMARE(9, "bloodmare", "Bloodmare", "gui.config.bloodmare", "gui.config.tooltip.bloodmare", HARD_BASE, HARD_ACTIVE, Page.ONE, Column.RIGHT, NMConfUtils.CONFIG.BLOODMARE),
        MAGIC_MONSTERS(10, "magicMonsters", "MagicMonsters", "gui.config.magic_monsters", "gui.config.tooltip.magic_monsters", HARD_BASE, HARD_ACTIVE, Page.ONE, Column.RIGHT, NMConfUtils.CONFIG.MAGIC_MONSTERS),
        UNKILLABLE_MOBS(11, "unkillableMobs", "UnkillableMobs", "gui.config.unkillable_mobs", "gui.config.tooltip.unkillable_mobs", IMPOSSIBLE_BASE, IMPOSSIBLE_ACTIVE, Page.ONE, Column.RIGHT, NMConfUtils.CONFIG.UNKILLABLE_MOBS),
        NO_HIT(12, "noHit", "NoHit", "gui.config.no_hit", "gui.config.tooltip.no_hit", IMPOSSIBLE_BASE, IMPOSSIBLE_ACTIVE, Page.ONE, Column.RIGHT, NMConfUtils.CONFIG.NO_HIT),
        REAL_TIME(13, "realTime", "RealTime", "gui.config.real_time", "gui.config.tooltip.real_time", HARD_BASE, HARD_ACTIVE, Page.ONE, Column.RIGHT, NMConfUtils.CONFIG.REAL_TIME),


        SHOULD_SHOW_DATE_TIMER(15, "shouldShowDateTimer", "NmMinecraftDayTimer", "gui.config.date_timer", "gui.config.tooltip.date_timer", AMBIENT_BASE, AMBIENT_ACTIVE, Page.TWO, Column.LEFT, null),
        SHOULD_SHOW_REAL_TIMER(16, "shouldShowRealTimer", "NmTimer", "gui.config.real_timer", "gui.config.tooltip.real_timer", AMBIENT_BASE, AMBIENT_ACTIVE, Page.TWO, Column.LEFT, null),
        BLOODMOON_COLORS(17, "bloodmoonColors", "BloodmoonColors", "gui.config.bloodmoon_colors", "gui.config.tooltip.bloodmoon_colors", AMBIENT_BASE, AMBIENT_ACTIVE, Page.TWO, Column.LEFT, null),
        CRIMSON(18, "crimson", "Crimson", "gui.config.crimson", "gui.config.tooltip.crimson", AMBIENT_BASE, AMBIENT_ACTIVE, Page.TWO, Column.LEFT, null),
        CONFIG_ON_HUD(19, "configOnHud", "ConfigOnHUD", "gui.config.config_on_hud", "gui.config.tooltip.config_on_hud", AMBIENT_BASE, AMBIENT_ACTIVE, Page.TWO, Column.LEFT, null),
        POTION_PARTICLES(20, "potionParticles", "PotionParticles", "gui.config.potion_particles", "gui.config.tooltip.potion_particles", AMBIENT_BASE, AMBIENT_ACTIVE, Page.TWO, Column.LEFT, null),
        SHOULD_DISPLAY_FISHING_ANNOUNCEMENTS(21, "shouldDisplayFishingAnnouncements", "FishingAnnouncements", "gui.config.fishing_alerts", "gui.config.tooltip.fishing_alerts", AMBIENT_BASE, AMBIENT_ACTIVE, Page.TWO, Column.LEFT, null),
        APRIL_FOOLS_RENDERING(22, "aprilFoolsRendering", "AprilFoolsWarpedRendering", "gui.config.cm_rendering", "gui.config.tooltip.cm_rendering", AMBIENT_BASE, AMBIENT_ACTIVE, Page.TWO, Column.RIGHT, null),
        PERFECT_START(23, "perfectStart", "PerfectStart", "gui.config.perfect_start", "gui.config.tooltip.perfect_start", HELPFUL_BASE, HELPFUL_ACTIVE, Page.TWO, Column.RIGHT, null),
        EXTRA_ARMOR(24, "extraArmor", "ExtraArmor", "gui.config.extra_armor", "gui.config.tooltip.extra_armor", HELPFUL_BASE, HELPFUL_ACTIVE, Page.TWO, Column.RIGHT, null),
        FULL_BRIGHT(26, "fullBright", "FullBright", "gui.config.full_bright", "gui.config.tooltip.full_bright", HELPFUL_BASE, HELPFUL_ACTIVE, Page.TWO, Column.RIGHT, null),
        FAST_VILLAGERS(27, "fastVillagers", "FastVillagers", "gui.config.fast_villagers", "gui.config.tooltip.fast_villagers", HELPFUL_BASE, HELPFUL_ACTIVE, Page.TWO, Column.RIGHT, null),
        BLOOD_MOON_HELPER(28, "bloodMoonHelper", "BloodMoonHelper", "gui.config.blood_moon_helper", "gui.config.tooltip.blood_moon_helper", HELPFUL_BASE, HELPFUL_ACTIVE, Page.TWO, Column.RIGHT, null);

        private final int id;
        private final String fieldName;
        private final String configKey;
        private final String displayKey;
        private final String tooltipKey;
        private final int baseColor;
        private final int activeColor;
        private final Page page;
        private final Column column;
        private final NMConfUtils.CONFIG configEnum;

        ConfigOption(int id, String fieldName, String configKey, String displayKey, String tooltipKey, int baseColor, int activeColor, Page page, Column column, NMConfUtils.CONFIG configEnum) {
            this.id = id;
            this.fieldName = fieldName;
            this.configKey = configKey;
            this.displayKey = displayKey;
            this.tooltipKey = tooltipKey;
            this.baseColor = baseColor;
            this.activeColor = activeColor;
            this.page = page;
            this.column = column;
            this.configEnum = configEnum;
        }

        public int getId() {
            return this.id;
        }
        public NMConfUtils.CONFIG getConfigEnum() {
            return this.configEnum;
        }

        public String getFieldName() {
            return fieldName;
        }

        public String getConfigKey() {
            return configKey;
        }

        public String getDisplayKey() {
            return displayKey;
        }

        public String getTooltipKey() {
            return tooltipKey;
        }

        public int getBaseColor() {
            return baseColor;
        }

        public int getActiveColor() {
            return activeColor;
        }

        public Page getPage() {
            return page;
        }

        public Column getColumn() {
            return column;
        }

        public void initConf(){}

    }

    private static final List<ConfigOption> PAGE_ONE_LEFT = Arrays.asList(
            ConfigOption.MORE_VARIANTS,
            ConfigOption.EVOLVED_MOBS,
            ConfigOption.BUFFED_SQUIDS,
            ConfigOption.NITE,
            ConfigOption.DARK_STORMY_NIGHTMARE,
            ConfigOption.NO_SKYBASES,
            ConfigOption.CANCER_MODE
    );

    private static final List<ConfigOption> PAGE_ONE_RIGHT = Arrays.asList(
            ConfigOption.REAL_TIME,
            ConfigOption.TOTAL_ECLIPSE,
            ConfigOption.BLOODMARE,
            ConfigOption.MAGIC_MONSTERS,
            ConfigOption.UNKILLABLE_MOBS,
            ConfigOption.NO_HIT
    );

    private static final List<ConfigOption> PAGE_TWO_LEFT = Arrays.asList(
            ConfigOption.SHOULD_SHOW_DATE_TIMER,
            ConfigOption.SHOULD_SHOW_REAL_TIMER,
            ConfigOption.BLOODMOON_COLORS,
            ConfigOption.CRIMSON,
            ConfigOption.CONFIG_ON_HUD,
            ConfigOption.POTION_PARTICLES,
            ConfigOption.SHOULD_DISPLAY_FISHING_ANNOUNCEMENTS
    );

    private static final List<ConfigOption> PAGE_TWO_RIGHT = Arrays.asList(
            ConfigOption.APRIL_FOOLS_RENDERING,
            ConfigOption.PERFECT_START,
            ConfigOption.EXTRA_ARMOR,
            ConfigOption.FULL_BRIGHT,
            ConfigOption.FAST_VILLAGERS,
            ConfigOption.BLOOD_MOON_HELPER
    );

    private final Map<ConfigOption, GuiColoredButton> buttons = new HashMap<ConfigOption, GuiColoredButton>();

    private final GuiScreen parentGuiScreen;

    public GuiConfig(GuiScreen par1GuiScreen) {
        this.parentGuiScreen = par1GuiScreen;
    }

    @Override
    public void drawScreen(int par1, int par2, float par3) {
        this.drawDefaultBackground();
        super.drawScreen(par1, par2, par3);

        Page currentPage = this.isOnSecondPage ? Page.TWO : Page.ONE;
        this.drawPageText(currentPage);


        int fillColor = 0;
        int outlineColor = 0xFFFFFF;    // white outline
        float starRadius = 8.0f;        // tweak size as needed (8–10 looks good)

        for (Object button : this.buttonList) {
            if (button instanceof GuiColoredButton tempButton) {
                if (tempButton.drawButton) {
                    float starX = tempButton.xPosition + tempButton.width + 60;
                    float starY = tempButton.yPosition + 10; // center vertically in 20px button

                    int[] arr = NMConfUtils.getCompletedConfigs();
                    NMConfUtils.CONFIG configValue = tempButton.getConfigValue();


                    if(configValue != null && tempButton.id < 14 && arr[configValue.getId() - 1] == 1){
                        fillColor = 0xDFCF00;
                    } else{
                        fillColor = 0;
                    }
                    if (tempButton.id < 14) { // hardcoded for first page buttons and real time button
                        this.drawStar(starX, starY, starRadius, fillColor, outlineColor);
                    }
                }


            }
            else if (button instanceof GuiInvisibleTooltipArea tooltipArea) {

                if (tooltipArea.getHover() && tooltipArea.drawButton) {
                    tooltipArea.drawTooltip(this.mc, tooltipArea.xPosition, tooltipArea.yPosition, tooltipArea.width,tooltipArea.height,
                            tooltipArea.getTooltipText()
                    );
                }
            }
        }
        for (Object button : this.buttonList) {
            if (button instanceof GuiColoredButton tempButton) {
                if (tempButton.drawButton) {
                    if (tempButton.shouldDrawToolTip) {
                        tempButton.drawTooltip(this.mc, tempButton.xPosition, tempButton.yPosition, tempButton.width, tempButton.height, tempButton.getTooltipText());
                    }
                }
            }
        }
    }



    private void setConfigValues() {
        buttons.get(ConfigOption.MORE_VARIANTS).setConfigValue(NMConfUtils.CONFIG.MORE_VARIANTS);
        buttons.get(ConfigOption.EVOLVED_MOBS).setConfigValue(NMConfUtils.CONFIG.EVOLVED_MOBS);
        buttons.get(ConfigOption.BUFFED_SQUIDS).setConfigValue(NMConfUtils.CONFIG.BUFFED_SQUIDS);
        buttons.get(ConfigOption.NITE).setConfigValue(NMConfUtils.CONFIG.NITE);
        buttons.get(ConfigOption.DARK_STORMY_NIGHTMARE).setConfigValue(NMConfUtils.CONFIG.DARK_STORMY_NIGHTMARE);
        buttons.get(ConfigOption.NO_SKYBASES).setConfigValue(NMConfUtils.CONFIG.NO_SKYBASES);
        buttons.get(ConfigOption.CANCER_MODE).setConfigValue(NMConfUtils.CONFIG.CANCER_MODE);
        buttons.get(ConfigOption.TOTAL_ECLIPSE).setConfigValue(NMConfUtils.CONFIG.TOTAL_ECLIPSE);
        buttons.get(ConfigOption.BLOODMARE).setConfigValue(NMConfUtils.CONFIG.BLOODMARE);
        buttons.get(ConfigOption.MAGIC_MONSTERS).setConfigValue(NMConfUtils.CONFIG.MAGIC_MONSTERS);
        buttons.get(ConfigOption.UNKILLABLE_MOBS).setConfigValue(NMConfUtils.CONFIG.UNKILLABLE_MOBS);
        buttons.get(ConfigOption.NO_HIT).setConfigValue(NMConfUtils.CONFIG.NO_HIT);
        buttons.get(ConfigOption.REAL_TIME).setConfigValue(NMConfUtils.CONFIG.REAL_TIME);
    }

    private void drawPageText(Page page) {
        List<List<ConfigOption>> pageLists = (page == Page.ONE)
                ? Arrays.asList(PAGE_ONE_LEFT, PAGE_ONE_RIGHT)
                : Arrays.asList(PAGE_TWO_LEFT, PAGE_TWO_RIGHT);

        for (List<ConfigOption> list : pageLists) {
            for (ConfigOption option : list) {
                GuiColoredButton button = this.buttons.get(option);
                if (button == null || !button.drawButton) {
                    continue;
                }

                boolean value = this.getValue(option);
                String textToDisplay = cap(Boolean.toString(value));
                int color = value ? option.getActiveColor() : option.getBaseColor();
                int stringWidth = this.fontRenderer.getStringWidth(textToDisplay);
                int textX = button.xPosition + 100 + stringWidth;
                int textY = button.yPosition + 7;

                this.drawCenteredString(this.fontRenderer, textToDisplay, textX, textY, color);
            }
        }
    }

    public static int getRainbowColor(float speed, float offset, float saturation, float brightness) {
        float time = (System.currentTimeMillis() % (long)(360 * speed)) / (speed * 360f);
        float hue = (time + offset) % 1.0f;
        return Color.HSBtoRGB(hue, saturation, brightness);
    }



    private void drawStar(float centerX, float centerY, float radius, int fillColor, int outlineColor) {
        // Outer radius (tips), inner radius (valleys) for a nice star shape
        float outerR = radius;
        float innerR = radius * 0.382f; // golden ratio-ish proportion ~0.382

        Tessellator tessellator = Tessellator.instance;
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL11.glDisable(GL11.GL_CULL_FACE);

        // 1. Draw filled black star
        GL11.glColor4f(
                (fillColor >> 16 & 255) / 255f,
                (fillColor >> 8 & 255) / 255f,
                (fillColor & 255) / 255f,
                1.0f
        );
        tessellator.startDrawing(GL11.GL_TRIANGLE_FAN);
//        tessellator.setColorOpaque_I(fillColor); // didn't help
        tessellator.addVertex(centerX, centerY, 0); // center


        for (int i = 0; i <= 10; i++) { // 5 points + closing
             float angle = (float) (Math.PI * 2 * i / 10.0 - Math.PI / 2);
             float r = (i % 2 == 0) ? outerR: innerR;
             float x = centerX + (float) Math.cos(angle) * r;
             float y = centerY + (float) Math.sin(angle) * r;
             tessellator.addVertex(x, y, 0);

        }
        tessellator.draw();

        // 2. Draw white outline (slightly larger)
        GL11.glLineWidth(1.5f);
        GL11.glColor4f(
                (outlineColor >> 16 & 255) / 255f,
                (outlineColor >> 8 & 255) / 255f,
                (outlineColor & 255) / 255f,
                1.0f
        );

        tessellator.startDrawing(GL11.GL_LINE_LOOP);
        for (int i = 0; i < 10; i++) {
            float angle = (float) (Math.PI * 2 * i / 10.0 - Math.PI / 2);
            float r = (i % 2 == 0) ? outerR : innerR;
            float x = centerX + (float) Math.cos(angle) * r;
            float y = centerY + (float) Math.sin(angle) * r;
            tessellator.addVertex(x, y, 0);
        }
        tessellator.draw();

        // Restore state
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_CULL_FACE);

    }

    private void drawStarFill(
            float cx, float cy,
            float outerR, float innerR,
            int color
    ) {
        Tessellator t = Tessellator.instance;

        // Precompute star points
        float[] xs = new float[10];
        float[] ys = new float[10];

        for (int i = 0; i < 10; i++) {
            float angle = (float)(Math.PI * 2 * i / 10.0 - Math.PI / 2);
            float r = (i % 2 == 0) ? outerR : innerR;
            xs[i] = cx + (float)Math.cos(angle) * r;
            ys[i] = cy + (float)Math.sin(angle) * r;
        }

        t.startDrawing(GL11.GL_TRIANGLES);
        t.setColorOpaque_I(color);

        for (int i = 0; i < 10; i++) {
            int next = (i + 1) % 10;

            t.addVertex(cx, cy, 0);
            t.addVertex(xs[i], ys[i], 0);
            t.addVertex(xs[next], ys[next], 0);
        }

        t.draw();
    }


    private void setButtonSettings(boolean showFirst) {
        List<List<ConfigOption>> showLists = showFirst ? Arrays.asList(PAGE_ONE_LEFT, PAGE_ONE_RIGHT) : Arrays.asList(PAGE_TWO_LEFT, PAGE_TWO_RIGHT);
        List<List<ConfigOption>> hideLists = showFirst ? Arrays.asList(PAGE_TWO_LEFT, PAGE_TWO_RIGHT) : Arrays.asList(PAGE_ONE_LEFT, PAGE_ONE_RIGHT);

        for (List<ConfigOption> list : hideLists) {
            for (ConfigOption option : list) {
                GuiColoredButton button = this.buttons.get(option);
                if (button != null) {
                    button.drawButton = false;
                }
            }
        }

        for (List<ConfigOption> list : showLists) {
            for (ConfigOption option : list) {
                GuiColoredButton button = this.buttons.get(option);
                if (button != null) {
                    button.drawButton = true;
                }
            }
        }


        for(Object button : this.buttonList){
            if(button instanceof GuiInvisibleTooltipArea b){
                b.drawButton = showFirst;
            }
        }

        this.isOnSecondPage = !showFirst;
    }

    @Override
    public void initGui() {
        this.buttonList.clear();
        this.buttons.clear();

        int baseX = this.width / 8;
        int rightColumnX = baseX + 200;
        int heightMultiplier = 25;

        this.buttonList.add(new GuiButton(0, baseX, this.height - 30, 100, 20, I18n.getString("gui.config.go_back")));
        this.buttonList.add(new GuiButton(14, baseX + 200, this.height - 30, 100, 20, I18n.getString("gui.config.switch_pages")));
//        this.buttonList.add(new GuiButton(40, baseX + 200, this.height - 30, 100, 20, I18n.getString("gui.config.switch_pages")));

        this.createButtonsForList(PAGE_ONE_LEFT, baseX, heightMultiplier, true);
        this.createButtonsForList(PAGE_ONE_RIGHT, rightColumnX, heightMultiplier, true);
        this.createButtonsForList(PAGE_TWO_LEFT, baseX, heightMultiplier, false);
        this.createButtonsForList(PAGE_TWO_RIGHT, rightColumnX, heightMultiplier, false);

        this.initializeButtonStates();
        this.setConfigValues();
    }

    private void createButtonsForList(List<ConfigOption> list, int x, int heightMultiplier, boolean isFirstPage) {
        for (int i = 0; i < list.size(); i++) {
            ConfigOption option = list.get(i);
            int y = (i + 1) * heightMultiplier;

            // config toggle button
            GuiColoredButton button = new GuiColoredButton(
                    option.getId(),
                    x,
                    y,
                    100,
                    20,
                    I18n.getString(option.getDisplayKey()),
                    option.getBaseColor(),
                    option.getActiveColor()
            );
            button.setTooltipText(I18n.getString(option.getTooltipKey()));
            if (option.getConfigEnum() != null) {
                System.out.println("set: "+ option.getConfigEnum());
                button.setConfigValue(option.getConfigEnum());
            }
            this.buttonList.add(button);
            this.buttons.put(option, button);


            // tooltip
//            System.out.println(isFirstPage);
            if (isFirstPage) {
                int starX = x + 150;
                int starY = y + 2;
                int hitboxSize = 20;

                String clearCondition = I18n.getString("gui.config.clear." + option.getFieldName().toLowerCase());

                GuiInvisibleTooltipArea starTooltipArea = new GuiInvisibleTooltipArea(
                        200 + option.getId(),
                        starX - 2,
                        starY - 2,
                        hitboxSize + 4,
                        hitboxSize + 4,
                        clearCondition
                );
                this.buttonList.add(starTooltipArea);
            }
        }
    }
    private void initializeButtonStates() {
        for (ConfigOption option : ConfigOption.values()) {
            GuiColoredButton button = this.buttons.get(option);
            if (button != null) {
                button.updateState(this.getValue(option));
            }
        }
        this.setButtonSettings(true); // Start with first page visible
    }

    private static String cap(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    private boolean getValue(ConfigOption option) {
        try {
            Field field = NightmareMode.class.getDeclaredField(option.getFieldName());
            field.setAccessible(true);
            return ((Boolean) field.get(null)).booleanValue();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void setValue(ConfigOption option, boolean value) {
        try {
            Field field = NightmareMode.class.getDeclaredField(option.getFieldName());
            field.setAccessible(true);
            field.set(null, Boolean.valueOf(value));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ConfigOption getOptionById(int id) {
        for (ConfigOption option : ConfigOption.values()) {
            if (option.getId() == id) {
                return option;
            }
        }
        return null;
    }

    @Override
    protected void actionPerformed(GuiButton par1GuiButton) {
        NightmareMode instance = NightmareMode.getInstance();
        AddonConfig config = instance.addonConfig;

        if (par1GuiButton.id == 0) {
            this.mc.displayGuiScreen(this.parentGuiScreen);
        } else if (par1GuiButton.id == 14) {
            this.setButtonSettings(this.isOnSecondPage);
        } else {
            ConfigOption option = this.getOptionById(par1GuiButton.id);
            if (option != null) {
                boolean newValue = !this.getValue(option);
                this.setValue(option, newValue);
                instance.modifyConfigProperty(option.getConfigKey(), newValue, config);
                GuiColoredButton button = this.buttons.get(option);
                if (button != null) {
                    button.updateState(newValue);
                }
            }
        }
    }
}