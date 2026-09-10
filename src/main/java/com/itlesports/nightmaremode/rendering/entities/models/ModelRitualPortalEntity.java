package com.itlesports.nightmaremode.rendering.entities.models;

import net.minecraft.src.Entity;
import net.minecraft.src.ModelBase;
import net.minecraft.src.ModelRenderer;

public class ModelRitualPortalEntity extends ModelBase {

    private static final int SPIKE_COUNT = 8;

    private static final int TENDRIL_THICKNESS = 1;

    private static final int TENDRIL_LENGTH = 16;

    public final ModelRenderer core;

    public final ModelRenderer coreInner;

    public final ModelRenderer[] bulges = new ModelRenderer[4];

    public final ModelRenderer crownRing;

    public final ModelRenderer[] spikes = new ModelRenderer[SPIKE_COUNT];

    public final ModelRenderer[] shards = new ModelRenderer[4];

    public final ModelRenderer[] tendrils = new ModelRenderer[6];

    public final ModelRenderer beamSocket;

    public ModelRitualPortalEntity() {
        textureWidth  = 128;
        textureHeight = 128;

        beamSocket = new ModelRenderer(this, 24, 48);
        beamSocket.addBox(-3f, 0f, -3f, 6, 6, 6);
        beamSocket.setRotationPoint(0f, 2f, 0f);

        core = new ModelRenderer(this, 0, 0);
        core.addBox(-5f, -10f, -5f, 10, 20, 10);
        core.setRotationPoint(0f, -4f, 0f);

        bulges[0] = new ModelRenderer(this, 0, 30);
        bulges[0].addBox(-8f, -6f, -8f, 6, 12, 6);
        bulges[0].setRotationPoint(0f, 0f, 0f);
        core.addChild(bulges[0]);

        bulges[1] = new ModelRenderer(this, 24, 30);
        bulges[1].addBox(2f, -4f, 2f, 6, 10, 6);
        bulges[1].setRotationPoint(0f, 0f, 0f);
        core.addChild(bulges[1]);

        bulges[2] = new ModelRenderer(this, 0, 30);
        bulges[2].addBox(2f, -5f, -8f, 5, 10, 5);
        bulges[2].setRotationPoint(0f, 0f, 0f);
        core.addChild(bulges[2]);

        bulges[3] = new ModelRenderer(this, 24, 30);
        bulges[3].addBox(-8f, -7f, 2f, 5, 12, 5);
        bulges[3].setRotationPoint(0f, 0f, 0f);
        core.addChild(bulges[3]);

        coreInner = new ModelRenderer(this, 0, 48);
        coreInner.addBox(-3f, -7f, -3f, 6, 14, 6);
        coreInner.setRotationPoint(0f, 0f, 0f);
        core.addChild(coreInner);

        crownRing = new ModelRenderer(this, 0, 0);
        crownRing.setRotationPoint(0f, -4f, 0f);

        addRingFin(crownRing,  -1f,  -2f,  -8f,  3, 4, 3,  0, 79);
        addRingFin(crownRing,  -1f,  -2f,   5f,  3, 4, 3, 12, 79);
        addRingFin(crownRing,  -8f,  -2f,  -1f,  3, 4, 3, 24, 79);
        addRingFin(crownRing,   5f,  -2f,  -1f,  3, 4, 3, 36, 79);

        spikes[0] = new ModelRenderer(this, 0, 68);
        spikes[0].addBox(-1f, -1f, -9f, 2, 2, 9);
        spikes[0].setRotationPoint(0f, -2f, -5f);
        core.addChild(spikes[0]);

        spikes[1] = new ModelRenderer(this, 0, 68);
        spikes[1].addBox(-1f, -1f, 0f, 2, 2, 9);
        spikes[1].setRotationPoint(0f, -2f, 5f);
        core.addChild(spikes[1]);

        spikes[2] = new ModelRenderer(this, 22, 68);
        spikes[2].addBox(-9f, -1f, -1f, 9, 2, 2);
        spikes[2].setRotationPoint(-5f, -2f, 0f);
        core.addChild(spikes[2]);

        spikes[3] = new ModelRenderer(this, 22, 68);
        spikes[3].addBox(0f, -1f, -1f, 9, 2, 2);
        spikes[3].setRotationPoint(5f, -2f, 0f);
        core.addChild(spikes[3]);

        spikes[4] = new ModelRenderer(this, 0, 68);
        spikes[4].addBox(-1f, -9f, -1f, 2, 9, 2);
        spikes[4].setRotationPoint(0f, -10f, 0f);
        core.addChild(spikes[4]);

        spikes[5] = new ModelRenderer(this, 22, 68);
        spikes[5].addBox(-1f, 0f, -1f, 2, 7, 2);
        spikes[5].setRotationPoint(0f, 10f, 0f);
        core.addChild(spikes[5]);

        spikes[6] = new ModelRenderer(this, 0, 68);
        spikes[6].addBox(-1f, -1f, -7f, 2, 2, 7);
        spikes[6].setRotationPoint(4f, -2f, -4f);
        spikes[6].rotateAngleY = rad(-45);
        core.addChild(spikes[6]);

        spikes[7] = new ModelRenderer(this, 22, 68);
        spikes[7].addBox(-1f, -1f, 0f, 2, 2, 7);
        spikes[7].setRotationPoint(-4f, -2f, 4f);
        spikes[7].rotateAngleY = rad(-45);
        core.addChild(spikes[7]);

        shards[0] = new ModelRenderer(this, 0, 86);
        shards[0].addBox(-1f, -7f, -1f, 2, 14, 2);
        shards[0].setRotationPoint(7f, -4f, 7f);
        shards[0].rotateAngleY = rad(25);

        shards[1] = new ModelRenderer(this, 8, 86);
        shards[1].addBox(-1f, -6f, -1f, 2, 12, 2);
        shards[1].setRotationPoint(6f, -4f, -6f);
        shards[1].rotateAngleY = rad(-40);
        shards[1].rotateAngleX = rad(12);

        shards[2] = new ModelRenderer(this, 16, 86);
        shards[2].addBox(-1f, -8f, -1f, 2, 16, 2);
        shards[2].setRotationPoint(-7f, -4f, 5f);
        shards[2].rotateAngleX = rad(-8);

        shards[3] = new ModelRenderer(this, 24, 86);
        shards[3].addBox(-1f, -5f, -1f, 2, 10, 2);
        shards[3].setRotationPoint(-5f, -4f, -7f);
        shards[3].rotateAngleY = rad(55);
        shards[3].rotateAngleZ = rad(-8);

        int w = TENDRIL_THICKNESS;
        int l = TENDRIL_LENGTH;

        tendrils[0] = new ModelRenderer(this, 0, 106);
        tendrils[0].addBox(-w/2f, 0f, -w/2f, w, l, w);
        tendrils[0].setRotationPoint(-3f, 9f, -3f);
        tendrils[0].rotateAngleX = rad(20);
        core.addChild(tendrils[0]);

        tendrils[1] = new ModelRenderer(this, 8, 106);
        tendrils[1].addBox(-w/2f, 0f, -w/2f, w, l - 3, w);
        tendrils[1].setRotationPoint(3f, 9f, 3f);
        tendrils[1].rotateAngleX = rad(-12);
        tendrils[1].rotateAngleZ = rad(8);
        core.addChild(tendrils[1]);

        tendrils[2] = new ModelRenderer(this, 16, 106);
        tendrils[2].addBox(-w/2f, 0f, -w/2f, w, l - 5, w);
        tendrils[2].setRotationPoint(3f, 9f, -4f);
        tendrils[2].rotateAngleZ = rad(-15);
        core.addChild(tendrils[2]);

        tendrils[3] = new ModelRenderer(this, 0, 106);
        tendrils[3].addBox(-w/2f, 0f, -w/2f, w, l - 1, w);
        tendrils[3].setRotationPoint(-4f, 9f, 3f);
        tendrils[3].rotateAngleX = rad(-15);
        tendrils[3].rotateAngleZ = rad(-10);
        core.addChild(tendrils[3]);

        tendrils[4] = new ModelRenderer(this, 8, 106);
        tendrils[4].addBox(-w/2f, 0f, -w/2f, w, l - 2, w);
        tendrils[4].setRotationPoint(0f, 9f, -4f);
        tendrils[4].rotateAngleX = rad(25);
        core.addChild(tendrils[4]);

        tendrils[5] = new ModelRenderer(this, 16, 106);
        tendrils[5].addBox(-w/2f, 0f, -w/2f, w, l - 4, w);
        tendrils[5].setRotationPoint(0f, 9f, 4f);
        tendrils[5].rotateAngleX = rad(-20);
        core.addChild(tendrils[5]);
    }

    @Override
    public void render(Entity entity,
                       float limbSwing, float limbSwingAmount,
                       float ageInTicks, float netHeadYaw,
                       float headPitch, float scale) {
        beamSocket.render(scale);
        core.render(scale);
        crownRing.render(scale);
        for (ModelRenderer shard : shards) {
            shard.render(scale);
        }
    }

    private static float rad(double degrees) {
        return (float) Math.toRadians(degrees);
    }

    private void addRingFin(ModelRenderer parent,
                            float x, float y, float z,
                            int w, int h, int d,
                            int u, int v) {
        ModelRenderer fin = new ModelRenderer(this, u, v);
        fin.addBox(x, y, z, w, h, d);
        fin.setRotationPoint(0f, 0f, 0f);
        parent.addChild(fin);
    }
}
