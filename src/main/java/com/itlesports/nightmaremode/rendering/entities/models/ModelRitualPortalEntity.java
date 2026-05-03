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

    // ── Animated (primary) ─────────────────────────────────────────────────

    /** Primary body mass. ANIMATE: rotY spin, posY bob. */
    public final ModelRenderer core;

    /**
     * Inner void shadow (child of core).
     * ANIMATE: counter-rotY relative to core, slow X/Z tilt.
     */
    public final ModelRenderer coreInner;

    /**
     * Equatorial orbit ring. Rendered independently of core.
     * ANIMATE: own rotY counter-spin, own tilt phase.
     */
    public final ModelRenderer crownRing;

    /**
     * Cardinal spines. Children of core — rotate with it.
     * ANIMATE: rotX (N/S) or rotZ (E/W) oscillation — breathing flare.
     * [0]=North(-Z)  [1]=South(+Z)  [2]=West(-X)  [3]=East(+X)
     */
    public final ModelRenderer[] spikes = new ModelRenderer[4];

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
     */
    public final ModelRenderer[] tendrils = new ModelRenderer[3];

    // ── Static / structural ────────────────────────────────────────────────

    /** Bottom anchor socket. Never animated. */
    public final ModelRenderer beamSocket;

    // ──────────────────────────────────────────────────────────────────────

    public ModelRitualPortalEntity() {
        textureWidth  = 128;
        textureHeight = 128;

        // ── BEAM SOCKET ──────────────────────────────────────────────────────
        // Sits at entity origin. The ritual-site beam terminates here.
        // Slightly larger than the base of the core for a socketed look.
        beamSocket = new ModelRenderer(this, 24, 48);
        beamSocket.addBox(-3f, 0f, -3f, 6, 6, 6);
        beamSocket.setRotationPoint(0f, 2f, 0f);

        // ── CORE ─────────────────────────────────────────────────────────────
        // Primary oblong shaft + two asymmetric bulge blobs.
        // Three overlapping boxes = jagged silhouette, reads as mass not geometry.
        //
        // rotationPoint Y=-4: entity feet at Y=0, so core hovers.
        core = new ModelRenderer(this, 0, 0);
        core.addBox(-5f, -10f, -5f, 10, 20, 10);   // central shaft
        core.setRotationPoint(0f, -4f, 0f);

        // Bulge front-left — juts further out than the shaft edge, shorter height
        ModelRenderer bulge0 = new ModelRenderer(this, 0, 30);
        bulge0.addBox(-8f, -6f, -8f, 6, 12, 6);
        bulge0.setRotationPoint(0f, 0f, 0f);
        core.addChild(bulge0);

        // Bulge back-right — opposite corner, slightly different Y offset → asymmetry
        ModelRenderer bulge1 = new ModelRenderer(this, 24, 30);
        bulge1.addBox(2f, -4f, 2f, 6, 10, 6);
        bulge1.setRotationPoint(0f, 0f, 0f);
        core.addChild(bulge1);

        // ── CORE INNER ───────────────────────────────────────────────────────
        // Smaller box embedded inside core. Darker texture region → suggests void.
        // Renderer counter-rotates this relative to core parent rotation.
        coreInner = new ModelRenderer(this, 0, 48);
        coreInner.addBox(-3f, -7f, -3f, 6, 14, 6);
        coreInner.setRotationPoint(0f, 0f, 0f);
        core.addChild(coreInner);

        // ── CROWN RING ───────────────────────────────────────────────────────
        // Four flat fins arranged at cardinal offsets around the core equator.
        // rotationPoint matches core hover height so counter-spin stays centered.
        // Not parented to core — renderer manages rotY independently.
        crownRing = new ModelRenderer(this, 0, 0); // dummy root, no own box
        crownRing.setRotationPoint(0f, -4f, 0f);

        //           parent      x     y     z     w  h  d   u   v
        addRingFin(crownRing,  -1f,  -2f,  -8f,  3, 4, 3,  0, 79);  // North fin
        addRingFin(crownRing,  -1f,  -2f,   5f,  3, 4, 3, 12, 79);  // South fin
        addRingFin(crownRing,  -8f,  -2f,  -1f,  3, 4, 3, 24, 79);  // West fin
        addRingFin(crownRing,   5f,  -2f,  -1f,  3, 4, 3, 36, 79);  // East fin

        // ── SPIKES ───────────────────────────────────────────────────────────
        // Thin elongated boxes pinned at their base (core surface).
        // Box extends outward from rotationPoint — so animating rotX/Z on the
        // part makes the tip sweep in/out relative to the core surface.
        //
        // Cardinal pair N/S: animate rotateAngleX ± 0.3 rad
        // Cardinal pair E/W: animate rotateAngleZ ± 0.3 rad

        spikes[0] = new ModelRenderer(this, 0, 68);  // North, points -Z
        spikes[0].addBox(-1f, -1f, -9f, 2, 2, 9);
        spikes[0].setRotationPoint(0f, -2f, -5f);    // pinned at north face
        core.addChild(spikes[0]);

        spikes[1] = new ModelRenderer(this, 0, 68);  // South, points +Z
        spikes[1].addBox(-1f, -1f, 0f, 2, 2, 9);
        spikes[1].setRotationPoint(0f, -2f, 5f);
        core.addChild(spikes[1]);

        spikes[2] = new ModelRenderer(this, 22, 68); // West, points -X
        spikes[2].addBox(-9f, -1f, -1f, 9, 2, 2);
        spikes[2].setRotationPoint(-5f, -2f, 0f);
        core.addChild(spikes[2]);

        spikes[3] = new ModelRenderer(this, 22, 68); // East, points +X
        spikes[3].addBox(0f, -1f, -1f, 9, 2, 2);
        spikes[3].setRotationPoint(5f, -2f, 0f);
        core.addChild(spikes[3]);

        // ── SHARDS ───────────────────────────────────────────────────────────
        // Tall thin pillars at diagonal positions. Baked rest-pose lean angles.
        // Renderer adds oscillation on top of the baked angles.
        // Rendered independently — NOT children of core.
        //
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

        // ── TENDRILS ─────────────────────────────────────────────────────────
        // Asymmetric drooping appendages. Rest-pose lean angles baked in.
        // Children of core — they inherit core spin and bob.
        // Renderer adds sway: rotX/Z using sin(t + phaseOffset per tendril).

        tendrils[0] = new ModelRenderer(this, 0, 106);   // front-left, longest
        tendrils[0].addBox(-1f, 0f, -1f, 2, 8, 2);
        tendrils[0].setRotationPoint(-3f, 9f, -3f);       // 9 = below shaft bottom
        tendrils[0].rotateAngleX = rad(20);               // rest splay
        core.addChild(tendrils[0]);

        tendrils[1] = new ModelRenderer(this, 8, 106);   // back-right, medium
        tendrils[1].addBox(-1f, 0f, -1f, 2, 6, 2);
        tendrils[1].setRotationPoint(3f, 9f, 3f);
        tendrils[1].rotateAngleX = rad(-12);
        tendrils[1].rotateAngleZ = rad(8);
        core.addChild(tendrils[1]);

        tendrils[2] = new ModelRenderer(this, 16, 106);  // front-right, short
        tendrils[2].addBox(-1f, 0f, -1f, 2, 5, 2);
        tendrils[2].setRotationPoint(3f, 9f, -4f);
        tendrils[2].rotateAngleZ = rad(-15);
        core.addChild(tendrils[2]);
    }

    // ──────────────────────────────────────────────────────────────────────

    /**
     * Render all parts.
     *
     * Render order (bottom → top, independent → dependent):
     *   1. beamSocket      — static base
     *   2. core            — also renders: bulge0, bulge1, coreInner,
     *                        spikes[0-3], tendrils[0-2]  (all children)
     *   3. crownRing       — also renders: 4 fin children
     *   4. shards[0-3]     — independent world-space positions
     *
     * Renderer should set part.rotateAngleY / X / Z on public fields
     * BEFORE calling this method each frame.
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

    // ── Helpers ────────────────────────────────────────────────────────────

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