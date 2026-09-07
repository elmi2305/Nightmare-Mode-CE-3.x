package com.itlesports.nightmaremode.rendering.entities;

import net.minecraft.src.EntityIronGolem;
import net.minecraft.src.RenderIronGolem;
import net.minecraft.src.ResourceLocation;

public class RenderIceGolem extends RenderIronGolem {
    private static final ResourceLocation ICE = new ResourceLocation("nightmare:textures/entity/outer/ifhyIceGolem.png");

    @Override
    protected ResourceLocation getIronGolemTextures(EntityIronGolem golem) {
        return ICE;
    }
}
