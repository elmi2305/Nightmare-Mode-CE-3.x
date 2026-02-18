package com.itlesports.nightmaremode;

import api.AddonHandler;
import api.BTWAddon;
import api.util.AddonSoundRegistryEntry;
import btw.client.network.packet.handler.CustomEntityPacketHandler;
import com.itlesports.nightmaremode.entity.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.Packet24MobSpawn;

import java.util.List;

import static com.itlesports.nightmaremode.util.NMFields.*;

public class NightmareModeAddon extends BTWAddon implements ModInitializer {
    private static NightmareModeAddon instance;

    public static final AddonSoundRegistryEntry NM_BOSS_MUSIC = new AddonSoundRegistryEntry("nightmare:nmBoss");

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
        CustomEntityPacketHandler.entryMap.put(CREEPER_FIRE, (world, dataStream, packet) -> {
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
        CustomEntityPacketHandler.entryMap.put(CREEPER_OBSIDIAN, (world, dataStream, packet) -> {
            EntityObsidianCreeper entityToSpawn = new EntityObsidianCreeper(world);
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
        CustomEntityPacketHandler.entryMap.put(CREEPER_SUPERCRITICAL, (world, dataStream, packet) -> {
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
        CustomEntityPacketHandler.entryMap.put(CREEPER_DUNG, (world, dataStream, packet) -> {
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
        CustomEntityPacketHandler.entryMap.put(CREEPER_LIGHTNING, (world, dataStream, packet) -> {
            EntityLightningCreeper entityToSpawn = new EntityLightningCreeper(world);
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