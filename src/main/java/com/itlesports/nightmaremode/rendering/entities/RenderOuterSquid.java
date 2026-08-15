package com.itlesports.nightmaremode.rendering.entities;

import btw.client.render.entity.SquidRenderer;
import com.itlesports.nightmaremode.entity.outer.EntityAcidSquid;
import com.itlesports.nightmaremode.entity.outer.EntityAngelSquid;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityLivingBase;
import net.minecraft.src.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderOuterSquid extends SquidRenderer {
    private static final ResourceLocation ANGEL = new ResourceLocation("nightmare:textures/entity/outer/angel_squid.png");
    private static final ResourceLocation ACID = new ResourceLocation("nightmare:textures/entity/outer/acid_squid.png");

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return entity instanceof EntityAngelSquid ? ANGEL : ACID;
    }

    @Override
    protected void preRenderCallback(EntityLivingBase entity, float partialTicks) {
        if (entity instanceof EntityAngelSquid) GL11.glScalef(1.2F, 1.2F, 1.2F);
    }
}
