package com.itlesports.nightmaremode.structure;

import java.util.Random;

public class Tier3VillagerPost extends NetherVillagerPost {
    public Tier3VillagerPost() {}

    public Tier3VillagerPost(Random random, int x, int z) {
        super(random, x, z, 49, 24, 49);
    }

    @Override
    protected String getStructurePath() {
        return "structures/tier3villagerpost.nbt";
    }

    @Override
    protected int getVillagerProfession() {
        return 3;
    }

    @Override
    protected double getVillagerHorizontalOffset() {
        return 8.0D;
    }

    @Override
    protected double getVillagerVerticalOffset() {
        return 8.0D;
    }

    @Override
    protected int getTier() {
        return 3;
    }
}
