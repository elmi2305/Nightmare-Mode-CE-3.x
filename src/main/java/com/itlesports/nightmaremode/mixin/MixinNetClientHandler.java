package com.itlesports.nightmaremode.mixin;

import com.itlesports.nightmaremode.block.blocks.BlockSteelLocker;
import com.itlesports.nightmaremode.entity.EntityMagicArrow;
import com.itlesports.nightmaremode.entity.underworld.EntitySporeArrow;
import com.itlesports.nightmaremode.nmgui.*;
import com.itlesports.nightmaremode.nmgui.GuiLocker;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetClientHandler.class)
public abstract class MixinNetClientHandler {
    @Inject(method = "handleOpenWindow", at = @At("HEAD"), cancellable = true)
    private void moreblocks$handleOpenWindow(Packet100OpenWindow packet, CallbackInfo ci) {
        if (packet.inventoryType != BlockSteelLocker.CUSTOM_WINDOW_TYPE) return;

        Minecraft mc = Minecraft.getMinecraft();
        EntityClientPlayerMP player = mc.thePlayer;
        if (player == null) {
            ci.cancel();
            return;
        }

        int size = packet.slotsCount > 0 ? packet.slotsCount : 133;
        String title = packet.windowTitle != null ? packet.windowTitle : "SteelLocker";
        boolean localized = packet.useProvidedWindowTitle;

        IInventory fake;
        try {
            fake = new InventoryBasic(title, localized, size);
        } catch (Throwable t) {
            fake = new InventoryBasic("SteelLocker", false, 133);
        }

        GuiLocker gui = new GuiLocker(player.inventory, fake);
        mc.displayGuiScreen(gui);

        if (player.openContainer != null) {
            player.openContainer.windowId = packet.windowId & 0xFF;
        }

        ci.cancel();
    }

    @Inject(method = "handleOpenWindow", at = @At("HEAD"), cancellable = true)
    private void onHandleOpenWindow(Packet100OpenWindow packet, CallbackInfo ci) {
        if (packet.inventoryType == 60) {
            EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
            IInventory inv = new InventoryHorseArmor(player, player.getHeldItem(), player.inventory.currentItem);
            player.openContainer = new ContainerHorseArmor(player.inventory, inv);
            Minecraft.getMinecraft().displayGuiScreen(new GuiAdvancedHorseArmor(player.inventory, inv));
            ci.cancel();
        }
    }

    @Inject(method = "handleVehicleSpawn", at = @At("HEAD"), cancellable = true)
    private void addArrowPacketHandling(Packet23VehicleSpawn par1Packet23VehicleSpawn, CallbackInfo ci) {
        double xPos = (double)par1Packet23VehicleSpawn.xPosition / 32.0;
        double yPos = (double)par1Packet23VehicleSpawn.yPosition / 32.0;
        double zPos = (double)par1Packet23VehicleSpawn.zPosition / 32.0;
        Entity entityToSpawn = null;
        NetClientHandler nch = (NetClientHandler)(Object)this;
        NetClientHandlerExt ext = (NetClientHandlerExt)nch;

        if (par1Packet23VehicleSpawn.type == EntitySporeArrow.getVehicleSpawnPacketType()) {
            entityToSpawn = new EntitySporeArrow(ext.getClient(), xPos, yPos, zPos);
        }
        else if (par1Packet23VehicleSpawn.type == EntityMagicArrow.getVehicleSpawnPacketType()) {
            entityToSpawn = new EntityMagicArrow(ext.getClient(), xPos, yPos, zPos);
        }
//        else if (par1Packet23VehicleSpawn.type == EntityGoldArrow.getVehicleSpawnPacketType()) {
//            entityToSpawn = new EntityGoldArrow(ext.getClient(), xPos, yPos, zPos);
//        }
//        else if (par1Packet23VehicleSpawn.type == EntityFrostArrow.getVehicleSpawnPacketType()) {
//            entityToSpawn = new EntityFrostArrow(ext.getClient(), xPos, yPos, zPos);
//        }
//        else if (par1Packet23VehicleSpawn.type == EntityBoneBolt.getVehicleSpawnPacketType()) {
//            entityToSpawn = new EntityBoneBolt(ext.getClient(), xPos, yPos, zPos);
//        }
//        else if (par1Packet23VehicleSpawn.type == EntityCopperBolt.getVehicleSpawnPacketType()) {
//            entityToSpawn = new EntityCopperBolt(ext.getClient(), xPos, yPos, zPos);
//        }
//        else if (par1Packet23VehicleSpawn.type == 3374) {
//            entityToSpawn = new EntityMolotov(ext.getClient(), xPos, yPos, zPos);
//        }

        if (entityToSpawn != null) {
            ((Entity)entityToSpawn).serverPosX = par1Packet23VehicleSpawn.xPosition;
            ((Entity)entityToSpawn).serverPosY = par1Packet23VehicleSpawn.yPosition;
            ((Entity)entityToSpawn).serverPosZ = par1Packet23VehicleSpawn.zPosition;
            ((Entity)entityToSpawn).rotationPitch = (float)(par1Packet23VehicleSpawn.pitch * 360) / 256.0f;
            ((Entity)entityToSpawn).rotationYaw = (float)(par1Packet23VehicleSpawn.yaw * 360) / 256.0f;
            Entity[] var12 = ((Entity)entityToSpawn).getParts();
            if (var12 != null) {
                int var10 = par1Packet23VehicleSpawn.entityId - ((Entity)entityToSpawn).entityId;
                for (int var11 = 0; var11 < var12.length; ++var11) {
                    var12[var11].entityId += var10;
                }
            }
            ((Entity)entityToSpawn).entityId = par1Packet23VehicleSpawn.entityId;
            ext.getClient().addEntityToWorld(par1Packet23VehicleSpawn.entityId, entityToSpawn);
            if (par1Packet23VehicleSpawn.throwerEntityId > 0) {
                Entity var13;
                if (entityToSpawn instanceof EntityArrow && (var13 = getEntityByID(par1Packet23VehicleSpawn.throwerEntityId)) instanceof EntityLivingBase) {
                    EntityArrow var14 = (EntityArrow)entityToSpawn;
                    var14.shootingEntity = var13;
                }
                ((Entity)entityToSpawn).setVelocity((double)par1Packet23VehicleSpawn.speedX / 8000.0, (double)par1Packet23VehicleSpawn.speedY / 8000.0, (double)par1Packet23VehicleSpawn.speedZ / 8000.0);
            }
        }


    }

    private Entity getEntityByID(int par1) {
        NetClientHandler nch = (NetClientHandler)(Object)this;
        NetClientHandlerExt ext = (NetClientHandlerExt)nch;
        return par1 == ext.getMc().thePlayer.entityId ? ext.getMc().thePlayer : ext.getClient().getEntityByID(par1);
    }
}