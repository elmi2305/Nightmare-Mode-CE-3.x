package com.itlesports.nightmaremode.mixin.gui;

import btw.client.gui.LockButton;
import btw.community.nightmaremode.NightmareMode;
import btw.world.util.difficulty.Difficulty;
import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiCreateWorld;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.I18n;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiCreateWorld.class)
public abstract class GuiCreateWorldMixin extends GuiScreen {
    @Shadow private boolean lockDifficulty;
    @Shadow private int difficultyID;
    @Shadow private LockButton buttonLockDifficulty;
    @Unique boolean onlyOnce = true;

    @Inject(method = "updateButtonText", at = @At("HEAD"))
    private void manageDifficulty(CallbackInfo ci){
        if(this.difficultyID == 0 && onlyOnce){
            this.difficultyID = 2;
            onlyOnce = false;
        }
    }

    @Inject(method = "actionPerformed", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/GuiCreateWorld;updateButtonText()V", ordinal = 8))
    private void manageDifficulty2(GuiButton par1GuiButton, CallbackInfo ci){
        if(this.difficultyID == 3){// if it's hostile (can't switch if bloodmare)
            this.difficultyID = 0; // sets it to standard
        } else if (this.difficultyID == 1){ // if it's standard
            this.difficultyID = 2; // sets it to hostile
        }
        if(NightmareMode.bloodmare){this.difficultyID = 2;}
    }
    @Inject(method = "actionPerformed", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/GuiCreateWorld;updateButtonText()V", ordinal = 9))
    private void alwaysLockedDifficulty(CallbackInfo ci){
        this.lockDifficulty = true;
    }

    @Redirect(method = "updateButtonText", at = @At(value = "INVOKE", target = "Lbtw/world/util/difficulty/Difficulty;getLocalizedName()Ljava/lang/String;"))
    private String customDifficultyName(Difficulty difficulty){
        if(difficulty.ID == 2){
            if(NightmareMode.bloodmare){
                return I18n.getString("difficulty.bloodmare.name");
            }
            return I18n.getString("difficulty.nightmare.name");
        } else if (difficulty.ID == 0){
            return I18n.getString("difficulty.baddream.name");
        }
        return difficulty.NAME;
    }
    @Inject(method = "updateButtonText", at = @At("TAIL"))
    private void lockButtonCannotBeClicked(CallbackInfo ci){
        // this fixes the issue TdL had right at the start, where he clicked the lock button thinking it'd make any difference
        this.buttonLockDifficulty.enabled = false;
    }

    @Redirect(method = "drawScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/I18n;getString(Ljava/lang/String;)Ljava/lang/String;",ordinal = 10))
    private String customText(String string){
        if (this.difficultyID == 2) {
            if(NightmareMode.bloodmare){
                return "";
            }
            return I18n.getString("difficulty.nightmare.description1");
        }
        return I18n.getString("difficulty.baddream.description1");
    }
    @Redirect(method = "drawScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/I18n;getString(Ljava/lang/String;)Ljava/lang/String;",ordinal = 11))
    private String customText1(String string){
        if (this.difficultyID == 2) {
            if(NightmareMode.bloodmare){
                return "";
            }
            return I18n.getString("difficulty.nightmare.description2");
        }
        return I18n.getString("difficulty.baddream.description2");
    }
    @Redirect(method = "drawScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/I18n;getString(Ljava/lang/String;)Ljava/lang/String;",ordinal = 12))
    private String customText2(String string){
        if (this.difficultyID == 2) {
            if(NightmareMode.bloodmare){
                return "";
            }
            return I18n.getString("difficulty.nightmare.description3");
        }
        return I18n.getString("difficulty.baddream.description3");
    }
}
