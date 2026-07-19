package com.itlesports.nightmaremode.skill;

import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NBTTagList;
import net.minecraft.src.NBTTagString;

import java.util.HashSet;
import java.util.Set;

public class SkillTreeData {
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
    public int strataOneCobblestoneMined;
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

    public float blockBreakSpeedBonus;
    public float mobLootChanceBonus;
    public float ironPileChanceBonus;
    public float craftingDurabilityBonus;
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

    public static SkillTreeData readFromNBT(NBTTagCompound tag) {
        SkillTreeData data = new SkillTreeData();
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
        data.strataOneCobblestoneMined = tag.getInteger("StrataOneCobblestoneMined");
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
        data.blockBreakSpeedBonus = tag.getFloat("BlockBreakSpeedBonus");
        data.mobLootChanceBonus = tag.getFloat("MobLootChanceBonus");
        data.ironPileChanceBonus = tag.getFloat("IronPileChanceBonus");
        data.craftingDurabilityBonus = tag.getFloat("CraftingDurabilityBonus");
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
        data.canUseCistern = tag.getBoolean("CanUseCistern");
        data.canUseEnchantmentTable = tag.getBoolean("CanUseEnchantmentTable");
        data.canUseBrewingStand = tag.getBoolean("CanUseBrewingStand");
        data.canMineCrystals = tag.getBoolean("CanMineCrystals");
        data.canMineNetherrack = tag.getBoolean("CanMineNetherrack");
        NBTTagList unlocked = tag.getTagList("UnlockedNodes");
        for (int i = 0; i < unlocked.tagCount(); ++i) {
            data.unlockedNodes.add(((NBTTagString)unlocked.tagAt(i)).data);
        }
        return data;
    }

    public static void writeToNBT(NBTTagCompound tag, SkillTreeData data) {
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
        tag.setInteger("StrataOneCobblestoneMined", data.strataOneCobblestoneMined);
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
        tag.setFloat("BlockBreakSpeedBonus", data.blockBreakSpeedBonus);
        tag.setFloat("MobLootChanceBonus", data.mobLootChanceBonus);
        tag.setFloat("IronPileChanceBonus", data.ironPileChanceBonus);
        tag.setFloat("CraftingDurabilityBonus", data.craftingDurabilityBonus);
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
