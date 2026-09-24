package com.itlesports.nightmaremode.util.command;

import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.network.ScaryEventNet;
import com.itlesports.nightmaremode.scary.ScaryEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;

import java.util.List;
import java.util.Locale;

public final class ScaryCommand extends CommandBase {
    @Override public String getCommandName() { return "scary"; }
    @Override public int getRequiredPermissionLevel() { return NightmareMode.showBetaOverlay ? 0 : 2; }
    @Override public String getCommandUsage(ICommandSender sender) { return ScaryEvent.help(); }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length != 1) {
            sender.sendChatToPlayer(new ChatMessageComponent().addText(ScaryEvent.help()));
            return;
        }
        String arg = args[0].toLowerCase(Locale.ROOT);
        if (arg.equals("on") || arg.equals("off")) {
            NightmareMode.scaryEvents = arg.equals("on");
            for (Object entry : MinecraftServer.getServer().getConfigurationManager().playerEntityList) {
                ScaryEventNet.sendControl((EntityPlayerMP)entry);
            }
            sender.sendChatToPlayer(new ChatMessageComponent().addText("Scary events: " + arg));
            return;
        }
        ScaryEvent event = ScaryEvent.find(arg);
        if (event == null) {
            sender.sendChatToPlayer(new ChatMessageComponent().addText(ScaryEvent.help()));
        } else if (sender instanceof EntityPlayerMP player) {
            ScaryEventNet.force(player, event);
        } else {
            sender.sendChatToPlayer(new ChatMessageComponent().addText("Invoke an event as a player to select its target."));
        }
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        if (args.length != 1) return null;
        String[] ids = new String[ScaryEvent.values().length + 2];
        ids[0] = "on";
        ids[1] = "off";
        for (int i = 0; i < ScaryEvent.values().length; i++) ids[i + 2] = ScaryEvent.values()[i].id;
        return getListOfStringsMatchingLastWord(args, ids);
    }
}
