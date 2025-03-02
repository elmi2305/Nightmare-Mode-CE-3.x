package com.itlesports.nightmaremode.mixin.render;

import com.itlesports.nightmaremode.NightmareUtils;
import com.itlesports.nightmaremode.entity.EntityStoneZombie;
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
    @Unique private static final ResourceLocation ZOMBIE_TEXTURE_ECLIPSE = new ResourceLocation("textures/entity/zombieEclipseHigh.png");
    @Unique private static final ResourceLocation PIGMAN_TEXTURE_ECLIPSE = new ResourceLocation("textures/entity/zombiePigmanEclipse.png");
    @Unique private static final ResourceLocation ZOMBIE_TEXTURE_STONE = new ResourceLocation("textures/entity/zombieStone.png");

    @Inject(method = "func_110863_a", at = @At("HEAD"),cancellable = true)
    private void manageEclipsedTextures(EntityZombie par1EntityZombie, CallbackInfoReturnable<ResourceLocation> cir){
        if(par1EntityZombie instanceof EntityStoneZombie){
            cir.setReturnValue(ZOMBIE_TEXTURE_STONE);
        } else if(NightmareUtils.getIsMobEclipsed(par1EntityZombie)){
            if (par1EntityZombie instanceof EntityPigZombie) {
                cir.setReturnValue(PIGMAN_TEXTURE_ECLIPSE);
            } else{
                cir.setReturnValue(ZOMBIE_TEXTURE_ECLIPSE);
            }
        }
    }
}
