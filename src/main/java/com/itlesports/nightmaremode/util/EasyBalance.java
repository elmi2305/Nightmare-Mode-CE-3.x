package com.itlesports.nightmaremode.util;

import com.itlesports.nightmaremode.world.BalanceProfile;

public final class EasyBalance {
    private static final java.util.Set<String> OPTIONAL_STAT_COUNTERS = java.util.Set.of(
            "leather_breeding",
            "bookshelf_xp",
            "turntable_rotations_128",
            "mine_coal_ore_256",
            "mine_iron_ore_1000",
            "catch_rare_items_16",
            "craft_books_256",
            "brew_potions_256",
            "activity_break_leaf_25000",
            "activity_mine_block_250000",
            "activity_jump_100000",
            "activity_kill_mob_64",
            "activity_kill_mob_25000",
            "activity_kill_zombie_5000",
            "activity_kill_skeleton_5000",
            "activity_kill_spider_5000",
            "activity_kill_witch_250",
            "activity_kill_slime_1024",
            "activity_kill_enderman_250");
    public static String skillRequirement(net.minecraft.src.ResourceLocation id, String text) {
        return BalanceProfile.isEasy() && OPTIONAL_STAT_COUNTERS.contains(id.getResourcePath().replace("skill/", ""))
                ? text + " (2x Easy credit)" : text;
    }
    private EasyBalance() {}
    public static int statCount(int recorded) { return BalanceProfile.isEasy() ? (int)Math.min(Integer.MAX_VALUE, (long)recorded * 2L) : recorded; }
    public static int resourceCount(int hardCount) { return BalanceProfile.isEasy() ? hardCount * 3 : hardCount; }
    public static float miningBonus(float skillBonus) { return BalanceProfile.isEasy() ? (1.0F + skillBonus) * 2.0F - 1.0F : skillBonus; }
    public static float processingBonus(float skillBonus) { return BalanceProfile.isEasy() ? (1.0F + skillBonus) * 3.0F - 1.0F : skillBonus; }
    public static int processingTicks(int hardTicks) { return BalanceProfile.isEasy() ? Math.max(1, (hardTicks + 2) / 3) : hardTicks; }
    public static float exhaustion(float hardCost) { return BalanceProfile.isEasy() ? hardCost * 0.35F : hardCost; }
    public static float spoilage(float hardRate) { return BalanceProfile.isEasy() ? hardRate * 0.5F : hardRate; }
    public static float pollution(float hardAmount) { return BalanceProfile.isEasy() ? hardAmount * 0.25F : hardAmount; }
    public static float deathLoss(float skillLoss) { return Math.max(0.0F, Math.min(1.0F, skillLoss - (BalanceProfile.isEasy() ? 0.5F : 0.0F))); }
    public static int hostileMultiplier(int knifeTier) { return BalanceProfile.isEasy() ? 2 + Math.max(0, knifeTier) : 1; }
}
