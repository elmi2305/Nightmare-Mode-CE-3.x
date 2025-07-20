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
    @Unique private static final List<String> tips = new ArrayList<>();
    @Unique private static final List<String> lategameTips = new ArrayList<>();
    @Unique private String tip = "";
    @Unique private String subTip = "";
    @Unique private static int TIP_CHAR_LIMIT = 60;


    @Inject(method = "initGui", at = @At(value = "FIELD", target = "Lnet/minecraft/src/GuiGameOver;buttonList:Ljava/util/List;",ordinal = 4,shift = At.Shift.AFTER))
    private void addNewButton(CallbackInfo ci){
        if (!MinecraftServer.getIsServer()) {
            this.buttonList.add(new GuiButton(4, this.width / 2 - 100, this.height / 4 + 120, "Next Attempt"));
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

        this.drawCenteredString(this.fontRenderer, "Tip:", centerX, initialY, 0x00FF00);
        this.drawCenteredString(this.fontRenderer, this.tip, centerX, initialY + 10, 0xFFFFFF);

        if (!this.subTip.isEmpty()) {
            this.drawCenteredString(this.fontRenderer, this.subTip, centerX, initialY + 20, 0xFFFFFF);
        }
    }



    @Unique private void selectRandomTip() {
        String rawTip;

        if (NightmareUtils.getWorldProgress() < 1) {
            rawTip = tips.get(random.nextInt(tips.size()));
        } else {
            rawTip = lategameTips.get(random.nextInt(lategameTips.size()));
        }

        if (rawTip.length() > TIP_CHAR_LIMIT) {
            // Split at the nearest space before the limit to avoid mid-word breaks
            int breakPoint = rawTip.lastIndexOf(' ', TIP_CHAR_LIMIT);
            if (breakPoint == -1) breakPoint = TIP_CHAR_LIMIT; // fallback if no space

            this.tip = rawTip.substring(0, breakPoint).trim();
            this.subTip = rawTip.substring(breakPoint).trim();
        } else {
            this.tip = rawTip;
            this.subTip = "";
        }
    }


    static {
        tips.add("Stay away from the ocean!");
        tips.add("Squids become permanently hostile in Hardmode.");
        tips.add("Squids can be killed for calamari, the best food item.");
        tips.add("Punch mobs off cliffs for an easy kill.");
        tips.add("You can sprint-punch mobs to deal additional knockback.");
        tips.add("Snowballs are great for hunting.");
        tips.add("The Bonus Chest gives you a stone sword. Sometimes that’s all you need.");
        tips.add("Mobs with iron tools have more range than you do.");
        tips.add("Creepers will explode quickly if caught on fire! Beware of chain reactions!");
        tips.add("Lit Brick Ovens will set fire to flammable blocks in front of them.");
        tips.add("Slowness will prevent you from jumping, sprinting, and climbing ladders.");
        tips.add("Mobs grow stronger as you progress. Don't underestimate them!");
        tips.add("Mountains and high points make for great shelter.");
        tips.add("Jungle Trees can be climbed for a quick and easy shelter on early nights.");
        tips.add("Mobs will not spawn for the first 90 seconds… make use of it!");
        tips.add("You can parry squid tentacles by blocking with your sword!");
        tips.add("Iron and Diamond pickaxes can give additional Iron and Gold from ore veins.");
        tips.add("Don't use tools as weapons - it uses twice the durability!");
        tips.add("Lit campfires can ignite adjacent campfires or brick ovens! Use your hunger wisely!");
        tips.add("Mad? Go yell at the developer on the NM Discord server. It's linked on the github release.");
        tips.add("Mob variants are very dangerous! Avoid fighting them unless you know what they do.");
        tips.add("Use Creeper explosions to get free dirt and stone!");
        tips.add("Fishing during a Full Moon is a great source of food.");
        tips.add("Always keep some torches with you if you get caught in the darkness!");
        tips.add("Bring lots of blocks when you go caving. It lets you block off dangerous paths.");
        tips.add("Think twice about getting iron armor! Heavy armor increases your hunger drain.");
        tips.add("Sometimes the best thing to do is to turn around and go home. Don't take unnecessary risks!");
        tips.add("Shallow water pools make for great bases! Mobs cannot spawn in the water.");
        tips.add("You can get a brick oven on the first day if you place clay down fast enough! Risky, but rewarding.");
        tips.add("Place campfires on 2 block tall pillars! It prevents them from burning the floor they are on.");
        tips.add("It's better to cave for diamonds than to strip-mine.");
        tips.add("Craft Bandages with wool and string. They can save your life!");
//        tips.add(".");
//        tips.add(".");




        lategameTips.add("It's important to take breaks. Come back stronger.");
        lategameTips.add("You got further than most. Be proud of that.");
        lategameTips.add("Progress is never wasted. Every death teaches you something.");
        lategameTips.add("You’ve come so far. You can go even further.");
        lategameTips.add("Don’t let one death define your journey.");
        lategameTips.add("Every time you die, you come back stronger.");
        lategameTips.add("You’ll remember what went wrong, and that’s how you’ll survive the next time.");
        lategameTips.add("Don't give up.");
        lategameTips.add("You'll be fine, just take a little break.");
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
