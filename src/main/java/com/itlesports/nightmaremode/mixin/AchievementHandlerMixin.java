package com.itlesports.nightmaremode.mixin;

import api.achievement.AchievementHandler;
import com.itlesports.nightmaremode.world.JourneyProfile;
import net.minecraft.src.Achievement;
import net.minecraft.src.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Records milestones only after the achievement backend has safely accepted the unlock. */
@Mixin(AchievementHandler.class)
public class AchievementHandlerMixin {
    @Inject(method = "triggerAchievement", at = @At("TAIL"))
    private static void recordJourneyMilestone(EntityPlayer player, Achievement achievement, CallbackInfo ci) {
        if (player == null || player.worldObj == null || player.worldObj.isRemote) return;
        JourneyProfile profile = JourneyProfile.getOrCreate(player.worldObj);
        profile.updateProgressFromAchievement(achievement);
        player.worldObj.setData(btw.community.nightmaremode.NightmareMode.JOURNEY_PROFILE, profile);
    }
}
