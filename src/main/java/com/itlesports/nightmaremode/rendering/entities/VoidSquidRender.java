package com.itlesports.nightmaremode.rendering.entities;

import btw.entity.model.SquidTentacleModel;
import com.itlesports.nightmaremode.entity.underworld.EntityVoidSquid;
import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;

public class VoidSquidRender extends RenderLiving {
    private static final ResourceLocation SQUID_TEXTURES = new ResourceLocation("nightmare:textures/entity/squidEclipse.png");
    SquidTentacleModel tentacleAttackModel = new SquidTentacleModel();

    public VoidSquidRender() {
        super(new ModelSquid(), 0.7f);
    }

    @Override
    protected float handleRotationFloat(EntityLivingBase entity, float par2) {
        EntityVoidSquid squid = (EntityVoidSquid)entity;
        return squid.prevTentacleAngle + (squid.tentacleAngle - squid.prevTentacleAngle) * par2;
    }

    @Override
    protected void rotateCorpse(EntityLivingBase entity, float par2, float par3, float par4) {
        EntityVoidSquid squid = (EntityVoidSquid)entity;
        float var5 = squid.prevSquidPitch + (squid.squidPitch - squid.prevSquidPitch) * par4;
        float var6 = squid.prevSquidYaw + (squid.squidYaw - squid.prevSquidYaw) * par4;
        GL11.glTranslatef(0.0f, 0.5f, 0.0f);
        GL11.glRotatef(180.0f - par3, 0.0f, 1.0f, 0.0f);
        GL11.glRotatef(var5, 1.0f, 0.0f, 0.0f);
        GL11.glRotatef(var6, 0.0f, 1.0f, 0.0f);
        GL11.glTranslatef(0.0f, -1.2f, 0.0f);
    }

    @Override
    public void doRenderLiving(EntityLiving par1EntityLiving, double par2, double par4, double par6, float par8, float par9) {
        super.doRenderLiving(par1EntityLiving, par2, par4, par6, par8, par9);
    }

    @Override
    public void doRender(Entity entity, double par2, double par4, double par6, float par8, float par9) {
        super.doRenderLiving((EntityLiving)entity, par2, par4, par6, par8, par9);
        this.renderTentacleAttack((EntityVoidSquid)entity, par2, par4, par6, par8, par9);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity var1) {
        return SQUID_TEXTURES;
    }

    public void renderTentacleAttack(EntityVoidSquid squid, double dRenderX, double dRenderY, double dRenderZ, float par8, float dPartialTick) {
        int iAttackProgressCounter = squid.tentacleAttackInProgressCounter;
        if (iAttackProgressCounter > 0) {
            float fPartialAttackProgress = (float)(iAttackProgressCounter - 1) + dPartialTick;
            Vec3 worldTipPos = squid.computeTentacleAttackTip(fPartialAttackProgress);
            if (squid.isHeadCrab()) {
                dRenderY -= (double)(squid.height * 2.0f / 3.0f);
            }
            double dLocalSourcePosX = dRenderX;
            double dLocalSourcePosY = dRenderY;
            double dLocalSourcePosZ = dRenderZ;
            if (!squid.isHeadCrab()) {
                dLocalSourcePosY += (double)(squid.height / 2.0f);
            }
            double dLocalTipPosX = worldTipPos.xCoord - squid.posX + dRenderX;
            double dLocalTipPosY = worldTipPos.yCoord - squid.posY + dRenderY;
            double dLocalTipPosZ = worldTipPos.zCoord - squid.posZ + dRenderZ;
            double dDeltaX = dLocalTipPosX - dLocalSourcePosX;
            double dDeltaY = dLocalTipPosY - dLocalSourcePosY;
            double dDeltaZ = dLocalTipPosZ - dLocalSourcePosZ;
            double dTentacleLength = MathHelper.sqrt_double(dDeltaX * dDeltaX + dDeltaY * dDeltaY + dDeltaZ * dDeltaZ);
            double dFlatTentacleLength = MathHelper.sqrt_double(dDeltaX * dDeltaX + dDeltaZ * dDeltaZ);
            Tessellator tesslator = Tessellator.instance;
            GL11.glPushMatrix();
            GL11.glDisable(2884);
            GL11.glTranslatef((float)dRenderX, (float)dRenderY, (float)dRenderZ);
            GL11.glEnable(32826);
            this.bindTexture(SQUID_TEXTURES);
            float fTentacleYaw = (float)(Math.atan2(dDeltaX, dDeltaZ) * 180.0 / Math.PI);
            float fTentaclePitch = (float)(Math.atan2(dFlatTentacleLength, dDeltaY) * 180.0 / Math.PI);
            float fScaleTentacleWidth = (float)(1.0 - 0.6 * squid.getAttackProgressSin(fPartialAttackProgress));
            if (fScaleTentacleWidth > 1.0f) {
                fScaleTentacleWidth = 1.0f;
            }
            float fScaleTentacleLength = (float)dTentacleLength;
            this.tentacleAttackModel.render(squid, fScaleTentacleWidth, fScaleTentacleLength, fScaleTentacleWidth, fTentacleYaw, fTentaclePitch, 0.0625f);
            GL11.glPopMatrix();
        }
    }
}
