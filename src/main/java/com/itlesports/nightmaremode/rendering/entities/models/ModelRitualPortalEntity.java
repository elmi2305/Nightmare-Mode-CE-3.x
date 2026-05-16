package com.itlesports.nightmaremode.rendering.entities.models;


import net.minecraft.src.Entity;
import net.minecraft.src.ModelBase;
import net.minecraft.src.ModelRenderer;

/**
 * Model for the Ritual Portal Entity.
 *
 * Texture: 128x128
 *
 * UV layout (u, v → w×h box footprint):
 *  Core shaft   [  0,  0] → 40×30   (box 10×20×10)
 *  Bulge 0      [  0, 30] → 24×18   (box  6×12× 6)
 *  Bulge 1      [ 24, 30] → 24×16   (box  6×10× 6)
 *  Core inner   [  0, 48] → 24×20   (box  6×14× 6)
 *  Beam socket  [ 24, 48] → 24×12   (box  6× 6× 6)
 *  Spike NS     [  0, 68] → 22×11   (box  2× 2× 9)
 *  Spike EW     [ 22, 68] → 22× 4   (box  9× 2× 2)
 *  Ring fins    [  0, 79] → 12× 7 ×4 segs (box 3×4×3)
 *  Shards       [  0, 86] →  8×16 ×4 shards (box 2×14×2 / variants)
 *  Tendrils     [  0,106] →  8×10, 8×8, 8×7
 *
 * ── ANIMATION TARGETS ─────────────────────────────────────────────────────
 *
 *  core
 *      rotateAngleY  += 0.005f * partialTick          slow primary spin
 *      rotationPointY = basePosY + sin(t*0.04)*0.15   gentle bob (in render units)
 *
 *  coreInner  (child of core — rotations are relative to core)
 *      rotateAngleY   = -coreAbsRotY * 0.6f           counter-spin: appears slower
 *      rotateAngleX   = sin(t * 0.06f) * 0.08f        tilt wobble
 *
 *  crownRing  (independent — not child of core)
 *      rotateAngleY   = -coreAbsRotY * 0.4f           slowest counter-spin
 *      rotateAngleX   = sin(t * 0.05f + PI) * 0.05f   opposite-phase tilt
 *
 *  spikes[i]  (children of core)
 *      N/S: rotateAngleX = sin(t*0.08 + i*HALF_PI) * 0.3f   flare breathing
 *      E/W: rotateAngleZ = sin(t*0.08 + i*HALF_PI) * 0.3f
 *
 *  shards[i]  (independent)
 *      rotateAngleY  += sin(t*0.03 + i) * 0.002f      slow drift (accumulate)
 *      rotateAngleX   = baseShardLean[i] + sin(t*0.05 + i*1.3)*0.06f
 *
 *  tendrils[i]  (children of core)
 *      rotateAngleX   = baseTendrilX[i] + sin(t*0.10 + i*1.2)*0.20f
 *      rotateAngleZ   = baseTendrilZ[i] + cos(t*0.07 + i*0.9)*0.10f
 */
public class ModelRitualPortalEntity extends ModelBase {


    /** Number of spikes around the core. Easy to modify for more/less spikes. */
    private static final int SPIKE_COUNT = 8;

    /** Tendril thickness (width/depth). */
    private static final int TENDRIL_THICKNESS = 1;

    /** Tendril length (height). */
    private static final int TENDRIL_LENGTH = 16;

    /** Primary body mass. ANIMATE: rotY spin, posY bob. */
    public final ModelRenderer core;

    /**
     * Inner void shadow (child of core).
     * ANIMATE: counter-rotY relative to core, slow X/Z tilt.
     */
    public final ModelRenderer coreInner;

    /**
     * Dynamic bulges that grow with ritual progress. Children of core.
     * ANIMATE: scale changes based on anger level.
     */
    public final ModelRenderer[] bulges = new ModelRenderer[4];

    /**
     * Equatorial orbit ring. Rendered independently of core.
     * ANIMATE: own rotY counter-spin, own tilt phase.
     */
    public final ModelRenderer crownRing;

    /**
     * Cardinal spines. Children of core — rotate with it.
     * ANIMATE: rotX (N/S) or rotZ (E/W) oscillation — breathing flare.
     * [0]=North(-Z)  [1]=South(+Z)  [2]=West(-X)  [3]=East(+X)
     * [4]=Up(+Y)    [5]=Down(-Y)    [6]=Diagonal NE  [7]=Diagonal SW
     */
    public final ModelRenderer[] spikes = new ModelRenderer[SPIKE_COUNT];

    /**
     * Diagonal crystal shards. NOT children of core — rendered independently.
     * Each has a rest-pose lean baked in; renderer adds oscillation on top.
     * ANIMATE: rotY drift accumulation, rotX lean wobble.
     * [0]=NE  [1]=SE  [2]=SW  [3]=NW
     */
    public final ModelRenderer[] shards = new ModelRenderer[4];

    /**
     * Bottom tendrils. Children of core — inherit spin.
     * ANIMATE: rotX/Z sway with per-tendril phase offset.
     * [0]=front-left(longest)  [1]=back-right(medium)  [2]=front-right(short)
     * [3]=back-left(long)     [4]=front-center(med)   [5]=back-center(short)
     */
    public final ModelRenderer[] tendrils = new ModelRenderer[6];


    /** Bottom anchor socket. Never animated. */
    public final ModelRenderer beamSocket;


    public ModelRitualPortalEntity() {
        textureWidth  = 128;
        textureHeight = 128;

        // Sits at entity origin. The ritual-site beam terminates here.
        // Slightly larger than the base of the core for a socketed look.
        beamSocket = new ModelRenderer(this, 24, 48);
        beamSocket.addBox(-3f, 0f, -3f, 6, 6, 6);
        beamSocket.setRotationPoint(0f, 2f, 0f);

        // Primary oblong shaft + dynamic bulge blobs.
        // Multiple overlapping boxes = jagged silhouette, reads as mass not geometry.
        //
        // rotationPoint Y=-4: entity feet at Y=0, so core hovers.
        core = new ModelRenderer(this, 0, 0);
        core.addBox(-5f, -10f, -5f, 10, 20, 10);   // central shaft
        core.setRotationPoint(0f, -4f, 0f);

        // Dynamic bulges that grow with ritual progress
        // [0]=front-left  [1]=back-right  [2]=front-right  [3]=back-left
        bulges[0] = new ModelRenderer(this, 0, 30);  // front-left
        bulges[0].addBox(-8f, -6f, -8f, 6, 12, 6);
        bulges[0].setRotationPoint(0f, 0f, 0f);
        core.addChild(bulges[0]);

        bulges[1] = new ModelRenderer(this, 24, 30); // back-right
        bulges[1].addBox(2f, -4f, 2f, 6, 10, 6);
        bulges[1].setRotationPoint(0f, 0f, 0f);
        core.addChild(bulges[1]);

        bulges[2] = new ModelRenderer(this, 0, 30);  // front-right
        bulges[2].addBox(2f, -5f, -8f, 5, 10, 5);
        bulges[2].setRotationPoint(0f, 0f, 0f);
        core.addChild(bulges[2]);

        bulges[3] = new ModelRenderer(this, 24, 30); // back-left
        bulges[3].addBox(-8f, -7f, 2f, 5, 12, 5);
        bulges[3].setRotationPoint(0f, 0f, 0f);
        core.addChild(bulges[3]);

        // Smaller box embedded inside core. Darker texture region → suggests void.
        // Renderer counter-rotates this relative to core parent rotation.
        coreInner = new ModelRenderer(this, 0, 48);
        coreInner.addBox(-3f, -7f, -3f, 6, 14, 6);
        coreInner.setRotationPoint(0f, 0f, 0f);
        core.addChild(coreInner);

        // Four flat fins arranged at cardinal offsets around the core equator
        // rotationPoint matches core hover height so counter-spin stays centered
        // Not parented to core. renderer manages rotY independently
        crownRing = new ModelRenderer(this, 0, 0); // dummy root, no own box
        crownRing.setRotationPoint(0f, -4f, 0f);

        addRingFin(crownRing,  -1f,  -2f,  -8f,  3, 4, 3,  0, 79);  // N
        addRingFin(crownRing,  -1f,  -2f,   5f,  3, 4, 3, 12, 79);  // S
        addRingFin(crownRing,  -8f,  -2f,  -1f,  3, 4, 3, 24, 79);  // W
        addRingFin(crownRing,   5f,  -2f,  -1f,  3, 4, 3, 36, 79);  // E

        // Thin elongated boxes pinned at their base (core surface)
        // Box extends outward from rotationPoint
        // Cardinal pair N/S: animate rotateAngleX ± 0.3 rad
        // Cardinal pair E/W: animate rotateAngleZ ± 0.3 rad

        spikes[0] = new ModelRenderer(this, 0, 68);  // N, -Z
        spikes[0].addBox(-1f, -1f, -9f, 2, 2, 9);
        spikes[0].setRotationPoint(0f, -2f, -5f);    // pinned at north face
        core.addChild(spikes[0]);

        spikes[1] = new ModelRenderer(this, 0, 68);  // S, +Z
        spikes[1].addBox(-1f, -1f, 0f, 2, 2, 9);
        spikes[1].setRotationPoint(0f, -2f, 5f);
        core.addChild(spikes[1]);

        spikes[2] = new ModelRenderer(this, 22, 68); // W, -X
        spikes[2].addBox(-9f, -1f, -1f, 9, 2, 2);
        spikes[2].setRotationPoint(-5f, -2f, 0f);
        core.addChild(spikes[2]);

        spikes[3] = new ModelRenderer(this, 22, 68); // E, +X
        spikes[3].addBox(0f, -1f, -1f, 9, 2, 2);
        spikes[3].setRotationPoint(5f, -2f, 0f);
        core.addChild(spikes[3]);

        spikes[4] = new ModelRenderer(this, 0, 68);  // +Y
        spikes[4].addBox(-1f, -9f, -1f, 2, 9, 2);
        spikes[4].setRotationPoint(0f, -10f, 0f);
        core.addChild(spikes[4]);

        spikes[5] = new ModelRenderer(this, 22, 68); // -Y
        spikes[5].addBox(-1f, 0f, -1f, 2, 7, 2);
        spikes[5].setRotationPoint(0f, 10f, 0f);
        core.addChild(spikes[5]);

        spikes[6] = new ModelRenderer(this, 0, 68);  // NE
        spikes[6].addBox(-1f, -1f, -7f, 2, 2, 7);
        spikes[6].setRotationPoint(4f, -2f, -4f);
        spikes[6].rotateAngleY = rad(-45);
        core.addChild(spikes[6]);

        spikes[7] = new ModelRenderer(this, 22, 68); // SW
        spikes[7].addBox(-1f, -1f, 0f, 2, 2, 7);
        spikes[7].setRotationPoint(-4f, -2f, 4f);
        spikes[7].rotateAngleY = rad(-45);
        core.addChild(spikes[7]);

        // Tall thin pillars at diagonal positions. Baked rest-pose lean angles.
        // All share UV strip [0,86]...[31,86] (8px wide each, 16+px tall)

        shards[0] = new ModelRenderer(this, 0, 86);  // NE — tallest, mild lean
        shards[0].addBox(-1f, -7f, -1f, 2, 14, 2);
        shards[0].setRotationPoint(7f, -4f, 7f);
        shards[0].rotateAngleY = rad(25);

        shards[1] = new ModelRenderer(this, 8, 86);  // SE — medium, leans forward
        shards[1].addBox(-1f, -6f, -1f, 2, 12, 2);
        shards[1].setRotationPoint(6f, -4f, -6f);
        shards[1].rotateAngleY = rad(-40);
        shards[1].rotateAngleX = rad(12);

        shards[2] = new ModelRenderer(this, 16, 86); // SW — tallest, leans back slightly
        shards[2].addBox(-1f, -8f, -1f, 2, 16, 2);
        shards[2].setRotationPoint(-7f, -4f, 5f);
        shards[2].rotateAngleX = rad(-8);

        shards[3] = new ModelRenderer(this, 24, 86); // NW — shortest, side lean
        shards[3].addBox(-1f, -5f, -1f, 2, 10, 2);
        shards[3].setRotationPoint(-5f, -4f, -7f);
        shards[3].rotateAngleY = rad(55);
        shards[3].rotateAngleZ = rad(-8);

        // Asymmetric drooping appendages. Rest-pose lean angles baked in
        // Renderer adds sway: rotX/Z using sin(t + phaseOffset per tendril)

        int w = TENDRIL_THICKNESS;
        int l = TENDRIL_LENGTH;

        tendrils[0] = new ModelRenderer(this, 0, 106);   // front-left
        tendrils[0].addBox(-w/2f, 0f, -w/2f, w, l, w);
        tendrils[0].setRotationPoint(-3f, 9f, -3f);       // 9 = below shaft bottom
        tendrils[0].rotateAngleX = rad(20);               // rest splay
        core.addChild(tendrils[0]);

        tendrils[1] = new ModelRenderer(this, 8, 106);   // back-right
        tendrils[1].addBox(-w/2f, 0f, -w/2f, w, l - 3, w);
        tendrils[1].setRotationPoint(3f, 9f, 3f);
        tendrils[1].rotateAngleX = rad(-12);
        tendrils[1].rotateAngleZ = rad(8);
        core.addChild(tendrils[1]);

        tendrils[2] = new ModelRenderer(this, 16, 106);  // front-right
        tendrils[2].addBox(-w/2f, 0f, -w/2f, w, l - 5, w);
        tendrils[2].setRotationPoint(3f, 9f, -4f);
        tendrils[2].rotateAngleZ = rad(-15);
        core.addChild(tendrils[2]);

        tendrils[3] = new ModelRenderer(this, 0, 106);   // back-left
        tendrils[3].addBox(-w/2f, 0f, -w/2f, w, l - 1, w);
        tendrils[3].setRotationPoint(-4f, 9f, 3f);
        tendrils[3].rotateAngleX = rad(-15);
        tendrils[3].rotateAngleZ = rad(-10);
        core.addChild(tendrils[3]);

        tendrils[4] = new ModelRenderer(this, 8, 106);   // front-center
        tendrils[4].addBox(-w/2f, 0f, -w/2f, w, l - 2, w);
        tendrils[4].setRotationPoint(0f, 9f, -4f);
        tendrils[4].rotateAngleX = rad(25);
        core.addChild(tendrils[4]);

        tendrils[5] = new ModelRenderer(this, 16, 106);  // back-center
        tendrils[5].addBox(-w/2f, 0f, -w/2f, w, l - 4, w);
        tendrils[5].setRotationPoint(0f, 9f, 4f);
        tendrils[5].rotateAngleX = rad(-20);
        core.addChild(tendrils[5]);
    }

    /**
     * Render all parts.
     * Render order (bottom -> top, independent -> dependent):
     *   1. beamSocket      - static base
     *   2. core            - also renders: bulge0, bulge1, coreInner, spikes[0-3], tendrils[0-2]  (all children)
     *   3. crownRing       - also renders: 4 fin children
     *   4. shards[0-3]     - independent world-space positions
     * Renderer should set part.rotateAngleY / X / Z on public fields BEFORE calling this method each frame.
     */
    @Override
    public void render(Entity entity,
                       float limbSwing, float limbSwingAmount,
                       float ageInTicks, float netHeadYaw,
                       float headPitch, float scale) {
        beamSocket.render(scale);
        core.render(scale);        // children auto-included
        crownRing.render(scale);   // children auto-included
        for (ModelRenderer shard : shards) {
            shard.render(scale);
        }
    }

    // HELPERS
    private static float rad(double degrees) {
        return (float) Math.toRadians(degrees);
    }

    /** Appends a fin box as a positioned child of {@code parent}. */
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