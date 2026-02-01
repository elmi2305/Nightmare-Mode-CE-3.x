package com.itlesports.nightmaremode.mixin.render;

import btw.client.render.entity.SquidRenderer;
import com.itlesports.nightmaremode.NMUtils;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityLivingBase;
import net.minecraft.src.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SquidRenderer.class)
public class SquidRendererMixin {
    @Unique
    private static final ResourceLocation SQUID_ECLIPSE = new ResourceLocation("nightmare:textures/entity/squidEclipse.png");

    @Inject(method = "getEntityTexture", at = @At("HEAD"),cancellable = true)
    private void manageEclipseTexture(Entity var1, CallbackInfoReturnable<ResourceLocation> cir){
        if (NMUtils.getIsMobEclipsed((EntityLivingBase) var1)) {
            cir.setReturnValue(SQUID_ECLIPSE);
        }
    }
}
