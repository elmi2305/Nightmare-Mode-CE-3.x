package com.itlesports.nightmaremode.underworld;

import btw.community.nightmaremode.NightmareMode;
import net.minecraft.src.*;

public class WorldProviderUnderworld extends WorldProvider {
    private float[] colorsSunriseSunsetUnderworld = new float[4];

    public WorldProviderUnderworld() {
    }

    @Override
    public String getDimensionName() {
        return "Underworld";
    }


    @Override
    public void generateLightBrightnessTable() {
        float var1 = 0.1F;

        for(int var2 = 0; var2 <= 10; ++var2) {
            float var3 = 1.0F - (float)var2 / 10.0F;
            this.lightBrightnessTable[var2] = (1.0F - var3) / (var3 * 3.0F + 1.0F) * (1.0F - var1) + var1;
        }
    }

    @Override
    public void registerWorldChunkManager() {
        // Simple for now: use the base WorldChunkManager. Replace later with biome logic.
        this.worldChunkMgr = new WorldChunkManager(worldObj.getSeed(),WorldType.DEFAULT);
        this.dimensionId = NightmareMode.UNDERWORLD_DIMENSION;
        this.isHellWorld = false;
        this.hasNoSky = false;
    }


    @Override
    public boolean canRespawnHere() {
        return false; // Forces returning via portal
    }

//    @Override
//    public boolean isSurfaceWorld() {
//        return false; // Treat differently than overworld
//    }

    public boolean isSurfaceWorld() {
        return true;
    }

    @Override
    public float getCloudHeight() {
        return 133f;
    }

    @Override
    public boolean isSkyColored() {
        return true;
    }


    @Override
    public Vec3 getFogColor(float celestialAngle, float partialTicks) {
        // Desaturated bluish-gray fog
        float fogStrength = 0.4f + (MathHelper.cos(celestialAngle * (float)Math.PI * 2.0f) * 0.1f);
        return Vec3.createVectorHelper(0.08f * fogStrength, 0.02f * fogStrength, 0.02f * fogStrength);
//        return this.worldObj.getWorldVec3Pool().getVecFromPool(0.08f * fogStrength, 0.02f * fogStrength, 0.02f * fogStrength);
    }
//    @Override
//    public Vec3 getFogColor(float par1, float par2) {
//        return Vec3.createVectorHelper(0,0,0);
//    }

    @Override
    public float[] calcSunriseSunsetColors(float celestialAngle, float fPartialTicks) {
        float midDayCosValue;
        float sunsetSunriseWindow = 0.4f;
        float dailySolarEffect = MathHelper.cos(celestialAngle * (float)Math.PI * 2.0f);

        midDayCosValue = 0.0f;

        if (dailySolarEffect >= midDayCosValue - sunsetSunriseWindow && dailySolarEffect <= midDayCosValue + sunsetSunriseWindow) {
            // Calculate how far into the transition window we are (normalized 0.0 to 1.0)
            float transitionProgress = (dailySolarEffect - midDayCosValue) / sunsetSunriseWindow * 0.5f + 0.5f;

            // Calculate the alpha/opacity for the sunset/sunrise effect
            float sunTransitionAlpha = 1.0f - (1.0f - MathHelper.sin(transitionProgress * (float)Math.PI)) * 0.99f;
            sunTransitionAlpha *= sunTransitionAlpha;

            this.colorsSunriseSunsetUnderworld[0] = transitionProgress * 0.7f + 0.3f; // r
            this.colorsSunriseSunsetUnderworld[1] = transitionProgress * transitionProgress * 0.7f + 0.3f; // g
            this.colorsSunriseSunsetUnderworld[2] = transitionProgress * transitionProgress * 0.3f + 0.7f; // b
            this.colorsSunriseSunsetUnderworld[3] = sunTransitionAlpha;

            return this.colorsSunriseSunsetUnderworld;
        }
        return null;
    }

    @Override
    public IChunkProvider createChunkGenerator() {
//        return new ChunkProviderGenerate(this.worldObj, this.worldObj.getSeed(),true);
        return new ChunkProviderGenerateUnderworld(this.worldObj, this.worldObj.getSeed());
    }

    @Override
    public double getVoidFogYFactor() {
        return 0.02; // Thick void fog low to the ground
    }

    @Override
    public boolean getWorldHasVoidParticles() {
        return true;
    }

    @Override
    public boolean doesXZShowFog(int x, int z) {
        return false; // Distant fog everywhere
    }

    public boolean canCoordinateBeSpawn(int par1, int par2) {
        return false;
    }

//    public float calculateCelestialAngle(long par1, float par3) {
//        return 0.5F;
//    }

    @Override
    public ChunkCoordinates getEntrancePortalLocation() {
        // Send them either to the world spawn or a fixed “return portal” spot.

        ChunkCoordinates spawn = worldObj.getSpawnPoint();
        return new ChunkCoordinates(spawn.posX, spawn.posY, spawn.posZ);
    }

}
