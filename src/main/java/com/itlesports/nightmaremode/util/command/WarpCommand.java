package com.itlesports.nightmaremode.util.command;

import net.minecraft.src.CommandBase;
import net.minecraft.src.ICommandSender;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;

import java.util.List;

public class WarpCommand extends CommandBase {

    @Override
    public String getCommandName() {
        return "warp";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/warp <nether|overworld|end|dimensionId> <player(optional)>";
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

        String dimensionArg = args[0].toLowerCase();

        switch (dimensionArg) {
            case "overworld":
            case "ow":
            case "world":
                targetDimension = 0;
                break;

            case "nether":
                targetDimension = -1;
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

        if (targetDimension != -1 && targetDimension != 0 && targetDimension != 1) {
            throw new WrongUsageException("Only dimensions -1, 0, and 1 are supported.");
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
        if (currentDimension == 0 && targetDimension == -1) {
            targetX /= 8.0D;
            targetZ /= 8.0D;
        } else if (currentDimension == -1 && targetDimension == 0) {
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

        int safeY = findSafeY(
                targetWorld,
                MathHelper.floor_double(targetX),
                MathHelper.floor_double(targetY),
                MathHelper.floor_double(targetZ)
        );

        player.travelToDimension(targetDimension);
        player.setPositionAndUpdate(targetX, safeY, targetZ);

//        player.playerNetServerHandler.setPlayerLocation(
//                targetX,
//                safeY,
//                targetZ,
//                player.rotationYaw,
//                player.rotationPitch
//        );

        player.sendChatToPlayer(new ChatMessageComponent().addText("Warped to dimension " + targetDimension));
    }
    private int findSafeY(World world, int x, int preferredY, int z) {

        // is current Y safe
        if (isSafe(world, x, preferredY, z)) {
            return preferredY;
        }

        // search up/down for 2 empty spaces
        for (int offset = 0; offset < 128; offset++) {

            int upY = preferredY + offset;

            if (upY < world.getHeight() && isSafe(world, x, upY, z)) {
                return upY;
            }

            int downY = preferredY - offset;

            if (downY > 1 && isSafe(world, x, downY, z)) {
                return downY;
            }
        }

        // fallback search entire column
        for (int y = world.getHeight() - 2; y >= 1; y--) {
            if (isSafe(world, x, y, z)) {
                return y;
            }
        }

        return world.getTopSolidOrLiquidBlock(x, z) + 1;
    }

    private boolean isSafe(World world, int x, int y, int z) {

        if (y <= 0 || y >= world.getHeight() - 1) {
            return false;
        }

        int feetBlock = world.getBlockId(x, y, z);
        int headBlock = world.getBlockId(x, y + 1, z);
        int groundBlock = world.getBlockId(x, y - 1, z);

        boolean feetAir = feetBlock == 0;
        boolean headAir = headBlock == 0;
        boolean solidGround = Block.blocksList[groundBlock] != null && Block.blocksList[groundBlock].blockMaterial.blocksMovement();

        return feetAir && headAir && solidGround;
    }
}