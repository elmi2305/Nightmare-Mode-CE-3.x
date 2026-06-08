package com.itlesports.nightmaremode.entity.variants;

import com.itlesports.nightmaremode.util.NMEvents;
import net.minecraft.src.*;

import java.util.Iterator;
import java.util.List;

public class EntityRainSpider extends EntitySpider {
    // used exclusively so it can spawn a ton during the spider rain event
    public EntityRainSpider(World w) {
        super(w);
    }

    @Override
    public boolean getCanSpawnHere() {
        return super.getCanSpawnHere() && NMEvents.SimpleEvent.SPIDER_RAIN.isActive();
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setAttribute(32);
    }

    @Override
    protected Entity findPlayerToAttack() {
        EntityLivingBase targetEntity = null;
        if (!(this.doesLightAffectAggessiveness() && !(this.getBrightness(1.0f) < 0.5f) || this.isAlwaysNeutral())) {
            targetEntity = this.worldObj.getClosestVulnerablePlayerToEntity(this, 32);
        }
        if (targetEntity == null) {
            List chickenList = this.worldObj.getEntitiesWithinAABB(EntityChicken.class, this.boundingBox.expand(16.0, 4.0, 16.0));
            Iterator chickenIterator = chickenList.iterator();
            double dClosestChickenDistSq = 257.0;
            while (chickenIterator.hasNext()) {
                double dDeltaZ;
                double dDeltaY;
                double dDeltaX;
                double dDistSq;
                EntityChicken chicken = (EntityChicken)chickenIterator.next();
                if (chicken.isLivingDead || !((dDistSq = (dDeltaX = this.posX - chicken.posX) * dDeltaX + (dDeltaY = this.posY - chicken.posY) * dDeltaY + (dDeltaZ = this.posZ - chicken.posZ) * dDeltaZ) < dClosestChickenDistSq)) continue;
                targetEntity = chicken;
                dClosestChickenDistSq = dDistSq;
            }
        }
        return targetEntity;
    }

    @Override
    protected boolean isValidLightLevel() {
        return true;
    }

    @Override
    protected boolean canDespawn() {
        return super.canDespawn() && !NMEvents.SimpleEvent.SPIDER_RAIN.isActive() && this.posY > 60;
    }
}
