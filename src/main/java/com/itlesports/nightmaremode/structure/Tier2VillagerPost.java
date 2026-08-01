package com.itlesports.nightmaremode.structure;

import java.util.Random;

public class Tier2VillagerPost extends NetherVillagerPost {
    public Tier2VillagerPost() {}

    public Tier2VillagerPost(Random random, int x, int z) {
        super(random, x, z, 29, 14, 29);
    }

    @Override
    protected String getStructurePath() {
        return "structures/tier2villagerpost.nbt";
    }

    @Override
    protected int getVillagerProfession() {
        return 2;
    }

    @Override
    protected double getVillagerHorizontalOffset() {
        return 8.0D;
    }

    @Override
    protected double getVillagerVerticalOffset() {
        return 3.0D;
    }

    @Override
    protected int getTier() {
        return 2;
    }
}
