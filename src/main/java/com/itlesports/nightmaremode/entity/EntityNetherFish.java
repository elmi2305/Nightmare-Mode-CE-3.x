package com.itlesports.nightmaremode.entity;

import net.minecraft.src.Entity;
import net.minecraft.src.EntitySilverfish;
import net.minecraft.src.World;

public class EntityNetherFish extends EntitySilverfish {
    public EntityNetherFish(World world) {
        super(world);
        this.isImmuneToFire = true;
    }

    @Override
    public boolean attackEntityAsMob(Entity target) {
        boolean hit = super.attackEntityAsMob(target);
        if (hit) {
            target.setFire(2);
        }
        return hit;
    }
}
