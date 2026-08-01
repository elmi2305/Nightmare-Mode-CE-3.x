package com.itlesports.nightmaremode.entity.variants;

import com.itlesports.nightmaremode.worldgen.NetherTierHelper;
import net.minecraft.src.EntityBlaze;
import net.minecraft.src.SharedMonsterAttributes;
import net.minecraft.src.World;

public class EntityHellfireBlaze extends EntityBlaze {
    public EntityHellfireBlaze(World world) {
        super(world);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(40.0D);
    }

    @Override
    public boolean getCanSpawnHere() {
        return NetherTierHelper.getTier(this.worldObj, this.posX, this.posZ) >= 3 && super.getCanSpawnHere();
    }
}
