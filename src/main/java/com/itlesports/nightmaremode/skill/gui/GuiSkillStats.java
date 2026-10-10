package com.itlesports.nightmaremode.skill.gui;

import com.itlesports.nightmaremode.skill.SkillHandler;
import com.itlesports.nightmaremode.skill.SkillBranch;
import com.itlesports.nightmaremode.skill.SkillNet;
import com.itlesports.nightmaremode.skill.SkillRegistry;
import com.itlesports.nightmaremode.skill.SkillRewardActions;
import com.itlesports.nightmaremode.skill.SkillTreeData;
import com.itlesports.nightmaremode.skill.WorldSkillData;
import com.itlesports.nightmaremode.util.NMFields;
import com.itlesports.nightmaremode.util.NMUtils;
import btw.community.nightmaremode.NightmareMode;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.RenderHelper;
import net.minecraft.src.RenderItem;
import net.minecraft.src.ResourceLocation;
import net.minecraft.src.Tessellator;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GuiSkillStats extends GuiScreen {
    private static final int PANE_WIDTH = 320;
    private static final int PANE_HEIGHT = 220;
    private static final SkillTreeData DEFAULT_PLAYER_STATS = new SkillTreeData();
    private static final ResourceLocation BORDER_TEXTURE = new ResourceLocation(NMFields.modID, "textures/gui/skill/border.png");
    private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(NMFields.modID, "textures/gui/skill/background.png");
    private static final ResourceLocation TAB_OUTLINE_TEXTURE = new ResourceLocation(NMFields.modID, "textures/gui/skill/tab_outline.png");
    private final GuiSkillTree parent;
    private int scroll;

    public GuiSkillStats(GuiSkillTree parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        if (this.mc.thePlayer != null) SkillNet.sendSyncRequest();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        int left = (this.width - PANE_WIDTH) / 2;
        int top = (this.height - PANE_HEIGHT) / 2;
        GL11.glColor4f(1, 1, 1, 1);
        this.mc.renderEngine.bindTexture(BACKGROUND_TEXTURE);
        this.drawFullTexturedRect(left, top + 23, PANE_WIDTH - 12, PANE_HEIGHT - 28);
        this.mc.renderEngine.bindTexture(BORDER_TEXTURE);
        this.drawFullTexturedRect(left - 4, top + 18, PANE_WIDTH + 8, PANE_HEIGHT - 14);
        this.drawTabs(left, top, mouseX, mouseY);
        drawRect(left + 14, top + 34, left + 306, top + 210, 0x960A101B);
        this.fontRenderer.drawStringWithShadow("Statistics", left + 22, top + 40, 0xE7E7E7);
        drawRect(left + 20, top + 53, left + 299, top + 54, 0xA0BEC5C8);

        if (this.mc.thePlayer == null || this.mc.theWorld == null) return;
        SkillTreeData data = SkillHandler.getPlayerData(this.mc.thePlayer);
        WorldSkillData world = SkillHandler.getWorldData(this.mc.theWorld);
        List<String> lines = new ArrayList<>();
        lines.add("PLAYER STATS");
        addPlayerStats(lines, data, world);
        lines.add("PROGRESSION");
        int worldProgress = NMUtils.getWorldProgress();
        lines.add(progress("Nether access", world.netherAccessUnlockProgress,
                    SkillRewardActions.NETHER_ACCESS_PROGRESS_REQUIRED, world.netherAccessUnlocked));
        lines.add(progress("Diamond ore mining", data.diamondHarvestProgress, 5, data.canHarvestDiamondOre));
        if (world.netherAccessUnlocked) {
            lines.add(progress("Wither summoning", world.witherSummonUnlockProgress,
                    SkillRewardActions.WITHER_SUMMON_PROGRESS_REQUIRED, world.witherSummoningUnlocked));
        }
        if (worldProgress >= NMFields.POSTWITHER) {
            lines.add(progress("End access", world.endAccessUnlockProgress, 1, world.endAccessUnlocked));
        }
        if (NightmareMode.noSkybases) {
            lines.add(progress("Stable logs", world.woodGravityUnlockProgress, 4,
                    world.woodBlocksIgnoreSkybaseGravity));
        }
        if (world.netherAccessUnlocked) add(lines, "Nether post tiers", data.netherPostCompletedTiers);
        lines.add("MOVEMENT & MINING");
        add(lines, "Jumps", data.jumps);
        add(lines, "Blocks mined", data.blocksMined);
        add(lines, "Stone mined", data.stoneMined);
        add(lines, "Clay mined", data.clayMined);
        add(lines, "Dirt mined", data.dirtMined);
        add(lines, "Coal ore mined", data.coalOreMined);
        add(lines, "Iron ore mined", data.ironOreMined);
        add(lines, "Diamond ore mined", data.diamondOreMined);
        add(lines, "Nickel ore mined", data.nickelOreMined);
        add(lines, "Strata one stone mined", data.strataOneStoneMined);
        add(lines, "Leaves mined", data.leavesMined);
        add(lines, "Tall grass harvested", data.tallGrassMined);
        lines.add("FARMING & EXPLORATION");
        add(lines, "Saplings planted", data.saplingsPlanted);
        add(lines, "Crops planted", data.cropsPlanted);
        add(lines, "Mature crops harvested", data.fullyGrownCropsHarvested);
        add(lines, "Weeds removed", data.weedsRemoved);
        add(lines, "Animals tamed", data.animalsTamed);
        add(lines, "Animals bred", data.animalsBred);
        add(lines, "Cows milked", data.cowsMilked);
        add(lines, "Fish caught", data.fishCaught);
        add(lines, "Rare items caught", data.rareItemsCaught);
        add(lines, "Biomes visited", data.getVisitedBiomeCount());
        lines.add("COMBAT & ACTIVITY");
        add(lines, "Mobs killed", data.mobsKilled);
        add(lines, "Zombies killed", data.zombiesKilled);
        add(lines, "Skeletons killed", data.skeletonsKilled);
        add(lines, "Spiders killed", data.spidersKilled);
        add(lines, "Slimes killed", data.slimesKilled);
        add(lines, "Witches killed", data.witchesKilled);
        add(lines, "Endermen killed", data.endermenKilled);
        add(lines, "Withers killed", data.withersKilled);
        add(lines, "Arrows fired", data.arrowsFired);
        add(lines, "Trades completed", data.tradesCompleted);
        lines.add("Unique recipe outputs crafted: " + data.getUniqueCraftedOutputCount());
        add(lines, "Turntable rotations", data.turntableRotations);
        add(lines, "Food cooked", data.foodCooked);
        add(lines, "Potions brewed", data.potionsBrewed);
        add(lines, "Books crafted", data.booksCrafted);
        add(lines, "Bookshelves crafted", data.bookshelvesCrafted);
        add(lines, "Iron nuggets kilned", data.ironNuggetsKilned);

        for (int i = lines.size() - 1; i >= 0; --i) {
            String line = lines.get(i);
            if (line.equals(line.toUpperCase(java.util.Locale.ROOT)) && !line.contains(":")) {
                if (i == lines.size() - 1 || lines.get(i + 1).equals(lines.get(i + 1).toUpperCase(java.util.Locale.ROOT))) {
                    lines.remove(i);
                }
            }
        }
        if (lines.isEmpty()) lines.add("No statistics recorded yet");

        int visible = 15;
        this.scroll = Math.max(0, Math.min(this.scroll, Math.max(0, lines.size() - visible)));
        for (int i = 0; i < visible && i + this.scroll < lines.size(); ++i) {
            String line = lines.get(i + this.scroll);
            boolean heading = line.equals(line.toUpperCase(java.util.Locale.ROOT)) && !line.contains(":");
            this.fontRenderer.drawStringWithShadow(line, left + 23, top + 59 + i * 9,
                    heading ? 0xE1D6AC : 0xE2E8ED);
        }
        this.fontRenderer.drawStringWithShadow("Scroll for more", left + 23, top + 199, 0xB6BCC0);
        this.fontRenderer.drawStringWithShadow((this.scroll + 1) + "-" + Math.min(lines.size(), this.scroll + visible)
                + " / " + lines.size(), left + 252, top + 199, 0xB6BCC0);
    }

    private void drawTabs(int left, int top, int mouseX, int mouseY) {
        RenderItem renderer = new RenderItem();
        for (SkillBranch branch : SkillRegistry.getBranches()) {
            int x = left + 12 + branch.getIndex() * 30;
            int y = top - 7;
            GL11.glColor4f(.65f, .65f, .65f, 1);
            this.mc.renderEngine.bindTexture(TAB_OUTLINE_TEXTURE);
            this.drawFullTexturedRect(x, y, 26, 26);
            GL11.glColor4f(1, 1, 1, 1);
            RenderHelper.enableGUIStandardItemLighting();
            renderer.renderItemAndEffectIntoGUI(this.fontRenderer, this.mc.renderEngine, branch.getIcon(), x + 5, y + 5);
            RenderHelper.disableStandardItemLighting();
        }
        int x = left + 12 + SkillRegistry.getBranches().size() * 30;
        this.mc.renderEngine.bindTexture(TAB_OUTLINE_TEXTURE);
        this.drawFullTexturedRect(x, top - 7, 26, 26);
        RenderHelper.enableGUIStandardItemLighting();
        renderer.renderItemAndEffectIntoGUI(this.fontRenderer, this.mc.renderEngine, new ItemStack(Item.paper), x + 5, top - 2);
        RenderHelper.disableStandardItemLighting();
    }

    private void drawFullTexturedRect(int x, int y, int width, int height) {
        Tessellator t = Tessellator.instance;
        t.startDrawingQuads();
        t.addVertexWithUV(x, y + height, this.zLevel, 0, 1);
        t.addVertexWithUV(x + width, y + height, this.zLevel, 1, 1);
        t.addVertexWithUV(x + width, y, this.zLevel, 1, 0);
        t.addVertexWithUV(x, y, this.zLevel, 0, 0);
        t.draw();
    }

    private static String progress(String name, int count, int required, boolean unlocked) {
        return name + ": " + (unlocked ? "Unlocked" : Math.min(count, required) + " / " + required);
    }

    private static void addPlayerStats(List<String> lines, SkillTreeData data, WorldSkillData world) {
        if (com.itlesports.nightmaremode.world.BalanceProfile.isEasy()) {
            lines.add("Difficulty: Easy");
            lines.add("Ore mining speed: +200% before skills");
            lines.add("Hostile drops: 2x (knives: 3x / 4x / 5x)");
            lines.add("Tool and armor wear: 50% less");
            lines.add("Fuel duration: +100%");
            lines.add("Crop yield: +100%");
            lines.add("Hunger exhaustion: 65% less");
            lines.add("Pollution gain: " + percent(com.itlesports.nightmaremode.util.EasyBalance.pollution(1.0F)));
            lines.add("Optional stat counters: 2x credit");
            lines.add("Earned inventory capacity survives death");
        }
        addPercentBonus(lines, "Block breaking speed", com.itlesports.nightmaremode.util.EasyBalance.miningBonus(data.blockBreakSpeedBonus));
        addPercentBonus(lines, "Shovel speed", data.shovelSpeedBonus);
        addPercentBonus(lines, "Carcass harvesting speed", data.carcassHarvestSpeedBonus);
        if (world.globalPollutionReduction > 0.0F) {
            lines.add("Pollution produced (world): -" + percent(world.globalPollutionReduction));
        }
        addPercentBonus(lines, "Movement speed", data.movementSpeedBonus);
        addPercentBonus(lines, "Mechanical block speed", com.itlesports.nightmaremode.util.EasyBalance.processingBonus(data.machineSpeedBonus));
        addPercentBonus(lines, "Millstone processing speed", data.millstoneSpeedBonus);
        addPercentBonus(lines, "Kiln processing speed", com.itlesports.nightmaremode.util.EasyBalance.processingBonus(data.kilnSpeedBonus));
        addPercentBonus(lines, "Cistern processing speed", com.itlesports.nightmaremode.util.EasyBalance.processingBonus(data.cisternSpeedBonus));
        addPercentBonus(lines, "Brewing speed", com.itlesports.nightmaremode.util.EasyBalance.processingBonus(data.brewingSpeedBonus));
        if (data.clayCookTimeReductionTicks > 0) {
            lines.add("Clay cooking time: " + ((36000 - data.clayCookTimeReductionTicks) / 20) + " seconds");
        }
        float spoilageRate = com.itlesports.nightmaremode.util.EasyBalance.spoilage(Math.max(0.05F,
                data.foodSpoilageRateMultiplier * world.globalFoodSpoilageRateMultiplier));
        if (spoilageRate < DEFAULT_PLAYER_STATS.foodSpoilageRateMultiplier) {
            lines.add("Food spoil rate: " + percent(1.0F - spoilageRate));
        }
        addPercentBonus(lines, "Melee damage", data.meleeDamageBonus);
        addPercentBonus(lines, "Ranged damage", data.rangedDamageBonus);
        addPercent(lines, "Heat damage reduction", Math.min(1.0F, data.heatDamageReduction));
        addPercent(lines, "Oxygen loss reduction", Math.min(1.0F, data.oxygenLossReduction));
        addPercent(lines, "Armor durability save chance", data.armorDurabilitySaveChance);
        addPercent(lines, "Hammer durability save chance", data.hammerDurabilitySaveChance);
        addPercent(lines, "Mob bonus loot chance", data.mobLootChanceBonus + world.globalMobLootChanceBonus);
        addPercentBonus(lines, "Experience gain", data.xpGainBonus + world.globalXpGainBonus);
        addPercent(lines, "Enchanting cost reduction", data.enchantCostReduction);
        if (com.itlesports.nightmaremode.util.EasyBalance.deathLoss(data.deathItemLossChance) < DEFAULT_PLAYER_STATS.deathItemLossChance) {
            lines.add("Item loss chance on death: " + percent(com.itlesports.nightmaremode.util.EasyBalance.deathLoss(data.deathItemLossChance)));
        }
        add(lines, "Extra hotbar slots", data.extraHotbarSlots);
        add(lines, "Permanent XP hotbar slots", data.permanentXpHotbarSlots);
        if (data.secondInventoryRowUnlocked) lines.add("Second inventory row: permanent");
        add(lines, "Bonus coal dust per ore (pickaxe)", data.coalDustDropBonus);
        add(lines, "Bonus iron dust per ore (pickaxe)", data.ironDustDropBonus);
        addPercentBonus(lines, "Iron pile drop chance", data.ironPileChanceBonus + world.globalIronPileChanceBonus);
        addPercentBonus(lines, "Diamond rock drop chance", data.diamondRockDropChanceBonus);
        addPercent(lines, "Double nickel rock chance", data.doubleNickelRockChance);
        if (data.doubleLithiumDrops) lines.add("Lithium drops: +100%");
        addPercentBonus(lines, "Crystal drop chance", data.crystalDropChanceBonus);
        addPercentBonus(lines, "Blaze rod drop chance", data.blazeRodDropChanceBonus);
        addPercentBonus(lines, "Hemp seed drop chance", data.hempSeedChanceBonus);
        addPercentBonus(lines, "Twig drop chance", data.twigDropChanceBonus);
        addPercentBonus(lines, "Rare fish chance", data.rareFishChanceBonus);
        if (data.tallGrassAlwaysDropsPlantFiber) {
            lines.add("Tall grass plant fiber chance: 100%");
        } else {
            addPercentBonus(lines, "Tall grass plant fiber chance", data.tallGrassPlantFiberChanceBonus);
        }
        if (data.villagerProfessionChangeChance < DEFAULT_PLAYER_STATS.villagerProfessionChangeChance) {
            lines.add("Villager profession change chance: " + percent(data.villagerProfessionChangeChance));
        }
        if (world.globalVillagerHungerDrainRateMultiplier < 1.0F) {
            lines.add("Villager starvation: " + percent(1.0F - world.globalVillagerHungerDrainRateMultiplier) + " slower");
        }
    }

    private static void addPercentBonus(List<String> lines, String name, float bonus) {
        if (bonus > 0.0F) lines.add(name + ": +" + percent(bonus));
    }

    private static void addPercent(List<String> lines, String name, float value) {
        if (value > 0.0F) lines.add(name + ": " + percent(value));
    }

    private static String percent(float value) {
        String formatted = String.format(Locale.ROOT, "%.1f", value * 100.0F);
        if (formatted.endsWith(".0")) formatted = formatted.substring(0, formatted.length() - 2);
        return formatted + "%";
    }

    private static void add(List<String> lines, String name, int count) {
        if (count > 0) lines.add(name + ": " + count);
    }

    @Override
    public void handleMouseInput() {
        super.handleMouseInput();
        int wheel = Mouse.getEventDWheel();
        if (wheel != 0) this.scroll = Math.max(0, this.scroll + (wheel > 0 ? -3 : 3));
    }

    @Override
    protected void keyTyped(char character, int keyCode) {
        if (keyCode == Keyboard.KEY_ESCAPE) this.mc.displayGuiScreen(this.parent);
        else if (keyCode == Keyboard.KEY_UP) this.scroll = Math.max(0, this.scroll - 1);
        else if (keyCode == Keyboard.KEY_DOWN) this.scroll++;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
        int left = (this.width - PANE_WIDTH) / 2;
        int top = (this.height - PANE_HEIGHT) / 2;
        if (button == 0 && mouseY >= top - 7 && mouseY < top + 19) {
            for (SkillBranch branch : SkillRegistry.getBranches()) {
                int x = left + 12 + branch.getIndex() * 30;
                if (mouseX >= x && mouseX < x + 26) {
                    this.parent.selectBranch(branch.getIndex());
                    this.mc.displayGuiScreen(this.parent);
                    return;
                }
            }
        }
        super.mouseClicked(mouseX, mouseY, button);
    }
}
