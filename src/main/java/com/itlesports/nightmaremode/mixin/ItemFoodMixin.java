package com.itlesports.nightmaremode.mixin;

import btw.achievement.event.AchievementEventDispatcher;
import com.itlesports.nightmaremode.achievements.NMAchievementEvents;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemFood.class)
public class ItemFoodMixin {
    @Shadow private int potionId;

    @Shadow private float potionEffectProbability;

    @Shadow private int potionDuration;

    @Shadow private int potionAmplifier;

    @Inject(method = "onEaten", at = @At("TAIL"))
    private void goldenCarrotFunctionality(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer, CallbackInfoReturnable<ItemStack> cir){
        if(par1ItemStack.itemID == Item.goldenCarrot.itemID){
            par3EntityPlayer.addPotionEffect(new PotionEffect(Potion.nightVision.id, 2400,0)); // 2 minutes of night vision
        }
    }
    @Inject(method = "onFoodEaten", at = @At(value = "HEAD"), cancellable = true)
    private void manageFoodPoisoningAchievement(ItemStack stack, World world, EntityPlayer player, CallbackInfo ci){
        boolean wasPoisoned = false;
        if (!world.isRemote && this.potionId > 0 && world.rand.nextFloat() < this.potionEffectProbability) {
            player.addPotionEffect(new PotionEffect(this.potionId, this.potionDuration * 20, this.potionAmplifier));
            wasPoisoned = true;
        }
        if(this.potionId == Potion.hunger.id){
            AchievementEventDispatcher.triggerEvent(NMAchievementEvents.PlayerPoisonedEvent.class, player, wasPoisoned);
        }
        ci.cancel();
    }
}
