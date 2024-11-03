package com.itlesports.nightmaremode.mixin;

import btw.world.util.difficulty.Difficulty;
import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiCreateWorld;
import net.minecraft.src.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiCreateWorld.class)
public abstract class GuiCreateWorldMixin extends GuiScreen {
    @Shadow private boolean lockDifficulty;
    @Shadow private int difficultyID;
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
        if(this.difficultyID == 3){ // if it's hostile
            this.difficultyID = 0; // sets it to standard
        } else if (this.difficultyID == 1){ // if it's standard
            this.difficultyID = 2; // sets it to hostile
        }
    }
    @Inject(method = "actionPerformed", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/GuiCreateWorld;updateButtonText()V", ordinal = 9))
    private void alwaysLockedDifficulty(CallbackInfo ci){
        this.lockDifficulty = true;
    }

    @Redirect(method = "updateButtonText", at = @At(value = "INVOKE", target = "Lbtw/world/util/difficulty/Difficulty;getLocalizedName()Ljava/lang/String;"))
    private String customDifficultyName(Difficulty difficulty){
        if(difficulty.ID == 2){
            return "Nightmare";
        } else if (difficulty.ID == 0){
            return "Bad Dream";
        }
        return difficulty.NAME;
    }

    @Redirect(method = "drawScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/I18n;getString(Ljava/lang/String;)Ljava/lang/String;",ordinal = 10))
    private String customText(String string){
        if (this.difficultyID == 2) {
            return "The ultimate challenge.";
        }
        return "A more relaxed experience. Makes";
    }
    @Redirect(method = "drawScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/I18n;getString(Ljava/lang/String;)Ljava/lang/String;",ordinal = 11))
    private String customText1(String string){
        if (this.difficultyID == 2) {
            return "";
        }
        return "many aspects of Nightmare Mode";
    }
    @Redirect(method = "drawScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/I18n;getString(Ljava/lang/String;)Ljava/lang/String;",ordinal = 12))
    private String customText2(String string){
        if (this.difficultyID == 2) {
            return "";
        }
        return "easier and more forgiving.";
    }
}
