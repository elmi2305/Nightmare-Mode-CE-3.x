package com.itlesports.nightmaremode.entity.variants;

import net.minecraft.src.World;

public class EntityDeadzonePigman extends EntityNetherPigZombieVariant {
    public EntityDeadzonePigman(World world) {
        super(world);
    }

    @Override protected int getMinimumNetherTier() { return 3; }
    @Override protected int getFireSecondsOnHit() { return 6; }
    @Override protected double getVariantHealth() { return 45.0D; }
    @Override protected double getVariantDamage() { return 10.0D; }
}
