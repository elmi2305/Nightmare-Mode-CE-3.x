package com.itlesports.nightmaremode.entity.outer;

import btw.entity.mob.BTWSquidEntity;
import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.SharedMonsterAttributes;
import net.minecraft.src.World;

public class EntityAngelSquid extends BTWSquidEntity {
    private static final double VISUAL_BOUND_PADDING = 3.5D;

    public EntityAngelSquid(World world) {
        super(world);
        this.setSize(1.15F, 1.15F);
        this.noClip = true;
    }

    @Override
    public AxisAlignedBB getVisualBoundingBox() {
        return this.boundingBox.expand(VISUAL_BOUND_PADDING, VISUAL_BOUND_PADDING, VISUAL_BOUND_PADDING);
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
