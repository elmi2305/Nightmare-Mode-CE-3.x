package com.itlesports.nightmaremode.mixin.entity;

import btw.entity.mob.JungleSpiderEntity;
import com.itlesports.nightmaremode.util.NMUtils;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(JungleSpiderEntity.class)
public class JungleSpiderEntityMixin extends EntitySpider{
    public JungleSpiderEntityMixin(World par1World) {
        super(par1World);
    }

    @Redirect(method = "attackEntityAsMob", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/src/EntityLivingBase;addPotionEffect(Lnet/minecraft/src/PotionEffect;)V", ordinal = 1))
    private void skipExtraHungerEffect(EntityLivingBase target, PotionEffect effect) {
    }

    @Inject(method = "dropsSpiderEyes", at = @At("RETURN"), cancellable = true)
    private void dropSpiderEyes(CallbackInfoReturnable<Boolean> cir){
        cir.setReturnValue(true);
    }

    @Inject(method = "applyEntityAttributes", at = @At("TAIL"))
    private void scaleHealth(CallbackInfo ci){
        if (this.worldObj != null) {
            boolean isEclipse = NMUtils.getIsMobEclipsed(this);
            boolean isBloodMoon = NMUtils.getIsBloodMoon();
            this.getEntityAttribute(SharedMonsterAttributes.followRange).setAttribute(NMUtils.getBalancedMobFollowRange(this.worldObj, 16.0d, NMUtils.getWorldProgress(), isBloodMoon, isEclipse));
            this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(11.0 + NMUtils.getWorldProgress() * 2);
        }
    }

}
