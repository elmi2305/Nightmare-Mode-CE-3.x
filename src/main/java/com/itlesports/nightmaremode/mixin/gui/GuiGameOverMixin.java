package com.itlesports.nightmaremode.mixin.gui;

import com.itlesports.nightmaremode.util.NMUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.itlesports.nightmaremode.util.NMFields.HARDMODE;

@Mixin(GuiGameOver.class)
public class GuiGameOverMixin extends GuiScreen {
    @Unique private boolean createClicked;
    @Unique private Random random = new Random();
    @Unique private static int TIP_CHAR_LIMIT = 60;
    @Unique private String tip = "";
    @Unique private String subTip = "";

    // Get tips from lang file
    @Unique private static String[] tips = new String[32];

    @Inject(method = "initGui", at = @At("HEAD"))
    private void declareChosenTipAndDeathMessage(CallbackInfo ci){
        this.selectRandomTip();
    }

    @ModifyArg(method = "drawScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/GuiGameOver;drawCenteredString(Lnet/minecraft/src/FontRenderer;Ljava/lang/String;III)V", ordinal = 0), index = 3)
    private int changeHeightOfYouDiedText(int par3){
        return par3 - 10;
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
            for (int i = 0; i < tips.length; ++i) {
                tips[i] = I18n.getString("gui.gameover.tip" + (i+1));
            }
        }

        String rawTip;

        rawTip = tips[random.nextInt(tips.length)];

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
//            this.mc.displayGuiScreen(null);
            if (this.createClicked) {
                return;
            }

            this.createClicked = true;
            long seed = new Random().nextLong(); // par4 is whether structures are enabled. forced on because attempting to capture it just doesn't work for some reason
            WorldSettings settings = new WorldSettings(seed, this.mc.theWorld.getWorldInfo().getGameType(), true, false, this.mc.theWorld.getWorldInfo().getTerrainType(), this.mc.theWorld.getWorldInfo().getDifficulty(),true);
            ISaveFormat var1 = this.mc.getSaveLoader();

            if(this.mc.theWorld.worldInfo.areCommandsAllowed() || this.mc.theWorld.getWorldInfo().getGameType() == EnumGameType.CREATIVE){
                settings.enableCommands();
            }

            List saveList = null;
            try {
                saveList = var1.getSaveList();
            } catch (AnvilConverterException ignored) {}

            saveList.sort(null);
            String mostRecentWorld = NMUtils.updateWorldName(((SaveFormatComparator) saveList.get(0)).getDisplayName());

            try {
                if (MinecraftServer.getServer() != null) {
                    MinecraftServer.getServer().stopServer();
                    this.mc.loadWorld(null);
                }
                this.mc.launchIntegratedServer(NMUtils.makeUseableName(mostRecentWorld, this.mc), mostRecentWorld.trim(), settings);
                this.mc.statFileWriter.readStat(StatList.createWorldStat, 1);
            } catch (Exception e) {
                ci.cancel();
            }
        }
    }

}