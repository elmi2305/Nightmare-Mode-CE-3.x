package com.itlesports.nightmaremode.mixin.entity;

import com.itlesports.nightmaremode.entity.creepers.EntityCreeperVariant;
import com.itlesports.nightmaremode.skill.SkillHandler;
import net.minecraft.src.EntityLivingBase;
import net.minecraft.src.DamageSource;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityTameable;
import net.minecraft.src.EntityWolf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityWolf.class)
public abstract class EntityWolfMixin {
    @Unique private boolean nightmareMode$wasTamedBeforeInteract;

    @Inject(method = "isWildAndHostile", at = @At("HEAD"), cancellable = true)
    private void keepUnprovokedPuppiesPassive(CallbackInfoReturnable<Boolean> cir) {
        EntityWolf wolf = (EntityWolf)(Object)this;
        if (wolf.isChild() && !wolf.isAngry()) cir.setReturnValue(false);
    }

    @Inject(method = "onNearbyPlayerStartles", at = @At("HEAD"), cancellable = true)
    private void keepPuppiesFromStartlingAtPlayers(EntityPlayer player, CallbackInfo ci) {
        if (((EntityWolf)(Object)this).isChild()) ci.cancel();
    }

    @Redirect(method = "attackEntityFrom", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/src/EntityWolf;setAngry(Z)V"))
    private void keepSuffocatingPuppiesCalm(EntityWolf wolf, boolean angry, DamageSource source, float damage) {
        if (!wolf.isChild() || source != DamageSource.inWall) wolf.setAngry(angry);
    }

    @Inject(method = "func_142018_a", at = @At("HEAD"), cancellable = true)
    private void avoidAttackingCreeperVariants(EntityLivingBase e1, EntityLivingBase e2, CallbackInfoReturnable<Boolean> cir){
        if(e1 instanceof EntityCreeperVariant){
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "interact", at = @At("HEAD"))
    private void captureSkillTameState(EntityPlayer player, CallbackInfoReturnable<Boolean> cir) {
        this.nightmareMode$wasTamedBeforeInteract = ((EntityTameable)(Object)this).isTamed();
    }

    @Inject(method = "interact", at = @At("RETURN"))
    private void trackSkillWolfTaming(EntityPlayer player, CallbackInfoReturnable<Boolean> cir) {
        EntityTameable wolf = (EntityTameable)(Object)this;
        if (!this.nightmareMode$wasTamedBeforeInteract
                && wolf.isTamed()
                && player != null
                && player.getCommandSenderName().equalsIgnoreCase(wolf.getOwnerName())) {
            SkillHandler.incrementAnimalsTamed(player);
        }
    }
}
