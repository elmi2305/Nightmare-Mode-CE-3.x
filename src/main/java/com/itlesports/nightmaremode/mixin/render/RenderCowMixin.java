package com.itlesports.nightmaremode.mixin.render;

import com.itlesports.nightmaremode.NightmareUtils;
import net.minecraft.src.EntityCow;
import net.minecraft.src.RenderCow;
import net.minecraft.src.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderCow.class)
public class RenderCowMixin {
    @Unique private static final ResourceLocation COW_ECLIPSE = new ResourceLocation("textures/entity/cowEclipse.png");

    @Inject(method = "getCowTextures", at = @At("HEAD"),cancellable = true)
    private void cowEclipseTextures(EntityCow par1EntityCow, CallbackInfoReturnable<ResourceLocation> cir){
        if (NightmareUtils.getIsEclipse()) {
            cir.setReturnValue(COW_ECLIPSE);
        }
    }
}
