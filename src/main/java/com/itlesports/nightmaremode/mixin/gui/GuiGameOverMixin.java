package com.itlesports.nightmaremode.mixin.gui;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mixin(GuiGameOver.class)
public class GuiGameOverMixin extends GuiScreen {
    @Unique
    private boolean createClicked;

    @Inject(method = "initGui", at = @At(value = "FIELD", target = "Lnet/minecraft/src/GuiGameOver;buttonList:Ljava/util/List;",ordinal = 4,shift = At.Shift.AFTER))
    private void addNewButton(CallbackInfo ci){
        if (!MinecraftServer.getIsServer()) {
            this.buttonList.add(new GuiButton(4, this.width / 2 - 100, this.height / 4 + 120, I18n.getString("deathScreen.nextAttempt")));
        }
    }


    @Inject(method = "actionPerformed", at = @At("TAIL"), cancellable = true)
    private void manageExtraButton(GuiButton par1GuiButton, CallbackInfo ci){
        if(par1GuiButton.id == 4){
            this.mc.displayGuiScreen(null);
            if (this.createClicked) {
                return;
            }

            this.createClicked = true;
            long seed = new Random().nextLong();
            WorldSettings settings = new WorldSettings(seed, this.mc.theWorld.getWorldInfo().getGameType(), this.mc.theWorld.getWorldInfo().isMapFeaturesEnabled(), false, this.mc.theWorld.getWorldInfo().getTerrainType(), this.mc.theWorld.getWorldInfo().getDifficulty(),true);
            ISaveFormat var1 = this.mc.getSaveLoader();

            if(this.mc.theWorld.worldInfo.areCommandsAllowed() || this.mc.theWorld.getWorldInfo().getGameType() == EnumGameType.CREATIVE){
                settings.enableCommands();
            }
            // TODO: no way to detect whether bonus chest was enabled. bonus chest defaults to off


            List saveList = null;
            try {
                saveList = var1.getSaveList();
            } catch (AnvilConverterException ignored) {
            }

            saveList.sort(null);
            String mostRecentWorld = updateWorldName(((SaveFormatComparator) saveList.get(0)).getDisplayName());

            try {
                if (MinecraftServer.getServer() != null) {
                    MinecraftServer.getServer().stopServer();
                    this.mc.loadWorld(null);
                }

                this.mc.launchIntegratedServer(this.makeUseableName(mostRecentWorld), mostRecentWorld.trim(), settings);
                this.mc.statFileWriter.readStat(StatList.createWorldStat, 1);
            } catch (Exception e) {
                ci.cancel();
            }
        }
    }

    @Unique
    private String makeUseableName(String worldName) {
        String trim = worldName.trim();
        for (char var4 : ChatAllowedCharacters.allowedCharactersArray) {
            trim = trim.replace(var4, '_');
        }
        if (MathHelper.stringNullOrLengthZero(trim)) {
            trim = "World";
        }
        trim = GuiCreateWorld.func_73913_a(this.mc.getSaveLoader(), trim);
        return trim;
    }

    @Unique
    private static String updateWorldName(String input) {
        // Regular expression to find numbers in the string
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(input);

        long largest = Long.MIN_VALUE;
        int start = -1, end = -1;

        // Find the largest number and its position in the string
        while (matcher.find()) {
            long num = Long.parseLong(matcher.group());
            if (num > largest) {
                largest = num;
                start = matcher.start();
                end = matcher.end();
            }
        }

        // If no number is found, return the original string
        if (largest == Long.MIN_VALUE) {
            return input;
        }

        // Increment the largest number
        long incrementedValue = largest + (Long.signum(largest));

        // Replace the largest number with the incremented value
        StringBuilder updatedString = new StringBuilder(input);
        updatedString.replace(start, end, String.valueOf(incrementedValue));

        return updatedString.toString();
    }
}
