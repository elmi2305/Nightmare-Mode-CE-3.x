package com.itlesports.nightmaremode.mixin;

import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.util.NMFields;
import com.itlesports.nightmaremode.util.NMUtils;
import com.itlesports.nightmaremode.util.interfaces.EntityPlayerExt;
import com.itlesports.nightmaremode.util.interfaces.FoodStatsExt;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.FoodStats;
import net.minecraft.src.NBTTagCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FoodStats.class)
public class FoodStatsMixin implements FoodStatsExt {
    @Unique private int foodCapChanged = 60;
    @Unique private EntityPlayer player;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void startingFoodLevel(CallbackInfo ci){
        FoodStats thisObj = (FoodStats)(Object)this;
        int desiredFoodLevel = NightmareMode.nite ? 18 : (NightmareMode.perfectStart ? 36 : 60);
        thisObj.setFoodLevel(desiredFoodLevel);
    }

    @ModifyConstant(method = "readNBT", constant = @Constant(intValue = 60))
    private int getPlayerShanks(int constant){
        return NMFields.MAX_FOOD_FROM_FRUITS; // returning this.foodCapChanged is ineffective because the food cap inits at 60
    }

    @Inject(method = "onUpdate", at = @At("HEAD"))
    private void onUpdateHead(EntityPlayer player, CallbackInfo ci) {
        this.player = player;
    }

    @ModifyConstant(method = {"addStats(IF)V", "needFood"}, constant = @Constant(intValue = 60))
    private int modifyAddStatsMaxHunger(int original) {
        if (NightmareMode.nite) {
            return player != null ? NMUtils.getFoodShanksFromLevel(player) : original;
        }
        return this.foodCapChanged;
    }
    @Inject(method = "readNBT", at = @At(value = "TAIL"))
    private void addCustomNBT(NBTTagCompound tag, CallbackInfo ci){
        this.foodCapChanged = tag.getInteger("nmFoodCapChanged");
        if(this.foodCapChanged < 60){
            this.foodCapChanged = 60;
        }
    }

    @Inject(method = "writeNBT", at = @At("TAIL"))
    private void writeCustomNBT(NBTTagCompound tag, CallbackInfo ci){
        tag.setInteger("nmFoodCapChanged", this.foodCapChanged);
    }
    @Override
    public int nightmareMode$getMaxFoodLevel() {
        return this.foodCapChanged;
    }

    @Override
    public void nightmareMode$setMaxFoodLevel(int foodLevel) {
        this.foodCapChanged = foodLevel;
    }
}
