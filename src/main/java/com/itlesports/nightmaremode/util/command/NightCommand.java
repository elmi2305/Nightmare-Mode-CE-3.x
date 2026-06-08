package com.itlesports.nightmaremode.util.command;


import net.minecraft.src.*;

import java.util.List;

public class NightCommand extends CommandBase {
    @Override
    public String getCommandName() {
        return "night";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }
    public List getCommandAliases() {
        return List.of("night");
    }


    @Override
    public String getCommandUsage(ICommandSender iCommandSender) {
        return "/night <num>";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        EntityPlayerMP player = (EntityPlayerMP) sender;
        World world = player.getEntityWorld();
        if (args.length == 0) {
            world.setWorldTime((int)((world.getWorldTime() + 24000) / 24000) + 12500);
            return;
        }

        if (args.length > 2) {
            throw new WrongUsageException("/night <num>");
        }


        String dayNumber = args[0].toLowerCase();
        int dayCount;
        try {
            dayCount = Integer.parseInt(dayNumber);
        } catch (NumberFormatException e) {
            throw new WrongUsageException("/night <num>");
        }
        if(dayCount < 1){
            throw new WrongUsageException("/night <num>");
        }

        world.setWorldTime((dayCount - 1) * 24000L + 12500L);
    }
}