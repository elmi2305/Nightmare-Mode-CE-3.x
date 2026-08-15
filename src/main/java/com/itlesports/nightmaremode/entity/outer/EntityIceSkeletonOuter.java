package com.itlesports.nightmaremode.entity.outer;

import com.itlesports.nightmaremode.util.NMFields;
import net.minecraft.src.*;

public class EntityIceSkeletonOuter extends EntitySkeleton {
    public EntityIceSkeletonOuter(World world) {
        super(world);
        this.setSkeletonType(NMFields.SKELETON_ICE);
    }

    @Override
    public EntityLivingData onSpawnWithEgg(EntityLivingData data) {
        data = super.onSpawnWithEgg(data);
        this.setSkeletonType(NMFields.SKELETON_ICE);
        this.setCurrentItemOrArmor(0, new ItemStack(Item.bow));
        return data;
    }
}
