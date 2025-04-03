package com.itlesports.nightmaremode.rendering;

import com.itlesports.nightmaremode.NightmareUtils;
import net.minecraft.src.EntityZombie;
import net.minecraft.src.RenderZombie;
import net.minecraft.src.ResourceLocation;

public class RenderShadowZombie extends RenderZombie {
    private static final ResourceLocation shadowZombieTextures = new ResourceLocation("textures/entity/shadowzombie.png");
    private static final ResourceLocation shadowZombieTexturesEclipse = new ResourceLocation("textures/entity/shadowzombieEclipse.png");

    @Override protected ResourceLocation func_110863_a(EntityZombie par1EntityZombie) {
        return NightmareUtils.getIsMobEclipsed(par1EntityZombie) ? shadowZombieTexturesEclipse : shadowZombieTextures;
    }
}
