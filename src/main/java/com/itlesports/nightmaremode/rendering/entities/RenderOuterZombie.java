package com.itlesports.nightmaremode.rendering.entities;

import com.itlesports.nightmaremode.entity.outer.EntityIceZombie;
import net.minecraft.src.EntityZombie;
import net.minecraft.src.RenderZombie;
import net.minecraft.src.ResourceLocation;

public class RenderOuterZombie extends RenderZombie {
    private static final ResourceLocation MUMMY = new ResourceLocation("nightmare:textures/entity/outer/ifhyMummyZombie.png");
    private static final ResourceLocation ICE = new ResourceLocation("nightmare:textures/entity/outer/ifhyIceZombie.png");

    @Override
    protected ResourceLocation func_110863_a(EntityZombie zombie) {
        return zombie instanceof EntityIceZombie ? ICE : MUMMY;
    }
}
