package com.itlesports.nightmaremode.util.command;

import com.itlesports.nightmaremode.skill.SkillRewardReload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;

public final class ReloadSkillsCommand extends CommandBase {
    @Override public String getCommandName() { return "reloadskills"; }
    @Override public String getCommandUsage(ICommandSender sender) { return "/reloadskills"; }
    @Override public int getRequiredPermissionLevel() { return 0; }

    @Override public void processCommand(ICommandSender sender, String[] args) {
        if (args.length != 0) throw new WrongUsageException(getCommandUsage(sender));
        MinecraftServer server = MinecraftServer.getServer();
        SkillRewardReload.validateWorld(server.worldServerForDimension(0), true);
        for (Object entry : server.getConfigurationManager().playerEntityList) {
            SkillRewardReload.validatePlayer((EntityPlayerMP) entry);
        }
        sender.sendChatToPlayer(ChatMessageComponent.createFromText(
                "Skill rewards rebuilt from current definitions. Unlocks and progress preserved; offline players update on login."));
    }
}
