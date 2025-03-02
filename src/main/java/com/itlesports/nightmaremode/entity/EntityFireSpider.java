package com.itlesports.nightmaremode.entity;

import btw.community.nightmaremode.NightmareMode;
import net.minecraft.src.*;

public class EntityFireSpider extends EntitySpider {
    public EntityFireSpider(World par1World) {
        super(par1World);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.7f);
    }

    @Override
    protected void dropFewItems(boolean bKilledByPlayer, int iLootingModifier) {
        if(this.rand.nextInt(4) == 0){
            this.dropItem(Item.fireworkCharge.itemID, 1 + iLootingModifier);
        }
        super.dropFewItems(bKilledByPlayer, iLootingModifier);
    }

    @Override
    public boolean attackEntityAsMob(Entity par1Entity) {
        par1Entity.setFire(2 + this.rand.nextInt(3));
        return super.attackEntityAsMob(par1Entity);
    }

    @Override
    public boolean attackEntityFrom(DamageSource par1DamageSource, float par2) {
        if(par1DamageSource.isFireDamage()){
            return false;
        }
        return super.attackEntityFrom(par1DamageSource, par2);
    }

    @Override
    public boolean getCanSpawnHere() {
        return NightmareMode.moreVariants && super.getCanSpawnHere();
    }
}
