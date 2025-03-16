package com.itlesports.nightmaremode.mixin.gui;

import btw.community.nightmaremode.NightmareMode;
import btw.world.util.difficulty.Difficulties;
import btw.world.util.difficulty.Difficulty;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.EnumOptions;
import net.minecraft.src.GuiOptions;
import net.minecraft.src.GuiSmallButton;
import net.minecraft.src.I18n;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(GuiOptions.class)
public class GuiOptionsMixin {
    @Inject(method = "initGui", at = @At(value = "FIELD", target = "Lnet/minecraft/src/GuiSmallButton;displayString:Ljava/lang/String;",shift = At.Shift.AFTER),locals = LocalCapture.CAPTURE_FAILHARD)
    private void manageIngameDifficultyDisplay(CallbackInfo ci, int var1, EnumOptions[] var2, int var3, int var4, EnumOptions var5, GuiSmallButton var6){
        Difficulty difficulty = MinecraftServer.getServer().worldServers[0].worldInfo.getDifficulty();
        String difficultyStringFormat = "%s: %s";
        if(difficulty == Difficulties.HOSTILE){
            var6.displayString = String.format(difficultyStringFormat, I18n.getString("selectWorld.difficulty"), I18n.getString("difficulty.nightmare.name"));
        } else if(difficulty == Difficulties.STANDARD){
            var6.displayString = String.format(difficultyStringFormat, I18n.getString("selectWorld.difficulty"), I18n.getString("difficulty.baddream.name"));
        }
        if(NightmareMode.bloodmare){
            var6.displayString = String.format(difficultyStringFormat, I18n.getString("selectWorld.difficulty"), I18n.getString("difficulty.bloodmare.name"));
        }
    }
}
