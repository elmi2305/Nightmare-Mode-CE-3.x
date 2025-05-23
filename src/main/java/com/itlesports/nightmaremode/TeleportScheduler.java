package com.itlesports.nightmaremode;

import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.EnumChatFormatting;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.itlesports.nightmaremode.TPACommand.createMessage;

public class TeleportScheduler {
    private static final List<PendingTeleport> pendingTeleports = new ArrayList<>();

    public static void scheduleTeleport(EntityPlayerMP player, EntityPlayerMP target, int delaySeconds) {
        pendingTeleports.add(new PendingTeleport(player, target, delaySeconds));
    }

    public static void onServerTick() {
        Iterator<PendingTeleport> iterator = pendingTeleports.iterator();
        while (iterator.hasNext()) {
            PendingTeleport pt = iterator.next();
            pt.ticksRemaining--;

            if (pt.ticksRemaining <= 0) {
                // Check if players still exist and are in same world
                if (pt.playerToTeleport.worldObj == pt.targetPlayer.worldObj) {
                    pt.playerToTeleport.setPositionAndUpdate(pt.targetPlayer.posX, pt.targetPlayer.posY, pt.targetPlayer.posZ);

                    pt.playerToTeleport.sendChatToPlayer(createMessage(
                            "Teleported to " + pt.targetPlayer.getCommandSenderName() + ".", EnumChatFormatting.GREEN, false, false));
                } else {
                    pt.playerToTeleport.sendChatToPlayer(createMessage(
                            "Teleport cancelled: player changed dimension.", EnumChatFormatting.RED, false, false));
                }
                iterator.remove();
            }
        }
    }
}
