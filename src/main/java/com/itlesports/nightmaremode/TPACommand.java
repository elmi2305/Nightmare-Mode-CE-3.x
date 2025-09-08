package com.itlesports.nightmaremode;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;

import java.util.List;
import java.util.UUID;

public class TPACommand extends CommandBase {
    @Override
    public String getCommandName() {
        return "tpa";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    public String getCommandUsage(ICommandSender iCommandSender) {
        return "commands.tpa.usage";
    }

    public List getCommandAliases() {
        return List.of("tpa");
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender par1ICommandSender) {
        return true;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        EntityPlayerMP player = (EntityPlayerMP) sender;

        if (args.length == 0) {
            throw new WrongUsageException("/tpa <player|accept|deny>");
        }

        String sub = args[0].toLowerCase();

        if (player.experienceLevel < 1) {
            sender.sendChatToPlayer(createMessage("commands.tpa.not_enough_xp", EnumChatFormatting.RED, true, false));
            return;
        }

        switch (sub) {
            case "accept":
                handleAccept(player);
                break;
            case "deny":
                handleDeny(player);
                break;
            default:
                handleRequest(player, sub);
                player.addExperienceLevel(-1);
                break;
        }
    }

    private void handleRequest(EntityPlayerMP sender, String targetName) throws CommandException {
        EntityPlayerMP target = MinecraftServer.getServer()
                .getConfigurationManager()
                .getPlayerEntity(targetName);

        if (target == null) {
            throw new PlayerNotFoundException();
        }
        if (sender == target) {
            sender.sendChatToPlayer(createMessage("commands.tpa.cannot_self", EnumChatFormatting.RED, false, false));
            return;
        }
        if (sender.dimension != target.dimension) {
            sender.sendChatToPlayer(createMessage("commands.tpa.different_dimension", EnumChatFormatting.RED, false, false));
            return;
        }

        TPAManager.sendRequest(sender, target);
        sender.sendChatToPlayer(createFormattedMessage("commands.tpa.sent", EnumChatFormatting.GREEN, false, false, target.getCommandSenderName()));
        target.sendChatToPlayer(createFormattedMessage("commands.tpa.request_message", EnumChatFormatting.GOLD, false, false, sender.getCommandSenderName()));
    }

    private void handleAccept(EntityPlayerMP target) {
        UUID senderUUID = TPAManager.getRequestSender(target.getUniqueID());
        if (senderUUID == null) {
            target.sendChatToPlayer(createMessage("commands.tpa.no_request_to_accept", EnumChatFormatting.RED, false, false));
            return;
        }

        EntityPlayerMP sender = getPlayerByUUID(senderUUID);
        if (sender == null) {
            target.sendChatToPlayer(createMessage("commands.tpa.sender_offline", EnumChatFormatting.RED, false, false));
            TPAManager.removeRequest(target.getUniqueID());
            return;
        }

        TPAManager.removeRequest(target.getUniqueID());
        target.sendChatToPlayer(createFormattedMessage("commands.tpa.accepted", EnumChatFormatting.GREEN, false, false, sender.getCommandSenderName()));
        sender.sendChatToPlayer(createFormattedMessage("commands.tpa.accepted_sender", EnumChatFormatting.GREEN, false, false, target.getCommandSenderName()));

        scheduleTeleport(sender, target, 10);
    }

    private void handleDeny(EntityPlayerMP target) {
        UUID senderUUID = TPAManager.getRequestSender(target.getUniqueID());
        if (senderUUID == null) {
            target.sendChatToPlayer(createMessage("commands.tpa.no_request_to_deny", EnumChatFormatting.RED, false, false));
            return;
        }

        EntityPlayerMP sender = getPlayerByUUID(senderUUID);
        if (sender != null) {
            sender.sendChatToPlayer(createFormattedMessage("commands.tpa.denied_sender", EnumChatFormatting.RED, false, false, target.getCommandSenderName()));
        }
        target.sendChatToPlayer(createMessage("commands.tpa.denied", EnumChatFormatting.YELLOW, false, false));
        TPAManager.removeRequest(target.getUniqueID());
    }

    private void scheduleTeleport(EntityPlayerMP sender, EntityPlayerMP target, int delaySeconds) {
        TeleportScheduler.scheduleTeleport(sender, target, delaySeconds);
    }

    public List addTabCompletionOptions(ICommandSender iCommandSender, String[] strings) {
        return strings.length != 1 && strings.length != 2 ? null : getListOfStringsMatchingLastWord(strings, MinecraftServer.getServer().getAllUsernames());
    }

    public boolean isUsernameIndex(String[] strings, int i) {
        return i == 0;
    }

    public static ChatMessageComponent createMessage(String key, EnumChatFormatting color, boolean italic, boolean underline) {
        ChatMessageComponent message = new ChatMessageComponent();
        message.addKey(key);
        message.setColor(color);
        message.setBold(true);
        message.setItalic(italic);
        message.setUnderline(underline);
        return message;
    }

    public static ChatMessageComponent createFormattedMessage(String key, EnumChatFormatting color, boolean italic, boolean underline, Object... args) {
        ChatMessageComponent message = new ChatMessageComponent();
        message.addFormatted(key, args);
        message.setColor(color);
        message.setBold(true);
        message.setItalic(italic);
        message.setUnderline(underline);
        return message;
    }

    public static EntityPlayerMP getPlayerByUUID(UUID uuid) {
        for (EntityPlayerMP player : (List<EntityPlayerMP>) MinecraftServer.getServer()
                .getConfigurationManager().playerEntityList) {
            if (player.getUniqueID().equals(uuid)) {
                return player;
            }
        }
        return null; // player not found or offline
    }
}