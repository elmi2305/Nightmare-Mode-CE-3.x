package com.itlesports.nightmaremode.entity;

import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.NightmareUtils;
import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.*;

public class EntityBlackWidowSpider extends EntitySpider {

    public EntityBlackWidowSpider(World par1World) {
        super(par1World);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.45f);
    }

    @Override
    protected void dropFewItems(boolean bKilledByPlayer, int iLootingModifier) {
        if (bKilledByPlayer) {
            int dropCount = this.rand.nextInt(2 + iLootingModifier); // 0 - 1

            for (int i = 0; i < dropCount; ++i) {
                if (this.rand.nextInt(16) == 0 && NightmareUtils.getWorldProgress(this.worldObj) > 0) {
                    this.dropItem(NMItems.spiderFangs.itemID, 1);
                }
                if(this.rand.nextInt(4) == 0){
                    this.dropItem(Item.silk.itemID, 1);
                }
                if(this.rand.nextInt(4) == 0){
                    this.dropItem(Item.fermentedSpiderEye.itemID, 1);
                }
            }

        }
        super.dropFewItems(bKilledByPlayer, iLootingModifier);
    }

    @Override
    public boolean attackEntityAsMob(Entity par1Entity) {
        if(par1Entity instanceof EntityLivingBase){
            ((EntityLivingBase) par1Entity).addPotionEffect(new PotionEffect(Potion.poison.id, 70 + NightmareUtils.getWorldProgress(this.worldObj) * 30, 1));
        }
        return super.attackEntityAsMob(par1Entity);
    }

    @Override
    public boolean attackEntityFrom(DamageSource par1DamageSource, float par2) {
        if(par1DamageSource.getEntity() instanceof EntityLivingBase){
            ((EntityLivingBase) par1DamageSource.getEntity()).addPotionEffect(new PotionEffect(Potion.digSlowdown.id, 100,1));
        }
        return super.attackEntityFrom(par1DamageSource, par2);
    }

    @Override
    public boolean getCanSpawnHere() {
        return (NightmareMode.moreVariants || NightmareMode.isAprilFools) && super.getCanSpawnHere();
    }
}
