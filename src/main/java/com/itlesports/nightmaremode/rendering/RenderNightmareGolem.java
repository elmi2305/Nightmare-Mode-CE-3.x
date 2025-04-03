package com.itlesports.nightmaremode.rendering;

import net.minecraft.src.EntityIronGolem;
import net.minecraft.src.RenderIronGolem;
import net.minecraft.src.ResourceLocation;

public class RenderNightmareGolem extends RenderIronGolem {
    private static final ResourceLocation NIGHTMARE_GOLEM = new ResourceLocation("textures/entity/nmGolem.png");

    @Override
    protected ResourceLocation getIronGolemTextures(EntityIronGolem entityIronGolem) {
        return NIGHTMARE_GOLEM;
    }
}
