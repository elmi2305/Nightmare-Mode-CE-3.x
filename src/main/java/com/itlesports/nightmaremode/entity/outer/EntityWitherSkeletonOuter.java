package com.itlesports.nightmaremode.entity.outer;

import net.minecraft.src.*;

public class EntityWitherSkeletonOuter extends EntitySkeleton {
    public EntityWitherSkeletonOuter(World world) {
        super(world);
        this.setSkeletonType(1);
    }

    @Override
    public EntityLivingData onSpawnWithEgg(EntityLivingData data) {
        data = super.onSpawnWithEgg(data);
        this.setSkeletonType(1);
        this.setCurrentItemOrArmor(0, new ItemStack(Item.swordStone));
        return data;
    }
}
