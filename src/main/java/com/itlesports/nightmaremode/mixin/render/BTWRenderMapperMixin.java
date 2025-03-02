package com.itlesports.nightmaremode.mixin.render;

import btw.client.render.BTWRenderMapper;
import com.itlesports.nightmaremode.*;
import com.itlesports.nightmaremode.entity.*;
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
        RenderManager.addEntityRenderer(EntityShadowZombie.class, new RenderShadowZombie());
        RenderManager.addEntityRenderer(EntityNightmareGolem.class, new RenderNightmareGolem());
        RenderManager.addEntityRenderer(EntityMetalCreeper.class, new RenderMetalCreeper());
        RenderManager.addEntityRenderer(EntitySuperchargedCreeper.class, new RenderSupercriticalCreeper());
    }
}
