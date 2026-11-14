package com.itlesports.nightmaremode.fishing;

import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.Item;
import java.util.*;

public final class FishingCreatures {
    public static final int BLUE_SQUID = -1, GIANT_SQUID = -2;
    public static final int FISH = 0, JELLYFISH = 1, SQUID = 2, KRAKEN = 3;
    private FishingCreatures() {}

    public static List<Entry> entries() {
        return Catalog.ENTRIES;
    }

    private static final class Catalog {
        private static final List<Entry> ENTRIES = createEntries();
    }

    private static List<Entry> createEntries() {
        return List.of(
                entry(NMItems.jellyfish, 18, 0.55F, 12, 0.4F, JELLYFISH, "ocean", "beach"),
                entry(NMItems.babySquid, 18, 0.75F, 20, 0.45F, SQUID, "ocean", "river", "beach"),
                entry(NMItems.blobfish, 25, 0.65F, 100, 0.65F, FISH, "ocean", "cold"),
                entry(NMItems.anglerfish, 24, 0.9F, 125, 1.4F, FISH, "ocean"),
                entry(NMItems.oarfish, 40, 1.05F, 150, 1.8F, FISH, "ocean"),
                entry(NMItems.electricEel, 26, 1.25F, 25, 1.3F, FISH, "swamp", "jungle"),
                entry(NMItems.pufferfish, 20, 0.65F, 12, 0.65F, FISH, "ocean", "beach", "desert"),
                entry(NMItems.sunfish, 34, 0.7F, 70, 1.35F, FISH, "ocean"),
                entry(NMItems.stonefish, 23, 0.45F, 32, 0.85F, FISH, "ocean", "jungle"),
                entry(NMItems.lionfish, 25, 1.05F, 45, 1.1F, FISH, "ocean", "jungle"),
                new Entry(new FishingGame.Spec(BLUE_SQUID, true, true, 29, 0.95F, 0.4F).depth(45).pull(1.15F).animate(SQUID), "ocean"),
                new Entry(new FishingGame.Spec(GIANT_SQUID, true, true, 142, 0.65F, 0.5F).depth(175).pull(2.6F).animate(KRAKEN).kraken(), "ocean"));
    }

    private static Entry entry(Item item, float radius, float speed, float depth, float pull, int animation, String... biomes) {
        return new Entry(new FishingGame.Spec(item.itemID, true, true, radius, speed, 0.35F).depth(depth).pull(pull).animate(animation), biomes);
    }

    public static FishingGame.Spec spec(int id) {
        for (Entry entry : entries()) if (entry.spec.itemId == id) return entry.spec;
        if (id == NMItems.pearl.itemID) return new FishingGame.Spec(id, false, true, 12, 0, 0);
        return null;
    }

    public static final class Entry {
        public final FishingGame.Spec spec;
        private final Set<String> biomes;
        private Entry(FishingGame.Spec spec, String... biomes) { this.spec = spec; this.biomes = Set.of(biomes); }
        public boolean matches(String biome) { return biomes.contains(biome); }
    }
}
