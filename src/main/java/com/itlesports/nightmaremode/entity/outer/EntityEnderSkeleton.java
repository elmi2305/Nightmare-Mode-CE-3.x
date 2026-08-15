package com.itlesports.nightmaremode.entity.outer;

import com.itlesports.nightmaremode.util.NMFields;
import net.minecraft.src.EntitySkeleton;
import net.minecraft.src.EntityLivingData;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;

public class EntityEnderSkeleton extends EntitySkeleton {
    public EntityEnderSkeleton(World world) {
        super(world);
        this.setSkeletonType(NMFields.SKELETON_ENDER);
    }

    @Override
    public EntityLivingData onSpawnWithEgg(EntityLivingData data) {
        data = super.onSpawnWithEgg(data);
        this.setSkeletonType(NMFields.SKELETON_ENDER);
        this.setCurrentItemOrArmor(0, new ItemStack(Item.bow));
        return data;
    }
}
