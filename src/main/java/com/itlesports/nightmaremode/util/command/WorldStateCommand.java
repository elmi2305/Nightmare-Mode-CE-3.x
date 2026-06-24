package com.itlesports.nightmaremode.util.command;

import btw.community.nightmaremode.NightmareMode;
import btw.world.BTWWorldData;
import net.minecraft.src.*;

import java.util.List;

public class WorldStateCommand extends CommandBase {
    @Override
    public String getCommandName() {
        return "state";
    }

    public String getCommandUsage(ICommandSender iCommandSender) {
        return "commands.state.usage";
    }

    public List getCommandAliases() {
        return List.of("ws", "state", "worldstate", "settst", "st");
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        EntityPlayerMP player = (EntityPlayerMP) sender;

        if (args.length == 0) {
            player.sendChatToPlayer((new ChatMessageComponent()).addText("World state is " + NightmareMode.worldState));
            return;
        }
        int choice;
        try {
            choice = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            throw new WrongUsageException("/ws <0|1|2|3>");
        }
        if(choice < 0 || choice > 3) {
            throw new WrongUsageException("/ws <0|1|2|3>");
        }

        NightmareMode.worldState = choice;

        boolean netherAccessed = choice >= 1;
        boolean witherSummoned = choice >= 2;
        boolean endAccessed = choice >= 3;

        player.worldObj.setData(BTWWorldData.NETHER_ACCESSED, netherAccessed);
        player.worldObj.setData(BTWWorldData.WITHER_SUMMONED, witherSummoned);
        player.worldObj.setData(BTWWorldData.END_ACCESSED, endAccessed);
        player.worldObj.setData(BTWWorldData.DRAGON_KILLED, endAccessed);
        player.worldObj.setData(NightmareMode.DRAGON_DEFEATED, endAccessed);

        NightmareMode.sendWorldStateToAllPlayers();
        player.sendChatToPlayer((new ChatMessageComponent()).addText("Set world state to " + NightmareMode.worldState));
    }
}
