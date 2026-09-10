package com.itlesports.nightmaremode.mixin.biomegen;

import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.util.NMUtils;
import net.minecraft.src.NoiseGenerator;
import net.minecraft.src.NoiseGeneratorOctaves;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(NoiseGeneratorOctaves.class)
public class NoiseGeneratorOctavesMixin extends NoiseGenerator {
    @Unique private static Random rand = new Random();

    @Inject(method = "generateNoiseOctaves*", at = @At("TAIL"), cancellable = true)
    private void injectCraziness(double[] noiseArray, int x, int y, int z, int xSize, int ySize, int zSize, double xScale, double yScale, double zScale, CallbackInfoReturnable<double[]> cir) {
        if (NightmareMode.isAprilFools) {
            double[] modifiedNoise = new double[noiseArray.length];
            int choice = 0;
            boolean intenseCorruption = NMUtils.isIntenseCorruption();
            int chanceBase = intenseCorruption ? 79 : 90;
            int chanceIntensityMultiplier = intenseCorruption ? 3 : 1;
            if (!intenseCorruption) {
                choice = rand.nextInt(100);
            }

            for (int i = 0; i < modifiedNoise.length; i++) {

                if(intenseCorruption){
                    choice = rand.nextInt(100);
                }

                if (choice < chanceBase) {

                    modifiedNoise[i] = noiseArray[i] * 3;
                } else if (choice < chanceBase + 2 * chanceIntensityMultiplier) {

                    double baseNoise = Math.abs(noiseArray[i]) * 1.1 * chanceIntensityMultiplier;
                    modifiedNoise[i] = Math.pow(baseNoise, 1.35) * (noiseArray[i] > 0 ? 1 : -1) * 6 * chanceIntensityMultiplier;
                    if (i > 0) modifiedNoise[i] = (modifiedNoise[i] + modifiedNoise[i - 1]) / 2.0;
                    if (modifiedNoise[i] > 80) modifiedNoise[i] = 80 + (modifiedNoise[i] - 80) * 0.5;
                } else if (choice < chanceBase + 5 * chanceIntensityMultiplier) {

                    double chaosFactor = 1 + (rand.nextDouble() * 1.15);
                    modifiedNoise[i] = noiseArray[i] * chaosFactor * 3 * chanceIntensityMultiplier;
                } else if (choice < chanceBase + 7 * chanceIntensityMultiplier) {

                    modifiedNoise[i] = Math.tan(noiseArray[i] * 0.4) * 7 * chanceIntensityMultiplier;
                } else {

                    modifiedNoise[i] = Math.sin(noiseArray[i] * 10) * 10 * chanceIntensityMultiplier;
                }
            }
            cir.setReturnValue(modifiedNoise);
        }
    }
}
