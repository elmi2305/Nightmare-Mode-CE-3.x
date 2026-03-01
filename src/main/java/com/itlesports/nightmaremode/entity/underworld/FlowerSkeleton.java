package com.itlesports.nightmaremode.entity.underworld;

import net.minecraft.src.*;

public class FlowerSkeleton extends EntitySkeleton implements IFlowerMob{
    public FlowerSkeleton(World par1World) {
        super(par1World);
    }

    @Override
    public void attackEntityWithRangedAttack(EntityLivingBase target, float fDamageModifier) {

        EntitySporeArrow sporeArrow = new EntitySporeArrow(this.worldObj, this, 1.6F);
        sporeArrow.setPosition(this.posX, this.posY + this.getEyeHeight() - 0.1D, this.posZ);
        sporeArrow.setThrowableHeading(target.posX - this.posX, target.posY + target.getEyeHeight() - 1.1D - sporeArrow.posY, target.posZ - this.posZ, 1.6F, 8.0F);
        this.worldObj.spawnEntityInWorld(sporeArrow);


        this.playSound("random.bow", 1.0f, 1.0f / (this.getRNG().nextFloat() * 0.4f + 0.8f));
    }
}
