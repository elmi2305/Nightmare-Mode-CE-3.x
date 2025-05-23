package com.itlesports.nightmaremode.nmgui;

import btw.community.nightmaremode.NightmareMode;
import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiScreen;

public class GuiConfig extends GuiScreen {

    private boolean isOnSecondPage = false;
    private GuiColoredButton moreVariantsButton;        // 1
    private GuiColoredButton evolvedMobsButton;         // 2
    private GuiColoredButton buffedSquidsButton;        // 3
    private GuiColoredButton NITEButton;                // 4
    private GuiColoredButton darkStormyNightmareButton; // 5
    private GuiColoredButton noSkybasesButton;          // 6
    private GuiColoredButton cancerModeButton;          // 7
    private GuiColoredButton totalEclipseButton;        // 8
    private GuiColoredButton bloodmareButton;           // 9
    private GuiColoredButton magicMonstersButton;       // 10
    private GuiColoredButton unkillableMobsButton;      // 11
    private GuiColoredButton noHitButton;               // 12
    private GuiColoredButton hordeModeButton;           // 24

    private GuiColoredButton shouldShowDateTimerButton; // 14
    private GuiColoredButton shouldShowRealTimerButton; // 15
    private GuiColoredButton bloodmoonColorsButton; // 16
    private GuiColoredButton crimsonButton; // 17
    private GuiColoredButton configOnHudButton; // 18
    private GuiColoredButton potionParticlesButton; // 19
    private GuiColoredButton shouldDisplayFishingAnnouncementsButton; // 20
    private GuiColoredButton aprilFoolsRenderingButton; // 21
    private GuiColoredButton perfectStartButton; // 22
    private GuiColoredButton extraArmorButton; // 23



    private final GuiScreen parentGuiScreen;

    public GuiConfig(GuiScreen par1GuiScreen) {
        this.parentGuiScreen = par1GuiScreen;
    }

    @Override
    public void drawScreen(int par1, int par2, float par3) {
        this.drawDefaultBackground();
        super.drawScreen(par1, par2, par3);

        if (this.isOnSecondPage) {
            this.drawSecondPageText();
        } else{
            this.drawFirstPageText();
        }
    }

    private void drawFirstPageText(){
        String textToDisplay;
        int width = this.width / 8;
        int textIndex = 1;
        int heightMultiplier = 25;

        textToDisplay = cap(Boolean.toString(NightmareMode.moreVariants));
        this.drawCenteredString(this.fontRenderer, textToDisplay, width + 100 + this.fontRenderer.getStringWidth(textToDisplay), heightMultiplier * textIndex  + 7, (NightmareMode.moreVariants ? 0xFFFF00 : 0x8a8a00));
        textIndex ++;

        textToDisplay = cap(Boolean.toString(NightmareMode.evolvedMobs));
        this.drawCenteredString(this.fontRenderer, textToDisplay, width + 100 + this.fontRenderer.getStringWidth(textToDisplay), heightMultiplier * textIndex  + 7, (NightmareMode.evolvedMobs ? 0xFFFF00 : 0x8a8a00));
        textIndex ++;

        textToDisplay = cap(Boolean.toString(NightmareMode.buffedSquids));
        this.drawCenteredString(this.fontRenderer, textToDisplay, width + 100 + this.fontRenderer.getStringWidth(textToDisplay), heightMultiplier * textIndex  + 7, (NightmareMode.buffedSquids ? 0xFFFF00 : 0x8a8a00));
        textIndex ++;

        textToDisplay = cap(Boolean.toString(NightmareMode.nite));
        this.drawCenteredString(this.fontRenderer, textToDisplay, width + 100 + this.fontRenderer.getStringWidth(textToDisplay), heightMultiplier * textIndex  + 7, (NightmareMode.nite ? 0xff8800 : 0x9c5300)); // orange
        textIndex ++;

        textToDisplay = cap(Boolean.toString(NightmareMode.darkStormyNightmare));
        this.drawCenteredString(this.fontRenderer, textToDisplay, width + 100 + this.fontRenderer.getStringWidth(textToDisplay), heightMultiplier * textIndex  + 7, (NightmareMode.darkStormyNightmare ? 0xff8800 : 0x9c5300)); // orange
        textIndex ++;

        textToDisplay = cap(Boolean.toString(NightmareMode.noSkybases));
        this.drawCenteredString(this.fontRenderer, textToDisplay, width + 100 + this.fontRenderer.getStringWidth(textToDisplay), heightMultiplier * textIndex  + 7, (NightmareMode.noSkybases ? 0xff8800 : 0x9c5300)); // orange
        textIndex ++;

        textToDisplay = cap(Boolean.toString(NightmareMode.isAprilFools));
        this.drawCenteredString(this.fontRenderer, textToDisplay, width + 100 + this.fontRenderer.getStringWidth(textToDisplay), heightMultiplier * textIndex  + 7, (NightmareMode.isAprilFools ? 0xff8800 : 0x9c5300)); // orange

        textIndex -= 6;
        width += 200;

        textToDisplay = cap(Boolean.toString(NightmareMode.hordeMode));
        this.drawCenteredString(this.fontRenderer, textToDisplay, width + 100 + this.fontRenderer.getStringWidth(textToDisplay), heightMultiplier * textIndex  + 7, (NightmareMode.hordeMode ? 0xFF0000 : 0x870101));
        textIndex ++;


        textToDisplay = cap(Boolean.toString(NightmareMode.totalEclipse));
        this.drawCenteredString(this.fontRenderer, textToDisplay, width + 100 + this.fontRenderer.getStringWidth(textToDisplay), heightMultiplier * textIndex  + 7, (NightmareMode.totalEclipse ? 0xFF0000 : 0x870101));
        textIndex ++;

        textToDisplay = cap(Boolean.toString(NightmareMode.bloodmare));
        this.drawCenteredString(this.fontRenderer, textToDisplay, width + 100 + this.fontRenderer.getStringWidth(textToDisplay), heightMultiplier * textIndex  + 7, (NightmareMode.bloodmare ? 0xFF0000 : 0x870101));
        textIndex ++;

        textToDisplay = cap(Boolean.toString(NightmareMode.magicMonsters));
        this.drawCenteredString(this.fontRenderer, textToDisplay, width + 100 + this.fontRenderer.getStringWidth(textToDisplay), heightMultiplier * textIndex  + 7, (NightmareMode.magicMonsters ? 0xFF0000 : 0x870101));
        textIndex ++;

        textToDisplay = cap(Boolean.toString(NightmareMode.unkillableMobs));
        this.drawCenteredString(this.fontRenderer, textToDisplay, width + 100 + this.fontRenderer.getStringWidth(textToDisplay), heightMultiplier * textIndex + 7, (NightmareMode.unkillableMobs ? 0xAAAAAA : 0x484848));
        textIndex ++;

        textToDisplay = cap(Boolean.toString(NightmareMode.noHit));
        this.drawCenteredString(this.fontRenderer, textToDisplay, width + 100 + this.fontRenderer.getStringWidth(textToDisplay), heightMultiplier * textIndex + 7, (NightmareMode.noHit ? 0xAAAAAA : 0x484848));

        for(Object coloredButton: this.buttonList){
            if(coloredButton instanceof GuiColoredButton tempButton){
                if(tempButton.shouldDrawToolTip){
                    tempButton.drawTooltip(this.mc, tempButton.xPosition, tempButton.yPosition, tempButton.width, tempButton.height, tempButton.getTooltipText());
                }
            }
        }
    }

    private void drawSecondPageText(){
        int width = this.width / 8;
        int textIndex = 1;
        int heightMultiplier = 25;

        String textToDisplay = cap(Boolean.toString(NightmareMode.shouldShowDateTimer));

        this.drawCenteredString(this.fontRenderer, textToDisplay, width + 100 + this.fontRenderer.getStringWidth(textToDisplay), heightMultiplier * textIndex  + 7, (NightmareMode.shouldShowDateTimer ? 0x428BFF : 0x264f91));
        textIndex ++;

        textToDisplay = cap(Boolean.toString(NightmareMode.shouldShowRealTimer));
        this.drawCenteredString(this.fontRenderer, textToDisplay, width + 100 + this.fontRenderer.getStringWidth(textToDisplay), heightMultiplier * textIndex  + 7, (NightmareMode.shouldShowRealTimer ? 0x428BFF : 0x264f91));
        textIndex ++;

        textToDisplay = cap(Boolean.toString(NightmareMode.bloodmoonColors));
        this.drawCenteredString(this.fontRenderer, textToDisplay, width + 100 + this.fontRenderer.getStringWidth(textToDisplay), heightMultiplier * textIndex  + 7, (NightmareMode.bloodmoonColors ? 0x428BFF : 0x264f91));
        textIndex ++;

        textToDisplay = cap(Boolean.toString(NightmareMode.crimson));
        this.drawCenteredString(this.fontRenderer, textToDisplay, width + 100 + this.fontRenderer.getStringWidth(textToDisplay), heightMultiplier * textIndex  + 7, (NightmareMode.crimson ? 0x428BFF : 0x264f91));
        textIndex ++;

        textToDisplay = cap(Boolean.toString(NightmareMode.configOnHud));
        this.drawCenteredString(this.fontRenderer, textToDisplay, width + 100 + this.fontRenderer.getStringWidth(textToDisplay), heightMultiplier * textIndex  + 7, (NightmareMode.configOnHud ? 0x428BFF : 0x264f91));
        textIndex ++;

        textToDisplay = cap(Boolean.toString(NightmareMode.potionParticles));
        this.drawCenteredString(this.fontRenderer, textToDisplay, width + 100 + this.fontRenderer.getStringWidth(textToDisplay), heightMultiplier * textIndex  + 7, (NightmareMode.potionParticles ? 0x428BFF : 0x264f91));
        textIndex = 1;
        width += 200;

        textToDisplay = cap(Boolean.toString(NightmareMode.shouldDisplayFishingAnnouncements));
        this.drawCenteredString(this.fontRenderer, textToDisplay, width + 100 + this.fontRenderer.getStringWidth(textToDisplay), heightMultiplier * textIndex  + 7, (NightmareMode.shouldDisplayFishingAnnouncements ? 0x428BFF : 0x264f91));
        textIndex ++;

        textToDisplay = cap(Boolean.toString(NightmareMode.aprilFoolsRendering));
        this.drawCenteredString(this.fontRenderer, textToDisplay, width + 100 + this.fontRenderer.getStringWidth(textToDisplay), heightMultiplier * textIndex  + 7, (NightmareMode.aprilFoolsRendering ? 0x428BFF : 0x264f91));
        textIndex ++; // adds one button of gap between green and blue

        textIndex ++;
        textToDisplay = cap(Boolean.toString(NightmareMode.perfectStart));
        this.drawCenteredString(this.fontRenderer, textToDisplay, width + 100 + this.fontRenderer.getStringWidth(textToDisplay), heightMultiplier * textIndex  + 7, (NightmareMode.perfectStart ? 0x5fe647 : 0x429c32));

        textIndex ++;
        textToDisplay = cap(Boolean.toString(NightmareMode.extraArmor));
        this.drawCenteredString(this.fontRenderer, textToDisplay, width + 100 + this.fontRenderer.getStringWidth(textToDisplay), heightMultiplier * textIndex  + 7, (NightmareMode.extraArmor ? 0x5fe647 : 0x429c32));

        for(Object coloredButton: this.buttonList){
            if(coloredButton instanceof GuiColoredButton tempButton){
                if(tempButton.shouldDrawToolTip){
                    tempButton.drawTooltip(this.mc, tempButton.xPosition, tempButton.yPosition, tempButton.width, tempButton.height, tempButton.getTooltipText());
                }
            }
        }
    }
    private void setButtonSettings(boolean par1){
        this.moreVariantsButton.drawButton = par1;
        this.evolvedMobsButton.drawButton = par1;
        this.buffedSquidsButton.drawButton = par1;
        this.cancerModeButton.drawButton = par1;
        this.NITEButton.drawButton = par1;
        this.noSkybasesButton.drawButton = par1;
        this.hordeModeButton.drawButton = par1;
        this.totalEclipseButton.drawButton = par1;
        this.bloodmareButton.drawButton = par1;
        this.magicMonstersButton.drawButton = par1;
        this.unkillableMobsButton.drawButton = par1;
        this.noHitButton.drawButton = par1;
        this.darkStormyNightmareButton.drawButton = par1;
        this.isOnSecondPage = !par1;

        this.shouldShowDateTimerButton.drawButton = !par1;
        this.shouldShowRealTimerButton.drawButton = !par1;
        this.bloodmoonColorsButton.drawButton = !par1;
        this.crimsonButton.drawButton = !par1;
        this.configOnHudButton.drawButton = !par1;
        this.potionParticlesButton.drawButton = !par1;
        this.shouldDisplayFishingAnnouncementsButton.drawButton = !par1;
        this.aprilFoolsRenderingButton.drawButton = !par1;
        this.perfectStartButton.drawButton = !par1;
        this.extraArmorButton.drawButton = !par1;
    }

    @Override
    public void initGui() {
        this.buttonList.clear();
        int width = this.width / 8;
        this.buttonList.add(new GuiButton(0, width, this.height - 30, 100, 20, "Go back"));
        this.buttonList.add(new GuiButton(13, width + 200, this.height - 30, 100, 20, "Switch pages"));

        // add configs
        int easyBase = 0x8a8a00;
        int easyActive = 0xFFFF00;

        int mediumBase = 0x9c5300;
        int mediumActive = 0xff8800;

        int hardBase = 0x870101;
        int hardActive = 0xFF0000;

        int impossibleBase = 0x484848;
        int impossibleActive = 0xAAAAAA;

        int heightMultiplier = 25;
        int rightColumnX = width + 200;

// Left column buttons
        addMoreVariantsButton(width, heightMultiplier, easyBase, easyActive);
        addEvolvedMobsButton(width, heightMultiplier, easyBase, easyActive);
        addBuffedSquidsButton(width, heightMultiplier, easyBase, easyActive);
        addNITEButton(width, heightMultiplier, mediumBase, mediumActive);
        addDarkStormyNightmareButton(width, heightMultiplier, mediumBase, mediumActive);
        addNoSkybasesButton(width, heightMultiplier, mediumBase, mediumActive);
        addCancerModeButton(width, heightMultiplier, mediumBase, mediumActive);

// Right column buttons
        addHordeModeButton(rightColumnX, heightMultiplier, hardBase, hardActive);
        addTotalEclipseButton(rightColumnX, heightMultiplier, hardBase, hardActive);
        addBloodmareButton(rightColumnX, heightMultiplier, hardBase, hardActive);
        addMagicMonstersButton(rightColumnX, heightMultiplier, hardBase, hardActive);
        addUnkillableMobsButton(rightColumnX, heightMultiplier, impossibleBase, impossibleActive);
        addNoHitButton(rightColumnX, heightMultiplier, impossibleBase, impossibleActive);


        // second page

        int miscBase = 0x264f91;
        int miscActive = 0x428BFF;
        int goodBase = 0x429c32;
        int goodActive = 0x5fe647;
        // your starting X for misc buttons

// Left column buttons

        addDateTimerButton(width, heightMultiplier, miscBase, miscActive);
        addRealTimerButton(width, heightMultiplier, miscBase, miscActive);
        addBloodmoonColorsButton(width, heightMultiplier, miscBase, miscActive);
        addCrimsonButton(width, heightMultiplier, miscBase, miscActive);
        addConfigOnHudButton(width, heightMultiplier, miscBase, miscActive);
        addPotionParticlesButton(width, heightMultiplier, miscBase, miscActive);

// Right column buttons
        addFishingAlertsButton(rightColumnX, heightMultiplier, miscBase, miscActive);
        addCMRenderingButton(rightColumnX, heightMultiplier, miscBase, miscActive);

// beneficial configs
        addPerfectStartButton(rightColumnX, heightMultiplier, goodBase, goodActive);
        addExtraArmorButton(rightColumnX, heightMultiplier, goodBase, goodActive);


        this.initializeButtonStates();
    }

    private void initializeButtonStates() {
        // Buttons visible only on second page
        this.shouldShowDateTimerButton.drawButton = this.isOnSecondPage;
        this.shouldShowRealTimerButton.drawButton = this.isOnSecondPage;
        this.bloodmoonColorsButton.drawButton = this.isOnSecondPage;
        this.crimsonButton.drawButton = this.isOnSecondPage;
        this.configOnHudButton.drawButton = this.isOnSecondPage;
        this.potionParticlesButton.drawButton = this.isOnSecondPage;
        this.shouldDisplayFishingAnnouncementsButton.drawButton = this.isOnSecondPage;
        this.aprilFoolsRenderingButton.drawButton = this.isOnSecondPage;
        this.perfectStartButton.drawButton = this.isOnSecondPage;
        this.extraArmorButton.drawButton = this.isOnSecondPage;

        // Buttons visible only on first page
        this.moreVariantsButton.drawButton = !this.isOnSecondPage;
        this.evolvedMobsButton.drawButton = !this.isOnSecondPage;
        this.buffedSquidsButton.drawButton = !this.isOnSecondPage;
        this.cancerModeButton.drawButton = !this.isOnSecondPage;
        this.NITEButton.drawButton = !this.isOnSecondPage;
        this.noSkybasesButton.drawButton = !this.isOnSecondPage;
        this.hordeModeButton.drawButton = !this.isOnSecondPage;
        this.totalEclipseButton.drawButton = !this.isOnSecondPage;
        this.bloodmareButton.drawButton = !this.isOnSecondPage;
        this.magicMonstersButton.drawButton = !this.isOnSecondPage;
        this.unkillableMobsButton.drawButton = !this.isOnSecondPage;
        this.noHitButton.drawButton = !this.isOnSecondPage;

        // Update button states
        this.moreVariantsButton.updateState(NightmareMode.moreVariants);
        this.evolvedMobsButton.updateState(NightmareMode.evolvedMobs);
        this.buffedSquidsButton.updateState(NightmareMode.buffedSquids);
        this.cancerModeButton.updateState(NightmareMode.isAprilFools);
        this.NITEButton.updateState(NightmareMode.nite);
        this.noSkybasesButton.updateState(NightmareMode.noSkybases);
        this.hordeModeButton.updateState(NightmareMode.hordeMode);
        this.totalEclipseButton.updateState(NightmareMode.totalEclipse);
        this.bloodmareButton.updateState(NightmareMode.bloodmare);
        this.magicMonstersButton.updateState(NightmareMode.magicMonsters);
        this.unkillableMobsButton.updateState(NightmareMode.unkillableMobs);
        this.noHitButton.updateState(NightmareMode.noHit);

        this.shouldShowDateTimerButton.updateState(NightmareMode.shouldShowDateTimer);
        this.shouldShowRealTimerButton.updateState(NightmareMode.shouldShowRealTimer);
        this.bloodmoonColorsButton.updateState(NightmareMode.bloodmoonColors);
        this.crimsonButton.updateState(NightmareMode.crimson);
        this.configOnHudButton.updateState(NightmareMode.configOnHud);
        this.potionParticlesButton.updateState(NightmareMode.potionParticles);
        this.shouldDisplayFishingAnnouncementsButton.updateState(NightmareMode.shouldDisplayFishingAnnouncements);
        this.aprilFoolsRenderingButton.updateState(NightmareMode.aprilFoolsRendering);
        this.perfectStartButton.updateState(NightmareMode.perfectStart);
        this.extraArmorButton.updateState(NightmareMode.extraArmor);
        this.darkStormyNightmareButton.updateState(NightmareMode.darkStormyNightmare);
    }

    private void addMoreVariantsButton(int x, int heightMultiplier, int baseColor, int activeColor) {
        this.moreVariantsButton = new GuiColoredButton(1, x, 1 * heightMultiplier, 100, 20, "More Variants", baseColor,activeColor);
        this.moreVariantsButton.setTooltipText("Adds various new variants to NM");
        this.buttonList.add(this.moreVariantsButton);
    }
    private void addEvolvedMobsButton(int x, int heightMultiplier, int baseColor, int activeColor) {
        this.evolvedMobsButton = new GuiColoredButton(2, x, 2 * heightMultiplier, 100, 20, "Evolved Mobs", baseColor,activeColor);
        this.evolvedMobsButton.setTooltipText("All mob variants can spawn, regardless of world progress");
        this.buttonList.add(this.evolvedMobsButton);
    }

    private void addBuffedSquidsButton(int x, int heightMultiplier, int baseColor, int activeColor) {
        this.buffedSquidsButton = new GuiColoredButton(3, x, 3 * heightMultiplier, 100, 20, "Buffed Squids", baseColor,activeColor);
        this.buffedSquidsButton.setTooltipText("Squids have doubled stats and can chase the player on land");
        this.buttonList.add(this.buffedSquidsButton);
}

    private void addNITEButton(int x, int heightMultiplier, int baseColor, int activeColor) {
        this.NITEButton = new GuiColoredButton(4, x, 4 * heightMultiplier, 100, 20, "NITE", baseColor, activeColor);
        this.NITEButton.setTooltipText("Nightmare Is Too Easy. Start with 3 hearts and shanks. Gain them back by levelling up. Mobs get stronger the longer you play. Raw food is safe to eat. Reduced hunger cost & movement penalties. Inspired by MITE");
        this.buttonList.add(this.NITEButton);
    }

    private void addDarkStormyNightmareButton(int x, int heightMultiplier, int baseColor, int activeColor) {
        this.darkStormyNightmareButton = new GuiColoredButton(5, x, 5 * heightMultiplier, 100, 20, "Dark Stormy Night", baseColor, activeColor);
        this.darkStormyNightmareButton.setTooltipText("The world is under a constant thunderstorm, lowering light and increasing enemy spawn rate. Thunder is quieter.");
        this.buttonList.add(this.darkStormyNightmareButton);
    }

    private void addNoSkybasesButton(int x, int heightMultiplier, int baseColor, int activeColor) {
        this.noSkybasesButton = new GuiColoredButton(6, x, 6 * heightMultiplier, 100, 20, "No Skybases", baseColor, activeColor);
        this.noSkybasesButton.setTooltipText("Logs and planks have gravity");
        this.buttonList.add(this.noSkybasesButton);
    }

    private void addCancerModeButton(int x, int heightMultiplier, int baseColor, int activeColor) {
        this.cancerModeButton = new GuiColoredButton(7, x, 7 * heightMultiplier, 100, 20, "Cancer Mode", baseColor, activeColor);
        this.cancerModeButton.setTooltipText("Destroys the world generation algorithm, destroys the renderer, adds stupid features and flashing lights. The True April Fools experience");
        this.buttonList.add(this.cancerModeButton);
    }

    private void addHordeModeButton(int x, int heightMultiplier, int baseColor, int activeColor) {
        this.hordeModeButton = new GuiColoredButton(24, x,  1 * heightMultiplier, 100, 20, "Horde Mode", baseColor, activeColor);
        this.hordeModeButton.setTooltipText("All mobs have perfect awareness of your position. They will track and chase you from anywhere. They were just going easy on you before.");
        this.buttonList.add(this.hordeModeButton);
    }

    private void addTotalEclipseButton(int x, int heightMultiplier, int baseColor, int activeColor) {
        this.totalEclipseButton = new GuiColoredButton(8, x, 2 * heightMultiplier, 100, 20, "Total Eclipse", baseColor, activeColor);
        this.totalEclipseButton.setTooltipText("Every day is a Solar Eclipse");
        this.buttonList.add(this.totalEclipseButton);
    }

    private void addBloodmareButton(int x, int heightMultiplier, int baseColor, int activeColor) {
        this.bloodmareButton = new GuiColoredButton(9, x, 3 * heightMultiplier, 100, 20, "Bloodmare", baseColor, activeColor);
        this.bloodmareButton.setTooltipText("Every night is a Blood Moon");
        this.buttonList.add(this.bloodmareButton);
    }

    private void addMagicMonstersButton(int x, int heightMultiplier, int baseColor, int activeColor) {
        this.magicMonstersButton = new GuiColoredButton(10, x, 4 * heightMultiplier, 100, 20, "Magic Monsters", baseColor, activeColor);
        this.magicMonstersButton.setTooltipText("All mobs are replaced by witches");
        this.buttonList.add(this.magicMonstersButton);
    }

    private void addUnkillableMobsButton(int x, int heightMultiplier, int baseColor, int activeColor) {
        this.unkillableMobsButton = new GuiColoredButton(11, x, 5 * heightMultiplier, 100, 20, "Unkillable Mobs", baseColor, activeColor);
        this.unkillableMobsButton.setTooltipText("Mobs cannot take melee damage");
        this.buttonList.add(this.unkillableMobsButton);
    }

    private void addNoHitButton(int x, int heightMultiplier, int baseColor, int activeColor) {
        this.noHitButton = new GuiColoredButton(12, x, 6 * heightMultiplier, 100, 20, "NoHit", baseColor, activeColor);
        this.noHitButton.setTooltipText("One hit, and you're out");
        this.buttonList.add(this.noHitButton);
    }





    private void addDateTimerButton(int x, int heightMultiplier, int baseColor, int activeColor) {
        this.shouldShowDateTimerButton = new GuiColoredButton(14, x, heightMultiplier * 1, 100, 20, "Date Timer", baseColor, activeColor);
        this.shouldShowDateTimerButton.setTooltipText("Shows the Day/Night cycle and the number of days played on the HUD");
        this.buttonList.add(this.shouldShowDateTimerButton);
    }

    private void addRealTimerButton(int x, int heightMultiplier, int baseColor, int activeColor) {
        this.shouldShowRealTimerButton = new GuiColoredButton(15, x, heightMultiplier * 2, 100, 20, "Real Timer", baseColor, activeColor);
        this.shouldShowRealTimerButton.setTooltipText("Shows the in-game time spent playing");
        this.buttonList.add(this.shouldShowRealTimerButton);
    }

    private void addBloodmoonColorsButton(int x, int heightMultiplier, int baseColor, int activeColor) {
        this.bloodmoonColorsButton = new GuiColoredButton(16, x, heightMultiplier * 3, 100, 20, "Bloodmoon Colors", baseColor, activeColor);
        this.bloodmoonColorsButton.setTooltipText("Determines whether the screen should be tinted red during a Blood Moon. Advised to turn this off if you are a content creator");
        this.buttonList.add(this.bloodmoonColorsButton);
    }

    private void addCrimsonButton(int x, int heightMultiplier, int baseColor, int activeColor) {
        this.crimsonButton = new GuiColoredButton(17, x, heightMultiplier * 4, 100, 20, "Crimson", baseColor, activeColor);
        this.crimsonButton.setTooltipText("Everything is blood red! Purely visual");
        this.buttonList.add(this.crimsonButton);
    }

    private void addConfigOnHudButton( int x, int heightMultiplier, int baseColor, int activeColor) {
        this.configOnHudButton = new GuiColoredButton(18, x, heightMultiplier * 5, 100, 20, "Config on HUD", baseColor, activeColor);
        this.configOnHudButton.setTooltipText("Displays the active config modes on the HUD");
        this.buttonList.add(this.configOnHudButton);
    }

    private void addPotionParticlesButton(int x, int heightMultiplier, int baseColor, int activeColor) {
        this.potionParticlesButton = new GuiColoredButton(19, x, heightMultiplier * 6, 100, 20, "Potion Particles", baseColor, activeColor);
        this.potionParticlesButton.setTooltipText("Whether particles from potions should appear or not");
        this.buttonList.add(this.potionParticlesButton);
    }

    private void addFishingAlertsButton(int x, int heightMultiplier, int baseColor, int activeColor) {
        this.shouldDisplayFishingAnnouncementsButton = new GuiColoredButton(20, x, heightMultiplier * 1, 100, 20, "Fishing Alerts", baseColor, activeColor);
        this.shouldDisplayFishingAnnouncementsButton.setTooltipText("Whether rare drops obtained by fishing should display in chat");
        this.buttonList.add(this.shouldDisplayFishingAnnouncementsButton);
    }

    private void addCMRenderingButton( int x, int heightMultiplier, int baseColor, int activeColor) {
        this.aprilFoolsRenderingButton = new GuiColoredButton(21, x, heightMultiplier * 2, 100, 20, "CM Rendering", baseColor, activeColor);
        this.aprilFoolsRenderingButton.setTooltipText("Enables / disables the warped rendering effects of the April Fools patch. Recommended for streamers or people with sensitive eyes.");
        this.buttonList.add(this.aprilFoolsRenderingButton);
    }

    private void addPerfectStartButton(int x, int heightMultiplier, int baseColor, int activeColor) {
        this.perfectStartButton = new GuiColoredButton(22, x, heightMultiplier * 4, 100, 20, "Perfect Start", baseColor, activeColor);
        this.perfectStartButton.setTooltipText("Tired of resetting over and over on the first night? This option starts you off on day 2 with a brick oven and an axe. However, you start with only 6 shanks");
        this.buttonList.add(this.perfectStartButton);
    }
    private void addExtraArmorButton(int x, int heightMultiplier, int baseColor, int activeColor) {
        this.extraArmorButton = new GuiColoredButton(23, x, heightMultiplier * 5, 100, 20, "Extra Armor", baseColor, activeColor);
        this.extraArmorButton.setTooltipText("Starts you off with a cheap set of armor");
        this.buttonList.add(this.extraArmorButton);
    }


    private static String cap(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    @Override
    protected void actionPerformed(GuiButton par1GuiButton) {
        NightmareMode instance = NightmareMode.getInstance();

        if (par1GuiButton.id == 0) {
            this.mc.displayGuiScreen(this.parentGuiScreen);
        } else if (par1GuiButton.id == 1) {
            NightmareMode.moreVariants = !NightmareMode.moreVariants;
            instance.modifyConfigProperty("MoreVariants", Boolean.toString(NightmareMode.moreVariants));
            this.moreVariantsButton.updateState(NightmareMode.moreVariants);
        } else if (par1GuiButton.id == 2) {
            NightmareMode.evolvedMobs = !NightmareMode.evolvedMobs;
            instance.modifyConfigProperty("EvolvedMobs", Boolean.toString(NightmareMode.evolvedMobs));
            this.evolvedMobsButton.updateState(NightmareMode.evolvedMobs);
        } else if (par1GuiButton.id == 3) {
            NightmareMode.buffedSquids = !NightmareMode.buffedSquids;
            instance.modifyConfigProperty("BuffedSquids", Boolean.toString(NightmareMode.buffedSquids));
            this.buffedSquidsButton.updateState(NightmareMode.buffedSquids);
        } else if (par1GuiButton.id == 4) {
            NightmareMode.nite = !NightmareMode.nite;
            instance.modifyConfigProperty("NITE", Boolean.toString(NightmareMode.nite));
            this.NITEButton.updateState(NightmareMode.nite);
        } else if (par1GuiButton.id == 5) {
            NightmareMode.darkStormyNightmare = !NightmareMode.darkStormyNightmare;
            instance.modifyConfigProperty("DarkStormyNightmare", Boolean.toString(NightmareMode.darkStormyNightmare));
            this.darkStormyNightmareButton.updateState(NightmareMode.darkStormyNightmare);
        } else if (par1GuiButton.id == 6) {
            NightmareMode.noSkybases = !NightmareMode.noSkybases;
            instance.modifyConfigProperty("NoSkybases", Boolean.toString(NightmareMode.noSkybases));
            this.noSkybasesButton.updateState(NightmareMode.noSkybases);
        } else if (par1GuiButton.id == 7) {
            NightmareMode.isAprilFools = !NightmareMode.isAprilFools;
            instance.modifyConfigProperty("AprilFoolsPatch", Boolean.toString(NightmareMode.isAprilFools));
            this.cancerModeButton.updateState(NightmareMode.isAprilFools);
        } else if (par1GuiButton.id == 8) {
            NightmareMode.totalEclipse = !NightmareMode.totalEclipse;
            instance.modifyConfigProperty("TotalEclipse", Boolean.toString(NightmareMode.totalEclipse));
            this.totalEclipseButton.updateState(NightmareMode.totalEclipse);
        } else if (par1GuiButton.id == 9) {
            NightmareMode.bloodmare = !NightmareMode.bloodmare;
            instance.modifyConfigProperty("Bloodmare", Boolean.toString(NightmareMode.bloodmare));
            this.bloodmareButton.updateState(NightmareMode.bloodmare);
        } else if (par1GuiButton.id == 10) {
            NightmareMode.magicMonsters = !NightmareMode.magicMonsters;
            instance.modifyConfigProperty("MagicMonsters", Boolean.toString(NightmareMode.magicMonsters));
            this.magicMonstersButton.updateState(NightmareMode.magicMonsters);
        } else if (par1GuiButton.id == 11) {
            NightmareMode.unkillableMobs = !NightmareMode.unkillableMobs;
            instance.modifyConfigProperty("UnkillableMobs", Boolean.toString(NightmareMode.unkillableMobs));
            this.unkillableMobsButton.updateState(NightmareMode.unkillableMobs);
        } else if (par1GuiButton.id == 12) {
            NightmareMode.noHit = !NightmareMode.noHit;
            instance.modifyConfigProperty("NoHit", Boolean.toString(NightmareMode.noHit));
            this.noHitButton.updateState(NightmareMode.noHit);
        } else if (par1GuiButton.id == 13) {
            this.setButtonSettings(!this.bloodmareButton.drawButton);
            // sets the second page
        } else if (par1GuiButton.id == 14) {
            NightmareMode.shouldShowDateTimer = !NightmareMode.shouldShowDateTimer;
            instance.modifyConfigProperty("NmMinecraftDayTimer", Boolean.toString(NightmareMode.shouldShowDateTimer));
            this.shouldShowDateTimerButton.updateState(NightmareMode.shouldShowDateTimer);
        } else if (par1GuiButton.id == 15) {
            NightmareMode.shouldShowRealTimer = !NightmareMode.shouldShowRealTimer;
            instance.modifyConfigProperty("NmTimer", Boolean.toString(NightmareMode.shouldShowRealTimer));
            this.shouldShowRealTimerButton.updateState(NightmareMode.shouldShowRealTimer);
        } else if (par1GuiButton.id == 16) {
            NightmareMode.bloodmoonColors = !NightmareMode.bloodmoonColors;
            instance.modifyConfigProperty("BloodmoonColors", Boolean.toString(NightmareMode.bloodmoonColors));
            this.bloodmoonColorsButton.updateState(NightmareMode.bloodmoonColors);
        } else if (par1GuiButton.id == 17) {
            NightmareMode.crimson = !NightmareMode.crimson;
            instance.modifyConfigProperty("Crimson", Boolean.toString(NightmareMode.crimson));
            this.crimsonButton.updateState(NightmareMode.crimson);
        } else if (par1GuiButton.id == 18) {
            NightmareMode.configOnHud = !NightmareMode.configOnHud;
            instance.modifyConfigProperty("ConfigOnHUD", Boolean.toString(NightmareMode.configOnHud));
            this.configOnHudButton.updateState(NightmareMode.configOnHud);
        } else if (par1GuiButton.id == 19) {
            NightmareMode.potionParticles = !NightmareMode.potionParticles;
            instance.modifyConfigProperty("PotionParticles", Boolean.toString(NightmareMode.potionParticles));
            this.potionParticlesButton.updateState(NightmareMode.potionParticles);
        } else if (par1GuiButton.id == 20) {
            NightmareMode.shouldDisplayFishingAnnouncements = !NightmareMode.shouldDisplayFishingAnnouncements;
            instance.modifyConfigProperty("FishingAnnouncements", Boolean.toString(NightmareMode.shouldDisplayFishingAnnouncements));
            this.shouldDisplayFishingAnnouncementsButton.updateState(NightmareMode.shouldDisplayFishingAnnouncements);
        } else if (par1GuiButton.id == 21) {
            NightmareMode.aprilFoolsRendering = !NightmareMode.aprilFoolsRendering;
            instance.modifyConfigProperty("AprilFoolsWarpedRendering", Boolean.toString(NightmareMode.aprilFoolsRendering));
            this.aprilFoolsRenderingButton.updateState(NightmareMode.aprilFoolsRendering);
        } else if (par1GuiButton.id == 22) {
            NightmareMode.perfectStart = !NightmareMode.perfectStart;
            instance.modifyConfigProperty("PerfectStart", Boolean.toString(NightmareMode.perfectStart));
            this.perfectStartButton.updateState(NightmareMode.perfectStart);
        } else if (par1GuiButton.id == 23) {
            NightmareMode.extraArmor = !NightmareMode.extraArmor;
            instance.modifyConfigProperty("ExtraArmor", Boolean.toString(NightmareMode.extraArmor));
            this.extraArmorButton.updateState(NightmareMode.extraArmor);
        } else if (par1GuiButton.id == 24) {
            NightmareMode.hordeMode = !NightmareMode.hordeMode;
            instance.modifyConfigProperty("HordeMode", Boolean.toString(NightmareMode.hordeMode));
            this.hordeModeButton.updateState(NightmareMode.hordeMode);
        }
    }
}
