package com.itlesports.nightmaremode.entity.outer;

import com.itlesports.nightmaremode.util.NMFields;
import net.minecraft.src.*;

public class EntityFireSkeletonOuter extends EntitySkeleton {
    public EntityFireSkeletonOuter(World world) {
        super(world);
        this.setSkeletonType(NMFields.SKELETON_FIRE);
        this.isImmuneToFire = true;
    }

    @Override
    public EntityLivingData onSpawnWithEgg(EntityLivingData data) {
        data = super.onSpawnWithEgg(data);
        this.setSkeletonType(NMFields.SKELETON_FIRE);
        this.setCurrentItemOrArmor(0, new ItemStack(Item.bow));
        this.isImmuneToFire = true;
        return data;
    }
}
