package com.itlesports.nightmaremode.mixin.render;

import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.NightmareUtils;
import net.minecraft.src.MathHelper;
import net.minecraft.src.RenderGlobal;
import net.minecraft.src.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.List;

@Mixin(RenderGlobal.class)
public class RenderGlobalMixin {
    @Unique private static int index = 0;
    @Unique private static final ResourceLocation BLOODMOON = new ResourceLocation("textures/bloodmoon.png");
    @Unique private static final ResourceLocation ECLIPSE = new ResourceLocation("textures/eclipse.png");
    @Unique private static List<ResourceLocation> sunAnimationList = new ArrayList<>();

    @Unique private static void initLists(){
        for(int i = 0; i <= 343; i++){
            sunAnimationList.add(new ResourceLocation("textures/sun/frame_" + i + "_delay-0.1s.gif"));
        }
    }

    @ModifyArg(method = "renderSky", at = @At(value = "INVOKE", target = "Lcom/prupe/mcpatcher/sky/SkyRenderer;setupCelestialObject(Lnet/minecraft/src/ResourceLocation;)Lnet/minecraft/src/ResourceLocation;",ordinal = 2))
    private ResourceLocation manageBloodMoonTexture(ResourceLocation defaultTexture){
        if(NightmareUtils.getIsBloodMoon()){
            return BLOODMOON;
        }
        return defaultTexture;
    }
    @ModifyArg(method = "renderSky", at = @At(value = "INVOKE", target = "Lcom/prupe/mcpatcher/sky/SkyRenderer;setupCelestialObject(Lnet/minecraft/src/ResourceLocation;)Lnet/minecraft/src/ResourceLocation;",ordinal = 0))
    private ResourceLocation manageEclipseTexture(ResourceLocation defaultTexture){
        if(NightmareMode.isAprilFools){
            if(sunAnimationList.isEmpty()){
                initLists();
            }
            index = (index + 1) % (343 * 6);
            return sunAnimationList.get(MathHelper.floor_float((float) index / 6));
        } else if(NightmareUtils.getIsEclipse()){
            return ECLIPSE;
        }
        return defaultTexture;
    }
    @Redirect(method = "renderSky", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glBlendFunc(II)V",ordinal = 2))
    private void manageSunNotBlendingOnEclipse(int sFactor, int dFactor){
        if(NightmareUtils.getIsEclipse()){
            GL11.glBlendFunc(770,1);
        }
        GL11.glBlendFunc(sFactor,dFactor);
    }
}
