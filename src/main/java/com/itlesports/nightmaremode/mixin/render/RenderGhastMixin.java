package com.itlesports.nightmaremode.mixin.render;

import com.itlesports.nightmaremode.util.NMUtils;
import com.itlesports.nightmaremode.entity.variants.EntityCreeperGhast;
import com.itlesports.nightmaremode.entity.variants.EntityAshGhast;
import com.itlesports.nightmaremode.entity.variants.EntitySiegeGhast;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderGhast.class)
public class RenderGhastMixin {
    @Unique private static final ResourceLocation GHAST_ECLIPSE = new ResourceLocation("nightmare:textures/entity/ghastEclipse.png");
    @Unique private static final ResourceLocation GHAST_CREEPER = new ResourceLocation("nightmare:textures/entity/ghastCreeper.png");
    @Unique private static final ResourceLocation ASH_GHAST = new ResourceLocation("nightmare:textures/entity/ifhyAshGhast.png");
    @Unique private static final ResourceLocation ASH_GHAST_FIRING = new ResourceLocation("nightmare:textures/entity/ifhyAshGhastCharging.png");
    @Unique private static final ResourceLocation SIEGE_GHAST = new ResourceLocation("nightmare:textures/entity/ifhySiegeGhast.png");
    @Unique private static final ResourceLocation SIEGE_GHAST_FIRING = new ResourceLocation("nightmare:textures/entity/ifhySiegeGhastCharging.png");

    @Inject(method = "func_110867_a", at = @At("HEAD"),cancellable = true)
    private void eclipseTextures(EntityGhast par1, CallbackInfoReturnable<ResourceLocation> cir) {
        if (par1 instanceof EntityAshGhast) {
            if (par1.func_110182_bF()) {
                cir.setReturnValue(ASH_GHAST_FIRING);
                return;
            }
            cir.setReturnValue(ASH_GHAST);
            return;
        }
        if (par1 instanceof EntitySiegeGhast) {
            if (par1.func_110182_bF()) {
                cir.setReturnValue(SIEGE_GHAST_FIRING);
                return;
            }
            cir.setReturnValue(SIEGE_GHAST);
            return;
        }
        if (par1 instanceof EntityCreeperGhast) {
            cir.setReturnValue(GHAST_CREEPER);
            return;
        }
        if (NMUtils.getIsMobEclipsed(par1)) {
            cir.setReturnValue(GHAST_ECLIPSE);
        }
    }
}
