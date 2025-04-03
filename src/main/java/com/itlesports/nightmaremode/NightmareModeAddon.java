package com.itlesports.nightmaremode;

import btw.AddonHandler;
import btw.BTWAddon;
import btw.client.network.packet.handler.CustomEntityPacketHandler;
import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.entity.EntityDungCreeper;
import com.itlesports.nightmaremode.entity.EntityFireCreeper;
import com.itlesports.nightmaremode.entity.EntityMetalCreeper;
import com.itlesports.nightmaremode.entity.EntitySuperchargedCreeper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.Packet24MobSpawn;

import java.util.List;

public class NightmareModeAddon extends BTWAddon implements ModInitializer {
    private static NightmareModeAddon instance;

    public NightmareModeAddon() {
        super();
    }

    @Override
    public void initialize() {
        AddonHandler.logMessage(this.getName() + " Version " + this.getVersionString() + " Initializing...");
        if (!MinecraftServer.getIsServer()) {
            postInitClient();
        }
    }

    public static NightmareModeAddon getInstance() {
        if (instance == null)
            instance = new NightmareModeAddon();
        return instance;
    }

    @Override
    public void onInitialize() {
        if(!MinecraftServer.getIsServer()){
            addPacketManagementForCustomEntities();
        }
    }

    @Environment(EnvType.CLIENT)
    private void postInitClient() {
        addPacketManagementForCustomEntities();
    }

    @Environment (EnvType.CLIENT)
    private static void addPacketManagementForCustomEntities() {
        CustomEntityPacketHandler.entryMap.put(18, (world, dataStream, packet) -> {
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
        CustomEntityPacketHandler.entryMap.put(19, (world, dataStream, packet) -> {
            EntityMetalCreeper entityToSpawn = new EntityMetalCreeper(world);
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
        CustomEntityPacketHandler.entryMap.put(20, (world, dataStream, packet) -> {
            EntitySuperchargedCreeper entityToSpawn = new EntitySuperchargedCreeper(world);
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
        CustomEntityPacketHandler.entryMap.put(21, (world, dataStream, packet) -> {
            EntityDungCreeper entityToSpawn = new EntityDungCreeper(world);
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