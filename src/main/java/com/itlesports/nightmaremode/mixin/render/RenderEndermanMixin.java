package com.itlesports.nightmaremode.mixin.render;

import com.itlesports.nightmaremode.NightmareUtils;
import com.itlesports.nightmaremode.entity.EntityRadioactiveEnderman;
import net.minecraft.src.EntityEnderman;
import net.minecraft.src.RenderEnderman;
import net.minecraft.src.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderEnderman.class)
public class RenderEndermanMixin {
    @Unique private boolean eclipseEyes;
    @Unique private static final ResourceLocation ENDERMAN_ECLIPSE = new ResourceLocation("textures/entity/endermanEclipseTux.png");
    @Unique private static final ResourceLocation ENDERMAN_GREEN = new ResourceLocation("textures/entity/endermanRadioactive.png");
    @Unique private static final ResourceLocation ENDERMAN_ECLIPSE_EYES = new ResourceLocation("textures/entity/endermanEclipseEyes.png");

    @Inject(method = "getEndermanTextures", at = @At("HEAD"), cancellable = true)
    private void endermanEclipseTextures(EntityEnderman par1EntityEnderman, CallbackInfoReturnable<ResourceLocation> cir) {
        if(par1EntityEnderman instanceof EntityRadioactiveEnderman){
            cir.setReturnValue(ENDERMAN_GREEN);
        } else if (NightmareUtils.getIsMobEclipsed(par1EntityEnderman)) {
            this.eclipseEyes = true;
            cir.setReturnValue(ENDERMAN_ECLIPSE);
        }
    }
    @ModifyArg(method = "renderEyes", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/RenderEnderman;bindTexture(Lnet/minecraft/src/ResourceLocation;)V"))
    private ResourceLocation addEyeTexturesOnEclipse(ResourceLocation par1){
        return this.eclipseEyes ? ENDERMAN_ECLIPSE_EYES : par1;
    }
}
