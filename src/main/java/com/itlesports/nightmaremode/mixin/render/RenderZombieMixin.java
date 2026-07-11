package com.itlesports.nightmaremode.mixin.render;

import com.itlesports.nightmaremode.util.NMUtils;
import com.itlesports.nightmaremode.entity.variants.EntityStoneZombie;
import com.itlesports.nightmaremode.util.elements.NMEvents;
import net.minecraft.src.EntityPigZombie;
import net.minecraft.src.EntityZombie;
import net.minecraft.src.RenderZombie;
import net.minecraft.src.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderZombie.class)
public class RenderZombieMixin {
    @Unique private static final ResourceLocation ZOMBIE_ECLIPSE = new ResourceLocation("nightmare:textures/entity/zombieEclipseHigh.png");
    @Unique private static final ResourceLocation PIG_ECLIPSE = new ResourceLocation("nightmare:textures/entity/zombiePigmanEclipse.png");
    @Unique private static final ResourceLocation PIGMAN_HELL = new ResourceLocation("nightmare:textures/entity/zombiePigmanHell.png");

    @Inject(method = "func_110863_a", at = @At("HEAD"),cancellable = true)
    private void manageEclipsedTextures(EntityZombie zomb, CallbackInfoReturnable<ResourceLocation> cir){
        if(NMUtils.getIsMobEclipsed(zomb)){
            if (zomb instanceof EntityPigZombie) {
                cir.setReturnValue(PIG_ECLIPSE);
            } else{
                cir.setReturnValue(ZOMBIE_ECLIPSE);
            }
            return;
        }
        if(NMEvents.SimpleEvent.HELL.isActive()){
            if (zomb instanceof EntityPigZombie) {
                cir.setReturnValue(PIGMAN_HELL);
            }
        }
    }
}
