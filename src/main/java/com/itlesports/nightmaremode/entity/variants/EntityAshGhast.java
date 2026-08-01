package com.itlesports.nightmaremode.entity.variants;

import com.itlesports.nightmaremode.worldgen.NetherTierHelper;
import net.minecraft.src.EntityGhast;
import net.minecraft.src.SharedMonsterAttributes;
import net.minecraft.src.World;

public class EntityAshGhast extends EntityGhast {
    public EntityAshGhast(World world) {
        super(world);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(18.0D);
    }

    @Override
    public boolean getCanSpawnHere() {
        return NetherTierHelper.getTier(this.worldObj, this.posX, this.posZ) >= 2 && super.getCanSpawnHere();
    }
}
