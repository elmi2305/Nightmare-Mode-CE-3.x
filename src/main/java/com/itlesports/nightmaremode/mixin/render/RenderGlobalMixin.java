package com.itlesports.nightmaremode.mixin.render;

import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.NMUtils;
import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderGlobal.class)
public abstract class RenderGlobalMixin {
    @Shadow private Minecraft mc;
    @Shadow private int glSkyList;
    @Shadow @Final private TextureManager renderEngine;
    @Shadow @Final private static ResourceLocation locationCloudsPng;
    @Shadow private WorldClient theWorld;
    @Shadow private int cloudTickCounter;

    @Shadow public abstract void renderCloudsFancy(float par1);

    @Unique private static final ResourceLocation BLOODMOON = new ResourceLocation("textures/bloodmoon.png");
    @Unique private static final ResourceLocation ECLIPSE = new ResourceLocation("textures/eclipse.png");

    @ModifyArg(method = "renderSky", at = @At(value = "INVOKE", target = "Lcom/prupe/mcpatcher/sky/SkyRenderer;setupCelestialObject(Lnet/minecraft/src/ResourceLocation;)Lnet/minecraft/src/ResourceLocation;",ordinal = 2))
    private ResourceLocation manageBloodMoonTexture(ResourceLocation defaultTexture){
        if(NMUtils.getIsBloodMoon()){
            return BLOODMOON;
        }
        return defaultTexture;
    }
    @ModifyArg(method = "renderSky", at = @At(value = "INVOKE", target = "Lcom/prupe/mcpatcher/sky/SkyRenderer;setupCelestialObject(Lnet/minecraft/src/ResourceLocation;)Lnet/minecraft/src/ResourceLocation;",ordinal = 0))
    private ResourceLocation manageEclipseTexture(ResourceLocation defaultTexture){
        if(NMUtils.getIsEclipse()){
            return ECLIPSE;
        }
        return defaultTexture;
    }
    @Redirect(method = "renderSky", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glBlendFunc(II)V",ordinal = 2), remap = false)
    private void manageSunNotBlendingOnEclipse(int sFactor, int dFactor){
        if(NMUtils.getIsEclipse()){
            GL11.glBlendFunc(770,1);
        }
        GL11.glBlendFunc(sFactor,dFactor);
    }

    @ModifyArg(method = "renderSky", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glColor4f(FFFF)V", ordinal = 0), index = 3)
    private float doNotRemoveSunOnBloodRain(float red){
        if(NMUtils.getIsBloodMoon()) {return 0.6f;}

        return red;
    }


    @Unique
    private static final ResourceLocation SKYBOX =  new ResourceLocation("textures/underworld/underworld_sky_special_2.png");
//    private static final ResourceLocation SKYBOX =  new ResourceLocation("textures/underworld/underworld_sky_special_4.png"); // implemented a minor fix for 3 by just repeating the texture so it loops. this makes it look too pattern-y and bad. looks good for red events
//    private static final ResourceLocation SKYBOX =  new ResourceLocation("textures/underworld/underworld_sky_special_3.png"); // goes really well for a dark / purple aesthetic, if only it was a proper skybox and looped
//    private static final ResourceLocation SKYBOX =  new ResourceLocation("textures/underworld/underworld_sky_special_2.png"); // kinda bleh low-key, but it could work as a decent pattern when colored
//    private static final ResourceLocation SKYBOX =  new ResourceLocation("textures/underworld/underworld_sky_special_1.png"); // insanely good for astral style events, a-la stardust pillar from terraria
//    private static final ResourceLocation SKYBOX =  new ResourceLocation("textures/underworld/underworld_sky_black_2.png"); // good for blue moon
//    private static final ResourceLocation SKYBOX =  new ResourceLocation("textures/underworld/underworld_sky_black_1.png"); // solid day color scheme


    @Redirect(
            method = "renderSky",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glCallList(I)V"
            )
    )
    private void redirectGlCallList(int list) {
        if(this.mc.thePlayer.dimension == NightmareMode.UNDERWORLD_DIMENSION) {
            try {
                if (list == this.glSkyList) {
                    renderTexturedSkyDome();
                } else {
                    // for other lists (stars etc.) preserve original behavior
                    GL11.glCallList(list);
                }
            } catch (Throwable t) {
                // Safety fallback — if anything goes wrong, fall back to calling list
                t.printStackTrace();
                GL11.glCallList(list);
            }
        } else {
            GL11.glCallList(list);
        }
    }

    @Unique
    private void renderTexturedSkyDome() {
        final float radius = 128;
        final int latSteps = 16;
        final int lonSteps = 64;
        final float uRepeat = 1.0f;
        final float vRepeat = 1.0f;

        // Attempt to bind the texture (best-effort for 1.6.4)
        if (this.mc != null && this.mc.renderEngine != null) {
            try {
                this.mc.renderEngine.bindTexture(SKYBOX);
            } catch (Exception e) {
                System.out.println("ERROR: Failed to bind texture");
                // ignore binding failure (we'll fall back to plain color)
            }
        }

        // --- SAVE & SET SAFE GL STATE ---
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glPushMatrix();

        // Ensure depth test is enabled, and we use LEQUAL so sun/quads render fine.
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthFunc(GL11.GL_LEQUAL);
        GL11.glDepthMask(false);

        // Reasonable defaults - ensure texture 2D is enabled
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glDisable(GL11.GL_CULL_FACE); // We want to see inside the dome

        // change color of the dome instead of using residual color (blue sky)
//        GL11.glColor4f(0.9f, 0.1f, 0.1f, 1f);



        // Start drawing dome geometry
        Tessellator tess = Tessellator.instance;

        for (int lat = 0; lat < latSteps; lat++) {
            double minTheta = -Math.PI / 6.0;  // -15 degrees below horizon
            double maxTheta = Math.PI / 2.0;    // +90 degrees at zenith
            double theta0 = minTheta + (maxTheta - minTheta) * ((double) lat / latSteps);
            double theta1 = minTheta + (maxTheta - minTheta) * ((double) (lat + 1) / latSteps);


            double y0 = Math.sin(theta0) * radius;
            double y1 = Math.sin(theta1) * radius;

            double r0 = Math.cos(theta0) * radius;
            double r1 = Math.cos(theta1) * radius;

            tess.startDrawingQuads();
            for (int lon = 0; lon < lonSteps; lon++) {
                double phi0 = 2.0 * Math.PI * ((double) lon / lonSteps);
                double phi1 = 2.0 * Math.PI * ((double) (lon + 1) / lonSteps);

                double x00 = r0 * Math.cos(phi0);
                double z00 = r0 * Math.sin(phi0);

                double x01 = r0 * Math.cos(phi1);
                double z01 = r0 * Math.sin(phi1);

                double x10 = r1 * Math.cos(phi0);
                double z10 = r1 * Math.sin(phi0);

                double x11 = r1 * Math.cos(phi1);
                double z11 = r1 * Math.sin(phi1);

                float u0 = (float) lon / lonSteps * uRepeat;
                float u1 = (float) (lon + 1) / lonSteps * uRepeat;
                float v0 = (float) lat / latSteps * vRepeat;
                float v1 = (float) (lat + 1) / latSteps * vRepeat;

                // Add quad (order chosen for consistent winding)
                tess.addVertexWithUV(x10, y1, z10, u0, v1);
                tess.addVertexWithUV(x11, y1, z11, u1, v1);
                tess.addVertexWithUV(x01, y0, z01, u1, v0);
                tess.addVertexWithUV(x00, y0, z00, u0, v0);
            }
            tess.draw();
        }


        // --- RESTORE GL STATE ---
        // restore depth write and set a safe color
        GL11.glDepthMask(true);
        GL11.glColor4f(1f, 1f, 1f, 1f);

        GL11.glPopMatrix();
        GL11.glPopAttrib(); // pops enable/depth/color bits we changed
    }
    @Redirect(method = "renderSky", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/WorldClient;getHorizon()D"))
    private double patchHorizon(WorldClient wcl) {
        if(this.mc.thePlayer.dimension == NightmareMode.UNDERWORLD_DIMENSION){
            return -99;
        }
        return wcl.getHorizon();
    }

    @Inject(method = "renderClouds", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/RenderGlobal;renderCloudsFancy(F)V"), cancellable = true)
    private void manageCloudSpikyUnderworld(float par1, CallbackInfo ci){
        if(this.mc.thePlayer.dimension == NightmareMode.UNDERWORLD_DIMENSION){
            this.renderFancyUnderworldClouds(par1);
        } else{
            this.renderCloudsFancy(par1);
        }
        ci.cancel();
    }

    @Unique
    public void renderFancyUnderworldClouds(float par1) {
        float var21;
        float var20;
        float var19;
        GL11.glDisable(2884);
        float var2 = (float)(this.mc.renderViewEntity.lastTickPosY + (this.mc.renderViewEntity.posY - this.mc.renderViewEntity.lastTickPosY) * (double)par1);
        Tessellator var3 = Tessellator.instance;
        float var4 = 12.0f;
        float var5 = 4.0f;
        double var6 = (float)this.cloudTickCounter + par1;
        float var12 = this.theWorld.provider.getCloudHeight() - var2 + 0.33f;
        this.renderEngine.bindTexture(locationCloudsPng);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        Vec3 var15 = this.theWorld.getCloudColour(par1);


        float var16 = (float)var15.xCoord;
        float var17 = (float)var15.yCoord;
        float var18 = (float)var15.zCoord;
        if (this.mc.gameSettings.anaglyph) {
            var19 = (var16 * 30.0f + var17 * 59.0f + var18 * 11.0f) / 100.0f;
            var20 = (var16 * 30.0f + var17 * 70.0f) / 100.0f;
            var21 = (var16 * 30.0f + var18 * 70.0f) / 100.0f;
            var16 = var19;
            var17 = var20;
            var18 = var21;
        }
        var21 = 0.00390625f;
        int numLayers = 3;
        float layerSpacing = 11.0f;
        float baseAlpha = 0.9f;
        float var26 = 9.765625E-4f;
        int var24 = 4;
        int var25 = 8;
        float freq = 0.2f;
        float amp = 7.0f;
        GL11.glScalef(var4, 1.0f, var4);
        for (int layer = 0; layer < numLayers; ++layer) {
            float layerAlpha = baseAlpha / (float)numLayers * 1.5f;
            float layerBrightness = 0.7f + 0.1f * (float)layer;
            double layerSpeed = 0.03 - 0.005 * layer;
            double layerVar8 = (this.mc.renderViewEntity.prevPosX + (this.mc.renderViewEntity.posX - this.mc.renderViewEntity.prevPosX) * (double)par1 + var6 * layerSpeed) / (double)var4;
            double layerVar10 = (this.mc.renderViewEntity.prevPosZ + (this.mc.renderViewEntity.posZ - this.mc.renderViewEntity.prevPosZ) * (double)par1) / (double)var4 + 0.33;
            layerVar8 += layer * 48.0;
            layerVar10 += layer * 37.0;
            int var13 = MathHelper.floor_double(layerVar8 / 2048.0);
            int var14 = MathHelper.floor_double(layerVar10 / 2048.0);
            layerVar8 -= (double)(var13 * 2048);
            layerVar10 -= (double)(var14 * 2048);
            var19 = (float)MathHelper.floor_double(layerVar8) * var21;
            var20 = (float)MathHelper.floor_double(layerVar10) * var21;
            float var22 = (float)(layerVar8 - (double)MathHelper.floor_double(layerVar8));
            float var23 = (float)(layerVar10 - (double)MathHelper.floor_double(layerVar10));
            float layerVar12 = var12 + layer * layerSpacing;
            for (int var27 = 0; var27 < 2; ++var27) {
                if (var27 == 0) {
                    GL11.glColorMask(false, false, false, false);
                } else if (this.mc.gameSettings.anaglyph) {
                    if (EntityRenderer.anaglyphField == 0) {
                        GL11.glColorMask(false, true, true, true);
                    } else {
                        GL11.glColorMask(true, false, false, true);
                    }
                } else {
                    GL11.glColorMask(true, true, true, true);
                }
                for (int var28 = -var25 + 1; var28 <= var25; ++var28) {
                    for (int var29 = -var25 + 1; var29 <= var25; ++var29) {
                        var3.startDrawingQuads();
                        float var30 = var28 * var24;
                        float var31 = var29 * var24;
                        float var32 = var30 - var22;
                        float var33 = var31 - var23;

                        float timePhase = (float)var6 * 0.01f; // Adjust speed for waving

                        // For each sub-cuboid (e.g., per subX/subZ)
                        for (int subX = 0; subX < var24; ++subX) {
                            for (int subZ = 0; subZ < var24; ++subZ) {
                                float x0 = var32 + subX;
                                float x1 = var32 + subX + 1;
                                float z0 = var33 + subZ;
                                float z1 = var33 + subZ + 1;

                                // Compute offsets for corners (bottom and top share x/z, differ in y-offset)
                                float offBottom00 = computeOffset(x0, z0, amp, freq, timePhase);
                                float offBottom10 = computeOffset(x1, z0, amp, freq, timePhase);
                                float offBottom11 = computeOffset(x1, z1, amp, freq, timePhase);
                                float offBottom01 = computeOffset(x0, z1, amp, freq, timePhase);

                                // Top offsets could differ for varying height; here assume uniform thickness, but mutate if desired
                                float offTop00 = offBottom00; // Or add noise: + (float)Math.sin(var6 * 0.05 + subX) * 0.5f;
                                float offTop10 = offBottom10;
                                float offTop11 = offBottom11;
                                float offTop01 = offBottom01;

                                // 8 points
                                float[][] points = new float[8][3];
                                points[0] = new float[]{x0, layerVar12 + offBottom00, z0}; // Bottom front left
                                points[1] = new float[]{x1, layerVar12 + offBottom10, z0}; // Bottom front right
                                points[2] = new float[]{x1, layerVar12 + offBottom11, z1}; // Bottom back right
                                points[3] = new float[]{x0, layerVar12 + offBottom01, z1}; // Bottom back left
                                points[4] = new float[]{x0, layerVar12 + var5 + offTop00, z0}; // Top front left
                                points[5] = new float[]{x1, layerVar12 + var5 + offTop10, z0}; // Top front right
                                points[6] = new float[]{x1, layerVar12 + var5 + offTop11, z1}; // Top back right
                                points[7] = new float[]{x0, layerVar12 + var5 + offTop01, z1}; // Top back left

                                // UV for this sub-cuboid
                                float subU = (var30 + subX) * var21 + var19;
                                float subV = (var31 + subZ) * var21 + var20;
                                float subScale = var21; // Per-unit scale

                                // Draw!
                                drawCuboid(var3, points, var16 * layerBrightness, var17 * layerBrightness, var18 * layerBrightness, layerAlpha, subU, subV, subScale);
                            }
                        }
                        var3.draw();
                    }
                }
            }
        }
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glDisable(3042);
        GL11.glEnable(2884);
    }


    @Unique
    private float computeOffset(float fullX, float fullZ, float amp, double freq, float timePhase) {
        double fx = (double)fullX + timePhase;
        double fz = (double)fullZ + timePhase * 0.5;
        return amp * (float) (0.5 * Math.sin(fx * freq) + 0.5 * Math.cos(fz * freq) + 0.25 * Math.sin(fx * freq * 2.0));
    }

    @Unique
    private void drawCuboid(Tessellator tess, float[][] points, float r, float g, float b, float a, float uBase, float vBase, float uvScale) {
        // points: 8-element array of float[3] {x,y,z} - bottom: [0-3], top: [4-7]
        // Assume order: bottom-front-left [0], bottom-front-right [1], bottom-back-right [2], bottom-back-left [3],
        //               top-front-left [4], top-front-right [5], top-back-right [6], top-back-left [7]

        // Bottom face
//        tess.setColorRGBA_F(r * 0.7f, g * 0.7f, b * 0.7f, a);
        tess.setColorRGBA_F(r * 0.2f, g * 0.2f, b * 0.2f, a);
        tess.setNormal(0.0f, -1.0f, 0.0f);
        tess.addVertexWithUV(points[0][0], points[0][1], points[0][2], uBase, vBase);
        tess.addVertexWithUV(points[1][0], points[1][1], points[1][2], uBase + uvScale, vBase);
        tess.addVertexWithUV(points[2][0], points[2][1], points[2][2], uBase + uvScale, vBase + uvScale);
        tess.addVertexWithUV(points[3][0], points[3][1], points[3][2], uBase, vBase + uvScale);

        // Top face
//        tess.setColorRGBA_F(r, g, b, a);
        tess.setColorRGBA_F(r * 0.2f, g * 0.2f, b * 0.2f, a);
        tess.setNormal(0.0f, 1.0f, 0.0f);
        tess.addVertexWithUV(points[4][0], points[4][1], points[4][2], uBase, vBase);
        tess.addVertexWithUV(points[5][0], points[5][1], points[5][2], uBase + uvScale, vBase);
        tess.addVertexWithUV(points[6][0], points[6][1], points[6][2], uBase + uvScale, vBase + uvScale);
        tess.addVertexWithUV(points[7][0], points[7][1], points[7][2], uBase, vBase + uvScale);

        // Side faces (with adjusted brightness)
//        tess.setColorRGBA_F(r * 0.9f, g * 0.9f, b * 0.9f, a); // Front/back example; adjust per side if needed
        tess.setColorRGBA_F(r * 0.3f, g * 0.3f, b * 0.3f, a); // Front/back example; adjust per side if needed
        // Front
        tess.setNormal(0.0f, 0.0f, -1.0f);
        tess.addVertexWithUV(points[0][0], points[0][1], points[0][2], uBase, vBase);
        tess.addVertexWithUV(points[1][0], points[1][1], points[1][2], uBase + uvScale, vBase);
        tess.addVertexWithUV(points[5][0], points[5][1], points[5][2], uBase + uvScale, vBase + uvScale);
        tess.addVertexWithUV(points[4][0], points[4][1], points[4][2], uBase, vBase + uvScale);

        // Back
        tess.setNormal(0.0f, 0.0f, 1.0f);
        tess.addVertexWithUV(points[3][0], points[3][1], points[3][2], uBase, vBase);
        tess.addVertexWithUV(points[2][0], points[2][1], points[2][2], uBase + uvScale, vBase);
        tess.addVertexWithUV(points[6][0], points[6][1], points[6][2], uBase + uvScale, vBase + uvScale);
        tess.addVertexWithUV(points[7][0], points[7][1], points[7][2], uBase, vBase + uvScale);

        // Left
        tess.setNormal(-1.0f, 0.0f, 0.0f);
        tess.addVertexWithUV(points[3][0], points[3][1], points[3][2], uBase, vBase);
        tess.addVertexWithUV(points[0][0], points[0][1], points[0][2], uBase + uvScale, vBase);
        tess.addVertexWithUV(points[4][0], points[4][1], points[4][2], uBase + uvScale, vBase + uvScale);
        tess.addVertexWithUV(points[7][0], points[7][1], points[7][2], uBase, vBase + uvScale);

        // Right
        tess.setNormal(1.0f, 0.0f, 0.0f);
        tess.addVertexWithUV(points[1][0], points[1][1], points[1][2], uBase, vBase);
        tess.addVertexWithUV(points[2][0], points[2][1], points[2][2], uBase + uvScale, vBase);
        tess.addVertexWithUV(points[6][0], points[6][1], points[6][2], uBase + uvScale, vBase + uvScale);
        tess.addVertexWithUV(points[5][0], points[5][1], points[5][2], uBase, vBase + uvScale);
    }
}
