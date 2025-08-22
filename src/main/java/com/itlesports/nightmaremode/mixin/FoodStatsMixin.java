package com.itlesports.nightmaremode.mixin;

import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.NMUtils;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.FoodStats;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FoodStats.class)
public class FoodStatsMixin {
    @Unique private EntityPlayer player;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void startingFoodLevel(CallbackInfo ci){
        FoodStats thisObj = (FoodStats)(Object)this;
        int desiredFoodLevel = NightmareMode.nite ? 18 : (NightmareMode.perfectStart ? 36 : 60);
        thisObj.setFoodLevel(desiredFoodLevel);
    }

    @Inject(method = "onUpdate", at = @At("HEAD"))
    private void onUpdateHead(EntityPlayer player, CallbackInfo ci) {
        this.player = player;
    }

    @ModifyConstant(method = {"addStats(IF)V", "needFood"}, constant = @Constant(intValue = 60, ordinal = 0))
    private int modifyAddStatsMaxHunger(int original) {
        if (NightmareMode.nite) {
            return player != null ? NMUtils.getFoodShanksFromLevel(player) : original;
        }
        return original;
    }

}
