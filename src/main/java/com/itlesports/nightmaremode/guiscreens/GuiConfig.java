package com.itlesports.nightmaremode.guiscreens;

import btw.BTWAddon;
import btw.community.nightmaremode.NightmareMode;
import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiScreen;

import java.util.HashMap;
import java.util.Map;

public class GuiConfig extends GuiScreen {
    private static Map<String, String> tempMap = new HashMap<>();


    private final GuiScreen parentGuiScreen;

    public GuiConfig(GuiScreen par1GuiScreen) {
        this.parentGuiScreen = par1GuiScreen;
    }

    @Override
    public void drawScreen(int par1, int par2, float par3) {
        this.drawDefaultBackground();

        String textToDisplay = "bro";
        this.drawCenteredString(this.fontRenderer, textToDisplay, this.width / 2, this.height / 2 - 35, 0xFFFFFF);
        textToDisplay = "you are about to join a world with cancer mode enabled";
        this.drawCenteredString(this.fontRenderer, textToDisplay, this.width / 2, this.height / 2 - 20, 0xFFFFFF);
        textToDisplay = "if this isn’t a backup or a throwaway world, turn back now";
        this.drawCenteredString(this.fontRenderer, textToDisplay, this.width / 2, this.height / 2 - 5, 0xFFFFFF);
        textToDisplay = "cause it WILL probably get destroyed";
        this.drawCenteredString(this.fontRenderer, textToDisplay, this.width / 2, this.height / 2 + 10, 0xFFFFFF);
        textToDisplay = "are you absolutely certain you want to do this? this is your only warning";
        this.drawCenteredString(this.fontRenderer, textToDisplay, this.width / 2, this.height / 2 + 40, 0xFFFFFF);

        super.drawScreen(par1, par2, par3);
    }

    @Override
    protected void actionPerformed(GuiButton par1GuiButton) {
        NightmareMode instance = NightmareMode.getInstance();
        tempMap = instance.configPropertyDefaults;
//        System.out.println(tempMap.keySet());

        if(par1GuiButton.id == 0){
            this.mc.displayGuiScreen(this.parentGuiScreen);
        } else if(par1GuiButton.id == 1){
            NightmareMode.bloodmare = !NightmareMode.bloodmare;
//            NightmareMode.setNeedUpdateConfig(true);
//            instance.updateConfigs("Bloodmare", Boolean.toString(NightmareMode.bloodmare));
//            NightmareMode.getInstance().registerProperty("Bloodmare", Boolean.toString(NightmareMode.bloodmare));
//            tempMap.put("Bloodmare", Boolean.toString(NightmareMode.bloodmare));
//            NightmareMode.getInstance().repopulateConfigFile(this.conf); // overwrites the config file to the default
//            instance.configProperties.remove("Bloodmare");
//            instance.configProperties.add("Bloodmare");
//            instance.configPropertyDefaults.put("Bloodmare", Boolean.toString(NightmareMode.bloodmare));
        }
    }

    @Override
    public void initGui() {
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, this.width / 2 - 155, this.height / 2 + 60, 150, 20, "Go back"));
        this.buttonList.add(new GuiButton(1, this.width / 2 - 155, 20, 150, 20, "Bloodmare"));
    }
}
