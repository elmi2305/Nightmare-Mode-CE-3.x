package com.itlesports.nightmaremode.tpa;

import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.EnumChatFormatting;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.itlesports.nightmaremode.tpa.TPACommand.createMessage;
import static com.itlesports.nightmaremode.tpa.TPACommand.createFormattedMessage;

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

                    pt.playerToTeleport.sendChatToPlayer(
                            createFormattedMessage("commands.tpa.teleport_success", EnumChatFormatting.GREEN, false, false, pt.targetPlayer.getCommandSenderName())
                    );
                } else {
                    pt.playerToTeleport.sendChatToPlayer(
                            createMessage("commands.tpa.teleport_cancelled", EnumChatFormatting.RED, false, false)
                    );
                }
                iterator.remove();
            }
        }
    }
}