package com.itlesports.nightmaremode.nmgui;

import btw.community.nightmaremode.NightmareMode;
import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiScreen;

public class GuiConfig extends GuiScreen {

    private boolean isOnSecondPage = false;
    private GuiColoredButton moreVariantsButton;        // 1
    private GuiColoredButton evolvedMobsButton;         // 2
    private GuiColoredButton buffedSquidsButton;        // 3
    private GuiColoredButton cancerModeButton;          // 4
    private GuiColoredButton NITEButton;                // 5
    private GuiColoredButton noSkybasesButton;          // 6
    private GuiColoredButton totalEclipseButton;        // 7
    private GuiColoredButton bloodmareButton;           // 8
    private GuiColoredButton magicMonstersButton;       // 9
    private GuiColoredButton unkillableMobsButton;      // 10
    private GuiColoredButton noHitButton;               // 11

    private GuiColoredButton shouldShowDateTimer; // 13
    private GuiColoredButton shouldShowRealTimer; // 14
    private GuiColoredButton bloodmoonColors; // 15
    private GuiColoredButton crimson; // 16
    private GuiColoredButton configOnHud; // 17
    private GuiColoredButton potionParticles; // 18
    private GuiColoredButton shouldDisplayFishingAnnouncements; // 19
    private GuiColoredButton aprilFoolsRendering; // 20
    private GuiColoredButton perfectStart; // 21



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

        textToDisplay = cap(Boolean.toString(NightmareMode.moreVariants));

        int textIndex = 1;
//        int heightMultiplier = this.height / 10;
        int heightMultiplier = 25;
        this.drawCenteredString(this.fontRenderer, textToDisplay, width + 100 + this.fontRenderer.getStringWidth(textToDisplay), heightMultiplier * textIndex  + 7, (NightmareMode.moreVariants ? 0xFFFF00 : 0x8a8a00));
        textIndex ++;

        textToDisplay = cap(Boolean.toString(NightmareMode.evolvedMobs));
        this.drawCenteredString(this.fontRenderer, textToDisplay, width + 100 + this.fontRenderer.getStringWidth(textToDisplay), heightMultiplier * textIndex  + 7, (NightmareMode.evolvedMobs ? 0xFFFF00 : 0x8a8a00));
        textIndex ++;

        textToDisplay = cap(Boolean.toString(NightmareMode.buffedSquids));
        this.drawCenteredString(this.fontRenderer, textToDisplay, width + 100 + this.fontRenderer.getStringWidth(textToDisplay), heightMultiplier * textIndex  + 7, (NightmareMode.buffedSquids ? 0xFFFF00 : 0x8a8a00));
        textIndex ++;

        textToDisplay = cap(Boolean.toString(NightmareMode.isAprilFools));
        this.drawCenteredString(this.fontRenderer, textToDisplay, width + 100 + this.fontRenderer.getStringWidth(textToDisplay), heightMultiplier * textIndex  + 7, (NightmareMode.isAprilFools ? 0xff8800 : 0x9c5300)); // orange
        textIndex ++;

        textToDisplay = cap(Boolean.toString(NightmareMode.nite));
        this.drawCenteredString(this.fontRenderer, textToDisplay, width + 100 + this.fontRenderer.getStringWidth(textToDisplay), heightMultiplier * textIndex  + 7, (NightmareMode.nite ? 0xff8800 : 0x9c5300)); // orange
        textIndex ++;

        textToDisplay = cap(Boolean.toString(NightmareMode.noSkybases));
        this.drawCenteredString(this.fontRenderer, textToDisplay, width + 100 + this.fontRenderer.getStringWidth(textToDisplay), heightMultiplier * textIndex  + 7, (NightmareMode.noSkybases ? 0xff8800 : 0x9c5300)); // orange
        textIndex -= 5;
        width += 200;
//        width = this.width / 2;

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
//                    tempButton.drawTooltip(this.mc, tempButton.currentMouseX, tempButton.currentMouseY, tempButton.getTooltipText());
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
        textIndex ++;

        textIndex ++; // adds one button of gap between green and blue
        textToDisplay = cap(Boolean.toString(NightmareMode.perfectStart));
        this.drawCenteredString(this.fontRenderer, textToDisplay, width + 100 + this.fontRenderer.getStringWidth(textToDisplay), heightMultiplier * textIndex  + 7, (NightmareMode.perfectStart ? 0x5fe647 : 0x429c32));

        for(Object coloredButton: this.buttonList){
            if(coloredButton instanceof GuiColoredButton tempButton){
                if(tempButton.shouldDrawToolTip){
//                    tempButton.drawTooltip(this.mc, tempButton.currentMouseX, tempButton.currentMouseY, tempButton.getTooltipText());
                    tempButton.drawTooltip(this.mc, tempButton.xPosition, tempButton.yPosition, tempButton.width, tempButton.height, tempButton.getTooltipText());
                }
            }
        }
    }
    private void setButtonRendering(boolean par1){
        this.moreVariantsButton.drawButton = par1;
        this.evolvedMobsButton.drawButton = par1;
        this.buffedSquidsButton.drawButton = par1;
        this.cancerModeButton.drawButton = par1;
        this.NITEButton.drawButton = par1;
        this.noSkybasesButton.drawButton = par1;
        this.totalEclipseButton.drawButton = par1;
        this.bloodmareButton.drawButton = par1;
        this.magicMonstersButton.drawButton = par1;
        this.unkillableMobsButton.drawButton = par1;
        this.noHitButton.drawButton = par1;
        this.isOnSecondPage = !par1;

        this.shouldShowDateTimer.drawButton = !par1;
        this.shouldShowRealTimer.drawButton = !par1;
        this.bloodmoonColors.drawButton = !par1;
        this.crimson.drawButton = !par1;
        this.configOnHud.drawButton = !par1;
        this.potionParticles.drawButton = !par1;
        this.shouldDisplayFishingAnnouncements.drawButton = !par1;
        this.aprilFoolsRendering.drawButton = !par1;
        this.perfectStart.drawButton = !par1;
    }

    @Override
    public void initGui() {
        this.buttonList.clear();
        int buttonIndex = 1;
        int width = this.width / 8;
        this.buttonList.add(new GuiButton(0, width, this.height - 30, 100, 20, "Go back"));
        this.buttonList.add(new GuiButton(12, width + 200, this.height - 30, 100, 20, "Switch pages"));

        // add configs

        int easyBase = 0x8a8a00;
        int easyActive = 0xFFFF00;

        int mediumBase = 0x9c5300;
        int mediumActive = 0xff8800;

        int hardActive = 0xFF0000;
        int hardBase = 0x870101;

        int impossibleActive = 0xAAAAAA;
        int impossibleBase = 0x484848;

        int heightMultiplier = 25;

        this.moreVariantsButton = new GuiColoredButton(buttonIndex, width, heightMultiplier * buttonIndex, 100, 20, "More Variants", 0xFFFFFF, 0xd4d4d4, 0xFFFFFF, easyBase, easyActive);
        this.moreVariantsButton.setTooltipText("Adds various new variants to NM");
        this.buttonList.add(this.moreVariantsButton);
        buttonIndex++;

        this.evolvedMobsButton = new GuiColoredButton(buttonIndex, width, heightMultiplier * buttonIndex, 100, 20, "Evolved Mobs", 0xFFFFFF, 0xd4d4d4, 0xFFFFFF, easyBase, easyActive);
        this.evolvedMobsButton.setTooltipText("All mob variants can spawn, regardless of world progress");
        this.buttonList.add(this.evolvedMobsButton);
        buttonIndex++;

        this.buffedSquidsButton = new GuiColoredButton(buttonIndex, width, heightMultiplier * buttonIndex, 100, 20, "Buffed Squids", 0xFFFFFF, 0xd4d4d4, 0xFFFFFF, easyBase, easyActive);
        this.buffedSquidsButton.setTooltipText("Squids have doubled stats and can chase the player on land");
        this.buttonList.add(this.buffedSquidsButton);
        buttonIndex++;

        this.cancerModeButton = new GuiColoredButton(buttonIndex, width, heightMultiplier * buttonIndex, 100, 20, "Cancer Mode", 0xFFFFFF, 0xd4d4d4, 0xFFFFFF, mediumBase, mediumActive);
        this.cancerModeButton.setTooltipText("Destroys the world generation algorithm, destroys the renderer, adds stupid features and flashing lights. The True April Fools experience");
        this.buttonList.add(this.cancerModeButton);
        buttonIndex++;

        this.NITEButton = new GuiColoredButton(buttonIndex, width, heightMultiplier * buttonIndex, 100, 20, "NITE", 0xFFFFFF, 0xd4d4d4, 0xFFFFFF, mediumBase, mediumActive);
        this.NITEButton.setTooltipText("Nightmare Is Too Easy. Start with 3 hearts and shanks. Gain them back by levelling up. Mobs get stronger the longer you play. Raw food is safe to eat. Reduced hunger cost & movement penalties. Inspired by MITE");
        this.buttonList.add(this.NITEButton);
        buttonIndex++;

        this.noSkybasesButton = new GuiColoredButton(buttonIndex, width, heightMultiplier * buttonIndex, 100, 20, "No Skybases", 0xFFFFFF, 0xd4d4d4, 0xFFFFFF, mediumBase, mediumActive);
        this.noSkybasesButton.setTooltipText("Logs and planks have gravity");
        this.buttonList.add(this.noSkybasesButton);
        buttonIndex = 1;
        width += 200;

        this.totalEclipseButton = new GuiColoredButton(buttonIndex + 6, width, heightMultiplier * buttonIndex, 100, 20, "Total Eclipse", 0xFFFFFF, 0xd4d4d4, 0xFFFFFF, hardBase, hardActive);
        this.totalEclipseButton.setTooltipText("Every day is a Solar Eclipse");
        this.buttonList.add(this.totalEclipseButton);
        buttonIndex++;

        this.bloodmareButton = new GuiColoredButton(buttonIndex + 6, width, heightMultiplier * buttonIndex, 100, 20, "Bloodmare", 0xFFFFFF, 0xd4d4d4, 0xFFFFFF, hardBase, hardActive);
        this.bloodmareButton.setTooltipText("Every night is a Blood Moon");
        this.buttonList.add(this.bloodmareButton);
        buttonIndex++;

        this.magicMonstersButton = new GuiColoredButton(buttonIndex + 6, width, heightMultiplier * buttonIndex, 100, 20, "Magic Monsters", 0xFFFFFF, 0xd4d4d4, 0xFFFFFF, hardBase, hardActive);
        this.magicMonstersButton.setTooltipText("All mobs are replaced by witches");
        this.buttonList.add(this.magicMonstersButton);
        buttonIndex++;

        this.unkillableMobsButton = new GuiColoredButton(buttonIndex + 6, width, heightMultiplier * buttonIndex, 100, 20, "Unkillable Mobs", 0xFFFFFF, 0xd4d4d4, 0xFFFFFF, impossibleBase, impossibleActive);
        this.unkillableMobsButton.setTooltipText("Mobs cannot take melee damage");
        this.buttonList.add(this.unkillableMobsButton);
        buttonIndex++;

        this.noHitButton = new GuiColoredButton(buttonIndex + 6, width, heightMultiplier * buttonIndex, 100, 20, "NoHit", 0xFFFFFF, 0xd4d4d4, 0xFFFFFF, impossibleBase, impossibleActive);
        this.noHitButton.setTooltipText("One hit, and you're out");
        this.buttonList.add(this.noHitButton);



        // second page
        buttonIndex = 13;
        width -= 200;

        int miscBase = 0x264f91;
//        int miscBase = 0x2e60b0;
        int miscActive = 0x428BFF;
//        int miscActive = 0x4287f5;
        int goodBase = 0x429c32;
        int goodActive = 0x5fe647;

        this.shouldShowDateTimer = new GuiColoredButton(buttonIndex, width, heightMultiplier * (buttonIndex - 12), 100, 20, "Date Timer", 0xFFFFFF, 0xd4d4d4, 0xFFFFFF, miscBase, miscActive);
        this.shouldShowDateTimer.setTooltipText("Shows the Day/Night cycle and the number of days played on the HUD");
        this.buttonList.add(this.shouldShowDateTimer);
        buttonIndex++;

        this.shouldShowRealTimer = new GuiColoredButton(buttonIndex, width, heightMultiplier * (buttonIndex - 12), 100, 20, "Real Timer", 0xFFFFFF, 0xd4d4d4, 0xFFFFFF, miscBase, miscActive);
        this.shouldShowRealTimer.setTooltipText("Shows the in-game time spent playing");
        this.buttonList.add(this.shouldShowRealTimer);
        buttonIndex++;

        this.bloodmoonColors = new GuiColoredButton(buttonIndex, width, heightMultiplier * (buttonIndex - 12), 100, 20, "Bloodmoon Colors", 0xFFFFFF, 0xd4d4d4, 0xFFFFFF, miscBase, miscActive);
        this.bloodmoonColors.setTooltipText("Determines whether the screen should be tinted red during a Blood Moon. Advised to turn this off if you are a content creator");
        this.buttonList.add(this.bloodmoonColors);
        buttonIndex++;

        this.crimson = new GuiColoredButton(buttonIndex, width, heightMultiplier * (buttonIndex - 12), 100, 20, "Crimson", 0xFFFFFF, 0xd4d4d4, 0xFFFFFF, miscBase, miscActive);
        this.crimson.setTooltipText("Everything is blood red! Purely visual");
        this.buttonList.add(this.crimson);
        buttonIndex++;

        this.configOnHud = new GuiColoredButton(buttonIndex, width, heightMultiplier * (buttonIndex - 12), 100, 20, "Config on HUD", 0xFFFFFF, 0xd4d4d4, 0xFFFFFF, miscBase, miscActive);
        this.configOnHud.setTooltipText("Displays the active config modes on the HUD");
        this.buttonList.add(this.configOnHud);
        buttonIndex++;

        this.potionParticles = new GuiColoredButton(buttonIndex, width, heightMultiplier * (buttonIndex - 12), 100, 20, "Potion Particles", 0xFFFFFF, 0xd4d4d4, 0xFFFFFF, miscBase, miscActive);
        this.potionParticles.setTooltipText("Whether particles from potions should appear or not");
        this.buttonList.add(this.potionParticles);
        buttonIndex = 13;
        width += 200;

        this.shouldDisplayFishingAnnouncements = new GuiColoredButton(buttonIndex + 6, width, heightMultiplier * (buttonIndex - 12), 100, 20, "Fishing Alerts", 0xFFFFFF, 0xd4d4d4, 0xFFFFFF, miscBase, miscActive);
        this.shouldDisplayFishingAnnouncements.setTooltipText("Whether rare drops obtained by fishing should display in chat");
        this.buttonList.add(this.shouldDisplayFishingAnnouncements);
        buttonIndex++;

        this.aprilFoolsRendering = new GuiColoredButton(buttonIndex + 6, width, heightMultiplier * (buttonIndex - 12), 100, 20, "CM Rendering", 0xFFFFFF, 0xd4d4d4, 0xFFFFFF, miscBase, miscActive);
        this.aprilFoolsRendering.setTooltipText("Enables / disables the warped rendering effects of the April Fools patch. Recommended for streamers or people with sensitive eyes.");
        this.buttonList.add(this.aprilFoolsRendering);

        buttonIndex++;
        buttonIndex++;

        this.perfectStart = new GuiColoredButton(buttonIndex + 6 - 1, width, heightMultiplier * (buttonIndex - 12), 100, 20, "Perfect Start", 0xFFFFFF, 0xd4d4d4, 0xFFFFFF, goodBase, goodActive);
        this.perfectStart.setTooltipText("Tired of resetting over and over on the first night? This option starts you off on day 2 with a brick oven and an axe. However, you start with only 6 shanks");
        this.buttonList.add(this.perfectStart);
        this.shouldShowDateTimer.drawButton = this.isOnSecondPage;
        this.shouldShowRealTimer.drawButton = this.isOnSecondPage;
        this.bloodmoonColors.drawButton = this.isOnSecondPage;
        this.crimson.drawButton = this.isOnSecondPage;
        this.configOnHud.drawButton = this.isOnSecondPage;
        this.potionParticles.drawButton = this.isOnSecondPage;
        this.shouldDisplayFishingAnnouncements.drawButton = this.isOnSecondPage;
        this.aprilFoolsRendering.drawButton = this.isOnSecondPage;
        this.perfectStart.drawButton = this.isOnSecondPage;



        this.moreVariantsButton.drawButton = !this.isOnSecondPage;
        this.evolvedMobsButton.drawButton = !this.isOnSecondPage;
        this.buffedSquidsButton.drawButton = !this.isOnSecondPage;
        this.cancerModeButton.drawButton = !this.isOnSecondPage;
        this.NITEButton.drawButton = !this.isOnSecondPage;
        this.noSkybasesButton.drawButton = !this.isOnSecondPage;
        this.totalEclipseButton.drawButton = !this.isOnSecondPage;
        this.bloodmareButton.drawButton = !this.isOnSecondPage;
        this.magicMonstersButton.drawButton = !this.isOnSecondPage;
        this.unkillableMobsButton.drawButton = !this.isOnSecondPage;
        this.noHitButton.drawButton = !this.isOnSecondPage;

        this.moreVariantsButton.updateState(NightmareMode.moreVariants);
        this.evolvedMobsButton.updateState(NightmareMode.evolvedMobs);
        this.buffedSquidsButton.updateState(NightmareMode.buffedSquids);
        this.cancerModeButton.updateState(NightmareMode.isAprilFools);
        this.NITEButton.updateState(NightmareMode.nite);
        this.noSkybasesButton.updateState(NightmareMode.noSkybases);
        this.totalEclipseButton.updateState(NightmareMode.totalEclipse);
        this.bloodmareButton.updateState(NightmareMode.bloodmare);
        this.magicMonstersButton.updateState(NightmareMode.magicMonsters);
        this.unkillableMobsButton.updateState(NightmareMode.unkillableMobs);
        this.noHitButton.updateState(NightmareMode.noHit);
        this.shouldShowDateTimer.updateState(NightmareMode.shouldShowDateTimer);
        this.shouldShowRealTimer.updateState(NightmareMode.shouldShowRealTimer);
        this.bloodmoonColors.updateState(NightmareMode.bloodmoonColors);
        this.crimson.updateState(NightmareMode.crimson);
        this.configOnHud.updateState(NightmareMode.configOnHud);
        this.potionParticles.updateState(NightmareMode.potionParticles);
        this.shouldDisplayFishingAnnouncements.updateState(NightmareMode.shouldDisplayFishingAnnouncements);
        this.aprilFoolsRendering.updateState(NightmareMode.aprilFoolsRendering);
        this.perfectStart.updateState(NightmareMode.perfectStart);


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
            NightmareMode.isAprilFools = !NightmareMode.isAprilFools;
            instance.modifyConfigProperty("AprilFoolsPatch", Boolean.toString(NightmareMode.isAprilFools));
            this.cancerModeButton.updateState(NightmareMode.isAprilFools);
        } else if (par1GuiButton.id == 5) {
            NightmareMode.nite = !NightmareMode.nite;
            instance.modifyConfigProperty("NITE", Boolean.toString(NightmareMode.nite));
            this.NITEButton.updateState(NightmareMode.nite);
        } else if (par1GuiButton.id == 6) {
            NightmareMode.noSkybases = !NightmareMode.noSkybases;
            instance.modifyConfigProperty("NoSkybases", Boolean.toString(NightmareMode.noSkybases));
            this.noSkybasesButton.updateState(NightmareMode.noSkybases);
        } else if (par1GuiButton.id == 7) {
            NightmareMode.totalEclipse = !NightmareMode.totalEclipse;
            instance.modifyConfigProperty("TotalEclipse", Boolean.toString(NightmareMode.totalEclipse));
            this.totalEclipseButton.updateState(NightmareMode.totalEclipse);
        } else if (par1GuiButton.id == 8) {
            NightmareMode.bloodmare = !NightmareMode.bloodmare;
            instance.modifyConfigProperty("Bloodmare", Boolean.toString(NightmareMode.bloodmare));
            this.bloodmareButton.updateState(NightmareMode.bloodmare);
        } else if (par1GuiButton.id == 9) {
            NightmareMode.magicMonsters = !NightmareMode.magicMonsters;
            instance.modifyConfigProperty("MagicMonsters", Boolean.toString(NightmareMode.magicMonsters));
            this.magicMonstersButton.updateState(NightmareMode.magicMonsters);
        } else if (par1GuiButton.id == 10) {
            NightmareMode.unkillableMobs = !NightmareMode.unkillableMobs;
            instance.modifyConfigProperty("UnkillableMobs", Boolean.toString(NightmareMode.unkillableMobs));
            this.unkillableMobsButton.updateState(NightmareMode.unkillableMobs);
        } else if (par1GuiButton.id == 11) {
            NightmareMode.noHit = !NightmareMode.noHit;
            instance.modifyConfigProperty("NoHit", Boolean.toString(NightmareMode.noHit));
            this.noHitButton.updateState(NightmareMode.noHit);
        } else if (par1GuiButton.id == 12) {
            this.setButtonRendering(!this.bloodmareButton.drawButton);
        } else if (par1GuiButton.id == 13) {
            NightmareMode.shouldShowDateTimer = !NightmareMode.shouldShowDateTimer;
            instance.modifyConfigProperty("NmMinecraftDayTimer", Boolean.toString(NightmareMode.shouldShowDateTimer));
            this.shouldShowDateTimer.updateState(NightmareMode.shouldShowDateTimer);
        } else if (par1GuiButton.id == 14) {
            NightmareMode.shouldShowRealTimer = !NightmareMode.shouldShowRealTimer;
            instance.modifyConfigProperty("NmTimer", Boolean.toString(NightmareMode.shouldShowRealTimer));
            this.shouldShowRealTimer.updateState(NightmareMode.shouldShowRealTimer);
        } else if (par1GuiButton.id == 15) {
            NightmareMode.bloodmoonColors = !NightmareMode.bloodmoonColors;
            instance.modifyConfigProperty("BloodmoonColors", Boolean.toString(NightmareMode.bloodmoonColors));
            this.bloodmoonColors.updateState(NightmareMode.bloodmoonColors);
        } else if (par1GuiButton.id == 16) {
            NightmareMode.crimson = !NightmareMode.crimson;
            instance.modifyConfigProperty("Crimson", Boolean.toString(NightmareMode.crimson));
            this.crimson.updateState(NightmareMode.crimson);
        } else if (par1GuiButton.id == 17) {
            NightmareMode.configOnHud = !NightmareMode.configOnHud;
            instance.modifyConfigProperty("ConfigOnHUD", Boolean.toString(NightmareMode.configOnHud));
            this.configOnHud.updateState(NightmareMode.configOnHud);
        } else if (par1GuiButton.id == 18) {
            NightmareMode.potionParticles = !NightmareMode.potionParticles;
            instance.modifyConfigProperty("PotionParticles", Boolean.toString(NightmareMode.potionParticles));
            this.potionParticles.updateState(NightmareMode.potionParticles);
        } else if (par1GuiButton.id == 19) {
            NightmareMode.shouldDisplayFishingAnnouncements = !NightmareMode.shouldDisplayFishingAnnouncements;
            instance.modifyConfigProperty("FishingAnnouncements", Boolean.toString(NightmareMode.shouldDisplayFishingAnnouncements));
            this.shouldDisplayFishingAnnouncements.updateState(NightmareMode.shouldDisplayFishingAnnouncements);
        } else if (par1GuiButton.id == 20) {
            NightmareMode.aprilFoolsRendering = !NightmareMode.aprilFoolsRendering;
            instance.modifyConfigProperty("AprilFoolsWarpedRendering", Boolean.toString(NightmareMode.aprilFoolsRendering));
            this.aprilFoolsRendering.updateState(NightmareMode.aprilFoolsRendering);
        } else if (par1GuiButton.id == 21) {
            NightmareMode.perfectStart = !NightmareMode.perfectStart;
            instance.modifyConfigProperty("PerfectStart", Boolean.toString(NightmareMode.perfectStart));
            this.perfectStart.updateState(NightmareMode.perfectStart);
        }

    }
}
