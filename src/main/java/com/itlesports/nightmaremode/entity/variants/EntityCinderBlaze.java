package com.itlesports.nightmaremode.entity.variants;

import com.itlesports.nightmaremode.worldgen.NetherTierHelper;
import net.minecraft.src.EntityBlaze;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Potion;
import net.minecraft.src.PotionEffect;
import net.minecraft.src.SharedMonsterAttributes;
import net.minecraft.src.World;

public class EntityCinderBlaze extends EntityBlaze {
    public EntityCinderBlaze(World world) {
        super(world);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(45.0D);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute(10.0D);
    }

    @Override
    public boolean getCanSpawnHere() {
        return NetherTierHelper.getTier(this.worldObj, this.posX, this.posZ) >= 2 && super.getCanSpawnHere();
    }

    @Override
    public boolean attackEntityAsMob(Entity target) {
        boolean hit = super.attackEntityAsMob(target);
        if (hit && target instanceof EntityPlayer && !this.worldObj.isRemote) {
            this.worldObj.newExplosion(this, this.posX, this.posY + this.height * 0.5D, this.posZ,
                    2F, true, this.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing"));
            this.setDead();
        }
        return hit;
    }

    @Override
    public void addPotionEffect(PotionEffect effect) {
        if (effect.getPotionID() != Potion.field_76443_y.id) {
            super.addPotionEffect(effect);
        }
    }
}
