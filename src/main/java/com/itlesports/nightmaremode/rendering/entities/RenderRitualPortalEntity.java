package com.itlesports.nightmaremode.rendering.entities;

import com.itlesports.nightmaremode.entity.underworld.EntityRitualPortal;
import com.itlesports.nightmaremode.rendering.entities.models.ModelRitualPortalEntity;
import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;

public class RenderRitualPortalEntity extends net.minecraft.src.RenderLiving {

    private static final ResourceLocation TEX_BODY =
            new ResourceLocation("textures/entity/ritual_portal.png");

    // Glow texture: same UV layout as body, only inner core regions painted.
    // Rest transparent. Used in additive pass — paint only what should pulse.
    private static final ResourceLocation TEX_GLOW =
            new ResourceLocation("textures/entity/ritual_portal_glow.png");

    private static final float SCALE = 1.0f / 16.0f;

    // Mirror baked rest-pose angles from ModelRitualPortalEntity.
    // Re-apply base + oscillation each frame — no drift accumulation.
    private static final float[] SHARD_BASE_Y = { rad(25),  rad(-40), 0f,       rad(55)  };
    private static final float[] SHARD_BASE_X = { 0f,       rad(12),  rad(-8),  0f       };
    private static final float[] SHARD_BASE_Z = { 0f,       0f,       0f,       rad(-8)  };

    private static final float[] TENDRIL_BASE_X = { rad(20), rad(-12), 0f       };
    private static final float[] TENDRIL_BASE_Z = { 0f,      rad(8),   rad(-15) };

    public RenderRitualPortalEntity() {
        super(new ModelRitualPortalEntity(), 0.5f);
    }

    // ── Entry point ────────────────────────────────────────────────────────

    public void renderPortalEntity(EntityRitualPortal entity, double x, double y, double z,
                                   float yaw, float partial) {
        animateModel(entity.ticksExisted + partial);
        super.doRenderLiving(entity, x, y, z, yaw, partial);
    }

    // ── Pre-render: GL state for shadowy look ─────────────────────────────

    @Override
    protected void preRenderCallback(EntityLivingBase entity, float partial) {
        GL11.glDisable(2896);        // GL_LIGHTING — flat dark, bypasses world light
        GL11.glEnable(3042);         // GL_BLEND
        GL11.glBlendFunc(770, 771);  // SRC_ALPHA, ONE_MINUS_SRC_ALPHA
        GL11.glColor4f(0.12f, 0.04f, 0.20f, 0.97f);
    }

    // ── Render passes ─────────────────────────────────────────────────────

    @Override
    protected int shouldRenderPass(EntityLivingBase entity, int pass, float partial) {
        if (pass == 1) {
            // Additive inner glow pulse — purple, low alpha, slightly scaled out
            float t = entity.ticksExisted + partial;
            float pulse = 0.08f + MathHelper.cos(t * 0.055f) * 0.06f;  // 0.02 – 0.14
            this.bindTexture(TEX_GLOW);
            GL11.glEnable(3042);      // GL_BLEND
            GL11.glBlendFunc(1, 1);   // ONE, ONE — additive: visible on dark sky/stone
            GL11.glDepthMask(false);
            GL11.glColor4f(0.50f, 0.05f, 0.90f, pulse);
            GL11.glScalef(1.06f, 1.06f, 1.06f);  // halo slightly outside body surface
            this.setRenderPassModel(this.mainModel);
            return 1;
        }
        if (pass == 2) {
            // Restore state after glow pass
            GL11.glDepthMask(true);
            GL11.glDisable(3042);     // GL_BLEND
            GL11.glEnable(2896);      // GL_LIGHTING
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        }
        return -1;
    }

    @Override
    protected int inheritRenderPass(EntityLivingBase entity, int pass, float partial) {
        return -1;
    }

    // ── Animation ─────────────────────────────────────────────────────────

    private void animateModel(float t) {
        ModelRitualPortalEntity m = (ModelRitualPortalEntity) this.mainModel;

        // Core: primary Y spin + vertical bob
        float coreRotY = t * 0.018f;
        float coreBobY = MathHelper.sin(t * 0.040f) * 0.12f;

        m.core.rotateAngleY   = coreRotY;
        m.core.rotationPointY = -4.0f + coreBobY;  // -4 = base hover from model ctor

        // Core inner: counter-spin relative to core parent + tilt wobble
        // world rotY = core.rotY + inner.rotY → set inner.rotY = -core.rotY * 0.6
        // so inner appears to spin at 40% of core speed in world space
        m.coreInner.rotateAngleY = -coreRotY * 0.60f;
        m.coreInner.rotateAngleX = MathHelper.sin(t * 0.060f) * 0.08f;

        // Crown ring: independent counter-spin, own tilt phase
        m.crownRing.rotateAngleY = -coreRotY * 0.40f;
        m.crownRing.rotateAngleX = MathHelper.sin(t * 0.050f + (float) Math.PI) * 0.05f;

        // Spikes: phase-offset breathing flare — children of core, spin with it
        // N/S (0,1): flare on rotX;  E/W (2,3): flare on rotZ
        for (int i = 0; i < 4; i++) {
            float wave = MathHelper.sin(t * 0.080f + i * 1.5708f) * 0.28f;
            if (i < 2) m.spikes[i].rotateAngleX = wave;
            else       m.spikes[i].rotateAngleZ = wave;
        }

        // Shards: lean wobble + slow Y drift on top of baked rest pose
        for (int i = 0; i < 4; i++) {
            m.shards[i].rotateAngleX = SHARD_BASE_X[i]
                    + MathHelper.sin(t * 0.050f + i * 1.30f) * 0.06f;
            m.shards[i].rotateAngleY = SHARD_BASE_Y[i]
                    + MathHelper.sin(t * 0.030f + i * 0.90f) * 0.04f;
            m.shards[i].rotateAngleZ = SHARD_BASE_Z[i];
        }

        // Tendrils: sway on X and Z with per-tendril phase offset
        for (int i = 0; i < 3; i++) {
            m.tendrils[i].rotateAngleX = TENDRIL_BASE_X[i]
                    + MathHelper.sin(t * 0.100f + i * 1.20f) * 0.20f;
            m.tendrils[i].rotateAngleZ = TENDRIL_BASE_Z[i]
                    + MathHelper.cos(t * 0.070f + i * 0.90f) * 0.10f;
        }
    }

    // ── Texture + delegate boilerplate ────────────────────────────────────

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