package com.itlesports.nightmaremode.entity.variants;

import net.minecraft.src.EntityLargeFireball;
import net.minecraft.src.EntityLivingBase;
import net.minecraft.src.World;

public class EntitySiegeFireball extends EntityLargeFireball {
    public EntitySiegeFireball(World world) {
        super(world);
    }

    public EntitySiegeFireball(World world, EntityLivingBase shooter, double dx, double dy, double dz) {
        super(world, shooter, dx, dy, dz);
    }

}
