package com.itlesports.nightmaremode.entity;

import net.minecraft.src.Entity;
import net.minecraft.src.EntitySilverfish;
import net.minecraft.src.World;
import com.itlesports.nightmaremode.item.NMItems;

public class EntityNetherFish extends EntitySilverfish {
    public EntityNetherFish(World world) {
        super(world);
        this.isImmuneToFire = true;
    }

    @Override
    public boolean attackEntityAsMob(Entity target) {
        boolean hit = super.attackEntityAsMob(target);
        if (hit) {
            target.setFire(2);
        }
        return hit;
    }

    @Override
    protected void dropFewItems(boolean killedByPlayer, int looting) {
        int rolls = 1 + this.rand.nextInt(2 + Math.max(0, looting));
        for (int i = 0; i < rolls; ++i) {
            if (this.rand.nextFloat() < 0.5F) {
                this.dropItem(NMItems.searingSilverScale.itemID, 1);
            }
        }
    }
}
