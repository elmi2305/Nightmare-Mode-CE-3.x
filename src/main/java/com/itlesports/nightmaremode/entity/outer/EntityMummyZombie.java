package com.itlesports.nightmaremode.entity.outer;

import net.minecraft.src.EntityZombie;
import net.minecraft.src.SharedMonsterAttributes;
import net.minecraft.src.World;

public class EntityMummyZombie extends EntityZombie {
    public EntityMummyZombie(World world) {
        super(world);
        this.isImmuneToFire = true;
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(36.0D);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.25D);
    }
}
