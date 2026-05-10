package com.itlesports.nightmaremode.entity;

import net.minecraft.src.EntityArrow;
import net.minecraft.src.World;

public class EntityBurningArrow extends EntityArrow {
    public EntityBurningArrow(World par1World, EntityArrow arrow) {
        super(par1World);
        this.setFire(400);
        this.motionX = arrow.motionX;
        this.motionY = arrow.motionY;
        this.motionZ = arrow.motionZ;
        this.copyLocationAndAnglesFrom(arrow);
    }
}
