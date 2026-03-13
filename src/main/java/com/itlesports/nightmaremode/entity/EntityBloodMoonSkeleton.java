package com.itlesports.nightmaremode.entity;

import com.itlesports.nightmaremode.util.NMFields;
import com.itlesports.nightmaremode.util.NMUtils;
import net.minecraft.src.*;

public class EntityBloodMoonSkeleton extends EntitySkeleton {

    public EntityBloodMoonSkeleton(World par1World) {
        super(par1World);
        // this skeleton is just used for the blood moon of the nether
        if (this.dimension == -1 && NMUtils.getIsBloodMoon()) {
            int skeletonType = this.rand.nextInt(4) == 0 ? NMFields.SKELETON_FIRE : 1;
            this.setSkeletonType(skeletonType);
        } else{
            this.setSkeletonType(0);
        }
    }



    @Override
    public boolean getCanSpawnHere() {
        if(this.dimension == -1 && NMUtils.getIsBloodMoon()){
            return super.getCanSpawnHere();
        }
        return false;
    }

}
