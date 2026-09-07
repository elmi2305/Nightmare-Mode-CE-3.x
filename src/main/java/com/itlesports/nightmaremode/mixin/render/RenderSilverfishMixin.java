package com.itlesports.nightmaremode.mixin.render;

import com.itlesports.nightmaremode.entity.EntityEnderSilverfish;
import com.itlesports.nightmaremode.entity.EntityNetherFish;
import net.minecraft.src.EntitySilverfish;
import net.minecraft.src.RenderSilverfish;
import net.minecraft.src.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderSilverfish.class)
public class RenderSilverfishMixin {
    @Unique private static final ResourceLocation netherFishTextures = new ResourceLocation("nightmare:textures/entity/ifhyNetherFish.png");
    @Unique private static final ResourceLocation enderFishTextures = new ResourceLocation("nightmare:textures/entity/ifhyEnderFish.png");

    @Inject(method = "getSilverfishTextures", at = @At("HEAD"),cancellable = true)
    private void addVariantTextures(EntitySilverfish sf, CallbackInfoReturnable<ResourceLocation> cir) {
        if (sf instanceof EntityNetherFish) {
            cir.setReturnValue(netherFishTextures);
            return;
        }
        if (sf instanceof EntityEnderSilverfish) {
            cir.setReturnValue(enderFishTextures);
            return;
        }
    }
}
