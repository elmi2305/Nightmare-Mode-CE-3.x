package com.itlesports.nightmaremode.entity;

import net.minecraft.src.World;

public class EntityTier1NetherVillager extends EntityNetherPostVillager {
    public static final int PROFESSION_ID = 6;

    public EntityTier1NetherVillager(World world) {
        super(world, PROFESSION_ID);
    }

    @Override
    public int getProfessionFromClass() {
        return PROFESSION_ID;
    }

    @Override
    public int getPostTier() {
        return 1;
    }
}
