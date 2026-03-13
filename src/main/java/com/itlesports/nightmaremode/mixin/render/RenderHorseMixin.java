package com.itlesports.nightmaremode.mixin.render;

import com.itlesports.nightmaremode.util.NMUtils;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(RenderHorse.class)
public abstract class RenderHorseMixin {
    @Unique private static final ResourceLocation HORSE_ECLIPSE = new ResourceLocation("nightmare:textures/entity/horseEclipse.png");

    @Inject(method = "func_110849_a", at = @At("HEAD"),cancellable = true)
    private void horseEclipseTextures(EntityHorse par1, CallbackInfoReturnable<ResourceLocation> cir){
        if (NMUtils.getIsMobEclipsed(par1)) {
            cir.setReturnValue(HORSE_ECLIPSE);
        }
    }
    @Redirect(method = "func_110849_a", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityHorse;getHungerLevel()I"))
    private int baseHungerIfTamed(EntityHorse instance){
        if(instance.isTame() && instance.func_110241_cb() != 0){
            return 0; // wearing armor
            // hacky fix. other stuff I tried to do (like fixing the render itself) didn't work
        }
        return instance.getHungerLevel();
    }
}
