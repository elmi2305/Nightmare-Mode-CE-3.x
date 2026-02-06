package com.itlesports.nightmaremode.mixin.render;

import com.itlesports.nightmaremode.util.NMUtils;
import net.minecraft.src.EntitySheep;
import net.minecraft.src.RenderSheep;
import net.minecraft.src.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderSheep.class)
public class RenderSheepMixin {
    @Unique private static final ResourceLocation SHEEP_ECLIPSE = new ResourceLocation("nightmare:textures/entity/sheepEclipse.png");

    @Inject(method = "func_110883_a", at = @At("HEAD"),cancellable = true)
    private void sheepEclipseTextures(EntitySheep par1EntitySheep, CallbackInfoReturnable<ResourceLocation> cir){
        if (NMUtils.getIsMobEclipsed(par1EntitySheep)) {
            cir.setReturnValue(SHEEP_ECLIPSE);
        }
    }
}
