package com.itlesports.nightmaremode.util.command;

import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.util.interfaces.BalanceWorldInfo;
import com.itlesports.nightmaremode.world.BalanceProfile;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;
import java.util.List;

public final class TestJourneySettingsCommand extends CommandBase {
    private final boolean combat;

    public TestJourneySettingsCommand(boolean combat) { this.combat = combat; }
    @Override public String getCommandName() { return this.combat ? "testdifficulty" : "testcomplexity"; }
    @Override public String getCommandUsage(ICommandSender sender) {
        return "/" + this.getCommandName() + (this.combat ? " [easy|hard]" : " [low|high]");
    }
    @Override public int getRequiredPermissionLevel() { return 0; }
    @Override public boolean canCommandSenderUseCommand(ICommandSender sender) { return NightmareMode.showBetaOverlay; }
    @Override public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args,
                this.combat ? new String[]{"easy", "hard"} : new String[]{"low", "high"}) : null;
    }

    @Override public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (!this.canCommandSenderUseCommand(sender)) throw new CommandException("Tester commands require beta testing to be enabled.");
        if (args.length != 1 || !(this.combat ? BalanceProfile.EASY.equals(args[0]) || BalanceProfile.HARD.equals(args[0])
                : BalanceProfile.LOW.equals(args[0]) || BalanceProfile.HIGH.equals(args[0])))
            throw new WrongUsageException(this.getCommandUsage(sender));
        MinecraftServer server = MinecraftServer.getServer();
        if (server == null || server.worldServers == null || server.worldServers[0] == null)
            throw new CommandException("No world is loaded.");
        String previous = ((BalanceWorldInfo)server.worldServers[0].getWorldInfo()).nm$getBalanceProfile();
        String combat = this.combat ? args[0] : BalanceProfile.combat(previous);
        String progression = this.combat ? BalanceProfile.progression(previous) : args[0];
        String selected = BalanceProfile.profile(combat, progression);
        BalanceProfile.selectPending(combat, progression);
        for (WorldServer world : server.worldServers) {
            if (world == null) continue;
            ((BalanceWorldInfo)world.getWorldInfo()).nm$setBalanceProfile(selected);
        }
        WorldServer overworld = server.worldServers[0];
        overworld.getSaveHandler().saveWorldInfo(overworld.getWorldInfo());
        server.getConfigurationManager().sendChatMsg(ChatMessageComponent.createFromText(
                "Journey settings saved for the entire world: " + BalanceProfile.journeyName(selected)
                        + ". Restart the game/server to apply."));
    }
}
