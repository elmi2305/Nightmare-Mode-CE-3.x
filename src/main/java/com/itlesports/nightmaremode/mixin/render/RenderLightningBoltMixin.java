package com.itlesports.nightmaremode.mixin.render;

import com.itlesports.nightmaremode.util.NMUtils;
import net.minecraft.src.EntityLightningBolt;
import net.minecraft.src.RenderLightningBolt;
import net.minecraft.src.Tessellator;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.Random;

@Mixin(RenderLightningBolt.class)
public class RenderLightningBoltMixin {
//    @ModifyArgs(method = "doRenderLightningBolt", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/Tessellator;setColorRGBA_F(FFFF)V"))
//    private void changeLightningColor(Args args){
//        if (NMUtils.getIsBloodMoon()) {
//            args.set(0,1.0f);
//            args.set(1,0.05f);
//            args.set(2,0.05f);
//        }
//    }
    @Inject(method = "doRenderLightningBolt", at = @At("HEAD"), cancellable = true)
    private void renderBloodMoon(EntityLightningBolt entityBolt, double par2, double par4, double par6, float par8, float par9, CallbackInfo ci){
        if(NMUtils.getIsBloodMoon()) {
            renderBloodMoonLightningBolt(entityBolt, par2, par4, par6, par8, par9);
            ci.cancel();
        }
    }


    @Unique
    public void renderBloodMoonLightningBolt(EntityLightningBolt entityBolt, double par2, double par4, double par6, float par8, float par9) {
        Tessellator var10 = Tessellator.instance;
        GL11.glDisable(3553);
        GL11.glDisable(2896);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 1);

        int segments = 12;
        double[] var11 = new double[segments];
        double[] var12 = new double[segments];
        double var13 = 0.0;
        double var15 = 0.0;
        Random var17 = new Random(entityBolt.renderSeed);

        for (int var18 = segments - 1; var18 >= 0; --var18) {
            var11[var18] = var13;
            var12[var18] = var15;
            var13 += (double)(var17.nextInt(15) - 7);
            var15 += (double)(var17.nextInt(15) - 7);
        }

        for (int var45 = 0; var45 < 6; ++var45) {  // Increased from 4
            Random var46 = new Random(entityBolt.renderSeed);
            for (int var19 = 0; var19 < 5; ++var19) {
                int var20 = segments - 1;
                int var21 = 0;
                if (var19 > 0) {
                    var20 = segments - 1 - var19 * 2;
                }
                if (var19 > 0) {
                    var21 = Math.max(0, var20 - 3);
                }

                double var22 = var11[var20] - var13;
                double var24 = var12[var20] - var15;

                for (int var26 = var20; var26 >= var21; --var26) {
                    double var27 = var22;
                    double var29 = var24;

                    if (var19 == 0) {
                        var22 += (double)(var46.nextInt(13) - 6);
                        var24 += (double)(var46.nextInt(13) - 6);
                    } else {
                        var22 += (double)(var46.nextInt(37) - 18);
                        var24 += (double)(var46.nextInt(37) - 18);
                    }

                    var10.startDrawing(5);

                    float intensity = 0.6f + var46.nextFloat() * 0.4f;
                    float r = 0.95f * intensity;
                    float g = 0.15f * intensity + (var45 % 2 == 0 ? 0.05f : 0.0f);
                    float b = 0.08f * intensity;
                    float alpha = 0.35f + (var45 * 0.03f);

                    var10.setColorRGBA_F(r, g, b, alpha);

                    double var32 = 0.08 + (double)var45 * 0.18;
                    if (var19 == 0) {
                        var32 *= (double)var26 * 0.09 + 1.2;
                    }
                    double var34 = 0.08 + (double)var45 * 0.18;
                    if (var19 == 0) {
                        var34 *= (double)(var26 - 1) * 0.09 + 1.2;
                    }

                    for (int var36 = 0; var36 < 5; ++var36) {
                        double var37 = par2 + 0.5 - var32;
                        double var39 = par6 + 0.5 - var32;
                        if (var36 == 1 || var36 == 2) {
                            var37 += var32 * 2.0;
                        }
                        if (var36 == 2 || var36 == 3) {
                            var39 += var32 * 2.0;
                        }

                        double var41 = par2 + 0.5 - var34;
                        double var43 = par6 + 0.5 - var34;
                        if (var36 == 1 || var36 == 2) {
                            var41 += var34 * 2.0;
                        }
                        if (var36 == 2 || var36 == 3) {
                            var43 += var34 * 2.0;
                        }

                        var10.addVertex(var41 + var22, par4 + (double)(var26 * 16), var43 + var24);
                        var10.addVertex(var37 + var27, par4 + (double)((var26 + 1) * 16), var39 + var29);
                    }
                    var10.draw();
                }
            }
        }

        Random chaosRand = new Random(entityBolt.renderSeed * 17L);
        for (int vein = 0; vein < 6; ++vein) {                    // More veins
            double baseOffsetX = chaosRand.nextDouble() * 5.0 - 2.5;
            double baseOffsetZ = chaosRand.nextDouble() * 5.0 - 2.5;

            int startSeg = 5 + chaosRand.nextInt(segments - 8);   // Start from various heights
            int veinLength = 5 + chaosRand.nextInt(6);             // Varying length

            for (int subLayer = 0; subLayer < 4; ++subLayer) {
                var10.startDrawing(5);

                // Darker, bloodier vein color
                float veinAlpha = 0.24f - subLayer * 0.04f;
                var10.setColorRGBA_F(0.82f, 0.03f, 0.04f, veinAlpha);

                double tx = 0.035 + subLayer * 0.085;

                for (int c = 0; c < 5; ++c) {
                    int seg1 = startSeg;
                    int seg2 = Math.max(0, startSeg - veinLength);

                    double vx1 = var11[seg1] - var13 + baseOffsetX;
                    double vz1 = var12[seg1] - var15 + baseOffsetZ;

                    double vx2 = var11[seg2] - var13 + baseOffsetX * 0.6;
                    double vz2 = var12[seg2] - var15 + baseOffsetZ * 0.6;

                    // Add extra chaos per corner
                    double chaosX = chaosRand.nextDouble() * 1.8 - 0.9;
                    double chaosZ = chaosRand.nextDouble() * 1.8 - 0.9;

                    double x1 = par2 + 0.5 - tx + chaosX;
                    double z1 = par6 + 0.5 - tx + chaosZ;
                    if (c == 1 || c == 2) x1 += tx * 2.0;
                    if (c == 2 || c == 3) z1 += tx * 2.0;

                    double x2 = par2 + 0.5 - tx * 0.7 + chaosX * 0.7;
                    double z2 = par6 + 0.5 - tx * 0.7 + chaosZ * 0.7;
                    if (c == 1 || c == 2) x2 += tx * 2.0 * 0.7;
                    if (c == 2 || c == 3) z2 += tx * 2.0 * 0.7;

                    var10.addVertex(x2 + vx2, par4 + (double)(seg2 * 16), z2 + vz2);
                    var10.addVertex(x1 + vx1, par4 + (double)(seg1 * 16), z1 + vz1);
                }
                var10.draw();
            }
        }

        GL11.glDisable(3042);
        GL11.glEnable(2896);
        GL11.glEnable(3553);
    }
}
