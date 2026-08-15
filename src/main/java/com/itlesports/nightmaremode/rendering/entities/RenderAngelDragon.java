package com.itlesports.nightmaremode.rendering.entities;

import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;

public class RenderAngelDragon extends RenderLiving {
    private static final ResourceLocation TEXTURE = new ResourceLocation("nightmare:textures/entity/outer/angel_dragon.png");

    public RenderAngelDragon() {
        super(new ModelDragon(0.0F), 1.0F);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return TEXTURE;
    }

    @Override
    protected void rotateCorpse(EntityLivingBase entity, float age, float yaw, float partialTicks) {
        GL11.glRotatef(180.0F - yaw, 0.0F, 1.0F, 0.0F);
    }

    @Override
    protected void preRenderCallback(EntityLivingBase entity, float partialTicks) {
        GL11.glScalef(0.72F, 0.72F, 0.72F);
    }
}
