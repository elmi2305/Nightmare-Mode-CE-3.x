package com.itlesports.nightmaremode.rendering.entities;

import com.itlesports.nightmaremode.entity.outer.EntityAngelGhast;
import net.minecraft.src.EntityGhast;
import net.minecraft.src.RenderGhast;
import net.minecraft.src.ResourceLocation;
import org.spongepowered.asm.mixin.Unique;

public class RenderOuterGhast extends RenderGhast {
    private static final ResourceLocation ANGEL_GHAST = new ResourceLocation("nightmare:textures/entity/outer/ifhyAngelGhast.png");
    private static final ResourceLocation ANGEL_GHAST_FIRING = new ResourceLocation("nightmare:textures/entity/outer/ifhyAngelGhastCharging.png");

    private static final ResourceLocation ACID = new ResourceLocation("nightmare:textures/entity/outer/ifhyAcidGhast.png");
    private static final ResourceLocation ACID_FIRING = new ResourceLocation("nightmare:textures/entity/outer/ifhyAcidGhastShooting.png");

    @Override
    protected ResourceLocation func_110867_a(EntityGhast ghast) {
        boolean angel = ghast instanceof EntityAngelGhast;
        if (ghast.func_110182_bF()) return angel ? ANGEL_GHAST_FIRING : ACID_FIRING;
        return angel ? ANGEL_GHAST : ACID;
    }
}
