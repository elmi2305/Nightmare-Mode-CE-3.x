package com.itlesports.nightmaremode.mixin;

import com.itlesports.nightmaremode.NightmareUtils;
import net.minecraft.src.EntityCreeper;
import net.minecraft.src.RenderCreeper;
import net.minecraft.src.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderCreeper.class)
public class RenderCreeperMixin {
    @Unique private static final ResourceLocation CREEPER_TEXTURE_ECLIPSE = new ResourceLocation("textures/entity/creeperEclipseHigh.png");

    @Inject(method = "getCreeperTextures", at = @At("HEAD"),cancellable = true)
    private void manageEclipsedTextures(EntityCreeper par1EntityCreeper, CallbackInfoReturnable<ResourceLocation> cir){
        if(NightmareUtils.getIsEclipse()){
            cir.setReturnValue(CREEPER_TEXTURE_ECLIPSE);
        }
    }
}