package com.itlesports.nightmaremode.entity;

import net.minecraft.src.World;

public class EntityTier2NetherVillager extends EntityNetherPostVillager {
    public static final int PROFESSION_ID = 7;

    public EntityTier2NetherVillager(World world) {
        super(world, PROFESSION_ID);
    }

    @Override
    public int getProfessionFromClass() {
        return PROFESSION_ID;
    }

    @Override
    public int getPostTier() {
        return 2;
    }
}
