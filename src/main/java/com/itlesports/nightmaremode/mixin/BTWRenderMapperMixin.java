package com.itlesports.nightmaremode.mixin;

import btw.client.render.BTWRenderMapper;
import com.itlesports.nightmaremode.EntityFireCreeper;
import com.itlesports.nightmaremode.RenderFireCreeper;
import net.minecraft.src.RenderCreeper;
import net.minecraft.src.RenderManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BTWRenderMapper.class)
public class BTWRenderMapperMixin {
    @Inject(method = "initEntityRenderers", at = @At("TAIL"),remap = false)
    private static void doNightmareEntityRenderMapping(CallbackInfo ci){
        RenderManager.addEntityRenderer(EntityFireCreeper.class, new RenderFireCreeper());
    }
}
