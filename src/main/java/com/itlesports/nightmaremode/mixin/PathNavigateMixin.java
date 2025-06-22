package com.itlesports.nightmaremode.mixin;

import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.entity.EntityBloodZombie;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.PathNavigate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PathNavigate.class)
public class PathNavigateMixin {
    @Shadow private EntityLiving theEntity;

    @Inject(method = "func_111269_d", at = @At("HEAD"),cancellable = true)
    private void increasePathRange(CallbackInfoReturnable<Float> cir){
        if (NightmareMode.hordeMode || this.theEntity instanceof EntityBloodZombie) {
            cir.setReturnValue(80f);
        }
    }
}
