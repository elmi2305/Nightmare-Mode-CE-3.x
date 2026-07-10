package com.itlesports.nightmaremode.mixin.gui;

import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.util.NMUtils;
import com.itlesports.nightmaremode.nmgui.GuiColoredButton;
import com.itlesports.nightmaremode.nmgui.GuiConfig;
import com.itlesports.nightmaremode.nmgui.GuiWarning;
import com.itlesports.nightmaremode.util.interfaces.GuiSelectWorldExt;
import com.itlesports.nightmaremode.util.interfaces.GuiWorldSlotExt;
import net.minecraft.src.*;
import org.lwjgl.Sys;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Mixin(GuiSelectWorld.class)
public abstract class GuiSelectWorldMixin extends GuiScreen implements GuiSelectWorldExt {

    @Unique private Set<String> favoritedWorlds = new HashSet<>(); // accessed by the save loader. should never be null
    @Unique private int lastMouseX; // since hitboxes are just hardcoded screen regions
    @Unique private int lastMouseY; // since hitboxes are just hardcoded screen regions

    @Shadow private List saveList;
    @Shadow private int selectedWorld;
    @Shadow private GuiWorldSlot worldSlotContainer;
    @Shadow protected abstract void loadSaves() throws AnvilConverterException;
    @Shadow public abstract void initGui();
    @Shadow private boolean selected;

    @Shadow
    protected abstract String getSaveName(int par1);

    @Unique private static int num = 0;
    @Unique private static boolean chaos = false;


    @Inject(method = "actionPerformed", at = @At("TAIL"))
    private void squidButton(GuiButton par1GuiButton, CallbackInfo ci) {
        if (NightmareMode.isAprilFools) {
            if (par1GuiButton.id == 10) {
                num += 1;
                NMUtils.setBuffedSquidBonus(roundIfNeeded(1 + num * 0.013));
            }
            if(par1GuiButton.id == 11){
                chaos = !chaos;
                NMUtils.setIntenseCorruption(chaos);
            }
        }
        if(par1GuiButton.id == 2305){
            this.mc.displayGuiScreen(new GuiConfig(this));
        }
    }

    @Inject(method = "selectWorld", at = @At("HEAD"),cancellable = true)
    private void manageWarning(int par1, CallbackInfo ci){
        if(NightmareMode.isAprilFools){
            if (!GuiWarning.hasPlayerAgreed()) {
                GuiWarning screen = new GuiWarning(this);
                this.mc.displayGuiScreen(screen);
                ci.cancel();
            }
        }else if(NightmareMode.getInstance().wasConfigModified){
            if (!GuiWarning.hasPlayerAgreed()) {
                GuiWarning screen = new GuiWarning(this);
                screen.setLine1(I18n.getString("gui.selectworld.warning_title"));
                screen.setLine2(I18n.getString("gui.selectworld.warning_recommend_restart"));
                screen.setLine3(I18n.getString("gui.selectworld.warning_config_crash"));
                screen.setLine4(I18n.getString("gui.selectworld.warning_continue"));
                screen.setLine5(I18n.getString("gui.selectworld.warning_final"));
                this.mc.displayGuiScreen(screen);
                ci.cancel();
            }
        }
    }

    @Unique
    private static double roundIfNeeded(double value) {
        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.stripTrailingZeros();
        int scale = Math.max(0, bd.scale());
        if (scale > 3) {
            bd = bd.setScale(3, RoundingMode.HALF_UP);
        }
        return bd.doubleValue();
    }
    @Redirect(method = "initGui", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/GuiSelectWorld;loadSaves()V"))
    private void loadSavesImproved (GuiSelectWorld instance) throws AnvilConverterException {
        loadFavoritedWorlds();

        ISaveFormat var1 = this.mc.getSaveLoader();
        this.saveList = var1.getSaveList();
        // pretty much how the vanilla one does it, just sorts favorited ones first
        this.saveList.sort((Comparator<SaveFormatComparator>) (a, b) -> {
            boolean aFav = favoritedWorlds.contains(a.getFileName());
            boolean bFav = favoritedWorlds.contains(b.getFileName());

            if (aFav && !bFav) {
                return -1;
            }
            if (!aFav && bFav) {
                return 1;
            }

            return a.compareTo(b);
        });

        this.selectedWorld = -1;
    }
    @Inject(method = "drawScreen", at = @At("HEAD"))
    private void trackMousePos(int par1, int par2, float par3, CallbackInfo ci){
        this.lastMouseX = par1;
        this.lastMouseY = par2;
    }
    @Inject(method = "actionPerformed", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/GuiSelectWorld;getDeleteWorldScreen(Lnet/minecraft/src/GuiScreen;Ljava/lang/String;I)Lnet/minecraft/src/GuiYesNo;"))
    private void removeFromFavoritesWhenPressedDelete(GuiButton par1GuiButton, CallbackInfo ci){
        String var2 = this.getSaveName(this.selectedWorld);
        this.favoritedWorlds.remove(var2);
        this.validateTextFile();
        // this removes the world name from the favorites list when the delete button is clicked REGARDLESS of whether the user presses Y/N
        // technically, this should only trigger when the user selects Yes, but it doesn't matter
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
        if (button == 0) {
            int starClickedIndex = ((GuiWorldSlotExt)(this.worldSlotContainer)).nightmareMode$getStarClicked(mouseX, mouseY);
            if (starClickedIndex >= 0 && starClickedIndex < this.saveList.size()) {
                String filename = ((SaveFormatComparator)this.saveList.get(starClickedIndex)).getFileName();
                toggleFavorite(filename);
                return;
            }


            int folderClicked = ((GuiWorldSlotExt)(this.worldSlotContainer)).nightmareMode$getFolderClicked(mouseX, mouseY);
            if(folderClicked >= 0 && folderClicked < this.saveList.size()) {
                String filename = ((SaveFormatComparator)this.saveList.get(folderClicked)).getFileName();

                openFolder(filename);

            }
        }

        super.mouseClicked(mouseX, mouseY, button);
    }
    @Unique
    private void openFolder(String filename) {
        File path = new File(Minecraft.getMinecraft().mcDataDir, "saves/" + filename);
        String absolutePath = path.getAbsolutePath();
        this.mc.getLogAgent().logInfo("Opening world folder");
        if (Util.getOSType() == EnumOS.MACOS) {
            try {
                this.mc.getLogAgent().logInfo(absolutePath);
                Runtime.getRuntime().exec(new String[]{"/usr/bin/open", absolutePath});
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (Util.getOSType() == EnumOS.WINDOWS) {
            String var4 = String.format("cmd.exe /C start \"Open file\" \"%s\"", absolutePath);
            try {
                Runtime.getRuntime().exec(var4);
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Sys.openURL("file://" + absolutePath);
    }
    @Override
    public int nightmareMode$getLastMouseX() {
        return this.lastMouseX;
    }

    @Override
    public int nightmareMode$getLastMouseY() {
        return this.lastMouseY;
    }
    @Override
    public boolean nightmareMode$isFavorited(String filename) {
        return favoritedWorlds.contains(filename);
    }

    @Unique
    private void toggleFavorite(String filename) {
        if (favoritedWorlds.contains(filename)) {
            favoritedWorlds.remove(filename);
        } else {
            favoritedWorlds.add(filename);
        }

        validateTextFile();
        int selectedWorld = this.selectedWorld;
        boolean selected = this.selected;

        try {
            loadSaves();
        } catch (AnvilConverterException e) {
            e.printStackTrace();
        }

        this.mc.displayGuiScreen(null);
        this.mc.displayGuiScreen(this);
//        this.selected = selected;
//        this.selectedWorld = selectedWorld;
//
//        ((GuiWorldSlotAccessor)(this.worldSlotContainer)).invokeElementClicked(this.selectedWorld, false);
    }

    @Unique
    private File getFavoritesFile() {
        return new File(this.mc.mcDataDir, "nmfavoritedworlds.txt");
    }

    @Unique
    private void validateTextFile() {
        File file = getFavoritesFile();

        try {
            // create file if missing
            if (!file.exists()) {
                file.createNewFile();
            }

            // rewrite file to match current favorites
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, false));
            for (String fav : favoritedWorlds) {
                writer.write(fav);
                writer.newLine();
            }
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Unique
    private void loadFavoritedWorlds() {
        File file = getFavoritesFile();

        favoritedWorlds.clear();

        try {
            // nothing to load
            if (!file.exists()) {
                return;
            }

            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;

            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    favoritedWorlds.add(line.trim());
                }
            }

            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
