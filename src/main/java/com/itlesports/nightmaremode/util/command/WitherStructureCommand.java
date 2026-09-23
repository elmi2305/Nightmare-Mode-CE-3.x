package com.itlesports.nightmaremode.util.command;

import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.util.WitherRitualTemplate;
import net.minecraft.src.*;

public final class WitherStructureCommand extends CommandBase {
    @Override
    public String getCommandName() {
        return "witherstructure";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/witherstructure <wither|bloodwither> [0|90|180|270]";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return NightmareMode.devMode && super.canCommandSenderUseCommand(sender);
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (!NightmareMode.devMode) throw new CommandException("This command requires dev mode.");
        if (args.length < 1 || args.length > 2) throw new WrongUsageException(getCommandUsage(sender));

        int ritual;
        if ("wither".equalsIgnoreCase(args[0])) {
            ritual = WitherRitualTemplate.WITHER;
        } else if ("bloodwither".equalsIgnoreCase(args[0])) {
            ritual = WitherRitualTemplate.BLOOD_WITHER;
        } else {
            throw new WrongUsageException(getCommandUsage(sender));
        }

        int degrees = args.length == 2 ? parseInt(sender, args[1]) : 0;
        if (degrees != 0 && degrees != 90 && degrees != 180 && degrees != 270) {
            throw new WrongUsageException(getCommandUsage(sender));
        }

        EntityPlayerMP player = getCommandSenderAsPlayer(sender);
        World world = player.worldObj;
        if (world.provider.dimensionId != 0) throw new CommandException("Wither rituals must be built in the Overworld.");

        int x = MathHelper.floor_double(player.posX);
        int floorY = MathHelper.floor_double(player.posY) - 1;
        int z = MathHelper.floor_double(player.posZ);
        int count;
        try {
            count = WitherRitualTemplate.build(world, x, floorY, z, degrees / 90, ritual);
        } catch (IllegalArgumentException | IllegalStateException exception) {
            throw new CommandException(exception.getMessage());
        }
        player.setPositionAndUpdate(x + 2.5, floorY + 1, z + 0.5);
        player.sendChatToPlayer(new ChatMessageComponent().addText("Built " + args[0].toLowerCase()
                + " ritual with " + count + " blocks, centered at X " + x + ", Z " + z
                + ", floor Y " + floorY + " (rotation " + degrees + "°). Moved you two blocks clear of the T."));
    }
}
