package com.itlesports.nightmaremode;

import net.minecraft.src.EntityPigZombie;
import net.minecraft.src.EntityZombie;
import net.minecraft.src.RenderZombie;
import net.minecraft.src.ResourceLocation;

public class RenderShadowZombie extends RenderZombie {
    private static final ResourceLocation shadowZombieTextures = new ResourceLocation("textures/entity/shadowzombie.png");

    @Override protected ResourceLocation func_110863_a(EntityZombie par1EntityZombie) {
        return shadowZombieTextures;
    }
}
