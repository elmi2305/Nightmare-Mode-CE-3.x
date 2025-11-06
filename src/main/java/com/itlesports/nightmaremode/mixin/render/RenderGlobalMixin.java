package com.itlesports.nightmaremode.mixin.render;

import com.itlesports.nightmaremode.NMUtils;
import net.minecraft.src.RenderGlobal;
import net.minecraft.src.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RenderGlobal.class)
public class RenderGlobalMixin {
    @Unique private static final ResourceLocation BLOODMOON = new ResourceLocation("textures/bloodmoon.png");
    @Unique private static final ResourceLocation ECLIPSE = new ResourceLocation("textures/eclipse.png");

    @ModifyArg(method = "renderSky", at = @At(value = "INVOKE", target = "Lcom/prupe/mcpatcher/sky/SkyRenderer;setupCelestialObject(Lnet/minecraft/src/ResourceLocation;)Lnet/minecraft/src/ResourceLocation;",ordinal = 2))
    private ResourceLocation manageBloodMoonTexture(ResourceLocation defaultTexture){
        if(NMUtils.getIsBloodMoon()){
            return BLOODMOON;
        }
        return defaultTexture;
    }
    @ModifyArg(method = "renderSky", at = @At(value = "INVOKE", target = "Lcom/prupe/mcpatcher/sky/SkyRenderer;setupCelestialObject(Lnet/minecraft/src/ResourceLocation;)Lnet/minecraft/src/ResourceLocation;",ordinal = 0))
    private ResourceLocation manageEclipseTexture(ResourceLocation defaultTexture){
        if(NMUtils.getIsEclipse()){
            return ECLIPSE;
        }
        return defaultTexture;
    }
    @Redirect(method = "renderSky", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glBlendFunc(II)V",ordinal = 2), remap = false)
    private void manageSunNotBlendingOnEclipse(int sFactor, int dFactor){
        if(NMUtils.getIsEclipse()){
            GL11.glBlendFunc(770,1);
        }
        GL11.glBlendFunc(sFactor,dFactor);
    }

    @ModifyArg(method = "renderSky", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glColor4f(FFFF)V", ordinal = 0), index = 3)
    private float doNotRemoveSunOnBloodRain(float red){
        if(NMUtils.getIsBloodMoon()) {return 0.6f;}

        return red;
    }
}
