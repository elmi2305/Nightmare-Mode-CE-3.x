package com.itlesports.nightmaremode.mixin.gui;

import com.itlesports.nightmaremode.NightmareUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mixin(GuiGameOver.class)
public class GuiGameOverMixin extends GuiScreen {
    @Unique private boolean createClicked;
    @Unique private Random random = new Random();
    @Unique private static int TIP_CHAR_LIMIT = 60;
    @Unique private String tip = "";
    @Unique private String subTip = "";

    // Get tips from lang file
    @Unique
    private static String[] tips = new String[32];
    @Unique
    private static String[] lategameTips = new String[9];

    @Inject(method = "initGui", at = @At(value = "FIELD", target = "Lnet/minecraft/src/GuiGameOver;buttonList:Ljava/util/List;",ordinal = 4,shift = At.Shift.AFTER))
    private void addNewButton(CallbackInfo ci){
        if (!MinecraftServer.getIsServer()) {
            this.buttonList.add(new GuiButton(4, this.width / 2 - 100, this.height / 4 + 120, I18n.getString("gui.gameover.next_attempt")));
        }
    }

    @ModifyConstant(method = "initGui", constant = @Constant(longValue = 10800L))
    private long reduceTimeBeforeRerollingSpawnPoint(long constant){
        return 600L;
    }

    @Inject(method = "initGui", at = @At("HEAD"))
    private void declareChosenTip(CallbackInfo ci){
        this.selectRandomTip();
    }

    @Inject(method = "drawScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/GuiScreen;drawScreen(IIF)V"))
    private void drawTipText(int par1, int par2, float par3, CallbackInfo ci){
        int centerX = this.width / 2;
        int initialY = ((this.height / 4 + 120) + this.height) / 2;

        this.drawCenteredString(this.fontRenderer, I18n.getString("gui.gameover.tip_prefix"), centerX, initialY, 0x00FF00);
        this.drawCenteredString(this.fontRenderer, this.tip, centerX, initialY + 10, 0xFFFFFF);

        if (!this.subTip.isEmpty()) {
            this.drawCenteredString(this.fontRenderer, this.subTip, centerX, initialY + 20, 0xFFFFFF);
        }
    }

    @Unique private void selectRandomTip() {
        if (tips[0] == null) {
            // Only load once for performance
            for (int i = 0; i < tips.length; ++i) {
                tips[i] = I18n.getString("gui.gameover.tip" + (i+1));
            }
            for (int i = 0; i < lategameTips.length; ++i) {
                lategameTips[i] = I18n.getString("gui.gameover.lategame" + (i+1));
            }
        }

        String rawTip;

        if (NightmareUtils.getWorldProgress() < 1) {
            rawTip = tips[random.nextInt(tips.length)];
        } else {
            rawTip = lategameTips[random.nextInt(lategameTips.length)];
        }

        if (rawTip.length() > TIP_CHAR_LIMIT) {
            int breakPoint = rawTip.lastIndexOf(' ', TIP_CHAR_LIMIT);
            if (breakPoint == -1) breakPoint = TIP_CHAR_LIMIT;
            this.tip = rawTip.substring(0, breakPoint).trim();
            this.subTip = rawTip.substring(breakPoint).trim();
        } else {
            this.tip = rawTip;
            this.subTip = "";
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

            List saveList = null;
            try {
                saveList = var1.getSaveList();
            } catch (AnvilConverterException ignored) {}

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
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(input);

        long largest = Long.MIN_VALUE;
        int start = -1, end = -1;
        while (matcher.find()) {
            long num = Long.parseLong(matcher.group());
            if (num > largest) {
                largest = num;
                start = matcher.start();
                end = matcher.end();
            }
        }
        if (largest == Long.MIN_VALUE) {
            return input;
        }
        long incrementedValue = largest + (Long.signum(largest));
        StringBuilder updatedString = new StringBuilder(input);
        updatedString.replace(start, end, String.valueOf(incrementedValue));
        return updatedString.toString();
    }
}