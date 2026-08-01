package com.itlesports.nightmaremode.entity;

import net.minecraft.src.World;

public class EntityTier3NetherVillager extends EntityNetherPostVillager {
    public static final int PROFESSION_ID = 8;

    public EntityTier3NetherVillager(World world) {
        super(world, PROFESSION_ID);
    }

    @Override
    public int getProfessionFromClass() {
        return PROFESSION_ID;
    }

    @Override
    public int getPostTier() {
        return 3;
    }
}
