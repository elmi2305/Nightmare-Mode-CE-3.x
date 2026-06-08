package com.itlesports.nightmaremode.entity.variants;

import com.itlesports.nightmaremode.util.NMEvents;
import net.minecraft.src.EntitySpider;
import net.minecraft.src.SharedMonsterAttributes;
import net.minecraft.src.World;

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
    protected boolean canDespawn() {
        return super.canDespawn() && !NMEvents.SimpleEvent.SPIDER_RAIN.isActive() && this.posY > 60;
    }
}
