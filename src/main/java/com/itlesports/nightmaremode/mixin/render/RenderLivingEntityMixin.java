package com.itlesports.nightmaremode.mixin.render;

import com.itlesports.nightmaremode.entity.EntityPhantomZombie;
import com.itlesports.nightmaremode.util.interfaces.CarcassAnimal;
import net.minecraft.src.EntityLivingBase;
import net.minecraft.src.EntitySpider;
import net.minecraft.src.RendererLivingEntity;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RendererLivingEntity.class)
public class RenderLivingEntityMixin {

    @Inject(
            method = "renderModel",
            at = @At("HEAD")
    )
    private void nm$startTransparency(EntityLivingBase entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale, CallbackInfo ci) {
        if (entity instanceof CarcassAnimal carcass && carcass.nm$isCarcass()) {
            GL11.glColor4f(0.72F, 0.16F, 0.16F, 1.0F);
        }
        if (entity instanceof EntityPhantomZombie) {
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.5F);
        }
    }

    @Inject(
            method = "renderModel",
            at = @At("RETURN")
    )
    private void nm$endTransparency(EntityLivingBase entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale, CallbackInfo ci) {
        if (entity instanceof EntityPhantomZombie) {
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        }
        if (entity instanceof CarcassAnimal carcass && carcass.nm$isCarcass()) {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    @Inject(method = "renderLivingAt", at = @At("TAIL"))
    private void nm$offsetSpiderCarcass(EntityLivingBase entity, double x, double y, double z, CallbackInfo ci) {
        if (entity instanceof EntitySpider
                && entity instanceof CarcassAnimal carcass
                && carcass.nm$isCarcass()) {
            GL11.glTranslatef(0.0F, 1.0F, 0.0F);
        }
    }
}
