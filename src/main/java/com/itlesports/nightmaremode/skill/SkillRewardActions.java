package com.itlesports.nightmaremode.skill;

import btw.community.nightmaremode.NightmareMode;

import java.util.function.Consumer;

public final class SkillRewardActions {
    private SkillRewardActions() {
    }

    private static SkillUnlockAction playerReward(Consumer<SkillTreeData> reward) {
        return (player, world) -> {
            SkillTreeData data = player.getData(NightmareMode.SKILL_TREE);
            reward.accept(data);
            player.setData(NightmareMode.SKILL_TREE, data);
        };
    }

    private static SkillUnlockAction worldReward(Consumer<WorldSkillData> reward) {
        return (player, world) -> {
            WorldSkillData data = world.getData(NightmareMode.WORLD_SKILL_TREE);
            reward.accept(data);
            world.setData(NightmareMode.WORLD_SKILL_TREE, data);
        };
    }

    public static SkillUnlockAction addBlockBreakSpeed(float amount) {
        return playerReward(data -> data.blockBreakSpeedBonus += amount);
    }

    public static SkillUnlockAction addMobLootChance(float amount) {
        return playerReward(data -> data.mobLootChanceBonus += amount);
    }

    public static SkillUnlockAction addGlobalMobLootChance(float amount) {
        return worldReward(data -> data.globalMobLootChanceBonus += amount);
    }

    public static SkillUnlockAction setTallGrassBreaksInstantly() {
        return playerReward(data -> data.grassBreaksInstantly = true);
    }

    public static SkillUnlockAction unlockDiamondHarvest() {
        return playerReward(data -> data.canHarvestDiamondOre = true);
    }

    public static SkillUnlockAction addDiamondHarvestProgress() {
        return playerReward(data -> {
            data.diamondHarvestProgress++;
            data.canHarvestDiamondOre = data.diamondHarvestProgress >= 5;
        });
    }

    public static SkillUnlockAction addLeatherArmorProgress() {
        return playerReward(data -> data.leatherArmorUnlockProgress++);
    }

    public static SkillUnlockAction addIronIngotRecipeProgress() {
        return playerReward(data -> data.ironIngotRecipeUnlockProgress++);
    }

    public static SkillUnlockAction unlockVillagerCuring() {
        return playerReward(data -> data.canCureVillagers = true);
    }

    public static SkillUnlockAction slowFoodSpoilage() {
        return multiplyFoodSpoilageRate(0.95F);
    }

    public static SkillUnlockAction slowFoodSpoilageGlobally() {
        return worldReward(data -> data.globalFoodSpoilageRateMultiplier *= 0.95F);
    }

    public static SkillUnlockAction multiplyFoodSpoilageRate(float multiplier) {
        return playerReward(data -> data.foodSpoilageRateMultiplier *= multiplier);
    }

    public static SkillUnlockAction addClayCookTimeReduction(int ticks) {
        return playerReward(data -> data.clayCookTimeReductionTicks += ticks);
    }

    public static SkillUnlockAction addIronPileChance(float amount) {
        return playerReward(data -> data.ironPileChanceBonus += amount);
    }

    public static SkillUnlockAction addGlobalIronPileChance(float amount) {
        return worldReward(data -> data.globalIronPileChanceBonus += amount);
    }

    public static SkillUnlockAction addCraftingDurability(float amount) {
        return playerReward(data -> data.craftingDurabilityBonus += amount);
    }

    public static SkillUnlockAction doubleLithiumDrops() {
        return playerReward(data -> data.doubleLithiumDrops = true);
    }

    public static SkillUnlockAction unlockStrataThreeOre() {
        return playerReward(data -> data.canMineStrataThreeOre = true);
    }

    public static SkillUnlockAction addDiamondRockDropChance(float amount) {
        return playerReward(data -> data.diamondRockDropChanceBonus += amount);
    }

    public static SkillUnlockAction guaranteeDiamondRockDrop() {
        return playerReward(data -> data.diamondRockDropChanceBonus = 1.0F);
    }

    public static SkillUnlockAction addDoubleNickelRockChance(float amount) {
        return playerReward(data -> data.doubleNickelRockChance += amount);
    }

    public static SkillUnlockAction addHammerDurabilitySaveChance(float amount) {
        return playerReward(data -> data.hammerDurabilitySaveChance += amount);
    }

    public static SkillUnlockAction addCisternSpeed(float amount) {
        return playerReward(data -> data.cisternSpeedBonus += amount);
    }

    public static SkillUnlockAction addOxygenLossReduction(float amount) {
        return playerReward(data -> data.oxygenLossReduction += amount);
    }

    public static SkillUnlockAction addCrystalDropChance(float amount) {
        return playerReward(data -> data.crystalDropChanceBonus += amount);
    }

    public static SkillUnlockAction addMeleeDamage(float amount) {
        return playerReward(data -> data.meleeDamageBonus += amount);
    }

    public static SkillUnlockAction addShovelSpeed(float amount) {
        return playerReward(data -> data.shovelSpeedBonus += amount);
    }

    public static SkillUnlockAction addBlazeRodDropChance(float amount) {
        return playerReward(data -> data.blazeRodDropChanceBonus += amount);
    }

    public static SkillUnlockAction addHempSeedChance(float amount) {
        return playerReward(data -> data.hempSeedChanceBonus += amount);
    }

    public static SkillUnlockAction addTwigDropChance(float amount) {
        return playerReward(data -> data.twigDropChanceBonus += amount);
    }

    public static SkillUnlockAction addRareFishChance(float amount) {
        return playerReward(data -> data.rareFishChanceBonus += amount);
    }

    public static SkillUnlockAction addTallGrassPlantFiberChance(float amount) {
        return playerReward(data -> data.tallGrassPlantFiberChanceBonus += amount);
    }

    public static SkillUnlockAction alwaysDropPlantFiberFromTallGrass() {
        return playerReward(data -> data.tallGrassAlwaysDropsPlantFiber = true);
    }

    public static SkillUnlockAction unlockXpAboveThirty() {
        return playerReward(data -> data.canExceedXpLevelThirty = true);
    }

    public static SkillUnlockAction unlockNetherWartFarming() {
        return playerReward(data -> data.canFarmNetherWart = true);
    }

    public static SkillUnlockAction unlockExperienceGain() {
        return playerReward(data -> data.canGainExperience = true);
    }

    public static SkillUnlockAction addEnchantCostReduction(float amount) {
        return playerReward(data -> data.enchantCostReduction += amount);
    }

    public static SkillUnlockAction addHotbarSlots(int amount) {
        return playerReward(data -> data.extraHotbarSlots = Math.min(2, data.extraHotbarSlots + amount));
    }

    public static SkillUnlockAction unlockThirdInventoryRow() {
        return playerReward(data -> data.thirdInventoryRowUnlocked = true);
    }

    public static SkillUnlockAction setVillagerProfessionChangeChance(float chance) {
        return playerReward(data -> data.villagerProfessionChangeChance = chance);
    }

    public static SkillUnlockAction unlockCisternUse() {
        return playerReward(data -> data.canUseCistern = true);
    }

    public static SkillUnlockAction unlockEnchantmentTableUse() {
        return playerReward(data -> data.canUseEnchantmentTable = true);
    }

    public static SkillUnlockAction addXpGain(float amount) {
        return playerReward(data -> data.xpGainBonus += amount);
    }

    public static SkillUnlockAction addGlobalXpGain(float amount) {
        return worldReward(data -> data.globalXpGainBonus += amount);
    }

    public static SkillUnlockAction unlockBrewingStandUse() {
        return playerReward(data -> data.canUseBrewingStand = true);
    }

    public static SkillUnlockAction addBrewingSpeed(float amount) {
        return playerReward(data -> data.brewingSpeedBonus += amount);
    }

    public static SkillUnlockAction unlockCrystalMining() {
        return playerReward(data -> data.canMineCrystals = true);
    }

    public static SkillUnlockAction unlockNetherrackMining() {
        return playerReward(data -> data.canMineNetherrack = true);
    }

    public static SkillUnlockAction unlockNetherAccess() {
        return worldReward(data -> data.netherAccessUnlocked = true);
    }

    public static SkillUnlockAction addNetherAccessProgress() {
        return worldReward(data -> {
            data.netherAccessUnlockProgress++;
            data.netherAccessUnlocked = data.netherAccessUnlockProgress >= 7;
        });
    }

    public static SkillUnlockAction disableWoodGravity() {
        return worldReward(data -> data.woodBlocksIgnoreSkybaseGravity = true);
    }

    public static SkillUnlockAction addWoodGravityProgress() {
        return worldReward(data -> {
            data.woodGravityUnlockProgress++;
            data.woodBlocksIgnoreSkybaseGravity = data.woodGravityUnlockProgress >= 4;
        });
    }

    public static SkillUnlockAction addWitherSummonProgress() {
        return worldReward(data -> {
            data.witherSummonUnlockProgress++;
            data.witherSummoningUnlocked = data.witherSummonUnlockProgress >= 5;
        });
    }

    public static SkillUnlockAction addEndAccessProgress() {
        return worldReward(data -> {
            data.endAccessUnlockProgress++;
            data.endAccessUnlocked = data.endAccessUnlockProgress >= 1;
        });
    }
}
