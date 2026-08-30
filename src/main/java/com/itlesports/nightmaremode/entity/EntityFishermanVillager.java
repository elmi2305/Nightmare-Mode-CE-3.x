package com.itlesports.nightmaremode.entity;

import net.minecraft.src.EntityVillager;
import net.minecraft.src.World;

/** A deliberately ordinary villager; his value is in the fishing trade tree. */
public class EntityFishermanVillager extends EntityVillager {
    public static final int PROFESSION_ID = 9;

    public EntityFishermanVillager(World world) {
        super(world, PROFESSION_ID);
        this.setPersistent(true);
    }

    @Override
    public int getProfessionFromClass() {
        return PROFESSION_ID;
    }
}
