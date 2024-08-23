package com.itlesports.nightmaremode;

import btw.AddonHandler;
import btw.BTWAddon;
import btw.block.BTWBlocks;
import btw.client.network.packet.handler.CustomEntityPacketHandler;
import btw.world.biome.BiomeDecoratorBase;
import net.fabricmc.api.ModInitializer;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;
import java.util.Random;

public class NightmareMode extends BTWAddon implements ModInitializer {
    private static NightmareMode instance;

    public NightmareMode() {
        super();
    }

    @Override
    public void initialize() {
        AddonHandler.logMessage(this.getName() + " Version " + this.getVersionString() + " Initializing...");
    }

//    @Override
//    public void decorateWorld(BiomeDecoratorBase decorator, World world, Random rand, int x, int y, BiomeGenBase biome) {
//        this.lavaPillowGenThirdStrata = new WorldGenMinable(BTWBlocks.lavaPillow.blockID, 10);
//        for(int var5 = 0; var5 < 30; ++var5) {
//            int var6 = x + rand.nextInt(16) + 8;
//            int var7 = rand.nextInt(20)+4;
//            int var8 = y + rand.nextInt(16) + 8; // this is supposed to be called z
//            lavaPillowGenThirdStrata.generate(world, rand, var6, var7, var8);
//        }
//    }

//    private void genLavaPillow(int par1, WorldGenerator par2WorldGenerator, int par3, int par4) {
//        for (int var5 = 0; var5 < par1; ++var5) {
//            int var6 = this.chunk_X + this.getRandomGenerator().nextInt(16) + 8;
//            int var7 = this.getRandomGenerator().nextInt(par4 - par3) + par3;
//            int var8 = this.getChunkZ() + this.getRandomGenerator().nextInt(16) + 8;
//            par2WorldGenerator.generate(this.getCurrentWorld(), this.getRandomGenerator(), var6, var7, var8);
//        }
//    }

    public static NightmareMode getInstance() {
        if (instance == null)
            instance = new NightmareMode();
        return instance;
    }

    @Override
    public void onInitialize() {
        addFireCreeperToSpawnEntities();
    }

    private static void addFireCreeperToSpawnEntities() {
        CustomEntityPacketHandler.entryMap.put(13, (world, dataStream, packet) -> {
            EntityFireCreeper entityToSpawn = new EntityFireCreeper(world);
            Packet24MobSpawn par1Packet24MobSpawn = new Packet24MobSpawn();
            par1Packet24MobSpawn.readPacketData(dataStream);
            double var2 = (double) par1Packet24MobSpawn.xPosition / 32.0;
            double var4 = (double) par1Packet24MobSpawn.yPosition / 32.0;
            double var6 = (double) par1Packet24MobSpawn.zPosition / 32.0;
            float var8 = (float) (par1Packet24MobSpawn.yaw * 360) / 256.0f;
            float var9 = (float) (par1Packet24MobSpawn.pitch * 360) / 256.0f;
            entityToSpawn.serverPosX = par1Packet24MobSpawn.xPosition;
            entityToSpawn.serverPosY = par1Packet24MobSpawn.yPosition;
            entityToSpawn.serverPosZ = par1Packet24MobSpawn.zPosition;
            entityToSpawn.rotationYawHead = (float) (par1Packet24MobSpawn.headYaw * 360) / 256.0f;
            entityToSpawn.entityId = par1Packet24MobSpawn.entityId;
            entityToSpawn.setPositionAndRotation(var2, var4, var6, var8, var9);
            entityToSpawn.motionX = (float) par1Packet24MobSpawn.velocityX / 8000.0f;
            entityToSpawn.motionY = (float) par1Packet24MobSpawn.velocityY / 8000.0f;
            entityToSpawn.motionZ = (float) par1Packet24MobSpawn.velocityZ / 8000.0f;
            List var14 = par1Packet24MobSpawn.getMetadata();
            if (var14 != null) {
                entityToSpawn.getDataWatcher().updateWatchedObjectsFromList(var14);
            }
            int timeSinceIgnited = dataStream.readInt();
            entityToSpawn.setTimeSinceIgnited(timeSinceIgnited);
            return entityToSpawn;
        });
    }
}