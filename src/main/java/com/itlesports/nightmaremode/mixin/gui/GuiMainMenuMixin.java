package com.itlesports.nightmaremode.mixin.gui;

import btw.community.nightmaremode.NightmareMode;
import net.minecraft.src.GuiMainMenu;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.*;

@Mixin(GuiMainMenu.class)
public class GuiMainMenuMixin extends GuiScreen {
    @Shadow private String splashText;
    @Shadow @Final private static Random rand;
    @Unique private static ResourceLocation MENU = null;

    @Unique private final ResourceLocation BLOODMARE_CLEAN = new ResourceLocation("textures/NightmareModeBloodmareClean.png");
    @Unique private final ResourceLocation BLOODMARE = new ResourceLocation("textures/NightmareModeBloodmare.png");
    @Unique private final ResourceLocation NIGHTMARE_MODE = new ResourceLocation("textures/NightmareMode.png");
    @Unique private final ResourceLocation CANCER_MODE = new ResourceLocation("textures/CancerMode.png");
    @Unique private final ResourceLocation BTW_CANCER = new ResourceLocation("textures/BTWCancer.png");
    @Unique private final ResourceLocation NIGHTMARE_MODE_RED = new ResourceLocation("textures/NightmareModeRed.png");
    @Unique private final ResourceLocation NIGHTMARE_MODE_DARK = new ResourceLocation("textures/NightmareModeDark.png");
    @Unique private final ResourceLocation NIGHTMARE_MODE_GREEN = new ResourceLocation("textures/NightmareModeGreen.png");
    @Unique private final ResourceLocation NIGHTMARE_MODE_BLUE = new ResourceLocation("textures/NightmareModeBlue.png");
    @Unique private final ResourceLocation NIGHTMARE_MODE_MISSING = new ResourceLocation("textures/NightmareModeMissing.png");
    @Unique private final ResourceLocation NIGHTMARE_MODE_PURPLE = new ResourceLocation("textures/NightmareModeMissing.png");
    @Unique private final List<ResourceLocation> logoList = Arrays.asList(
            NIGHTMARE_MODE_RED,
            NIGHTMARE_MODE_DARK,
            NIGHTMARE_MODE_GREEN,
            NIGHTMARE_MODE_BLUE,
            NIGHTMARE_MODE_MISSING,
            NIGHTMARE_MODE_PURPLE
    );

    @Inject(method = "initGui", at = @At("TAIL"))
    private void manageSplashText(CallbackInfo ci){
        this.splashText = getQuotes().get(rand.nextInt(getQuotes().size()));
        if (MENU == null) {
            MENU = NightmareMode.isAprilFools ? CANCER_MODE : (NightmareMode.bloodmare ? (rand.nextInt(64) == 0 ? BLOODMARE : BLOODMARE_CLEAN) : (rand.nextInt(100000) == 0 ? logoList.get(rand.nextInt(logoList.size())) : NIGHTMARE_MODE));
        }
    }

    @ModifyArg(method = "drawScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/TextureManager;bindTexture(Lnet/minecraft/src/ResourceLocation;)V"))
    private ResourceLocation cancerBTWLogo(ResourceLocation par1ResourceLocation){
        if(NightmareMode.isAprilFools){
            return BTW_CANCER;
        }
        return par1ResourceLocation;
    }
    @ModifyArg(method = "addSingleplayerMultiplayerButtons", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/GuiButton;<init>(IIILjava/lang/String;)V",ordinal = 0),index = 3)
    private String replaceSinglePlayerText(String par4Str){
        if(NightmareMode.isAprilFools){
            return "Skibidiplayer";
        }
        return par4Str;
    }
    @ModifyArg(method = "addSingleplayerMultiplayerButtons", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/GuiButton;<init>(IIILjava/lang/String;)V",ordinal = 1),index = 3)
    private String replaceMultiPlayerText(String par4Str){
        if(NightmareMode.isAprilFools){
            return "Rizz your friends";
        }
        return par4Str;
    }

    @ModifyArgs(method = "drawScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/GuiMainMenu;drawCenteredString(Lnet/minecraft/src/FontRenderer;Ljava/lang/String;III)V"))
    private void changeSplashScreenHeight(Args args) {
        int xOffset = args.get(2);
        int yOffset = args.get(3);
        args.set(2, xOffset + 25);
        args.set(3, yOffset - 3);
    }
    @ModifyArgs(method = "drawScreen", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glColor4f(FFFF)V"))
    private void colorOfBetterThanWolvesSign(Args args){
        if (NightmareMode.bloodmare) {
            args.set(1, 0.15f);
            args.set(2, 0.15f);
            args.set(3, 0.15f);
        }
    }

    @ModifyArgs(method = "drawPanorama", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/Tessellator;setColorRGBA_I(II)V"))
    private void modifyColorsOnBloodmare0(Args args){
        if (NightmareMode.bloodmare) {
        args.set(0, (255 << 16) | (40 << 8) | 40);
        args.set(1, 40);
        }
    }

    @Inject(method = "drawScreen", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glPushMatrix()V"))
    private void customNightmareGui(int par1, int par2, float par3, CallbackInfo ci){
        short var5 = 256;
        int var6 = this.width / 2 - var5 / 2;
        byte var7 = 30;

        this.mc.getTextureManager().bindTexture(MENU);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        int customTextureY = var7 + 46; // Slightly below the logo

        // Define size (Adjust to your texture dimensions)
        int customWidth = 256;  // Replace with actual width
        int customHeight = 51;  // Replace with actual height

        // Draw the texture
        this.drawTexturedModalRect(var6, customTextureY, 0, 0, customWidth, customHeight);
    }

    @Unique
    private static @NotNull List<String> getQuotes() {
        List<String> quotesList = new ArrayList<>();
        quotesList.add("Nightmare Mode!");
        quotesList.add("Also try MEA!");
        quotesList.add("Also try Hostile!");
        quotesList.add("Am I dreaming?");
        quotesList.add("Wakey wakey!");
        quotesList.add("Orange-flavored creepers?!");
        quotesList.add("Better Than MEA?");
        quotesList.add("It's just a bad dream!");
        quotesList.add("Not the water creatures!");
        quotesList.add("MEN! WITH ME!");
        quotesList.add("Wake up!");
        quotesList.add("Type 1 if you agree!");
        quotesList.add("Now with Blood Moons!");
        quotesList.add("It's easy if you're good!");
        quotesList.add("Hug the saw!");
        quotesList.add("Also try Bloodmare!");
        quotesList.add("Axes aren't weapons!");
        quotesList.add("Buff Squids!");
        quotesList.add("Cookie Creepers!");
        quotesList.add("MEA is harder!");
        quotesList.add("A God does not fear death!");
        quotesList.add("Greed!");
        quotesList.add("Shattered Sun!");
        quotesList.add("Now with Solar Eclipses!");
        return quotesList;
    }
}
