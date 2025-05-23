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

        if(player.experienceLevel < 1){
            sender.sendChatToPlayer(createMessage("You do not have enough XP to use this command.", EnumChatFormatting.RED, true, false));
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
            sender.sendChatToPlayer(createMessage("You can't send a TPA request to yourself.", EnumChatFormatting.RED, false, false));
            return;
        }
        if (sender.dimension != target.dimension) {
            sender.sendChatToPlayer(createMessage("Unable to send TPA request. Both players must be in the same dimension.", EnumChatFormatting.RED, false, false));
            return;
        }

        TPAManager.sendRequest(sender, target);
        sender.sendChatToPlayer(createMessage("TPA request sent to " + target.getCommandSenderName() + ".", EnumChatFormatting.GREEN, false, false));
        target.sendChatToPlayer(createMessage(sender.getCommandSenderName() + " wants to teleport to you. Type /tpa accept to allow.", EnumChatFormatting.GOLD, false, false));
    }


    private void handleAccept(EntityPlayerMP target) {
        UUID senderUUID = TPAManager.getRequestSender(target.getUniqueID());
        if (senderUUID == null) {
            target.sendChatToPlayer(createMessage("No valid TPA requests to accept.", EnumChatFormatting.RED, false, false));
            return;
        }

        EntityPlayerMP sender = getPlayerByUUID(senderUUID);
        if (sender == null) {
            target.sendChatToPlayer(createMessage("The player who sent the request is no longer online.", EnumChatFormatting.RED, false, false));
            TPAManager.removeRequest(target.getUniqueID());
            return;
        }

        TPAManager.removeRequest(target.getUniqueID());
        target.sendChatToPlayer(createMessage("Accepted TPA request from " + sender.getCommandSenderName() + ". Teleporting in 10 seconds...", EnumChatFormatting.GREEN, false, false));
        sender.sendChatToPlayer(createMessage(target.getCommandSenderName() + " accepted your teleport request! Teleporting in 10 seconds...", EnumChatFormatting.GREEN, false, false));

        scheduleTeleport(sender, target, 10);
    }

    private void handleDeny(EntityPlayerMP target) {
        UUID senderUUID = TPAManager.getRequestSender(target.getUniqueID());
        if (senderUUID == null) {
            target.sendChatToPlayer(createMessage("No valid TPA requests to deny.", EnumChatFormatting.RED, false, false));
            return;
        }

        EntityPlayerMP sender = getPlayerByUUID(senderUUID);
        if (sender != null) {
            sender.sendChatToPlayer(createMessage(target.getCommandSenderName() + " denied your teleport request.", EnumChatFormatting.RED, false, false));
        }
        target.sendChatToPlayer(createMessage("Denied the TPA request.", EnumChatFormatting.YELLOW, false, false));
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
    public static ChatMessageComponent createMessage(String text, EnumChatFormatting color, boolean italic, boolean underline) {
        ChatMessageComponent message = new ChatMessageComponent();
        message.addText(text);
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
