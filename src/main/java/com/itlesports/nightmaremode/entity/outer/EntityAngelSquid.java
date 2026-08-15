package com.itlesports.nightmaremode.entity.outer;

import btw.entity.mob.BTWSquidEntity;
import net.minecraft.src.SharedMonsterAttributes;
import net.minecraft.src.World;

public class EntityAngelSquid extends BTWSquidEntity {
    public EntityAngelSquid(World world) {
        super(world);
        this.setSize(1.15F, 1.15F);
        this.noClip = true;
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(28.0D);
    }

    @Override
    public boolean getCanSpawnHere() {
        return this.worldObj.difficultySetting > 0 && this.worldObj.checkNoEntityCollision(this.boundingBox);
    }
}
