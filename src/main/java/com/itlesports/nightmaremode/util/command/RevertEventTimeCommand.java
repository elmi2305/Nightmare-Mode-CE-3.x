package com.itlesports.nightmaremode.util.command;

import btw.community.nightmaremode.NightmareMode;
import net.minecraft.src.ChatMessageComponent;
import net.minecraft.src.CommandBase;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.ICommandSender;

import java.util.List;

public class RevertEventTimeCommand extends CommandBase {
    @Override
    public String getCommandName() {
        return "revert";
    }

    @Override
    public String getCommandUsage(ICommandSender iCommandSender) {
        return "/rev";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }
    public List getCommandAliases() {
        return List.of("rev", "revert");
    }

    @Override
    public void processCommand(ICommandSender iCommandSender, String[] strings) {
        EntityPlayerMP player = (EntityPlayerMP) iCommandSender;

        EventCommand.setTimeToReference(player.worldObj);
        player.sendChatToPlayer((new ChatMessageComponent()).addText("Set time to " + player.worldObj.getWorldTime()));

    }
}
