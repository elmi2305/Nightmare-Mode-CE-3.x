package com.itlesports.nightmaremode;

import net.minecraft.src.Vec3;
import net.minecraft.src.WorldProvider;

public class WorldProviderUnderworld extends WorldProvider {
    @Override
    public String getDimensionName() {
        return "Underworld";
    }

    @Override
    public Vec3 getFogColor(float par1, float par2) {
        return Vec3.createVectorHelper(0,0,0);
    }
    @Override
    public void generateLightBrightnessTable() {
        float var1 = 0.1F;

        for(int var2 = 0; var2 <= 10; ++var2) {
            float var3 = 1.0F - (float)var2 / 10.0F;
            this.lightBrightnessTable[var2] = (1.0F - var3) / (var3 * 3.0F + 1.0F) * (1.0F - var1) + var1;
        }

    }

    public boolean canCoordinateBeSpawn(int par1, int par2) {
        return false;
    }

    public float calculateCelestialAngle(long par1, float par3) {
        return 0.5F;
    }

    public boolean canRespawnHere() {
        return false;
    }

    public boolean doesXZShowFog(int par1, int par2) {
        return true;
    }
}
