package com.itlesports.nightmaremode.underworld;

import btw.community.nightmaremode.NightmareMode;
import net.minecraft.src.*;

public class WorldProviderUnderworld extends WorldProvider {
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
        this.worldChunkMgr = new WorldChunkManager(worldObj);
        this.dimensionId = NightmareMode.UNDERWORLD_DIMENSION;
        this.isHellWorld = false;
        this.hasNoSky = false;
    }

    @Override
    public boolean canRespawnHere() {
        return false; // Forces returning via portal
    }

    @Override
    public boolean isSurfaceWorld() {
        return false; // Treat differently than overworld
    }

    @Override
    public float getCloudHeight() {
        return 96.0f; // Lower clouds = heavier feeling
    }

    @Override
    public boolean isSkyColored() {
        return false; // No blue sky
    }

    @Override
    public Vec3 getFogColor(float celestialAngle, float partialTicks) {
        // Desaturated bluish-gray fog
        float fogStrength = 0.2f + (MathHelper.cos(celestialAngle * (float)Math.PI * 2.0f) * 0.1f);
        return this.worldObj.getWorldVec3Pool().getVecFromPool(0.1f * fogStrength, 0.08f * fogStrength, 0.12f * fogStrength);
    }
//    @Override
//    public Vec3 getFogColor(float par1, float par2) {
//        return Vec3.createVectorHelper(0,0,0);
//    }

    @Override
    public float[] calcSunriseSunsetColors(float par1, float par2) {
        return null; // No sunrise/sunset
    }

    @Override
    public IChunkProvider createChunkGenerator() {
        return new ChunkProviderGenerate(this.worldObj, this.worldObj.getSeed(), false);
        // Replace later with custom generator if needed
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
        return true; // Distant fog everywhere
    }

    public boolean canCoordinateBeSpawn(int par1, int par2) {
        return false;
    }

    public float calculateCelestialAngle(long par1, float par3) {
        return 0.5F;
    }

    @Override
    public ChunkCoordinates getEntrancePortalLocation() {
        // Send them either to the world spawn or a fixed “return portal” spot.


        ChunkCoordinates spawn = worldObj.getSpawnPoint();
        return new ChunkCoordinates(spawn.posX, spawn.posY, spawn.posZ);
    }
}
