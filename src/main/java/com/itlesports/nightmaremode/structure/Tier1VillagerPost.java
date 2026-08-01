package com.itlesports.nightmaremode.structure;

import java.util.Random;

public class Tier1VillagerPost extends NetherVillagerPost {
    public Tier1VillagerPost() {}

    public Tier1VillagerPost(Random random, int x, int z) {
        super(random, x, z, 17, 6, 17);
    }

    @Override
    protected String getStructurePath() {
        return "structures/tier1villagerpost.nbt";
    }

    @Override
    protected int getVillagerProfession() {
        return 1;
    }

    @Override
    protected double getVillagerHorizontalOffset() {
        return 5.5D;
    }

    @Override
    protected double getVillagerVerticalOffset() {
        return 3.0D;
    }

    @Override
    protected int getTier() {
        return 1;
    }
}
