package com.itlesports.nightmaremode.util.command;

import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.world.BalanceProfile;
import net.minecraft.src.*;

public final class TestEasyBalanceCommand extends CommandBase {
    public static final String[] NAMES = {"testeasypollution", "testeasyhungerdrain",
            "testeasyorequantities", "testeasydurability"};
    private final String name;

    public TestEasyBalanceCommand(String name) { this.name = name; }
    @Override public String getCommandName() { return this.name; }
    @Override public String getCommandUsage(ICommandSender sender) { return "/" + this.name; }
    @Override public int getRequiredPermissionLevel() { return 0; }
    @Override public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return BalanceProfile.isEasy() && sender instanceof EntityPlayer player
                && !player.capabilities.isCreativeMode;
    }

    @Override public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (!this.canCommandSenderUseCommand(sender)) throw new CommandException("Available in Easy survival mode.");
        if (args.length != 0) throw new WrongUsageException(this.getCommandUsage(sender));
        String key;
        boolean enabled;
        switch (this.name) {
            case "testeasypollution" -> { key = "TestEasyPollution"; enabled = !NightmareMode.testEasyPollution; }
            case "testeasyhungerdrain" -> { key = "TestEasyHungerDrain"; enabled = !NightmareMode.testEasyHungerDrain; }
            case "testeasyorequantities" -> { key = "TestEasyOreQuantities"; enabled = !NightmareMode.testEasyOreQuantities; }
            case "testeasydurability" -> { key = "TestEasyDurability"; enabled = !NightmareMode.testEasyDurability; }
            default -> throw new WrongUsageException(this.getCommandUsage(sender));
        }
        NightmareMode addon = NightmareMode.getInstance();
        addon.modifyConfigProperty(key, enabled, addon.addonConfig, false);
        switch (this.name) {
            case "testeasypollution" -> NightmareMode.testEasyPollution = enabled;
            case "testeasyhungerdrain" -> NightmareMode.testEasyHungerDrain = enabled;
            case "testeasyorequantities" -> NightmareMode.testEasyOreQuantities = enabled;
            case "testeasydurability" -> NightmareMode.testEasyDurability = enabled;
        }
        sender.sendChatToPlayer(ChatMessageComponent.createFromText("/" + this.name + ": "
                + (enabled ? "Hard behavior ON" : "Easy behavior restored")
                + ". Saved in config; applies to this game/server instance."));
    }
}
