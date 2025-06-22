package com.itlesports.nightmaremode.entity;

import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.NightmareUtils;
import net.minecraft.src.*;

public class EntityCustomSkeleton extends EntitySkeleton {

    public EntityCustomSkeleton(World par1World) {
        super(par1World);
        if (this.dimension == -1 && NightmareUtils.getIsBloodMoon()) {
            int skeletonType = this.rand.nextInt(4) == 0 ? NightmareMode.SKELETON_FIRE : 1;
            this.setSkeletonType(skeletonType);
        } else{
            this.setSkeletonType(0);
        }
    }



    @Override
    public boolean getCanSpawnHere() {
        if(this.dimension == -1 && NightmareUtils.getIsBloodMoon()){
            return super.getCanSpawnHere();
        }
        return false;
    }

}
