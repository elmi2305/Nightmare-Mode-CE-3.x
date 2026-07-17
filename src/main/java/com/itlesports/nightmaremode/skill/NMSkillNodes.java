package com.itlesports.nightmaremode.skill;

import api.achievement.AchievementHandler;
import btw.block.BTWBlocks;
import btw.item.BTWItems;
import com.itlesports.nightmaremode.achievements.NMAchievements;
import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.util.NMFields;
import net.minecraft.src.Block;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ResourceLocation;

import static com.itlesports.nightmaremode.skill.SkillNodeProvider.getBuilder;

public class NMSkillNodes {
    public static final SkillBranch MINING = new SkillBranch("Mining", Item.pickaxeIron);
    public static final SkillBranch HUSBANDRY = new SkillBranch("Husbandry", Item.wheat);
    public static final SkillBranch RITUAL = new SkillBranch("Ritual", Item.blazePowder);
    public static final SkillBranch KNOWLEDGE = new SkillBranch("Knowledge", Item.book);
    public static final SkillBranch COMBAT = new SkillBranch("Combat", Item.swordIron);

    // x -> right
    // y -> down
    public static final SkillNode MINING_INITIATE = getBuilder()
            .id(loc("mining_initiate"))
            .name("Mining Initiate")
            .icon(Block.stone)
            .displayLocation(0, 0)
            .requirementText("Mine 1000 blocks.")
            .triggerCondition((player, world) -> SkillHandler.getPlayerData(player).blocksMined >= 1000)
            .reward("+5% block breaking speed.", SkillRewardActions.addBlockBreakSpeed(0.05F))
            .build()
            .register(MINING);

    public static final SkillNode MINE_GRASS = getBuilder()
            .id(loc("mine_grass"))
            .name("Weed Inspector")
            .icon(Block.tallGrass)
            .displayLocation(0, -1)
            .requirementText("Mine 250 Tall Grass.")
            .triggerCondition((player, world) -> SkillHandler.getPlayerData(player).tallGrassMined >= 250)
            .reward("Tall Grass breaks instantly.", SkillRewardActions.setTallGrassBreaksInstantly())
            .build()
            .register(MINING);

    public static final SkillNode IRON_SURVEYOR = getBuilder()
            .id(loc("iron_surveyor"))
            .name("Iron Surveyor")
            .icon(Block.oreIron)
            .displayLocation(2, 0)
            .requirementText("Mine 10 iron ore blocks.")
            .triggerCondition((player, world) -> SkillHandler.getPlayerData(player).ironOreMined >= 10)
            .parents(MINING_INITIATE)
            .reward("+5% block breaking speed.", SkillRewardActions.addBlockBreakSpeed(0.05F))
            .build()
            .register(MINING);

    public static final SkillNode DIAMOND_EXTRACTION = getBuilder()
            .id(loc("diamond_extraction"))
            .name("Diamond Extraction")
            .icon(Block.oreDiamond)
            .displayLocation(4, 0)
            .requirementText("Bring 4 polished crystal shards.")
            .triggerCondition((player, world) -> SkillInventory.has(player, NMItems.polishedCrystalShard.itemID, 0, false, 4))
            .onUnlockConsume((player, world) -> SkillInventory.consume(player, NMItems.polishedCrystalShard.itemID, 0, false, 4))
            .parents(IRON_SURVEYOR)
            .reward("Diamond ore can be harvested.", SkillRewardActions.unlockDiamondHarvest())
            .build()
            .register(MINING);













    // ========================================================================================================
    // ======================================= HUSBANDRY ======================================================
    // ========================================================================================================

    public static final SkillNode HERDSMAN = getBuilder()
            .id(loc("herdsman"))
            .name("Herdsman")
            .icon(Item.leash)
            .displayLocation(0, 0)
            .requirementText("Tame 3 animals. Tracker field is ready for horse/dog/cat hooks.")
            .triggerCondition((player, world) -> SkillHandler.getPlayerData(player).animalsTamed >= 3)
            .reward("Food spoilage takes longer. Hook-ready reward flag.", SkillRewardActions.slowFoodSpoilage())
            .build()
            .register(HUSBANDRY);

    public static final SkillNode ANIMAL_KEEPER = getBuilder()
            .id(loc("animal_keeper"))
            .name("Animal Keeper")
            .icon(BTWItems.breedingHarness)
            .displayLocation(2, 0)
            .requirementText("Breed 5 animals. Tracker field is ready for breeding hooks.")
            .triggerCondition((player, world) -> SkillHandler.getPlayerData(player).animalsBred >= 5)
            .parents(HERDSMAN)
            .reward("Animal-related rewards can attach here.", (player, world) -> {})
            .build()
            .register(HUSBANDRY);

    public static final SkillNode FISHER = getBuilder()
            .id(loc("fisher"))
            .name("Fisher")
            .icon(Item.fishingRod)
            .displayLocation(1, 2)
            .requirementText("Catch 10 fish.")
            .triggerCondition((player, world) -> SkillHandler.getPlayerData(player).fishCaught >= 10)
            .reward("+3% mob loot drop chance.", SkillRewardActions.addMobLootChance(0.03F))
            .build()
            .register(HUSBANDRY);

    public static final SkillNode TIMBER_RIGHTS = getBuilder()
            .id(loc("timber_rights"))
            .name("Timber Rights")
            .icon(Block.wood)
            .displayLocation(4, 1)
            .requirementText("Bring 64 books as land surveys.")
            .triggerCondition((player, world) -> SkillInventory.has(player, Item.book, 64))
            .onUnlockConsume((player, world) -> SkillInventory.consume(player, Item.book, 64))
            .parents(ANIMAL_KEEPER)
            .reward("Wood-based blocks ignore no-skybase gravity globally.", SkillRewardActions.disableWoodGravity())
            .worldReward()
            .build()
            .register(HUSBANDRY);




















    // ========================================================================================================
    // ========================================== RITUAL ======================================================
    // ========================================================================================================


    public static final SkillNode ALCHEMICAL_APPARATUS = getBuilder()
            .id(loc("alchemical_apparatus"))
            .name("Alchemical Apparatus")
            .icon(Item.brewingStand)
            .displayLocation(0, 0)
            .requirementText("Bring a brewing stand.")
            .triggerCondition((player, world) -> SkillInventory.has(player, Item.brewingStand, 1))
            .onUnlockConsume((player, world) -> SkillInventory.consume(player, Item.brewingStand, 1))
            .reward("Nether portal entry is unlocked globally.", SkillRewardActions.unlockNetherAccess())
            .worldReward()
            .build()
            .register(RITUAL);

    public static final SkillNode APOTHECARY = getBuilder()
            .id(loc("apothecary"))
            .name("Apothecary")
            .icon(Item.potion)
            .displayLocation(2, 0)
            .requirementText("Brew 3 potions. Tracker field is ready for brewing stand hooks.")
            .triggerCondition((player, world) -> SkillHandler.getPlayerData(player).potionsBrewed >= 3)
            .parents(ALCHEMICAL_APPARATUS)
            .reward("Villager curing is allowed for this player.", SkillRewardActions.unlockVillagerCuring())
            .build()
            .register(RITUAL);

    public static final SkillNode ENCHANTING_FOCUS = getBuilder()
            .id(loc("enchanting_focus"))
            .name("Enchanting Focus")
            .icon(Block.enchantmentTable)
            .displayLocation(1, 2)
            .requirementText("Bring an enchanting table.")
            .triggerCondition((player, world) -> SkillInventory.has(player, Block.enchantmentTable.blockID, 0, false, 1))
            .onUnlockConsume((player, world) -> SkillInventory.consume(player, Block.enchantmentTable.blockID, 0, false, 1))
            .reward("+5% block breaking speed.", SkillRewardActions.addBlockBreakSpeed(0.05F))
            .build()
            .register(RITUAL);












    // ========================================================================================================
    // ======================================= KNOWLEDGE ======================================================
    // ========================================================================================================

    public static final SkillNode SCRIBE = getBuilder()
            .id(loc("scribe"))
            .name("Scribe")
            .icon(Item.book)
            .displayLocation(0, 0)
            .requirementText("Bring 16 books.")
            .triggerCondition((player, world) -> SkillInventory.has(player, Item.book, 16))
            .onUnlockConsume((player, world) -> SkillInventory.consume(player, Item.book, 16))
            .reward("+3% block breaking speed.", SkillRewardActions.addBlockBreakSpeed(0.03F))
            .build()
            .register(KNOWLEDGE);

    public static final SkillNode ACHIEVEMENT_STUDY = getBuilder()
            .id(loc("achievement_study"))
            .name("Achievement Study")
            .icon(BTWBlocks.wickerBasket)
            .displayLocation(2, 0)
            .requirementText("Unlock the custom basket achievement.")
            .triggerCondition((player, world) -> AchievementHandler.hasUnlocked(player, NMAchievements.CRAFT_BASKET_NM))
            .parents(SCRIBE)
            .reward("+2% block breaking speed.", SkillRewardActions.addBlockBreakSpeed(0.02F))
            .build()
            .register(KNOWLEDGE);

    public static final SkillNode REDSTONE_PRIMER = getBuilder()
            .id(loc("redstone_primer"))
            .name("Redstone Primer")
            .icon(Item.redstone)
            .displayLocation(1, 2)
            .requirementText("Bring 32 redstone dust.")
            .triggerCondition((player, world) -> SkillInventory.has(player, Item.redstone, 32))
            .onUnlockConsume((player, world) -> SkillInventory.consume(player, Item.redstone, 32))
            .reward("Knowledge branch machinery rewards can attach here.", (player, world) -> {})
            .build()
            .register(KNOWLEDGE);


















    // ========================================================================================================
    // ======================================= COMBAT =========================================================
    // ========================================================================================================


    public static final SkillNode MOB_HUNTER = getBuilder()
            .id(loc("mob_hunter"))
            .name("Mob Hunter")
            .icon(Item.bone)
            .displayLocation(0, 0)
            .requirementText("Kill 25 mobs.")
            .triggerCondition((player, world) -> SkillHandler.getPlayerData(player).mobsKilled >= 25)
            .reward("+5% mob loot drop chance.", SkillRewardActions.addMobLootChance(0.05F))
            .build()
            .register(COMBAT);

    public static final SkillNode ZOMBIE_CULLER = getBuilder()
            .id(loc("zombie_culler"))
            .name("Zombie Culler")
            .icon(Item.rottenFlesh)
            .displayLocation(2, 0)
            .requirementText("Kill 10 zombies.")
            .triggerCondition((player, world) -> SkillHandler.getPlayerData(player).zombiesKilled >= 10)
            .parents(MOB_HUNTER)
            .reward("+5% mob loot drop chance.", SkillRewardActions.addMobLootChance(0.05F))
            .build()
            .register(COMBAT);

    public static final SkillNode RARE_TROPHY = getBuilder()
            .id(loc("rare_trophy"))
            .name("Rare Trophy")
            .icon(Item.enderPearl)
            .displayLocation(4, 1)
            .requirementText("Catch 1 rare item while fishing. Tracker field is wired to fishing loot hook.")
            .triggerCondition((player, world) -> SkillHandler.getPlayerData(player).rareItemsCaught >= 1)
            .parents(MOB_HUNTER)
            .reward("+3% mob loot drop chance.", SkillRewardActions.addMobLootChance(0.03F))
            .build()
            .register(COMBAT);

    public static void initialize() {
        // Forces static initialization.
    }

    private static ResourceLocation loc(String path) {
        return new ResourceLocation(NMFields.modID, "skill/" + path);
    }
}
