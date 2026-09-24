package com.itlesports.nightmaremode.util.command;

import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.skill.SkillHandler;
import com.itlesports.nightmaremode.skill.SkillNode;
import com.itlesports.nightmaremode.skill.SkillRegistry;
import com.itlesports.nightmaremode.skill.SkillTreeData;
import net.minecraft.src.ChatMessageComponent;
import net.minecraft.src.CommandBase;
import net.minecraft.src.CommandException;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.ICommandSender;
import net.minecraft.src.WrongUsageException;

public final class TestToolsCommand extends CommandBase {
    public static final String[] NAMES = {
            "testhelp", "testdevmode", "testunlockallskills", "testalwaysshowskillrewards",
            "testfreeskills", "testdisableplayerfatigue", "testfullinventorycapacity"
    };

    private final String name;

    public TestToolsCommand(String name) {
        this.name = name;
    }

    @Override public String getCommandName() { return name; }
    @Override public String getCommandUsage(ICommandSender sender) { return "/" + name; }
    @Override public int getRequiredPermissionLevel() { return 0; }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return NightmareMode.showBetaOverlay && super.canCommandSenderUseCommand(sender);
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (!NightmareMode.showBetaOverlay) throw new CommandException("Tester commands require the beta overlay.");
        if (args.length != 0) throw new WrongUsageException(getCommandUsage(sender));

        switch (name) {
            case "testhelp" -> {
                reply(sender, "Tester commands (available while the beta overlay is enabled):");
                reply(sender, "/testdevmode: toggle developer behavior, including full inventory, debug helpers, and altered mob/world checks.");
                reply(sender, "/testunlockallskills: unlock every skill node for you, including world rewards.");
                reply(sender, "/testalwaysshowskillrewards: toggle hidden skill reward descriptions.");
                reply(sender, "/testfreeskills: toggle unlocking skills by clicking without requirements.");
                reply(sender, "/testdisableplayerfatigue: toggle fatigue suppression.");
                reply(sender, "/testfullinventorycapacity: toggle 9 hotbar and 27 backpack slots.");
                reply(sender, "Current: dev mode " + state(NightmareMode.devMode)
                        + ", rewards " + state(NightmareMode.alwaysShowRewards)
                        + ", free skills " + state(NightmareMode.unlockSkillsWithClick)
                        + ", fatigue suppression " + state(NightmareMode.disableFatigue)
                        + ", full inventory " + state(NightmareMode.fullInventoryCapacity) + ".");
                reply(sender, "Session toggles reset on restart; skill unlocks are saved. Dev mode does not redo startup setup or generated terrain.");
            }
            case "testdevmode" -> {
                NightmareMode.devMode = !NightmareMode.devMode;
                reply(sender, "Dev mode: " + state(NightmareMode.devMode)
                        + ". Changes inventory capacity, debug helpers, mobs, rituals, and some world checks for this session.");
                reply(sender, "This toggle cannot change item visibility or world setup already decided at startup; existing terrain stays as generated.");
            }
            case "testunlockallskills" -> {
                EntityPlayerMP player = getCommandSenderAsPlayer(sender);
                SkillHandler.unlockAllSkillsForTester(player);
                SkillTreeData data = player.getData(NightmareMode.SKILL_TREE);
                int unlocked = 0;
                int total = 0;
                for (SkillNode node : SkillRegistry.getNodes()) {
                    total++;
                    if (data.isUnlocked(node)) unlocked++;
                }
                reply(sender, "Your skill nodes: " + unlocked + "/" + total
                        + " unlocked. World skill rewards were also applied where needed and this progress is saved.");
            }
            case "testalwaysshowskillrewards" -> {
                NightmareMode.alwaysShowRewards = !NightmareMode.alwaysShowRewards;
                reply(sender, "Always show skill reward descriptions: " + state(NightmareMode.alwaysShowRewards) + ".");
            }
            case "testfreeskills" -> {
                NightmareMode.unlockSkillsWithClick = !NightmareMode.unlockSkillsWithClick;
                reply(sender, "Unlock skills by clicking without requirements: " + state(NightmareMode.unlockSkillsWithClick)
                        + ". Sandbox restrictions still apply.");
            }
            case "testdisableplayerfatigue" -> {
                NightmareMode.disableFatigue = !NightmareMode.disableFatigue;
                reply(sender, "Player fatigue suppression: " + state(NightmareMode.disableFatigue) + ".");
            }
            case "testfullinventorycapacity" -> {
                NightmareMode.fullInventoryCapacity = !NightmareMode.fullInventoryCapacity;
                reply(sender, "Force full inventory capacity: " + state(NightmareMode.fullInventoryCapacity)
                        + " (9 hotbar, 27 backpack slots when on). Dev mode or creative may also grant full capacity.");
            }
            default -> throw new WrongUsageException(getCommandUsage(sender));
        }
    }

    private static String state(boolean value) {
        return value ? "ON" : "OFF";
    }

    private static void reply(ICommandSender sender, String message) {
        sender.sendChatToPlayer(ChatMessageComponent.createFromText(message));
    }
}
