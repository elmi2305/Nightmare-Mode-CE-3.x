package com.itlesports.nightmaremode.util.command;

import com.itlesports.nightmaremode.util.NMFields;
import com.itlesports.nightmaremode.util.underworld.UWUtils;
import net.minecraft.src.CommandBase;
import net.minecraft.src.ICommandSender;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;

import java.util.List;
import java.util.Locale;

public class WarpCommand extends CommandBase {

    @Override
    public String getCommandName() {
        return "warp";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/warp <nether|overworld|end|underworld|dimensionId> <player(optional)>";
    }

    @Override
    public List getCommandAliases() {
        return List.of("tpdim", "sendto", "dim");
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {

        if (args.length < 1) {
            throw new WrongUsageException(getCommandUsage(sender));
        }

        EntityPlayerMP player;

        if (args.length >= 2) {
            player = getPlayer(sender, args[1]);
        } else {
            if (!(sender instanceof EntityPlayerMP)) {
                throw new PlayerNotFoundException();
            }

            player = (EntityPlayerMP) sender;
        }

        int targetDimension;

        String dimensionArg = args[0].toLowerCase(Locale.ROOT);

        switch (dimensionArg) {
            case "overworld":
            case "ow":
            case "world":
                targetDimension = 0;
                break;

            case "nether":
                targetDimension = -1;
                break;

            case "underworld":
            case "uw":
                targetDimension = NMFields.UNDERWORLD_DIMENSION;
                break;

            case "end":
                targetDimension = 1;
                break;

            default:
                try {
                    targetDimension = Integer.parseInt(dimensionArg);
                } catch (NumberFormatException e) {
                    throw new WrongUsageException(getCommandUsage(sender));
                }
                break;
        }

        if (targetDimension != -1 && targetDimension != 0 && targetDimension != 1 && targetDimension != NMFields.UNDERWORLD_DIMENSION) {
            throw new WrongUsageException("Unsupported dimension.");
        }

        int currentDimension = player.dimension;

        if (currentDimension == targetDimension) {
            player.sendChatToPlayer(new ChatMessageComponent().addText("Player is already in that dimension."));
            return;
        }

        double targetX = player.posX;
        double targetY = player.posY;
        double targetZ = player.posZ;

        // nether coordinate scaling
        if (currentDimension != -1 && targetDimension == -1) {
            targetX /= 8.0D;
            targetZ /= 8.0D;
        } else if (currentDimension == -1 && targetDimension != -1) {
            targetX *= 8.0D;
            targetZ *= 8.0D;
        }

        // end always goes to 0,0
        if (targetDimension == 1) {
            targetX = 0.0D;
            targetZ = 0.0D;
        }

        MinecraftServer server = MinecraftServer.getServer();
        WorldServer targetWorld = server.worldServerForDimension(targetDimension);

        if (targetWorld == null || targetWorld.provider.dimensionId != targetDimension) {
            throw new CommandException("The requested dimension is not available.");
        }
        int x = MathHelper.clamp_int(MathHelper.floor_double(targetX), -29995000, 29995000);
        int z = MathHelper.clamp_int(MathHelper.floor_double(targetZ), -29995000, 29995000);
        ChunkCoordinates landing = targetDimension == NMFields.UNDERWORLD_DIMENSION
                ? UWUtils.findBlightlandsLanding(targetWorld, x, z)
                : UWUtils.findSafePosition(targetWorld, x, MathHelper.floor_double(targetY), z);
        if (landing == null) throw new CommandException("No safe landing was found in the requested dimension.");
        if (targetDimension == NMFields.UNDERWORLD_DIMENSION) {
            UWUtils.transferPlayer(player, targetDimension, landing.posX + 0.5, landing.posY, landing.posZ + 0.5);
        } else {
            UWUtils.travelPlayer(player, targetDimension, landing.posX + 0.5, landing.posY, landing.posZ + 0.5);
        }

        player.sendChatToPlayer(new ChatMessageComponent().addText("Warped to dimension " + targetDimension));
    }



}
