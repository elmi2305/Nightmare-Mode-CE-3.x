package com.itlesports.nightmaremode.mixin.render;

import com.itlesports.nightmaremode.NightmareUtils;
import net.minecraft.src.EntitySlime;
import net.minecraft.src.RenderSlime;
import net.minecraft.src.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderSlime.class)
public class RenderSlimeMixin {
    @Unique private static final ResourceLocation SLIME_1 = new ResourceLocation("textures/entity/slime1.png");
    @Unique private static final ResourceLocation SLIME_2 = new ResourceLocation("textures/entity/slime2.png");
    @Unique private static final ResourceLocation SLIME_3 = new ResourceLocation("textures/entity/slime3.png");
    @Unique private static final ResourceLocation SLIME_4 = new ResourceLocation("textures/entity/slime4.png");
    @Unique private static final ResourceLocation SLIME_5 = new ResourceLocation("textures/entity/slime5.png");
    @Unique private static final ResourceLocation SLIME_6 = new ResourceLocation("textures/entity/slime6.png");
    @Unique private static final ResourceLocation SLIME_7 = new ResourceLocation("textures/entity/slime7.png");
    @Unique private static final ResourceLocation SLIME_8 = new ResourceLocation("textures/entity/slime8.png");

    @Inject(method = "getSlimeTextures", at = @At("HEAD"),cancellable = true)
    private void slimeEclipseTextures(EntitySlime par1, CallbackInfoReturnable<ResourceLocation> cir){
        if(NightmareUtils.getIsMobEclipsed(par1)){
            switch(par1.getSlimeSize() % 8 + 1){
                case 1:
                    cir.setReturnValue(SLIME_1);
                    break;
                case 2:
                    cir.setReturnValue(SLIME_2);
                    break;
                case 3:
                    cir.setReturnValue(SLIME_3);
                    break;
                case 4:
                    cir.setReturnValue(SLIME_4);
                    break;
                case 5:
                    cir.setReturnValue(SLIME_5);
                    break;
                case 6:
                    cir.setReturnValue(SLIME_6);
                    break;
                case 7:
                    cir.setReturnValue(SLIME_7);
                    break;
                case 8:
                    cir.setReturnValue(SLIME_8);
                    break;
            }
        }
    }
}
