package com.itlesports.nightmaremode.entity;

import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.NightmareUtils;
import net.minecraft.src.*;

public class EntityRadioactiveEnderman extends EntityEnderman {
    public EntityRadioactiveEnderman(World par1World) {
        super(par1World);
    }

    @Override
    public boolean getCanSpawnHere() {
        return NightmareMode.moreVariants && super.getCanSpawnHere();
    }

    @Override
    public boolean attackEntityAsMob(Entity par1Entity) {
        if(par1Entity instanceof EntityLivingBase && !((EntityLivingBase) par1Entity).isPotionActive(Potion.poison)){
            ((EntityLivingBase) par1Entity).addPotionEffect(new PotionEffect(Potion.poison.id,80,0));
        }
        return super.attackEntityAsMob(par1Entity);
    }

    @Override
    protected void dropFewItems(boolean bKilledByPlayer, int iLootingModifier) {
        if (bKilledByPlayer && NightmareUtils.getWorldProgress(this.worldObj) > 0) {
            int dropCount = this.rand.nextInt(2 + iLootingModifier); // 0 - 1

            for (int i = 0; i < dropCount; ++i) {
                if (this.rand.nextInt(4 ) == 0) {
                    this.dropItem(Item.fermentedSpiderEye.itemID, 1);
                }
            }
        }
        super.dropFewItems(bKilledByPlayer, iLootingModifier);
    }
}
