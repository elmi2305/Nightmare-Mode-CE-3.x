package com.itlesports.nightmaremode.mixin;

import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.NightmareUtils;
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
            boolean intenseCorruption = NightmareUtils.isIntenseCorruption();
            int chanceBase = intenseCorruption ? 79 : 90; // 90 by default, reduced to 79 during chaos mode
            int chanceIntensityMultiplier = intenseCorruption ? 3 : 1; // makes the individual choices more likely. also greatly amplifies the chaos
            if (!intenseCorruption) {
                choice = rand.nextInt(100); // 1 in 100 chance to switch noise style
            }


            for (int i = 0; i < modifiedNoise.length; i++) {
                // moving the choice declaration here results in exponentially more chaos
                if(intenseCorruption){
                    choice = rand.nextInt(100); // 1 in 100 chance to switch noise style
                }

                if (choice < chanceBase) {
                    // Default noise (normal chaos)
                    modifiedNoise[i] = noiseArray[i] * 3; // Reduced from *8
                } else if (choice < chanceBase + 2 * chanceIntensityMultiplier) {
                    // Flying Islands mode - Lowered intensity
                    double baseNoise = Math.abs(noiseArray[i]) * 1.1 * chanceIntensityMultiplier;
                    modifiedNoise[i] = Math.pow(baseNoise, 1.35) * (noiseArray[i] > 0 ? 1 : -1) * 6 * chanceIntensityMultiplier; // Reduced from *12
                    if (i > 0) modifiedNoise[i] = (modifiedNoise[i] + modifiedNoise[i - 1]) / 2.0;
                    if (modifiedNoise[i] > 80) modifiedNoise[i] = 80 + (modifiedNoise[i] - 80) * 0.5;
                } else if (choice < chanceBase + 5 * chanceIntensityMultiplier) {
                    // Chaotic terrain mode - Less aggressive random factor
                    double chaosFactor = 1 + (rand.nextDouble() * 1.15); // Lowered from *1.5
                    modifiedNoise[i] = noiseArray[i] * chaosFactor * 3 * chanceIntensityMultiplier; // Lowered from *5
                } else if (choice < chanceBase + 7 * chanceIntensityMultiplier) {
                    // Rolling hills mode - More gentle hills
                    modifiedNoise[i] = Math.tan(noiseArray[i] * 0.4) * 7 * chanceIntensityMultiplier; // Reduced from *12
                } else {
                    // Spiky terrain mode - Slightly toned down spikes
                    modifiedNoise[i] = Math.sin(noiseArray[i] * 10) * 10 * chanceIntensityMultiplier; // Reduced from *15
                }
            }
            cir.setReturnValue(modifiedNoise);
        }
    }
}
