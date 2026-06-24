package com.itlesports.nightmaremode.mixin.render;

import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.util.NMEvents;
import com.itlesports.nightmaremode.util.NMFields;
import com.itlesports.nightmaremode.util.NMUtils;
import com.itlesports.nightmaremode.util.underworld.SkyboxObject;
import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(RenderGlobal.class)
public abstract class RenderGlobalMixin {
    @Shadow private Minecraft mc;
    @Shadow private int glSkyList;
    @Shadow @Final private TextureManager renderEngine;
    @Shadow @Final private static ResourceLocation locationCloudsPng;
    @Shadow private WorldClient theWorld;
    @Shadow private int cloudTickCounter;

    @Shadow public abstract void renderCloudsFancy(float par1);

    @Shadow @Final private static ResourceLocation locationSunPng;
    @Shadow private int glSkyList2;
    @Shadow private int starGLCallList;

    @Shadow
    @Final
    private static ResourceLocation locationMoonMoonPhasesPng;
    @Shadow
    @Final
    private static ResourceLocation locationMoonPhasesPng;
    @Shadow
    @Final
    private static ResourceLocation locationEndSkyPng;

    @Shadow
    public abstract void renderClouds(float par1);

    @Unique private static final ResourceLocation BLOODMOON = new ResourceLocation("nightmare:textures/moon/bloodmoon.png");
    @Unique private static final ResourceLocation ECLIPSE = new ResourceLocation("nightmare:textures/moon/eclipse.png");
    @Unique private static final ResourceLocation CRACK = new ResourceLocation("nightmare:textures/moon/crack.png");
    @Unique private static final ResourceLocation BLUE_MOON = new ResourceLocation("nightmare:textures/moon/bluemoon.png");
    @Unique private static final ResourceLocation SKYBOX_RED = new ResourceLocation("nightmare:textures/effects/red.png");
    @Unique private static final ResourceLocation SKYBOX_WHITE = new ResourceLocation("nightmare:textures/effects/white.png");
    @Unique private static final ResourceLocation STARE = new ResourceLocation("nightmare:textures/effects/stare.png");

    /*

    CUSTOM MOON

     */

    @Unique private boolean isUnderWorld(){
        return this.mc.thePlayer.dimension == NMFields.UNDERWORLD_DIMENSION;
    }

//    @Redirect(method = "renderSky", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/Tessellator;draw()I", ordinal = 3))
//    private int changeMoonSkyTexture(Tessellator instance){
//        if(this.isUnderWorld() && NMUtils.getIsBlueMoon()) return 0;        // avoids drawing the moon, it is manually drawn later
//        return instance.draw();
//    }
//    @Redirect(method = "renderSky", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/Tessellator;startDrawingQuads()V", ordinal = 2))
//    private void doNothingAndDoNotStartDrawingMoon(Tessellator instance){
//        if(this.isUnderWorld() && NMUtils.getIsBlueMoon()) return;        // avoids drawing the moon, it is manually drawn later
//        instance.startDrawingQuads();
//    }
//    @Inject(method = "renderSky", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/Tessellator;draw()I", shift = At.Shift.AFTER, ordinal =  3))
//    private void setUpUVForMoon(float par1, CallbackInfo ci){
//        if(!this.isUnderWorld() || !NMUtils.getIsBlueMoon()) return;
//        // only runs in UW
//        Tessellator var23 = Tessellator.instance;
//
//        float size = 30.0f;
//
//        int phase = this.theWorld.getMoonPhase();
//
//        this.renderEngine.bindTexture(SkyRenderer.setupCelestialObject(BLUE_MOON));
//        float uMin = 0.0f;
//        float uMax = 1.0f;
//        float vMin = 0.0f;
//        float vMax = 1.0f;
//
//        var23.startDrawingQuads();
//        var23.addVertexWithUV(-size, -100.0,  size, uMax, vMax);
//        var23.addVertexWithUV( size, -100.0,  size, uMin, vMax);
//        var23.addVertexWithUV( size, -100.0, -size, uMin, vMin);
//        var23.addVertexWithUV(-size, -100.0, -size, uMax, vMin);
//
//        var23.draw();
//    }



    /*

    CUSTOM SUN

     */

//    @Redirect(method = "renderSky", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/Tessellator;draw()I", ordinal = 2))
//    private int avoidDrawingInitialSun(Tessellator instance){
//        // avoids drawing the moon, it is manually drawn later
//        return 0;
//    }
//    @Redirect(method = "renderSky", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/Tessellator;startDrawingQuads()V", ordinal = 1))
//    private void doNotStartDrawingSunQuads(Tessellator instance){}
//    private float sunAnimationProgress = 0.0f;       // 0.0 → 1.0 over the animation window
//    private long lastWorldTime = -1L;                // To detect time changes / wrap-around
//    @Inject(method = "renderSky", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/Tessellator;draw()I", shift = At.Shift.AFTER, ordinal = 2))
//    private void setUpCustomSun(float par1, CallbackInfo ci){
//        this.renderEngine.bindTexture(SkyRenderer.setupCelestialObject(locationSunPng));
//        Tessellator var23 = Tessellator.instance;  // Assuming var23 is Tessellator.instance
//
//        float sunSize = 30.0f;
//        float crackThreshold = 0.6f;
//        float crackAlpha = MathHelper.clamp_float(sunAnimationProgress / crackThreshold, 0.0f, 1.0f);
//        float shatterProgress = MathHelper.clamp_float((sunAnimationProgress - crackThreshold) / (1.0f - crackThreshold), 0.0f, 1.0f);
//        float pieceFade = 1.0f - shatterProgress;
//        float spread = shatterProgress * 12.0f;
//
//        ResourceLocation sunTex = SkyRenderer.setupCelestialObject(locationSunPng);
//        ResourceLocation crackTex = CRACK;  // Your 32x32 crack texture
//
//        if (sunAnimationProgress < crackThreshold) {
//            // Phase 1: Normal sun + fading-in crack overlay
//            this.renderEngine.bindTexture(sunTex);
//            var23.startDrawingQuads();
//            var23.setColorOpaque_F(1.0F, 1.0F, 1.0F);
//            var23.addVertexWithUV(-sunSize, 100.0D, -sunSize, 0.0D, 0.0D);
//            var23.addVertexWithUV( sunSize, 100.0D, -sunSize, 1.0D, 0.0D);
//            var23.addVertexWithUV( sunSize, 100.0D,  sunSize, 1.0D, 1.0D);
//            var23.addVertexWithUV(-sunSize, 100.0D,  sunSize, 0.0D, 1.0D);
//            var23.draw();
//
//            // Black crack overlay (fades in perfectly over 32-unit sun)
//            this.renderEngine.bindTexture(crackTex);
//            var23.startDrawingQuads();
//            var23.setColorRGBA_F(0.0F, 0.0F, 0.0F, crackAlpha);
//            var23.addVertexWithUV(-sunSize, 100.0D, -sunSize, 0.0D, 0.0D);
//            var23.addVertexWithUV( sunSize, 100.0D, -sunSize, 1.0D, 0.0D);
//            var23.addVertexWithUV( sunSize, 100.0D,  sunSize, 1.0D, 1.0D);
//            var23.addVertexWithUV(-sunSize, 100.0D,  sunSize, 0.0D, 1.0D);
//            var23.draw();
//        } else {
//            // Phase 2: Shatter into 4 rotating, spreading, fading pieces using partial sun UVs for realism
//            this.renderEngine.bindTexture(sunTex);
//            float pieceSize = sunSize * 0.65f;
//
//            // Translate to sun center for per-piece transforms (Y=100 fixed)
//            GL11.glPushMatrix();
//            GL11.glTranslatef(0.0F, 100.0F, 0.0F);
//
//            // Piece data: offsets (X,Z), base rotation (degrees), UV bounds (minU,maxU,minV,maxV)
//            float[][] offsets = {
//                    {-spread * 0.7f, -spread * 0.7f},
//                    { spread * 0.7f, -spread * 0.7f},
//                    {-spread * 0.5f,  spread * 0.8f},
//                    { spread * 0.6f,  spread * 0.5f}
//            };
//            float[] baseRotations = {90.0f, -120.0f, 45.0f, -80.0f};
//            float[][] uvs = {
//                    {0.0f, 0.55f, 0.0f, 0.55f},   // Bottom-left shard
//                    {0.45f, 1.0f, 0.0f, 0.55f},   // Bottom-right
//                    {0.0f, 0.55f, 0.45f, 1.0f},   // Top-left
//                    {0.45f, 1.0f, 0.45f, 1.0f}    // Top-right
//            };
//
//            for (int i = 0; i < 4; i++) {
//                GL11.glPushMatrix();
//                GL11.glTranslatef(offsets[i][0], 0.0F, offsets[i][1]);
//                GL11.glRotatef(baseRotations[i] + shatterProgress * 720.0f, 0.0F, 1.0F, 0.0F);  // Spin + tumble
//
//                var23.startDrawingQuads();
//                var23.setColorRGBA_F(1.0F, 1.0F, 1.0F, pieceFade);
//                float minU = uvs[i][0], maxU = uvs[i][1];
//                float minV = uvs[i][2], maxV = uvs[i][3];
//                var23.addVertexWithUV(-pieceSize,   0.0D, -pieceSize,   minU, minV);
//                var23.addVertexWithUV( pieceSize,   0.0D, -pieceSize,   maxU, minV);
//                var23.addVertexWithUV( pieceSize,   0.0D,  pieceSize,   maxU, maxV);
//                var23.addVertexWithUV(-pieceSize,   0.0D,  pieceSize,   minU, maxV);
//                var23.draw();
//
//                GL11.glPopMatrix();
//            }
//
//            GL11.glPopMatrix();
//        }
//    }
//    @Inject(method = "renderSky", at = @At("HEAD"))
//    private void trackProgress(float par1, CallbackInfo ci){
//        World world = Minecraft.getMinecraft().theWorld;
//        if (world == null) {
//            sunAnimationProgress = 0.0f;
//            return;
//        }
//
//        long currentTime = world.getWorldTime();           // full world time (increases forever)
//        long timeOfDay = currentTime % 24000L;             // 0–23999
//
//        if (currentTime < lastWorldTime || Math.abs(currentTime - lastWorldTime) > 1000) {
//            // Time was set backwards or jumped → reset animation state if needed
//            sunAnimationProgress = 0.0f;
//        }
//        lastWorldTime = currentTime;
//
//        final long ANIM_START = 11300L;   // Slightly before official sunset for anticipation
//        final long ANIM_END   = 11800L;   // Slightly after for lingering effect
//        final long ANIM_LENGTH = ANIM_END - ANIM_START;
//
//        if (timeOfDay >= ANIM_START && timeOfDay <= ANIM_END) {
//            sunAnimationProgress = (float)(timeOfDay - ANIM_START) / (float)ANIM_LENGTH;
//            sunAnimationProgress = MathHelper.clamp_float(sunAnimationProgress, 0.0f, 1.0f);
//        } else if (timeOfDay > ANIM_END || timeOfDay < ANIM_START - 1000) {
//            sunAnimationProgress = 0.0f;
//        } else {
//            sunAnimationProgress = 0.0f;
//        }
//
//        sunAnimationProgress = MathHelper.clamp_float(
//                sunAnimationProgress + par1 / 20.0f * 0.01f,  // tiny smoothing
//                0.0f, 1.0f
//        );
//        sunAnimationProgress = Math.min(sunAnimationProgress, 1.0f);
//        sunAnimationProgress = Math.max(sunAnimationProgress, 0f);
//
//    }


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

    @ModifyArg(method = "renderSky", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glColor4f(FFFF)V", ordinal = 0), index = 3, remap = false)
    private float doNotRemoveSunOnBloodRain(float red){
        if(NMUtils.getIsBloodMoon()) {return 0.6f;}

        return red;
    }


    @Unique
    private static final ResourceLocation SKYBOX =  new ResourceLocation("nightmare:textures/underworld/underworld_sky_special_2.png");
//    private static final ResourceLocation SKYBOX =  new ResourceLocation("nightmare:textures/underworld/underworld_sky_special_4.png"); // implemented a minor fix for 3 by just repeating the texture so it loops. this makes it look too pattern-y and bad. looks good for red events
//    private static final ResourceLocation SKYBOX =  new ResourceLocation("nightmare:textures/underworld/underworld_sky_special_3.png"); // goes really well for a dark / purple aesthetic, if only it was a proper skybox and looped
//    private static final ResourceLocation SKYBOX =  new ResourceLocation("nightmare:textures/underworld/underworld_sky_special_2.png"); // kinda bleh low-key, but it could work as a decent pattern when colored
//    private static final ResourceLocation SKYBOX =  new ResourceLocation("nightmare:textures/underworld/underworld_sky_special_1.png"); // insanely good for astral style events, a-la stardust pillar from terraria
//    private static final ResourceLocation SKYBOX =  new ResourceLocation("nightmare:textures/underworld/underworld_sky_black_2.png"); // good for blue moon
//    private static final ResourceLocation SKYBOX =  new ResourceLocation("nightmare:textures/underworld/underworld_sky_black_1.png"); // solid day color scheme


    @Redirect(
            method = "renderSky",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glCallList(I)V"
            ), remap = false
    )
    private void redirectGlCallList(int list) {
        if(this.mc.thePlayer.dimension == NMFields.UNDERWORLD_DIMENSION) {
            try {
                // always render custom sky texture, regardless of time
                if (list == this.glSkyList || list == this.starGLCallList) {
                    renderTexturedSkyDome();
                } else {
                    // for other lists (stars etc.) preserve original behavior
                    GL11.glCallList(list);
                }
            } catch (Throwable t) {
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

        // attempt to bind the texture (best-effort for 1.6.4)
        if (this.mc != null && this.mc.renderEngine != null) {
            try {
                this.mc.renderEngine.bindTexture(SKYBOX);
            } catch (Exception e) {
                System.out.println("ERROR: Failed to bind texture");
                // ignore binding failure (falls back to plain color)
            }
        }

        // SAVE & SET GL STATE
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glPushMatrix();

        // depth
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthFunc(GL11.GL_LEQUAL);
        GL11.glDepthMask(false);

        // getting ready to draw
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glDisable(GL11.GL_CULL_FACE);

        // dome color, if required
//        GL11.glColor4f(0.9f, 0.1f, 0.1f, 1f);



        // start drawing the skybox
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

                // add quad
                tess.addVertexWithUV(x10, y1, z10, u0, v1);
                tess.addVertexWithUV(x11, y1, z11, u1, v1);
                tess.addVertexWithUV(x01, y0, z01, u1, v0);
                tess.addVertexWithUV(x00, y0, z00, u0, v0);
            }
            tess.draw();
        }


        // restore depth write and set a safe color
        GL11.glDepthMask(true);
        GL11.glColor4f(1f, 1f, 1f, 1f);

        GL11.glPopMatrix();
        GL11.glPopAttrib(); // pops enable/depth/color bits we changed
    }
    @Redirect(method = "renderSky", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/WorldClient;getHorizon()D"))
    private double patchHorizon(WorldClient wcl) {
        // magic "disable the stupid horizon lines because they're ugly" method
        // love it
        if(this.mc.thePlayer.dimension == NMFields.UNDERWORLD_DIMENSION){
            return -200;
        }
        return wcl.getHorizon();
    }
    @ModifyConstant(method = "renderSky", constant = @Constant(doubleValue = 0, ordinal = 10))
    private double renderEverythingDespiteHorizon(double constant){
        if(this.mc.thePlayer.dimension == NMFields.UNDERWORLD_DIMENSION){
            return -199;
        }
        return constant;
    }

    // code stops here



    @Inject(method = "renderClouds", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/RenderGlobal;renderCloudsFancy(F)V"), cancellable = true)
    private void manageCloudSpikyUnderworld(float par1, CallbackInfo ci){
        if(NightmareMode.renderFancyClouds){
            this.renderFancyUnderworldClouds(par1);
        } else{
            this.renderCloudsFancy(par1);
        }
        ci.cancel();
    }
    @Unique
    public void renderFancyUnderworldClouds(float par1) {
        GL11.glDisable(2884);

        Tessellator tess = Tessellator.instance;

        // Interpolated player position
        float  playerY = (float)(this.mc.renderViewEntity.lastTickPosY +
                (this.mc.renderViewEntity.posY - this.mc.renderViewEntity.lastTickPosY) * (double)par1);
        double playerX = this.mc.renderViewEntity.prevPosX +
                (this.mc.renderViewEntity.posX - this.mc.renderViewEntity.prevPosX) * (double)par1;
        double playerZ = this.mc.renderViewEntity.prevPosZ +
                (this.mc.renderViewEntity.posZ - this.mc.renderViewEntity.prevPosZ) * (double)par1;

        double tick      = (float)this.cloudTickCounter + par1;
        float  cloudBase = this.theWorld.provider.getCloudHeight() - playerY + 0.33f;
        float  timePhase = (float)tick * 0.01f;

        this.renderEngine.bindTexture(locationCloudsPng);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);

        // Cloud base color
        Vec3 col = (this.mc.thePlayer.dimension == NMFields.UNDERWORLD_DIMENSION)
                ? Vec3.createVectorHelper(1.0, 1.0, 1.0)
                : this.theWorld.getCloudColour(par1);

        float cr = (float)col.xCoord;
        float cg = (float)col.yCoord;
        float cb = (float)col.zCoord;

        if (this.mc.gameSettings.anaglyph) {
            float mono = (cr * 30.0f + cg * 59.0f + cb * 11.0f) / 100.0f;
            float cyan = (cr * 30.0f + cg * 70.0f)               / 100.0f;
            float red  = (cr * 30.0f + cb * 70.0f)               / 100.0f;
            cr = mono; cg = cyan; cb = red;
        }


        final boolean cmR, cmG, cmB;
        if (this.mc.gameSettings.anaglyph) {
            cmR = EntityRenderer.anaglyphField != 0;
            cmG = EntityRenderer.anaglyphField == 0;
            cmB = EntityRenderer.anaglyphField == 0;
        } else {
            cmR = true;
            cmG = true;
            cmB = true;
        }

        final float CLOUD_SCALE = 12.0f;
        final float CLOUD_THICKNESS = 4.0f;
        final float UV_STEP = 0.00390625f;
        final int NUM_LAYERS = 3;
        final float LAYER_SPACING = 11.0f;
        final float BASE_ALPHA = 0.9f;
        final int TILE_SIZE = 4;
        final int GRID_HALF = 8;
        final float WAVE_FREQ = 0.2f;
        final float WAVE_AMP = 7.0f;

        // pre-allocated once
        float[][] pts = new float[8][3];

        GL11.glScalef(CLOUD_SCALE, 1.0f, CLOUD_SCALE);

        for (int layer = 0; layer < NUM_LAYERS; ++layer) {
            float alpha = BASE_ALPHA / NUM_LAYERS * 1.5f;
            float brightness = 0.7f + 0.1f * layer;
            double speed = 0.03 - 0.005 * layer;

            // bake brightness into color once per layer
            float lr = cr * brightness;
            float lg = cg * brightness;
            float lb = cb * brightness;

            // UV scroll
            double lVar8  = (playerX + tick * speed) / CLOUD_SCALE + layer * 48.0;
            double lVar10 = playerZ / CLOUD_SCALE + 0.33 + layer * 37.0;

            lVar8  -= (double)(MathHelper.floor_double(lVar8  / 2048.0) * 2048);
            lVar10 -= (double)(MathHelper.floor_double(lVar10 / 2048.0) * 2048);

            // cache floor results
            double fVar8  = MathHelper.floor_double(lVar8);
            double fVar10 = MathHelper.floor_double(lVar10);

            float uvOffU = (float)fVar8  * UV_STEP;
            float uvOffV = (float)fVar10 * UV_STEP;
            float fracU  = (float)(lVar8  - fVar8);
            float fracV  = (float)(lVar10 - fVar10);
            float layerY = cloudBase + layer * LAYER_SPACING;

            for (int pass = 0; pass < 2; ++pass) {
                if (pass == 0) {
                    GL11.glColorMask(false, false, false, false);
                } else {
                    GL11.glColorMask(cmR, cmG, cmB, true);
                }

                // single draw call per layer per pass
                tess.startDrawingQuads();

                for (int tx = -GRID_HALF + 1; tx <= GRID_HALF; ++tx) {
                    float tileX   = tx * TILE_SIZE;
                    float originX = tileX - fracU;
                    float baseU   = tileX * UV_STEP + uvOffU; // hoisted from sx loop

                    for (int tz = -GRID_HALF + 1; tz <= GRID_HALF; ++tz) {
                        float tileZ   = tz * TILE_SIZE;
                        float originZ = tileZ - fracV;
                        float baseV   = tileZ * UV_STEP + uvOffV; // hoisted from sz loop

                        for (int sx = 0; sx < TILE_SIZE; ++sx) {
                            float x0   = originX + sx;
                            float x1   = x0 + 1.0f;
                            float subU = baseU + sx * UV_STEP;

                            for (int sz = 0; sz < TILE_SIZE; ++sz) {
                                float z0   = originZ + sz;
                                float z1   = z0 + 1.0f;
                                float subV = baseV + sz * UV_STEP;

                                // offTop == offBottom always in original
                                float off00 = computeOffset(x0, z0, WAVE_AMP, WAVE_FREQ, timePhase);
                                float off10 = computeOffset(x1, z0, WAVE_AMP, WAVE_FREQ, timePhase);
                                float off11 = computeOffset(x1, z1, WAVE_AMP, WAVE_FREQ, timePhase);
                                float off01 = computeOffset(x0, z1, WAVE_AMP, WAVE_FREQ, timePhase);

                                float yb00 = layerY + off00,  yt00 = yb00 + CLOUD_THICKNESS;
                                float yb10 = layerY + off10,  yt10 = yb10 + CLOUD_THICKNESS;
                                float yb11 = layerY + off11,  yt11 = yb11 + CLOUD_THICKNESS;
                                float yb01 = layerY + off01,  yt01 = yb01 + CLOUD_THICKNESS;

                                pts[0][0]=x0; pts[0][1]=yb00; pts[0][2]=z0;
                                pts[1][0]=x1; pts[1][1]=yb10; pts[1][2]=z0;
                                pts[2][0]=x1; pts[2][1]=yb11; pts[2][2]=z1;
                                pts[3][0]=x0; pts[3][1]=yb01; pts[3][2]=z1;
                                pts[4][0]=x0; pts[4][1]=yt00; pts[4][2]=z0;
                                pts[5][0]=x1; pts[5][1]=yt10; pts[5][2]=z0;
                                pts[6][0]=x1; pts[6][1]=yt11; pts[6][2]=z1;
                                pts[7][0]=x0; pts[7][1]=yt01; pts[7][2]=z1;

                                drawCuboid(tess, pts, lr, lg, lb, alpha, subU, subV, UV_STEP);
                            }
                        }
                    }
                }

                tess.draw();
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
//        tess.setColorRGBA_F(r * 0.9f, g * 0.9f, b * 0.9f, a);
        tess.setColorRGBA_F(r * 0.3f, g * 0.3f, b * 0.3f, a);
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


    @Unique private float horrorSkyIntensity = 0f;
    @Unique private float horrorSkyRotation = 0f;
    @Unique private long horrorSkyLastUpdate = 0L;

    @Inject(method = "renderSky", at = @At("TAIL"))
    private void renderHorrorSkyEffects(float par1, CallbackInfo ci) {
        if (this.mc.thePlayer == null || this.mc.theWorld == null) {
            return;
        }

        double ritualRange = 128.0;
        float targetIntensity = NMUtils.getRitualIntensity(this.mc.thePlayer, ritualRange);

        float fadeSpeed = 0.02f;
        if (this.horrorSkyIntensity < targetIntensity) {
            this.horrorSkyIntensity = Math.min(targetIntensity, this.horrorSkyIntensity + fadeSpeed);
        } else if (this.horrorSkyIntensity > targetIntensity) {
            this.horrorSkyIntensity = Math.max(targetIntensity, this.horrorSkyIntensity - fadeSpeed);
        }

        // skip rendering if too low
        if (this.horrorSkyIntensity > 0.01f) {

            // animation state
            long currentTime = System.currentTimeMillis();
            if (currentTime - horrorSkyLastUpdate > 50) {
                horrorSkyRotation += 0.5f * this.horrorSkyIntensity;
                horrorSkyLastUpdate = currentTime;
            }

            // Render horror sky effects
            renderRitualSkybox();
            renderSkyboxObjects();
            return;
        }
        if(NMEvents.noEventsActive()){
            this.skyboxTargetAlpha = 0f;
            this.skyboxCurrentAlpha = 0f;
        }


        if(NMEvents.SimpleEvent.HELL.isActive()){
            setSkyboxTint(0xAA2020, 1.0f);
            renderBasicSkybox(SKYBOX_WHITE);

//            renderColoredSky(0xAA2020, 0.5f);

        }
    }
    private void renderColoredSky(int skyColor, float alpha) {
        // changes sky color, has weird horizon effects
        float red = (float)(skyColor >> 16 & 255) / 255.0F;
        float green = (float)(skyColor >> 8 & 255) / 255.0F;
        float blue = (float)(skyColor & 255) / 255.0F;

        if (this.mc.gameSettings.anaglyph) {
            float gray = (red * 30.0F + green * 59.0F + blue * 11.0F) / 100.0F;
            float adjustedGreen = (red * 30.0F + green * 70.0F) / 100.0F;
            float adjustedBlue = (red * 30.0F + blue * 70.0F) / 100.0F;

            red = gray;
            green = adjustedGreen;
            blue = adjustedBlue;
        }

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDepthMask(false);

        // upper sky dome
        GL11.glColor4f(red, green, blue, alpha);
        GL11.glEnable(GL11.GL_FOG);
        GL11.glCallList(this.glSkyList);
        GL11.glDisable(GL11.GL_FOG);

        // lower sky dome
        GL11.glColor4f(red, green, blue, alpha);
        GL11.glCallList(this.glSkyList2);

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDepthMask(true);
    }

    @Unique
    private void renderRitualSkybox() {
        final float radius = 120;
        final int latSteps = 12;
        final int lonSteps = 48;

        if (this.mc.renderEngine == null) return;

        try {
            this.mc.renderEngine.bindTexture(SKYBOX_RED);
        } catch (Exception e) {
            return;
        }

        GL11.glPushAttrib(
                GL11.GL_ENABLE_BIT
                        | GL11.GL_COLOR_BUFFER_BIT
                        | GL11.GL_DEPTH_BUFFER_BIT
                        | GL11.GL_CURRENT_BIT
                        | GL11.GL_TEXTURE_BIT
        );

        GL11.glPushMatrix();

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glDepthMask(false);

        GL11.glColor4f(1.0f, 1.0f, 1.0f, this.horrorSkyIntensity);

        GL11.glRotatef(this.horrorSkyRotation * 0.1f, 0.0f, 1.0f, 0.0f);

        Tessellator tess = Tessellator.instance;

        for (int lat = 0; lat < latSteps; lat++) {
            double minTheta = -Math.PI / 6.0;
            double maxTheta = Math.PI / 2.0;
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

                float u0 = (float) lon / lonSteps;
                float u1 = (float) (lon + 1) / lonSteps;
                float v0 = (float) lat / latSteps;
                float v1 = (float) (lat + 1) / latSteps;

                tess.addVertexWithUV(x10, y1, z10, u0, v1);
                tess.addVertexWithUV(x11, y1, z11, u1, v1);
                tess.addVertexWithUV(x01, y0, z01, u1, v0);
                tess.addVertexWithUV(x00, y0, z00, u0, v0);
            }
            tess.draw();
        }

        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

    @Unique private int skyboxTargetColor = 0xFFFFFF;
    @Unique private float skyboxTargetAlpha = 1.0f;
    @Unique private float skyboxCurrentR = 1.0F;
    @Unique private float skyboxCurrentG = 1.0F;
    @Unique private float skyboxCurrentB = 1.0F;
    @Unique private float skyboxCurrentAlpha = 0f;
    @Unique private long skyboxLastUpdateTime = System.nanoTime();


    @Unique
    public void setSkyboxTint(int rgb, float alpha) {
        this.skyboxTargetColor = rgb & 0xFFFFFF;

//        NMUtils.setSkyBrightness(new float[]{this.skyboxCurrentR, this.skyboxCurrentG, this.skyboxCurrentB, this.skyboxCurrentAlpha});
        this.skyboxTargetAlpha = alpha;
    }


    @Unique
    private void renderBasicSkybox(ResourceLocation location) {
        final float radius = 60;
        final int latSteps = 12;
        final int lonSteps = 48;

        if (this.mc.renderEngine == null) return;

        try {
            this.mc.renderEngine.bindTexture(location);
        } catch (Exception e) {
            return;
        }

        long now = System.nanoTime();
        float deltaSeconds = (now - this.skyboxLastUpdateTime) * 1.0E-9F;
        this.skyboxLastUpdateTime = now;

        float targetR = ((this.skyboxTargetColor >> 16) & 255) / 255.0F;
        float targetG = ((this.skyboxTargetColor >> 8) & 255) / 255.0F;
        float targetB = (this.skyboxTargetColor & 255) / 255.0F;

        float fadeSpeed = 5.0F;
        float blend = 1.0F - (float)Math.exp(-fadeSpeed * deltaSeconds);
        if (Math.abs(this.skyboxCurrentAlpha - this.skyboxTargetAlpha) > 0.01f) {
            this.skyboxCurrentAlpha = NMUtils.lerp(0.005f, this.skyboxCurrentAlpha, this.skyboxTargetAlpha);
        }



        this.skyboxCurrentR += (targetR - this.skyboxCurrentR) * blend;
        this.skyboxCurrentG += (targetG - this.skyboxCurrentG) * blend;
        this.skyboxCurrentB += (targetB - this.skyboxCurrentB) * blend;

        GL11.glPushAttrib(
                GL11.GL_ENABLE_BIT
                        | GL11.GL_COLOR_BUFFER_BIT
                        | GL11.GL_DEPTH_BUFFER_BIT
                        | GL11.GL_CURRENT_BIT
                        | GL11.GL_TEXTURE_BIT
        );

        GL11.glPushMatrix();

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glDepthMask(false);

        GL11.glColor4f(
                this.skyboxCurrentR,
                this.skyboxCurrentG,
                this.skyboxCurrentB,
                this.skyboxCurrentAlpha
        );

        Tessellator tess = Tessellator.instance;

        for (int lat = 0; lat < latSteps; lat++) {
            double minTheta = -Math.PI / 6.0;
            double maxTheta = Math.PI / 2.0;

            double theta0 = minTheta + (maxTheta - minTheta) * ((double)lat / latSteps);
            double theta1 = minTheta + (maxTheta - minTheta) * ((double)(lat + 1) / latSteps);

            double y0 = Math.sin(theta0) * radius;
            double y1 = Math.sin(theta1) * radius;

            double r0 = Math.cos(theta0) * radius;
            double r1 = Math.cos(theta1) * radius;

            tess.startDrawingQuads();

            for (int lon = 0; lon < lonSteps; lon++) {
                double phi0 = 2.0 * Math.PI * ((double)lon / lonSteps);
                double phi1 = 2.0 * Math.PI * ((double)(lon + 1) / lonSteps);

                double x00 = r0 * Math.cos(phi0);
                double z00 = r0 * Math.sin(phi0);

                double x01 = r0 * Math.cos(phi1);
                double z01 = r0 * Math.sin(phi1);

                double x10 = r1 * Math.cos(phi0);
                double z10 = r1 * Math.sin(phi0);

                double x11 = r1 * Math.cos(phi1);
                double z11 = r1 * Math.sin(phi1);

                float u0 = (float)lon / lonSteps;
                float u1 = (float)(lon + 1) / lonSteps;
                float v0 = (float)lat / latSteps;
                float v1 = (float)(lat + 1) / latSteps;

                tess.addVertexWithUV(x10, y1, z10, u0, v1);
                tess.addVertexWithUV(x11, y1, z11, u1, v1);
                tess.addVertexWithUV(x01, y0, z01, u1, v0);
                tess.addVertexWithUV(x00, y0, z00, u0, v0);
            }

            tess.draw();
        }

        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

    @Unique
    private static final SkyboxObject[] EYES = {
            new SkyboxObject(11.0F, 22.0F),
            new SkyboxObject( 11.0f,  22.0f ),
            new SkyboxObject( 29.0f,  81.0f ),
            new SkyboxObject( 47.0f,  52.0f ),
            new SkyboxObject( 63.0f,  19.0f ),
            new SkyboxObject(101.0f,  36.0f ),
            new SkyboxObject(117.0f,  57.0f ),
            new SkyboxObject(133.0f,  14.0f ),
            new SkyboxObject(151.0f,  65.0f ),
            new SkyboxObject(169.0f,  42.0f ),
            new SkyboxObject(205.0f,  24.0f ),
            new SkyboxObject(223.0f,  61.0f ),
            new SkyboxObject(241.0f,  33.0f ),
            new SkyboxObject(277.0f,  17.0f ),
            new SkyboxObject(295.0f,  53.0f ),
            new SkyboxObject(313.0f,  27.0f ),
            new SkyboxObject(349.0f,  46.0f )
    };

    @Unique
    private void renderSkyboxObjects() {
        if (this.mc.renderEngine == null) return;

        try {
            this.mc.renderEngine.bindTexture(STARE);
        } catch (Exception e) {
            return;
        }

        EntityLivingBase player = mc.thePlayer;
        Vec3 lookVec = player.getLookVec();

        final float eyeRadius = 100.0f;
        final float eyeSize   = 8.0f + 4.0f * this.horrorSkyIntensity;
        final float pulse     = 1.0f + 0.03f * (float)Math.sin(System.currentTimeMillis() * 0.003);

        Tessellator tess = Tessellator.instance;

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDepthMask(false);

        for (SkyboxObject eye : EYES) {

            float yawRad   = (float)Math.toRadians(eye.yaw);
            float pitchRad = (float)Math.toRadians(eye.pitch);

            double dirX = -MathHelper.cos(pitchRad) * MathHelper.sin(yawRad);
            double dirY =  MathHelper.sin(pitchRad);
            double dirZ = -MathHelper.cos(pitchRad) * MathHelper.cos(yawRad);

            Vec3 eyeDir = Vec3.createVectorHelper(dirX, dirY, dirZ).normalize();

            double dot = lookVec.dotProduct(eyeDir);
            boolean lookedAt = dot > 0.98;

            // smooth animation
            long now = System.currentTimeMillis();
            float delta = (now - eye.lastRenderMs) / 1000f;
            eye.lastRenderMs = now;

            // override state if player looks at eye
            if (lookedAt && eye.state != SkyboxObject.EyeState.CLOSING && eye.state != SkyboxObject.EyeState.CLOSED) {
                eye.state = SkyboxObject.EyeState.CLOSING;
                eye.stateTimer = now;
            }

            float openCloseSpeed = 6.0f;

            switch (eye.state) {
                case CLOSED:
                    eye.openness = 0f;
                    if (now - eye.stateTimer > eye.nextOpenDelay && !lookedAt) {
                        eye.state = SkyboxObject.EyeState.OPENING;
                        eye.stateTimer = now;
                    }
                    break;

                case OPENING:
                    eye.openness = Math.min(1f, eye.openness + delta * openCloseSpeed);
                    if (eye.openness >= 1f) {
                        eye.state = SkyboxObject.EyeState.OPEN;
                        eye.stateTimer = now;
                    }
                    break;

                case OPEN:
                    eye.openness = 1f;
                    if (now - eye.stateTimer > eye.openDuration) {
                        eye.state = SkyboxObject.EyeState.CLOSING;
                        eye.stateTimer = now;
                    }
                    break;

                case CLOSING:
                    eye.openness = Math.max(0f, eye.openness - delta * openCloseSpeed);
                    if (eye.openness <= 0f) {
                        eye.state = SkyboxObject.EyeState.CLOSED;
                        eye.stateTimer = now;
                        // randomize next cycle
                        eye.nextOpenDelay = (long)(2000 + Math.random() * 5000);
                        eye.openDuration  = (long)(300  + Math.random() * 600);
                    }
                    break;
            }

            GL11.glPushMatrix();

            // rotate to sky position
            GL11.glRotatef(eye.yaw, 0.0f, 1.0f, 0.0f);
            GL11.glRotatef(eye.pitch, 1.0f, 0.0f, 0.0f);

            // move outward into sky sphere
            GL11.glTranslatef(0.0f, 0.0f, -eyeRadius);

            // scale quad
            GL11.glScalef(
                    pulse * eyeSize,
                    pulse * eyeSize * eye.openness, // vertical squash - the eye shuts
                    1.0f
            );

            float brightness = 0.3F + 0.7F * eye.openness;
            GL11.glColor4f(
                    brightness,
                    brightness,
                    brightness,
                    (this.horrorSkyIntensity / 9.0F)
            );

            // billboard quad
            tess.startDrawingQuads();

            tess.addVertexWithUV(-1.0, -1.0, 0.0, 0.0, 0.0);
            tess.addVertexWithUV( 1.0, -1.0, 0.0, 1.0, 0.0);
            tess.addVertexWithUV( 1.0,  1.0, 0.0, 1.0, 1.0);
            tess.addVertexWithUV(-1.0,  1.0, 0.0, 0.0, 1.0);

            tess.draw();

            GL11.glPopMatrix();
        }
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
    }
}
