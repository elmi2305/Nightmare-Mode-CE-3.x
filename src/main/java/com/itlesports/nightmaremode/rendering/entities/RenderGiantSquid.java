package com.itlesports.nightmaremode.rendering.entities;

import btw.client.render.entity.SquidRenderer;
import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;

public final class RenderGiantSquid extends SquidRenderer {
    private static final ResourceLocation TEXTURE = new ResourceLocation("nightmare:textures/entity/nmGiantSquid.png");
    @Override protected ResourceLocation getEntityTexture(Entity entity) { return TEXTURE; }
    @Override protected void preRenderCallback(EntityLivingBase entity, float partialTicks) {
        BossStatus.setBossStatus((IBossDisplayData)entity, false);
        GL11.glScalef(3.6F, 3.6F, 3.6F);
    }
}
