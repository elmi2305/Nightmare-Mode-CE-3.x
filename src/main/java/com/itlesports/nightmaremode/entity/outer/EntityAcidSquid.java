package com.itlesports.nightmaremode.entity.outer;

import btw.entity.mob.BTWSquidEntity;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.SharedMonsterAttributes;
import net.minecraft.src.World;

public class EntityAcidSquid extends BTWSquidEntity {
    private int acidAttackCooldown;

    public EntityAcidSquid(World world) {
        super(world);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(26.0D);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (this.worldObj.isRemote || --this.acidAttackCooldown > 0) return;
        EntityPlayer target = this.worldObj.getClosestVulnerablePlayerToEntity(this, 16.0D);
        if (target != null && this.canEntityBeSeen(target)) {
            this.worldObj.spawnEntityInWorld(new EntityAcidProjectile(this.worldObj, this, target));
            this.playSound("random.bow", 0.8F, 0.75F);
            this.acidAttackCooldown = 55 + this.rand.nextInt(30);
        }
    }
}
