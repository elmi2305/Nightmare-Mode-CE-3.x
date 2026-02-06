package com.itlesports.nightmaremode.rendering.entities;

import com.itlesports.nightmaremode.entity.EntitySkeletonDrowned;
import com.itlesports.nightmaremode.entity.EntitySkeletonMelted;
import net.minecraft.src.EntitySkeleton;
import net.minecraft.src.RenderSkeleton;
import net.minecraft.src.ResourceLocation;

public class RenderCustomSkeleton extends RenderSkeleton {
    private static final ResourceLocation skeletonDrowned = new ResourceLocation("nightmare:textures/entity/skeletonDrowned.png");
    private static final ResourceLocation skeletonMelted = new ResourceLocation("nightmare:textures/entity/skeletonMelted.png");

    @Override
    protected ResourceLocation func_110860_a(EntitySkeleton entitySkeleton) {
        if(entitySkeleton instanceof EntitySkeletonDrowned){
            return skeletonDrowned;
        } else if(entitySkeleton instanceof EntitySkeletonMelted){
            return skeletonMelted;
        }
        return super.func_110860_a(entitySkeleton);
    }
}
