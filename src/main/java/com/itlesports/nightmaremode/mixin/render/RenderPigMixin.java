package com.itlesports.nightmaremode.mixin.render;

import com.itlesports.nightmaremode.NMUtils;
import net.minecraft.src.EntityPig;
import net.minecraft.src.RenderPig;
import net.minecraft.src.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderPig.class)
public class RenderPigMixin {
    @Unique private static final ResourceLocation PIG_ECLIPSE = new ResourceLocation("nightmare:textures/entity/pigEclipseCreeper.png");

    @Inject(method = "getPigTextures", at = @At("HEAD"),cancellable = true)
    private void pigEclipseTextures(EntityPig par1EntityPig, CallbackInfoReturnable<ResourceLocation> cir){
        if (NMUtils.getIsMobEclipsed(par1EntityPig)) {
            cir.setReturnValue(PIG_ECLIPSE);
        }
    }
}
