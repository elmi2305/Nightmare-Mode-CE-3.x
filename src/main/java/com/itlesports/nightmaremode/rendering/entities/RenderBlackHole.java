package com.itlesports.nightmaremode.rendering.entities;

import com.itlesports.nightmaremode.entity.underworld.EntityBlackHole;
import com.itlesports.nightmaremode.rendering.entities.models.ModelBlackHole;
import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;
import net.minecraft.src.*;

public class RenderBlackHole extends Render {

    private final ModelBlackHole model = new ModelBlackHole();

    public RenderBlackHole() {
        this.shadowSize = 0.0F;
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        EntityBlackHole hole = (EntityBlackHole) entity;

        GL11.glPushMatrix();
        GL11.glTranslatef((float)x, (float)y + 0.9F, (float)z);

        float pulse = 1.0F + (float)Math.sin(hole.ticksExisted * 0.12F) * 0.12F;
        GL11.glScalef(pulse, pulse, pulse);

        GL11.glColor4f(0.005F, 0.0F, 0.01F, 1.0F);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_TEXTURE_2D);

        GL11.glRotatef(hole.ticksExisted * 2.2F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(35.0F, 1.0F, 0.2F, 0.8F);

        this.model.render(0.0625F);

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPopMatrix();
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return null;
    }
}