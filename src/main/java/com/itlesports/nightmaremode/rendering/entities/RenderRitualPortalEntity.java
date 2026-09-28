package com.itlesports.nightmaremode.rendering.entities;

import com.itlesports.nightmaremode.entity.underworld.EntityRitualPortal;
import com.itlesports.nightmaremode.rendering.entities.models.ModelRitualPortalEntity;
import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;

public class RenderRitualPortalEntity extends net.minecraft.src.RenderLiving {

    private static final ResourceLocation TEX_BODY = new ResourceLocation("nightmare:textures/entity/nmRitualPortal.png");

    private static final ResourceLocation TEX_GLOW = new ResourceLocation("nightmare:textures/entity/nmRitualPortalGlow.png");

    private static final float SCALE = 1.0f / 16.0f;

    private static final float[] SHARD_BASE_Y = { rad(25),  rad(-40), 0f,       rad(55)  };
    private static final float[] SHARD_BASE_X = { 0f,       rad(12),  rad(-8),  0f       };
    private static final float[] SHARD_BASE_Z = { 0f,       0f,       0f,       rad(-8)  };

    private static final float[] TENDRIL_BASE_X = { rad(20), rad(-12), 0f,       rad(-15), rad(25), rad(-20) };
    private static final float[] TENDRIL_BASE_Z = { 0f,      rad(8),   rad(-15), rad(-10), 0f,      0f       };

    public RenderRitualPortalEntity() {
        super(new ModelRitualPortalEntity(), 0.5f);
    }

    public void renderPortalEntity(EntityRitualPortal entity, double x, double y, double z,
                                   float yaw, float partial) {
        animateModel(entity, entity.ticksExisted + partial);

        super.doRenderLiving(entity, x, y - 5, z, yaw, partial);
    }

    @Override
    protected void preRenderCallback(EntityLivingBase entity, float partial) {
        EntityRitualPortal portal = (EntityRitualPortal) entity;
        float growthScale = 4 + portal.getGrowthScale();

        float anger = portal.getAngerLevel();

        float r = 0.27f + anger * 0.22f;
        float g = 0.09f + anger * 0.08f;
        float b = 0.32f - anger * 0.13f;

        GL11.glDisable(2896);
        GL11.glEnable(3042);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(r, g, b, 0.97f);
        GL11.glScalef(growthScale, growthScale, growthScale);
    }

    @Override
    protected int shouldRenderPass(EntityLivingBase entity, int pass, float partial) {
        EntityRitualPortal portal = (EntityRitualPortal) entity;
        float anger = portal.getAngerLevel();

        if (pass == 1) {

            float t = entity.ticksExisted + partial;
            float pulse = 0.08f + MathHelper.cos(t * 0.055f) * 0.06f + anger * 0.15f;

            float r = 0.50f + anger * 0.50f;
            float g = 0.05f + anger * 0.20f;
            float b = 0.90f - anger * 0.70f;

            this.bindTexture(TEX_GLOW);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glDepthMask(false);
            GL11.glColor4f(r, g, b, pulse);
            float glowScale = 1.06f + anger * 0.15f;
            GL11.glScalef(glowScale, glowScale, glowScale);
            this.setRenderPassModel(this.mainModel);
            return 1;
        }
        if (pass == 2) {

            GL11.glDepthMask(true);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        }
        return -1;
    }

    @Override
    protected int inheritRenderPass(EntityLivingBase entity, int pass, float partial) {
        return -1;
    }

    private void animateModel(EntityRitualPortal entity, float t) {
        ModelRitualPortalEntity m = (ModelRitualPortalEntity) this.mainModel;

        float volatility;
        float tendrilExt = entity.getTendrilExtension();
        float anger = entity.getAngerLevel();

        volatility = 1;

        float coreRotY = t * 0.018f * volatility;
        float coreBobY = MathHelper.sin(t * 0.040f * volatility) * (0.12f + anger * 0.15f);

        m.core.rotateAngleY   = coreRotY;
        m.core.rotationPointY = -4.0f + coreBobY;

        m.coreInner.rotateAngleY = -coreRotY * 0.60f;
        m.coreInner.rotateAngleX = MathHelper.sin(t * 0.060f * volatility) * (0.08f + anger * 0.06f);

        m.crownRing.rotateAngleY = -coreRotY * 0.40f;
        m.crownRing.rotateAngleX = MathHelper.sin(t * 0.050f * volatility + (float) Math.PI) * (0.05f + anger * 0.04f);

        for (int i = 0; i < 8; i++) {
            float wave = MathHelper.sin(t * 0.080f * volatility + i * 1.5708f) * (0.28f + anger * 0.05f);
            if (i < 2) m.spikes[i].rotateAngleX = wave;
            else if (i < 4) m.spikes[i].rotateAngleZ = wave;
            else if (i < 6) m.spikes[i].rotateAngleZ = wave;
            else {
                m.spikes[i].rotateAngleX = wave * 0.7f;
                m.spikes[i].rotateAngleZ = wave * 0.7f;
            }
        }

        for (int i = 0; i < 4; i++) {
            m.shards[i].rotateAngleX = SHARD_BASE_X[i]
                    + MathHelper.sin(t * 0.050f * volatility + i * 1.30f) * (0.06f + anger * 0.06f);
            m.shards[i].rotateAngleY = SHARD_BASE_Y[i]
                    + MathHelper.sin(t * 0.030f * volatility + i * 0.90f) * (0.04f + anger * 0.04f);
            m.shards[i].rotateAngleZ = SHARD_BASE_Z[i];
        }

        for (int i = 0; i < 6; i++) {
            float swayX = MathHelper.sin(t * 0.100f + i * 1.20f) * (0.20f + anger * 0.06f) * tendrilExt;
            float swayZ = MathHelper.cos(t * 0.070f + i * 0.90f) * (0.10f + anger * 0.06f) * tendrilExt;

            m.tendrils[i].rotateAngleX = TENDRIL_BASE_X[i] + swayX;
            m.tendrils[i].rotateAngleZ = TENDRIL_BASE_Z[i] + swayZ;
        }

        if (entity.shouldLunge()) {
            EntityPlayer nearestPlayer = getNearestPlayer(entity);
            if (nearestPlayer != null) {
                float lungeIntensity = entity.getLungeIntensity();
                float lungePhase = MathHelper.sin(t * 0.15f) * lungeIntensity;

                float dx = (float) (nearestPlayer.posX - entity.posX);
                float dz = (float) (nearestPlayer.posZ - entity.posZ);
                float angleToPlayer = (float) Math.atan2(dz, dx);

                m.core.rotateAngleY += MathHelper.wrapAngleTo180_float(angleToPlayer - coreRotY) * lungeIntensity * 0.5f;
                m.core.rotationPointY += lungePhase * 0.3f;

                for (int i = 0; i < 6; i++) {
                    m.tendrils[i].rotateAngleX += lungePhase * 0.2f;
                }
            }
        }
    }

    private EntityPlayer getNearestPlayer(EntityRitualPortal entity) {
        float minDist = 32.0f;
        return entity.worldObj.getClosestVulnerablePlayerToEntity(entity, minDist);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return TEX_BODY;
    }

    @Override
    public void doRenderLiving(EntityLiving entity, double x, double y, double z,
                               float yaw, float partial) {
        this.renderPortalEntity((EntityRitualPortal) entity, x, y, z, yaw, partial);
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z,
                         float yaw, float partial) {
        this.renderPortalEntity((EntityRitualPortal) entity, x, y, z, yaw, partial);
    }

    @Override
    public void renderPlayer(EntityLivingBase entity, double x, double y, double z,
                             float yaw, float partial) {
        this.renderPortalEntity((EntityRitualPortal) entity, x, y, z, yaw, partial);
    }

    private static float rad(double deg) {
        return (float) Math.toRadians(deg);
    }
}
