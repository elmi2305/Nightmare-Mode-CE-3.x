package com.itlesports.nightmaremode.mixin.render;

import com.itlesports.nightmaremode.util.NMFields;
import com.itlesports.nightmaremode.util.NMUtils;
import com.itlesports.nightmaremode.util.interfaces.EntityBlazeVariantExt;
import com.itlesports.nightmaremode.entity.variants.EntityCinderBlaze;
import com.itlesports.nightmaremode.entity.variants.EntityHellfireBlaze;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderBlaze.class)
public class RenderBlazeMixin {
    @Unique private static final ResourceLocation BLAZE_ECLIPSE = new ResourceLocation("nightmare:textures/entity/blazeEclipse.png");
    @Unique private static final ResourceLocation BLAZE_WATER = new ResourceLocation("nightmare:textures/entity/blazeBlue.png");
    @Unique private static final ResourceLocation CINDER_BLAZE = new ResourceLocation("nightmare:textures/entity/cinderBlaze.png");
    @Unique private static final ResourceLocation HELLFIRE_BLAZE = new ResourceLocation("nightmare:textures/entity/hellfireBlaze.png");

    @Inject(method = "getBlazeTextures", at = @At("HEAD"),cancellable = true)
    private void blazeEclipseTextures(EntityBlaze par1, CallbackInfoReturnable<ResourceLocation> cir){
        if (par1 instanceof EntityCinderBlaze) {
            cir.setReturnValue(CINDER_BLAZE);
            return;
        }
        if (par1 instanceof EntityHellfireBlaze) {
            cir.setReturnValue(HELLFIRE_BLAZE);
            return;
        }
        if(NMUtils.getIsMobEclipsed(par1)){
            if(((EntityBlazeVariantExt)par1).nm$getBlazeVariant() == NMFields.BLAZE_AQUA){
                cir.setReturnValue(BLAZE_WATER);
            } else {
                cir.setReturnValue(BLAZE_ECLIPSE);
            }
        }
    }
}
