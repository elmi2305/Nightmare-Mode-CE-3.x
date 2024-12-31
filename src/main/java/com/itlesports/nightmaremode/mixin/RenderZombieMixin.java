package com.itlesports.nightmaremode.mixin;

import com.itlesports.nightmaremode.NightmareUtils;
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
    @Unique
    private static final ResourceLocation ZOMBIE_TEXTURE_ECLIPSE = new ResourceLocation("textures/entity/zombieEclipseHigh.png");

    @Inject(method = "func_110863_a", at = @At("HEAD"),cancellable = true)
    private void manageEclipsedTextures(EntityZombie par1EntityZombie, CallbackInfoReturnable<ResourceLocation> cir){
        if(NightmareUtils.getIsEclipse() && !(par1EntityZombie instanceof EntityPigZombie)){
            cir.setReturnValue(ZOMBIE_TEXTURE_ECLIPSE);
        }
    }
}
