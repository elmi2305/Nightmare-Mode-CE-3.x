package com.itlesports.nightmaremode.network;

import api.BTWAddon;
import com.itlesports.nightmaremode.block.tileEntities.TileEntityHammerAnvil;
import com.itlesports.nightmaremode.item.items.ItemHammer;
import com.itlesports.nightmaremode.util.HammerAnvilAnimation;
import com.itlesports.nightmaremode.util.HammerAnvilHelper;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.TileEntity;

import java.nio.ByteBuffer;

public final class HammerAnvilNet {
    private static String channel;

    private HammerAnvilNet() {}

    public static void register(BTWAddon addon) {
        channel = addon.getModID() + "|hammer";
        addon.registerPacketHandler(channel, (packet, player) -> {
            // only successful server crafts can enqueue a cosmetic animation.
            if (!player.worldObj.isRemote || packet.data == null || packet.data.length != 40) return;
            ByteBuffer data = ByteBuffer.wrap(packet.data);
            int dimension = data.getInt();
            int x = data.getInt();
            int y = data.getInt();
            int z = data.getInt();
            int block = data.getInt();
            int hits = data.getInt();
            int inputID = data.getInt();
            int inputDamage = data.getInt();
            int hammerID = data.getInt();
            int hammerDamage = data.getInt();
            if (player.dimension != dimension || !HammerAnvilHelper.isAnvil(block)
                    || player.worldObj.getBlockId(x, y, z) != block || hits < 1
                    || !validItem(inputID) || !validItem(hammerID)
                    || !(Item.itemsList[hammerID] instanceof ItemHammer)) return;
            TileEntity tile = player.worldObj.getBlockTileEntity(x, y, z);
            if (tile instanceof TileEntityHammerAnvil anvil) {
                anvil.queueAnimation(new HammerAnvilAnimation(new ItemStack(inputID, 1, inputDamage),
                        new ItemStack(hammerID, 1, hammerDamage), hits));
            }
        });
    }

    public static void send(EntityPlayer player, TileEntityHammerAnvil anvil, ItemStack input, ItemStack hammer, int hits) {
        if (!(player instanceof EntityPlayerMP target)) return;
        ByteBuffer data = ByteBuffer.allocate(40);
        data.putInt(player.dimension).putInt(anvil.xCoord).putInt(anvil.yCoord).putInt(anvil.zCoord);
        data.putInt(anvil.worldObj.getBlockId(anvil.xCoord, anvil.yCoord, anvil.zCoord)).putInt(hits);
        data.putInt(input.itemID).putInt(input.getItemDamage()).putInt(hammer.itemID).putInt(hammer.getItemDamage());
        target.playerNetServerHandler.sendPacketToPlayer(new Packet250CustomPayload(channel, data.array()));
    }

    private static boolean validItem(int id) {
        return id > 0 && id < Item.itemsList.length && Item.itemsList[id] != null;
    }
}
