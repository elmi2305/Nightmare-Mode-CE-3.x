package com.itlesports.nightmaremode.mixin.render;

import com.itlesports.nightmaremode.util.interfaces.EntityWitherSkullExt;
import net.minecraft.src.EntityWitherSkull;
import net.minecraft.src.RenderWitherSkull;
import net.minecraft.src.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderWitherSkull.class)
public class RenderWitherSkullMixin {
    private static final ResourceLocation RED = new ResourceLocation("nightmare:textures/entity/nmRedSkull.png");

    @Inject(method = "func_110809_a", at = @At("HEAD"),cancellable = true)
    private void addTextureForRedOnes(EntityWitherSkull skull, CallbackInfoReturnable<ResourceLocation> cir){
        if(skull instanceof EntityWitherSkullExt && ((EntityWitherSkullExt) skull).nightmareMode$getLifeStealing()){
            cir.setReturnValue(RED);
        }
    }
}
