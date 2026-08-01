package com.itlesports.nightmaremode.entity.variants;

import net.minecraft.src.World;

public class EntityCinderPigman extends EntityNetherPigZombieVariant {
    public EntityCinderPigman(World world) {
        super(world);
    }

    @Override protected int getMinimumNetherTier() { return 2; }
    @Override protected int getFireSecondsOnHit() { return 3; }
}
