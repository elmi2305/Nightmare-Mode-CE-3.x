package com.itlesports.nightmaremode.util.command;

import btw.community.nightmaremode.NightmareMode;
import net.minecraft.src.*;

public final class ToggleSandboxCommand extends CommandBase {
    @Override public String getCommandName() { return "togglesandbox"; }
    @Override public String getCommandUsage(ICommandSender sender) { return "/togglesandbox"; }
    @Override public int getRequiredPermissionLevel() { return 2; }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return NightmareMode.devMode && super.canCommandSenderUseCommand(sender);
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (!NightmareMode.devMode) throw new CommandException("This command requires dev mode.");
        if (args.length != 0) throw new WrongUsageException(getCommandUsage(sender));
        NightmareMode.lockDownCreative = !NightmareMode.lockDownCreative;
        sender.sendChatToPlayer(ChatMessageComponent.createFromText(
                "Sandbox creative behavior: " + (NightmareMode.lockDownCreative ? "enabled" : "disabled")));
    }
}
