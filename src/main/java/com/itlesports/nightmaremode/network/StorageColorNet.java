package com.itlesports.nightmaremode.network;

import api.BTWAddon;
import api.network.CustomPacketHandler;
import com.itlesports.nightmaremode.util.StorageColor;
import com.itlesports.nightmaremode.util.interfaces.IDyeableStorage;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityMinecartChest;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Minecraft;
import net.minecraft.src.MovingObjectPosition;
import net.minecraft.src.Packet250CustomPayload;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class StorageColorNet {
    private static final byte BLOCK = 0;
    private static final byte MINECART = 1;
    public static String CHANNEL;

    private StorageColorNet() {}

    public static void register(BTWAddon addon) {
        CHANNEL = addon.getModID() + "|sc";
        addon.registerPacketHandler(CHANNEL, new CustomPacketHandler() {
            @Override
            public void handleCustomPacket(Packet250CustomPayload packet, EntityPlayer player) {
                if (packet == null || packet.data == null || player.worldObj.isRemote || !(player instanceof EntityPlayerMP)) {
                    return;
                }
                handleServer(packet.data, player);
            }
        });
    }

    @Environment(EnvType.CLIENT)
    public static boolean tryDyeTarget(Minecraft minecraft) {
        if (CHANNEL == null || minecraft.currentScreen != null || !GuiScreen.isCtrlKeyDown()
                || minecraft.thePlayer == null || minecraft.objectMouseOver == null) {
            return false;
        }

        ItemStack dye = minecraft.thePlayer.inventory.getCurrentItem();
        if (dye == null || dye.itemID != Item.dyePowder.itemID) {
            return false;
        }

        MovingObjectPosition target = minecraft.objectMouseOver;
        Packet250CustomPayload packet = null;
        if (target.typeOfHit == net.minecraft.src.EnumMovingObjectType.TILE
                && StorageColor.isStorageBlock(minecraft.theWorld.getBlockId(target.blockX, target.blockY, target.blockZ))) {
            packet = createBlockPacket(target.blockX, target.blockY, target.blockZ, dye.getItemDamage());
        } else if (target.typeOfHit == net.minecraft.src.EnumMovingObjectType.ENTITY
                && target.entityHit instanceof EntityMinecartChest) {
            packet = createMinecartPacket(target.entityHit.entityId, dye.getItemDamage());
        }

        if (packet == null) {
            return false;
        }
        minecraft.thePlayer.sendQueue.addToSendQueue(packet);
        return true;
    }

    private static void handleServer(byte[] bytes, EntityPlayer player) {
        try (DataInputStream input = new DataInputStream(new ByteArrayInputStream(bytes))) {
            byte targetType = input.readByte();
            int color = input.readUnsignedByte();
            ItemStack dye = player.inventory.getCurrentItem();
            if (dye == null || dye.itemID != Item.dyePowder.itemID || dye.getItemDamage() != color) {
                return;
            }

            if (targetType == BLOCK) {
                int x = input.readInt();
                int y = input.readInt();
                int z = input.readInt();
                if (player.getDistanceSq(x + 0.5D, y + 0.5D, z + 0.5D) <= 64.0D) {
                    StorageColor.dyeStorage(player.worldObj, x, y, z, player, dye);
                }
            } else if (targetType == MINECART) {
                Entity entity = player.worldObj.getEntityByID(input.readInt());
                if (entity instanceof EntityMinecartChest chest && player.getDistanceSqToEntity(chest) <= 64.0D) {
                    ((IDyeableStorage) chest).nm$setStorageColor(color);
                    consumeDye(player, dye);
                }
            }
        } catch (IOException ignored) {
        }
    }

    private static void consumeDye(EntityPlayer player, ItemStack dye) {
        if (!player.capabilities.isCreativeMode && --dye.stackSize == 0) {
            player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
        }
    }

    private static Packet250CustomPayload createBlockPacket(int x, int y, int z, int color) {
        try (ByteArrayOutputStream bytes = new ByteArrayOutputStream(); DataOutputStream output = new DataOutputStream(bytes)) {
            output.writeByte(BLOCK);
            output.writeByte(color);
            output.writeInt(x);
            output.writeInt(y);
            output.writeInt(z);
            return new Packet250CustomPayload(CHANNEL, bytes.toByteArray());
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to create storage color packet", exception);
        }
    }

    private static Packet250CustomPayload createMinecartPacket(int entityId, int color) {
        try (ByteArrayOutputStream bytes = new ByteArrayOutputStream(); DataOutputStream output = new DataOutputStream(bytes)) {
            output.writeByte(MINECART);
            output.writeByte(color);
            output.writeInt(entityId);
            return new Packet250CustomPayload(CHANNEL, bytes.toByteArray());
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to create storage color packet", exception);
        }
    }
}
