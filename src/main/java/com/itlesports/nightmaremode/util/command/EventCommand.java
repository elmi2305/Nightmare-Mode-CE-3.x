package com.itlesports.nightmaremode.util.command;

import btw.community.nightmaremode.NightmareMode;
import btw.world.BTWWorldData;
import com.itlesports.nightmaremode.util.NMUtils;
import net.minecraft.src.*;

import java.util.List;

public class EventCommand extends CommandBase {
    private static long reference;

    @Override
    public String getCommandName() {
        return "event";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }
    public List getCommandAliases() {
        return List.of("event", "ev", "evset", "bmec");
    }


    @Override
    public String getCommandUsage(ICommandSender iCommandSender) {
        return "/ev <bloodmoon|bm|eclipse|ec> <true|false|t|f|1|0>";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        EntityPlayerMP player = (EntityPlayerMP) sender;

        if (args.length == 0) {
            boolean bloodMoon = NMUtils.getIsBloodMoon();
            boolean eclipse = NMUtils.getIsEclipse();

            String activeEvent;

            if (bloodMoon && eclipse) {
                activeEvent = "Blood Moon + Eclipse";
            } else if (bloodMoon) {
                activeEvent = "Blood Moon";
            } else if (eclipse) {
                activeEvent = "Eclipse";
            } else {
                activeEvent = "None";
            }

            player.sendChatToPlayer(
                    (new ChatMessageComponent()).addText("Active event: " + activeEvent)
            );

            return;
        }

        if (args.length < 2) {
            throw new WrongUsageException("/event <bloodmoon|bm|eclipse|ec> <true|false|t|f|1|0>");
        }

        String eventArg = args[0].toLowerCase();
        String valueArg = args[1].toLowerCase();

        boolean value;

        if (valueArg.equals("true") || valueArg.equals("t") || valueArg.equals("1")) {
            value = true;
        } else if (valueArg.equals("false") || valueArg.equals("f") || valueArg.equals("0")) {
            value = false;
        } else {
            throw new WrongUsageException("/event <bloodmoon|bm|eclipse|ec> <true|false|t|f|1|0>");
        }

        World world = player.worldObj;

        reference = world.getWorldTime();

        if (eventArg.equals("bloodmoon") || eventArg.equals("bm")) {

            if (value) {
                world.setWorldTime(NMUtils.getNextBloodMoonTime(world.getWorldTime()) - 5000);
                NightmareMode.worldState = Math.max(NightmareMode.worldState, 1);
            } else{
                if (NMUtils.getIsBloodMoon()) {
                    world.setWorldTime(NMUtils.getNextBloodMoonTime(world.getWorldTime()) + 6000);
                }
            }
            NightmareMode.setBloodmoon(value);

        }
        else if (eventArg.equals("eclipse") || eventArg.equals("ec")) {
            NightmareMode.setEclipse(value);

            if (value) {
                world.setWorldTime(NMUtils.getNextEclipseTime(world.getWorldTime()));
                NightmareMode.worldState = Math.max(NightmareMode.worldState, 3);
            } else{
                if (NMUtils.getIsEclipse()) {
                    world.setWorldTime(NMUtils.getNextEclipseTime(world.getWorldTime()) + 24000);
                }
            }
        }
        else {
            throw new WrongUsageException("/event <bloodmoon|bm|eclipse|ec> <true|false|t|f|1|0>");
        }

        boolean netherAccessed = NightmareMode.worldState >= 1;
        boolean witherSummoned = NightmareMode.worldState >= 2;
        boolean endAccessed = NightmareMode.worldState >= 3;

        world.setData(BTWWorldData.NETHER_ACCESSED, netherAccessed);
        world.setData(BTWWorldData.WITHER_SUMMONED, witherSummoned);
        world.setData(BTWWorldData.END_ACCESSED, endAccessed);
        world.setData(BTWWorldData.DRAGON_KILLED, endAccessed);
        world.setData(NightmareMode.DRAGON_DEFEATED, endAccessed);

        NightmareMode.sendWorldStateToAllPlayers();

        player.sendChatToPlayer(
                (new ChatMessageComponent()).addText(
                        "Set " + eventArg + " to " + value
                )
        );
    }

    public static void setTimeToReference(World w){
        if(reference != 0){
            w.setWorldTime(reference);
            reference = 0;
        }
    }
}
