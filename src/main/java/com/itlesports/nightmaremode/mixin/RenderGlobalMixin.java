package com.itlesports.nightmaremode.mixin;

import com.itlesports.nightmaremode.NightmareUtils;
import net.minecraft.src.RenderGlobal;
import net.minecraft.src.ResourceLocation;
import net.minecraft.src.WorldClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(RenderGlobal.class)
public class RenderGlobalMixin {
    @Unique
    private static final ResourceLocation bloodmoontexture = new ResourceLocation("textures/bloodmoon.png");
    @Shadow private WorldClient theWorld;

    @ModifyArg(method = "renderSky", at = @At(value = "INVOKE", target = "Lcom/prupe/mcpatcher/sky/SkyRenderer;setupCelestialObject(Lnet/minecraft/src/ResourceLocation;)Lnet/minecraft/src/ResourceLocation;",ordinal = 2))
    private ResourceLocation manageBloodMoonTexture(ResourceLocation defaultTexture){
        if(NightmareUtils.getIsBloodMoon(this.theWorld)){
            return bloodmoontexture;
        }
        return defaultTexture;
    }
}
