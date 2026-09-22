package com.itlesports.nightmaremode.mixin.gui;

import com.itlesports.nightmaremode.client.ScaryEvents;
import net.minecraft.src.Achievement;
import net.minecraft.src.GuiAchievement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiAchievement.class)
public class GuiAchievementScaryMixin {
    @Inject(method = {"queueTakenAchievement", "queueAchievementInformation"}, at = @At("HEAD"))
    private void deferScaresAfterAchievement(Achievement achievement, CallbackInfo ci) {
        ScaryEvents.majorEvent();
    }
}
