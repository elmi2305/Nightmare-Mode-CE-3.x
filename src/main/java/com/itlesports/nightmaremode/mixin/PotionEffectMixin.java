package com.itlesports.nightmaremode.mixin;

import com.itlesports.nightmaremode.util.NMUtils;
import net.minecraft.src.EntityLivingBase;
import net.minecraft.src.PotionEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PotionEffect.class)
public abstract class PotionEffectMixin {
    @Shadow protected abstract int deincrementDuration();

    @Inject(method = "onUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/PotionEffect;deincrementDuration()I"))
    private void decrementDurationOnBloodArmor(EntityLivingBase entity, CallbackInfoReturnable<Boolean> cir) {
        if (entity.rand.nextInt(5 - NMUtils.getBloodArmorWornCount(entity)) == 0)
        {
            this.deincrementDuration();
        }
    }
}
