package com.itlesports.nightmaremode.nmgui;

import api.config.AddonConfig;
import btw.community.nightmaremode.NightmareMode;
import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.I18n;

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
    private int onPage = 1;

    private enum Page { ONE, TWO, THREE }

    private enum Column { LEFT, RIGHT }




    public enum ConfigOption {
        MORE_VARIANTS(1, "moreVariants", "MoreVariants", "gui.config.more_variants", "gui.config.tooltip.more_variants", EASY_BASE, EASY_ACTIVE, Page.ONE, Column.LEFT),
        BUFFED_SQUIDS(3, "buffedSquids", "BuffedSquids", "gui.config.buffed_squids", "gui.config.tooltip.buffed_squids", EASY_BASE, EASY_ACTIVE, Page.ONE, Column.LEFT),

        UNKILLABLE_MOBS(11, "unkillableMobs", "UnkillableMobs", "gui.config.unkillable_mobs", "gui.config.tooltip.unkillable_mobs", MED_BASE, MED_ACTIVE, Page.ONE, Column.LEFT),
        BLOODMARE(9, "bloodmare", "Bloodmare", "gui.config.bloodmare", "gui.config.tooltip.bloodmare", MED_BASE, MED_ACTIVE, Page.ONE, Column.LEFT),
        MAGIC_MONSTERS(10, "magicMonsters", "MagicMonsters", "gui.config.magic_monsters", "gui.config.tooltip.magic_monsters", MED_BASE, MED_ACTIVE, Page.ONE, Column.LEFT),

        NITE(4, "nite", "NITE", "gui.config.nite", "gui.config.tooltip.nite", HARD_BASE, HARD_ACTIVE, Page.ONE, Column.RIGHT),
        TOTAL_ECLIPSE(8, "totalEclipse", "TotalEclipse", "gui.config.total_eclipse", "gui.config.tooltip.total_eclipse", HARD_BASE, HARD_ACTIVE, Page.ONE, Column.RIGHT),
        CANCER_MODE(7, "isAprilFools", "AprilFoolsPatch", "gui.config.cancer_mode", "gui.config.tooltip.cancer_mode", HARD_BASE, HARD_ACTIVE, Page.ONE, Column.RIGHT),
        DARK_STORMY_NIGHTMARE(5, "darkStormyNightmare", "DarkStormyNightmare", "gui.config.dark_stormy_night", "gui.config.tooltip.dark_stormy_night", HARD_BASE, HARD_ACTIVE, Page.ONE, Column.RIGHT),

        EVOLVED_MOBS(2, "evolvedMobs", "EvolvedMobs", "gui.config.evolved_mobs", "gui.config.tooltip.evolved_mobs", IMPOSSIBLE_BASE, IMPOSSIBLE_ACTIVE, Page.ONE, Column.RIGHT),
        NO_HIT(12, "noHit", "NoHit", "gui.config.no_hit", "gui.config.tooltip.no_hit", IMPOSSIBLE_BASE, IMPOSSIBLE_ACTIVE, Page.ONE, Column.RIGHT),


        SHOULD_SHOW_DATE_TIMER(15, "shouldShowDateTimer", "NmMinecraftDayTimer", "gui.config.date_timer", "gui.config.tooltip.date_timer", AMBIENT_BASE, AMBIENT_ACTIVE, Page.TWO, Column.LEFT),
        SHOULD_SHOW_REAL_TIMER(16, "shouldShowRealTimer", "NmTimer", "gui.config.real_timer", "gui.config.tooltip.real_timer", AMBIENT_BASE, AMBIENT_ACTIVE, Page.TWO, Column.LEFT),
        BLOODMOON_COLORS(17, "bloodmoonColors", "BloodmoonColors", "gui.config.bloodmoon_colors", "gui.config.tooltip.bloodmoon_colors", AMBIENT_BASE, AMBIENT_ACTIVE, Page.TWO, Column.LEFT),
        CRIMSON(18, "crimson", "Crimson", "gui.config.crimson", "gui.config.tooltip.crimson", AMBIENT_BASE, AMBIENT_ACTIVE, Page.TWO, Column.LEFT),
        POTION_PARTICLES(20, "potionParticles", "PotionParticles", "gui.config.potion_particles", "gui.config.tooltip.potion_particles", AMBIENT_BASE, AMBIENT_ACTIVE, Page.TWO, Column.LEFT),
        SHOULD_DISPLAY_FISHING_ANNOUNCEMENTS(21, "shouldDisplayFishingAnnouncements", "FishingAnnouncements", "gui.config.fishing_alerts", "gui.config.tooltip.fishing_alerts", AMBIENT_BASE, AMBIENT_ACTIVE, Page.TWO, Column.LEFT),
        APRIL_FOOLS_RENDERING(22, "aprilFoolsRendering", "AprilFoolsWarpedRendering", "gui.config.cm_rendering", "gui.config.tooltip.cm_rendering", AMBIENT_BASE, AMBIENT_ACTIVE, Page.TWO, Column.RIGHT),
        PERFECT_START(23, "perfectStart", "PerfectStart", "gui.config.perfect_start", "gui.config.tooltip.perfect_start", HELPFUL_BASE, HELPFUL_ACTIVE, Page.TWO, Column.RIGHT),
        EXTRA_ARMOR(24, "extraArmor", "ExtraArmor", "gui.config.extra_armor", "gui.config.tooltip.extra_armor", HELPFUL_BASE, HELPFUL_ACTIVE, Page.TWO, Column.RIGHT),
        FULL_BRIGHT(26, "fullBright", "FullBright", "gui.config.full_bright", "gui.config.tooltip.full_bright", HELPFUL_BASE, HELPFUL_ACTIVE, Page.TWO, Column.RIGHT),
        FAST_VILLAGERS(27, "fastVillagers", "FastVillagers", "gui.config.fast_villagers", "gui.config.tooltip.fast_villagers", HELPFUL_BASE, HELPFUL_ACTIVE, Page.TWO, Column.RIGHT),
        BLOOD_MOON_HELPER(28, "bloodMoonHelper", "BloodMoonHelper", "gui.config.blood_moon_helper", "gui.config.tooltip.blood_moon_helper", HELPFUL_BASE, HELPFUL_ACTIVE, Page.TWO, Column.RIGHT),
        DRAW_FANCY_CLOUDS(29, "renderFancyClouds", "RenderFancyClouds", "gui.config.render_fancy_clouds", "gui.config.tooltip.render_fancy_clouds", AMBIENT_BASE, AMBIENT_ACTIVE, Page.TWO, Column.RIGHT),
        RENDER_VIGNETTE(30, "renderVignette", "RenderVignette", "gui.config.render_vignette", "gui.config.tooltip.render_vignette", AMBIENT_BASE, AMBIENT_ACTIVE, Page.THREE, Column.LEFT);

        private final int id;
        private final String fieldName;
        private final String configKey;
        private final String displayKey;
        private final String tooltipKey;
        private final int baseColor;
        private final int activeColor;
        private final Page page;
        private final Column column;

        ConfigOption(int id, String fieldName, String configKey, String displayKey, String tooltipKey, int baseColor, int activeColor, Page page, Column column) {
            this.id = id;
            this.fieldName = fieldName;
            this.configKey = configKey;
            this.displayKey = displayKey;
            this.tooltipKey = tooltipKey;
            this.baseColor = baseColor;
            this.activeColor = activeColor;
            this.page = page;
            this.column = column;
        }

        public int getId() {
            return this.id;
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
            ConfigOption.BUFFED_SQUIDS,
            ConfigOption.UNKILLABLE_MOBS,
            ConfigOption.BLOODMARE,
            ConfigOption.MAGIC_MONSTERS
    );

    private static final List<ConfigOption> PAGE_ONE_RIGHT = Arrays.asList(
            ConfigOption.NITE,
            ConfigOption.TOTAL_ECLIPSE,
            ConfigOption.CANCER_MODE,
            ConfigOption.DARK_STORMY_NIGHTMARE,
            ConfigOption.EVOLVED_MOBS,
            ConfigOption.NO_HIT
    );

    private static final List<ConfigOption> PAGE_TWO_LEFT = Arrays.asList(
            ConfigOption.SHOULD_SHOW_DATE_TIMER,
            ConfigOption.SHOULD_SHOW_REAL_TIMER,
            ConfigOption.BLOODMOON_COLORS,
            ConfigOption.CRIMSON,
            ConfigOption.POTION_PARTICLES,
            ConfigOption.SHOULD_DISPLAY_FISHING_ANNOUNCEMENTS
    );

    private static final List<ConfigOption> PAGE_TWO_RIGHT = Arrays.asList(
            ConfigOption.APRIL_FOOLS_RENDERING,
            ConfigOption.PERFECT_START,
            ConfigOption.EXTRA_ARMOR,
            ConfigOption.FULL_BRIGHT,
            ConfigOption.FAST_VILLAGERS,
            ConfigOption.BLOOD_MOON_HELPER,
            ConfigOption.DRAW_FANCY_CLOUDS
    );

    private static final List<ConfigOption> PAGE_THREE_LEFT = Arrays.asList(
            ConfigOption.RENDER_VIGNETTE
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

        Page currentPage = this.onPage == 1 ? Page.ONE : (this.onPage == 2 ? Page.TWO : Page.THREE);
        this.drawPageText(currentPage);


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



    private void drawPageText(Page page) {
        List<List<ConfigOption>> pageLists = (page == Page.ONE)
                ? Arrays.asList(PAGE_ONE_LEFT, PAGE_ONE_RIGHT)
                : (page == Page.THREE ? List.of(PAGE_THREE_LEFT) :Arrays.asList(PAGE_TWO_LEFT, PAGE_TWO_RIGHT));

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
                int textX = button.xPosition + 110 + stringWidth;
                int textY = button.yPosition + 7;

                this.drawCenteredString(this.fontRenderer, textToDisplay, textX, textY, color);
            }
        }
    }

    private void setButtonSettings(int pageNumber) {
        List<List<ConfigOption>> showLists = pageNumber == 1 ? Arrays.asList(PAGE_ONE_LEFT, PAGE_ONE_RIGHT) : (pageNumber == 2 ? Arrays.asList(PAGE_TWO_LEFT, PAGE_TWO_RIGHT) : Arrays.asList(PAGE_THREE_LEFT));
        List<List<ConfigOption>> hideLists = pageNumber == 1 ? Arrays.asList(PAGE_TWO_LEFT, PAGE_TWO_RIGHT, PAGE_THREE_LEFT) : (pageNumber == 2 ? Arrays.asList(PAGE_ONE_LEFT, PAGE_ONE_RIGHT) : Arrays.asList(PAGE_ONE_LEFT, PAGE_ONE_RIGHT,PAGE_TWO_LEFT, PAGE_TWO_RIGHT));

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


        this.onPage = pageNumber;

//        this.isOnSecondPage = !showFirst;
    }

    @Override
    public void initGui() {
        this.buttonList.clear();
        this.buttons.clear();

        int baseX = this.width / 8 - 40;
        int rightColumnX = baseX + 200;
        int heightMultiplier = 25;

        this.buttonList.add(new GuiButton(0, baseX, this.height - 30, 100, 20, I18n.getString("gui.config.go_back")));
        this.buttonList.add(new GuiButton(14, baseX + 200, this.height - 30, 100, 20, I18n.getString("gui.config.switch_pages")));
//        this.buttonList.add(new GuiButton(40, baseX + 200, this.height - 30, 100, 20, I18n.getString("gui.config.switch_pages")));

        this.createButtonsForList(PAGE_ONE_LEFT, baseX, heightMultiplier);
        this.createButtonsForList(PAGE_ONE_RIGHT, rightColumnX, heightMultiplier);
        this.createButtonsForList(PAGE_TWO_LEFT, baseX, heightMultiplier);
        this.createButtonsForList(PAGE_TWO_RIGHT, rightColumnX, heightMultiplier);
        this.createButtonsForList(PAGE_THREE_LEFT, baseX, heightMultiplier);

        this.initializeButtonStates();
    }

    private void createButtonsForList(List<ConfigOption> list, int x, int heightMultiplier) {
        for (int i = 0; i < list.size(); i++) {
            ConfigOption option = list.get(i);
            int y = (i + 1) * heightMultiplier;

            // config toggle button
            GuiColoredButton button = new GuiColoredButton(
                    option.getId(),
                    x,
                    y,
                    110,
                    20,
                    I18n.getString(option.getDisplayKey()),
                    option.getBaseColor(),
                    option.getActiveColor()
            );
            button.setTooltipText(I18n.getString(option.getTooltipKey()));
            this.buttonList.add(button);
            this.buttons.put(option, button);


        }
    }

    private void initializeButtonStates() {
        for (ConfigOption option : ConfigOption.values()) {
            GuiColoredButton button = this.buttons.get(option);
            if (button != null) {
                button.updateState(this.getValue(option));
            }
        }
        this.setButtonSettings(1); // Start with first page visible
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
            int pageToSelect = this.onPage + 1;
            if(pageToSelect > 3) pageToSelect = 1;
            this.setButtonSettings(pageToSelect);
        } else {
            ConfigOption option = this.getOptionById(par1GuiButton.id);
            if (option != null) {
                boolean newValue = !this.getValue(option);
                this.setValue(option, newValue);
                instance.modifyConfigProperty(option.getConfigKey(), newValue, config, option.getPage() == Page.ONE);
                GuiColoredButton button = this.buttons.get(option);
                if (button != null) {
                    button.updateState(newValue);
                }
            }
        }
    }
}
