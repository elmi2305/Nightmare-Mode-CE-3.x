package com.itlesports.nightmaremode.skill;

import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NBTTagInt;
import net.minecraft.src.NBTTagList;
import net.minecraft.src.NBTTagString;
import net.minecraft.src.ItemStack;

import java.util.HashSet;
import java.util.Set;

public class SkillTreeData {
    public String rewardsVersion = SkillRewardReload.LEGACY_VERSION;
    public int rewardsSchema;
    public long rewardsRevision;
    public int netherPostCompletedTiers;
    public int blocksMined;
    public int coalOreMined;
    public int ironOreMined;
    public int diamondOreMined;
    public int tallGrassMined;
    public int animalsTamed;
    public int animalsBred;
    public int mobsKilled;
    public int zombiesKilled;
    public int skeletonsKilled;
    public int fishCaught;
    public int rareItemsCaught;
    public int foodCooked;
    public int booksCrafted;
    public int potionsBrewed;
    public int clayMined;
    public int stoneMined;
    public int nickelOreMined;
    public int strataOneStoneMined;
    public int dirtMined;
    public int leavesMined;
    public int saplingsPlanted;
    public int cropsPlanted;
    public int fullyGrownCropsHarvested;
    public int weedsRemoved;
    public int cowsMilked;
    public int tradesCompleted;
    public int bookshelvesCrafted;
    public int witchesKilled;
    public int endermenKilled;
    public int spidersKilled;
    public int slimesKilled;
    public int withersKilled;
    public int jumps;
    public int arrowsFired;
    public int turntableRotations;
    public int ironNuggetsKilned;
    private final Set<Integer> visitedBiomeIds = new HashSet<>();
    private final Set<Integer> craftedOutputIds = new HashSet<>();

    public int ironDustDropBonus;
    public int coalDustDropBonus;
    public float blockBreakSpeedBonus;
    public float carcassHarvestSpeedBonus;
    public float mobLootChanceBonus;
    public float ironPileChanceBonus;
    public float kilnSpeedBonus;
    public float movementSpeedBonus;
    public float heatDamageReduction;
    public float armorDurabilitySaveChance;
    public float rangedDamageBonus;
    public float machineSpeedBonus;
    public float millstoneSpeedBonus;
    public float diamondRockDropChanceBonus;
    public float doubleNickelRockChance;
    public float hammerDurabilitySaveChance;
    public float cisternSpeedBonus;
    public float oxygenLossReduction;
    public float crystalDropChanceBonus;
    public float meleeDamageBonus;
    public float shovelSpeedBonus;
    public float blazeRodDropChanceBonus;
    public float hempSeedChanceBonus;
    public float twigDropChanceBonus;
    public float rareFishChanceBonus;
    public float tallGrassPlantFiberChanceBonus;
    public float deathItemLossChance = 0.75F;
    public int easyInventoryLevel;
    public float enchantCostReduction;
    public float xpGainBonus;
    public float brewingSpeedBonus;
    public float foodSpoilageRateMultiplier = 1.0F;
    public float villagerProfessionChangeChance = 0.40F;

    public int clayCookTimeReductionTicks;
    public int diamondHarvestProgress;
    public int leatherArmorUnlockProgress;
    public int ironIngotRecipeUnlockProgress;
    public int extraHotbarSlots;
    public int permanentXpHotbarSlots;

    public boolean canHarvestDiamondOre;
    public boolean canCureVillagers;
    public boolean grassBreaksInstantly;
    public boolean tallGrassAlwaysDropsPlantFiber;
    public boolean doubleLithiumDrops;
    public boolean canMineStrataThreeOre;
    public boolean canExceedXpLevelThirty;
    public boolean canFarmNetherWart;
    public boolean canGainExperience;
    public boolean thirdInventoryRowUnlocked;
    public boolean secondInventoryRowUnlocked;
    public boolean canUseCistern;
    public boolean canUseEnchantmentTable;
    public boolean canUseBrewingStand;
    public boolean canMineCrystals;
    public boolean canMineNetherrack;
    private final Set<String> unlockedNodes = new HashSet<>();

    public boolean isUnlocked(SkillNode node) {
        return node != null && this.unlockedNodes.contains(node.id.toString());
    }

    public boolean isUnlocked(String nodeId) {
        return this.unlockedNodes.contains(nodeId);
    }

    public void unlock(SkillNode node) {
        if (node != null) {
            this.unlockedNodes.add(node.id.toString());
        }
    }

    public Set<String> getUnlockedNodes() {
        return this.unlockedNodes;
    }

    public boolean visitBiome(int biomeId) {
        return this.visitedBiomeIds.add(biomeId);
    }

    public int getVisitedBiomeCount() {
        return this.visitedBiomeIds.size();
    }

    public boolean recordCraftedOutput(ItemStack output) {
        int outputKey = output.itemID << 16 | (output.getItemDamage() & 65535);
        return this.craftedOutputIds.add(outputKey);
    }

    public int getUniqueCraftedOutputCount() {
        return this.craftedOutputIds.size();
    }

    /** Reset derived rewards only; retain unlocks, counters and quest progress. */
    public void resetRewards() {
        SkillTreeData defaults = new SkillTreeData();
        this.ironDustDropBonus = defaults.ironDustDropBonus;
        this.coalDustDropBonus = defaults.coalDustDropBonus;
        this.blockBreakSpeedBonus = defaults.blockBreakSpeedBonus;
        this.carcassHarvestSpeedBonus = defaults.carcassHarvestSpeedBonus;
        this.mobLootChanceBonus = defaults.mobLootChanceBonus;
        this.ironPileChanceBonus = defaults.ironPileChanceBonus;
        this.kilnSpeedBonus = defaults.kilnSpeedBonus;
        this.movementSpeedBonus = defaults.movementSpeedBonus;
        this.heatDamageReduction = defaults.heatDamageReduction;
        this.armorDurabilitySaveChance = defaults.armorDurabilitySaveChance;
        this.rangedDamageBonus = defaults.rangedDamageBonus;
        this.machineSpeedBonus = defaults.machineSpeedBonus;
        this.millstoneSpeedBonus = defaults.millstoneSpeedBonus;
        this.diamondRockDropChanceBonus = defaults.diamondRockDropChanceBonus;
        this.doubleNickelRockChance = defaults.doubleNickelRockChance;
        this.hammerDurabilitySaveChance = defaults.hammerDurabilitySaveChance;
        this.cisternSpeedBonus = defaults.cisternSpeedBonus;
        this.oxygenLossReduction = defaults.oxygenLossReduction;
        this.crystalDropChanceBonus = defaults.crystalDropChanceBonus;
        this.meleeDamageBonus = defaults.meleeDamageBonus;
        this.shovelSpeedBonus = defaults.shovelSpeedBonus;
        this.blazeRodDropChanceBonus = defaults.blazeRodDropChanceBonus;
        this.hempSeedChanceBonus = defaults.hempSeedChanceBonus;
        this.twigDropChanceBonus = defaults.twigDropChanceBonus;
        this.rareFishChanceBonus = defaults.rareFishChanceBonus;
        this.tallGrassPlantFiberChanceBonus = defaults.tallGrassPlantFiberChanceBonus;
        this.deathItemLossChance = defaults.deathItemLossChance;
        this.enchantCostReduction = defaults.enchantCostReduction;
        this.xpGainBonus = defaults.xpGainBonus;
        this.brewingSpeedBonus = defaults.brewingSpeedBonus;
        this.foodSpoilageRateMultiplier = defaults.foodSpoilageRateMultiplier;
        this.villagerProfessionChangeChance = defaults.villagerProfessionChangeChance;
        this.clayCookTimeReductionTicks = defaults.clayCookTimeReductionTicks;
        this.diamondHarvestProgress = defaults.diamondHarvestProgress;
        this.leatherArmorUnlockProgress = defaults.leatherArmorUnlockProgress;
        this.ironIngotRecipeUnlockProgress = defaults.ironIngotRecipeUnlockProgress;
        this.extraHotbarSlots = defaults.extraHotbarSlots;
        this.permanentXpHotbarSlots = defaults.permanentXpHotbarSlots;
        this.canHarvestDiamondOre = defaults.canHarvestDiamondOre;
        this.canCureVillagers = defaults.canCureVillagers;
        this.grassBreaksInstantly = defaults.grassBreaksInstantly;
        this.tallGrassAlwaysDropsPlantFiber = defaults.tallGrassAlwaysDropsPlantFiber;
        this.doubleLithiumDrops = defaults.doubleLithiumDrops;
        this.canMineStrataThreeOre = defaults.canMineStrataThreeOre;
        this.canExceedXpLevelThirty = defaults.canExceedXpLevelThirty;
        this.canFarmNetherWart = defaults.canFarmNetherWart;
        this.canGainExperience = defaults.canGainExperience;
        this.thirdInventoryRowUnlocked = defaults.thirdInventoryRowUnlocked;
        this.secondInventoryRowUnlocked = defaults.secondInventoryRowUnlocked;
        this.canUseCistern = defaults.canUseCistern;
        this.canUseEnchantmentTable = defaults.canUseEnchantmentTable;
        this.canUseBrewingStand = defaults.canUseBrewingStand;
        this.canMineCrystals = defaults.canMineCrystals;
        this.canMineNetherrack = defaults.canMineNetherrack;
    }

    public static SkillTreeData readFromNBT(NBTTagCompound tag) {
        SkillTreeData data = new SkillTreeData();
        data.rewardsVersion = SkillRewardReload.savedVersion(tag.getString("RewardsVersion"));
        data.rewardsSchema = tag.getInteger("RewardsSchema");
        data.rewardsRevision = tag.getLong("RewardsRevision");
        data.ironDustDropBonus = tag.getInteger("IronDustDropBonus");
        data.coalDustDropBonus = tag.getInteger("CoalDustDropBonus");
        data.netherPostCompletedTiers = tag.getInteger("NetherPostCompletedTiers");
        data.blocksMined = tag.getInteger("BlocksMined");
        data.coalOreMined = tag.getInteger("CoalOreMined");
        data.ironOreMined = tag.getInteger("IronOreMined");
        data.diamondOreMined = tag.getInteger("DiamondOreMined");
        data.tallGrassMined = tag.getInteger("TallGrassMined");
        data.animalsTamed = tag.getInteger("AnimalsTamed");
        data.animalsBred = tag.getInteger("AnimalsBred");
        data.mobsKilled = tag.getInteger("MobsKilled");
        data.zombiesKilled = tag.getInteger("ZombiesKilled");
        data.skeletonsKilled = tag.getInteger("SkeletonsKilled");
        data.fishCaught = tag.getInteger("FishCaught");
        data.rareItemsCaught = tag.getInteger("RareItemsCaught");
        data.foodCooked = tag.getInteger("FoodCooked");
        data.booksCrafted = tag.getInteger("BooksCrafted");
        data.potionsBrewed = tag.getInteger("PotionsBrewed");
        data.clayMined = tag.getInteger("ClayMined");
        data.stoneMined = tag.getInteger("StoneMined");
        data.nickelOreMined = tag.getInteger("NickelOreMined");
        data.strataOneStoneMined = tag.hasKey("StrataOneStoneMined")
                ? tag.getInteger("StrataOneStoneMined") : tag.getInteger("StrataOneCobblestoneMined");
        data.dirtMined = tag.getInteger("DirtMined");
        data.leavesMined = tag.getInteger("LeavesMined");
        data.saplingsPlanted = tag.getInteger("SaplingsPlanted");
        data.cropsPlanted = tag.getInteger("CropsPlanted");
        data.fullyGrownCropsHarvested = tag.getInteger("FullyGrownCropsHarvested");
        data.weedsRemoved = tag.getInteger("WeedsRemoved");
        data.cowsMilked = tag.getInteger("CowsMilked");
        data.tradesCompleted = tag.getInteger("TradesCompleted");
        data.bookshelvesCrafted = tag.getInteger("BookshelvesCrafted");
        data.witchesKilled = tag.getInteger("WitchesKilled");
        data.endermenKilled = tag.getInteger("EndermenKilled");
        data.spidersKilled = tag.getInteger("SpidersKilled");
        data.slimesKilled = tag.getInteger("SlimesKilled");
        data.withersKilled = tag.getInteger("WithersKilled");
        data.jumps = tag.getInteger("Jumps");
        data.arrowsFired = tag.getInteger("ArrowsFired");
        data.turntableRotations = tag.getInteger("TurntableRotations");
        data.ironNuggetsKilned = tag.getInteger("IronNuggetsKilned");
        NBTTagList visitedBiomes = tag.getTagList("VisitedBiomeIds");
        for (int i = 0; i < visitedBiomes.tagCount(); ++i) {
            data.visitedBiomeIds.add(((NBTTagInt)visitedBiomes.tagAt(i)).data);
        }
        NBTTagList craftedOutputs = tag.getTagList("CraftedOutputIds");
        for (int i = 0; i < craftedOutputs.tagCount(); ++i) {
            data.craftedOutputIds.add(((NBTTagInt)craftedOutputs.tagAt(i)).data);
        }
        data.blockBreakSpeedBonus = tag.getFloat("BlockBreakSpeedBonus");
        data.carcassHarvestSpeedBonus = tag.getFloat("CarcassHarvestSpeedBonus");
        data.mobLootChanceBonus = tag.getFloat("MobLootChanceBonus");
        data.ironPileChanceBonus = tag.getFloat("IronPileChanceBonus");
        data.kilnSpeedBonus = tag.getFloat("KilnSpeedBonus");
        data.movementSpeedBonus = tag.getFloat("MovementSpeedBonus");
        data.heatDamageReduction = tag.getFloat("HeatDamageReduction");
        data.armorDurabilitySaveChance = tag.getFloat("ArmorDurabilitySaveChance");
        data.rangedDamageBonus = tag.getFloat("RangedDamageBonus");
        data.machineSpeedBonus = tag.getFloat("MachineSpeedBonus");
        data.millstoneSpeedBonus = tag.getFloat("MillstoneSpeedBonus");
        data.diamondRockDropChanceBonus = tag.getFloat("DiamondRockDropChanceBonus");
        data.doubleNickelRockChance = tag.getFloat("DoubleNickelRockChance");
        data.hammerDurabilitySaveChance = tag.getFloat("HammerDurabilitySaveChance");
        data.cisternSpeedBonus = tag.getFloat("CisternSpeedBonus");
        data.oxygenLossReduction = tag.getFloat("OxygenLossReduction");
        data.crystalDropChanceBonus = tag.getFloat("CrystalDropChanceBonus");
        data.meleeDamageBonus = tag.getFloat("MeleeDamageBonus");
        data.shovelSpeedBonus = tag.getFloat("ShovelSpeedBonus");
        data.blazeRodDropChanceBonus = tag.getFloat("BlazeRodDropChanceBonus");
        data.hempSeedChanceBonus = tag.getFloat("HempSeedChanceBonus");
        data.twigDropChanceBonus = tag.getFloat("TwigDropChanceBonus");
        data.rareFishChanceBonus = tag.getFloat("RareFishChanceBonus");
        data.tallGrassPlantFiberChanceBonus = tag.getFloat("TallGrassPlantFiberChanceBonus");
        data.easyInventoryLevel = Math.max(0, tag.getInteger("EasyInventoryLevel"));
        data.deathItemLossChance = tag.hasKey("DeathItemLossChance") ? tag.getFloat("DeathItemLossChance") : 0.75F;
        data.enchantCostReduction = tag.getFloat("EnchantCostReduction");
        data.xpGainBonus = tag.getFloat("XpGainBonus");
        data.brewingSpeedBonus = tag.getFloat("BrewingSpeedBonus");
        data.foodSpoilageRateMultiplier = tag.hasKey("FoodSpoilageRateMultiplier") ? tag.getFloat("FoodSpoilageRateMultiplier") : 1.0F;
        data.villagerProfessionChangeChance = tag.hasKey("VillagerProfessionChangeChance") ? tag.getFloat("VillagerProfessionChangeChance") : 0.40F;
        data.clayCookTimeReductionTicks = tag.getInteger("ClayCookTimeReductionTicks");
        data.diamondHarvestProgress = tag.getInteger("DiamondHarvestProgress");
        data.leatherArmorUnlockProgress = tag.getInteger("LeatherArmorUnlockProgress");
        data.ironIngotRecipeUnlockProgress = tag.getInteger("IronIngotRecipeUnlockProgress");
        data.extraHotbarSlots = tag.getInteger("ExtraHotbarSlots");
        data.permanentXpHotbarSlots = tag.getInteger("PermanentXpHotbarSlots");
        data.canHarvestDiamondOre = tag.getBoolean("CanHarvestDiamondOre");
        data.canCureVillagers = tag.getBoolean("CanCureVillagers");
        data.grassBreaksInstantly = tag.getBoolean("GrassBreaksInstantly");
        data.tallGrassAlwaysDropsPlantFiber = tag.getBoolean("TallGrassAlwaysDropsPlantFiber");
        data.doubleLithiumDrops = tag.getBoolean("DoubleLithiumDrops");
        data.canMineStrataThreeOre = tag.getBoolean("CanMineStrataThreeOre");
        data.canExceedXpLevelThirty = tag.getBoolean("CanExceedXpLevelThirty");
        data.canFarmNetherWart = tag.getBoolean("CanFarmNetherWart");
        data.canGainExperience = tag.getBoolean("CanGainExperience");
        data.thirdInventoryRowUnlocked = tag.getBoolean("ThirdInventoryRowUnlocked");
        data.secondInventoryRowUnlocked = tag.getBoolean("SecondInventoryRowUnlocked");
        data.canUseCistern = tag.getBoolean("CanUseCistern");
        data.canUseEnchantmentTable = tag.getBoolean("CanUseEnchantmentTable");
        data.canUseBrewingStand = tag.getBoolean("CanUseBrewingStand");
        data.canMineCrystals = tag.getBoolean("CanMineCrystals");
        data.canMineNetherrack = tag.getBoolean("CanMineNetherrack");
        NBTTagList unlocked = tag.getTagList("UnlockedNodes");
        for (int i = 0; i < unlocked.tagCount(); ++i) {
            data.unlockedNodes.add(((NBTTagString)unlocked.tagAt(i)).data);
        }
        if (data.isUnlocked(NMSkillNodes.BRING_CLAY_BALL_32)) {
            data.canGainExperience = true;
        }
        return data;
    }

    public static void writeToNBT(NBTTagCompound tag, SkillTreeData data) {
        tag.setString("RewardsVersion", SkillRewardReload.savedVersion(data.rewardsVersion));
        tag.setInteger("RewardsSchema", data.rewardsSchema);
        tag.setLong("RewardsRevision", data.rewardsRevision);
        tag.setInteger("IronDustDropBonus", data.ironDustDropBonus);
        tag.setInteger("CoalDustDropBonus", data.coalDustDropBonus);
        tag.setInteger("NetherPostCompletedTiers", data.netherPostCompletedTiers);
        tag.setInteger("BlocksMined", data.blocksMined);
        tag.setInteger("CoalOreMined", data.coalOreMined);
        tag.setInteger("IronOreMined", data.ironOreMined);
        tag.setInteger("DiamondOreMined", data.diamondOreMined);
        tag.setInteger("TallGrassMined", data.tallGrassMined);
        tag.setInteger("AnimalsTamed", data.animalsTamed);
        tag.setInteger("AnimalsBred", data.animalsBred);
        tag.setInteger("MobsKilled", data.mobsKilled);
        tag.setInteger("ZombiesKilled", data.zombiesKilled);
        tag.setInteger("SkeletonsKilled", data.skeletonsKilled);
        tag.setInteger("FishCaught", data.fishCaught);
        tag.setInteger("RareItemsCaught", data.rareItemsCaught);
        tag.setInteger("FoodCooked", data.foodCooked);
        tag.setInteger("BooksCrafted", data.booksCrafted);
        tag.setInteger("PotionsBrewed", data.potionsBrewed);
        tag.setInteger("ClayMined", data.clayMined);
        tag.setInteger("StoneMined", data.stoneMined);
        tag.setInteger("NickelOreMined", data.nickelOreMined);
        tag.setInteger("StrataOneStoneMined", data.strataOneStoneMined);
        tag.setInteger("DirtMined", data.dirtMined);
        tag.setInteger("LeavesMined", data.leavesMined);
        tag.setInteger("SaplingsPlanted", data.saplingsPlanted);
        tag.setInteger("CropsPlanted", data.cropsPlanted);
        tag.setInteger("FullyGrownCropsHarvested", data.fullyGrownCropsHarvested);
        tag.setInteger("WeedsRemoved", data.weedsRemoved);
        tag.setInteger("CowsMilked", data.cowsMilked);
        tag.setInteger("TradesCompleted", data.tradesCompleted);
        tag.setInteger("BookshelvesCrafted", data.bookshelvesCrafted);
        tag.setInteger("WitchesKilled", data.witchesKilled);
        tag.setInteger("EndermenKilled", data.endermenKilled);
        tag.setInteger("SpidersKilled", data.spidersKilled);
        tag.setInteger("SlimesKilled", data.slimesKilled);
        tag.setInteger("WithersKilled", data.withersKilled);
        tag.setInteger("Jumps", data.jumps);
        tag.setInteger("ArrowsFired", data.arrowsFired);
        tag.setInteger("TurntableRotations", data.turntableRotations);
        tag.setInteger("IronNuggetsKilned", data.ironNuggetsKilned);
        NBTTagList visitedBiomes = new NBTTagList("VisitedBiomeIds");
        for (Integer biomeId : data.visitedBiomeIds) {
            visitedBiomes.appendTag(new NBTTagInt("", biomeId));
        }
        tag.setTag("VisitedBiomeIds", visitedBiomes);
        NBTTagList craftedOutputs = new NBTTagList("CraftedOutputIds");
        for (Integer itemId : data.craftedOutputIds) {
            craftedOutputs.appendTag(new NBTTagInt("", itemId));
        }
        tag.setTag("CraftedOutputIds", craftedOutputs);
        tag.setFloat("BlockBreakSpeedBonus", data.blockBreakSpeedBonus);
        tag.setFloat("CarcassHarvestSpeedBonus", data.carcassHarvestSpeedBonus);
        tag.setFloat("MobLootChanceBonus", data.mobLootChanceBonus);
        tag.setFloat("IronPileChanceBonus", data.ironPileChanceBonus);
        tag.setFloat("KilnSpeedBonus", data.kilnSpeedBonus);
        tag.setFloat("MovementSpeedBonus", data.movementSpeedBonus);
        tag.setFloat("HeatDamageReduction", data.heatDamageReduction);
        tag.setFloat("ArmorDurabilitySaveChance", data.armorDurabilitySaveChance);
        tag.setFloat("RangedDamageBonus", data.rangedDamageBonus);
        tag.setFloat("MachineSpeedBonus", data.machineSpeedBonus);
        tag.setFloat("MillstoneSpeedBonus", data.millstoneSpeedBonus);
        tag.setFloat("DiamondRockDropChanceBonus", data.diamondRockDropChanceBonus);
        tag.setFloat("DoubleNickelRockChance", data.doubleNickelRockChance);
        tag.setFloat("HammerDurabilitySaveChance", data.hammerDurabilitySaveChance);
        tag.setFloat("CisternSpeedBonus", data.cisternSpeedBonus);
        tag.setFloat("OxygenLossReduction", data.oxygenLossReduction);
        tag.setFloat("CrystalDropChanceBonus", data.crystalDropChanceBonus);
        tag.setFloat("MeleeDamageBonus", data.meleeDamageBonus);
        tag.setFloat("ShovelSpeedBonus", data.shovelSpeedBonus);
        tag.setFloat("BlazeRodDropChanceBonus", data.blazeRodDropChanceBonus);
        tag.setFloat("HempSeedChanceBonus", data.hempSeedChanceBonus);
        tag.setFloat("TwigDropChanceBonus", data.twigDropChanceBonus);
        tag.setFloat("RareFishChanceBonus", data.rareFishChanceBonus);
        tag.setFloat("TallGrassPlantFiberChanceBonus", data.tallGrassPlantFiberChanceBonus);
        tag.setFloat("DeathItemLossChance", data.deathItemLossChance);
        tag.setInteger("EasyInventoryLevel", data.easyInventoryLevel);
        tag.setFloat("EnchantCostReduction", data.enchantCostReduction);
        tag.setFloat("XpGainBonus", data.xpGainBonus);
        tag.setFloat("BrewingSpeedBonus", data.brewingSpeedBonus);
        tag.setFloat("FoodSpoilageRateMultiplier", data.foodSpoilageRateMultiplier);
        tag.setFloat("VillagerProfessionChangeChance", data.villagerProfessionChangeChance);
        tag.setInteger("ClayCookTimeReductionTicks", data.clayCookTimeReductionTicks);
        tag.setInteger("DiamondHarvestProgress", data.diamondHarvestProgress);
        tag.setInteger("LeatherArmorUnlockProgress", data.leatherArmorUnlockProgress);
        tag.setInteger("IronIngotRecipeUnlockProgress", data.ironIngotRecipeUnlockProgress);
        tag.setInteger("ExtraHotbarSlots", data.extraHotbarSlots);
        tag.setInteger("PermanentXpHotbarSlots", data.permanentXpHotbarSlots);
        tag.setBoolean("CanHarvestDiamondOre", data.canHarvestDiamondOre);
        tag.setBoolean("CanCureVillagers", data.canCureVillagers);
        tag.setBoolean("GrassBreaksInstantly", data.grassBreaksInstantly);
        tag.setBoolean("TallGrassAlwaysDropsPlantFiber", data.tallGrassAlwaysDropsPlantFiber);
        tag.setBoolean("DoubleLithiumDrops", data.doubleLithiumDrops);
        tag.setBoolean("CanMineStrataThreeOre", data.canMineStrataThreeOre);
        tag.setBoolean("CanExceedXpLevelThirty", data.canExceedXpLevelThirty);
        tag.setBoolean("CanFarmNetherWart", data.canFarmNetherWart);
        tag.setBoolean("CanGainExperience", data.canGainExperience);
        tag.setBoolean("ThirdInventoryRowUnlocked", data.thirdInventoryRowUnlocked);
        tag.setBoolean("SecondInventoryRowUnlocked", data.secondInventoryRowUnlocked);
        tag.setBoolean("CanUseCistern", data.canUseCistern);
        tag.setBoolean("CanUseEnchantmentTable", data.canUseEnchantmentTable);
        tag.setBoolean("CanUseBrewingStand", data.canUseBrewingStand);
        tag.setBoolean("CanMineCrystals", data.canMineCrystals);
        tag.setBoolean("CanMineNetherrack", data.canMineNetherrack);
        NBTTagList unlocked = new NBTTagList("UnlockedNodes");
        for (String id : data.unlockedNodes) {
            unlocked.appendTag(new NBTTagString("", id));
        }
        tag.setTag("UnlockedNodes", unlocked);
    }
}
