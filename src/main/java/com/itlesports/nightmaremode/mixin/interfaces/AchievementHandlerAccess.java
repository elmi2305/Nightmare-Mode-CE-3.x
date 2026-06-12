package com.itlesports.nightmaremode.mixin.interfaces;

import api.achievement.AchievementHandler;
import net.minecraft.src.Achievement;
import net.minecraft.src.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AchievementHandler.class)
public interface AchievementHandlerAccess {
    @Invoker("triggerAchievement") // doesn't work
    static void invokeAchievementTrigger(EntityPlayer player, Achievement achievement){};
}
