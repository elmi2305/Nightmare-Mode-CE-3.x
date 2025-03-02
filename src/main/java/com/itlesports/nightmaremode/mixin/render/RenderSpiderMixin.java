package com.itlesports.nightmaremode.mixin.render;

import com.itlesports.nightmaremode.NightmareUtils;
import com.itlesports.nightmaremode.entity.EntityBlackWidowSpider;
import com.itlesports.nightmaremode.entity.EntityFireSpider;
import net.minecraft.src.EntitySpider;
import net.minecraft.src.RenderSpider;
import net.minecraft.src.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderSpider.class)
public class RenderSpiderMixin {
    @Unique private boolean eclipseEyes;
    @Unique private static final ResourceLocation SPIDER_TEXTURE_ECLIPSE = new ResourceLocation("textures/entity/spiderEclipseHigh.png");
    @Unique private static final ResourceLocation SPIDER_TEXTURE_FIRE = new ResourceLocation("textures/entity/spiderFire.png");
    @Unique private static final ResourceLocation SPIDER_TEXTURE_BLACK = new ResourceLocation("textures/entity/spiderBlackWidow.png");
    @Unique private static final ResourceLocation NOTHING = new ResourceLocation("textures/entity/nothing.png");

    @Inject(method = "getSpiderTextures", at = @At("HEAD"),cancellable = true)
    private void manageEclipsedTextures(EntitySpider par1EntitySpider, CallbackInfoReturnable<ResourceLocation> cir) {
        if(par1EntitySpider instanceof EntityBlackWidowSpider){
            cir.setReturnValue(SPIDER_TEXTURE_BLACK);
        }else if(par1EntitySpider instanceof EntityFireSpider){
            cir.setReturnValue(SPIDER_TEXTURE_FIRE);
        } else if(NightmareUtils.getIsMobEclipsed(par1EntitySpider)){
            this.eclipseEyes = true;
            cir.setReturnValue(SPIDER_TEXTURE_ECLIPSE);
        }
    }
    @ModifyArg(method = "setSpiderEyeBrightness", at = @At(value = "INVOKE", target = "Lcom/prupe/mcpatcher/mob/MobRandomizer;randomTexture(Lnet/minecraft/src/Entity;Lnet/minecraft/src/ResourceLocation;)Lnet/minecraft/src/ResourceLocation;"),index = 1)
    private ResourceLocation noEyesDuringEclipse(ResourceLocation texture){
        if(this.eclipseEyes){
            return NOTHING;
        }
        return texture;
    }
}
