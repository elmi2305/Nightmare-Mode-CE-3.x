package com.itlesports.nightmaremode.mixin.render;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.EntityEnderCrystal;
import net.minecraft.src.Minecraft;
import net.minecraft.src.RenderEnderCrystal;
import net.minecraft.src.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderEnderCrystal.class)
public class RenderEnderCrystalMixin {
    @Unique private boolean isInOverworld;
    @Unique private static final ResourceLocation CRYSTAL_NO_BASE = new ResourceLocation("textures/entity/enderCrystalNoBase.png");

    @Inject(method = "getEnderCrystalTextures", at = @At("RETURN"),cancellable = true)
    private void setCrystalNoBase(EntityEnderCrystal par1, CallbackInfoReturnable<ResourceLocation> cir){
        if (this.isInOverworld) {
            cir.setReturnValue(CRYSTAL_NO_BASE);
        }
    }
    @ModifyArg(method = "doRenderEnderCrystal", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/RenderEnderCrystal;bindTexture(Lnet/minecraft/src/ResourceLocation;)V"))
    private ResourceLocation manageEnderCrystalOverworldTexture(ResourceLocation par1){
        boolean isInOverworld = !MinecraftServer.getIsServer() && Minecraft.getMinecraft().thePlayer.dimension == 0;
        if(isInOverworld){
            this.isInOverworld = true;
            return CRYSTAL_NO_BASE;
        }
        return par1;
    }
}
