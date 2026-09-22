package com.itlesports.nightmaremode.mixin.render;

import com.itlesports.nightmaremode.util.interfaces.IHighSpeedMinecart;
import com.itlesports.nightmaremode.util.interfaces.IColoredChest;
import com.itlesports.nightmaremode.util.StorageColor;
import net.minecraft.src.Block;
import net.minecraft.src.EntityMinecart;
import net.minecraft.src.EntityMinecartChest;
import net.minecraft.src.ModelBase;
import net.minecraft.src.RenderMinecart;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderMinecart.class)
public class RenderMinecartMixin {
    @Inject(method = "renderTheMinecart", at = @At("HEAD"))
    private void tintHighSpeedCartContents(EntityMinecart minecart, double x, double y, double z,
                                           float yaw, float partialTicks, CallbackInfo ci) {
        this.applyCobaltTint(minecart);
    }

    @Inject(method = "renderTheMinecart", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/src/ModelBase;render(Lnet/minecraft/src/Entity;FFFFFF)V"))
    private void tintHighSpeedCartBody(EntityMinecart minecart, double x, double y, double z,
                                       float yaw, float partialTicks, CallbackInfo ci) {
        this.applyCobaltTint(minecart);
    }

    @Inject(method = "renderTheMinecart", at = @At("TAIL"))
    private void resetHighSpeedCartTint(EntityMinecart minecart, double x, double y, double z,
                                        float yaw, float partialTicks, CallbackInfo ci) {
        if (minecart instanceof IHighSpeedMinecart highSpeed && highSpeed.nightmareMode$isHighSpeed()) {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    @Inject(method = "renderBlockInMinecart", at = @At("HEAD"))
    private void tintChestMinecart(EntityMinecart minecart, float partialTicks, Block block, int metadata, CallbackInfo ci) {
        if (minecart instanceof EntityMinecartChest && minecart instanceof IColoredChest chest
                && chest.nm$hasChestColor()) {
            StorageColor.setChestRenderColorOverride(chest.nm$getChestColor());
        }
    }

    @Inject(method = "renderBlockInMinecart", at = @At("TAIL"))
    private void resetChestMinecartTint(EntityMinecart minecart, float partialTicks, Block block, int metadata, CallbackInfo ci) {
        StorageColor.clearChestRenderColorOverride();
        StorageColor.clearRenderColorOverride();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private void applyCobaltTint(EntityMinecart minecart) {
        if (minecart instanceof IHighSpeedMinecart highSpeed && highSpeed.nightmareMode$isHighSpeed()) {
            GL11.glColor4f(0.16F, 0.38F, 0.82F, 1.0F);
        }
    }
}
