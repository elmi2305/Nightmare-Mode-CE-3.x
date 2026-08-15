package com.itlesports.nightmaremode.rendering.entities;

import com.itlesports.nightmaremode.entity.outer.EntityAngelGhast;
import net.minecraft.src.EntityGhast;
import net.minecraft.src.RenderGhast;
import net.minecraft.src.ResourceLocation;

public class RenderOuterGhast extends RenderGhast {
    private static final ResourceLocation ANGEL = new ResourceLocation("nightmare:textures/entity/outer/angel_ghast.png");
    private static final ResourceLocation ANGEL_SHOOTING = new ResourceLocation("nightmare:textures/entity/outer/angel_ghast_shooting.png");
    private static final ResourceLocation ACID = new ResourceLocation("nightmare:textures/entity/outer/acid_ghast.png");
    private static final ResourceLocation ACID_SHOOTING = new ResourceLocation("nightmare:textures/entity/outer/acid_ghast_shooting.png");

    @Override
    protected ResourceLocation func_110867_a(EntityGhast ghast) {
        boolean angel = ghast instanceof EntityAngelGhast;
        if (ghast.func_110182_bF()) return angel ? ANGEL_SHOOTING : ACID_SHOOTING;
        return angel ? ANGEL : ACID;
    }
}
