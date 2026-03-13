package com.itlesports.nightmaremode;

import api.AddonHandler;
import api.BTWAddon;
import api.util.AddonSoundRegistryEntry;
import btw.client.network.packet.handler.CustomEntityPacketHandler;
import com.itlesports.nightmaremode.entity.creepers.*;
import com.itlesports.nightmaremode.entity.underworld.EntityBlackHole;
import com.itlesports.nightmaremode.entity.underworld.EntityPollenCloud;
import com.itlesports.nightmaremode.entity.underworld.EntitySporeArrow;
import com.itlesports.nightmaremode.entity.underworld.FlowerCreeper;
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
        CustomEntityPacketHandler.entryMap.put(PACKET_CREEPER_FIRE, (world, dataStream, packet) -> {
            EntityFireCreeper entityToSpawn = new EntityFireCreeper(world);
            Packet24MobSpawn mobSpawnPacket = new Packet24MobSpawn();
            mobSpawnPacket.readPacketData(dataStream);
            double var2 = (double) mobSpawnPacket.xPosition / 32.0;
            double var4 = (double) mobSpawnPacket.yPosition / 32.0;
            double var6 = (double) mobSpawnPacket.zPosition / 32.0;
            float var8 = (float) (mobSpawnPacket.yaw * 360) / 256.0f;
            float var9 = (float) (mobSpawnPacket.pitch * 360) / 256.0f;
            entityToSpawn.serverPosX = mobSpawnPacket.xPosition;
            entityToSpawn.serverPosY = mobSpawnPacket.yPosition;
            entityToSpawn.serverPosZ = mobSpawnPacket.zPosition;
            entityToSpawn.rotationYawHead = (float) (mobSpawnPacket.headYaw * 360) / 256.0f;
            entityToSpawn.entityId = mobSpawnPacket.entityId;
            entityToSpawn.setPositionAndRotation(var2, var4, var6, var8, var9);
            entityToSpawn.motionX = (float) mobSpawnPacket.velocityX / 8000.0f;
            entityToSpawn.motionY = (float) mobSpawnPacket.velocityY / 8000.0f;
            entityToSpawn.motionZ = (float) mobSpawnPacket.velocityZ / 8000.0f;
            List var14 = mobSpawnPacket.getMetadata();
            if (var14 != null) {
                entityToSpawn.getDataWatcher().updateWatchedObjectsFromList(var14);
            }
            int timeSinceIgnited = dataStream.readInt();
            entityToSpawn.setTimeSinceIgnited(timeSinceIgnited);
            return entityToSpawn;
        });
        CustomEntityPacketHandler.entryMap.put(PACKET_CREEPER_OBSIDIAN, (world, dataStream, packet) -> {
            EntityObsidianCreeper entityToSpawn = new EntityObsidianCreeper(world);
            Packet24MobSpawn mobSpawnPacket = new Packet24MobSpawn();
            mobSpawnPacket.readPacketData(dataStream);
            double var2 = (double) mobSpawnPacket.xPosition / 32.0;
            double var4 = (double) mobSpawnPacket.yPosition / 32.0;
            double var6 = (double) mobSpawnPacket.zPosition / 32.0;
            float var8 = (float) (mobSpawnPacket.yaw * 360) / 256.0f;
            float var9 = (float) (mobSpawnPacket.pitch * 360) / 256.0f;
            entityToSpawn.serverPosX = mobSpawnPacket.xPosition;
            entityToSpawn.serverPosY = mobSpawnPacket.yPosition;
            entityToSpawn.serverPosZ = mobSpawnPacket.zPosition;
            entityToSpawn.rotationYawHead = (float) (mobSpawnPacket.headYaw * 360) / 256.0f;
            entityToSpawn.entityId = mobSpawnPacket.entityId;
            entityToSpawn.setPositionAndRotation(var2, var4, var6, var8, var9);
            entityToSpawn.motionX = (float) mobSpawnPacket.velocityX / 8000.0f;
            entityToSpawn.motionY = (float) mobSpawnPacket.velocityY / 8000.0f;
            entityToSpawn.motionZ = (float) mobSpawnPacket.velocityZ / 8000.0f;
            List var14 = mobSpawnPacket.getMetadata();
            if (var14 != null) {
                entityToSpawn.getDataWatcher().updateWatchedObjectsFromList(var14);
            }
            int timeSinceIgnited = dataStream.readInt();
            entityToSpawn.setTimeSinceIgnited(timeSinceIgnited);
            return entityToSpawn;
        });
        CustomEntityPacketHandler.entryMap.put(PACKET_CREEPER_SUPERCRITICAL, (world, dataStream, packet) -> {
            EntityNitroCreeper entityToSpawn = new EntityNitroCreeper(world);
            Packet24MobSpawn mobSpawnPacket = new Packet24MobSpawn();
            mobSpawnPacket.readPacketData(dataStream);
            double var2 = (double) mobSpawnPacket.xPosition / 32.0;
            double var4 = (double) mobSpawnPacket.yPosition / 32.0;
            double var6 = (double) mobSpawnPacket.zPosition / 32.0;
            float var8 = (float) (mobSpawnPacket.yaw * 360) / 256.0f;
            float var9 = (float) (mobSpawnPacket.pitch * 360) / 256.0f;
            entityToSpawn.serverPosX = mobSpawnPacket.xPosition;
            entityToSpawn.serverPosY = mobSpawnPacket.yPosition;
            entityToSpawn.serverPosZ = mobSpawnPacket.zPosition;
            entityToSpawn.rotationYawHead = (float) (mobSpawnPacket.headYaw * 360) / 256.0f;
            entityToSpawn.entityId = mobSpawnPacket.entityId;
            entityToSpawn.setPositionAndRotation(var2, var4, var6, var8, var9);
            entityToSpawn.motionX = (float) mobSpawnPacket.velocityX / 8000.0f;
            entityToSpawn.motionY = (float) mobSpawnPacket.velocityY / 8000.0f;
            entityToSpawn.motionZ = (float) mobSpawnPacket.velocityZ / 8000.0f;
            List var14 = mobSpawnPacket.getMetadata();
            if (var14 != null) {
                entityToSpawn.getDataWatcher().updateWatchedObjectsFromList(var14);
            }
            int timeSinceIgnited = dataStream.readInt();
            entityToSpawn.setTimeSinceIgnited(timeSinceIgnited);
            return entityToSpawn;
        });
        CustomEntityPacketHandler.entryMap.put(PACKET_CREEPER_DUNG, (world, dataStream, packet) -> {
            EntityDungCreeper entityToSpawn = new EntityDungCreeper(world);
            Packet24MobSpawn mobSpawnPacket = new Packet24MobSpawn();
            mobSpawnPacket.readPacketData(dataStream);
            double var2 = (double) mobSpawnPacket.xPosition / 32.0;
            double var4 = (double) mobSpawnPacket.yPosition / 32.0;
            double var6 = (double) mobSpawnPacket.zPosition / 32.0;
            float var8 = (float) (mobSpawnPacket.yaw * 360) / 256.0f;
            float var9 = (float) (mobSpawnPacket.pitch * 360) / 256.0f;
            entityToSpawn.serverPosX = mobSpawnPacket.xPosition;
            entityToSpawn.serverPosY = mobSpawnPacket.yPosition;
            entityToSpawn.serverPosZ = mobSpawnPacket.zPosition;
            entityToSpawn.rotationYawHead = (float) (mobSpawnPacket.headYaw * 360) / 256.0f;
            entityToSpawn.entityId = mobSpawnPacket.entityId;
            entityToSpawn.setPositionAndRotation(var2, var4, var6, var8, var9);
            entityToSpawn.motionX = (float) mobSpawnPacket.velocityX / 8000.0f;
            entityToSpawn.motionY = (float) mobSpawnPacket.velocityY / 8000.0f;
            entityToSpawn.motionZ = (float) mobSpawnPacket.velocityZ / 8000.0f;
            List var14 = mobSpawnPacket.getMetadata();
            if (var14 != null) {
                entityToSpawn.getDataWatcher().updateWatchedObjectsFromList(var14);
            }
            int timeSinceIgnited = dataStream.readInt();
            entityToSpawn.setTimeSinceIgnited(timeSinceIgnited);
            return entityToSpawn;
        });
        CustomEntityPacketHandler.entryMap.put(PACKET_CREEPER_LIGHTNING, (world, dataStream, packet) -> {
            EntityLightningCreeper entityToSpawn = new EntityLightningCreeper(world);
            Packet24MobSpawn mobSpawnPacket = new Packet24MobSpawn();
            mobSpawnPacket.readPacketData(dataStream);
            double var2 = (double) mobSpawnPacket.xPosition / 32.0;
            double var4 = (double) mobSpawnPacket.yPosition / 32.0;
            double var6 = (double) mobSpawnPacket.zPosition / 32.0;
            float var8 = (float) (mobSpawnPacket.yaw * 360) / 256.0f;
            float var9 = (float) (mobSpawnPacket.pitch * 360) / 256.0f;
            entityToSpawn.serverPosX = mobSpawnPacket.xPosition;
            entityToSpawn.serverPosY = mobSpawnPacket.yPosition;
            entityToSpawn.serverPosZ = mobSpawnPacket.zPosition;
            entityToSpawn.rotationYawHead = (float) (mobSpawnPacket.headYaw * 360) / 256.0f;
            entityToSpawn.entityId = mobSpawnPacket.entityId;
            entityToSpawn.setPositionAndRotation(var2, var4, var6, var8, var9);
            entityToSpawn.motionX = (float) mobSpawnPacket.velocityX / 8000.0f;
            entityToSpawn.motionY = (float) mobSpawnPacket.velocityY / 8000.0f;
            entityToSpawn.motionZ = (float) mobSpawnPacket.velocityZ / 8000.0f;
            List var14 = mobSpawnPacket.getMetadata();
            if (var14 != null) {
                entityToSpawn.getDataWatcher().updateWatchedObjectsFromList(var14);
            }
            int timeSinceIgnited = dataStream.readInt();
            entityToSpawn.setTimeSinceIgnited(timeSinceIgnited);
            return entityToSpawn;
        });

        CustomEntityPacketHandler.entryMap.put(PACKET_CREEPER_FLOWER, (world, dataStream, packet) -> {
            FlowerCreeper entityToSpawn = new FlowerCreeper(world);
            Packet24MobSpawn mobSpawnPacket = new Packet24MobSpawn();
            mobSpawnPacket.readPacketData(dataStream);
            double var2 = (double) mobSpawnPacket.xPosition / 32.0;
            double var4 = (double) mobSpawnPacket.yPosition / 32.0;
            double var6 = (double) mobSpawnPacket.zPosition / 32.0;
            float var8 = (float) (mobSpawnPacket.yaw * 360) / 256.0f;
            float var9 = (float) (mobSpawnPacket.pitch * 360) / 256.0f;
            entityToSpawn.serverPosX = mobSpawnPacket.xPosition;
            entityToSpawn.serverPosY = mobSpawnPacket.yPosition;
            entityToSpawn.serverPosZ = mobSpawnPacket.zPosition;
            entityToSpawn.rotationYawHead = (float) (mobSpawnPacket.headYaw * 360) / 256.0f;
            entityToSpawn.entityId = mobSpawnPacket.entityId;
            entityToSpawn.setPositionAndRotation(var2, var4, var6, var8, var9);
            entityToSpawn.motionX = (float) mobSpawnPacket.velocityX / 8000.0f;
            entityToSpawn.motionY = (float) mobSpawnPacket.velocityY / 8000.0f;
            entityToSpawn.motionZ = (float) mobSpawnPacket.velocityZ / 8000.0f;
            List var14 = mobSpawnPacket.getMetadata();
            if (var14 != null) {
                entityToSpawn.getDataWatcher().updateWatchedObjectsFromList(var14);
            }
            int timeSinceIgnited = dataStream.readInt();
            entityToSpawn.setTimeSinceIgnited(timeSinceIgnited);
            return entityToSpawn;
        });
        CustomEntityPacketHandler.entryMap.put(PACKET_SPORE, (world, dataStream, packet) -> {
            double radius = dataStream.readDouble();
            EntityPollenCloud entityToSpawn = new EntityPollenCloud(world, radius);
            Packet24MobSpawn mobSpawnPacket = new Packet24MobSpawn();
            mobSpawnPacket.readPacketData(dataStream);
            double var2 = (double) mobSpawnPacket.xPosition / 32.0;
            double var4 = (double) mobSpawnPacket.yPosition / 32.0;
            double var6 = (double) mobSpawnPacket.zPosition / 32.0;
            float var8 = (float) (mobSpawnPacket.yaw * 360) / 256.0f;
            float var9 = (float) (mobSpawnPacket.pitch * 360) / 256.0f;
            entityToSpawn.serverPosX = mobSpawnPacket.xPosition;
            entityToSpawn.serverPosY = mobSpawnPacket.yPosition;
            entityToSpawn.serverPosZ = mobSpawnPacket.zPosition;
            entityToSpawn.entityId = mobSpawnPacket.entityId;
            entityToSpawn.setPositionAndRotation(var2, var4, var6, var8, var9);
            entityToSpawn.motionX = (float) mobSpawnPacket.velocityX / 8000.0f;
            entityToSpawn.motionY = (float) mobSpawnPacket.velocityY / 8000.0f;
            entityToSpawn.motionZ = (float) mobSpawnPacket.velocityZ / 8000.0f;
            List var14 = mobSpawnPacket.getMetadata();
            if (var14 != null) {
                entityToSpawn.getDataWatcher().updateWatchedObjectsFromList(var14);
            }
            return entityToSpawn;
        });
        CustomEntityPacketHandler.entryMap.put(PACKET_BLACKHOLE, (world, dataStream, packet) -> {
            double radius = dataStream.readDouble();
            EntityBlackHole entityToSpawn = new EntityBlackHole(world, radius);
            Packet24MobSpawn mobSpawnPacket = new Packet24MobSpawn();
            mobSpawnPacket.readPacketData(dataStream);
            double var2 = (double) mobSpawnPacket.xPosition / 32.0;
            double var4 = (double) mobSpawnPacket.yPosition / 32.0;
            double var6 = (double) mobSpawnPacket.zPosition / 32.0;
            float var8 = (float) (mobSpawnPacket.yaw * 360) / 256.0f;
            float var9 = (float) (mobSpawnPacket.pitch * 360) / 256.0f;
            entityToSpawn.serverPosX = mobSpawnPacket.xPosition;
            entityToSpawn.serverPosY = mobSpawnPacket.yPosition;
            entityToSpawn.serverPosZ = mobSpawnPacket.zPosition;
            entityToSpawn.entityId = mobSpawnPacket.entityId;
            entityToSpawn.setPositionAndRotation(var2, var4, var6, var8, var9);
            entityToSpawn.motionX = (float) mobSpawnPacket.velocityX / 8000.0f;
            entityToSpawn.motionY = (float) mobSpawnPacket.velocityY / 8000.0f;
            entityToSpawn.motionZ = (float) mobSpawnPacket.velocityZ / 8000.0f;
            List var14 = mobSpawnPacket.getMetadata();
            if (var14 != null) {
                entityToSpawn.getDataWatcher().updateWatchedObjectsFromList(var14);
            }
            return entityToSpawn;
        });
    }
}