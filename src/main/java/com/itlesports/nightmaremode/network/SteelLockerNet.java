package com.itlesports.nightmaremode.network;

import api.BTWAddon;
import api.network.CustomPacketHandler;
import com.itlesports.nightmaremode.block.tileEntities.TileEntitySteelLocker;
import com.itlesports.nightmaremode.rendering.ContainerSteelLocker;
import net.minecraft.src.*;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

public final class SteelLockerNet {

    public static String CHANNEL;

    private SteelLockerNet(){}

    public static void register(BTWAddon addon){

        CHANNEL = addon.getModID() + "|sl";

        addon.registerPacketHandler(CHANNEL, new CustomPacketHandler() {
            @Override
            public void handleCustomPacket(Packet250CustomPayload packet, EntityPlayer player) {
                if (packet == null || packet.data == null) return;
                if (!player.worldObj.isRemote) return;
                handleClient(packet, (EntityClientPlayerMP) player);
            }

            private void handleClient(Packet250CustomPayload packet, EntityClientPlayerMP player){
                try (DataInputStream in = new DataInputStream(new ByteArrayInputStream(packet.data))) {
                    byte action = in.readByte();
                    if (action == 1) {
                        int windowId = in.readInt();
                        int x = in.readInt();
                        int y = in.readInt();
                        int z = in.readInt();
                        upgradeToRealContainer(windowId, x, y, z, player);
                    }
                } catch (Exception ignored){}
            }

            private void upgradeToRealContainer(int windowId, int x,int y,int z, EntityClientPlayerMP player){
                if (player.openContainer == null) return;
                if ( (windowId & 0xFF) != (player.openContainer.windowId & 0xFF) ) return;

                TileEntity te = player.worldObj.getBlockTileEntity(x,y,z);
                if (!(te instanceof TileEntitySteelLocker chest)) return;

                Container oldC = player.openContainer;
                IInventory oldInv = null;
                if (oldC instanceof ContainerSteelLocker scOld) {
                    oldInv = scOld.getChestInventory();
                }

                if (oldInv != null) {
                    int limit = Math.min(oldInv.getSizeInventory(), chest.getSizeInventory());
                    for (int i=0;i<limit;i++){
                        ItemStack s = oldInv.getStackInSlot(i);
                        if (s != null && chest.getStackInSlot(i)==null){
                            chest.setInventorySlotContents(i, s.copy());
                        }
                    }
                }

                int wid = player.openContainer.windowId;

                ContainerSteelLocker newC = new ContainerSteelLocker(player.inventory, chest);
                newC.windowId = wid;

                if (Minecraft.getMinecraft().currentScreen instanceof GuiContainer gui){
                    gui.inventorySlots = newC;
                }
                player.openContainer = newC;
            }
        });
    }
}