package com.itlesports.nightmaremode.fishing;

import btw.item.BTWItems;
import com.itlesports.nightmaremode.agriculture.ChunkAttributeManager;
import com.itlesports.nightmaremode.crafting.recipe.types.FishingRodUpgradeRecipe;
import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.item.items.ItemNetherFishingRod;
import com.itlesports.nightmaremode.skill.SkillHandler;
import com.itlesports.nightmaremode.util.ArmorSetHelper;
import com.itlesports.nightmaremode.util.NMFishingRates;
import com.itlesports.nightmaremode.util.NMUtils;
import com.itlesports.nightmaremode.util.elements.FishingCatch;
import net.minecraft.src.*;
import java.util.Locale;

public final class FishingPool {
    private static final FishingCatch[] JUNK_CATCHES = {
            catchOf(NMItems.seaweed, 12, false),
            catchOf(NMItems.scrapedBark, 5, false),
            catchOf(BTWItems.pointyStick, 3, false),
            catchOf(Item.stick, 4, false),
            catchOf(Item.itemsList[Block.dirt.blockID], 5, false),
            catchOf(Item.itemsList[Block.sand.blockID], 5, false),
            catchOf(Item.itemsList[Block.gravel.blockID], 5, false)
    };

    // each biome has its own common fish and scarce trophy fish.
    private static final FishingCatch[] OCEAN_CATCHES = {
            catchOf(Item.fishRaw, 2, false),
            catchOf(NMItems.mackerel, 26, false), catchOf(NMItems.cod, 22, false),
            catchOf(NMItems.tuna, 14, false), catchOf(NMItems.swordfish, 2, true)
    };
    private static final FishingCatch[] RIVER_CATCHES = {
            catchOf(Item.fishRaw, 2, false),
            catchOf(NMItems.bass, 26, false), catchOf(NMItems.trout, 22, false),
            catchOf(NMItems.carp, 14, false), catchOf(NMItems.goldenCarp, 2, true)
    };
    private static final FishingCatch[] SWAMP_CATCHES = {
            catchOf(Item.fishRaw, 2, false),
            catchOf(NMItems.mudfish, 26, false), catchOf(NMItems.catfish, 22, false),
            catchOf(NMItems.swampEel, 14, false), catchOf(NMItems.alligatorGar, 2, true)
    };
    private static final FishingCatch[] JUNGLE_CATCHES = {
            catchOf(Item.fishRaw, 2, false),
            catchOf(NMItems.piranha, 26, false), catchOf(NMItems.neonTetra, 22, false),
            catchOf(NMItems.jungleCatfish, 14, false), catchOf(NMItems.arapaima, 2, true)
    };
    private static final FishingCatch[] COLD_CATCHES = {
            catchOf(Item.fishRaw, 2, false),
            catchOf(NMItems.salmon, 26, false), catchOf(NMItems.perch, 22, false),
            catchOf(NMItems.icefish, 14, false), catchOf(NMItems.frostfish, 2, true)
    };
    private static final FishingCatch[] DESERT_CATCHES = {
            catchOf(Item.fishRaw, 2, false),
            catchOf(NMItems.desertMinnow, 26, false), catchOf(NMItems.sandfish, 22, false),
            catchOf(NMItems.tilapia, 14, false), catchOf(NMItems.duneKoi, 2, true)
    };
    private static final FishingCatch[] NETHER_CATCHES = {
            catchOf(NMItems.obsidianShard, 4, false),
            catchOf(NMItems.ashClump, 8, false),
            catchOf(NMItems.netherrackChunk, 10, false)
    };
    private static final FishingCatch[] BAITED_NETHER_CATCHES = {
            catchOf(NMItems.obsidianShard, 2, false),
            catchOf(NMItems.ashClump, 8, false),
            catchOf(NMItems.lavafish, 5, false),
            catchOf(NMItems.netherrackChunk, 12, false)
    };

    private final EntityFishHook hook;
    private boolean baited;
    public final FishingHabitat habitat;
    public final boolean lava;

    public FishingPool(EntityFishHook hook, boolean baited) {
        this.hook = hook;
        this.baited = baited;
        ItemStack rod = hook.angler.getCurrentEquippedItem();
        lava = rod != null && rod.getItem() instanceof ItemNetherFishingRod;
        habitat = new FishingHabitat(hook, lava);
    }

    public float stock() {
        return ChunkAttributeManager.getFishingStockFraction(hook.worldObj, x(), z());
    }

    public float arrivals() {
        float rod = rodMultiplier(hook.angler.getCurrentEquippedItem())
                * (1 - 0.10F * ArmorSetHelper.getAnglerPieceCount(hook.angler));
        int odds = lava ? NMFishingRates.netherBiteOdds(rod, has("IfhyFishingLure"))
                : NMFishingRates.biteOdds(hook.worldObj.getWorldTime(), hook.worldObj.getMoonPhase(),
                        NMUtils.getIsBloodMoon(), raining(), stock(), rod, has("IfhyFishingLure"));
        return NMFishingRates.arrivalTicks(odds);
    }

    public int activationTicks() {
        return NMFishingRates.activationTicks(arrivals(), rodMultiplier(hook.angler.getCurrentEquippedItem()), abundance());
    }

    public float abundance() {
        return NMFishingRates.abundance(hook.worldObj.getWorldTime(), hook.worldObj.getMoonPhase(), NMUtils.getIsBloodMoon(),
                raining(), hook.worldObj.isThundering(), lava)
                * FishingBalance.abundance(com.itlesports.nightmaremode.world.BalanceProfile.isLowProgression());
    }

    public void setBaited(boolean value) { baited = value; }

    public float captureMultiplier() {
        return NMFishingRates.captureMultiplier(rodMultiplier(hook.angler.getCurrentEquippedItem()), has("IfhyFishingAutoReel"));
    }

    public float resistance() {
        return NMFishingRates.pullResistance(rodMultiplier(hook.angler.getCurrentEquippedItem()));
    }

    public FishingGame.Spec next(boolean initial, float depth) {
        if (lava) {
            ItemStack rod = hook.angler.getCurrentEquippedItem();
            if (rod != null && (rod.getItem() == NMItems.tungstenFishingRod || rod.getItem() == NMItems.tungstenFishingRodBaited)
                    && hook.worldObj.rand.nextFloat() < (baited ? 0.65F : 0.80F)) return spec(baited ? NMItems.lavafish : NMItems.obsidianShard);
            return spec(weighted(baited ? BAITED_NETHER_CATCHES : NETHER_CATCHES).item);
        }
        if (!initial && depth > 0 && habitat.biome.contains("ocean") && depth >= habitat.maxDepth * 0.94F
                && hook.worldObj.rand.nextFloat() < 0.008F) return spec(NMItems.pearl);
        float bonus = SkillHandler.getPlayerData(hook.angler).rareFishChanceBonus
                + 0.025F * ArmorSetHelper.getAnglerPieceCount(hook.angler)
                + (has("IfhyRareFishLure") ? 0.20F : 0);
        float rareChance = Math.min(1, rarityChance(depth, bonus, raining()) * habitat.rarityMultiplier
                * FishingBalance.rarity(com.itlesports.nightmaremode.world.BalanceProfile.isLowProgression()));
        if (!initial && hook.worldObj.rand.nextFloat() < rareChance) {
            java.util.List<FishingGame.Spec> rare = new java.util.ArrayList<>();
            FishingCatch[] local = biomeCatches();
            FishingGame.Spec trophy = spec(local[local.length - 1].item);
            if (depth >= trophy.minDepth) rare.add(trophy);
            for (FishingCreatures.Entry entry : FishingCreatures.entries()) {
                if (entry.matches(biomeKey()) && depth >= entry.spec.minDepth
                        && (entry.spec.itemId != FishingCreatures.GIANT_SQUID || habitat.supportsGiantSquid())) rare.add(entry.spec);
            }
            if (!rare.isEmpty()) {
                for (FishingGame.Spec candidate : rare)
                    if (candidate.itemId == FishingTuning.FORCED_RARE_CREATURE_ID) return candidate;
                return rare.get(hook.worldObj.rand.nextInt(rare.size()));
            }
        }
        float fishChance = FishingGame.clamp((baited ? 0.72F : 0.55F) + abundance() * 0.07F, 0.5F, 0.96F);
        if (hook.worldObj.rand.nextFloat() > fishChance) return spec(weighted(JUNK_CATCHES).item);
        FishingCatch[] fish = biomeCatches();
        int total = 0;
        for (FishingCatch entry : fish) if (!entry.rare) total += entry.weight;
        int roll = hook.worldObj.rand.nextInt(total);
        for (FishingCatch entry : fish) if (!entry.rare && (roll -= entry.weight) < 0) return spec(entry.item);
        return spec(fish[0].item);
    }

    public static float rarityChance(float depth, float skillBonus, boolean rain) {
        return NMFishingRates.rareAppearanceChance(depth, skillBonus, rain);
    }

    private String biomeKey() {
        String name = habitat.biome;
        if (name.contains("ocean")) return "ocean";
        if (name.contains("beach")) return "beach";
        if (name.contains("swamp")) return "swamp";
        if (name.contains("jungle")) return "jungle";
        if (name.contains("taiga") || name.contains("ice") || name.contains("frozen")) return "cold";
        if (name.contains("desert")) return "desert";
        return "river";
    }

    private FishingCatch weighted(FishingCatch[] table) {
        int roll = hook.worldObj.rand.nextInt(totalWeight(table));
        for (FishingCatch entry : table) if ((roll -= entry.weight) < 0) return entry;
        return table[0];
    }

    private static int totalWeight(FishingCatch[] table) {
        int total = 0;
        for (FishingCatch entry : table) total += entry.weight;
        return total;
    }

    private FishingCatch[] biomeCatches() {
        String name = hook.worldObj.getBiomeGenForCoords(x(), z()).biomeName.toLowerCase(Locale.ROOT);
        if (name.contains("ocean") || name.contains("beach")) return OCEAN_CATCHES;
        if (name.contains("swamp")) return SWAMP_CATCHES;
        if (name.contains("jungle")) return JUNGLE_CATCHES;
        if (name.contains("taiga") || name.contains("ice") || name.contains("frozen")) return COLD_CATCHES;
        if (name.contains("desert")) return DESERT_CATCHES;
        return RIVER_CATCHES;
    }

    public boolean has(String key) { return FishingRodUpgradeRecipe.hasUpgrade(hook.angler.getCurrentEquippedItem(), key); }
    private boolean raining() { return !lava && hook.worldObj.isPrecipitatingAtPos(x(), z()); }
    private int x() { return MathHelper.floor_double(hook.posX); }
    private int z() { return MathHelper.floor_double(hook.posZ); }

    public static float rodMultiplier(ItemStack stack) {
        if (stack == null) return 1;
        Item item = stack.getItem();
        if (item == NMItems.tungstenFishingRod || item == NMItems.tungstenFishingRodBaited) return 0.40F;
        if (item == NMItems.nickelFishingPole || item == NMItems.nickelFishingPoleBaited) return 0.80F;
        if (item == NMItems.ironFishingPole || item == NMItems.ironFishingPoleBaited) return 0.65F;
        if (item == NMItems.diamondFishingPole || item == NMItems.diamondFishingPoleBaited) return 0.50F;
        if (item == NMItems.steelFishingPole || item == NMItems.steelFishingPoleBaited) return 0.40F;
        return 1;
    }

    public static FishingGame.Spec spec(int id) {
        FishingGame.Spec creature = FishingCreatures.spec(id);
        if (creature != null) return creature;
        if (id <= 0 || id >= Item.itemsList.length || Item.itemsList[id] == null) return null;
        return spec(Item.itemsList[id]);
    }

    public static FishingGame.Spec spec(Item item) {
        FishingGame.Spec creature = FishingCreatures.spec(item.itemID);
        if (creature != null) return creature;
        FishingCatch[][] tables = {OCEAN_CATCHES, RIVER_CATCHES, SWAMP_CATCHES, JUNGLE_CATCHES, COLD_CATCHES, DESERT_CATCHES};
        // per-species body size, swimming speed, and tail rhythm follow the source fish silhouettes.
        float[][] sizes = {{15,18,19,23,26}, {15,19,18,21,24}, {15,18,23,22,26},
                {15,18,12,23,28}, {15,19,18,16,25}, {15,12,20,19,24}};
        float[][] speeds = {{0.65F,0.95F,0.7F,1.1F,1.3F}, {0.65F,0.8F,1,0.6F,0.85F},
                {0.65F,0.6F,0.5F,0.9F,0.75F}, {0.65F,1.1F,1.3F,0.6F,0.7F},
                {0.65F,0.95F,0.75F,0.85F,1.1F}, {0.65F,1.2F,0.65F,0.8F,0.9F}};
        for (int b = 0; b < tables.length; b++) for (int f = 0; f < tables[b].length; f++) {
            FishingCatch entry = tables[b][f];
            if (entry.item == item) {
                float speed = item == NMItems.swordfish ? 0.82F : item == NMItems.goldenCarp ? 0.62F : speeds[b][f];
                FishingGame.Spec spec = new FishingGame.Spec(item.itemID, true, entry.rare,
                        sizes[b][f], speed, 0.20F + (b * 5 + f) * 0.009F)
                        .depth(entry.rare ? new float[]{60,20,30,50,45,20}[b] : 0).pull(entry.rare ? 0.8F + b * 0.12F : 0).flippedFacing();
                if (item == NMItems.swordfish || item == NMItems.goldenCarp) spec.flee(1.25F);
                return spec;
            }
        }
        if (item == NMItems.lavafish) return new FishingGame.Spec(item.itemID, true, false, 22, 0.8F, 0.35F).reversed();
        return new FishingGame.Spec(item.itemID, false, false, 14, 0, 0);
    }

    private static FishingCatch catchOf(Item item, int weight, boolean rare) { return new FishingCatch(item, weight, rare); }
}
